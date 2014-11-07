package hcim.auric.record.screen.textlog;

import hcim.auric.database.ConfigurationDatabase;
import android.content.Context;

public class SimpleTextLog extends hcim.auric.record.screen.Log {

	public SimpleTextLog(Context c) {
		super(c);
	}

	@Override
	public void startLogging(String intrusionID) {
		RecordSimpleText.setIntrusion(intrusionID);
	}

	@Override
	public void stopLogging() {
		RecordSimpleText.setIntrusion(null);

	}

	@Override
	public String type() {
		return ConfigurationDatabase.TEXT_LOG;
	}

}
