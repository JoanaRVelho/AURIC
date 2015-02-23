package hcim.auric.strategy;

import hcim.auric.detector.IDetector;
import hcim.auric.record.IRecorder;
import android.graphics.Bitmap;

public interface IStrategy {
	public final String TAG = "AURIC";
	
	public IRecorder getRecorder();
	
	public IDetector getDetector();

	public void actionOn();

	public void actionOff();

	public void actionNewPicture(Bitmap bmp);

	public void actionResult(boolean intrusion);

}
