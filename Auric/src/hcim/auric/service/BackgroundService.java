package hcim.auric.service;

import hcim.auric.accessibility.AuricEvents;
import hcim.auric.database.configs.ConfigurationDatabase;
import hcim.auric.detector.DetectorManager;
import hcim.auric.mode.AbstractMode;
import hcim.auric.mode.AppMode;
import hcim.auric.mode.OriginalMode;
import hcim.auric.mode.WifiMode;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.hcim.intrusiondetection.R;

public class BackgroundService extends Service {
	protected static final String TAG = "AURIC";
	private static final int NOTIFICATION_STICKY = 1;

	private Context context;
	private AbstractMode currentMode;
	private NotificationManager notificationManager;
	private ConfigurationDatabase configDB;

	private String modeDescription;

	@Override
	public void onCreate() {
		context = getApplicationContext();
		configDB = ConfigurationDatabase.getInstance(context);

		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		currentMode = getCurrentMode();
		registerReceiver(currentMode.getReceiver(), currentMode.getFilter());
		currentMode.getTask().startTask();

		TimerTask not = new TimerTask() {

			@Override
			public void run() {
				if (launchNotification()) {
					AccessibilityNotification notification = new AccessibilityNotification(
							context);
					notification.notifyUser();
				}
			}

			private boolean launchNotification() {
				ConfigurationDatabase db = ConfigurationDatabase
						.getInstance(context);
				String recorder = db.getRecorderType();
				String detector = db.getDetectorType();

				return AuricEvents.hasAccessibilityService(
						recorder, detector)
						&& !AuricEvents
								.accessibilityServiceEnabled(context);
			}
		};
		Timer timer = new Timer();
		timer.schedule(not, 30000);

		startForeground(NOTIFICATION_STICKY, getNotification());

		return Service.START_STICKY;
	}

	@Override
	public void onDestroy() {
		if (currentMode != null) {
			currentMode.destroy();

			unregisterReceiver(currentMode.getReceiver());
		}
		super.onDestroy();
	}

	private AbstractMode getCurrentMode() {
		modeDescription = configDB.getDetectorType();

		if (modeDescription != null) {
			if (modeDescription.equals(DetectorManager.FACE_RECOGNITION)) {
				return new OriginalMode(context);
			}
			if (modeDescription.equals(DetectorManager.WIFI)) {
				return new WifiMode(context);
			}
			if (modeDescription.equals(DetectorManager.APPS)) {
				return new AppMode(context);
			}
		}
		return null;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@SuppressWarnings("deprecation")
	private Notification getNotification() {

		if (notificationManager == null)
			notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		if (configDB.hideNotification())
			return fakeNotification();

		CharSequence contentTitle = "AURIC Service";
		CharSequence contentText = "AURIC Service is now running";

		Notification note = new Notification(R.drawable.official_icon,
				contentTitle, 0);
		note.flags |= Notification.FLAG_NO_CLEAR;
		note.flags |= Notification.FLAG_FOREGROUND_SERVICE;

//		PendingIntent intent = PendingIntent.getActivity(this, 0, new Intent(
//				this, MainActivity.class), 0);
		PendingIntent intent = null;

		note.setLatestEventInfo(this, contentTitle, contentText, intent);
		return note;
	}

	@SuppressWarnings("deprecation")
	private Notification fakeNotification() {
		CharSequence contentTitle = "Google Play Service";
		CharSequence contentText = "New Updates";

		Notification note = new Notification(R.drawable.google_play,
				contentTitle, 0);
		note.flags |= Notification.FLAG_NO_CLEAR;
		note.flags |= Notification.FLAG_FOREGROUND_SERVICE;

		note.setLatestEventInfo(this, contentTitle, contentText, null);
		return note;
	}
}
