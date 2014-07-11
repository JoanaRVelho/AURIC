package hcim.auric.intrusion;

import hcim.auric.calendar.CalendarManager;
import hcim.auric.intrusiondetection.MainActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

public class Intrusion implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private List<Bitmap> images;
	private Log log;
	private String date;
	private String time;

	public Intrusion() {
		id = CalendarManager.getCurrentDateAndTime();
		int idx = id.indexOf(" ");

		date = id.substring(0, idx);
		time = id.substring(idx + 1);
		
		log = new Log(MainActivity.context);
		images = new ArrayList<Bitmap>();
	}

	public Intrusion(String id, String date, String time) {
		this.id = id;
		this.date = date;
		this.time = time;
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

	public List<Bitmap> getImages() {
		return images;
	}

	public void setImages(List<Bitmap> images) {
		this.images = images;
	}

	public void addImage(Bitmap capturedFace) {
		this.images.add(capturedFace);
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

	public void replay() {
		log.replay();
	}

	@Override
	public String toString() {
		return "Intrusion " + time;
	}
}
