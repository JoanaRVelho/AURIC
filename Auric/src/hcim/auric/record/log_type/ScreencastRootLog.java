package hcim.auric.record.log_type;

import android.content.Context;
import hcim.auric.database.ConfigurationDatabase;
import hcim.auric.record.screen.screencast_root.RecordScreencast;
import hcim.auric.utils.FileManager;

public class ScreencastRootLog extends AbstractLog {

	private RecordScreencast recorder;

	public ScreencastRootLog(Context c) {
		super(c);
	}

	@Override
	public void startLogging(String intrusionID) {
		recorder = new RecordScreencast(new FileManager(context), intrusionID);
		recorder.start();
	}

	@Override
	public void stopLogging() {
		if (recorder != null){
			recorder.stopThread();
			recorder = null;
		}
	}

	@Override
	public String type() {
		return ConfigurationDatabase.SCREENCAST_ROOT_LOG;
	}
}
