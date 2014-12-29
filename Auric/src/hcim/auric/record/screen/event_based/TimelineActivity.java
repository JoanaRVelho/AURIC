package hcim.auric.record.screen.event_based;

import hcim.auric.database.EventBasedLogDatabase;
import hcim.auric.database.IntrusionsDatabase;
import hcim.auric.intrusion.Intrusion;
import hcim.auric.record.screen.SeverityAdapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.hcim.intrusiondetection.R;

@SuppressLint("InflateParams") public class TimelineActivity extends Activity {
	public static final String EXTRA_ID = "extra";
	static final String TAG = "AURIC";

	private Intrusion intrusion;
	private IntrusionsDatabase intDB;
	private EventBasedLogDatabase logDB;
	private TimelineAdapter adapter;
	private Spinner spinnerSeverity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.run_timeline);

		intDB = IntrusionsDatabase.getInstance(this);
		logDB = EventBasedLogDatabase.getInstance(this);

		String intrusionID = getIntent().getStringExtra(EXTRA_ID);
		intrusion = intDB.getIntrusion(intrusionID);

		EventBasedLog log = logDB.get(intrusionID, this);
		List<EventBasedLogItem> list = log.getList();

		adapter = new TimelineAdapter(list, intrusion.getImages(), this);

		GridView layout = (GridView) findViewById(R.id.timeline_layout);
		layout.setAdapter(adapter);
		layout.setEnabled(true);
		layout.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				EventBasedLogItem item = (EventBasedLogItem) adapter
						.getItem(position);
				ArrayList<String> listPic = item.getPicturesIDs();

				Intent i = new Intent(TimelineActivity.this, Details.class);
				i.putExtra(Details.EXTRA_ID_1, item.getId());
				i.putExtra(Details.EXTRA_ID_2, listPic);
				startActivity(i);
			}
		});

		ImageView trash = (ImageView) findViewById(R.id.trash);
		trash.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				trashButtonAlertDialog();
			}
		});
	}

	@Override
	public void finish() {
		if (intrusion.isChecked()) {
			super.finish();
		} else {
			markIntrusionAlertDialog();
		}
	}

	private View spinnerView() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout view = new LinearLayout(this);
		view = (LinearLayout) inflater.inflate(R.layout.severity, null);
		
		spinnerSeverity = (Spinner) view.findViewById(R.id.severity_spinner);
		spinnerSeverity.setAdapter(new SeverityAdapter(this));
		
		return view;
	}

	private void markIntrusionAlertDialog() {
		AlertDialog.Builder alertDialog;
		alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("Severity of the intrusion");
		alertDialog.setView(spinnerView());
		alertDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						intrusion.setTag((int)spinnerSeverity.getSelectedItemPosition());
						intDB.updateIntrusion(intrusion);

						TimelineActivity.super.finish();
					}
				});
		alertDialog.show();
	}

	private void trashButtonAlertDialog() {
		AlertDialog.Builder alertDialog;
		alertDialog = new AlertDialog.Builder(TimelineActivity.this);
		alertDialog.setTitle("Delete Intrusion Log");
		alertDialog
				.setMessage("Are you sure that you want to delete this intrusion log?");
		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						delete();
					}
				});
		alertDialog.setNegativeButton("NO", null);
		alertDialog.show();
	}

	private void delete() {
		String intrusionID = intrusion.getID();

		intDB.deleteIntrusion(intrusionID, false);
		logDB.delete(intrusionID);

		super.finish();
	}
}
