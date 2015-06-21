package hcim.auric.audit;

import hcim.auric.camera.inconspicuous.CameraManager;
import hcim.auric.camera.inconspicuous.FrontPictureCallback;
import hcim.auric.database.SettingsPreferences;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;

public class IntruderCaptureTask {
	private CameraManager camera;

	private TimerTask timerTask;
	private Timer timer;
	private int cameraPeriod;

	public IntruderCaptureTask(AuditQueue queue, Context context) {
		this.camera = new CameraManager(new FrontPictureCallback(queue));
		SettingsPreferences db = new SettingsPreferences(context);

		this.cameraPeriod = db.getCameraPeriod();
	}

	public void start(boolean delay) {
		if (delay) {
			this.timerTask = new TimerTask() {

				@Override
				public void run() {
					camera.takePicture();
				}
			};
			timer = new Timer();
			timer.schedule(timerTask, cameraPeriod);
		} else {
			camera.takePicture();
		}
	}

	public void stop() {
		if (timerTask != null)
			timerTask.cancel();

		if (timer != null)
			timer.cancel();

		timer = null;
		timerTask = null;
	}
}
