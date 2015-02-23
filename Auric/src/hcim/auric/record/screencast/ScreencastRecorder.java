package hcim.auric.record.screencast;

import hcim.auric.record.AbstractRecorder;
import hcim.auric.record.RecorderManager;
import hcim.auric.utils.FileManager;
import android.content.Context;

public class ScreencastRecorder extends AbstractRecorder {

	private RecordScreencast recorder;

	public ScreencastRecorder(Context c) {
		super(c);
	}

	@Override
	public void start(String intrusionID) {
		recorder = new RecordScreencast(new FileManager(context), intrusionID);
		recorder.start();
	}

	@Override
	public void stop() {
		if (recorder != null){
			recorder.stopThread();
			recorder = null;
		}
	}

	@Override
	public String type() {
		return RecorderManager.SCREENCAST_ROOT;
	}

	@Override
	public void destroy() {
	}
}
