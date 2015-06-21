package hcim.auric.general_activities.images;

import hcim.auric.database.configs.PicturesDatabase;
import hcim.auric.recognition.FaceRecognition;
import hcim.auric.recognition.Picture;

import java.util.List;

import android.view.View;

import com.hcim.intrusiondetection.R;

public class SlideShowRecognizedPictures extends SlideShowActivity {

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
	protected void refresh(){
		String id = pictures.get(current).getID();
		PicturesDatabase db = PicturesDatabase.getInstance(this);

		if (db.hasPicture(id)) {
			String type = db.getPicture(id).getType();

			if (type != null) {
				if (type.equals(FaceRecognition.getMyPictureType())) {
					typeIcon.setVisibility(View.VISIBLE);
					typeIcon.setImageResource(R.drawable.green);
				} else if (type.equals(FaceRecognition.getIntruderPictureType())) {
					typeIcon.setVisibility(View.VISIBLE);
					typeIcon.setImageResource(R.drawable.red);
				} else {
					typeIcon.setVisibility(View.INVISIBLE);
				}
			}
		} else {
			typeIcon.setVisibility(View.INVISIBLE);
		}
		
		setMessageVisibility(false);

		super.refresh();
	}
}
