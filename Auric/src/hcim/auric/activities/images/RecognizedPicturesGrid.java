package hcim.auric.activities.images;

import hcim.auric.database.PicturesDatabase;
import hcim.auric.recognition.Picture;

import java.util.List;

import android.content.Intent;

public class RecognizedPicturesGrid extends PicturesGrid {

	@Override
	protected ImageAdapter getAdapter() {
		PicturesDatabase db = PicturesDatabase.getInstance(this);
		List<Picture> list = db.getAllPictures();

		return new ImageAdapter(this, list);
	}

	@Override
	protected void onPictureSelected(Picture p, int position) {
		Intent i = new Intent(getApplicationContext(), FullRecognizedPicture.class);
		i.putExtra(FullPicture.EXTRA_ID, p.getID());
		startActivity(i);
	}

}
