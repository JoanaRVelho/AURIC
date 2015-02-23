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
 * During a session, this task assumes that the device is being attacked and
 * checks who the operator is until the opposite is confirmed, while recording
 * he's interactions. If is not considered an intrusion it deletes the
 * associated data.
 * 
 * @author Joana Velho
 * 
 */
public class SimpleStrategy extends FaceRecognitionStrategy {
	protected boolean first;
	private DetectorByFaceRecognition detector;

	public SimpleStrategy(Context context, AuditQueue queue, int n) {
		super(context);

		detector = new DetectorByFaceRecognition(context, queue, n);
	}

	@Override
	public void actionOn() {
		detector.start();

		currentIntrusion = new Intrusion(recorder.type());
		recorder.start(currentIntrusion.getID());

		currentSession = new Session();
		currentSession.addInteraction(currentIntrusion);
		currentSession.setTagIntrusion(true);

		first = true;
	}

	@Override
	public void actionOff() {
		if (currentIntrusion != null) {
			recorder.stop();
			detector.stop();
			Log.d(TAG, "AuditTask - stop logging");

			intrusionsDB.insertIntrusionData(currentIntrusion);
			sessionsDB.insertSession(currentSession);

			currentIntrusion = null;
			currentSession = null;

			notifier.cancelNotification();
		}
	}

	@Override
	public void actionResult(boolean intrusion) {
		if (intrusion) {
			if (first) {
				first = false;
				notifier.notifyUser();
			}
		} else {
			detector.stop();
			notifier.cancelNotification();
			intrusionsDB.deletePicturesOfTheIntruder(this.currentIntrusion
					.getID());
			recorder.stop();

			this.currentIntrusion = null;
			currentSession = null;
		}

	}

	@Override
	public IDetector getDetector() {
		return detector;
	}

	@Override
	public void actionNewPicture(Bitmap bmp) {
		RecognitionResult result = detector.newData(bmp);
		intrusionsDB.insertPictureOfTheIntruder(currentIntrusion.getID(), bmp,
				result);
	}
}
