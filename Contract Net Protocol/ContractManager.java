import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Random;

import lights.*;
import lights.interfaces.*;

public class ContractManager extends Contractor {
	
	int threshold;
	long thresholdTime;

	public ContractManager(int processID, int headProcessID, IRemoteTupleSpace space) {
		super();
		this.processID = processID;
		this.headProcessID = headProcessID;		
		this.space = space;
		this.threshold = this.processID / 1000;
		this.thresholdTime = 20*1000;
		this.contractID = new Random().nextInt((10000 - 0) + 1) + 0;
	}
	
	public int get_contract_id(){
		return this.contractID;
	}
	
	@Override
	public boolean act_as_contractor(String desc) {
		// TODO Auto-generated method stub	
		
		Contract contr = this.callForProposal(desc);				
		
		// If some contractTuple arrives
		ITuple contractTuple;
		if ((contractTuple = wait_for_acceptance(contr)) != null) {
			
			Contract contract = new Contract(
					String.valueOf(contractTuple.get(3)),
					Integer.valueOf(Integer.valueOf(contractTuple.get(4).toString())),
					Integer.valueOf(Integer.valueOf(contractTuple.get(5).toString())),
					Integer.valueOf(Integer.valueOf(contractTuple.get(6).toString())));
			
			this.accept(contractTuple, desc);
			
			int process_id = Integer.valueOf(contractTuple.get(0).toString());
			this.activeContracts.add(contract);			
			if(this.endOfContract(process_id, contract)) {
				return true;
			}
			else
				return false; 
		}		
		return false;
	}
	
	public boolean endOfContract(int process_id, Contract contr) {
		// contract closure
		
		TupleAdapter adapter = new TupleAdapter(new Tuple())
				.adaptValue(Integer.valueOf(process_id))
				.adaptValue("Contract Closure")
				.adaptValue(Integer.valueOf(this.processID))
				.adaptType(String.class)
				.adaptType(Integer.class)				
				.adaptType(Integer.class)	
				.adaptType(Integer.class);
		
		ITuple contracTupleTemplate = adapter.get();		
		
		try {
			long start = System.currentTimeMillis();
			while(this.space.count(contracTupleTemplate) == 0) {
				if ((System.currentTimeMillis() - start) > this.thresholdTime) {
					adapter = new TupleAdapter(new Tuple())
							.adaptValue(Integer.valueOf(this.processID))
							.adaptValue("Contract Leader Accept")
							.adaptValue(Integer.valueOf(process_id))
							.adaptValue(contr.name)
							.adaptValue(Integer.valueOf(contr.contractID))				
							.adaptValue(Integer.valueOf(contr.earning))	
							.adaptValue(Integer.valueOf(0));
					ITuple remove_accept_toolate = adapter.get();
					System.out.println("The Process " + process_id + "was too late (" + (System.currentTimeMillis() - start) +") for the closing of contract " + contr.contractID);
					this.space.inp(remove_accept_toolate);
					return false;
				}
			}
			ITuple contractTuple = this.space.in(contracTupleTemplate);		
			
			
			if (contr.failed == 0) {// contr.failed non checkato ancora hardcodato
				System.out.println("Succesfull Contract " + contr.contractID + " with earning " + contr.earning + " from the process " + process_id);
				return true;
			}else {
				System.out.println("Contract "+ contr.contractID + " Failed");
				return false;
			}
		} catch (TupleSpaceException | RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}		
	}
	
	public boolean check(ITuple contractTuple) {
		
		Contract contract = new Contract(
				String.valueOf(contractTuple.get(3)),
				Integer.valueOf(Integer.valueOf(contractTuple.get(4).toString())),
				Integer.valueOf(Integer.valueOf(contractTuple.get(5).toString())),
				Integer.valueOf(Integer.valueOf(contractTuple.get(6).toString())));
		
		if (contractTuple == null)
			return false;
		
//		if (contract.earning >= this.threshold)
//			return true;
//		else
//			return false;
		
		return true;
	}
	
	public void accept(ITuple contractTuple, String type) {
		int process_id = Integer.valueOf(contractTuple.get(0).toString());
		int contract_id = Integer.valueOf(contractTuple.get(4).toString());		
		int earning = Integer.valueOf(contractTuple.get(5).toString());	
		
		TupleAdapter adapter = new TupleAdapter(new Tuple())
				.adaptValue(Integer.valueOf(this.processID))
				.adaptValue("Contract Leader Accept")
				.adaptValue(Integer.valueOf(process_id))
				.adaptValue(type)
				.adaptValue(Integer.valueOf(contract_id))				
				.adaptValue(Integer.valueOf(earning))	
				.adaptValue(Integer.valueOf(0));
		
		ITuple contracTupleTemplate = adapter.get();
		
		
		try {
			this.space.out(contracTupleTemplate);
			System.out.println("Leader accepted process " + process_id + " for the contract " + contract_id + " of type " + type + " with earning of " + earning);
		} catch (TupleSpaceException | RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public ITuple wait_for_acceptance(Contract contr) {
		
		TupleAdapter adapter = new TupleAdapter(new Tuple())
				.adaptType(Integer.class)
				.adaptValue("Contract Proposal")
				.adaptValue(Integer.valueOf(this.processID))
				.adaptValue(contr.name)
				.adaptValue(Integer.valueOf(contr.contractID))				
				.adaptType(Integer.class)	
				.adaptType(Integer.class);
		
		ITuple contracTupleTemplate = adapter.get();
		
		try {
			ITuple contractTuple = this.space.in(contracTupleTemplate);
			if(check(contractTuple)) {
				//propose
				return contractTuple;
			}else {
				//reject
				return null;				
			}
		} catch (TupleSpaceException | RemoteException e) {
			// TODO Auto-generated catch block			
			e.printStackTrace();
			return null;
		}
	}
	
	public Contract callForProposal(String contractDesc) {
		Contract contr = new Contract(contractDesc, this.contractID);	
		
		TupleAdapter adapter = new TupleAdapter(new Tuple())		
								.adaptValue(Integer.valueOf(this.processID))
								.adaptValue("Contract Proposal")
								.adaptValue(contr.name)
								.adaptValue(Integer.valueOf(contr.contractID))
								.adaptValue(Integer.valueOf(0))
								.adaptValue(Integer.valueOf(0));
		
		ITuple contracTuple = adapter.get();
		
		
		try {
			if (this.space.count(contracTuple) == 0) {
				//System.out.println(contracTuple);
				this.space.out(contracTuple);
			}			
		} catch (TupleSpaceException | RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return contr;				
	}

}
