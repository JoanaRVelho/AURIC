package hcim.auric.record.screen.textlog;

import hcim.auric.utils.FileManager;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

/**
 * 
 * @author Joana Velho
 * 
 */
public class RecordSimpleText extends AccessibilityService {
	public static String TAG = "AURIC";

	private static TextualLog log;
	private static volatile String intrusion;

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		if (intrusion == null) {
			if (log != null) {
				Log.i(TAG, "RecordSimpleText - stop recording");
				TextualLog.store(new FileManager(getApplicationContext()), log);
				log = null;
			}
		} else {
			if (log == null) {
				Log.i(TAG, "RecordSimpleText - start recording");
				log = new TextualLog(intrusion, getPackageManager());
			}

			Log.i(TAG, "RecordSimpleText - recording - new event");
			if (event != null) {
				log.addItem(event);
			}
		}
	}

	@Override
	public void onInterrupt() {
	}

	@Override
	protected void onServiceConnected() {
		super.onServiceConnected();

		AccessibilityServiceInfo serviceInfo = new AccessibilityServiceInfo();
		serviceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
		serviceInfo.flags = AccessibilityServiceInfo.DEFAULT;
		serviceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;

		setServiceInfo(serviceInfo);
	}

	public static String getIntrusion() {
		return intrusion;
	}

	public static void setIntrusion(String intrusion) {
		RecordSimpleText.intrusion = intrusion;
	}
}