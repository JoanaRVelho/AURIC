package hcim.auric.calendar;

import java.util.Calendar;

public class CalendarManager {
	
	private static final String DATE_AND_TIME_SEPARATOR = "@";
	private static final String DATE_SEPARATOR = " ";
	private static final String TIME_SEPARATOR = ":";
	
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

	public static String getTimestampFormat(Calendar c) {
		return c.getTimeInMillis() + "";
	}

	public static String getDateAndTime(Calendar c) {
		int seconds = c.get(Calendar.SECOND);
		int minutes = c.get(Calendar.MINUTE);
		int hours = c.get(Calendar.HOUR_OF_DAY);
		int day = c.get(Calendar.DAY_OF_MONTH);
		String month = CalendarManager.getMonthAsString(c.get(Calendar.MONTH));
		int year = c.get(Calendar.YEAR);
	
		StringBuilder s = new StringBuilder();
		s.append(day);
		s.append(DATE_SEPARATOR);
		s.append(month);
		s.append(DATE_SEPARATOR);
		s.append(year);
		s.append(DATE_AND_TIME_SEPARATOR);
		
		if(hours<10)
			s.append("0");
		s.append(hours);
		s.append(TIME_SEPARATOR);
		
		if (minutes < 10)
			s.append("0");
		s.append(minutes);
		s.append(TIME_SEPARATOR);
		
		if (seconds < 10)
			s.append("0");
		s.append(seconds);
		
		return s.toString();
	}

	public static String getDateFormat(String d, String m, String y) {
		StringBuilder s = new StringBuilder();
		s.append(m);
		s.append(" ");
		s.append(d);
		s.append(", ");
		s.append(y);
		return s.toString();
	}
	
	public static String getDateFormat(Calendar c) {
		int day = c.get(Calendar.DAY_OF_MONTH);
		String month = CalendarManager.getMonthAsString(c.get(Calendar.MONTH));
		int year = c.get(Calendar.YEAR);

		return getDateFormat(day+"", month, year+"");
	}

	public static String getTimeFormat(Calendar c) {
		int seconds = c.get(Calendar.SECOND);
		int minutes = c.get(Calendar.MINUTE);
		int hours = c.get(Calendar.HOUR_OF_DAY);

		StringBuilder s = new StringBuilder();
		if(hours<10)
			s.append("0");
		s.append(hours);
		s.append(TIME_SEPARATOR);
		
		if (minutes < 10)
			s.append("0");
		s.append(minutes);
		s.append(TIME_SEPARATOR);
		
		if (seconds < 10)
			s.append("0");
		s.append(seconds);
		
		return s.toString();
	}

	public static String getTimeFormat(String h, String m, String s) {
		StringBuilder builder = new StringBuilder();
		builder.append(h);
		builder.append(" ");
		builder.append(m);
		builder.append(", ");
		builder.append(s);
		return builder.toString();
	}
}
