import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import lights.*;
import lights.interfaces.*;

public class Agent {
	int leaderID;
	int processID;
	public String name;
	
	public IRemoteTupleSpace space;		
	public ArrayList<Task> queue;
	public ElectionThread et;
	public ArrayList<Thread> threadQueue;
	public HashMap<Integer, Integer> ratings;
	
	
	public Agent() {
		this.processID = new Random().nextInt((10000 - 0) + 1) + 0;
		this.name = new String("TupleSpace");
		this.threadQueue = new ArrayList<Thread>();
		this.ratings = new HashMap<Integer, Integer>();
		try {
			space = (IRemoteTupleSpace)Naming.lookup("//localhost/" + name);
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		this.et = new ElectionThread(space, processID, this.threadQueue);
		this.et.start();
//		
		this.queue = new ArrayList<Task>();		
		this.queue.add(new Task(new Random().nextInt((10000 - 0) + 1) + 0, 
				   "Stampa", new Date(System.currentTimeMillis()+200*1000).toString(), 7, 0,"null"));
		this.queue.add(new Task(new Random().nextInt((10000 - 0) + 1) + 0, 
				   "Scannerizza", new Date(System.currentTimeMillis()+200*1000).toString(), 8, 0,"null"));
		this.queue.add(new Task(new Random().nextInt((10000 - 0) + 1) + 0, 
				   "Controlla", new Date(System.currentTimeMillis()+200*1000).toString(), 9, 0,"null"));
		this.queue.add(new Task(new Random().nextInt((10000 - 0) + 1) + 0, 
				   "Agisci", new Date(System.currentTimeMillis()+200*1000).toString(), 6, 0,"null"));
		this.queue.add(new Task(new Random().nextInt((10000 - 0) + 1) + 0, 
				   "Intercetta", new Date(System.currentTimeMillis()+200*1000).toString(), 4, 0,"null"));
		this.queue.add(new Task(new Random().nextInt((10000 - 0) + 1) + 0, 
				   "Elimina", new Date(System.currentTimeMillis()+200*1000).toString(), 5, 0,"null"));
	}
	
	public void act_as_leader() {	
		this.leaderID = this.et.getLeaderID();
		
		if (!this.queue.isEmpty()) {
			Task q = this.queue.remove(this.queue.size()-1);
			ContractThread ct = new ContractThread(this.processID, this.leaderID, this.space, this.ratings, q);
			this.threadQueue.add(ct);
			ct.start();		
		}
		
		
//		Message msg = new Message(-1, "", -1,new Task(-1, "", "", -1, -1, ""));
//		Message msgget = new Message();
//		ITuple tuple;
//		try {
//			while((tuple = space.rdp(msg.toTuple())) != null) {
//				msgget.setFromTuple(tuple);
//				System.out.println(msgget);
//				int waitsec = 5;
//				try {
//					Thread.sleep(waitsec*1000);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		} catch (RemoteException | TupleSpaceException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
	
	public void act_as_process() {
		this.leaderID = this.et.getLeaderID();
		
		Contractor te = new TaskExecutor(this.processID, this.leaderID, this.space);
		te.act_as_contractor(null);
				
	}
	
	
	
	public void act() {
		int waitsec = 3;
		try {
			Thread.sleep(waitsec*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		if(this.et.amILeader()) {
			this.act_as_leader();
		}else {
			this.act_as_process();
		}	
		
		this.act();
	}
	
	public static void main(String[] args) {				
		Agent a = new Agent();	
		
		a.act();
		
	}
}
