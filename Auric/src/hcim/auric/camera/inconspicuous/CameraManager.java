package hcim.auric.camera.inconspicuous;

import hcim.auric.utils.LogUtils;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;

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
			LogUtils.exception(e);
			camera = null;

			return;
		}
		try {
			SurfaceTexture dummySurfaceTextureF = new SurfaceTexture(0);
			try {
				camera.setPreviewTexture(dummySurfaceTextureF);
				camera.startPreview();
			} catch (RuntimeException e) {
				LogUtils.exception(e);
				return;
			}
			camera.takePicture(null, null, callback);
		} catch (Exception e) {
			LogUtils.exception(e);
			if (camera != null)
				camera.release();
		}
	}

}
