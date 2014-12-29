package hcim.auric.activities;

import android.content.Context;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextUtils;
import android.util.Log;

public class CheckSettings {

	private static final String TAG = "AURIC";

	public static boolean isAccessibilitySettingsOn(Context mContext) {
		int accessibilityEnabled = 0;
		final String service = "com.hcim.intrusiondetection/mswat.core.activityManager.HierarchicalService";
		boolean accessibilityFound = false;
		try {
			accessibilityEnabled = Settings.Secure.getInt(mContext
					.getApplicationContext().getContentResolver(),
					android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
			Log.i(TAG, "accessibilityEnabled = " + accessibilityEnabled);
		} catch (SettingNotFoundException e) {
			Log.e(TAG,
					"Error finding setting, default accessibility to not found: "
							+ e.getMessage());
		}
		TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(
				':');

		if (accessibilityEnabled == 1) {
			Log.i(TAG, "***ACCESSIBILIY IS ENABLED***");
			String settingValue = Settings.Secure.getString(mContext
					.getApplicationContext().getContentResolver(),
					Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
			if (settingValue != null) {
				TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
				splitter.setString(settingValue);
				while (splitter.hasNext()) {
					String accessabilityService = splitter.next();

					Log.i(TAG, "-------------- > accessabilityService :: "
							+ accessabilityService);
					if (accessabilityService.equalsIgnoreCase(service)) {
						Log.i(TAG,
								"We've found the correct setting - accessibility is switched on!");
						return true;
					}
				}
			}
		} else {
			Log.i(TAG, "***ACCESSIBILIY IS DISABLED***");
		}
		return accessibilityFound;
	}

}
