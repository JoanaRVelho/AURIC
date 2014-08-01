package hcim.auric.audit;

import hcim.auric.intrusion.Intrusion;
import hcim.auric.periodic.AuricTimerTask;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

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
		Log.d("SCREEN", "start task");
		TaskMessage taskMessage;
		String id;
		while (true) {
			if (!queue.isEmpty()) {
				try {
					taskMessage = queue.take();
					id = taskMessage.getID();

					Log.d("SCREEN", "task=" + id);

					if (id.equals(ACTION_STOP)) {
						screenOff = true;
						actionStop();
					}
					else if (id.equals(ACTION_START)) {
						screenOff = false;
						actionStart(taskMessage.getTimestamp()+"");
					}
					else if (id.equals(ACTION_NEW_PICTURE)) {
						actionNewPicture(taskMessage.getPic());
					}

				} catch (InterruptedException e) {
					Log.e("SCREEN", e.getMessage());
				}

			}
		}
	}

	@SuppressLint("SimpleDateFormat")
	public void actionStart(String timestamp) {
		Log.d("SCREEN", timestamp);
		long timestampLong = Long.valueOf(timestamp);
		Date d = new Date(timestampLong);
		Format f = new SimpleDateFormat("dd-MMMM-yyyy HH:mm:ss");
	
		String id = f.format(d).toString();
		if(id.charAt(0) == '0')
			id = id.substring(1);
		String[] array = id.split(" ");
		
		Log.d("SCREEN","WifiTask - id="+id + " data="+array[0]+ " horas="+ array[1]);
	
		currentIntrusion = new Intrusion(id, array[0], array[1], timestamp);
		
		timerTask = new AuricTimerTask(this.camera);
		timer = new Timer();
		timer.scheduleAtFixedRate(timerTask, 0, PERIOD);
	
		notifier.notifyUser();
	}

	public void actionStop() {
		if (timer != null) {
			timer.cancel();
			timer = null;
			Log.d("SCREEN", "STOP periodic thread");
		}
		timerTask = null;
	
		intrusionsDB.addIntrusion(currentIntrusion);
		currentIntrusion = null;
	
		if (notifier != null) {
			notifier.cancelNotification();
		}
	}

	public void actionNewPicture(Bitmap bm) {
		if(currentIntrusion != null){
			currentIntrusion.addImage(bm);
		}
	}
}
