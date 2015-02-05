package hcim.auric.record.screen.event_based;

import hcim.auric.database.intrusions.EventBasedLogDatabase;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

/**
 * 
 * @author Joana Velho
 * 
 */
public class RecordEventBasedLog extends AccessibilityService {
	public static final String TAG = "AURIC";
	public static final String[] PACKAGES = { "com.android.systemui",
			"com.sec.android.app.popupuireceiver" };

	private static EventBasedLog log;
	private static volatile String intrusion;

	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		if (intrusion == null) {
			if (log != null) {
				Log.i(TAG, "RecordEventBasedLog - stop recording");
				EventBasedLogDatabase db = EventBasedLogDatabase
						.getInstance(this);
				log.filter();
				db.insert(log);

				log = null;
			}
		} else {
			if (log == null) {
				Log.i(TAG, "RecordEventBasedLog - start recording");
				log = new EventBasedLog(intrusion);
			}

			if (event != null) {
				EventBasedLogItem t = new EventBasedLogItem(event, this);
				if (!reject(t))
					log.addItem(t);
			}
		}
	}

	private boolean reject(EventBasedLogItem t) {
		String packageName = t.getPackageName();
		for(String s : PACKAGES){
			if(s.equals(packageName))
				return true;
		}
		return false;
	}

	@Override
	public void onInterrupt() {
	}

	@Override
	protected void onServiceConnected() {
		super.onServiceConnected();

		AccessibilityServiceInfo serviceInfo = new AccessibilityServiceInfo();
		// serviceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;

		// serviceInfo.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED
		// | AccessibilityEvent.TYPE_VIEW_LONG_CLICKED
		// | AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
		
		serviceInfo.eventTypes = EventManager.getEventTypes();

		serviceInfo.flags = AccessibilityServiceInfo.DEFAULT;
		serviceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;

		setServiceInfo(serviceInfo);
	}

	public static String getIntrusion() {
		return intrusion;
	}

	public static void setIntrusion(String intrusion) {
		RecordEventBasedLog.intrusion = intrusion;
	}
}