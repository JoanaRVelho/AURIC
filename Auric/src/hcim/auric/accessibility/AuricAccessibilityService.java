package hcim.auric.accessibility;

import hcim.auric.record.RecorderManager;
import hcim.auric.record.events.EventRecorder;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;

/**
 * 
 * Auric's Accessibility Service
 * 
 * @author Joana Velho
 * 
 */
public class AuricAccessibilityService extends AccessibilityService {

	private static final String SERVICE = "com.hcim.intrusiondetection/hcim.auric.accessibility.AuricAccessibilityService";

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		EventRecorder recorder = EventRecorder.getInstance();

		if (recorder != null && recorder.running()) {
			recorder.newEvent(event);
		}
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

	/**
	 * Checks if a recorder type requires Auric's Accessibility Service
	 * 
	 * @param recorder
	 *            : recorder type
	 * @return true if recorder type requires Auric's Accessibility Service,
	 *         false otherwise
	 */
	public static boolean hasAccessibilityService(String recorder) {
		if (recorder.equals(RecorderManager.EVENT_BASED)) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if Auric's service is enabled
	 * 
	 * @param c
	 *            : application context
	 * @return true Auric's service is enabled, false otherwise
	 */
	public static boolean accessibilityServiceEnabled(Context c) {
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

					if (accessabilityService.equalsIgnoreCase(SERVICE)) {
						return true;
					}
				}
			}
		}
		return accessibilityFound;
	}

	@Override
	public void onInterrupt() {

	}
}