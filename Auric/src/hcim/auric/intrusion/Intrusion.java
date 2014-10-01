package hcim.auric.intrusion;

import hcim.auric.calendar.CalendarManager;
import hcim.auric.recognition.Picture;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;

public class Intrusion implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private List<Picture> images;
	private Log log;
	private String date;
	private String time;

	public Intrusion(Context c) {
		id = CalendarManager.getCurrentDateAndTime();
		int idx = id.indexOf(" ");

		date = id.substring(0, idx);
		time = id.substring(idx + 1);

		log = new Log(c); // MainActivity.context
		images = new ArrayList<Picture>();
	}

	public Intrusion(String id, String date, String time) {
		this.id = id;
		this.date = date;
		this.time = time;
	}

	public Intrusion(String id, String date, String time, String timestamp) {
		this.id = id;
		this.date = date;
		this.time = time;
		this.log = new Log(timestamp);
		images = new ArrayList<Picture>();
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

	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}

	public List<Picture> getImages() {
		return images;
	}

	public void setImages(List<Picture> images) {
		this.images = images;
	}

	public void addImage(Bitmap capturedFace) {
		this.images.add(new Picture(null, null,capturedFace));
	}

	public Log getLog() {
		return log;
	}

	public void setLog(Log log) {
		this.log = log;
	}

	public void startLogging() {
		log.startLogging();
	}

	public void stopLogging() {
		log.stopLogging();
	}

	@Override
	public String toString() {
		return "Intrusion " + time;
	}
}
