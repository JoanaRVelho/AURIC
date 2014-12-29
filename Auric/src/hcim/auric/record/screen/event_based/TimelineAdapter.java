package hcim.auric.record.screen.event_based;

import hcim.auric.audit.AbstractAuditTask;
import hcim.auric.calendar.CalendarManager;
import hcim.auric.recognition.Picture;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
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
public class TimelineAdapter extends BaseAdapter {
	static final String TAG = "AURIC";

	private List<EventBasedLogItem> logList;
	private Context context;
	private int idxIntruder;

	public TimelineAdapter(List<EventBasedLogItem> list,
			List<Picture> intruders, Context context) {
		this.logList = list;
		this.context = context;
		organize(intruders);
	}

	private void organize(List<Picture> intruders) {
		Log.i(TAG, "intruders pics = " + intruders.size());
		
		EventBasedLogItem item;
		Picture pic;
		idxIntruder = 0;
		
		for (int position = 0; position < logList.size(); position++) {
			item = logList.get(position);
			
			if (position < logList.size() - 1) {
				int imgNumber = distance(item, logList.get(position + 1));

				for (int i = 0; i <= imgNumber; i++) {
					pic = nextIntruder(intruders);
					item.addPicture(pic);
				}
			} else {
				pic = nextIntruder(intruders);
				item.addPicture(pic);
			}
		}
	}

	@Override
	public int getCount() {
		return logList.size();
	}

	@Override
	public Object getItem(int position) {
		return logList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		EventBasedLogItem item = (EventBasedLogItem) getItem(position);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		GridLayout view = new GridLayout(context);
		view = (GridLayout) inflater.inflate(R.layout.timeline_item, null);

		ImageView icon = (ImageView) view.findViewById(R.id.icon_timeline);
		TextView text = (TextView) view.findViewById(R.id.text_timeline);
		TextView time = (TextView) view.findViewById(R.id.time_timeline);
		LinearLayout intruder = (LinearLayout) view
				.findViewById(R.id.intruders_layout);

		text.setText(item.getAppName());
		time.setText(item.getTime());

		//add icon
		Drawable d = item.getIcon();
		if (d != null)
			icon.setImageDrawable(d);
		else
			icon.setImageResource(R.drawable.android);
		
		//add Pictures
		for(Picture p : item.getPictures()){
			intruder.addView(getView(p.getImage()));
		}
		
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

	private Picture nextIntruder(List<Picture> intruders) {
		Picture result;

		if (idxIntruder >= intruders.size()) {
			result = intruders.get(intruders.size() - 1);
		} else {
			result = intruders.get(idxIntruder);
			idxIntruder++;
		}

		return result;
	}

	private static int distance(EventBasedLogItem previous,
			EventBasedLogItem next) {
		String[] thisSplit = next.getTime().split(
				CalendarManager.TIME_SEPARATOR);
		String[] previousSplit = previous.getTime().split(
				CalendarManager.TIME_SEPARATOR);

		int thisHours = Integer.valueOf(thisSplit[0]);
		int thisMin = Integer.valueOf(thisSplit[1]);
		int thisSec = Integer.valueOf(thisSplit[2]);

		int previousHours = Integer.valueOf(previousSplit[0]);
		int previousMin = Integer.valueOf(previousSplit[1]);
		int previousSec = Integer.valueOf(previousSplit[2]);

		int hours = (thisHours - previousHours) * 3600;
		int min = (thisMin - previousMin) * 60;
		int sec = (thisSec - previousSec);

		int result = hours + min + sec;
		int cameraPeriodSec = (AbstractAuditTask.CAMERA_PERIOD_MILIS * 2) / 1000;
		result /= cameraPeriodSec;

		return result;
	}
}
