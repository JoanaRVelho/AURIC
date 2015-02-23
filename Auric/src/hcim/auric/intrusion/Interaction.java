package hcim.auric.intrusion;

public class Interaction extends Intrusion {
	public static final int FALSE_INTRUSION = 4;

	public Interaction() {
		super();
	}
	public Interaction(String recorderType){
		super(recorderType);
		
		tag = FALSE_INTRUSION;
	}

	public Interaction(String recorderType, long timestamp){
		super(recorderType, timestamp);
		
		tag = FALSE_INTRUSION;
	}
	
	public boolean isChecked() {
		return tag == FALSE_INTRUSION;
	}
}
