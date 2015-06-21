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

public class ScreencastStrategy extends AbstractStrategy {
	private DetectorByFaceRecognition detector;
	private boolean first;

	public ScreencastStrategy(Context context, AuditQueue queue, int n) {
		super(context);
		detector = new DetectorByFaceRecognition(context, queue, n);
	}

	@Override
	public IDetector getDetector() {
		return detector;
	}

	public void actionOn() {
		if (currentSession == null) {
			detector.start();
			currentSession = new Session(recorder.type());
			currentIntrusion = new Intrusion();
			recorder.start(currentIntrusion.getID());
			first = true;
		}
	}

	public void actionOff() {
		if (first) {// intrusion result didn't appear
			detector.stop();
			recorder.stop();

			currentSession = null;
			currentIntrusion = null;
			sessionsDB.deletePicturesUnknownIntrusion();
		}
		if (currentSession != null) {
			detector.stop();
			recorder.stop();

			currentSession.addIntrusion(currentIntrusion);
			sessionsDB.insertIntrusionData(currentIntrusion);
			sessionsDB.insertSession(currentSession);
			currentSession = null;
			sessionsDB.deletePicturesUnknownIntrusion();
		}
	}

	@Override
	public void actionNewData(byte[] data) {
		Bitmap original = Converter.decodeCameraDataToBitmap(data);
		Bitmap small = Converter.decodeCameraDataToSmallBitmap(data);

		RecognitionResult result = detector.newData(original);

		if (result != null) {
			sessionsDB.insertPictureUnknownIntrusion(small, result);
		}
	}

	@Override
	public void actionResult(boolean intrusion) {
		if (first) { // first detection result
			first = false;
		}
		
		sessionsDB.updatePicturesUnknownIntrusion(currentIntrusion.getID());
		
		if (intrusion) {
			notifier.notifyUser();
			currentSession.flagAsIntrusion();
		} else {
			currentIntrusion.markAsFalse();
		}
	}
}
