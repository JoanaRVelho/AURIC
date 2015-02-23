package hcim.auric.strategy;

import hcim.auric.audit.AuditQueue;
import hcim.auric.detector.AppDetector;
import hcim.auric.detector.DetectorByFaceRecognition;
import hcim.auric.detector.IDetector;
import hcim.auric.intrusion.Intrusion;
import hcim.auric.recognition.RecognitionResult;
import android.content.Context;
import android.graphics.Bitmap;

public class AppLaunchedStrategy extends AbstractStrategy {

	private DetectorByFaceRecognition detector;
	private boolean recording;
	private AppDetector appDetector;
	private long timestamp;

	public AppLaunchedStrategy(Context context, AuditQueue queue, int n) {
		super(context);
		this.detector = new DetectorByFaceRecognition(context, queue, n);
		this.appDetector = new AppDetector(context, queue);
	}

	@Override
	public IDetector getDetector() {
		return detector;
	}

	@Override
	public void actionOn() {
	}

	@Override
	public void actionOff() {
		if (recording) {
			recorder.stop();
		}
		appDetector.destroy();
	}

	@Override
	public void actionNewPicture(Bitmap bmp) {
		if (recording) {
			RecognitionResult result = detector.newData(bmp);
			intrusionsDB.insertPictureUnknownIntrusion(bmp, result);
		}
	}

	@Override
	public void actionResult(boolean intrusion) {
		if (!intrusion) {
			recording = false;
			intrusionsDB.deletePicturesUnknownIntrusion();
		}
	}

	public void actionAppLaunched(boolean targetApp) {
		if (targetApp) {
			recording = true;
			timestamp = System.currentTimeMillis();
		} else {
			if (recording) {
				recording = false;
				Intrusion i = new Intrusion(recorder.type(), timestamp);
				intrusionsDB.updatePicturesUnknownIntrusion(i.getID());
				intrusionsDB.insertIntrusionData(i);
			}
		}

	}

}
