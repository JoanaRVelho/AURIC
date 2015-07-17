package hcim.auric.activities;

import hcim.auric.activities.passcode.Unlock;
import hcim.auric.activities.settings.SettingsActivity;
import hcim.auric.data.EventLogDatabase;
import hcim.auric.data.PicturesDatabase;
import hcim.auric.data.SessionDatabase;
import hcim.auric.data.SettingsPreferences;
import hcim.auric.recognition.FaceRecognition;
import hcim.auric.utils.CalendarManager;
import hcim.auric.utils.LogUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.opencv.android.OpenCVLoader;

import android.app.Activity;
import android.content.Context;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.hcim.intrusiondetection.R;

/**
 * 
 * CustomCalendarAndroid - https://github.com/manishsri01/CustomCalendarAndroid
 */
public class CalendarActivity extends Activity {
	static final int WELCOME_CODE = 10;
	static final int UNLOCK_CODE = 20;

	private TextView currentMonth;
	private ImageView prevMonth;
	private ImageView nextMonth;
	private GridView calendarView;
	private MonthCellAdapter adapter;
	private Calendar myCalendar;
	private int month, year;

	private String dayClicked;

	private SettingsPreferences settings;
	private SessionDatabase sessionsDB;

	private CheckBox showOnlyIntCheckBox;
	private boolean showOnlyIntrusions;
	private Context context;
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
		sessionsDB.printAll();
	}

	private void initDatabase() {
		settings = new SettingsPreferences(this);
		sessionsDB = SessionDatabase.getInstance(this);
		fr = FaceRecognition.getInstance(this);
		PicturesDatabase.getInstance(this);
		EventLogDatabase.getInstance(this);
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
		adapter = new MonthCellAdapter(month, year);
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
								adapter = new MonthCellAdapter(month, year);
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
		prevMonth.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (month <= 1) {
					month = 12;
					year--;
				} else {
					month--;
				}
				setGridCellAdapterToDate(month, year);
			}
		});

		currentMonth = (TextView) this.findViewById(R.id.currentMonth);
		currentMonth.setText(CalendarManager.monthYearString(month, year));

		nextMonth = (ImageView) this.findViewById(R.id.nextMonth);
		nextMonth.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (month > 11) {
					month = 1;
					year++;
				} else {
					month++;
				}

				setGridCellAdapterToDate(month, year);
			}
		});

		calendarView = (GridView) this.findViewById(R.id.calendar);

		adapter = new MonthCellAdapter(month, year);
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

	private void startSettingsActivity() {
		Intent i = new Intent(CalendarActivity.this, SettingsActivity.class);
		startActivity(i);
	}

	private void startSessionsListActivity() {
		if (dayClicked != null) {
			Intent i = new Intent(CalendarActivity.this, SessionsList.class);
			i.putExtra(SessionsList.EXTRA_ID, dayClicked);
			startActivity(i);
		}
	}

	private void setGridCellAdapterToDate(int month, int year) {
		adapter = new MonthCellAdapter(month, year);
		myCalendar.set(year, month - 1, myCalendar.get(Calendar.DAY_OF_MONTH));
		currentMonth.setText(CalendarManager.monthYearString(month, year));
		adapter.notifyDataSetChanged();
		calendarView.setAdapter(adapter);
	}

	public class MonthCellAdapter extends BaseAdapter implements
			OnClickListener {
		private final List<DayCell> list;
		private static final int DAY_OFFSET = 1;

		private int daysInMonth;
		private int currentDayOfMonth;
		private int currentWeekDay;
		private Button gridcell;

		public MonthCellAdapter(int month, int year) {
			this.list = new ArrayList<DayCell>();

			Calendar calendar = Calendar.getInstance();
			setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
			setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));

			createMonthCalendar(month, year);
		}

		@Override
		public DayCell getItem(int position) {
			return list.get(position);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		private void createMonthCalendar(int mm, int yy) {
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
			String day;
			String month = CalendarManager.getMonthAsString(prevMonth);
			String year = String.valueOf(prevYear);

			DayCell d;

			// Trailing Month days
			for (int i = 0; i < trailingSpaces; i++) {
				day = String
						.valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET)
								+ i);

				d = new DayCell(R.color.lightgray, day, month, year);
				list.add(d);
			}

			month = CalendarManager.getMonthAsString(currentMonth);
			year = String.valueOf(yy);

			// Current Month Days
			for (int i = 1; i <= daysInMonth; i++) {
				d = new DayCell(R.color.gray, String.valueOf(i), month, year);
				list.add(d);
			}

			month = CalendarManager.getMonthAsString(nextMonth);
			year = String.valueOf(nextYear);

			// Leading Month days
			for (int i = 1; i < list.size() % 7; i++) {
				d = new DayCell(R.color.lightgray, String.valueOf(i), month,
						year);
				list.add(d);
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

			gridcell = (Button) row.findViewById(R.id.calendar_day_gridcell);
			gridcell.setOnClickListener(this);

			DayCell d = list.get(position);
			gridcell.setText(d.day);
			gridcell.setTextColor(getResources().getColor(d.resourceColor));
			gridcell.setTag(CalendarManager.getDateFormat(d.day, d.month,
					d.year));

			if (currentDay(d)) {
				gridcell.setTypeface(null, Typeface.BOLD);
			}

			boolean dayOfSession = sessionsDB.dayOfSession(d.day, d.month,
					d.year, showOnlyIntrusions);

			if (dayOfSession) {
				gridcell.setTextColor(getResources().getColor(R.color.orange));
			}

			return row;
		}

		private boolean currentDay(DayCell d) {
			return CalendarManager.currentDay().equals(d.day)
					&& CalendarManager.currentMonth().equals(d.month)
					&& CalendarManager.currentYear().equals(d.year);
		}

		@Override
		public void onClick(View view) {
			String date_month_year = (String) view.getTag();

			if (!sessionsDB.dayOfSession(date_month_year, showOnlyIntrusions)) {
				return;
			}
			dayClicked = date_month_year;

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