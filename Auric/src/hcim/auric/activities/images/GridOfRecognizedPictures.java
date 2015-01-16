package hcim.auric.activities.images;

import hcim.auric.activities.settings.AddPictureActivity;
import hcim.auric.database.PicturesDatabase;
import hcim.auric.recognition.Picture;

import java.util.List;

import android.content.Intent;
import android.view.View;

public class GridOfRecognizedPictures extends GridOfPictures {

	@Override
	protected ImageAdapter getAdapter() {
		PicturesDatabase db = PicturesDatabase.getInstance(this);
		List<Picture> list = db.getAllPictures();

		return new ImageAdapter(this, list);
	}

	public void addPictures(View v) {
		Intent i = new Intent(GridOfRecognizedPictures.this,
				AddPictureActivity.class);
		startActivity(i);
	}

	@Override
	protected void onPictureSelected(Picture p, int position) {
		Intent i = new Intent(getApplicationContext(), FullPicture.class);
		i.putExtra(FullPicture.EXTRA_ID, p.getID());
		startActivity(i);
	}

}
