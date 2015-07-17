package hcim.auric.activities.replay;

import hcim.auric.Intrusion;
import hcim.auric.Picture;
import hcim.auric.data.EventLogDatabase;
import hcim.auric.data.SettingsPreferences;
import hcim.auric.record.events.EventBasedLog;
import hcim.auric.record.events.EventBasedLogItem;
import hcim.auric.utils.CalendarManager;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
	private int cameraPeriod;
	private Context context;
	private EventLogDatabase logDB;
	private List<EventBasedLogItem> items;

	private int idxIntruder;

	public TimelineAdapter(Context context, List<Intrusion> intrusions) {
		this.context = context;
		this.logDB = EventLogDatabase.getInstance(context);
		cameraPeriod = new SettingsPreferences(context).getCameraPeriod();
		items = new ArrayList<EventBasedLogItem>();

		for (Intrusion i : intrusions) {
			if (i != null) {
				EventBasedLog log = logDB.get(i.getID(), context);
				List<Picture> intruders = i.getImages();
				int color = getColor(i);

				if (log.isListEmpty()) {
					items.add(new EventBasedLogItem(i.getTime(), intruders,
							color, i.getID()));
				} else {
					organize(intruders, log, color, i.getID());
				}
			}
		}
	}

	private int getColor(Intrusion i) {
		if (i.isFalseIntrusion()) {
			return R.color.lightgreen;
		}
		return R.color.lightred;
	}

	private void organize(List<Picture> intruders, EventBasedLog log,
			int color, String intrusionID) {
		EventBasedLogItem item;
		Picture pic;
		idxIntruder = 0;

		for (int position = 0; position < log.size(); position++) {
			item = log.getItem(position);
			item.setColorRes(color);
			item.setIntrusionID(intrusionID);

			if (position < log.size() - 1) {
				int imgNumber = distance(item, log.getItem(position + 1));

				for (int i = 0; i <= imgNumber; i++) {
					pic = nextIntruder(intruders);
					item.addPicture(pic);
				}
			} else {
				pic = nextIntruder(intruders);
				item.addPicture(pic);
			}
			items.add(item);
		}
	}

	// private EventBasedLogItem getEventItem(int position) {
	// int idx = position;
	// for (EventBasedLog log : logs) {
	// if (idx > log.size()) {
	// idx -= log.size();
	// } else {
	// return log.getItem(idx);
	// }
	// }
	// return null;
	// }

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	// public int getIntrusionIndex(int position) {
	// int idx = position;
	// for (int i = 0; i < logs.size(); i++) {
	// EventBasedLog log = logs.get(i);
	// if (idx > log.size()) {
	// idx -= log.size();
	// } else {
	// return i;
	// }
	// }
	// return -1;
	// }

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

		// add icon
		Drawable d = item.getIcon();
		if (d != null)
			icon.setImageDrawable(d);

		// add Pictures
		for (Picture p : item.getPictures()) {
			intruder.addView(getView(p.getImage()));
		}

		view.setBackgroundResource(item.getColorRes());
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

	private int distance(EventBasedLogItem previous, EventBasedLogItem next) {
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
		int cameraPeriodSec = (cameraPeriod * 2) / 1000;
		result /= cameraPeriodSec;

		return result;
	}

	public String getIntrusionID(int position) {
		return items.get(position).getIntrusionID();
	}
}
