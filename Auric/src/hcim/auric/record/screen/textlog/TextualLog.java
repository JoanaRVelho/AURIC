package hcim.auric.record.screen.textlog;

import hcim.auric.calendar.CalendarManager;
import hcim.auric.utils.FileManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

public class TextualLog {

	private static final String TAG = "AURIC";
	private List<TextualLogItem> list;
	private String intrusionID;
	private PackageManager pm;

	public TextualLog(String intrusionID, PackageManager pm) {
		this.intrusionID = intrusionID;
		this.list = new ArrayList<TextualLogItem>();
		this.pm = pm;
	}

	public void addItem(AccessibilityEvent event) {
		String time = CalendarManager.getTime(event.getEventTime() + "");
		String packageName = event.getPackageName().toString();
		String appName = getAppName(packageName);

		list.add(new TextualLogItem(appName, time, packageName));
	}

	private String getAppName(String packageName) {
		ApplicationInfo info;
		try {
			info = pm.getApplicationInfo(packageName, 0);
		} catch (final NameNotFoundException e) {
			return "UNKNOWN";
		}
		if (info == null)
			return "UNKNOWN";

		String result = pm.getApplicationLabel(info).toString();

		if (result.equals("")) {
			return "UNKNOWN";
		}
		
		return result;
	}

	private Drawable getAppIcon(String packageName) {
		Drawable d = null;
		try {
			d = pm.getApplicationIcon(packageName);
		} catch (NameNotFoundException e) {
		}
		return d;
	}

	private void filter() {
		List<TextualLogItem> newList = new ArrayList<TextualLogItem>();
		TextualLogItem last = null;

		for (TextualLogItem t : list) {
			if (last == null) {
				last = t;
			} else {
				if (!last.getAppName().equals(t.getAppName())) {
					newList.add(last);
				}
				last = t;
			}
		}
		
		if(last != null)
			newList.add(last);
		
		list = newList;
	}

	public static void load(FileManager fileManager, TextualLog log) {
		String filepath = fileManager.getIntrusionLog(log.intrusionID);

		try {
			BufferedReader reader = new BufferedReader(new FileReader(filepath));
			String s;

			while ((s = reader.readLine()) != null) {
				TextualLogItem t = TextualLogItem.convertString(s);

				Drawable d = log.getAppIcon(t.getPackageName());
				t.setIcon(d);

				log.list.add(t);
			}
			reader.close();
		} catch (IOException e) {
		}

		// log.filter();
	}

	public static void store(FileManager fileManager, TextualLog log) {
		log.filter();

		String filepath = fileManager.getIntrusionDirectory(log.intrusionID);
		File f = new File(filepath);
		f.mkdirs();

		filepath = fileManager.getIntrusionLog(log.intrusionID);
		try {
			Log.d(TAG, "TextualLog - store");
			BufferedWriter writer = new BufferedWriter(new FileWriter(filepath));
			for (TextualLogItem t : log.list) {
				writer.write(t.toString());
				writer.newLine();
				Log.d(TAG, "TextualLog - item=" + t.toString());
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
		}
	}

	public List<TextualLogItem> getList() {
		return list;
	}
}
