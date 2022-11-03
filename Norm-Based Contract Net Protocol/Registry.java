

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import lights.interfaces.ITuple;

public class Registry {	
	public static void main(String[] args) throws Exception{
        String name;

		
	    if (args.length ==  1)
	            name = args[0];
	    else
	            name = "TupleSpace";

		IRemoteTupleSpace remoteTupleSpace = new RemoteTupleSpace(name);
		
		try {
          LocateRegistry.createRegistry(1099);
          System.out.println("Java RMI registry created.");
		} catch (RemoteException e) {
          System.out.println("Java RMI registry already exists.");
		}
		
        Naming.rebind("//localhost/" + name, remoteTupleSpace);
        System.out.println("Remote TupleSpace registered on //localhost:1099/TupleSpace.");
 
		
	}
}
