package hcim.auric.activities.images;

import hcim.auric.recognition.FaceRecognition;
import hcim.auric.recognition.Picture;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hcim.intrusiondetection.R;

public abstract class FullPicture extends Activity {
	public static final String EXTRA_ID = "extra";

	static final String TAG = "AURIC";

	protected Picture picture;

	public Picture getPicture() {
		return picture;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.full_image);

		Bundle extras = getIntent().getExtras();
		String id = extras.getString(EXTRA_ID);

		picture = getPicture(id);

		ImageView imageView = (ImageView) findViewById(R.id.full_image_view);
		imageView.setImageBitmap(picture.getImage());

		String type = picture.getType();
		Log.i(TAG, picture.toString());

		if (type != null) {
			if (type.equals(FaceRecognition.INTRUDER_PICTURE_TYPE))
				setBackground(Color.rgb(204, 0, 0));

			if (type.equals(FaceRecognition.MY_PICTURE_TYPE))
				setBackground(Color.rgb(112, 173, 71));
			
			if(type.equals(FaceRecognition.UNKNOWN_PICTURE_TYPE))
				setBackground(Color.BLACK);
		}
	}

	private void setBackground(int color) {
		LinearLayout l = (LinearLayout)findViewById(R.id.background);
		l.setBackgroundColor(color);
	}

	protected abstract Picture getPicture(String id);

}