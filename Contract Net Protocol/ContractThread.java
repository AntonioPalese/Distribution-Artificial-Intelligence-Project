
public class ContractThread extends Thread {
	
	public int processID;
	public int headProcessID;
	public IRemoteTupleSpace space;
	public String type;
	
	public ContractThread(int processID, int headProcessID, IRemoteTupleSpace space, String type) {
		super();
		this.processID = processID;
		this.headProcessID = headProcessID;
		this.space = space;
		this.type = type;
	}
	
	public void run() {
		Contractor contr = new ContractManager(this.processID, this.headProcessID, this.space);
		while(!contr.act_as_contractor(this.type)) { };		
		//this.run();
	}
	
	
}
