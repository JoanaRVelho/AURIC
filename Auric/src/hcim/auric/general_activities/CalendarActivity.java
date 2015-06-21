package hcim.auric.general_activities;

import hcim.auric.database.SettingsPreferences;
import hcim.auric.database.configs.PicturesDatabase;
import hcim.auric.database.intrusions.EventBasedLogDatabase;
import hcim.auric.database.intrusions.SessionDatabase;
import hcim.auric.general_activities.passcode.Unlock;
import hcim.auric.general_activities.settings.SettingsActivity;
import hcim.auric.recognition.FaceRecognition;
import hcim.auric.utils.CalendarManager;
import hcim.auric.utils.FileManager;
import hcim.auric.utils.LogUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.opencv.android.OpenCVLoader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hcim.intrusiondetection.R;

/**
 * 
 * CustomCalendarAndroid - https://github.com/manishsri01/CustomCalendarAndroid
 */
public class CalendarActivity extends Activity implements OnClickListener {
	private static final int SETTINGS = 1;
	private static final int LIST = 2;
	static final int WELCOME_CODE = 10;
	static final int UNLOCK_CODE = 20;

	private static final String RESEARCHER_PASSCODE = "auric15";

	private TextView currentMonth;
	private ImageView prevMonth;
	private ImageView nextMonth;
	private GridView calendarView;
	private GridCellAdapter adapter;
	private Calendar myCalendar;
	private int month, year;

	private String dayClicked;

	private SettingsPreferences settings;
	private SessionDatabase sessionsDB;

	private CheckBox showOnlyIntCheckBox;
	private boolean showOnlyIntrusions;
	private Context context;
	private EditText researcherPasscode;
	private FaceRecognition fr;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar);

		context = this;
		initDatabase();

		if (settings.hasPasscode()) {
			startUnlockActivity();
		}

		initView();

		appOpen();
	}

	private void appOpen() {
		long sessionTimestamp = System.currentTimeMillis();
		String dateTime = CalendarManager.getDate(sessionTimestamp) + " "
				+ CalendarManager.getTime(sessionTimestamp);

		FileManager manager = new FileManager(this);
		File open = new File(manager.getOpenAppFile());

		if (!open.exists()) {
			try {
				boolean b = open.createNewFile();
				LogUtils.debug("open app create: " + b);
			} catch (IOException e) {
				LogUtils.exception(e);
			}
		}
		try {
			FileWriter writer = new FileWriter(open, true);
			writer.append(dateTime + "\n");
			writer.close();
		} catch (IOException e) {
			LogUtils.exception(e);
		}
	}

	private void initDatabase() {
		settings = new SettingsPreferences(this);
		sessionsDB = SessionDatabase.getInstance(this);
		fr = FaceRecognition.getInstance(this);
		PicturesDatabase.getInstance(this);
		EventBasedLogDatabase.getInstance(this);
	}

	private boolean firstLaunch() {
		return !settings.hasPreviouslyStarted();
	}

	private void startWelcome() {
		Intent i = new Intent(this, Welcome.class);
		startActivityForResult(i, WELCOME_CODE);
	}

	private void startUnlockActivity() {
		Intent i = new Intent(this, Unlock.class);
		startActivityForResult(i, UNLOCK_CODE);
	}

	@Override
	protected void onResume() {
		adapter = new GridCellAdapter(month, year);
		adapter.notifyDataSetChanged();
		calendarView.setAdapter(adapter);

		super.onResume();

		if (OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this,
				fr.getLoaderCallback())) {
			if (firstLaunch()) {
				startWelcome();
			}
		} else {
			LogUtils.error("Face Recognition - Cannot connect to OpenCV Manager");
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == UNLOCK_CODE && resultCode == RESULT_OK) {
			boolean b = data.getBooleanExtra(Unlock.EXTRA_ID, false);

			if (!b)
				finish();
		}

		if (requestCode == WELCOME_CODE) {
			if (!settings.hasPreviouslyStarted()) {
				super.finish();
			}
		}
	}

	private void initView() {
		showOnlyIntrusions = settings.showOnlyIntrusionSessions();
		showOnlyIntCheckBox = (CheckBox) findViewById(R.id.show_only_int);
		showOnlyIntCheckBox.setChecked(showOnlyIntrusions);
		showOnlyIntCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						showOnlyIntrusions = isChecked;
						settings.setShowOnlyIntrusionSessions(isChecked);
						runOnUiThread(new Runnable() {
							public void run() {
								adapter = new GridCellAdapter(month, year);
								adapter.notifyDataSetChanged();
								calendarView.setAdapter(adapter);
							}
						});

					}

				});

		Button config = (Button) findViewById(R.id.button1);
		config.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// launchResearcherPasscodeDialog(SETTINGS);
				startSettingsActivity();
			}
		});

		initDaysOfTheWeekLayout();

		myCalendar = Calendar.getInstance(Locale.getDefault());
		month = myCalendar.get(Calendar.MONTH) + 1;
		year = myCalendar.get(Calendar.YEAR);

		prevMonth = (ImageView) this.findViewById(R.id.prevMonth);
		prevMonth.setOnClickListener(this);

		currentMonth = (TextView) this.findViewById(R.id.currentMonth);
		currentMonth.setText(CalendarManager.monthYearString(month, year));

		nextMonth = (ImageView) this.findViewById(R.id.nextMonth);
		nextMonth.setOnClickListener(this);

		calendarView = (GridView) this.findViewById(R.id.calendar);

		adapter = new GridCellAdapter(month, year);
		adapter.notifyDataSetChanged();
		calendarView.setAdapter(adapter);
	}

	private void initDaysOfTheWeekLayout() {
		GridView week = (GridView) findViewById(R.id.days_week);
		week.setAdapter(new BaseAdapter() {
			String[] week = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView t = new TextView(CalendarActivity.this);
				t.setText(week[position]);
				t.setTextColor(Color.WHITE);
				t.setBackgroundResource(R.color.sky);
				t.setGravity(Gravity.CENTER);

				return t;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public Object getItem(int position) {
				return week[position];
			}

			@Override
			public int getCount() {
				return week.length;
			}
		});
	}

	protected void launchResearcherPasscodeDialog(final int code) {
		AlertDialog.Builder alertDialog;
		alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("Researcher's Passcode");
		alertDialog.setView(getResearcherPasscodeView());
		alertDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (checkPasscode()) {
							switch (code) {
							case SETTINGS:
								startSettingsActivity();
								break;
							case LIST:
								startSessionsListActivity();
								break;
							}
						} else
							Toast.makeText(context, "Wrong Passcode",
									Toast.LENGTH_SHORT).show();
					}
				});
		alertDialog.show();
	}

	private boolean checkPasscode() {
		String text = researcherPasscode.getText().toString();
		return text.equals(RESEARCHER_PASSCODE);
	}

	@SuppressLint("InflateParams")
	private View getResearcherPasscodeView() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout view = new RelativeLayout(this);
		view = (RelativeLayout) inflater.inflate(R.layout.researcher, null);

		researcherPasscode = (EditText) view.findViewById(R.id.researcher_edit);
		return view;
	}

	private void startSettingsActivity() {
		Intent i = new Intent(CalendarActivity.this, SettingsActivity.class);
		startActivity(i);
	}

	private void startSessionsListActivity() {
		if (dayClicked != null) {
			Intent i = new Intent(CalendarActivity.this,
					SessionsListActivity.class);
			i.putExtra(SessionsListActivity.EXTRA_ID, dayClicked);
			startActivity(i);
		}
	}

	@Override
	public void onClick(View v) {
		if (v == prevMonth) {
			if (month <= 1) {
				month = 12;
				year--;
			} else {
				month--;
			}
			setGridCellAdapterToDate(month, year);
		}
		if (v == nextMonth) {
			if (month > 11) {
				month = 1;
				year++;
			} else {
				month++;
			}

			setGridCellAdapterToDate(month, year);
		}

	}

	private void setGridCellAdapterToDate(int month, int year) {
		adapter = new GridCellAdapter(month, year);
		myCalendar.set(year, month - 1, myCalendar.get(Calendar.DAY_OF_MONTH));
		currentMonth.setText(CalendarManager.monthYearString(month, year));
		adapter.notifyDataSetChanged();
		calendarView.setAdapter(adapter);
	}

	public class GridCellAdapter extends BaseAdapter implements OnClickListener {
		private final List<String> list;
		private static final int DAY_OFFSET = 1;

		private int daysInMonth;
		private int currentDayOfMonth;
		private int currentWeekDay;
		private Button gridcell;

		public GridCellAdapter(int month, int year) {
			super();
			this.list = new ArrayList<String>();

			Calendar calendar = Calendar.getInstance();
			setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
			setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));

			// Print Month
			printMonth(month, year);
		}

		public String getItem(int position) {
			return list.get(position);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		/**
		 * Prints Month
		 * 
		 * @param mm
		 * @param yy
		 */
		private void printMonth(int mm, int yy) {
			int trailingSpaces = 0;
			int daysInPrevMonth = 0;
			int prevMonth = 0;
			int prevYear = 0;
			int nextMonth = 0;
			int nextYear = 0;

			int currentMonth = mm - 1;
			daysInMonth = CalendarManager.getNumberOfDaysOfMonth(currentMonth);

			GregorianCalendar cal = new GregorianCalendar(yy, currentMonth, 1);

			if (currentMonth == 11) {
				prevMonth = currentMonth - 1;
				daysInPrevMonth = CalendarManager
						.getNumberOfDaysOfMonth(prevMonth);
				nextMonth = 0;
				prevYear = yy;
				nextYear = yy + 1;

			} else if (currentMonth == 0) {
				prevMonth = 11;
				prevYear = yy - 1;
				nextYear = yy;
				daysInPrevMonth = CalendarManager
						.getNumberOfDaysOfMonth(prevMonth);
				nextMonth = 1;

			} else {
				prevMonth = currentMonth - 1;
				nextMonth = currentMonth + 1;
				nextYear = yy;
				prevYear = yy;
				daysInPrevMonth = CalendarManager
						.getNumberOfDaysOfMonth(prevMonth);

			}

			int currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
			trailingSpaces = currentWeekDay;

			if (cal.isLeapYear(cal.get(Calendar.YEAR)))
				if (mm == 2)
					++daysInMonth;
				else if (mm == 3)
					++daysInPrevMonth;

			// Trailing Month days
			for (int i = 0; i < trailingSpaces; i++) {
				list.add(String
						.valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET)
								+ i)
						+ "-GREY"
						+ "-"
						+ CalendarManager.getMonthAsString(prevMonth)
						+ "-"
						+ prevYear);
			}

			// Current Month Days
			for (int i = 1; i <= daysInMonth; i++) {

				if (i == getCurrentDayOfMonth()) {
					list.add(String.valueOf(i) + "-BLUE" + "-"
							+ CalendarManager.getMonthAsString(currentMonth)
							+ "-" + yy);
				} else {
					list.add(String.valueOf(i) + "-WHITE" + "-"
							+ CalendarManager.getMonthAsString(currentMonth)
							+ "-" + yy);
				}
			}

			// Leading Month days
			for (int i = 0; i < list.size() % 7; i++) {

				list.add(String.valueOf(i + 1) + "-GREY" + "-"
						+ CalendarManager.getMonthAsString(nextMonth) + "-"
						+ nextYear);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.screen_gridcell, parent, false);
			}

			// Get a reference to the Day gridcell
			gridcell = (Button) row.findViewById(R.id.calendar_day_gridcell);
			gridcell.setOnClickListener(this);

			// ACCOUNT FOR SPACING

			String[] day_color = list.get(position).split("-");
			String theday = day_color[0];
			String themonth = day_color[2];
			String theyear = day_color[3];

			// Set the Day GridCell
			gridcell.setText(theday);
			gridcell.setTag(CalendarManager.getDateFormat(theday, themonth,
					theyear));

			if (day_color[1].equals("GREY")) {
				gridcell.setTextColor(getResources()
						.getColor(R.color.lightgray));
			}
			if (day_color[1].equals("WHITE")) {
				gridcell.setTextColor(getResources().getColor(
						R.color.lightgray02));
			}
			if (day_color[1].equals("BLUE")) {
				String month = CalendarManager.currentMonth();
				String year = CalendarManager.currentYear();
				if (month.equals(themonth) && year.equals(theyear)) {
					// gridcell.setTextColor(Color.BLUE);
					gridcell.setTextColor(getResources().getColor(
							R.color.lightgray02));
					gridcell.setTypeface(null, Typeface.BOLD);
				} else {
					gridcell.setTextColor(getResources().getColor(
							R.color.lightgray));
				}
			}

			boolean dayOfSession = sessionsDB.dayOfSession(theday, themonth,
					theyear, showOnlyIntrusions);
			if (dayOfSession) {
				gridcell.setTextColor(getResources().getColor(R.color.orange));
			}

			return row;
		}

		@Override
		public void onClick(View view) {
			String date_month_year = (String) view.getTag();

			if (!sessionsDB.dayOfSession(date_month_year, showOnlyIntrusions)) {
				return;
			}
			dayClicked = date_month_year;

			// launchResearcherPasscodeDialog(LIST);
			startSessionsListActivity();
		}

		public int getCurrentDayOfMonth() {
			return currentDayOfMonth;
		}

		private void setCurrentDayOfMonth(int currentDayOfMonth) {
			this.currentDayOfMonth = currentDayOfMonth;
		}

		public void setCurrentWeekDay(int currentWeekDay) {
			this.currentWeekDay = currentWeekDay;
		}

		public int getCurrentWeekDay() {
			return currentWeekDay;
		}
	}
}