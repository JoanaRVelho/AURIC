package hcim.auric.activities.images;

import hcim.auric.database.IntrusionsDatabase;
import hcim.auric.database.PicturesDatabase;
import hcim.auric.recognition.FaceRecognition;
import hcim.auric.recognition.Picture;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

public class FullIntruderPicture extends FullPicture {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Toast.makeText(this, "Double tap to identify this picture.",
				Toast.LENGTH_LONG).show();
	}

	@SuppressWarnings("deprecation")
	final GestureDetector gestureDetector = new GestureDetector(
			new GestureDetector.SimpleOnGestureListener() {
				@Override
				public boolean onDoubleTap(MotionEvent e) {
					AlertDialog.Builder alertDialog;
					alertDialog = new AlertDialog.Builder(
							FullIntruderPicture.this);
					alertDialog.setTitle("Face Recognition");
					alertDialog.setMessage("Is this a picture of you?");
					alertDialog.setPositiveButton("YES",
							getPositiveButtonOnClickListener());
					alertDialog.setNeutralButton("NO",
							getNegativeButtonOnClickListener());
					alertDialog.setNegativeButton("Cancel", null);
					alertDialog.show();

					return false;
				}
			});

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	};

	private DialogInterface.OnClickListener getPositiveButtonOnClickListener() {
		return new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				updateFaceRecognitionData(FaceRecognition.MY_PICTURE_TYPE);

				Intent data = new Intent();
				data.putExtra("return", FaceRecognition.MY_PICTURE_TYPE);
				setResult(RESULT_OK, data);

				finish();
			}
		};
	}

	private DialogInterface.OnClickListener getNegativeButtonOnClickListener() {
		return new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				updateFaceRecognitionData(FaceRecognition.INTRUDER_PICTURE_TYPE);

				Intent data = new Intent();
				data.putExtra("return", FaceRecognition.MY_PICTURE_TYPE);
				setResult(RESULT_OK, data);

				finish();
			}
		};
	}

	private void updateFaceRecognitionData(String type) {
		picture.setType(type);

		FaceRecognition recognition = FaceRecognition
				.getInstance(FullIntruderPicture.this);
		recognition.trainPicture(picture.getImage(), picture.getID());

		PicturesDatabase picturesDB = PicturesDatabase
				.getInstance(FullIntruderPicture.this);

		String msg;

		if (picturesDB.hasPicture(picture.getID())) {
			picturesDB.setPictureType(picture);
			msg = "picture updated";
		} else {
			picturesDB.addPicture(picture);
			msg = "picture added";
		}

		IntrusionsDatabase intrusionDB = IntrusionsDatabase.getInstance(this);
		intrusionDB.updatePictureType(picture);

		Toast.makeText(FullIntruderPicture.this, "Face Recognition: " + msg,
				Toast.LENGTH_SHORT).show();
		finish();
	}

	@Override
	protected Picture getPicture(String id) {
		IntrusionsDatabase db = IntrusionsDatabase.getInstance(this);
		return db.getPictureOfTheIntruder(id);
	}

}