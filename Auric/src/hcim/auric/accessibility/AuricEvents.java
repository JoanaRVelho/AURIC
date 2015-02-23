package hcim.auric.accessibility;

import hcim.auric.detector.AppDetector;
import hcim.auric.detector.DetectorManager;
import hcim.auric.record.RecorderManager;
import hcim.auric.record.events.RecordEvents;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;

/**
 * 
 * @author Joana Velho
 * 
 */
public class AuricEvents extends AccessibilityService {
	
	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		RecordEvents recorder = RecordEvents.getInstance(this);
		
		if (recorder != null) {
			recorder.onAccessibilityEvent(event);
		}

		AppDetector appDetector = AppDetector.getInstance(this
				.getApplicationContext());
		if (appDetector != null)
			appDetector.onAccessibilityEvent(event);
	}

	@Override
	public void onInterrupt() {
	}

	@Override
	protected void onServiceConnected() {
		super.onServiceConnected();

		AccessibilityServiceInfo serviceInfo = new AccessibilityServiceInfo();

		serviceInfo.eventTypes = EventManager.getEventTypes();
		serviceInfo.flags = AccessibilityServiceInfo.DEFAULT;
		serviceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;

		setServiceInfo(serviceInfo);
	}

	public static boolean hasAccessibilityService(String recorder,
			String detector) {
		if (recorder.equals(RecorderManager.EVENT_BASED)) {
			return true;
		}
		if (detector.equals(DetectorManager.APPS)) {
			return true;
		}
		return false;
	}

	public static boolean accessibilityServiceEnabled(Context c) {
		boolean b = checkAccessibilitySettings(
				c,
				"com.hcim.intrusiondetection/hcim.auric.accessibility.AuricEvents");
		return b;
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
						return true;
					}
				}
			}
		}
		return accessibilityFound;
	}
}