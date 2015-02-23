package hcim.auric.intrusion;

import hcim.auric.recognition.Picture;
import hcim.auric.utils.CalendarManager;

import java.util.List;

public class Intrusion {
	public static final int UNCHECKED = -1;
	public static final int NONE = 0;
	public static final int LOW = 1;
	public static final int MODERATE = 2;
	public static final int HIGH = 3;

	protected String id;
	protected String date;
	protected String time;
	protected int tag;
	protected String recorderType;

	protected List<Picture> images;

	Intrusion() {
	}

	public Intrusion(String recorderType) {
		long timestamp = System.currentTimeMillis();

		this.id = Long.toString(timestamp);
		this.date = CalendarManager.getDate(timestamp);
		this.time = CalendarManager.getTime(timestamp);
		this.tag = UNCHECKED;
		this.recorderType = recorderType;
	}

	public Intrusion(String recorderType, long timestamp) {
		this.id = Long.toString(timestamp);
		this.date = CalendarManager.getDate(timestamp);
		this.time = CalendarManager.getTime(timestamp);
		this.tag = UNCHECKED;
		this.recorderType = recorderType;
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
		return recorderType;
	}

	public void setLogType(String logType) {
		this.recorderType = logType;
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
				+ ", tag=" + tag + ", logType=" + recorderType + "]";
	}
}
