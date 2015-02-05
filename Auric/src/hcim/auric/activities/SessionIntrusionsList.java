package hcim.auric.activities;

import hcim.auric.database.configs.ConfigurationDatabase;
import hcim.auric.database.intrusions.IntrusionsDatabase;
import hcim.auric.database.intrusions.SessionDatabase;
import hcim.auric.intrusion.Interaction;
import hcim.auric.intrusion.Intrusion;
import hcim.auric.intrusion.Session;
import hcim.auric.record.screen.event_based.RunTimelineActivity;
import hcim.auric.record.screen.screencast_root.RunScreencast;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.hcim.intrusiondetection.R;

public class SessionIntrusionsList extends Activity {
	public static final String EXTRA_ID = "extra";

	private IntrusionsDatabase intrusionsDB;
	private SessionDatabase sessionDB;
	private String sessionID;
	private LinearLayout layout;
	private ProgressBar bar;

	private TextView t;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intrusions_list);

		sessionDB = SessionDatabase.getInstance(this);
		intrusionsDB = IntrusionsDatabase.getInstance(this);

		layout = (LinearLayout) findViewById(R.id.listlogs);
		bar = (ProgressBar) findViewById(R.id.progress_bar);
		bar.setVisibility(View.INVISIBLE);

		Bundle extras = getIntent().getExtras();
		sessionID = extras.getString(EXTRA_ID);

		Session session = sessionDB.getSession(sessionID);
		t = (TextView) findViewById(R.id.textView1);
		t.setText(session.getTime() + " Session");

		List<Intrusion> intrusions = session.getInteractions(intrusionsDB);

		addButtons(intrusions);
	}

	@Override
	protected void onResume() {
		bar.setVisibility(View.GONE);

		super.onResume();
	}

	private void addButtons(final List<Intrusion> intrusions) {
		runOnUiThread(new Runnable() {
			@SuppressLint("InflateParams")
			@Override
			public void run() {
				for (Intrusion i : intrusions) {
					LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					View view = new View(SessionIntrusionsList.this);
					view = inflater.inflate(R.layout.button_in_list, null);
					
					Button b = (Button) view.findViewById(R.id.the_button);

					if (i instanceof Interaction)
						b.setText("Interaction " + i.getTime());
					else
						b.setText("Intrusion " + i.getTime());

					b.setOnClickListener(new IntrusionClickListener(i.getID()));
					layout.addView(view);

					// Button b = new Button(SessionIntrusionsList.this);
					// if (i instanceof Interaction)
					// b.setText("Interaction " + i.getTime());
					// else
					// b.setText("Intrusion " + i.getTime());
					// b.setTextColor(Color.WHITE);
					// b.setBackgroundResource(R.drawable.button_design);
					//
					// layout.addView(b);
					// b.setOnClickListener(new
					// IntrusionClickListener(i.getID()));
				}
			}
		});
	}

	private void startTimeline(String intrusion) {
		Intent intent = new Intent(SessionIntrusionsList.this,
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
					intent = new Intent(SessionIntrusionsList.this,
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
