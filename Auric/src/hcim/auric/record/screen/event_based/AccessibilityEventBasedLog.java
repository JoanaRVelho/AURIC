package hcim.auric.record.screen.event_based;

import hcim.auric.database.ConfigurationDatabase;
import hcim.auric.record.screen.AbstractLog;
import android.content.Context;

public class AccessibilityEventBasedLog extends AbstractLog {

	public AccessibilityEventBasedLog(Context c) {
		super(c);
	}

	@Override
	public void startLogging(String intrusionID) {
		RecordEventBasedLog.setIntrusion(intrusionID);
	}

	@Override
	public void stopLogging() {
		RecordEventBasedLog.setIntrusion(null);

	}

	@Override
	public String type() {
		return ConfigurationDatabase.TEXT_LOG;
	}

}
