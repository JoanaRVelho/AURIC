package hcim.auric.record.events;

import hcim.auric.database.intrusions.EventBasedLogDatabase;
import hcim.auric.database.intrusions.SessionDatabase;
import hcim.auric.general_activities.images.SlideShowSetOfPictures;
import hcim.auric.recognition.Picture;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hcim.intrusiondetection.R;

@SuppressLint("InflateParams")
public class Details extends Activity {
	public static String EXTRA_ID_1 = "extra1";
	public static String EXTRA_ID_2 = "extra2";
	
	private ArrayList<String> pics;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details_view);

		Bundle bundle = getIntent().getExtras();
		int id = bundle.getInt(EXTRA_ID_1);
		pics = bundle.getStringArrayList(EXTRA_ID_2);

		EventBasedLogDatabase db = EventBasedLogDatabase.getInstance(this);
		EventBasedLogItem item = db.get(id, this);

		if (item == null) {
			finish();
		} else {
			TextView details = (TextView) findViewById(R.id.app_details);
			TextView time = (TextView) findViewById(R.id.app_time);
			TextView appName = (TextView) findViewById(R.id.app_name);
			ImageView icon = (ImageView) findViewById(R.id.app_icon);
			LinearLayout intruder = (LinearLayout) findViewById(R.id.app_intruders_layout);

			details.setText(item.detailsToString());
			time.setText(item.getTime());
			appName.setText(item.getAppName());
			icon.setImageDrawable(item.getIcon());

			addPictures(pics, intruder);
		}
	}

	private void addPictures(List<String> pics, LinearLayout intruder) {
		SessionDatabase db = SessionDatabase.getInstance(this);
		Picture p;
		int i = 0; 
		for (String id : pics) {
			p = db.getPictureOfTheIntruder(id);
			intruder.addView(getView(p, i));
			i++;
		}
	}

	private View getView(Picture pic, int idx) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout view = new LinearLayout(this);
		view = (LinearLayout) inflater.inflate(R.layout.intruder_film, null);

		ImageView img = (ImageView) view.findViewById(R.id.intruder);
		img.setImageBitmap(pic.getImage());
		img.setOnClickListener(new PictureOnClickListner(idx));
		return view;
	}

	class PictureOnClickListner implements OnClickListener {
		private int idx;

		public PictureOnClickListner(int idx ) {
			this.idx = idx;
		}

		@Override
		public void onClick(View v) {
			Intent i = new Intent(Details.this, SlideShowSetOfPictures.class);
			i.putExtra(SlideShowSetOfPictures.EXTRA_ID_IDX, idx);
			i.putExtra(SlideShowSetOfPictures.EXTRA_ID_LIST, pics);
			startActivity(i);
		}

	}
}
