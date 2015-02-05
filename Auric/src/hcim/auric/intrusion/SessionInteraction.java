package hcim.auric.intrusion;

public class SessionInteraction {

	private String interactionID;

	public SessionInteraction(Intrusion i) {
		interactionID = i.getID();
	}

	public String getInteractionID() {
		return interactionID;
	}

}