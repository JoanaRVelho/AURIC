package hcim.auric;

import hcim.auric.utils.CalendarManager;

import java.util.List;

public class Intrusion {
	public static final int FALSE_INTRUSION = 4;

	private String id;
	private String date;
	private String time;
	private int tag;
	private String session;
	private List<Picture> images;

	public Intrusion(String id, String date, String time, int tag,
			String session) {
		this.id = id;
		this.date = date;
		this.time = time;
		this.tag = tag;
		this.session = session;
	}

	public Intrusion() {
		long timestamp = System.currentTimeMillis();

		this.id = Long.toString(timestamp);
		this.date = CalendarManager.getDate(timestamp);
		this.time = CalendarManager.getTime(timestamp);
	}

	public String getDate() {
		return date;
	}

	public String getTime() {
		return time;
	}

	public int getTag() {
		return tag;
	}

	public boolean isFalseIntrusion() {
		return tag == FALSE_INTRUSION;
	}

	public void markAsFalse() {
		tag = FALSE_INTRUSION;
	}

	public String getID() {
		return id;
	}

	public String getSession() {
		return session;
	}

	public List<Picture> getImages() {
		return images;
	}

	public void setImages(List<Picture> images) {
		this.images = images;
	}

	@Override
	public String toString() {
		return "Intrusion [id=" + id + ", date=" + date + ", time=" + time
				+ ", tag=" + tag + ", session=" + session + "]";
	}
}
