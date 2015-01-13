package hcim.auric.activities.images;

import hcim.auric.database.PicturesDatabase;
import hcim.auric.recognition.Picture;

import java.util.List;

import android.graphics.Bitmap;

public class RecognizedPicturesSlideShow extends SlideShowActivity {

	@Override
	protected List<Picture> getPictures() {
		PicturesDatabase db = PicturesDatabase.getInstance(this);
		List<Picture> list = db.getAllPictures();

		cropAllPictures(list);

		return list;
	}

	private void cropAllPictures(List<Picture> list) {
		Bitmap bmp, newBmp;
		for (Picture p : list) {
			bmp = p.getImage();
			newBmp = Bitmap.createBitmap(bmp, 3, 3, bmp.getWidth()-6,
					bmp.getHeight()-6);
			p.setBitmap(newBmp);
		}
	}

	@Override
	protected int startAt() {
		return 0;
	}

	@Override
	protected void onClickMessage() {
	}

}
