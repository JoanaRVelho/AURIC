package hcim.auric.authentication;

import android.graphics.Bitmap;

public class TaskMessage {
	String ID;
	Bitmap pic;
	
	public TaskMessage(String id, Bitmap bm) {
		this.ID = id;
		this.pic = bm;
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

}
