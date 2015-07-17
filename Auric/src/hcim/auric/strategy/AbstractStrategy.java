package hcim.auric.strategy;

import hcim.auric.Intrusion;
import hcim.auric.Session;
import hcim.auric.data.SessionDatabase;
import hcim.auric.detector.IDetector;
import hcim.auric.record.IRecorder;
import hcim.auric.record.RecorderManager;
import android.content.Context;

public abstract class AbstractStrategy implements IStrategy {
	protected Session currentSession;
	protected Intrusion currentIntrusion;
	protected SessionDatabase sessionsDB;

	protected IntrusionNotifier notifier;
	protected IRecorder recorder;
	protected IDetector detector;

	public AbstractStrategy(Context context) {
		this.sessionsDB = SessionDatabase.getInstance(context);
		this.currentIntrusion = null;
		this.currentSession = null;

		this.notifier = new IntrusionNotifier(context);
		this.recorder = RecorderManager.getSelectedRecorder(context);
	}

	@Override
	public final IRecorder getRecorder() {
		return recorder;
	}

	@Override
	public IDetector getDetector() {
		return detector;
	}
}
