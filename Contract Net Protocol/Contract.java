import java.io.Serializable;

public class Contract implements Serializable{

	public String name;
	int contractID;
	int earning;
	int failed;

	public Contract(String name, int contractID) {
		this.name = name;
		this.contractID = contractID;		
	}
	
	public Contract(String name, int contractID, int earning, int failed) {
		this.name = name;
		this.contractID = contractID;
		this.earning = earning;
		this.failed = failed;
	}
}
