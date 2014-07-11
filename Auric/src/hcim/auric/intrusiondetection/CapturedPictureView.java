package hcim.auric.intrusiondetection;

import hcim.auric.database.IntrusionsDatabase;
import hcim.auric.intrusion.Intrusion;

import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hcim.intrusiondetection.R;

public class CapturedPictureView extends Activity {

	private int pictureIdx;
	private int totalPictures;
	private ImageView img;
	private List<Bitmap> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		View decorView = getWindow().getDecorView();
//		int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
//		decorView.setSystemUiVisibility(uiOptions);
//		ActionBar actionBar = getActionBar();
//		actionBar.hide();
		
		setContentView(R.layout.captured_pictures);
		pictureIdx = 0;

		Bundle extras = getIntent().getExtras();
		String intrusion = extras.getString("id");

		Intrusion i = IntrusionsDatabase.getIntrusion(intrusion);

		list = i.getImages();
		totalPictures = list.size();

		Button next = (Button) findViewById(R.id.next);
		Button prev = (Button) findViewById(R.id.previous);
		img = (ImageView) findViewById(R.id.image_view);

		TextView t = (TextView) findViewById(R.id.textView1);
		t.append(" "+intrusion);
		
		setImageView(list.get(pictureIdx));

		next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				incPictureIdx();
				setImageView(list.get(pictureIdx));
			}

		});

		prev.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				decPictureIdx();
				setImageView(list.get(pictureIdx));
			}

		});
	}

	private void incPictureIdx() {
		pictureIdx = (pictureIdx + 1) % totalPictures;
	}

	private void decPictureIdx() {
		pictureIdx = (pictureIdx + 1) % totalPictures;
	}

	private void setImageView(Bitmap bm) {
		img.setImageBitmap(bm);
	}

	@Override
	public void finish() {
		pictureIdx = 0;
		super.finish();
	}
}
