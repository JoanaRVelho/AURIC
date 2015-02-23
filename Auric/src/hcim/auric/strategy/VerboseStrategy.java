package hcim.auric.strategy;

import hcim.auric.audit.AuditQueue;
import hcim.auric.detector.DetectorByFaceRecognition;
import hcim.auric.detector.IDetector;
import hcim.auric.intrusion.Interaction;
import hcim.auric.intrusion.Intrusion;
import hcim.auric.intrusion.Session;
import hcim.auric.recognition.RecognitionResult;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

public class VerboseStrategy extends FaceRecognitionStrategy {
	private DetectorByFaceRecognition detector;
	private long timestamp;

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
			currentSession = new Session();
			timestamp = System.currentTimeMillis();
			currentIntrusion = null;
			recorder.start(timestamp + "");

			Log.d(TAG, currentSession.toString());
		}
	}

	public void actionOff() {
		if (currentSession != null) {
			stopInt();

			detector.stop();
			sessionsDB.insertSession(currentSession);
			Log.d(TAG, currentSession.toString());
			currentSession = null;
			intrusionsDB.deletePicturesUnknownIntrusion();
		}
	}

	@Override
	public void actionNewPicture(Bitmap bmp) {
		RecognitionResult result = detector.newData(bmp);
		intrusionsDB.insertPictureUnknownIntrusion(bmp, result);
		Log.d(TAG, result.toString());
	}

	@Override
	public void actionResult(boolean intrusion) {
		Log.d(TAG, "intrusion=" + intrusion);

		if (currentIntrusion == null) { // first detection result
			if (intrusion) {
				currentIntrusion = new Intrusion(recorder.type(), timestamp);
				notifier.notifyUser();
				currentSession.setTagIntrusion(true);
			} else {
				currentIntrusion = new Interaction(recorder.type(), timestamp);
				currentSession.setTagIntrusion(false);
			}

			currentSession.addInteraction(currentIntrusion);
			intrusionsDB.updatePicturesUnknownIntrusion(currentIntrusion
					.getID());
		} else {
			boolean xor = intrusion
					^ (currentIntrusion.getTag() == Interaction.FALSE_INTRUSION);
			if (!xor) {
				stopInt();
				startInt(intrusion);
			} else {
				intrusionsDB.updatePicturesUnknownIntrusion(currentIntrusion
						.getID());
			}
		}

	}

	private void startInt(boolean intrusion) {
		if (intrusion) {
			currentIntrusion = new Intrusion(recorder.type());
			notifier.notifyUser();
		} else {
			currentIntrusion = new Interaction(recorder.type());
		}

		recorder.start(currentIntrusion.getID());
		intrusionsDB.updatePicturesUnknownIntrusion(currentIntrusion.getID());
		currentSession.addInteraction(currentIntrusion);
	}

	private void stopInt() {
		recorder.stop();
		intrusionsDB.insertIntrusionData(currentIntrusion);

		if (!(currentIntrusion instanceof Interaction)) {
			notifier.cancelNotification();
		}
		currentIntrusion = null;
	}

}
