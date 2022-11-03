
import java.rmi.Remote;
import java.rmi.RemoteException;

import lights.interfaces.ITuple;
import lights.interfaces.TupleSpaceException;

public interface IRemoteTupleSpace extends Remote{
	
	public String getName() throws RemoteException;

	public void out(ITuple tuple) throws RemoteException, TupleSpaceException;

	public void outg(ITuple[] tuples) throws RemoteException, TupleSpaceException;

	public ITuple in(ITuple template) throws RemoteException, TupleSpaceException;

	public ITuple inp(ITuple template) throws RemoteException, TupleSpaceException;

	public ITuple[] ing(ITuple template) throws RemoteException, TupleSpaceException;

	public ITuple rd(ITuple template) throws RemoteException, TupleSpaceException;

	public ITuple rdp(ITuple template) throws RemoteException, TupleSpaceException;

	public ITuple[] rdg(ITuple template) throws RemoteException, TupleSpaceException;

	public int count(ITuple template) throws RemoteException, TupleSpaceException;
	
}
