package hcim.auric.record.events;

import hcim.auric.data.EventLogDatabase;
import hcim.auric.record.IRecorder;
import hcim.auric.record.RecorderManager;
import android.content.Context;
import android.view.accessibility.AccessibilityEvent;

public class EventRecorder implements IRecorder {
	private static final String[] FORBIDDEN_PACKAGES = { "android",
			"com.android.systemui", "com.sec.android.app.popupuireceiver" };

	private EventBasedLog log;
	private EventLogDatabase db;
	private Context context;
	private volatile static boolean stop;

	private static EventRecorder instance;

	public static EventRecorder getInstance() {
		return instance;
	}

	public EventRecorder(Context c) {
		context = c;
		db = EventLogDatabase.getInstance(c);
		instance = this;
	}

	public void newEvent(AccessibilityEvent event) {
		if (stop)
			return;

		if (log == null || event == null)
			return;

		if (rejectPackage(event.getPackageName().toString()))
			return;

		log.addItem(event, context);
	}

	private void store(EventBasedLog logToStore) {
		if (logToStore != null) {
			logToStore.filter();
			db.insert(logToStore);
		}
	}

	private boolean rejectPackage(String packageName) {
		for (String s : FORBIDDEN_PACKAGES) {
			if (s.equals(packageName) || s.startsWith(packageName))
				return true;
		}
		return (packageName.contains("launcher") || packageName
				.contains("keyboard"));
	}

	@Override
	public void start(String intrusionID) {
	//	AuricEvents.start();
		if (log == null) {
			log = new EventBasedLog(intrusionID);
			stop = false;
		}
	}

	@Override
	public void stop() {
	//	AuricEvents.stop();
		
		stop = true;
		store(log);
		log = null;
	}

	@Override
	public String type() {
		return RecorderManager.EVENT_BASED;
	}

	@Override
	public void destroy() {
		stop();
		instance = null;
	}

	public boolean running() {
		return !stop;
	}
}
