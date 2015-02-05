package hcim.auric.activities.setup;

import hcim.auric.camera.FrontPictureCallback;
import hcim.auric.recognition.FaceRecognition;
import hcim.auric.recognition.RecognitionResult;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hcim.intrusiondetection.R;

public class TestPhoto extends Activity {
	private static final String RESULT = "Result: ";
	private static final String INTRUDER = "Intruder";
	private static final String OWNER = "Owner";
	private static final String DETAILS = "\n\nDetails\n";
	private static final String MATCH = "matches the owner\n";
	private static final String NOT_MATCH = "does not match the owner\n";
	private static final String DISTANCE = "Distance = ";
	private static final String TAG = "AURIC";

	private FaceRecognition faceRecognition;

	private ImageView img;
	private TextView txt;
	private Camera camera;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_photo_view);

		img = (ImageView) findViewById(R.id.mypicture);
		txt = (TextView) findViewById(R.id.result);

		faceRecognition = FaceRecognition.getInstance(this);
	}

	/**
	 * 
	 * @param v
	 */
	public void takePicture(View v) {
		camera = null;

		try {
			camera = Camera.open(CameraInfo.CAMERA_FACING_FRONT);
		} catch (RuntimeException e) {
			camera = null;
			Log.e(TAG, "CameraManager - " + e.getMessage());
		}
		try {
			if (camera == null) {
			} else {
				SurfaceTexture dummySurfaceTextureF = new SurfaceTexture(0);
				try {
					camera.setPreviewTexture(dummySurfaceTextureF);
					camera.startPreview();
				} catch (Exception e) {
					Log.e(TAG, "CameraManager - " + e.getMessage());
				}

				camera.takePicture(null, null, new PictureCallback() {

					@Override
					public void onPictureTaken(byte[] data, Camera camera) {
						Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
								data.length);
						bitmap = FrontPictureCallback.rotateBitmap(bitmap);
						
						img.setImageBitmap(bitmap);
						txt.setText(getData(bitmap));
					}
				});
			}
		} catch (Exception e) {
			if (camera != null)
				camera.release();
		}
	}

	private String getData(Bitmap imageBitmap) {
		RecognitionResult result = faceRecognition
				.recognizePicture(imageBitmap);

		boolean faceDetected = result.isFaceDetected();

		if (faceDetected) {
			boolean ownerRecognized = result.isFaceRecognized();
			int diff = result.getDifference();
			boolean matchOwner = FaceRecognition.matchsOwnerName(result
					.getMatch());
			return (RESULT + (ownerRecognized ? OWNER : INTRUDER) + DETAILS + (matchOwner ? (MATCH
					+ DISTANCE + diff)
					: NOT_MATCH));
		} else {
			return ("Face Detection Failed! Take another picture!");
		}
	}
}
