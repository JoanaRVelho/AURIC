package hcim.auric.activities;

import hcim.auric.database.configs.ConfigurationDatabase;
import hcim.auric.database.intrusions.IntrusionsDatabase;
import hcim.auric.intrusion.Intrusion;
import hcim.auric.record.screen.event_based.RunTimelineActivity;
import hcim.auric.record.screen.screencast_root.RunScreencast;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hcim.intrusiondetection.R;

public class IntrusionsListActivity extends Activity {
	public static final String EXTRA_ID = "extra";

	private IntrusionsDatabase intrusionsDB;
	private String date;
	private LinearLayout layout;
	private ProgressBar bar;

	private TextView t;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intrusions_list);

		intrusionsDB = IntrusionsDatabase.getInstance(this);

		layout = (LinearLayout) findViewById(R.id.listlogs);
		bar = (ProgressBar) findViewById(R.id.progress_bar);

		Bundle extras = getIntent().getExtras();
		date = extras.getString(EXTRA_ID);

		t = (TextView) findViewById(R.id.textView1);
		t.setText(date + " Intrusion");
	}

	@Override
	protected void onResume() {
		// remove all
		if (((LinearLayout) layout).getChildCount() > 0)
			((LinearLayout) layout).removeAllViews();

		List<Intrusion> intrusions = intrusionsDB
				.getIntrusionsDataFromADay(date);

		if (intrusions == null || intrusions.size() == 0)
			finish();

		if (intrusions != null) {
			if (intrusions.size() > 1)
				t.append("s");
		}

		addButtons(intrusions);
		bar.setVisibility(View.GONE);

		super.onResume();
	}

	private void addButtons(final List<Intrusion> intrusions) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				for (Intrusion i : intrusions) {
					Button b = new Button(IntrusionsListActivity.this);
					b.setText("Intrusion " + i.getTime());
					b.setTextColor(Color.WHITE);

					switch (i.getTag()) {
					case Intrusion.UNCHECKED:
						b.setBackgroundResource(R.drawable.mark_new);
						break;
					default:
						b.setBackgroundResource(R.drawable.mark_false);
						break;
					}

					layout.addView(b);
					b.setOnClickListener(new IntrusionClickListener(i.getID()));
				}
			}
		});
	}

	private void startTimeline(String intrusion) {
		Intent intent = new Intent(IntrusionsListActivity.this,
				RunTimelineActivity.class);
		intent.putExtra(RunTimelineActivity.EXTRA_ID, intrusion);
		startActivity(intent);
	}

	class IntrusionClickListener implements OnClickListener {
		private String intrusion;

		public IntrusionClickListener(String intrusion) {
			this.intrusion = intrusion;
		}

		@Override
		public void onClick(View v) {
			bar.setVisibility(View.VISIBLE);
			Intrusion i = intrusionsDB.getIntrusion(intrusion);
			runActivity(i);
		}

		private void runActivity(Intrusion i) {
			String log = i.getLogType();

			if (log != null) {
				Intent intent;
				if (log.equals(ConfigurationDatabase.SCREENCAST_ROOT_LOG)) {
					intent = new Intent(IntrusionsListActivity.this,
							RunScreencast.class);
					intent.putExtra(RunScreencast.EXTRA_ID, intrusion);
					startActivity(intent);
				}
				if (log.equals(ConfigurationDatabase.EVENT_LOG)) {
					startTimeline(intrusion);
				}
			}
		}
	}
}