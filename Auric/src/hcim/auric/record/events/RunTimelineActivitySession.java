package hcim.auric.record.events;

import hcim.auric.activities.images.SlideShowIntrusionPictures;
import hcim.auric.database.intrusions.EventBasedLogDatabase;
import hcim.auric.database.intrusions.IntrusionsDatabase;
import hcim.auric.database.intrusions.SessionDatabase;
import hcim.auric.intrusion.Intrusion;
import hcim.auric.intrusion.Session;

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
public class RunTimelineActivitySession extends Activity {
	public static final String EXTRA_ID = "extra";
	static final String TAG = "AURIC";

	private EventBasedLogDatabase logDB;
	private TimelineAdapterSession adapter;
	private List<Intrusion> intrusions;
	private SessionDatabase sessionDB;
	private Session session;
	private IntrusionsDatabase intDB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.run_timeline);

		intDB = IntrusionsDatabase.getInstance(this);
		logDB = EventBasedLogDatabase.getInstance(this);
		sessionDB = SessionDatabase.getInstance(this);

		String sessionID = getIntent().getStringExtra(EXTRA_ID);
		session = sessionDB.getSession(sessionID);
		intrusions = session.getIntrusions(intDB);

		adapter = new TimelineAdapterSession(this, intrusions);

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

		Intent i = new Intent(this, Details.class);
		i.putExtra(Details.EXTRA_ID_1, item.getId());
		i.putExtra(Details.EXTRA_ID_2, listPic);
		startActivity(i);
	}

	private void clickEntry(int position) {
		String intrusionID = adapter.getIntrusionID(position);

		Intent i = new Intent(this, SlideShowIntrusionPictures.class);
		i.putExtra(SlideShowIntrusionPictures.EXTRA_ID, intrusionID);
		startActivity(i);
	}

	protected void delete() {
		for (Intrusion i : intrusions) {
			intDB.deleteIntrusion(i.getID(), false);
			logDB.delete(i.getID());
		}

		sessionDB.deleteSession(session.getID());
	}

	protected void trashButtonAlertDialog() {
		AlertDialog.Builder alertDialog;
		alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("Delete Intrusion's Log");
		alertDialog
				.setMessage("Are you sure that you want to delete this intrusion's log?");
		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						delete();
						RunTimelineActivitySession.super.finish();
					}

				});
		alertDialog.setNegativeButton("NO", null);
		alertDialog.show();
	}
}
