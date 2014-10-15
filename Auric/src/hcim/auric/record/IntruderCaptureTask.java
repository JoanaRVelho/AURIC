package hcim.auric.record;

import hcim.auric.camera.CameraManager;

import java.util.TimerTask;

public class IntruderCaptureTask extends TimerTask {
	CameraManager camera;

	public IntruderCaptureTask(CameraManager c) {
		this.camera = c;
	}

	@Override
	public void run() {
		camera.takePicture();
	}
}
