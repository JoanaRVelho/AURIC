package hcim.auric.activities.images;

import hcim.auric.database.IntrusionsDatabase;
import hcim.auric.intrusion.Intrusion;
import hcim.auric.recognition.FaceRecognition;
import hcim.auric.recognition.Picture;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;

public class IntruderPictureGrid extends PicturesGrid {
	public static final String EXTRA_ID = "extra";

	private int selectedLocation;

	@Override
	protected ImageAdapter getAdapter() {
		Bundle extras = getIntent().getExtras();
		String intrusionID = extras.getString(EXTRA_ID);

		IntrusionsDatabase db = IntrusionsDatabase.getInstance(this);
		Intrusion i = db.getIntrusion(intrusionID);

		List<Picture> intrusionPicturesList = i.getImages();
		List<Picture> result = new ArrayList<Picture>();

		FaceRecognition f = FaceRecognition.getInstance(this);

		for (Picture p : intrusionPicturesList) {
			if (f.detectFace(p.getImage())) {
				result.add(p);
			}
		}
		
		if (result.size() > 0)
			return new ImageAdapter(this, result);
		else {
			return null;
		}
	}

	@Override
	protected void onPictureSelected(Picture selected, int position) {
		selectedLocation = position;

		Intent i = new Intent(getApplicationContext(),
				FullIntruderPicture.class);
		i.putExtra(FullPicture.EXTRA_ID, selected.getID());
		startActivityForResult(i, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == 0) {
			if (data.hasExtra("return")) {
				String type = (String) data.getExtras().getString("return");
				adapter.setPictureType(type, selectedLocation);
				selectedLocation = -1;
			}
		}
	}

	@Override
	protected void onResume() {
		adapter.notifyDataSetChanged();
		super.onResume();
	}
}