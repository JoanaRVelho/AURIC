package hcim.auric.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import com.hcim.intrusiondetection.R;

public class AccessibilityNotification {
	private static final int NOTIFICATION_ID = 981533;

	private NotificationManager notificationManager;
	private Notification serviceDisabled;

	public AccessibilityNotification(Context context) {
		this.notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		serviceDisabled = new Notification.Builder(context)
				.setContentTitle("AURIC Service")
				.setContentText("Accessibility Service Disabled")
				.setSmallIcon(R.drawable.auric_icon).build();
	}

	public void notifyUser() {
		notificationManager.notify(NOTIFICATION_ID, serviceDisabled);
	}

	public void cancelNotification() {
		notificationManager.cancel(NOTIFICATION_ID);
	}
}
