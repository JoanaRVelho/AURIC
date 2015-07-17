package hcim.auric.strategy;

import hcim.auric.detector.IDetector;
import hcim.auric.record.IRecorder;

/**
 * Interface IStrategy represents an operating strategy that coordinates
 * intrusion detection results and recordings.
 * 
 * @author Joana Velho
 * 
 */
public interface IStrategy {
	public IRecorder getRecorder();

	public IDetector getDetector();

	public void actionOn();

	public void actionOff();

	public void actionResult(boolean intrusion);
}
