package hcim.auric.strategy;

import hcim.auric.audit.AuditQueue;
import hcim.auric.detector.IDetector;
import hcim.auric.detector.WifiDetector;
import hcim.auric.recognition.RecognitionResult;
import android.content.Context;
import android.graphics.Bitmap;

public class WifiStrategy extends AbstractStrategy {

	private WifiDetector detector;

	public WifiStrategy(Context context, AuditQueue queue) {
		super(context);

		detector = new WifiDetector(context, queue);
	}

	@Override
	public IDetector getDetector() {
		return detector;
	}

	@Override
	public void actionOn() {
		// TODO
	}

	@Override
	public void actionOff() {
		// TODO Auto-generated method stub

	}

	@Override
	public void actionResult(boolean intrusion) {
		// TODO Auto-generated method stub

	}

	@Override
	public void actionNewPicture(Bitmap bmp) {
		intrusionsDB.insertPictureOfTheIntruder(currentIntrusion.getID(), bmp,
				new RecognitionResult(true, false, "Unknown", -1));
	}

}
