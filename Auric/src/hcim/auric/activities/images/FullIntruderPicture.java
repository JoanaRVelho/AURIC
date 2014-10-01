package hcim.auric.activities.images;

import hcim.auric.database.IntrusionsDatabase;
import hcim.auric.database.PicturesDatabase;
import hcim.auric.recognition.FaceRecognition;
import hcim.auric.recognition.Picture;
import hcim.auric.utils.StringGenerator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

import com.hcim.intrusiondetection.R;

public class FullIntruderPicture extends Activity {
	public static final String EXTRA_ID = "extra";
	
	private Picture picture;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.full_image);

		Bundle extras = getIntent().getExtras();
		String id = extras.getString(EXTRA_ID);
		IntrusionsDatabase db = IntrusionsDatabase.getInstance(this);
		picture = db.getIntruserPicture(id);

		ImageView imageView = (ImageView) findViewById(R.id.full_image_view);
		imageView.setImageBitmap(picture.getImage());
		
		Toast.makeText(this, "Double tap to identify this picture.", Toast.LENGTH_LONG).show();
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
				update(FaceRecognition.MY_PICTURE_TYPE);

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
				update(FaceRecognition.INTRUDER_PICTURE_TYPE);

				Intent data = new Intent();
				data.putExtra("return", FaceRecognition.MY_PICTURE_TYPE);
				setResult(RESULT_OK, data);

				finish();
			}
		};
	}

	void update(String type) {
		if (picture.getID() == null)
			picture.setID(StringGenerator.generateString());

		picture.setType(type);

		FaceRecognition f = FaceRecognition
				.getInstance(FullIntruderPicture.this);
		f.trainPicture(picture.getImage(), picture.getID());

		PicturesDatabase db = PicturesDatabase
				.getInstance(FullIntruderPicture.this);
		db.addPicture(picture);

		Toast.makeText(FullIntruderPicture.this,
				"Face Recognition: picture added", Toast.LENGTH_SHORT).show();
		finish();
	}

}