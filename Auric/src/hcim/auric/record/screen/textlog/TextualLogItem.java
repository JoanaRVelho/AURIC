package hcim.auric.record.screen.textlog;

import hcim.auric.calendar.CalendarManager;
import android.graphics.drawable.Drawable;

public class TextualLogItem {
	static final String TAG = "AURIC";

	private static final String DELIMITER = "_LIMIT_";

	private String appName;
	private String time;
//	private String details;
	private Drawable icon;

	private String packageName;
	
	public TextualLogItem(){
		this.appName = null;
		this.time = null;
		this.icon = null;
		this.packageName = null;
	}

	public TextualLogItem(String appName, String time, String packageName) {
		this.appName = appName;
		this.time = time;
		this.packageName = packageName;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String text) {
		this.appName = text;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
//
//	public String getDetails() {
//		return details;
//	}
//
//	public void setDetails(String details) {
//		this.details = details;
//	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	/**
	 * 
	 * @param previous : previous item
	 * @return top distance between this and t
	 */
	public int distance(TextualLogItem previous){
		String[] thisSplit = this.time.split(CalendarManager.TIME_SEPARATOR);
		String[] previousSplit = previous.time.split(CalendarManager.TIME_SEPARATOR);
		
		int thisHours = Integer.valueOf(thisSplit[0]);
		int thisMin = Integer.valueOf(thisSplit[1]);
		int thisSec = Integer.valueOf(thisSplit[2]);
		
		int previousHours = Integer.valueOf(previousSplit[0]);
		int previousMin = Integer.valueOf(previousSplit[1]);
		int previousSec = Integer.valueOf(previousSplit[2]);
		
		int hours = (thisHours - previousHours)*3600;
		int min = (thisMin - previousMin)*60;
		int sec = (thisSec - previousSec);
		
		int result = hours + min + sec;
		result *=50;
		return result;
	}
	
	public static TextualLogItem convertString(String s) {
		String[] array = s.split(DELIMITER);
		TextualLogItem result = new TextualLogItem(array[0], array[1], array[2]);

		return result;
	}
	
	@Override
	public String toString() {
		return appName + DELIMITER + time + DELIMITER + packageName;
	}
}
