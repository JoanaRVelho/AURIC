package hcim.auric.authentication;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import com.hcim.intrusiondetection.R;

public class Notifier {
	private static final int NOTIFICATION_ID = 981532;

	Notification intrusionDetected;
	Context context;
	NotificationManager notificationManager;

	public Notifier(Context context) {
		this.context = context;
		this.notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		intrusionDetected = new Notification.Builder(context)
				.setContentTitle("Auric - Intrusion Detected")
				.setContentText("Background recording")
				.setSmallIcon(R.drawable.android_icon).build();
	}

	public void notifyUser() {
		notificationManager.notify(NOTIFICATION_ID, intrusionDetected);
	}

	public void cancelNotification() {
		notificationManager.cancel(NOTIFICATION_ID);
	}
}
