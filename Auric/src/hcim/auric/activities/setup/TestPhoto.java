package hcim.auric.activities.setup;

import hcim.auric.recognition.FaceRecognition;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hcim.intrusiondetection.R;

public class TestPhoto extends Activity {
	private static final String RESULT = "Result: ";
	private static final String INTRUDER = "Intruder";
	private static final String OWNER = "Owner";
	private static final String DETAILS = "\n\nDetails\n";
	private static final String MATCH = "MATCH THE OWNER\n";
	private static final String NOT_MATCH = "DOES NOT MATCH THE OWNER\n";
	private static final String DISTANCE = "Distance = ";
	private static final int REQUEST_IMAGE_CAPTURE = 1;

	private FaceRecognition faceRecognition;

	private ImageView img;
	private TextView txt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_photo_view);

		img = (ImageView) findViewById(R.id.mypicture);
		txt = (TextView) findViewById(R.id.result);

		faceRecognition = FaceRecognition.getInstance(this);
		
		dispatchTakePictureIntent();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
			Bitmap imageBitmap = (Bitmap) extras.get("data");
			img.setImageBitmap(imageBitmap);

			boolean faceDetected = faceRecognition.detectFace(imageBitmap);
			boolean ownerRecognized = faceRecognition
					.recognizePicture(imageBitmap);
			int result = faceRecognition.getLastResult();
			boolean matchOwner = faceRecognition.lastResultMatchOwner();

			if (faceDetected) {
				txt.setText(RESULT
						+ (ownerRecognized ? OWNER : INTRUDER)
						+ DETAILS
						+ (matchOwner ? (MATCH + DISTANCE + result) : NOT_MATCH));
			} else {
				txt.setText("Face Detection Failed! Take another picture!");
			}
		}
	}

	/**
	 * 
	 * @param v
	 */
	public void takePicture(View v) {
		dispatchTakePictureIntent();
	}

	private void dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
		}
	}
}
