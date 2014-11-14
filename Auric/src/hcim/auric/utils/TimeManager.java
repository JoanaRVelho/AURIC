package hcim.auric.utils;

import hcim.auric.calendar.CalendarManager;
import android.os.SystemClock;

public class TimeManager {
	
	public static String getTime(long eventTime){
		long bootTime = System.currentTimeMillis() - SystemClock.elapsedRealtime();
		long sleep = SystemClock.elapsedRealtime() - SystemClock.uptimeMillis();
		long d = bootTime + eventTime + sleep;
		
		return CalendarManager.getTime(d);
	}

}
