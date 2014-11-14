package hcim.auric.record.screen;

import hcim.auric.database.ConfigurationDatabase;
import hcim.auric.record.screen.event_based.AccessibilityEventBasedLog;
import hcim.auric.record.screen.mswat_lib.MswatLibLog;
import hcim.auric.record.screen.screencast_root.ScreencastRootLog;
import android.content.Context;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextUtils;

public class LogManager {

	private static final String TAG = "AURIC";

	public static AbstractLog getSelectedLog(String log, Context context) {

		if (log != null) {
			if (log.equals(ConfigurationDatabase.MSWAT_LIB_LOG))
				return new MswatLibLog(context);

			if (log.equals(ConfigurationDatabase.SCREENCAST_ROOT_LOG))
				return new ScreencastRootLog(context);

			if (log.equals(ConfigurationDatabase.TEXT_LOG))
				return new AccessibilityEventBasedLog(context);
		}
		return null;
	}

	public static boolean hasAccessibilityService(String logType) {
		if (logType.equals(ConfigurationDatabase.MSWAT_LIB_LOG)) {
			return true;
		}
		if (logType.equals(ConfigurationDatabase.TEXT_LOG)) {
			return true;
		}
		return false;
	}

	public static boolean accessibilityServiceEnabled(Context c, String logType) {
		if (logType.equals(ConfigurationDatabase.MSWAT_LIB_LOG)) {
			boolean b =  checkAccessibilitySettings(c,
					"com.hcim.intrusiondetection/mswat.core.activityManager.HierarchicalService");
			android.util.Log.d(TAG, "MSWAT = "+ b);
			return b;
		}
		if (logType.equals(ConfigurationDatabase.TEXT_LOG)) {
			boolean b = checkAccessibilitySettings(c,
					"com.hcim.intrusiondetection/hcim.auric.record.screen.event_based.RecordEventBasedLog");
			android.util.Log.d(TAG, "TEXT LOG = "+ b);
			return b;
		}
		return false;
	}

	private static boolean checkAccessibilitySettings(Context c,
			final String service) {
		int accessibilityEnabled = 0;
		boolean accessibilityFound = false;
		try {
			accessibilityEnabled = Settings.Secure.getInt(c
					.getApplicationContext().getContentResolver(),
					android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
		} catch (SettingNotFoundException e) {
			android.util.Log.e(TAG, e.getMessage());
		}
		TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(
				':');

		if (accessibilityEnabled == 1) {
			String settingValue = Settings.Secure.getString(c
					.getApplicationContext().getContentResolver(),
					Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
			if (settingValue != null) {
				TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
				splitter.setString(settingValue);
				while (splitter.hasNext()) {
					String accessabilityService = splitter.next();

					if (accessabilityService.equalsIgnoreCase(service)) {
						android.util.Log.i(TAG, "ACCESSIBILIY IS ENABLED");
						return true;
					}
				}
			}
		} else {
			android.util.Log.i(TAG, "ACCESSIBILIY IS DISABLED");
		}
		return accessibilityFound;
	}
}
