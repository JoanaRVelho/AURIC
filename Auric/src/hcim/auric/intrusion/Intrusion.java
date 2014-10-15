package hcim.auric.intrusion;

import hcim.auric.calendar.CalendarManager;
import hcim.auric.recognition.Picture;

import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.content.Intent;

public class Intrusion {
	private Context context;

	private String id;
	private String date;
	private String time;

	private List<Picture> images;

	public Intrusion(Context context) {
		this.context = context;

		Calendar c = Calendar.getInstance();
		date = CalendarManager.getDateFormat(c);
		time = CalendarManager.getTimeFormat(c);
		id = CalendarManager.getTimestampFormat(c);
	}

	Intrusion() {
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

	public void stopLogging() {
		Intent intent = new Intent();
		intent.setAction("swat_interaction");
		intent.putExtra("logging", false);
		context.sendBroadcast(intent);
	}

	public void startLogging() {
		Intent intent = new Intent();
		intent.setAction("swat_interaction");
		intent.putExtra("logging", true);
		intent.putExtra("timestamp", id);

		context.sendBroadcast(intent);
	}

	@Override
	public String toString() {
		return "Intrusion [id=" + id + ", date=" + date + ", time=" + time
				+ "]";
	}
}
