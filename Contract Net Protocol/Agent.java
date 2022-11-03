import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Random;

import lights.*;
import lights.interfaces.*;

public class Agent {
	int headProcessID;
	int processID;
	public String name;
	public IRemoteTupleSpace space;	
	public ElectionThread et;
	public ArrayList<String> queue;
	public ArrayList<Thread> threadQueue;
	
	
	public Agent() {
		processID = new Random().nextInt((10000 - 0) + 1) + 0;
		name = new String("TupleSpace2");		
		try {
			space = (IRemoteTupleSpace)Naming.lookup("//localhost/" + name);
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		this.threadQueue = new ArrayList<Thread>();	
		
		
		this.et = new ElectionThread(space, processID, this.threadQueue);
		this.et.start();
		
		this.queue = new ArrayList<String>();		
		this.queue.add("Stampa");
		this.queue.add("Scannerizza");
		this.queue.add("Controlla");	
		this.queue.add("Agisci");
		this.queue.add("Intercetta");
		this.queue.add("Elimina");
	}
	
	public void act_as_leader() {	
		this.headProcessID = this.et.getLeaderID();
		
		if (!this.queue.isEmpty()) {
			String q = this.queue.remove(this.queue.size()-1);
			ContractThread ct = new ContractThread(this.processID, this.headProcessID, this.space, q);
			this.threadQueue.add(ct);
			ct.start();		
		}			
		
	}
	
	public void act_as_process() {
		this.headProcessID = this.et.getLeaderID();
		
		ContractExecutor ce = new ContractExecutor(this.processID, this.headProcessID, this.space);
		ce.act_as_contractor("");		
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
