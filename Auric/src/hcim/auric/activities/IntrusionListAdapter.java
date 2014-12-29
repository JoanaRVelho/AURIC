package hcim.auric.activities;

import hcim.auric.intrusion.Intrusion;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hcim.intrusiondetection.R;

@SuppressLint("ViewHolder")
public class IntrusionListAdapter extends BaseAdapter {

	private Context context;
	private List<Intrusion> list;

	public IntrusionListAdapter(List<Intrusion> list, Context context) {
		this.context = context;
		this.list = list;

	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		RelativeLayout view = new RelativeLayout(context);
		view = (RelativeLayout) inflater.inflate(R.layout.intrusion_list_item,
				 parent, false);

		ImageView icon = (ImageView) view.findViewById(R.id.back);
		TextView text = (TextView) view.findViewById(R.id.intrusion_txt);

		Intrusion i = list.get(position);

		if (!i.isChecked()) {
			icon.setBackgroundResource(R.drawable.mark_new);
		}

		text.setText("Intrusion " + i.getTime());

		return text;
	}

}
