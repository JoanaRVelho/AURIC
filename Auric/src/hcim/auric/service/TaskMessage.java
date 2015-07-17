package hcim.auric.service;


public class TaskMessage {
	public final static String ACTION_RESULT = "RESULT";
	public final static String ACTION_OFF = "OFF";
	public final static String ACTION_OFF_DESTROY = "OFF and DESTROY";
	public final static String ACTION_ON = "ON";

	private String ID;
	private String timestamp;
	private String packageName;
	private boolean intrusion;

	public TaskMessage(String id) {
		this.ID = id;
	}

	public boolean isIntrusion() {
		return intrusion;
	}

	public void setIntrusion(boolean intrusion) {
		this.intrusion = intrusion;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

}
