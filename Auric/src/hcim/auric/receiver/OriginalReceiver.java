package hcim.auric.receiver;

import hcim.auric.audit.AuditTask;
import hcim.auric.audit.TaskMessage;
import android.content.Context;
import android.content.Intent;

public class OriginalReceiver extends AbstractReceiver {

	public OriginalReceiver(AuditTask task) {
		this.task = task;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		if (action.equals(Intent.ACTION_SCREEN_OFF)) {
			task.addTaskMessage(new TaskMessage(AuditTask.ACTION_OFF));
		}

		if (action.equals(Intent.ACTION_SCREEN_ON)) {
			task.addTaskMessage(new TaskMessage(AuditTask.ACTION_ON));
		}
	}
}
