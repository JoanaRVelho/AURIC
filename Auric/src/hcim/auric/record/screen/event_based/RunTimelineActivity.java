package hcim.auric.record.screen.event_based;

import hcim.auric.activities.images.SlideShowIntrusionPictures;
import hcim.auric.database.intrusions.EventBasedLogDatabase;
import hcim.auric.database.intrusions.IntrusionsDatabase;
import hcim.auric.recognition.Picture;
import hcim.auric.record.screen.RunInteraction;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.hcim.intrusiondetection.R;

@SuppressLint("InflateParams")
public class RunTimelineActivity extends RunInteraction {
	public static final String EXTRA_ID = "extra";
	static final String TAG = "AURIC";

	private EventBasedLogDatabase logDB;
	private BaseAdapter adapter;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.run_timeline);

		intDB = IntrusionsDatabase.getInstance(this);
		logDB = EventBasedLogDatabase.getInstance(this);

		final String intrusionID = getIntent().getStringExtra(EXTRA_ID);
		intrusion = intDB.getIntrusion(intrusionID);

		EventBasedLog log = logDB.get(intrusionID, this);
		List<EventBasedLogItem> list = log.getList();
		List<Picture> pictures = intrusion.getImages();

		if (list.isEmpty())
			adapter = new EmptyActionTimelineAdapter(pictures, this);
		else
			adapter = new TimelineAdapter(list, pictures, this);

		GridView layout = (GridView) findViewById(R.id.timeline_layout);
		layout.setAdapter(adapter);
		layout.setEnabled(true);
		layout.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (adapter instanceof TimelineAdapter)
					clickEventLogItem(position);
				else {
					clickEntry(position);
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

	private void clickEventLogItem(int position) {
		EventBasedLogItem item = (EventBasedLogItem) adapter.getItem(position);
		ArrayList<String> listPic = item.getPicturesIDs();

		Intent i = new Intent(RunTimelineActivity.this, Details.class);
		i.putExtra(Details.EXTRA_ID_1, item.getId());
		i.putExtra(Details.EXTRA_ID_2, listPic);
		startActivity(i);
	}

	private void clickEntry(int position) {
		Intent i = new Intent(RunTimelineActivity.this,
				SlideShowIntrusionPictures.class);
		i.putExtra(SlideShowIntrusionPictures.EXTRA_ID, intrusion.getID());
		i.putExtra(SlideShowIntrusionPictures.EXTRA_ID_IDX, position);
		startActivity(i);
	}

	@Override
	protected void delete() {
		String intrusionID = intrusion.getID();

		intDB.deleteIntrusion(intrusionID, false);
		logDB.delete(intrusionID);

		//super.finish();
	}
}
