package hcim.auric.camera;

import java.util.List;

import org.opencv.android.JavaCameraView;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.AttributeSet;

public class CameraViewBackup extends JavaCameraView {

	public CameraViewBackup(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public List<String> getEffectList() {
		return mCamera.getParameters().getSupportedColorEffects();
	}

	public boolean isEffectSupported() {
		return (mCamera.getParameters().getColorEffect() != null);
	}

	public String getEffect() {
		return mCamera.getParameters().getColorEffect();
	}

//	public void setOrientation() {
//		if (mCamera != null) {
//			disconnectCamera();
//			mCamera.setDisplayOrientation(90);
//			connectCamera(getWidth(), getHeight());
//		}
//	}

	public void setEffect(String effect) {
		Camera.Parameters params = mCamera.getParameters();
		params.setColorEffect(effect);
		mCamera.setParameters(params);
	}

	public List<Size> getResolutionList() {
		return mCamera.getParameters().getSupportedPreviewSizes();
	}

	public void setResolution(Size resolution) {
		disconnectCamera();
		mMaxHeight = resolution.height;
		mMaxWidth = resolution.width;
		connectCamera(getWidth(), getHeight());
	}

	public void setResolution(int w, int h) {
		disconnectCamera();
		mMaxHeight = h;
		mMaxWidth = w;

		connectCamera(getWidth(), getHeight());
	}

	public void setAutofocus() {
		Camera.Parameters parameters = mCamera.getParameters();
		parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
		mCamera.setParameters(parameters);
	}

	public void setFrontCamera() {
		disconnectCamera();
		setCameraIndex(org.opencv.android.CameraBridgeViewBase.CAMERA_ID_FRONT);
		connectCamera(getWidth(), getHeight());
	}

	public void setBackCamera() {
		disconnectCamera();
		setCameraIndex(org.opencv.android.CameraBridgeViewBase.CAMERA_ID_BACK);
		connectCamera(getWidth(), getHeight());
	}

	public int numberCameras() {
		return Camera.getNumberOfCameras();
	}

	public Size getResolution() {
		return mCamera.getParameters().getPreviewSize();
	}

	public void release() {
		this.disconnectCamera();
	}
}

