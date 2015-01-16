package hcim.auric.activities.images;

import hcim.auric.database.IntrusionsDatabase;
import hcim.auric.database.PicturesDatabase;
import hcim.auric.intrusion.Intrusion;
import hcim.auric.recognition.FaceRecognition;
import hcim.auric.recognition.Picture;
import hcim.auric.utils.StringGenerator;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

public class SlideShowIntrusionPictures extends SlideShowActivity {
	public static final String EXTRA_ID = "extra";
	
	private FaceRecognition recognition;
	

	@Override
	protected List<Picture> getPictures() {
		Bundle extras = getIntent().getExtras();
		String intrusionID = extras.getString(EXTRA_ID);

		IntrusionsDatabase db = IntrusionsDatabase.getInstance(this);
		Intrusion i = db.getIntrusion(intrusionID);

		recognition = FaceRecognition.getInstance(this);

		return i.getImages();
	}

	@Override
	protected int startAt() {
		return 0;
	}

	@Override
	protected void refresh() {
		super.refresh();

		boolean detected = recognition.detectFace(getCurrentPicture().getImage());

		setMessageVisibility(detected);
		
	}

	
	private DialogInterface.OnClickListener getPositiveButtonOnClickListener() {
		return new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				updateFaceRecognitionData(FaceRecognition.MY_PICTURE_TYPE);
			}
		};
	}

	private DialogInterface.OnClickListener getNegativeButtonOnClickListener() {
		return new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				updateFaceRecognitionData(FaceRecognition.INTRUDER_PICTURE_TYPE);
			}
		};
	}

	private void updateFaceRecognitionData(String type) {
		Picture picture = getCurrentPicture();
		picture.setType(type);
		
		IntrusionsDatabase intrusionDB = IntrusionsDatabase.getInstance(this);
		intrusionDB.updatePictureType(picture);
		
		if(type.equals(FaceRecognition.MY_PICTURE_TYPE)){
			picture.setID(StringGenerator.generateOwnerName());
		}

		FaceRecognition recognition = FaceRecognition
				.getInstance(SlideShowIntrusionPictures.this);
		recognition.trainPicture(picture.getImage(), picture.getID());
		recognition.stopTrain();

		PicturesDatabase picturesDB = PicturesDatabase
				.getInstance(SlideShowIntrusionPictures.this);

		if (picturesDB.hasPicture(picture.getID())) {
			picturesDB.setPictureType(picture);
		} else {
			picturesDB.addPicture(picture);
		}
	}

	@Override
	protected void onClickMessage() {
		AlertDialog.Builder alertDialog;
		alertDialog = new AlertDialog.Builder(
				SlideShowIntrusionPictures.this);
		alertDialog.setTitle("Face Recognition");
		alertDialog.setMessage("Is this a picture of you?");
		alertDialog.setPositiveButton("YES",
				getPositiveButtonOnClickListener());
		alertDialog.setNeutralButton("NO",
				getNegativeButtonOnClickListener());
		alertDialog.setNegativeButton("Cancel", null);
		alertDialog.show();
	}

}
