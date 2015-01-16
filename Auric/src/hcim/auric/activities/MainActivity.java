package hcim.auric.activities;

import hcim.auric.activities.passcode.Unlock;
import hcim.auric.activities.settings.SettingsActivity;
import hcim.auric.activities.setup.Welcome;
import hcim.auric.database.ConfigurationDatabase;
import hcim.auric.database.IntrusionsDatabase;
import hcim.auric.recognition.FaceRecognition;
import hcim.auric.utils.CalendarManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.opencv.android.OpenCVLoader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.hcim.intrusiondetection.R;

/**
 * 
 * CustomCalendarAndroid - https://github.com/manishsri01/CustomCalendarAndroid
 */
public class MainActivity extends Activity implements OnClickListener {
	public static final String TAG = "AURIC";

	private TextView currentMonth;
	private ImageView prevMonth;
	private ImageView nextMonth;
	private GridView calendarView;
	private GridCellAdapter adapter;
	private Calendar myCalendar;
	private int month, year;

	private static final int UNLOCK_CODE_SETTINGS = 20;
	private static final int UNLOCK_CODE_INT = 30;
	public static final int WELCOME_CODE = 40;

	private String dayClicked;

	private IntrusionsDatabase intrusionsDB;
	private ConfigurationDatabase configDB;
	private FaceRecognition fr;

	private int flag;

	private void initDatabase() {
		configDB = ConfigurationDatabase.getInstance(this);
		intrusionsDB = IntrusionsDatabase.getInstance(this);
		fr = FaceRecognition.getInstance(this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar);

		initDatabase();

		// init layout
		Button config = (Button) findViewById(R.id.button1);
		config.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (configDB.hasPasscode())
					startUnlockActivity(UNLOCK_CODE_SETTINGS);
				else
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

		adapter = new GridCellAdapter(this, R.id.calendar_day_gridcell, month,
				year);
		adapter.notifyDataSetChanged();
		calendarView.setAdapter(adapter);

	}

	private void initDaysOfTheWeekLayout() {
		GridView week = (GridView) findViewById(R.id.days_week);
		week.setAdapter(new BaseAdapter() {
			String[] week = { "\t\tSun", "\t\tMon", "\t\tTue", "\t\tWed",
					"\t\tThu", "\t\tFri", "\t\tSat" };

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView t = new TextView(MainActivity.this);
				t.setText(week[position]);
				t.setTextColor(Color.WHITE);
				t.setBackgroundResource(R.drawable.sub_bar);
				t.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);

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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// if (requestCode == UNLOCK_CODE && resultCode == RESULT_OK) {
		// boolean b = data.getBooleanExtra(Unlock.EXTRA_ID, false);
		//
		// if (!b)
		// finish();
		// }

		if (requestCode == UNLOCK_CODE_SETTINGS && resultCode == RESULT_OK) {
			boolean b = data.getBooleanExtra(Unlock.EXTRA_ID, false);

			if (b)
				startSettingsActivity();
		}
		if (requestCode == UNLOCK_CODE_INT && resultCode == RESULT_OK) {
			boolean b = data.getBooleanExtra(Unlock.EXTRA_ID, false);

			if (b) {
				startIntrusionsListActivity();
			}
		}
		if (requestCode == WELCOME_CODE) {
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(getBaseContext());
			boolean previouslyStarted = prefs.getBoolean(
					getString(R.string.pref_previously_started), false);
			if (!previouslyStarted) {
				super.finish();
			}
		}
	}

	@Override
	protected void onResume() {
		adapter = new GridCellAdapter(this, R.id.calendar_day_gridcell, month,
				year);
		adapter.notifyDataSetChanged();
		calendarView.setAdapter(adapter);

		super.onResume();

		if (OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this,
				fr.getLoaderCallback())) {
			if (flag == 0)
				firstLaunch();
		} else {
			Log.e(TAG, "Face Recognition - Cannot connect to OpenCV Manager");
		}
	}

	private void firstLaunch() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		boolean previouslyStarted = prefs.getBoolean(
				getString(R.string.pref_previously_started), false);
		if (!previouslyStarted) {
			// SharedPreferences.Editor edit = prefs.edit();
			// edit.putBoolean(getString(R.string.pref_previously_started),
			// Boolean.TRUE);
			// edit.commit();
			flag = -1;
			startWelcome();
		}
	}

	private void startWelcome() {
		Intent i = new Intent(MainActivity.this, Welcome.class);
		startActivityForResult(i, WELCOME_CODE);
	}

	private void startUnlockActivity(int code) {
		Intent i = new Intent(MainActivity.this, Unlock.class);
		startActivityForResult(i, code);
	}

	private void startSettingsActivity() {
		Intent i = new Intent(MainActivity.this, SettingsActivity.class);
		startActivity(i);
	}

	private void startIntrusionsListActivity() {
		if (dayClicked != null) {
			Intent i = new Intent(MainActivity.this,
					IntrusionsListActivity.class);
			i.putExtra(IntrusionsListActivity.EXTRA_ID, dayClicked);
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
		adapter = new GridCellAdapter(this, R.id.calendar_day_gridcell, month,
				year);
		myCalendar.set(year, month - 1, myCalendar.get(Calendar.DAY_OF_MONTH));
		currentMonth.setText(CalendarManager.monthYearString(month, year));
		adapter.notifyDataSetChanged();
		calendarView.setAdapter(adapter);
	}

	public class GridCellAdapter extends BaseAdapter implements OnClickListener {
		private final Context _context;

		private final List<String> list;
		private static final int DAY_OFFSET = 1;

		private int daysInMonth;
		private int currentDayOfMonth;
		private int currentWeekDay;
		private Button gridcell;
		private TextView num_events_per_day;
		private final HashMap<String, Integer> eventsPerMonthMap;

		public GridCellAdapter(Context context, int textViewResourceId,
				int month, int year) {
			super();
			this._context = context;
			this.list = new ArrayList<String>();

			Calendar calendar = Calendar.getInstance();
			setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
			setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));

			// Print Month
			printMonth(month, year);

			// Find Number of Events
			eventsPerMonthMap = findNumberOfEventsPerMonth(year, month);
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

		/**
		 * NOTE: YOU NEED TO IMPLEMENT THIS PART Given the YEAR, MONTH, retrieve
		 * ALL entries from a SQLite database for that month. Iterate over the
		 * List of All entries, and get the dateCreated, which is converted into
		 * day.
		 * 
		 * @param year
		 * @param month
		 * @return
		 */
		private HashMap<String, Integer> findNumberOfEventsPerMonth(int year,
				int month) {
			HashMap<String, Integer> map = new HashMap<String, Integer>();

			return map;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				LayoutInflater inflater = (LayoutInflater) _context
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
			if ((!eventsPerMonthMap.isEmpty()) && (eventsPerMonthMap != null)) {
				if (eventsPerMonthMap.containsKey(theday)) {
					num_events_per_day = (TextView) row
							.findViewById(R.id.num_events_per_day);
					Integer numEvents = (Integer) eventsPerMonthMap.get(theday);
					num_events_per_day.setText(numEvents.toString());
				}
			}

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
					gridcell.setTextColor(Color.BLUE);
					gridcell.setTypeface(null, Typeface.BOLD);
				} else {
					gridcell.setTextColor(getResources().getColor(
							R.color.lightgray));
				}
			}
			if (intrusionsDB.dayOfIntrusion(theday, themonth, theyear)) {
				gridcell.setTextColor(getResources().getColor(R.color.orange));
			}
			return row;
		}

		@Override
		public void onClick(View view) {
			String date_month_year = (String) view.getTag();

			if (!intrusionsDB.dayOfIntrusion(date_month_year)) {
				return;
			}
			dayClicked = date_month_year;

			if (configDB.hasPasscode()) {
				startUnlockActivity(UNLOCK_CODE_INT);
			} else {
				startIntrusionsListActivity();
			}
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