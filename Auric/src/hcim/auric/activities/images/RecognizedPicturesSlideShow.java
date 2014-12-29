package hcim.auric.activities.images;

import hcim.auric.database.PicturesDatabase;
import hcim.auric.recognition.Picture;

import java.util.List;

public class RecognizedPicturesSlideShow extends SlideShowActivity {

	@Override
	protected List<Picture> getPictures() {
		PicturesDatabase db = PicturesDatabase.getInstance(this);
		List<Picture> list = db.getAllPictures();

		return list;
	}

	@Override
	protected int startAt() {
		return 0;
	}

	@Override
	protected void onClickMessage() {
	}

}
