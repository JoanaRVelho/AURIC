package hcim.auric.intrusion;

import hcim.auric.calendar.CalendarManager;
import hcim.auric.recognition.Picture;

import java.util.List;

public class Intrusion {
	public static final int UNCHECKED = 0;
	public static final int FALSE_INTRUSION = 1;
	public static final int REAL_INTRUSION = 2;

	private String id;
	private String date;
	private String time;
	private int tag;
	private String logType;

	private List<Picture> images;

	Intrusion() {
	}

	public Intrusion(String log) {
		id = CalendarManager.currentTimeMillis();
		date = CalendarManager.getDate(id);
		time = CalendarManager.getTime(id);
		tag = UNCHECKED;
		logType = log;
	}

	public boolean isRealIntrusion() {
		return tag == REAL_INTRUSION;
	}

	public boolean isFalseIntrusion() {
		return tag == FALSE_INTRUSION;
	}

	public boolean isChecked() {
		return tag != UNCHECKED;
	}

	public void markAsRealIntrusion() {
		this.tag = REAL_INTRUSION;
	}

	public void markAsFalseIntrusion() {
		this.tag = FALSE_INTRUSION;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}

	public String getLogType() {
		return logType;
	}

	public void setLogType(String logType) {
		this.logType = logType;
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
				+ ", tag=" + tag + ", logType=" + logType + "]";
	}
}
