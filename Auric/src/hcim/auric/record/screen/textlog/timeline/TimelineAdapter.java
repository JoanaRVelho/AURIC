package hcim.auric.record.screen.textlog.timeline;

import hcim.auric.audit.AbstractAuditTask;
import hcim.auric.calendar.CalendarManager;
import hcim.auric.recognition.Picture;
import hcim.auric.record.screen.textlog.TextualLogItem;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.hcim.intrusiondetection.R;

@SuppressLint({ "ViewHolder", "InflateParams" })
public class TimelineAdapter extends BaseAdapter {

	private List<TextualLogItem> logList;
	private List<Picture> intruders;
	private Context context;
	private int rep;

	public TimelineAdapter(List<TextualLogItem> list, List<Picture> intruders,
			Context context) {
		this.logList = ajdust(list);
		this.intruders = intruders;
		this.context = context;
		this.rep = computeRepValue();
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
		TextualLogItem item = (TextualLogItem) getItem(position);

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		GridLayout view = new GridLayout(context);
		view = (GridLayout) inflater.inflate(R.layout.timeline_item, null);

		ImageView icon = (ImageView) view.findViewById(R.id.icon);
		TextView text = (TextView) view.findViewById(R.id.text);
		TextView time = (TextView) view.findViewById(R.id.time);
		ImageView intruder = (ImageView) view.findViewById(R.id.intruder);

		if (item.getAppName() != null) {
			text.setText(item.getAppName());
			time.setText(item.getTime());

			Drawable d = item.getIcon();
			if (d != null)
				icon.setImageDrawable(d);
			else
				icon.setImageResource(R.drawable.android);
		}

		int intruderIdx = getIntruderIdx(position);
		intruder.setImageBitmap(intruders.get(intruderIdx).getImage());

		// view.setEnabled(false);
		return view;
	}

	private int computeRepValue() {
		int sizeLogs = logList.size();
		int intrudersPhotos = intruders.size();

		if (intrudersPhotos >= sizeLogs)
			return -1;

		else {
			int div = sizeLogs / intrudersPhotos;

			if ((sizeLogs % intrudersPhotos) != 0)
				div++;

			return div;
		}
	}

	private int getIntruderIdx(int position) {
		if (rep == -1)
			return position;

		return position / rep;
	}

	private static int distance(TextualLogItem previous, TextualLogItem next) {
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
		int cameraPeriodSec = AbstractAuditTask.CAMERA_PERIOD_MILIS / 1000;
		result /= cameraPeriodSec;

		return result;
	}

	private static void addDummies(int number, List<TextualLogItem> result) {
		for (int i = 0; i < number; i++) {
			result.add(new TextualLogItem());
		}
	}

	private static List<TextualLogItem> ajdust(List<TextualLogItem> list) {
		List<TextualLogItem> result = new ArrayList<TextualLogItem>();
		TextualLogItem current, next = null;
		int last = list.size()-1;
		int d;
		for (int i = 0; i <= last; i++) {
			current = list.get(i);
			result.add(current);

			if (i < last) {
				next = list.get(i + 1);
				d = distance(current, next);
				addDummies(d, result);
			}
		}

		return result;
	}

}
