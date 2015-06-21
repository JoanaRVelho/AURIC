package hcim.auric.general_activities.images;

import hcim.auric.database.intrusions.SessionDatabase;
import hcim.auric.recognition.Picture;

import java.util.ArrayList;
import java.util.List;

import android.view.View;

public class SlideShowSetOfPictures extends SlideShowActivity {
	public static final String EXTRA_ID_IDX = "extra_idx";
	public static final String EXTRA_ID_LIST = "extra_list";

	@Override
	protected List<Picture> getPictures() {
		SessionDatabase db = SessionDatabase.getInstance(this);
		List<String> picsIds = getIntent().getExtras().getStringArrayList(
				EXTRA_ID_LIST);
		List<Picture> list = new ArrayList<Picture>();
		Picture p;
		
		for(String id :picsIds){
			p = db.getPictureOfTheIntruder(id);
			list.add(p);
		}

		return list;
	}
	
	@Override
	protected void refresh(){
		typeIcon.setVisibility(View.GONE);
		setMessageVisibility(true);
		Picture p = pictures.get(current);
		setMessage(p.getDescription());
		
		super.refresh();
	}

	@Override
	protected int startAt() {
		return getIntent().getExtras().getInt(EXTRA_ID_IDX);
	}
//
//	@Override
//	protected void onClickMessage() {
//		AlertDialog.Builder alertDialog;
//		alertDialog = new AlertDialog.Builder(
//				SlideShowSetOfPictures.this);
//		alertDialog.setTitle("Face Recognition");
//		alertDialog.setMessage("Is this a picture of you?");
//		alertDialog.setPositiveButton("YES",
//				getPositiveButtonOnClickListener());
//		alertDialog.setNeutralButton("NO",
//				getNegativeButtonOnClickListener());
//		alertDialog.setNegativeButton("Cancel", null);
//		alertDialog.show();
//	}
	//
	// private DialogInterface.OnClickListener
	// getPositiveButtonOnClickListener() {
	// return new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int which) {
	// updateFaceRecognitionData(FaceRecognition.MY_PICTURE_TYPE);
	// }
	// };
	// }
	//
	// private DialogInterface.OnClickListener
	// getNegativeButtonOnClickListener() {
	// return new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int which) {
	// updateFaceRecognitionData(FaceRecognition.INTRUDER_PICTURE_TYPE);
	// }
	// };
	// }
	//
	// private void updateFaceRecognitionData(String type) {
	// Picture picture = getCurrentPicture();
	// picture.setType(type);
	//
	// IntrusionsDatabase intrusionDB = IntrusionsDatabase.getInstance(this);
	// intrusionDB.updatePictureType(picture);
	//
	// if(type.equals(FaceRecognition.MY_PICTURE_TYPE)){
	// picture.setID(StringGenerator.generateOwnerName());
	// }
	//
	// FaceRecognition recognition = FaceRecognition
	// .getInstance(SlideShowSetOfPictures.this);
	// recognition.trainPicture(picture.getImage(), picture.getID());
	// recognition.stopTrain();
	//
	// PicturesDatabase picturesDB = PicturesDatabase
	// .getInstance(SlideShowSetOfPictures.this);
	//
	// if (picturesDB.hasPicture(picture.getID())) {
	// picturesDB.setPictureType(picture);
	// } else {
	// picturesDB.addPicture(picture);
	// }
	// }
}
