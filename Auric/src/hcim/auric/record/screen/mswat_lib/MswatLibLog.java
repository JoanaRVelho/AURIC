package hcim.auric.record.screen.mswat_lib;

import hcim.auric.database.ConfigurationDatabase;
import hcim.auric.record.screen.AbstractLog;
import android.content.Context;
import android.content.Intent;

public class MswatLibLog extends AbstractLog{

	public MswatLibLog(Context c) {
		super(c);
	}

	public void stopLogging() {
		Intent intent = new Intent();
		intent.setAction("swat_interaction");
		intent.putExtra("logging", false);
		context.sendBroadcast(intent);
	}

	public void startLogging(String intrusionID) {
		Intent intent = new Intent();
		intent.setAction("swat_interaction");
		intent.putExtra("logging", true);
		intent.putExtra("timestamp", intrusionID);

		context.sendBroadcast(intent);
	}

	@Override
	public String type() {
		return ConfigurationDatabase.MSWAT_LIB_LOG;
	}
}
