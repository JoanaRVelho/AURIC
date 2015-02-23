package hcim.auric.detector;

import hcim.auric.audit.AuditQueue;
import hcim.auric.audit.IAuditTask;
import hcim.auric.audit.TaskMessage;
import hcim.auric.database.configs.TargetAppDatabase;
import android.content.Context;
import android.view.accessibility.AccessibilityEvent;

public class AppDetector {
	private TargetAppDatabase db;
	private AuditQueue queue;
	private boolean recording;

	private static AppDetector INSTANCE;

	public AppDetector(Context c, AuditQueue queue) {
		this.queue = queue;
		db = TargetAppDatabase.getInstance(c);
		INSTANCE = this;
		recording = false;
	}

	public void onAccessibilityEvent(AccessibilityEvent event) {
		String packageName = event.getPackageName().toString();
		boolean targetApp = db.isTargetApplication(packageName);

		if (recording && !targetApp) {
			TaskMessage t = new TaskMessage(IAuditTask.ACTION_NEW_APP);
			t.setIntrusion(false);
			queue.addTaskMessage(t);
		}

		if (!recording && targetApp) {
			TaskMessage t = new TaskMessage(IAuditTask.ACTION_NEW_APP);
			t.setIntrusion(true);
			queue.addTaskMessage(t);
		}
	}

	public static AppDetector getInstance(Context c) {
		return INSTANCE;
	}

	public void destroy() {
		INSTANCE = null;
	}
}
