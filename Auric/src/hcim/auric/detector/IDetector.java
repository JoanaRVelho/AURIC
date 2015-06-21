package hcim.auric.detector;

public interface IDetector {

	/**
	 * This method must be called to start the detection
	 */
	public void start();

	/**
	 * This method must be called to stop the detection
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
