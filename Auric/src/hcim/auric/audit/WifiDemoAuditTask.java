package hcim.auric.audit;

import hcim.auric.intrusion.Intrusion;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

public class WifiDemoAuditTask extends AbstractAuditTask {
	public static final String ACTION_START = "start";
	public static final String ACTION_STOP = "stop";

	public WifiDemoAuditTask(Context c) {
		super(c);
	}

	@Override
	public void run() {
		Log.d(TAG, "WifiDemoAuditTask - start task");
		TaskMessage taskMessage;
		String id;
		while (true) {
			if (!queue.isEmpty()) {
				try {
					taskMessage = queue.take();
					id = taskMessage.getID();

					Log.d(TAG, "WifiDemoAuditTask - task=" + id);

					if (id.equals(ACTION_STOP)) {
						screenOff = true;
						actionStop();
					} else if (id.equals(ACTION_START)) {
						screenOff = false;
						actionStart(taskMessage.getTimestamp() + "");
					} else if (id.equals(ACTION_NEW_PICTURE)) {
						actionNewPicture(taskMessage.getPic());
					}

				} catch (InterruptedException e) {
					Log.e(TAG, "WifiDemoAuditTask - " + e.getMessage());
				}

			}
		}
	}

	@SuppressLint("SimpleDateFormat")
	public void actionStart(String timestamp) {
		long timestampLong = Long.valueOf(timestamp);
		Date d = new Date(timestampLong);
		Format f = new SimpleDateFormat("dd-MMMM-yyyy HH:mm:ss");

		String id = f.format(d).toString();
		if (id.charAt(0) == '0')
			id = id.substring(1);
		String[] array = id.split(" ");

		currentIntrusion = new Intrusion(id, array[0], array[1], timestamp);

		startTimerTask(false);

		notifier.notifyUser();
	}

	public void actionStop() {
		stopTimerTask();

		intrusionsDB.addIntrusion(currentIntrusion);
		currentIntrusion = null;

		if (notifier != null) {
			notifier.cancelNotification();
		}
	}

	public void actionNewPicture(Bitmap bm) {
		if (currentIntrusion != null) {
			currentIntrusion.addImage(bm);
		}
	}
}
