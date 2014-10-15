package hcim.auric.audit;

import hcim.auric.authentication.IntrusionNotifier;
import hcim.auric.camera.CameraManager;
import hcim.auric.camera.FrontPictureCallback;
import hcim.auric.database.ConfigurationDatabase;
import hcim.auric.database.IntrusionsDatabase;
import hcim.auric.intrusion.Intrusion;
import hcim.auric.record.IntruderCaptureTask;

import java.util.Timer;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.Context;
import android.util.Log;

public abstract class AbstractAuditTask extends Thread {
	protected static final String TAG = "AURIC";

	public static final String ACTION_NEW_PICTURE = "new picture";

	private IntruderCaptureTask timerTask;
	private Timer timer;
	protected CameraManager camera;

	protected Intrusion currentIntrusion;
	protected IntrusionNotifier notifier;
	protected LinkedBlockingQueue<TaskMessage> queue;
	protected IntrusionsDatabase intrusionsDB;
	protected Context context;

	protected boolean screenOff;

	public AbstractAuditTask(Context context) {
		this.context = context;
		this.queue = new LinkedBlockingQueue<TaskMessage>();
		this.notifier = new IntrusionNotifier(context);
		this.camera = new CameraManager(new FrontPictureCallback(this));
		this.intrusionsDB = IntrusionsDatabase.getInstance(context);
		this.screenOff = false;
		this.timer = new Timer();
	}

	public void addTaskMessage(TaskMessage msg) {
		// ignore task ACTION NEW PICTURE after screen is off
		if (screenOff && msg.getID() == ACTION_NEW_PICTURE) {
			return;
		}
		queue.add(msg);
	}

	public Context getContext() {
		return context;
	}

	public IntruderCaptureTask getTimerTask() {
		return timerTask;
	}

	@Override
	public void interrupt() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		super.interrupt();
	}

	protected void startTimerTask(boolean delay) {
		ConfigurationDatabase db = ConfigurationDatabase.getInstance(context);
		int period = db.getCameraCaptureOption();
		
		timerTask = new IntruderCaptureTask(this.camera);
		timer = new Timer();
		timer.scheduleAtFixedRate(timerTask, delay ? period : 0, period);
		
		Log.d(TAG, "AuditTask - start timer task");
	}

	protected void stopTimerTask() {
		if (timer != null) {
			timer.cancel();
			timer = null;
			
			Log.d(TAG, "AuditTask - stop timer task");
		}
		timerTask = null;
	}
}
