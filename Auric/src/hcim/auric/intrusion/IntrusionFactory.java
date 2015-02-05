package hcim.auric.intrusion;

public class IntrusionFactory {

	public static Intrusion createIntrusion(String id, String date,
			String time, int tag, String log) {
		Intrusion i;

		if (isFalseIntrusion(tag)) {
			i = new Interaction();
		} else {
			i = new Intrusion();
		}

		i.setDate(date);
		i.setID(id);
		i.setTime(time);
		i.setTag(tag);
		i.setLogType(log);

		return i;
	}

	private static boolean isFalseIntrusion(int tag) {
		return tag == Interaction.FALSE_INTRUSION;
	}
}
