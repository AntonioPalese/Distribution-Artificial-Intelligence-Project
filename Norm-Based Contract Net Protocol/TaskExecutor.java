import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import lights.interfaces.TupleSpaceException;

public class TaskExecutor extends Contractor {
	
	public long thresholdTime;

	public TaskExecutor(int processID, int leaderID, IRemoteTupleSpace space) {
		super();
		this.processID = processID;
		this.leaderID = leaderID;		
		this.space = space;
		this.thresholdTime = 10*1000;
	}
	
	@Override
	public boolean act_as_contractor(Task task) {
		// TODO Auto-generated method stub
		
		try {
			Task task_assigned = this.propose();
			if(task_assigned == null) {
				return false;
			}			
			Message acceptance_msg;		
			if((acceptance_msg = this.wait_for_leader_acceptance(task_assigned)) != null) {
				System.out.println("Process Leader " + this.leaderID + " accepted task " + acceptance_msg.task);
				Task task_executed = this.execute(acceptance_msg);
				this.close(task_executed);				
			}else {
				System.out.println("Process Leader " + this.leaderID + " refused task " + task_assigned);
				return false;
			}		
			
		} catch (RemoteException | TupleSpaceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}

	private Task propose() throws RemoteException, TupleSpaceException {		
		
		Message leader_request = new Message(this.leaderID, "Contract Announcement", -1, new Task(-1, "", "", -1, -1, ""));
		Message received = new Message();	
		
		long start = System.currentTimeMillis();
		while(this.space.count(leader_request.toTuple()) == 0) {
			if ((System.currentTimeMillis() - start) > this.thresholdTime) {
				// rifiutato dal leader
				System.out.println("Too much time to get answer from leader...");
				return null;
			}
		}		
		
		
		received.setFromTuple(this.space.in(leader_request.toTuple()));
		Task task = received.task;
		
		// rigid norms		
		// rifiutato dal processo 
		SimpleDateFormat formatter1=new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy", Locale.ENGLISH);		
		try {
//			System.out.println(formatter1.parse(task.expirationTime));
//			System.out.println(new Date(System.currentTimeMillis()));
			if(!formatter1.parse(task.expirationTime).after(new Date(System.currentTimeMillis()))){
				System.out.println("Task "+ task.taskID + " has Expired");
				return null;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		// rifiutato dal processo
		int reward = task.reward;		
		if(reward < (int)(this.processID / 1000)) {
			System.out.println("Process refused for reward " + task);
			this.space.out(received.toTuple());
			return null;
		}
		
		task.earning = new Random().nextInt(((10 + (this.leaderID / 1000))  - 1) + 1) + 1;	
		task.priority = "medium";
		
		Message response = new Message(this.processID, "Contract Announcement Response", this.leaderID, task);
		this.space.out(response.toTuple());
		System.out.println("Process " + this.processID + " proposed task " + task);
		
		return task;
	}	
	
	
	
	private Message wait_for_leader_acceptance(Task task_assigned) throws RemoteException, TupleSpaceException {
		
		Message acceptance = new Message(this.leaderID, "Leader Accept", this.processID, task_assigned);
		
		long start = System.currentTimeMillis();
		while(this.space.count(acceptance.toTuple()) == 0) {
			if ((System.currentTimeMillis() - start) > this.thresholdTime) {
				// rifiutato dal leader
				return null;
			}
		}	
		
		acceptance.setFromTuple(this.space.in(acceptance.toTuple())); 		
		
		return acceptance;
	}
	
	
	private Task execute(Message acceptance_msg) {
		// TODO Auto-generated method stub
		
		Task toExecute = acceptance_msg.task;
		
		// simulate execution
		int waitsec = 5;
		try {
			Thread.sleep(waitsec*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return toExecute;
	}
	
	private void close(Task task_executed) throws RemoteException, TupleSpaceException {
		// TODO Auto-generated method stub
		
		Message closeMessage = new Message(this.processID, "Contract Closure", this.leaderID, task_executed);
		this.space.out(closeMessage.toTuple());
		System.out.println("Task " + closeMessage.task  + " Closed");
		
	}
}
