package hcim.auric.activities.images;

import hcim.auric.database.PicturesDatabase;
import hcim.auric.recognition.FaceRecognition;
import hcim.auric.recognition.Picture;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.hcim.intrusiondetection.R;

public class FullPicture extends Activity {
	public static final String EXTRA_ID = "extra";

	protected Picture picture;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.full_image);

		Bundle extras = getIntent().getExtras();
		String id = extras.getString(EXTRA_ID);
		PicturesDatabase db = PicturesDatabase.getInstance(this);
		picture = db.getPicture(id);

		ImageView imageView = (ImageView) findViewById(R.id.full_image_view);
		imageView.setImageBitmap(picture.getImage());

		String txt = null;

		if (picture.getType() != null) {
			if (picture.getType().equals(FaceRecognition.INTRUDER_PICTURE_TYPE))
				txt = "This is an intruder.";
			if (picture.getType().equals(FaceRecognition.MY_PICTURE_TYPE))
				txt="This is you.";
		}
		// txt+=" Double Tap to change.";
		if (txt != null)
			Toast.makeText(this, txt, Toast.LENGTH_LONG).show();
		
	}

}