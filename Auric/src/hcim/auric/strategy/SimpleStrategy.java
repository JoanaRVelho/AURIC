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
 * During a session, this task assumes that the device is being attacked and
 * checks who the operator is until the opposite is confirmed, while recording
 * he's interactions. If is not considered an intrusion it deletes the
 * associated data.
 * 
 * @author Joana Velho
 * 
 */
public class SimpleStrategy extends AbstractStrategy {
	protected boolean first;
	private DetectorByFaceRecognition detector;

	public SimpleStrategy(Context context, AuditQueue queue, int n) {
		super(context);

		detector = new DetectorByFaceRecognition(context, queue, n);
	}

	@Override
	public void actionOn() {
		detector.start();

		currentSession = new Session(recorder.type());
		currentIntrusion = new Intrusion();
		recorder.start(currentIntrusion.getID());
		
		currentSession.addIntrusion(currentIntrusion);
		currentSession.flagAsIntrusion();

		first = true;
	}

	@Override
	public void actionOff() {
		if (currentIntrusion != null) {
			recorder.stop();
			detector.stop();

			sessionsDB.insertIntrusionData(currentIntrusion);
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
			sessionsDB.deletePicturesOfTheIntruder(this.currentIntrusion
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
	public void actionNewData(byte[] data) {

		Bitmap original = Converter.decodeCameraDataToBitmap(data);
		Bitmap small = Converter.decodeCameraDataToSmallBitmap(data);

		RecognitionResult result = detector.newData(original);
		sessionsDB.insertPictureOfTheIntruder(currentIntrusion.getID(), small,
				result);
	}
}
