import java.rmi.RemoteException;
import java.util.Random;

import lights.*;
import lights.interfaces.*;

public class ContractExecutor extends Contractor {
	
	public long thresholdTime;	
	
	public ContractExecutor(int processID, int headProcessID, IRemoteTupleSpace space) {
		super();
		this.processID = processID;
		this.headProcessID = headProcessID;		
		this.space = space;
		this.thresholdTime = 5*1000;
	}
	
	@Override
	public boolean act_as_contractor(String desc) {
		// TODO Auto-generated method stub
		
		int contract_id = propose();
		if (contract_id == -1) {
			System.out.println("Contract " + desc + " was refused");
			return false;
		}
		ITuple accepted_tuple;
		if((accepted_tuple =  wait_for_leader_acceptance(contract_id)) != null) {
			System.out.println("Process Leader " + this.headProcessID + " accepted contract " + contract_id);
			Contract contr = execute(accepted_tuple);
			close(contr);
			return true;
		}else {	
			System.out.println("Process Leader " + this.headProcessID + " refused contract " + contract_id);			
			return false;
		}
	}
	
	public void close(Contract contr) {
		
		TupleAdapter adapter = new TupleAdapter(new Tuple())
				.adaptValue(Integer.valueOf(this.processID))
				.adaptValue("Contract Closure")
				.adaptValue(Integer.valueOf(this.headProcessID))
				.adaptValue(contr.name)
				.adaptValue(Integer.valueOf(this.contractID))				
				.adaptValue(Integer.valueOf(contr.earning))
				.adaptValue(Integer.valueOf(0));
		
		ITuple contracTupleTemplate = adapter.get();
		
		try {
			this.space.out(contracTupleTemplate);
			System.out.println("Contract " + contr.contractID  + " Closed");
		} catch (RemoteException | TupleSpaceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public Contract execute(ITuple accepted) {
		
		Contract contr = new Contract(
				String.valueOf(accepted.get(3)),
				Integer.valueOf(Integer.valueOf(accepted.get(4).toString())),
				Integer.valueOf(Integer.valueOf(accepted.get(5).toString())),
				Integer.valueOf(Integer.valueOf(accepted.get(6).toString())));
		
		this.contractID = contr.contractID;
		
		int waitsec = 5;
		try {
			Thread.sleep(waitsec*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		contr.failed = 0;	
		
		this.activeContracts.add(contr);		
		return contr;
	}
	
	
	
	public ITuple wait_for_leader_acceptance(int contract_id) {
	
		TupleAdapter adapter = new TupleAdapter(new Tuple())
				.adaptValue(Integer.valueOf(this.headProcessID))
				.adaptValue("Contract Leader Accept")
				.adaptValue(Integer.valueOf(this.processID))
				.adaptType(String.class)
				.adaptValue(Integer.valueOf(contract_id))				
				.adaptType(Integer.class)	
				.adaptType(Integer.class);
		
		ITuple read_tuple = adapter.get();
		
		int waitsec = 5;
		try {
			Thread.sleep(waitsec*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			ITuple received_tupe =  this.space.inp(read_tuple);
			return received_tupe;
		} catch (RemoteException | TupleSpaceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public int propose() {	
		
		TupleAdapter adapter = new TupleAdapter(new Tuple())		
				.adaptValue(Integer.valueOf(this.headProcessID))
				.adaptValue("Contract Proposal")
				.adaptType(String.class)
				.adaptType(Integer.class)
				.adaptType(Integer.class)
				.adaptType(Integer.class);
		
		ITuple read_tuple = adapter.get();
		
		try {			
			long start = System.currentTimeMillis();
			while(this.space.count(read_tuple) == 0) {
				if ((System.currentTimeMillis() - start) > this.thresholdTime) {
					return -1;
				}
			}
			ITuple leader_propose = this.space.in(read_tuple);	
			
			Contract contr = new Contract(
					String.valueOf(leader_propose.get(2)),
					Integer.valueOf(Integer.valueOf(leader_propose.get(3).toString())),
					Integer.valueOf(Integer.valueOf(leader_propose.get(4).toString())),
					Integer.valueOf(Integer.valueOf(leader_propose.get(5).toString())));
			
			
			contr.earning = new Random().nextInt((10 - 0) + 1) + 0;	
			
			TupleAdapter adapter_myProposeTuple = new TupleAdapter(new Tuple())		
					.adaptValue(Integer.valueOf(this.processID))
					.adaptValue("Contract Proposal")
					.adaptValue(Integer.valueOf(this.headProcessID))
					.adaptValue(contr.name)
					.adaptValue(Integer.valueOf(contr.contractID))
					.adaptValue(Integer.valueOf(contr.earning))
					.adaptValue(Integer.valueOf(0));
			
			ITuple myProposeTuple = adapter_myProposeTuple.get();
			
			this.space.out(myProposeTuple);	
			System.out.println("Process " + this.processID + " proposed contract " + contr.contractID + " of type " + contr.name + " with earning " + contr.earning);
			return contr.contractID;
		} catch (TupleSpaceException | RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}				
		
	}
	
	
	
	
	

}
