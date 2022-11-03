import java.util.ArrayList;
import java.util.Random;

public abstract class Contractor {
	public int processID;
	public int headProcessID;
	public int contractID;
	public ArrayList<Contract> activeContracts;
	public IRemoteTupleSpace space;
	
	public Contractor() {
		this.activeContracts = new ArrayList<Contract>();
	}
	
	public abstract boolean act_as_contractor(String desc);
}
