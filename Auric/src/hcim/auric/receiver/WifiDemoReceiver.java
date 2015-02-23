package hcim.auric.receiver;

import hcim.auric.audit.AuditQueue;
import hcim.auric.audit.IAuditTask;
import hcim.auric.audit.TaskMessage;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

@SuppressLint("SimpleDateFormat")
public class WifiDemoReceiver extends AbstractReceiver {

	public WifiDemoReceiver(AuditQueue queue) {
		super(queue);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("swat_interaction")) {
			boolean run = intent.getBooleanExtra("logging", false);

			if (run) {
				String timestamp = intent.getStringExtra("timestamp");

				TaskMessage t = new TaskMessage(IAuditTask.ACTION_ON);
				t.setTimestamp(timestamp);
				queue.addTaskMessage(t);

			} else {
				TaskMessage t = new TaskMessage(IAuditTask.ACTION_OFF);
				queue.addTaskMessage(t);
			}

		} else { // screen off
			TaskMessage t = new TaskMessage(IAuditTask.ACTION_OFF);
			queue.addTaskMessage(t);
		}

	}
}
