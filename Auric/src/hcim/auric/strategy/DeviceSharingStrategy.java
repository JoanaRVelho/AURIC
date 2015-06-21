package hcim.auric.strategy;

import hcim.auric.audit.AuditQueue;
import hcim.auric.detector.DetectorByFaceRecognition;
import hcim.auric.detector.IDetector;
import hcim.auric.intrusion.Intrusion;
import hcim.auric.intrusion.Session;
import hcim.auric.recognition.RecognitionResult;
import hcim.auric.utils.Converter;
import android.content.Context;
import android.graphics.Bitmap;

/**
 * During a session, this task is always checking who the operator is and
 * records only if an intrusion is detected.
 * 
 * @author Joana Velho
 * 
 */
public class DeviceSharingStrategy extends AbstractStrategy {
	protected long timestamp;
	private DetectorByFaceRecognition detector;
	private boolean first;

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

		currentSession = new Session(getRecorder().type());
		currentIntrusion = new Intrusion();
		first = true;
		recorder.start(currentIntrusion.getID());
	}

	@Override
	public void actionOff() {
		if (currentIntrusion != null) {
			recorder.stop();
			detector.stop();

			sessionsDB.insertIntrusionData(currentIntrusion);
			currentIntrusion = null;

			notifier.cancelNotification();
		}

		if (!currentSession.isEmpty()) {
			sessionsDB.insertSession(currentSession);
		}
		currentSession = null;
	}

	@Override
	public void actionResult(boolean intrusion) {
		// FIXME
		if (intrusion) {
			notifier.notifyUser();

			if (first) {
				currentSession.addIntrusion(currentIntrusion);
				currentSession.flagAsIntrusion();
				notifier.notifyUser();
				sessionsDB.updatePicturesUnknownIntrusion(currentIntrusion
						.getID());
			} else {
				if (currentIntrusion == null) {

				}
			}
		} else { // isn't an intrusion
			notifier.cancelNotification();

			if (currentIntrusion != null) { // first
				sessionsDB.insertIntrusionData(currentIntrusion);
				sessionsDB.deletePicturesUnknownIntrusion();
				currentIntrusion = null;
			}
		}

		timestamp = System.currentTimeMillis();
	}

	@Override
	public void actionNewData(byte[] data) {
		if (data != null) {
			Bitmap original = Converter.decodeCameraDataToBitmap(data);
			Bitmap small = Converter.decodeCameraDataToSmallBitmap(data);

			RecognitionResult result = detector.newData(original);
			sessionsDB.insertPictureUnknownIntrusion(small, result);
		}
	}

}
