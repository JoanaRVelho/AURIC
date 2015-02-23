package hcim.auric.intrusion;

import hcim.auric.database.intrusions.IntrusionsDatabase;
import hcim.auric.utils.CalendarManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents an interaction with the device since the screen was ON
 * until gets OFF
 * 
 * @author Joana Velho
 *
 */
public class Session {
	protected String id;
	protected String date;
	protected boolean isIntrusion;
	protected List<String> interactions;
	protected String time;

	public Session() {
		long timestamp = System.currentTimeMillis();

		id = Long.toString(timestamp);
		date = CalendarManager.getDate(timestamp);
		time = CalendarManager.getTime(timestamp);
		interactions = new ArrayList<String>();
	}

	public Session(String id, String date, String time,
			List<String> interactions, boolean tag) {
		this.id = id;
		this.date = date;
		this.time = time;
		this.interactions = interactions;
		this.isIntrusion = tag;
	}

	public String getID() {
		return id;
	}

	public List<String> getInteractionsIDs() {
		return interactions;
	}

	public void addInteraction(Intrusion i) {
		this.interactions.add(i.getID());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("Session -> ");
		builder.append("date=" + date + ", time=" + time + ", id=" + id);

		builder.append(", [ ");
		for (String s : interactions) {
			builder.append(s);
			builder.append(" ");
		}
		builder.append("]");

		return builder.toString();
	}

	public String getDate() {
		return date;
	}

	public String getTime() {
		return time;
	}

	public List<Intrusion> getIntrusions(IntrusionsDatabase db) {
		List<Intrusion> result = new ArrayList<Intrusion>();

		for (String s : interactions) {
			result.add(db.getIntrusion(s));
		}
		return result;
	}

	public boolean hasIntrusions() {
		return !interactions.isEmpty();
	}

	public boolean isTaggedAsIntrusion() {
		return isIntrusion;
	}

	public void setTagIntrusion(boolean isIntrusion) {
		this.isIntrusion = isIntrusion;
	}
}
