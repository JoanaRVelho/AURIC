package hcim.auric.record.screen.screencast_root;

import android.content.Context;
import hcim.auric.database.ConfigurationDatabase;
import hcim.auric.record.screen.Log;
import hcim.auric.utils.FileManager;

public class ScreencastRootLog extends Log {

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
		recorder.stopThread();
	}

	@Override
	public String type() {
		return ConfigurationDatabase.SCREENCAST_ROOT_LOG;
	}

}
