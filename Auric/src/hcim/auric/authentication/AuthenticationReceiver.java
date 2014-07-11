package hcim.auric.authentication;

import hcim.auric.database.ConfigurationDatabase;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AuthenticationReceiver extends BroadcastReceiver {

	private AuditTask task;
	
	public AuthenticationReceiver(AuditTask task) {
		this.task = task;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (ConfigurationDatabase.getMyPicture() == null) {
			Log.d("SCREEN", "DB null get my picture");
			return;
		}
		if (ConfigurationDatabase.getNegativePicture() == null) {
			Log.d("SCREEN", "DB null get other");
			return;
		}

		String action = intent.getAction();

		if (action.equals(Intent.ACTION_SCREEN_OFF)) {
			task.addTaskMessage(AuditTask.ACTION_OFF, null);
		}

		if (action.equals(Intent.ACTION_SCREEN_ON)) {
			task.addTaskMessage(AuditTask.ACTION_ON, null);
		}
	}
}
