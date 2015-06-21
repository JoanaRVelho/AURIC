package hcim.auric.service;

import hcim.auric.audit.AuditQueue;
import hcim.auric.audit.AuditTask;
import hcim.auric.audit.TaskMessage;
import hcim.auric.database.SettingsPreferences;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.TelephonyManager;

public class OnOffReceiver extends BroadcastReceiver {
	private IntentFilter filter;
	private AuditQueue queue;

	public OnOffReceiver(Context c, AuditQueue queue) {
		this.queue = queue;
		filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
	}

	public IntentFilter getIntentFilter() {
		return filter;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		SettingsPreferences settings = new SettingsPreferences(context);
		if (!settings.isIntrusionDetectorActive())
			return;

		String action = intent.getAction();

		if (action.equals(Intent.ACTION_SCREEN_OFF)) {
			// ignore screen turning off event due to a phone call
			if (!phoneCallInProgress(context)) { // off hook
				queue.addTaskMessage(new TaskMessage(AuditTask.ACTION_OFF));
			}
		}
		if (action.equals(Intent.ACTION_SCREEN_ON)) {
			if (!phoneCallInProgress(context)) {// off hook
				queue.addTaskMessage(new TaskMessage(AuditTask.ACTION_ON));
			}
		}
	}

	public boolean phoneCallInProgress(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getCallState() == TelephonyManager.CALL_STATE_OFFHOOK;
	}
}
