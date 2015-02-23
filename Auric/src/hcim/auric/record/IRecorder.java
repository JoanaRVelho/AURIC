package hcim.auric.record;

public interface IRecorder {
	public void start(String intrusionID);

	public void stop();

	public String type();
	
	public void destroy();
}
