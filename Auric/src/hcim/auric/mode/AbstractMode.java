package hcim.auric.mode;

import hcim.auric.audit.IAuditTask;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

public abstract class AbstractMode {
	protected BroadcastReceiver receiver;
	protected IntentFilter filter;
	protected Context context;
	protected IAuditTask task;

	public AbstractMode(Context c) {
		context = c;
	}

	public IAuditTask getTask() {
		return task;
	}

	public BroadcastReceiver getReceiver() {
		return receiver;
	}

	public IntentFilter getFilter() {
		return filter;
	}

	public void destroy() {
		if (task != null)
			task.stopTask();
	}

	public String getDetectorType() {
		return task.getDetector().type();
	}

	public String getRecorderType() {
		return task.getRecorder().type();
	}
}
