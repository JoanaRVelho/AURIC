package hcim.auric.audit;

import hcim.auric.intrusion.Intrusion;
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
			startTimerTask(false); //startTimeTask no delay
		}
	}

	@Override
	public void actionOff() {
		stopTimerTask();

		if (startLog) {
			log.stopLogging();
			//currentIntrusion.stopLogging();
			intrusionsDB.insertIntrusionData(currentIntrusion);
			Log.d(TAG, "AuditTask - stop logging");

			currentIntrusion = null;
			notifier.cancelNotification();
		}

		startLog = false;
	}

	@Override
	public void actionNewPicture(Bitmap capturedFace) {
		boolean intrusion = !recognizer.recognizePicture(capturedFace);

		Log.d(TAG, "AuditTask - intrusion=" + intrusion);

		if (intrusion) {
			if (!startLog) { // iniciar auditoria
				startLog = true;

				Log.d(TAG, "AuditTask - new intrusion, start audit");
				currentIntrusion = new Intrusion(log.type());
				
				intrusionsDB.insertPictureOfTheIntruder(
						currentIntrusion.getID(), capturedFace);
				
			//	currentIntrusion.startLogging();
				log.startLogging(currentIntrusion.getID());
				Log.d(TAG, "AuditTask - start logging");

				notifier.notifyUser();

				
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

				if (currentIntrusion != null) {
					log.stopLogging();
					//currentIntrusion.stopLogging();
					Log.d(TAG, "AuditTask - stop logging");
					
					intrusionsDB.insertIntrusionData(currentIntrusion);
					currentIntrusion = null;
				}
				startLog = false;

				notifier.cancelNotification();
			}
		}
	}

}
