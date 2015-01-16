package hcim.auric.record.screen.event_based;

import hcim.auric.recognition.Picture;
import hcim.auric.utils.CalendarManager;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hcim.intrusiondetection.R;

@SuppressLint({ "ViewHolder", "InflateParams" })
public class EmptyActionTimelineAdapter extends BaseAdapter {
	static final String TAG = "AURIC";

	private Context context;
	private List<Picture> pictures;

	public EmptyActionTimelineAdapter(List<Picture> intruders, Context context) {
		this.context = context;
		this.pictures = intruders;

	}

	@Override
	public int getCount() {
		return pictures.size();
	}

	@Override
	public Object getItem(int position) {
		return pictures.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Picture p = pictures.get(position);
		String t = CalendarManager.getTime(Long.parseLong(p.getID()));

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		GridLayout view = new GridLayout(context);
		view = (GridLayout) inflater.inflate(R.layout.timeline_item, null);

		//ImageView icon = (ImageView) view.findViewById(R.id.icon_timeline);
		TextView text = (TextView) view.findViewById(R.id.text_timeline);
		TextView time = (TextView) view.findViewById(R.id.time_timeline);
		LinearLayout intruder = (LinearLayout) view
				.findViewById(R.id.intruders_layout);

		text.setText("nothing to show");
		time.setText(t);
		
		intruder.addView(getView(p.getImage()));

		return view;
	}

	private View getView(Bitmap b) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout view = new LinearLayout(context);
		view = (LinearLayout) inflater.inflate(R.layout.intruder_film, null);

		ImageView img = (ImageView) view.findViewById(R.id.intruder);
		img.setImageBitmap(b);

		return view;
	}
}
