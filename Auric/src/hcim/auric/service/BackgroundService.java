package hcim.auric.service;

import hcim.auric.data.SettingsPreferences;
import hcim.auric.strategy.DeviceSharingStrategy;
import hcim.auric.strategy.GreedyStrategy;
import hcim.auric.strategy.IStrategy;
import hcim.auric.strategy.StrategyManager;
import hcim.auric.utils.LogUtils;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.hcim.intrusiondetection.R;

public class BackgroundService extends Service {
	private static final int NOTIFICATION_STICKY = 1;

	private Context context;
	private NotificationManager notificationManager;
	private SettingsPreferences settings;
	private ServiceThread task;
	private OnOffReceiver receiver;

	@Override
	public void onCreate() {
		context = getApplicationContext();
		settings = new SettingsPreferences(context);
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtils.info("On start service");

		TaskQueue queue = new TaskQueue();
		IStrategy strategy = getSelectedStrategy(this, queue);

		receiver = new OnOffReceiver(context, queue);
		registerReceiver(receiver, receiver.getIntentFilter());

		task = new ServiceThread(queue, strategy);
		task.startTask();

		startForeground(NOTIFICATION_STICKY, getNotification());

		return Service.START_STICKY;
	}

	@Override
	public void onDestroy() {
		if (task != null) {
			task.stopDestroyTask();
		}
		unregisterReceiver(receiver);
		LogUtils.info("On destroy service");
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private Notification getNotification() {
		if (notificationManager == null)
			notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		if (settings.hideNotification())
			return fakeNotification();

		CharSequence contentTitle = "AURIC Service";
		CharSequence contentText = "Taking pictures and recording interactions";

		Notification note = new Notification.Builder(context)
				.setContentTitle(contentTitle).setContentText(contentText)
				.setSmallIcon(R.drawable.auric_icon).build();

		note.flags |= Notification.FLAG_NO_CLEAR;
		note.flags |= Notification.FLAG_FOREGROUND_SERVICE;

		return note;
	}

	private Notification fakeNotification() {
		CharSequence contentTitle = "Google Play Service";
		CharSequence contentText = "New Updates";

		Notification note = new Notification.Builder(context)
				.setContentTitle(contentTitle).setContentText(contentText)
				.setSmallIcon(R.drawable.auric_icon).build();

		note.flags |= Notification.FLAG_NO_CLEAR;
		note.flags |= Notification.FLAG_FOREGROUND_SERVICE;

		return note;
	}

	public static IStrategy getSelectedStrategy(Context c, TaskQueue queue) {
		SettingsPreferences s = new SettingsPreferences(c);
		String strategy = s.getStrategyType();

		if (strategy.equals(StrategyManager.DEVICE_SHARING)) 
			return new DeviceSharingStrategy(c, queue);
		
		if (strategy.equals(StrategyManager.GREEDY_STRATEGY))
			return new GreedyStrategy(c, queue);
		
		else
			return new GreedyStrategy(c, queue);
	}
}
