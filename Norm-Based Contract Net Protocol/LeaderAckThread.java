import java.rmi.RemoteException;

import lights.*;
import lights.interfaces.*;

public class LeaderAckThread extends Thread{
	
	public IRemoteTupleSpace space;
	public int processID;
	public int leaderID;
	
	public LeaderAckThread(IRemoteTupleSpace space, int processID, int leaderID) {
		this.space = space;
		this.processID = processID;
		this.leaderID = leaderID;
	}
	
	@Override
	public void run() {		
		ITuple t = new Tuple().add(new Field().setType(Integer.class)).add(new Field().setValue("wake-up"))
				.add(new Field().setValue(Integer.valueOf(this.processID)));
		
		try {
			ITuple res =  this.space.in(t);
			
			
			int pid = Integer.valueOf(res.get(0).toString());			
			t = new Tuple().add(new Field().setValue(Integer.valueOf(this.leaderID))).add(new Field().setValue("wake-up ack"))
					.add(new Field().setValue(Integer.valueOf(pid)));			
			this.space.out(t);	
			
		} catch (RemoteException | TupleSpaceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		this.run();
	}
	
}
