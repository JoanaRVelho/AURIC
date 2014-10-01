package hcim.auric.audit;

import hcim.auric.intrusion.Intrusion;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

public class AuditTaskWithoutDeviceSharing extends AuditTask {

	public AuditTaskWithoutDeviceSharing(Context c) {
		super(c);
	}

	@Override
	public void actionOn() {
		if (!startLog) {
			camera.takePicture();
		}
	}

	@Override
	public void actionOff() {
		if (startLog) {
			currentIntrusion.stopLogging();
			Log.d(TAG, "AuditTask - stop logging");

			stopTimerTask();

			intrusionsDB.addIntrusion(currentIntrusion);
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
				currentIntrusion = new Intrusion(context);
				currentIntrusion.addImage(capturedFace);
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
				currentIntrusion.addImage(capturedFace);
			}
		} else {
			if (startLog) { // parar auditoria
				Log.d(TAG, "AuditTask - stop audit");

				stopTimerTask();

				if (currentIntrusion != null) {
					currentIntrusion.stopLogging();
					intrusionsDB.removeIntrusion(currentIntrusion);
					currentIntrusion = null;
				}
				startLog = false;

				Log.d(TAG, "AuditTask - stop logging");

				notifier.cancelNotification();
			}
		}
	}

}
