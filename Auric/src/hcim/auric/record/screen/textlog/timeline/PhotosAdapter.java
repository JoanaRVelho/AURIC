package hcim.auric.record.screen.textlog.timeline;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.hcim.intrusiondetection.R;

@SuppressLint({ "ViewHolder", "InflateParams" })
public class PhotosAdapter extends BaseAdapter {

	private List<Bitmap> list;
	private Context context;

	public PhotosAdapter(List<Bitmap> list, Context context) {
		this.list = list;
		this.context = context;
		adjust();
	}

	private void adjust() {
		//
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View view = new View(context);

		view = inflater.inflate(R.layout.timeline_item, null);

		ImageView icon = (ImageView) view.findViewById(R.id.icon);
		icon.setImageBitmap(list.get(position));

		view.setEnabled(false);
		return view;
	}
}
