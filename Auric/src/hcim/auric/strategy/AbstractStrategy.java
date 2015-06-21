package hcim.auric.strategy;

import hcim.auric.database.intrusions.SessionDatabase;
import hcim.auric.intrusion.Intrusion;
import hcim.auric.intrusion.Session;
import hcim.auric.record.IRecorder;
import hcim.auric.record.RecorderManager;
import hcim.auric.service.IntrusionNotifier;
import android.content.Context;

public abstract class AbstractStrategy implements IStrategy {
	protected Session currentSession;
	protected Intrusion currentIntrusion;
	protected SessionDatabase sessionsDB;

	protected IntrusionNotifier notifier;
	protected IRecorder recorder;

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
}
