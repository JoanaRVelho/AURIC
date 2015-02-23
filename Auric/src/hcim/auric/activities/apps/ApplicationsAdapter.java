package hcim.auric.activities.apps;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.hcim.intrusiondetection.R;

@SuppressLint({ "ViewHolder", "InflateParams" })
public class ApplicationsAdapter implements ListAdapter {

	private List<ApplicationData> list;
	private Context context;
	private PackageManager packageManager;

	public ApplicationsAdapter(Context context, List<ApplicationData> list) {
		this.context = context;
		this.packageManager = context.getPackageManager();
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int location) {
		return list.get(location);
	}

	@Override
	public long getItemId(int location) {
		return location;
	}

	@Override
	public int getItemViewType(int location) {
		return location;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View view = new View(context);
		view = inflater.inflate(R.layout.list_apps_item, null);

		ImageView icon = (ImageView) view.findViewById(R.id.app_icon);
		TextView name = (TextView) view.findViewById(R.id.app_name);
		TextView packageName = (TextView) view.findViewById(R.id.app_package);

		ApplicationData app = list.get(position);

		try {
			icon.setImageDrawable(packageManager.getApplicationIcon(app
					.getPackageName()));
		} catch (NameNotFoundException e) {
		}
		name.setText(app.getName());
		packageName.setText(app.getPackageName());

		if (app.isTarget()) {
			ListAppsActivity.setSelectedItemView(view);
		}

		return view;
	}

	@Override
	public int getViewTypeCount() {
		return list.size();
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
	}

	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}

	@Override
	public boolean isEnabled(int arg0) {
		return true;
	}
}
