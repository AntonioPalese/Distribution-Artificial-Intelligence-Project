import lights.interfaces.ITuple;
import lights.utils.Tuplable;
import lights.*;

public class Message implements Tuplable{
	
	
	public int senderID;
	public String command;
	public int receiverID;		
	public Task task;
	
	public Message(int senderID, String command, int receiverID, Task task) {
		super();
		this.senderID = senderID;
		this.command = command;
		this.receiverID = receiverID;
		this.task = task;
	}
	
	public Message() {}
	
	public Task getTask() {
		return task;
	}

	@Override
	public void setFromTuple(ITuple arg0) {
		// TODO Auto-generated method stub
		this.senderID = Integer.valueOf(arg0.get(0).toString());
		this.command = arg0.get(1).toString();
		this.receiverID = Integer.valueOf(arg0.get(2).toString());
		int taskID = Integer.valueOf(arg0.get(3).toString());
		String taskName = arg0.get(4).toString();
		String expirationTime = arg0.get(5).toString();
		int reward = Integer.valueOf(arg0.get(6).toString());
		int earning = Integer.valueOf(arg0.get(7).toString());
		String priority = arg0.get(8).toString();
		
		
		this.task =  new Task(taskID, taskName, expirationTime,reward, earning, priority);		
	}

	@Override
	public ITuple toTuple() {
		// TODO Auto-generated method stub
		ITuple tuple = new Tuple();
		
		if(this.senderID != -1)		
			tuple = tuple.add(new Field().setValue(Integer.valueOf(this.senderID)));
		else
			tuple = tuple.add(new Field().setType(Integer.class));
		
		if(this.command != "")
			tuple = tuple.add(new Field().setValue(command));
		else
			tuple = tuple.add(new Field().setType(String.class));
		
		if(this.receiverID != -1)
			tuple = tuple.add(new Field().setValue(Integer.valueOf(this.receiverID)));
		else
			tuple = tuple.add(new Field().setType(Integer.class));
		
		if(this.task.taskID != -1)
			tuple = tuple.add(new Field().setValue(Integer.valueOf(this.task.taskID)));
		else
			tuple = tuple.add(new Field().setType(Integer.class));
		
		if(this.task.taskName != "")
			tuple = tuple.add(new Field().setValue(this.task.taskName));	
		else
			tuple = tuple.add(new Field().setType(String.class));
		
		if(this.task.expirationTime != "")
			tuple = tuple.add(new Field().setValue(this.task.expirationTime));	
		else
			tuple = tuple.add(new Field().setType(String.class));
		
		if(this.task.reward != -1)
			tuple = tuple.add(new Field().setValue(Integer.valueOf(this.task.reward)));
		else
			tuple = tuple.add(new Field().setType(Integer.class));
		
		if(this.task.earning != -1)
			tuple = tuple.add(new Field().setValue(Integer.valueOf(this.task.earning)));
		else
			tuple = tuple.add(new Field().setType(Integer.class));
		
		if(this.task.priority != "")
			tuple = tuple.add(new Field().setValue(this.task.priority));	
		else
			tuple = tuple.add(new Field().setType(String.class));
		
		
		
		return tuple;
	}

	@Override
	public String toString() {
		return "Message [senderID=" + senderID + ", command=" + command + ", receiverID="
				+ receiverID + ", task=" + task + "]";
	}

}
