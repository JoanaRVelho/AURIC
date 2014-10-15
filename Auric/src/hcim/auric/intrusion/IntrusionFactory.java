package hcim.auric.intrusion;


public class IntrusionFactory {

	public static Intrusion createIntrusion(String id, String date, String time) {
		Intrusion i = new Intrusion();
		
		i.setDate(date);
		i.setID(id);
		i.setTime(time);
		
		return i;
	}

}
