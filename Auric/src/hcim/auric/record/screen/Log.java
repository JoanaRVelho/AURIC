package hcim.auric.record.screen;

import android.content.Context;

public abstract class Log {
	protected Context context;

	public Log(Context c) {
		context = c;
	}

	public abstract void startLogging(String intrusionID);

	public abstract void stopLogging();

	public abstract String type();

}
