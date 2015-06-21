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

public class VerboseStrategy extends AbstractStrategy {
	private DetectorByFaceRecognition detector;
	private boolean first;

	public VerboseStrategy(Context context, AuditQueue queue, int n) {
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
			currentSession = null;
			currentIntrusion = null;
			sessionsDB.deletePicturesUnknownIntrusion();
		}
		if (currentSession != null) {
			stopInt();

			detector.stop();
			sessionsDB.insertSession(currentSession);
//			LogUtils.debug(currentSession.toString());
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
//			LogUtils.debug(result.toString());
		}
	}

	@Override
	public void actionResult(boolean intrusion) {
//		LogUtils.debug("intrusion=" + intrusion);

		if (first) { // first detection result
			first = false;
			if (intrusion) {
				notifier.notifyUser();
				currentSession.flagAsIntrusion();
			} else {
				currentIntrusion.markAsFalse();
			}

			currentSession.addIntrusion(currentIntrusion);
			sessionsDB.updatePicturesUnknownIntrusion(currentIntrusion
					.getID());
		} else {
			boolean xor = intrusion ^ currentIntrusion.isFalseIntrusion();
			if (!xor) {
				stopInt();
				startInt(intrusion);
			} else {
				sessionsDB.updatePicturesUnknownIntrusion(currentIntrusion
						.getID());
			}
		}

	}

	private void startInt(boolean intrusion) {
		currentIntrusion = new Intrusion();
		if (intrusion) {
			notifier.notifyUser();
			currentSession.flagAsIntrusion();
		} else {
			currentIntrusion.markAsFalse();
		}

		recorder.start(currentIntrusion.getID());
		sessionsDB.updatePicturesUnknownIntrusion(currentIntrusion.getID());
		currentSession.addIntrusion(currentIntrusion);

//		LogUtils.debug("new intrusion = " + currentIntrusion.getID());
	}

	private void stopInt() {
		recorder.stop();

		if (currentIntrusion != null) {
			sessionsDB.insertIntrusionData(currentIntrusion);

			if (!currentIntrusion.isFalseIntrusion()) {
				notifier.cancelNotification();
			}
		}
		currentIntrusion = null;
	}
}
