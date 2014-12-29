package hcim.auric.activities.images;

import hcim.auric.database.PicturesDatabase;
import hcim.auric.recognition.FaceRecognition;
import hcim.auric.recognition.Picture;
import hcim.auric.record.screen.mswat_lib.OnSwipeTouchListener;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hcim.intrusiondetection.R;

public abstract class SlideShowActivity extends Activity {

	private int size;
	private int current;
	private List<Picture> pictures;

	private ImageView[] dots;
	private ImageView img;
	protected ImageView typeIcon;
	protected TextView msg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.slideshow);

		pictures = getPictures();
		size = pictures.size();
		current = startAt();

		// init dots
		LinearLayout lin = (LinearLayout) findViewById(R.id.dots);
		dots = new ImageView[size];
		for (int i = 0; i < dots.length; i++) {
			dots[i] = new ImageView(this);
			dots[i].setImageResource(R.drawable.cinzento);
			lin.addView(dots[i]);
		}

		// init txt
		msg = (TextView) findViewById(R.id.double_tap_msg);
		msg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onClickMessage();
			}
		});
		setMessageVisibility(false);

		// init type icon
		typeIcon = (ImageView) findViewById(R.id.type_pic);

		// init image bitmap
		img = (ImageView) findViewById(R.id.intruder_pict);
		img.setImageBitmap(pictures.get(current).getImage());
		img.setOnTouchListener(new OnSwipeTouchListener(this) {

			@Override
			public void onSwipeRight() {
				dots[current].setImageResource(R.drawable.cinzento);
				if (current == 0)
					current = size - 1;
				else
					current = (current - 1) % size;

				Log.i("AURIC", "swipe r current=" + current);
				refresh();
			}

			@Override
			public void onSwipeLeft() {
				dots[current].setImageResource(R.drawable.cinzento);
				current = (current + 1) % size;
				Log.i("AURIC", "swipe l current=" + current);
				refresh();
			}

			public boolean onTouch(View v, MotionEvent event) {
				boolean result = gestureDetector.onTouchEvent(event);
				return result;
			}
		});

		refresh();
	}

	protected abstract int startAt();

	protected void refresh() {
		String id = pictures.get(current).getID();
		PicturesDatabase db = PicturesDatabase.getInstance(this);

		if (db.hasPicture(id)) {
			String type = db.getPicture(id).getType();

			if (type != null) {
				if (type.equals(FaceRecognition.MY_PICTURE_TYPE)) {
					typeIcon.setVisibility(View.VISIBLE);
					typeIcon.setImageResource(R.drawable.green);
				} else if (type.equals(FaceRecognition.INTRUDER_PICTURE_TYPE)) {
					typeIcon.setVisibility(View.VISIBLE);
					typeIcon.setImageResource(R.drawable.red);
				} else {
					typeIcon.setVisibility(View.INVISIBLE);
				}
			}
		} else {
			typeIcon.setVisibility(View.INVISIBLE);
		}

		img.setImageBitmap(pictures.get(current).getImage());
		dots[current].setImageResource(R.drawable.azul);
	}

	protected abstract List<Picture> getPictures();

	protected int getCurrent() {
		return current;
	}

	protected void setCurrent(int current) {
		this.current = current;
	}

	protected Picture getCurrentPicture() {
		return pictures.get(current);
	}

	protected void setMessageVisibility(boolean visible) {
		msg.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
	}

	protected boolean isMessageVisible() {
		return msg.getVisibility() == View.VISIBLE;
	}

	protected abstract void onClickMessage();
}
