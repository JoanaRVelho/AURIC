package hcim.auric.record;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hcim.intrusiondetection.R;

@SuppressLint({ "ViewHolder", "InflateParams" }) public class SeverityAdapter extends BaseAdapter {
	private String[] items, subItems;
	private Context context;

	public SeverityAdapter(Context c) {
		this.context = c;
		this.items = c.getResources().getStringArray(R.array.severity_array);
		this.subItems = c.getResources().getStringArray(
				R.array.severity_desc_array);
	}

	@Override
	public int getCount() {
		return items.length;
	}

	@Override
	public Object getItem(int position) {
		return items[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		LinearLayout view = new LinearLayout(context);
		view = (LinearLayout) inflater.inflate(R.layout.severity_lay_item, null);
		
		TextView item = (TextView) view.findViewById(R.id.severity_item);
		TextView subitem = (TextView) view.findViewById(R.id.severity_subitem);
		
		item.setText(items[position]);
		subitem.setText(subItems[position]);
		
		return view;
	}
}