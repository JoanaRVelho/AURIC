package hcim.auric.audit;

import hcim.auric.database.configs.ConfigurationDatabase;
import hcim.auric.intrusion.Intrusion;
import hcim.auric.intrusion.IntrusionFactory;
import hcim.auric.utils.CalendarManager;

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
		Format f = new SimpleDateFormat("dd-MMMM-yyyy-HH-mm-ss");

		String calendar = f.format(d).toString();
		if (calendar.charAt(0) == '0')
			calendar = calendar.substring(1);

		String[] array = calendar.split("-");

		String date = CalendarManager.getDateFormat(array[0], array[1],
				array[2]);
		String time = CalendarManager.getTimeFormat(array[3], array[4],
				array[5]);

		String logType = ConfigurationDatabase.getInstance(context).getLogType();
		currentIntrusion = IntrusionFactory.createIntrusion(timestamp, date,
				time, Intrusion.UNCHECKED, logType);
		intrusionsDB.insertIntrusionData(currentIntrusion);

		startTimerTask(false);

		notifier.notifyUser();
	}

	public void actionStop() {
		stopTimerTask();

		currentIntrusion = null;

		if (notifier != null) {
			notifier.cancelNotification();
		}
	}

	public void actionNewPicture(Bitmap bm) {
		if (currentIntrusion != null) {
			intrusionsDB.insertPictureOfTheIntruder(currentIntrusion.getID(),
					bm);
		}
	}
}
