package hcim.auric.authentication;

import hcim.auric.camera.CameraManager;

public class PeriodicAuthentication extends Thread {
	private static final long PERIOD = 10000;

	private CameraManager camera;

	public PeriodicAuthentication(CameraManager camera) {
		this.camera = camera;
	}

	@Override
	public void run() {

		while (!isInterrupted()) {
			camera.takePicture();

			try {
				sleep(PERIOD);
			} catch (InterruptedException e) {
			}
		}
	}
}
