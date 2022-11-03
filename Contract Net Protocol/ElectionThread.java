import java.rmi.RemoteException;
import java.util.ArrayList;

import lights.Field;
import lights.Tuple;
import lights.interfaces.ITuple;
import lights.interfaces.TupleSpaceException;

public class ElectionThread extends Thread{
	public IRemoteTupleSpace space;
	public int processID;
	public int leaderID;
	int notack = 0;
	public ArrayList<Thread> threadQueue;
	
	public boolean amILeader() {
		return (this.processID == this.leaderID);
	}
	
	public int getLeaderID() {
		return this.leaderID;
	}
	
	public ElectionThread(IRemoteTupleSpace space, int processID, ArrayList<Thread> threadQueue) {
		this.space = space;
		this.processID = processID;
		this.threadQueue = threadQueue;		
		this.candidate();
	}
	
	
	public void delete_leader_candidature() {
		
		ITuple template_leader_tuple = new Tuple().add(new Field().setType(Integer.class)).add(new Field().setValue("Leader"));
				
		try {
			if(this.get_leader() != -1) {
				this.space.inp(template_leader_tuple);
			}
		} catch (NumberFormatException | RemoteException | TupleSpaceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void candidate() {
		
		
		ITuple template_leader_tuple = new Tuple().add(new Field().setType(Integer.class)).add(new Field().setValue("Leader"));
		ITuple template = new Tuple().add(new Field().setValue(Integer.valueOf(this.processID))).add(new Field().setValue("Leader"));
		int pid_leader;
		try {
			if((pid_leader = this.get_leader()) != -1) {				
				if (this.processID > pid_leader) {					
					this.space.inp(template_leader_tuple);
					this.space.out(template);
					this.leaderID = processID;
				}
				else {
					this.delete_active_thread();
					this.remove_active_tuple(this.processID);
					this.leaderID = pid_leader;
				}
			}else {
				this.space.out(template);
				this.leaderID = this.processID;
			}
		} catch (NumberFormatException | RemoteException | TupleSpaceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Leader for " + this.processID + " is " + this.leaderID);
	}
	
	public void delete_active_thread() {		
		for (Thread t : threadQueue) {
			if (t.isAlive()) {
				System.out.println("Deleting still active thread of : " + this.processID);
				t.interrupt();
			}
		}
	}
	
	public void remove_active_tuple(int pid) {

		TupleAdapter adapter_proposal = new TupleAdapter(new Tuple())
				.adaptValue(Integer.valueOf(pid))
				.adaptValue("Contract Proposal")
				.adaptType(Integer.class)
				.adaptType(String.class)
				.adaptType(Integer.class)				
				.adaptType(Integer.class)	
				.adaptType(Integer.class);
		
		ITuple contract_proposal_template = adapter_proposal.get();
		
		TupleAdapter adapter_proposal_process = new TupleAdapter(new Tuple())
				.adaptType(Integer.class)
				.adaptValue("Contract Proposal")
				.adaptValue(Integer.valueOf(pid))
				.adaptType(Integer.class)
				.adaptType(String.class)
				.adaptType(Integer.class)				
				.adaptType(Integer.class)	
				.adaptType(Integer.class);
		
		ITuple contract_proposal_process_template = adapter_proposal.get();
		
		
		TupleAdapter adapter_accept = new TupleAdapter(new Tuple())
				.adaptValue(Integer.valueOf(pid))
				.adaptValue("Contract Leader Accept")
				.adaptType(Integer.class)
				.adaptType(String.class)
				.adaptType(Integer.class)				
				.adaptType(Integer.class)	
				.adaptType(Integer.class);		
		
		ITuple contract_accept_template = adapter_accept.get();
		
		TupleAdapter remove_process_closure_adapter = new TupleAdapter(new Tuple())
				.adaptType(Integer.class)
				.adaptValue("Contract Closure")
				.adaptValue(Integer.valueOf(pid))
				.adaptType(String.class)
				.adaptType(Integer.class)				
				.adaptType(Integer.class)	
				.adaptType(Integer.class);
		
		ITuple remove_process_closure = remove_process_closure_adapter.get();		
		
			try {
				while(this.space.count(contract_proposal_template) > 0) {
					System.out.println("Removing proposal tuple of : " + pid);
					this.space.in(contract_proposal_template);
				}
				while(this.space.count(contract_proposal_process_template) > 0) {
					System.out.println("Removing proposal response for : " + pid);
					this.space.in(contract_proposal_process_template);
				}
				while(this.space.count(contract_accept_template) > 0) {
					System.out.println("Removing accpetance tuple of : " + pid);
					this.space.in(contract_accept_template);
				}
				while(this.space.count(remove_process_closure) > 0) {
					System.out.println("Removing closure tuple for : " + pid);
					this.space.in(remove_process_closure);
				}
			} catch (RemoteException | TupleSpaceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	
	public void rielection() {
		System.out.println("Process " + this.processID + " called a rielection");
		this.delete_leader_candidature();
		this.remove_active_tuple(this.leaderID);
		this.candidate();
		this.run();
	}
	
	public void simply_act() {
		ITuple t = new Tuple().add(new Field().setValue(Integer.valueOf(this.processID))).add(new Field().setValue("wake-up"))
				.add(new Field().setValue(Integer.valueOf(this.leaderID)));
		try {
			this.space.out(t);
			//System.out.println("Send ack to the leader...");
		} catch (RemoteException | TupleSpaceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int waitsec = 5;
		try {
			Thread.sleep(waitsec*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ITuple template = new Tuple().add(new Field().setValue(Integer.valueOf(this.leaderID))).add(new Field().setValue("wake-up ack"))
				.add(new Field().setValue(Integer.valueOf(this.processID)));	
		try {
			if (this.space.inp(template) == null) {
				System.out.println("Process " + this.processID + " experienced not acknowledgment from head process " + this.leaderID);
				if (notack >= 2) {
					notack = 0;
					this.rielection();
				}else
					notack++;
								
			}
			else {
				//System.out.println("ack received");
				notack = 0;
			}
		} catch (RemoteException | TupleSpaceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.run();		
	}
	
	public void act_as_leader() {
		int waitsec = 2;
		try {
			Thread.sleep(waitsec*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		LeaderAckThread ackThread = new LeaderAckThread(this.space, this.processID, this.leaderID);
		ackThread.start();		
		
		this.run();
	}
	
	
	public int get_leader() {
		ITuple template = new Tuple().add(new Field().setType(Integer.class)).add(new Field().setValue("Leader"));
		int pid_leader = -1;
		try {
			if(this.space.count(template) > 0) {
				ITuple res = this.space.rdp(template);
				pid_leader = Integer.valueOf(res.get(0).toString());			
			}
		} catch (NumberFormatException | RemoteException | TupleSpaceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return pid_leader;
	}
	
	public void run() {
		int pid_leader;
		if((pid_leader = get_leader()) != -1) {
			if(pid_leader != this.leaderID) {
				this.candidate();
			}
		}
		if (this.processID == this.leaderID) {
			this.act_as_leader();
		}
		else {
			this.simply_act();
		}	
	}
}
