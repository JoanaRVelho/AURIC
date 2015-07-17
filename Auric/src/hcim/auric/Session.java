package hcim.auric;

import hcim.auric.utils.CalendarManager;

/**
 * Class that represents an interaction with the device since the screen was ON
 * until gets OFF
 * 
 * @author Joana Velho
 * 
 */
public class Session {
	private String id;
	private String date;
	private String time;
	private String recorderType;
	private boolean isIntrusion;

	public Session(String recorderType) {
		long timestamp = System.currentTimeMillis();

		id = Long.toString(timestamp);
		date = CalendarManager.getDate(timestamp);
		time = CalendarManager.getTime(timestamp);
		isIntrusion = false;
		this.recorderType = recorderType;
	}

	public Session(String id, String date, String time, String recorderType,
			boolean tag) {
		this.id = id;
		this.date = date;
		this.time = time;
		this.recorderType = recorderType;
		this.isIntrusion = tag;
	}

	public String getID() {
		return id;
	}

	@Override
	public String toString() {
		return "id=" + id + ", date=" + date + ", time=" + time
				+ ", intrusion=" + isIntrusion + ", recorder=" + recorderType;
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

	public boolean isIntrusion() {
		return isIntrusion;
	}

	public void flagAsIntrusion() {
		this.isIntrusion = true;
	}
}
