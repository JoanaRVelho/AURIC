package hcim.auric.detector;

/**
 * Interface that represents an Intrusion Detector
 * 
 * @author Joana Velho
 * 
 */
public interface IDetector {

	/**
	 * This method must be called to start detection
	 */
	public void start();

	/**
	 * This method must be called to stop detection
	 */
	public void stop();

	/**
	 * 
	 * @return type of detector
	 */
	public String type();

	/**
	 * destroys the detector completely
	 */
	public void destroy();
}
