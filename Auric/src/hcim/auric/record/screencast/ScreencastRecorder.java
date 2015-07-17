package hcim.auric.record.screencast;

import hcim.auric.record.IRecorder;
import hcim.auric.record.RecorderManager;
import hcim.auric.utils.FileManager;
import android.content.Context;

public class ScreencastRecorder implements IRecorder {

	private RecordScreen recorder;
	private Context context;

	public ScreencastRecorder(Context c) {
		this.context = c;
	}

	@Override
	public void start(String intrusionID) {
		recorder = new RecordScreen(new FileManager(context), intrusionID);
		recorder.start();
	}

	@Override
	public void stop() {
		if (recorder != null) {
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
