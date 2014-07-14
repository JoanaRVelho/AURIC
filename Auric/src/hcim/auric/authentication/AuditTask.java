package hcim.auric.authentication;

import hcim.auric.camera.CameraManager;
import hcim.auric.camera.FrontPictureCallback;
import hcim.auric.database.IntrusionsDatabase;
import hcim.auric.intrusion.Intrusion;
import hcim.auric.recognition.FaceRecognition;

import java.util.concurrent.LinkedBlockingQueue;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

public class AuditTask extends Thread {

	public static final String ACTION_OFF = "OFF";
	public static final String ACTION_ON = "ON";
	public static final String ACTION_INTRUSION_DETECTION = "DETECT";

	private boolean startLog;
	private CameraManager camera;
	private Intrusion currentIntrusion;
	private FaceRecognition recognizer;
	private PeriodicAuthentication periodicThread;

	private boolean screenOff;

	private Notifier notifier;

	private LinkedBlockingQueue<TaskMessage> queue;

	public AuditTask(Context c) {
		Log.d("SCREEN", "start task");
		queue = new LinkedBlockingQueue<TaskMessage>();

		startLog = false;
		currentIntrusion = null;
		camera = new CameraManager(new FrontPictureCallback(this));
		recognizer = FaceRecognition.getInstance();

		notifier = new Notifier(c);

		screenOff = false;
	}

	@Override
	public void run() {
		TaskMessage taskMessage;
		String id;
		while (true) {


			if (!queue.isEmpty()) {
				try {
					taskMessage = queue.take();
					id = taskMessage.getID();

					Log.d("SCREEN", "task=" + id);

					if (id.equals(ACTION_OFF)) {
						screenOff = true;
						actionOff();
					}
					if (id.equals(ACTION_ON)) {
						screenOff = false;
						actionOn();
					}
					if (id.equals(ACTION_INTRUSION_DETECTION)) {
						actionIntrusionDetected(taskMessage.getPic());
					}

				} catch (InterruptedException e) {
					Log.e("SCREEN", e.getMessage());
				}

			}
		}
	}

	public void actionIntrusionDetected(Bitmap capturedFace) {
		boolean intrusion = !recognizer.recognizePicture(capturedFace);

		Log.d("SCREEN", "intrusion=" + intrusion);

		if (intrusion) {
			if (!startLog) { // iniciar auditoria
				startLog = true;

				Log.d("SCREEN", "iniciar auditoria - new Intrusion");
				currentIntrusion = new Intrusion();
				currentIntrusion.addImage(capturedFace);
				currentIntrusion.startLogging();
				Log.d("SCREEN", "start logging");

				notifier.notifyUser();

				periodicThread = new PeriodicAuthentication(camera);
				periodicThread.start();
				Log.d("SCREEN", "start periodic thread");
			} else { // continuar auditoria
				Log.d("SCREEN", "continuar auditoria");
				if (currentIntrusion == null) {
					return;
				}
				currentIntrusion.addImage(capturedFace);
			}
		} else {
			if (startLog) { // parar auditoria
				Log.d("SCREEN", "parar auditoria");

				if (periodicThread != null) {
					periodicThread.interrupt();
					periodicThread = null;
					Log.d("SCREEN", "STOP periodic thread");
				}

				if (currentIntrusion != null) {
					currentIntrusion.stopLogging();
					IntrusionsDatabase.removeIntrusion(currentIntrusion);
					currentIntrusion = null;
				}
				startLog = false;

				Log.d("SCREEN", "stop logging");

				notifier.cancelNotification();
			}
		}
	}

	public void actionOff() {
		if (startLog) {
			if (periodicThread != null) {
				periodicThread.interrupt();
				periodicThread = null;
				Log.d("SCREEN", "STOP periodic thread");
			}

			currentIntrusion.stopLogging();

			Intrusion i = currentIntrusion;
			currentIntrusion = null;
			IntrusionsDatabase.addIntrusion(i);
			
			notifier.cancelNotification();
			Log.d("SCREEN", "stop logging");
		}

		startLog = false;
	}

	public void actionOn() {
		if (!startLog) {
			Log.d("SCREEN", "take picture");
			camera.takePicture();
		}
	}

	public void addTaskMessage(String task, Bitmap p) {
		// ignore task ACTION INTRUSION DETECTEION after screen is off
		if (screenOff && task == ACTION_INTRUSION_DETECTION) {
			return;
		}

		TaskMessage m = new TaskMessage(task, p);
		queue.add(m);
	}
}
