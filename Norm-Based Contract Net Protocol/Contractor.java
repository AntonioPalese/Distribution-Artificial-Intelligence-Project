import java.util.ArrayList;

public abstract class Contractor {
	public int processID;
	public int leaderID;
	public IRemoteTupleSpace space;
	
	public Contractor() {
	
	}
	
	public abstract boolean act_as_contractor(Task t);
}
