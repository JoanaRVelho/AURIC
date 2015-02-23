package hcim.auric.record.events;

import hcim.auric.record.AbstractRecorder;
import hcim.auric.record.RecorderManager;
import android.content.Context;

public class EventRecorder extends AbstractRecorder {
	private RecordEvents recorder;

	public EventRecorder(Context c) {
		super(c);
		recorder = new RecordEvents(c);
	}

	@Override
	public void start(String intrusionID) {
		recorder.setIntrusion(intrusionID);
	}

	@Override
	public void stop() {
		if (recorder != null) {
			recorder.setIntrusion(null);
		}
	}

	@Override
	public String type() {
		return RecorderManager.EVENT_BASED;
	}

	@Override
	public void destroy() {
		recorder.destroy();
	}

}
