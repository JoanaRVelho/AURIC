package hcim.auric.intrusion;

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
	protected String time;
	protected List<String> interactions;
	protected String recorderType;
	protected boolean isIntrusion;

	public Session(String recorderType) {
		long timestamp = System.currentTimeMillis();

		id = Long.toString(timestamp);
		date = CalendarManager.getDate(timestamp);
		time = CalendarManager.getTime(timestamp);
		interactions = new ArrayList<String>();
		isIntrusion = false;
		this.recorderType = recorderType;
	}

	public Session(String id, String date, String time, String recorderType,
			List<String> interactions, boolean tag) {
		this.id = id;
		this.date = date;
		this.time = time;
		this.recorderType = recorderType;
		this.interactions = interactions;
		this.isIntrusion = tag;
	}

	public String getID() {
		return id;
	}

	public List<String> getIntrusionIDs() {
		return interactions;
	}

	public void addIntrusion(Intrusion i) {
		this.interactions.add(i.getID());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("Session -> ");
		builder.append("id=" + id + ", date=" + date + ", time=" + time
				+ ", intrusion=" + isIntrusion + ", recorder=" + recorderType);

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

	public String getRecorderType() {
		return recorderType;
	}

	public boolean isEmpty() {
		return interactions.isEmpty();
	}

	public boolean isIntrusion() {
		return isIntrusion;
	}

	public void flagAsIntrusion() {
		this.isIntrusion = true;
	}
}
