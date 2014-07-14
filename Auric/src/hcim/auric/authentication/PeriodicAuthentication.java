package hcim.auric.authentication;

import hcim.auric.camera.CameraManager;
import android.util.Log;

public class PeriodicAuthentication extends Thread {
	private static final long PERIOD = 5000;

	private CameraManager camera;

	public PeriodicAuthentication(CameraManager camera) {
		this.camera = camera;
	}

	@Override
	public void run() {
		while (true) {
			try {
				sleep(PERIOD);
			} catch (InterruptedException e) {
				return;
			}
			Log.d("SCREEN", "interrupted="+ isInterrupted());
			
			if(isInterrupted()){
				return;
			}
				camera.takePicture();
						
		}

	}
}
