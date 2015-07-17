package hcim.auric.activities.replay;

import hcim.auric.Intrusion;
import hcim.auric.Session;
import hcim.auric.activities.images.SlideShowIntrusionPictures;
import hcim.auric.data.SessionDatabase;
import hcim.auric.record.events.EventBasedLogItem;
import hcim.auric.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;

import com.hcim.intrusiondetection.R;

@SuppressLint("InflateParams")
public class TimelineActivity extends Activity {
	public static final String EXTRA_ID = "extra";

	private TimelineAdapter adapter;
	private List<Intrusion> intrusions;
	private SessionDatabase sessionDB;
	private Session session;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.run_timeline);

		sessionDB = SessionDatabase.getInstance(this);

		String sessionID = getIntent().getStringExtra(EXTRA_ID);
		session = sessionDB.getSession(sessionID);
		intrusions = sessionDB.getIntrusionsFromSession(sessionID);

		LogUtils.debug(session.toString());
		adapter = new TimelineAdapter(this, intrusions);

		GridView layout = (GridView) findViewById(R.id.timeline_layout);
		layout.setAdapter(adapter);
		layout.setEnabled(true);
		layout.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				EventBasedLogItem item = (EventBasedLogItem) adapter
						.getItem(position);

				if (item.nothingToShow()) {
					clickEntry(position);
				} else {
					clickEventLogItem(item);
				}
			}
		});
		Button trash = (Button) findViewById(R.id.trash);
		trash.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				trashButtonAlertDialog();
			}
		});
	}

	private void clickEventLogItem(EventBasedLogItem item) {
		ArrayList<String> listPic = item.getPicturesIDs();

		Intent i = new Intent(this, AppDetails.class);
		i.putExtra(AppDetails.EXTRA_ID_1, item.getId());
		i.putExtra(AppDetails.EXTRA_ID_2, listPic);
		startActivity(i);
	}

	private void clickEntry(int position) {
		String intrusionID = adapter.getIntrusionID(position);

		Intent i = new Intent(this, SlideShowIntrusionPictures.class);
		i.putExtra(SlideShowIntrusionPictures.EXTRA_ID, intrusionID);
		startActivity(i);
	}

	protected void delete() {
		sessionDB.deleteSession(session.getID());
	}

	protected void trashButtonAlertDialog() {
		AlertDialog.Builder alertDialog;
		alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("Delete Intrusion's Log");
		alertDialog
				.setMessage("Are you sure that you want to delete this intrusion's log?");
		alertDialog.setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						delete();
						TimelineActivity.super.finish();
					}

				});
		alertDialog.setNegativeButton("No", null);
		alertDialog.show();
	}
}
