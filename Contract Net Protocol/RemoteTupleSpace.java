
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import lights.*;
import lights.interfaces.*;

public class RemoteTupleSpace extends UnicastRemoteObject implements IRemoteTupleSpace{
	
	private static final long serialVersionUID = 1L;
	
	public ITupleSpace space;

	public RemoteTupleSpace(String name) throws RemoteException {
		super();
		this.space = new TupleSpace(name);
	}
		

	@Override
	public String getName() throws RemoteException {
		// TODO Auto-generated method stub
		return this.space.getName();
	}


	@Override
	public void out(ITuple tuple) throws RemoteException, TupleSpaceException {
		// TODO Auto-generated method stub
		this.space.out(tuple);
	}


	@Override
	public void outg(ITuple[] tuples) throws RemoteException, TupleSpaceException {
		// TODO Auto-generated method stub
		this.space.outg(tuples);
	}


	@Override
	public ITuple in(ITuple template) throws RemoteException, TupleSpaceException {
		// TODO Auto-generated method stub
		return this.space.in(template);
	}


	@Override
	public ITuple inp(ITuple template) throws RemoteException, TupleSpaceException {
		// TODO Auto-generated method stub
		return this.space.inp(template);
	}


	@Override
	public ITuple[] ing(ITuple template) throws RemoteException, TupleSpaceException {
		// TODO Auto-generated method stub
		return this.space.ing(template);
	}


	@Override
	public ITuple rd(ITuple template) throws RemoteException, TupleSpaceException {
		// TODO Auto-generated method stub
		return this.space.rd(template);
	}


	@Override
	public ITuple rdp(ITuple template) throws RemoteException, TupleSpaceException {
		// TODO Auto-generated method stub
		return this.space.rdp(template);
	}


	@Override
	public ITuple[] rdg(ITuple template) throws RemoteException, TupleSpaceException {
		// TODO Auto-generated method stub
		return this.space.rdg(template);
	}


	@Override
	public int count(ITuple template) throws RemoteException, TupleSpaceException {
		// TODO Auto-generated method stub
		return this.space.count(template);
	}	
	
}
