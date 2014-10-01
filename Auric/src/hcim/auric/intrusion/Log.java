package hcim.auric.intrusion;

import android.content.Context;
import android.content.Intent;

public class Log {
	private Context c;
	private String id; //timestamp

	public Log(Context c) {
		this.c = c;
	}

	public Log(String log) {
		this.id = log;
	}

	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}

	public void stopLogging() {
		Intent intent = new Intent();
		intent.setAction("swat_interaction");
		intent.putExtra("logging", false);
		c.sendBroadcast(intent);

	}

	public void startLogging() {
		this.id = System.currentTimeMillis() + "";
		Intent intent = new Intent();
		intent.setAction("swat_interaction");
		intent.putExtra("logging", true);
		intent.putExtra("timestamp", id);

		c.sendBroadcast(intent);
	}
}
