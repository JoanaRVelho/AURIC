package hcim.auric.audit;

import hcim.auric.intrusion.Intrusion;
import hcim.auric.recognition.RecognitionResult;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

public class AuditTaskWithDeviceSharing extends AuditTask {

	public AuditTaskWithDeviceSharing(Context c) {
		super(c);
	}

	@Override
	public void actionOn() {
		if (!startLog) {
			startTimerTask(false); //startTimeTask without delay
		}
	}

	@Override
	public void actionOff() {
		stopTimerTask();

		if (startLog) {
			log.stopLogging();
			intrusionsDB.insertIntrusionData(currentIntrusion);
			Log.d(TAG, "AuditTask - stop logging");

			currentIntrusion = null;
			notifier.cancelNotification();
		}

		startLog = false;
	}

	@Override
	public void actionNewPicture(Bitmap capturedFace) {
		RecognitionResult result = recognizer.recognizePicture(capturedFace);
		boolean intrusion = !result.isFaceRecognized();

		Log.d(TAG, "Audit Task DS - intrusion=" + intrusion);

		if (intrusion) {
			if (!startLog) { // iniciar auditoria
				startLog = true;

				Log.d(TAG, "Audit Task DS - new intrusion, start audit");
				currentIntrusion = new Intrusion(log.type());
				
				intrusionsDB.insertPictureOfTheIntruder(
						currentIntrusion.getID(), capturedFace);
				
				log.startLogging(currentIntrusion.getID());
				Log.d(TAG, "Audit Task DS - start logging");

				notifier.notifyUser();

				
			} else { // continuar auditoria
				Log.d(TAG, "Audit Task DS - continue");
				if (currentIntrusion == null) {
					return;
				}
				
				intrusionsDB.insertPictureOfTheIntruder(
						currentIntrusion.getID(), capturedFace);
			}
		} else {
			if (startLog) { // parar auditoria
				Log.d(TAG, "Audit Task DS - stop audit");

				if (currentIntrusion != null) {
					log.stopLogging();
					Log.d(TAG, "Audit Task DS - stop logging");
					
					intrusionsDB.insertIntrusionData(currentIntrusion);
					currentIntrusion = null;
				}
				startLog = false;

				notifier.cancelNotification();
			}
		}
	}

}
