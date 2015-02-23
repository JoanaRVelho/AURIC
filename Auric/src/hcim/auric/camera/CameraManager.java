package hcim.auric.camera;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.util.Log;

public class CameraManager {
	protected static final String TAG = "AURIC";

	private Camera camera;
	private FrontPictureCallback callback;

	public CameraManager(FrontPictureCallback c) {
		camera = null;
		callback = c;
	}

	public void takePicture() {
		camera = null;

		try {
			camera = Camera.open(CameraInfo.CAMERA_FACING_FRONT);
		} catch (RuntimeException e) {
			Log.e(TAG, "CameraManager open() - " + e.getMessage());
			camera = null;

			return;
		}
		try {
			SurfaceTexture dummySurfaceTextureF = new SurfaceTexture(0);
			try {
				camera.setPreviewTexture(dummySurfaceTextureF);
				camera.startPreview();
			} catch (Exception e) {
				Log.e(TAG, "CameraManager Preview- " + e.getMessage());
				return;
			}
			camera.takePicture(null, null, callback);
		} catch (Exception e) {
			Log.e(TAG, "CameraManager takePicture - " + e.getMessage());
			if (camera != null)
				camera.release();
		}
	}

}
