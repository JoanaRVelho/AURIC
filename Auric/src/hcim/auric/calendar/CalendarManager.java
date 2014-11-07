package hcim.auric.calendar;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;

@SuppressLint("SimpleDateFormat") public class CalendarManager {
	
	public static final String MONTH_DAY_SEPARATOR = " ";
	public static final String DAY_YEAR_SEPARATOR = ", ";
	public static final String TIME_SEPARATOR = ":";
	
	private static final String[] weekdays = new String[] { "Sun", "Mon",
			"Tue", "Wed", "Thu", "Fri", "Sat" };
	private static final String[] months = { "January", "February", "March",
			"April", "May", "June", "July", "August", "September", "October",
			"November", "December" };

	private static final int[] daysOfMonth = { 31, 28, 31, 30, 31, 30, 31, 31,
			30, 31, 30, 31 };

	public static String getMonthAsString(int i) {
		return months[i];
	}

	public static String getWeekDayAsString(int i) {
		return weekdays[i];
	}

	public static int getNumberOfDaysOfMonth(int i) {
		return daysOfMonth[i];
	}

	public static String currentMonthYear() {
		Calendar c = Calendar.getInstance();
		return CalendarManager.getMonthAsString(c.get(Calendar.MONTH)) + " "
				+ c.get(Calendar.YEAR);
	}
	
	public static String currentTimeMillis(){
		return System.currentTimeMillis() + "";
	}

	public static String getDate(String timestamp) {
		long timestampLong = Long.valueOf(timestamp);
		Date d = new Date(timestampLong);
		Format f = new SimpleDateFormat("dd-MMMM-yyyy");
	
		String calendar = f.format(d).toString();
		if (calendar.charAt(0) == '0')
			calendar = calendar.substring(1);
	
		String[] array = calendar.split("-");
	
		String date = CalendarManager.getDateFormat(array[0], array[1],
				array[2]);
		
		return date; 
	}

	public static String getDateFormat(String d, String m, String y) {
		StringBuilder s = new StringBuilder();
		s.append(m);
		s.append(MONTH_DAY_SEPARATOR);
		s.append(d);
		s.append(DAY_YEAR_SEPARATOR);
		s.append(y);
		return s.toString();
	}
	
	public static String getTime(String timestamp) {
		long timestampLong = Long.valueOf(timestamp);
		Date d = new Date(timestampLong);
		Format f = new SimpleDateFormat("HH-mm-ss");
	
		String calendar = f.format(d).toString();
		if (calendar.charAt(0) == '0')
			calendar = calendar.substring(1);
	
		String[] array = calendar.split("-");
	
		String time = CalendarManager.getTimeFormat(array[0], array[1],
				array[2]);
		
		return time; 
	}

	public static String getTimeFormat(String h, String m, String s) {
		StringBuilder builder = new StringBuilder();
		builder.append(h);
		builder.append(TIME_SEPARATOR);
		builder.append(m);
		builder.append(TIME_SEPARATOR);
		builder.append(s);
		return builder.toString();
	}
}
