package hcim.auric.camera;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class PortraitCameraPreview extends SurfaceView implements
		SurfaceHolder.Callback {
	private Camera camera;
	private SurfaceHolder surfaceHolder;
	private PreviewCallback previewCallback;
	private WindowManager windowManager;

	@SuppressWarnings("deprecation")
	PortraitCameraPreview(Activity a, PreviewCallback p) {
		super(a);
		this.windowManager = a.getWindowManager();

		this.surfaceHolder = getHolder();
		this.surfaceHolder.addCallback(this);
		this.surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		this.previewCallback = p;
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		camera = Camera.open(CameraInfo.CAMERA_FACING_FRONT);
		camera.setPreviewCallback(previewCallback);
		try {
			camera.setPreviewDisplay(holder);
		} catch (IOException exception) {
			camera.release();
			camera = null;
		}

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
		camera.stopPreview();

		Camera.Parameters parameters = camera.getParameters();

		List<Size> sizes = parameters.getSupportedPreviewSizes();
		Size optimalSize = getOptimalPreviewSize(sizes, w, h);
		parameters.setPreviewSize(optimalSize.width, optimalSize.height);
		sizes = parameters.getSupportedPictureSizes();
		optimalSize = getOptimalPreviewSize(sizes, w, h);
		parameters.setPictureSize(optimalSize.width, optimalSize.height);

		setCameraDisplayOrientation(windowManager, 0, camera);

		camera.setParameters(parameters);

		camera.setPreviewCallback(previewCallback);
		camera.startPreview();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		camera.setPreviewCallback(null);
		camera.stopPreview();
		camera.release();
		camera = null;
	}

	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
		final float ASPECT_TOLERANCE = 0.05f;
		if (w < h) {
			int temp = w;
			w = h;
			h = temp;
		}
		float targetRatio = (float) w / h;
		if (sizes == null)
			return null;

		Size optimalSize = null;
		float minDiff = Float.MAX_VALUE;

		int targetHeight = h;

		// Try to find an size match aspect ratio and size
		for (Size size : sizes) {
			float ratio = (float) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
				continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}

		// Cannot find the one match the aspect ratio, ignore the requirement
		if (optimalSize == null) {
			minDiff = Float.MAX_VALUE;
			for (Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}

	public static void setCameraDisplayOrientation(WindowManager windowManager,
			int cameraId, android.hardware.Camera camera) {

		camera.setDisplayOrientation(getCameraDisplayOrientation(windowManager));
	}

	public static int getCameraDisplayOrientation(WindowManager windowManager) {
		int rotation = windowManager.getDefaultDisplay().getRotation();
		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}
		return (90 + 360 - degrees) % 360;
	}
}
