package hcim.auric.activities;

import hcim.auric.Session;
import hcim.auric.activities.replay.Screencast;
import hcim.auric.activities.replay.TimelineActivity;
import hcim.auric.data.SessionDatabase;
import hcim.auric.data.SettingsPreferences;
import hcim.auric.record.RecorderManager;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hcim.intrusiondetection.R;

/**
 * 
 * @author Joana Velho
 * 
 */
public class SessionsList extends Activity {
	public static final String EXTRA_ID = "extra";

	private SessionDatabase sessionDB;
	private String date;
	private LinearLayout layout;
	private ProgressBar bar;
	private List<Session> sessions;

	private TextView title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intrusions_list);

		sessionDB = SessionDatabase.getInstance(this);

		layout = (LinearLayout) findViewById(R.id.listlogs);
		bar = (ProgressBar) findViewById(R.id.progress_bar);

		Bundle extras = getIntent().getExtras();
		date = extras.getString(EXTRA_ID);

		title = (TextView) findViewById(R.id.textView1);
		title.setText(date + " Session");
	}

	@Override
	protected void onResume() {
		// remove all
		if (((LinearLayout) layout).getChildCount() > 0)
			((LinearLayout) layout).removeAllViews();

		boolean showOnlyIntrusions = new SettingsPreferences(this)
				.showOnlyIntrusionSessions();

		sessions = showOnlyIntrusions ? sessionDB
				.getIntrusionSessionsFromADay(date) : sessionDB
				.getSessionsFromADay(date);

		if (sessions == null || sessions.size() == 0)
			finish();

		title.setText(date + " Session");
		if (sessions != null) {
			if (sessions.size() > 1)
				title.append("s");
		}

		addButtons(sessions);
		bar.setVisibility(View.GONE);

		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.session_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.delete_all) {
			deleteAllAlertDialog();

			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void deleteAllAlertDialog() {
		AlertDialog.Builder alertDialog;
		alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("Delete Intrusion's Log");
		alertDialog
				.setMessage("Are you sure that you want to delete all sessions of "
						+ date + "?");
		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						deleteAll();
						SessionsList.super.finish();
						Toast.makeText(SessionsList.this,
								title.getText().toString() + " deleted",
								Toast.LENGTH_LONG).show();
					}

				});
		alertDialog.setNegativeButton("NO", null);
		alertDialog.show();
	}

	private void deleteAll() {
		for (Session s : sessions) {
			sessionDB.deleteSession(s.getID());
		}
	}

	private void addButtons(final List<Session> sessions) {
		runOnUiThread(new Runnable() {
			@SuppressLint("InflateParams")
			@Override
			public void run() {
				for (int i = sessions.size() - 1; i >= 0; i--) {
					Session s = sessions.get(i);
					LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

					View view = new View(SessionsList.this);
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

			String log = sessionDB.getRecorderType(session);

			if (log != null)
				runActivity(log);
			else
				bar.setVisibility(View.GONE);
		}

		private void runActivity(String log) {
			Intent intent;
			if (log.equals(RecorderManager.SCREENCAST_ROOT)) {
				intent = new Intent(SessionsList.this,
						Screencast.class);
				intent.putExtra(Screencast.EXTRA_ID, session);
				startActivity(intent);
			}
			if (log.equals(RecorderManager.EVENT_BASED)) {
				intent = new Intent(SessionsList.this,
						TimelineActivity.class);
				intent.putExtra(TimelineActivity.EXTRA_ID, session);
				startActivity(intent);
			}
		}
	}
}