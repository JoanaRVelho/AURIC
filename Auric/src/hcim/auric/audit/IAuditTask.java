package hcim.auric.audit;

import hcim.auric.detector.IDetector;
import hcim.auric.record.IRecorder;
import hcim.auric.strategy.IStrategy;

public interface IAuditTask {

	public final String TAG = "AURIC";

	public final String ACTION_NEW_PICTURE = "NEW PICTURE";
	public final String ACTION_NEW_APP = "NEW APP";
	public final String ACTION_RESULT = "RESULT";
	public final String ACTION_OFF = "OFF";
	public final String ACTION_ON = "ON";

	public IStrategy getStrategy();

	public IRecorder getRecorder();

	public IDetector getDetector();

	public void stopTask();

	public void startTask();
}
