package hcim.auric.audit;

import hcim.auric.intrusion.Intrusion;
import hcim.auric.recognition.FaceRecognition;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

public class AuditTask extends AbstractAuditTask {
	public static final String ACTION_OFF = "OFF";
	public static final String ACTION_ON = "ON";

	protected FaceRecognition recognizer;
	protected boolean startLog;

	public AuditTask(Context c) {
		super(c);

		recognizer = FaceRecognition.getInstance(context);
		startLog = false;
	}

	@Override
	public void run() {
		Log.d(TAG, "AuditTask - start");

		TaskMessage taskMessage;
		String id;
		while (true) {

			if (!queue.isEmpty()) {
				try {
					taskMessage = queue.take();
					id = taskMessage.getID();

					Log.d(TAG, "AuditTask - task = " + id);

					if (id.equals(ACTION_OFF)) {
						screenOff = true;
						actionOff();
					}
					if (id.equals(ACTION_ON)) {
						screenOff = false;
						actionOn();
					}
					if (id.equals(ACTION_NEW_PICTURE)) {
						actionNewPicture(taskMessage.getPic());
					}

				} catch (InterruptedException e) {
					Log.e(TAG, "Audit Task - " + e.getMessage());
				}

			}
		}
	}

	public void actionOn() {
		if (!startLog) {
			camera.takePicture();
		}
	}

	public void actionOff() {
		if (startLog) {
			currentIntrusion.stopLogging();
			Log.d(TAG, "AuditTask - stop logging");

			stopTimerTask();

			currentIntrusion = null;
			notifier.cancelNotification();
		}

		startLog = false;
	}

	public void actionNewPicture(Bitmap capturedFace) {
		boolean intrusion = !recognizer.recognizePicture(capturedFace);

		Log.d(TAG, "AuditTask - intrusion=" + intrusion);

		if (intrusion) {
			if (!startLog) { // iniciar auditoria
				startLog = true;

				Log.d(TAG, "AuditTask - new intrusion, start audit");
				currentIntrusion = new Intrusion(context);
				
				intrusionsDB.insertIntrusionData(currentIntrusion);
				intrusionsDB.insertPictureOfTheIntruder(
						currentIntrusion.getID(), capturedFace);
				
				currentIntrusion.startLogging();
				Log.d(TAG, "AuditTask - start logging");

				notifier.notifyUser();

				startTimerTask(true);
				Log.d(TAG, "AuditTask - start timer task");
			} else { // continuar auditoria
				Log.d(TAG, "AuditTask - continue");
				if (currentIntrusion == null) {
					return;
				}
				intrusionsDB.insertPictureOfTheIntruder(
						currentIntrusion.getID(), capturedFace);
			}
		} else {
			if (startLog) { // parar auditoria
				Log.d(TAG, "AuditTask - stop audit");

				stopTimerTask();

				if (currentIntrusion != null) {
					currentIntrusion.stopLogging();
					intrusionsDB.deleteIntrusion(currentIntrusion.getID());
					currentIntrusion = null;
				}
				startLog = false;

				Log.d(TAG, "AuditTask - stop logging");

				notifier.cancelNotification();
			}
		}
	}
}
