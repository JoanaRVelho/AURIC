package hcim.auric.service;

import hcim.auric.database.SettingsPreferences;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import com.hcim.intrusiondetection.R;

public class IntrusionNotifier {
	private static final int NOTIFICATION_ID = 981532;

	private NotificationManager notificationManager;
	private Notification intrusionDetected;
	private boolean hide;

	public IntrusionNotifier(Context context) {
		this.notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		intrusionDetected = new Notification.Builder(context)
				.setContentTitle("AURIC - Intrusion Detected")
				.setContentText("Background recording")
				.setSmallIcon(R.drawable.auric_icon).build();
		hide = new SettingsPreferences(context).hideNotification();
	}

	public void notifyUser() {
		if (!hide)
			notificationManager.notify(NOTIFICATION_ID, intrusionDetected);
	}

	public void cancelNotification() {
		if (!hide)
			notificationManager.cancel(NOTIFICATION_ID);
	}
}
