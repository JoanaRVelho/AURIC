package hcim.auric.general_activities;

import hcim.auric.utils.CalendarManager;
import hcim.auric.utils.FileManager;
import hcim.auric.utils.LogUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;

import com.hcim.intrusiondetection.R;

public class QuestionActivity extends Activity {

	public static final String EXTRA_ID_1 = "extra1";
	public static final String EXTRA_ID_2 = "extra2";
	private String sessionID;
	private ArrayList<String> apps;
	private RadioGroup who, auth, worried;
	private Button done;
	private RelativeLayout central;

	private String whoAnswer, authAnswer, worriedAnswer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mini_quest);

		sessionID = getIntent().getExtras().getString(EXTRA_ID_1);
		LogUtils.debug("session=" + sessionID);
		apps = getIntent().getExtras().getStringArrayList(EXTRA_ID_2);

		initView();
	}

	@Override
	public void finish() {
		// don't
	}

	private void initView() {
		done = (Button) findViewById(R.id.done_q);
		central = (RelativeLayout) findViewById(R.id.central);

		who = (RadioGroup) findViewById(R.id.who);
		who.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.me) { // it's me
					done.setVisibility(View.VISIBLE);
					whoAnswer = "eu";
					setVisibility(false);
				} else if (checkedId == R.id.intruder) { // intruder
					setVisibility(true);
					whoAnswer = "intruder";

					if (whoAnswer != null && authAnswer != null
							&& worriedAnswer != null) {
						done.setVisibility(View.VISIBLE);
					} else {
						done.setVisibility(View.INVISIBLE);
					}
				}
			}
		});
		auth = (RadioGroup) findViewById(R.id.authorized_a);
		auth.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.yes_auth) {
					authAnswer = "yes";
				} else if (checkedId == R.id.no_auth) {
					authAnswer = "no";
				}

				if (whoAnswer != null && authAnswer != null
						&& worriedAnswer != null) {
					done.setVisibility(View.VISIBLE);
				} else {
					done.setVisibility(View.INVISIBLE);
				}
			}
		});
		worried = (RadioGroup) findViewById(R.id.worried_a);
		worried.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.yes_w) {
					worriedAnswer = "yes";
				} else if (checkedId == R.id.no_w) {
					worriedAnswer = "no";
				}
				if (whoAnswer != null && authAnswer != null
						&& worriedAnswer != null) {
					done.setVisibility(View.VISIBLE);
				} else {
					done.setVisibility(View.INVISIBLE);
				}
			}
		});
	}

	public void onClickDoneButton(View v) {
		FileManager manager = new FileManager(this);
		File f = new File(manager.getFieldStudyFile(sessionID));

		try {
			boolean b = f.createNewFile();
			LogUtils.debug("create new file : " + b);
		} catch (IOException e) {
			LogUtils.exception(e);
			return;
		}

		String data = getData();
		writeToFile(f, data);

		super.finish();
	}

	private String getData() {
		long timestamp = System.currentTimeMillis();
		String dateTimeView = CalendarManager.getDate(timestamp) + " "
				+ CalendarManager.getTime(timestamp);

		long sessionTimestamp = Long.parseLong(sessionID);
		String dateTimeSession = CalendarManager.getDate(sessionTimestamp)
				+ " " + CalendarManager.getTime(sessionTimestamp);

		StringBuilder builder = new StringBuilder();
		//builder.append("Session: ");
		builder.append(dateTimeSession);
		//builder.append("; View: ");
		builder.append(";");
		builder.append(dateTimeView);
		//builder.append("; Interval: ");
		builder.append(";");
		builder.append(timestamp - sessionTimestamp);
		//builder.append("; Apps: ");
		builder.append(";");
		for (int i = 0; i < apps.size() - 1; i++) {
			builder.append(apps.get(i));
			builder.append(",");
		}
		builder.append(apps.get(apps.size() - 1));
		//builder.append("; Who: ");
		builder.append(";");
		builder.append(whoAnswer);
		//builder.append("; Auth: ");
		builder.append(";");
		if (authAnswer == null)
			builder.append("-");
		else
			builder.append(authAnswer);
	//	builder.append("; Worried: ");
		builder.append(";");
		if (worriedAnswer == null)
			builder.append("-");
		else
			builder.append(worriedAnswer);

		return builder.toString();
	}

	private void writeToFile(File f, String data) {
		try {
			FileWriter writer = new FileWriter(f);
			writer.append(data);
			writer.close();
		} catch (IOException e) {
			LogUtils.exception(e);
		}
	}

	private void setVisibility(boolean visible) {
		central.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
	}

}
