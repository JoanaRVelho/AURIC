package hcim.auric.receiver;

import hcim.auric.audit.AuditTask;
import hcim.auric.audit.TaskMessage;
import hcim.auric.database.PicturesDatabase;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class OriginalReceiver extends AbstractReceiver {
	private PicturesDatabase picturesDB;

	public OriginalReceiver(AuditTask task) {
		this.task = task;
		picturesDB = PicturesDatabase.getInstance(task.getContext());
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (picturesDB.getMyPicture() == null) {
			Log.d(TAG, "DB null");
			return;
		}

		String action = intent.getAction();

		if (action.equals(Intent.ACTION_SCREEN_OFF)) {
			task.addTaskMessage(new TaskMessage(AuditTask.ACTION_OFF));
		}

		if (action.equals(Intent.ACTION_SCREEN_ON)) {
			task.addTaskMessage(new TaskMessage(AuditTask.ACTION_ON));
		}
	}
}
