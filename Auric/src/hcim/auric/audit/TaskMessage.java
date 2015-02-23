package hcim.auric.audit;

import android.graphics.Bitmap;

public class TaskMessage {
	private String ID;
	private Bitmap pic;
	private String timestamp;
	private String packageName;
	private boolean intrusion;

	public boolean isIntrusion() {
		return intrusion;
	}

	public void setIntrusion(boolean intrusion) {
		this.intrusion = intrusion;
	}

	public TaskMessage(String id) {
		this.ID = id;
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

	public Bitmap getPic() {
		return pic;
	}

	public void setPic(Bitmap pic) {
		this.pic = pic;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

}
