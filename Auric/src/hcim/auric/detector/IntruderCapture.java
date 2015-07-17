package hcim.auric.detector;

import hcim.auric.camera.CameraManager;

import java.util.Timer;
import java.util.TimerTask;

import android.hardware.Camera;

/**
 * Captures intruder pictures.
 * 
 * @author Joana Velho
 * 
 */
public class IntruderCapture {
	private CameraManager camera;

	private TimerTask timerTask;
	private Timer timer;
	private int cameraPeriod;

	/**
	 * Constructor
	 * 
	 * @param callback
	 *            : picture callback
	 * @param cameraPeriod
	 *            : period between picture captures
	 */
	public IntruderCapture(Camera.PictureCallback callback, int cameraPeriod) {
		this.camera = new CameraManager(callback);
		this.cameraPeriod = cameraPeriod;
	}

	/**
	 * Starts capturing
	 * 
	 * @param delay
	 *            : with or without delay
	 */
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

	/**
	 * Stops picture capturing
	 */
	public void stop() {
		if (timerTask != null)
			timerTask.cancel();

		if (timer != null)
			timer.cancel();

		timer = null;
		timerTask = null;
	}

}
