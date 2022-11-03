import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import lights.*;
import lights.interfaces.*;

public class TaskManager extends Contractor {
	
	public HashMap<Integer, Integer> ratings;
	public int threshold;
	
	
	public TaskManager(int processID, int leaderID, IRemoteTupleSpace space, HashMap<Integer, Integer> ratings) {
		super();
		this.processID = processID;
		this.leaderID = leaderID;
		this.space = space;
		this.threshold = this.processID / 1000;
		this.ratings = ratings;
	}
	
	
	@Override
	public boolean act_as_contractor(Task t) {
		// TODO Auto-generated method stub
		
		try {
			Task task = this.task_announcement(t);
			Message received;
			if((received = this.wait_for_acceptance(task)) != null) {
				if(this.check(received)) {
					this.accept(received);
					if(this.endOfContract(received)) {
						return true;
					}else
						return false;
				}				
			}
			
		} catch (RemoteException | TupleSpaceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		return false;
		
	}


	private Task task_announcement(Task t) throws RemoteException, TupleSpaceException {
		
		Message msg = new Message(this.processID, "Contract Announcement", 0, t);
		if (this.space.count(msg.toTuple()) == 0) {
			this.space.out(msg.toTuple());
		}
		
		return t;
	}
	
	private Message wait_for_acceptance(Task t) throws RemoteException, TupleSpaceException {
		// TODO Auto-generated method stub

		Message msg = new Message(-1, "Contract Announcement Response", this.processID, 
				new Task(t.taskID, t.taskName, t.expirationTime, t.reward, -1, ""));
		Message received = new Message();
		
		received.setFromTuple(this.space.in(msg.toTuple()));
		System.out.println("Task proposed from " +  received.senderID + " " + received.task);
		
		return received;			
	}
	
	private boolean check(Message msg) {
		// TODO Auto-generated method stub
		System.out.println("Checking " + msg.task);
		Task task = msg.task;
		if (task.earning < this.threshold) {
			if(!task.priority.equals("high")) {
				System.out.println("Refused for earning " + task);
				return false;
			}
		}
		if(ratings.containsKey(msg.senderID)) {
			if (ratings.get(msg.senderID) < 5) {
				if(!task.priority.equals("medium")) {
					System.out.println("Refused for ratings " + task);
					return false;
				}
			}
		}
		
		return true;
	}
	
	private void accept(Message received) throws RemoteException, TupleSpaceException {
		System.out.println("After check i am accepting" + received.task);
		Message response = new Message(this.processID, "Leader Accept", received.senderID, received.task);
		this.space.out(response.toTuple());
		
	}
	
	private boolean endOfContract(Message received) throws RemoteException, TupleSpaceException {
		// TODO Auto-generated method stub
		
		Message receive_closure = new Message(received.senderID, "Contract Closure", this.processID, received.task);
		
		long start = System.currentTimeMillis();
		while(this.space.count(receive_closure.toTuple()) == 0) {
			if ((System.currentTimeMillis() - start) > 20*1000) {
				Message remove_accept = new Message(this.processID, "Leader Accept", received.senderID, received.task);
				System.out.println("The Process " + received.senderID + "was too late (" + (System.currentTimeMillis() - start) +") for the closing of contract " + received.task);
				this.space.inp(remove_accept.toTuple());
				return false;
			}
		}
		
		this.space.in(receive_closure.toTuple());
		SimpleDateFormat formatter1=new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy", Locale.ENGLISH);
		
		Date expiration = null;
		try {
			expiration = formatter1.parse(received.task.expirationTime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Date now = new Date(System.currentTimeMillis());
		
		if (expiration.after(now)) {
			if(ratings.containsKey(received.senderID)) {
				int rate = ratings.get(received.senderID);
				ratings.replace(received.senderID, rate-1);
			}
			else {
				ratings.put(received.senderID, -1);
			}
				
		}else {
			if(expiration.getTime() - now.getTime() > 15*1000) {
				if(ratings.containsKey(received.senderID)) {
					int rate = ratings.get(received.senderID);
					ratings.replace(received.senderID, rate+2);
				}
				else {
					ratings.put(received.senderID, 2);
				}
			}
			else if(expiration.getTime() - now.getTime() >10*1000) {
				if(ratings.containsKey(received.senderID)) {
					int rate = ratings.get(received.senderID);
					ratings.replace(received.senderID, rate+1);
				}
				else {
					ratings.put(received.senderID, 1);
				}
			}
			else if(expiration.getTime() - now.getTime() > 5*1000) {
				if(ratings.containsKey(received.senderID)) {
					int rate = ratings.get(received.senderID);
				}
				else {
					ratings.put(received.senderID, 0);
				}
			}
			
		}		
		
		System.out.println("Successful Task " + receive_closure.task);
		
		return true;
	}

	
	

}
