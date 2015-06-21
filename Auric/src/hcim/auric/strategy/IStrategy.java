package hcim.auric.strategy;

import hcim.auric.detector.IDetector;
import hcim.auric.record.IRecorder;

public interface IStrategy {
	public IRecorder getRecorder();
	
	public IDetector getDetector();

	public void actionOn();

	public void actionOff();

	public void actionNewData(byte[] data);

	public void actionResult(boolean intrusion);

}
