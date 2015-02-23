package hcim.auric.audit;

import hcim.auric.camera.CameraManager;
import hcim.auric.camera.FrontPictureCallback;
import hcim.auric.database.configs.ConfigurationDatabase;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;

public class IntruderCaptureTask {
	private CameraManager camera;

	private TimerTask timerTask;
	private Timer timer;
	private boolean stop;
	private int cameraPeriod;

	public IntruderCaptureTask(AuditQueue queue, Context context) {
		this.camera = new CameraManager(new FrontPictureCallback(queue));
		ConfigurationDatabase db = ConfigurationDatabase.getInstance(context);

		this.cameraPeriod = db.getCameraPeriod();
	}

	public void start(boolean delay) {
		if (delay) {
			this.timerTask = new TimerTask() {

				@Override
				public void run() {
					if (!stop)
						camera.takePicture();
				}
			};
			stop = false;
			timer = new Timer();
			timer.schedule(timerTask, cameraPeriod);
		}else{
			camera.takePicture();
		}
	}

	public void stop() {
		stop = true;
		if (timer != null && timerTask != null) {
			timerTask.cancel();
			timer.cancel();
			timer = null;
			timerTask = null;
		}
	}
}
