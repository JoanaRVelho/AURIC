package hcim.auric.detector;

import hcim.auric.audit.AuditQueue;
import hcim.auric.audit.IntruderCaptureTask;
import android.content.Context;

public class WifiDetector implements IDetector {

	public WifiDetector(Context context, AuditQueue queue) {
		intruderCaptureTask = new IntruderCaptureTask(queue, context);
	}

	private IntruderCaptureTask intruderCaptureTask;

	@Override
	public void start() {
		intruderCaptureTask.start(false);
	}

	@Override
	public void stop() {
		intruderCaptureTask.stop();

	}

	@Override
	public String type() {
		return DetectorManager.WIFI;
	}
}
