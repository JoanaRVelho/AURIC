package hcim.auric.activities;

import hcim.auric.database.intrusions.SessionDatabase;
import hcim.auric.intrusion.Session;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

		Log.d("AURIC", date + " Session");

		sessionDB.printAll();

	}

	@Override
	protected void onResume() {
		// remove all
		if (((LinearLayout) layout).getChildCount() > 0)
			((LinearLayout) layout).removeAllViews();

		List<Session> intrusions = sessionDB.getSessionsFromADay(date);

		if (intrusions == null || intrusions.size() == 0)
			finish();

		if (intrusions != null) {
			if (intrusions.size() > 1)
				t.append("s");
		}

		addButtons(intrusions);
		bar.setVisibility(View.GONE);

		sessionDB.printAll();
		super.onResume();
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

	private class SessionClickListener implements OnClickListener {
		private String sessionID;

		public SessionClickListener(String sessionID) {
			this.sessionID = sessionID;

		}

		@Override
		public void onClick(View v) {
			bar.setVisibility(View.VISIBLE);

			Intent intent = new Intent(SessionsListActivity.this,
					SessionIntrusionsList.class);
			intent.putExtra(SessionIntrusionsList.EXTRA_ID, sessionID);
			startActivity(intent);
		}
	}
}