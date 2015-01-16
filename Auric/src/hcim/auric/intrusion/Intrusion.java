package hcim.auric.intrusion;

import hcim.auric.recognition.Picture;
import hcim.auric.utils.CalendarManager;

import java.util.List;

public class Intrusion {
	public static final int UNCHECKED = -1;

	private String id;
	private String date;
	private String time;
	private int tag;
	private String logType;

	private List<Picture> images;

	Intrusion() {
	}

	public Intrusion(String log) {
		long timestamp = System.currentTimeMillis();
		
		id = Long.toString(timestamp);
		date = CalendarManager.getDate(timestamp);
		time = CalendarManager.getTime(timestamp);
		tag = UNCHECKED;
		logType = log;
	}

	public boolean isChecked() {
		return tag != UNCHECKED;
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

	public void setTag(int i) {
		this.tag = i;
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
