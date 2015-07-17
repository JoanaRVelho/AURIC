package hcim.auric.camera;

import hcim.auric.utils.LogUtils;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;

/**
 * CameraManager takes pictures without preview
 * 
 * @author Joana Velho
 * 
 */
public class CameraManager {
	private Camera camera;
	private Camera.PictureCallback callback;

	/**
	 * Constructor
	 * 
	 * @param pictureCallback
	 *            : Callback used to supply image data from a photo capture.
	 */
	public CameraManager(Camera.PictureCallback pictureCallback) {
		camera = null;
		callback = pictureCallback;
	}

	/**
	 * Takes a pictures without preview
	 */
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
