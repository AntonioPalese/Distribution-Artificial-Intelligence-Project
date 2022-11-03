import java.util.HashMap;

public class ContractThread extends Thread {
	
	public int processID;
	public int leaderID;
	public IRemoteTupleSpace space;
	public HashMap<Integer, Integer> ratings;
	public Task task;
	
	public ContractThread(int processID, int leaderID, IRemoteTupleSpace space, HashMap<Integer, Integer> ratings, Task task) {
		super();
		this.processID = processID;
		this.leaderID = leaderID;
		this.space = space;
		this.ratings = ratings;
		this.task = task;
	}
	
	public void run() {
		Contractor contr = new TaskManager(this.processID, this.leaderID, this.space, this.ratings);
		while(!contr.act_as_contractor(this.task)) { };		
		//this.run();
	}
	
	
}
