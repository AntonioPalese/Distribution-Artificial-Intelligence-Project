import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Random;

import lights.*;
import lights.interfaces.*;

public class TupleAdapter {
	
	ITuple tuple;
	
	public TupleAdapter(ITuple tuple) {
		this.tuple = tuple;
	}	
	
	public TupleAdapter adaptValue(Object value) {
		if (value instanceof Integer) {
			this.tuple.add(new Field().setValue(Integer.valueOf((int)value)));			
		}
		else if(value instanceof String) {
			this.tuple.add(new Field().setValue((String)value));	
		}
		return this;
	}
	
	public TupleAdapter adaptType(Class<?> class_type) {		
		this.tuple.add(new Field().setType(class_type));
		
		return this;
	}
	
	public ITuple get() {
		return this.tuple;
	}
	
}
