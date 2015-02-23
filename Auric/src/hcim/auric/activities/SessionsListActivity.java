package hcim.auric.activities;

import hcim.auric.database.configs.ConfigurationDatabase;
import hcim.auric.database.intrusions.SessionDatabase;
import hcim.auric.intrusion.Session;
import hcim.auric.record.RecorderManager;
import hcim.auric.record.events.RunTimelineActivitySession;
import hcim.auric.record.screencast.RunScreencast;

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

public class SessionsListActivity extends Activity {
	public static final String EXTRA_ID = "extra";

	private SessionDatabase sessionDB;
	private String date;
	private LinearLayout layout;
	private ProgressBar bar;

	private TextView t;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intrusions_list);

		sessionDB = SessionDatabase.getInstance(this);

		layout = (LinearLayout) findViewById(R.id.listlogs);
		bar = (ProgressBar) findViewById(R.id.progress_bar);

		Bundle extras = getIntent().getExtras();
		date = extras.getString(EXTRA_ID);

		t = (TextView) findViewById(R.id.textView1);
		t.setText(date + " Session");

		sessionDB.printAll();
	}

	@Override
	protected void onResume() {
		// remove all
		if (((LinearLayout) layout).getChildCount() > 0)
			((LinearLayout) layout).removeAllViews();

		List<Session> sessions = getSessions();

		if (sessions == null || sessions.size() == 0)
			finish();

		t.setText(date + " Session");
		if (sessions != null) {
			if (sessions.size() > 1)
				t.append("s");
		}

		addButtons(sessions);
		bar.setVisibility(View.GONE);

		sessionDB.printAll();
		super.onResume();
	}

	private List<Session> getSessions() {
		boolean showAll = ConfigurationDatabase.getInstance(this)
				.showAllSessions();

		if (showAll)
			return sessionDB.getAllSessionsFromADay(date);
		else
			return sessionDB.getIntrusionSessionsFromADay(date);
	}

	private void addButtons(final List<Session> sessions) {
		runOnUiThread(new Runnable() {
			@SuppressLint("InflateParams")
			@Override
			public void run() {
				for (Session s : sessions) {
					LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

					View view = new View(SessionsListActivity.this);
					view = inflater.inflate(R.layout.button_in_list, null);

					Button b = (Button) view.findViewById(R.id.the_button);
					b.setText("Session " + s.getTime());
					b.setOnClickListener(new SessionClickListener(s.getID()));
					layout.addView(view);
				}
			}
		});
	}

	class SessionClickListener implements OnClickListener {
		private String session;

		public SessionClickListener(String session) {
			this.session = session;
		}

		@Override
		public void onClick(View v) {
			bar.setVisibility(View.VISIBLE);

			String log = sessionDB.getLogType(session);
			if (log != null)
				runActivity(log);
		}

		private void runActivity(String log) {
			Intent intent;
			if (log.equals(RecorderManager.SCREENCAST_ROOT)) {
				intent = new Intent(SessionsListActivity.this,
						RunScreencast.class);
				intent.putExtra(RunScreencast.EXTRA_ID, session);
				startActivity(intent);
			}
			if (log.equals(RecorderManager.EVENT_BASED)) {
				startTimeline(session);
			}
		}

		private void startTimeline(String session) {
			Intent intent = new Intent(SessionsListActivity.this,
					RunTimelineActivitySession.class);
			intent.putExtra(RunTimelineActivitySession.EXTRA_ID, session);
			startActivity(intent);
		}
	}
}