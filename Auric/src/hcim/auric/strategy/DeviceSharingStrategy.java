package hcim.auric.strategy;

import hcim.auric.audit.AuditQueue;
import hcim.auric.detector.DetectorByFaceRecognition;
import hcim.auric.detector.IDetector;
import hcim.auric.intrusion.Intrusion;
import hcim.auric.intrusion.Session;
import hcim.auric.recognition.RecognitionResult;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

/**
 * During a session, this task is always checking who the operator is and
 * records only if an intrusion is detected.
 * 
 * @author Joana Velho
 * 
 */
public class DeviceSharingStrategy extends FaceRecognitionStrategy {
	protected long timestamp;
	private DetectorByFaceRecognition detector;

	public DeviceSharingStrategy(Context context, AuditQueue queue, int n) {
		super(context);

		detector = new DetectorByFaceRecognition(context, queue, n);
	}

	@Override
	public IDetector getDetector() {
		return detector;
	}

	@Override
	public void actionOn() {
		detector.start();

		currentSession = new Session();
		timestamp = System.currentTimeMillis();
		currentIntrusion = null;

		recorder.start(currentIntrusion.getID());
	}

	@Override
	public void actionOff() {
		if (currentIntrusion != null) {
			recorder.stop();
			detector.stop();
			Log.d(TAG, "AuditTask - stop logging");

			intrusionsDB.insertIntrusionData(currentIntrusion);
			currentIntrusion = null;

			notifier.cancelNotification();
		}

		if (currentSession.hasIntrusions()) {
			sessionsDB.insertSession(currentSession);
		}
		currentSession = null;
	}

	@Override
	public void actionResult(boolean intrusion) {
		String recorderType = recorder.type();

		if (intrusion) {
			notifier.notifyUser();

			if (currentIntrusion == null) {
				currentIntrusion = new Intrusion(recorderType, timestamp);
				recorder.start(currentIntrusion.getID());
				currentSession.addInteraction(currentIntrusion);
				currentSession.setTagIntrusion(true);
			}
			intrusionsDB.updatePicturesUnknownIntrusion(currentIntrusion
					.getID());

		} else { // isn't an intrusion
			notifier.cancelNotification();

			if (currentIntrusion != null) { // first
				intrusionsDB.insertIntrusionData(currentIntrusion);
				intrusionsDB.deletePicturesUnknownIntrusion();
				currentIntrusion = null;
			}
		}

		timestamp = System.currentTimeMillis();
	}

	@Override
	public void actionNewPicture(Bitmap bmp) {
		if (bmp != null) {
			RecognitionResult result = detector.newData(bmp);
			intrusionsDB.insertPictureUnknownIntrusion(bmp, result);
		} else {
			Log.e(TAG, "DeviceSharingStrategy - Bitmap is null");
		}
	}

}
