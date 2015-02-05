package hcim.auric.activities.images;

import hcim.auric.recognition.Picture;
import hcim.auric.utils.OnSwipeTouchListener;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hcim.intrusiondetection.R;

public abstract class SlideShowActivity extends Activity {

	protected int current;
	protected List<Picture> pictures;

	private ImageView[] dots;
	private ImageView img;
	protected ImageView typeIcon;
	protected TextView msg;
	protected LinearLayout lin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.slideshow);

		pictures = getPictures();
		current = startAt();

		// init txt
		msg = (TextView) findViewById(R.id.double_tap_msg);
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
					current = pictures.size() - 1;
				else
					current = (current - 1) % pictures.size();

				refresh();
			}

			@Override
			public void onSwipeLeft() {
				dots[current].setImageResource(R.drawable.cinzento);
				current = (current + 1) % pictures.size();
				refresh();
			}

			public boolean onTouch(View v, MotionEvent event) {
				boolean result = gestureDetector.onTouchEvent(event);
				return result;
			}
		});

		initDots();
		refresh();
	}

	protected void setMessageVisibility(boolean b) {
		msg.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
	}
	
	protected void setMessage(String t){
		msg.setText(t);
	}

	protected void initDots() {
		// init dots
		lin = (LinearLayout) findViewById(R.id.dots);
		dots = new ImageView[pictures.size()];
		for (int i = 0; i < dots.length; i++) {
			dots[i] = new ImageView(this);
			dots[i].setImageResource(R.drawable.cinzento);
			lin.addView(dots[i]);
		}
	}
	
	protected void refresh() {
//		String id = pictures.get(current).getID();
//		PicturesDatabase db = PicturesDatabase.getInstance(this);
//
//		if (db.hasPicture(id)) {
//			String type = db.getPicture(id).getType();
//
//			if (type != null) {
//				if (type.equals(FaceRecognition.MY_PICTURE_TYPE)) {
//					typeIcon.setVisibility(View.VISIBLE);
//					typeIcon.setImageResource(R.drawable.green);
//				} else if (type.equals(FaceRecognition.INTRUDER_PICTURE_TYPE)) {
//					typeIcon.setVisibility(View.VISIBLE);
//					typeIcon.setImageResource(R.drawable.red);
//				} else {
//					typeIcon.setVisibility(View.INVISIBLE);
//				}
//			}
//		} else {
//			typeIcon.setVisibility(View.INVISIBLE);
//		}

		img.setImageBitmap(pictures.get(current).getImage());
		dots[current].setImageResource(R.drawable.azul);
	}

	protected Picture getCurrentPicture() {
		return pictures.get(current);
	}

//	protected void setMessageVisibility(boolean visible) {
//		msg.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
//	}
//
//	protected boolean isMessageVisible() {
//		return msg.getVisibility() == View.VISIBLE;
//	}

	protected abstract int startAt();

	protected abstract List<Picture> getPictures();

//	protected abstract void onClickMessage();
}
