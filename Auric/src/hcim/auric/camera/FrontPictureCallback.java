package hcim.auric.camera;

import java.util.Observable;

import android.hardware.Camera;

/**
 * Callback interface used to supply image data from a photo capture. Notifies
 * observers on picture taken sending a byte array of the picture data.
 * 
 * @author Joana Velho
 * 
 */
public class FrontPictureCallback extends Observable implements
		Camera.PictureCallback {

	public FrontPictureCallback() {
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		camera.stopPreview();
		camera.release();

		setChanged();
		notifyObservers(data);
	}
}
