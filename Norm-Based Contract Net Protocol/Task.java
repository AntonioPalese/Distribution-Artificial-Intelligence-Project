
public class Task {
	
	public int taskID;
	public String taskName;
	public String expirationTime;
	public int reward;
	public int earning;
	String priority;
	
	public Task(int taskID, String taskName, String expirationTime, int reward, int earning, String priority) {
		super();
		this.taskID = taskID;
		this.taskName = taskName;
		this.expirationTime = expirationTime;
		this.reward = reward;
		this.earning = earning;
		this.priority = priority;
	}

	@Override
	public String toString() {
		return "Task [taskID=" + taskID + ", taskName=" + taskName + ", expirationTime=" + expirationTime + ", reward="
				+ reward + ", earning=" + earning + ", priority=" + priority + "]";
	}
	
	
}
