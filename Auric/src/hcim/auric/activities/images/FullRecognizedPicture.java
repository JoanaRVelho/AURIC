package hcim.auric.activities.images;

import hcim.auric.database.PicturesDatabase;
import hcim.auric.recognition.Picture;

public class FullRecognizedPicture extends FullPicture {
	
	@Override
	protected Picture getPicture(String id) {
		PicturesDatabase db = PicturesDatabase.getInstance(this);
		return db.getPicture(id);
	}
}
