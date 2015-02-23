package hcim.auric.receiver;

import hcim.auric.audit.AuditQueue;
import hcim.auric.audit.IAuditTask;
import hcim.auric.audit.TaskMessage;
import android.content.Context;
import android.content.Intent;

public class OriginalReceiver extends AbstractReceiver {

	public OriginalReceiver(AuditQueue queue) {
		super(queue);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();

		if (action.equals(Intent.ACTION_SCREEN_OFF)) {
			queue.addTaskMessage(new TaskMessage(IAuditTask.ACTION_OFF));
		}

		if (action.equals(Intent.ACTION_SCREEN_ON)) {
			queue.addTaskMessage(new TaskMessage(IAuditTask.ACTION_ON));
		}
	}
}
