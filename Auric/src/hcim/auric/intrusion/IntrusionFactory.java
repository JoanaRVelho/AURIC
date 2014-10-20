package hcim.auric.intrusion;


public class IntrusionFactory {

	public static Intrusion createIntrusion(String id, String date, String time, int tag) {
		Intrusion i = new Intrusion();
		
		i.setDate(date);
		i.setID(id);
		i.setTime(time);
		i.setTag(tag);
		
		return i;
	}

}
