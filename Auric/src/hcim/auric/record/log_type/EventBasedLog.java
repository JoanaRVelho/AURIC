package hcim.auric.record.log_type;

import hcim.auric.database.ConfigurationDatabase;
import hcim.auric.record.screen.event_based.RecordEventBasedLog;
import android.content.Context;

public class EventBasedLog extends AbstractLog {

	public EventBasedLog(Context c) {
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
		return ConfigurationDatabase.EVENT_LOG;
	}

}
