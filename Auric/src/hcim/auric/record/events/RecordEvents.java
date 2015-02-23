package hcim.auric.record.events;

import hcim.auric.database.intrusions.EventBasedLogDatabase;
import android.content.Context;
import android.view.accessibility.AccessibilityEvent;

/**
 * 
 * @author Joana Velho
 * 
 */
public class RecordEvents {
	public static final String TAG = "AURIC";
	public static final String[] PACKAGES = { "com.android.systemui",
			"com.sec.android.app.popupuireceiver" };

	private static RecordEvents INSTANCE = null;

	private EventBasedLog log;
	private volatile String intrusion;
	private EventBasedLogDatabase db;
	private Context context;

	public RecordEvents(Context c) {
		this.context = c;
		db = EventBasedLogDatabase.getInstance(c);
		INSTANCE = this;
	}

	public void onAccessibilityEvent(AccessibilityEvent event) {
		if (intrusion == null) {
			if (log != null) {
				log.filter();
				db.insert(log);

				log = null;
			}
		} else {
			if (log == null) {
				log = new EventBasedLog(intrusion);
			}

			if (event != null) {
				EventBasedLogItem t = new EventBasedLogItem(event, context);
				if (!reject(t)) {
					log.addItem(t);
				}
			}
		}
	}

	private boolean reject(EventBasedLogItem t) {
		String packageName = t.getPackageName();
		for (String s : PACKAGES) {
			if (s.equals(packageName))
				return true;
		}
		return false;
	}

	public String getIntrusion() {
		return intrusion;
	}

	public void setIntrusion(String intrusion) {
		this.intrusion = intrusion;
	}

	public static RecordEvents getInstance(Context c) {
		return INSTANCE;
	}

	public void destroy() {
		INSTANCE = null;
	}
}