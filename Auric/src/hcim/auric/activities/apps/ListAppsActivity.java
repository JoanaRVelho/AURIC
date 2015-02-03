package hcim.auric.activities.apps;

import hcim.auric.database.TargetAppDatabase;

import java.util.List;

import com.hcim.intrusiondetection.R;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class ListAppsActivity extends Activity {

	private static final int SELECTED_RES = R.color.sky;
	private static final int NOT_SELECTED_RES = R.color.lightgray;

	private ListView listView;
	private List<ApplicationData> list;
	private TargetAppDatabase db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_apps);

		db = TargetAppDatabase.getInstance(this);

		list = ApplicationsManager.getInstalledAppsSortedByName(getPackageManager());

		for (ApplicationData app : list) {
			if (db.hasApplication(app.getPackageName())) {
				app.setSelected(true);
			} else {
				app.setSelected(false);
			}
		}

		listView = (ListView) findViewById(R.id.list_apps);
		listView.setAdapter(new ApplicationsAdapter(this, list));
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ApplicationData app = list.get(position);
				boolean selected = app.isSelected();
				app.setSelected(!selected);

				if (app.isSelected()) {
					setSelectedItemView(view);
				} else {
					setNotSelectedItemView(view);
				}
			}
		});
	}

	protected static void setNotSelectedItemView(View view) {
		view.setBackgroundResource(NOT_SELECTED_RES);
		TextView txt = (TextView) view.findViewById(R.id.app_name);
		txt.setTextColor(Color.BLACK);
		txt = (TextView) view.findViewById(R.id.app_package);
		txt.setTextColor(Color.BLACK);
	}

	protected static void setSelectedItemView(View view) {
		view.setBackgroundResource(SELECTED_RES);
		TextView txt = (TextView) view.findViewById(R.id.app_name);
		txt.setTextColor(Color.WHITE);
		txt = (TextView) view.findViewById(R.id.app_package);
		txt.setTextColor(Color.WHITE);
	}

	@Override
	public void finish() {
		updateTargetApps();
		super.finish();
	}

	private void updateTargetApps() {
		for (ApplicationData app : list) {
			if (app.isSelected()) {
				if (!db.hasApplication(app.getPackageName())) {
					db.insertApplication(app);
				}
			}else{
				if (db.hasApplication(app.getPackageName())) {
					db.removeApplication(app);
				}
			}
		}

	}
}
