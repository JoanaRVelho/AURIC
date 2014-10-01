package hcim.auric.database;

import hcim.auric.calendar.CalendarManager;
import hcim.auric.intrusion.Intrusion;
import hcim.auric.intrusion.Log;
import hcim.auric.recognition.Picture;

import java.util.List;

import android.content.Context;

public class IntrusionsDatabase {
	private static IntrusionsDatabase INSTANCE;

	SQLiteIntrusion intrusionsDB;
	SQLiteIntruderPictures intrusionPicturesDB;

	private IntrusionsDatabase(Context c) {
		intrusionsDB = new SQLiteIntrusion(c);
		intrusionPicturesDB = new SQLiteIntruderPictures(c);
	}

	public int getNumberOfLogs() {
		return 1;
	}

	public List<Intrusion> getIntrusionsFromADay(String date) {
		List<Intrusion> list = intrusionsDB.getAllIntrusionsFromDay(date);

		if (list == null)
			return null;

		String id;
		List<Picture> pics;
		String log;

		for (Intrusion i : list) {
			id = i.getID();
			pics = intrusionPicturesDB.getAllIntrusionPicture(id);
			log = i.getLog().getID();

			i.setImages(pics);
			i.setLog(new Log(log));
		}

		return list;
	}

	public void addIntrusion(Intrusion i) {
		List<Picture> list = i.getImages();
		String intrusionID = i.getID();
		intrusionsDB.addIntrusion(i);

		if (list != null) {
			for (Picture p : list)
				intrusionPicturesDB.addIntrusionPicture(intrusionID,
						p.getImage());
		}
	}

	public void removeIntrusion(Intrusion i) {
		String id = i.getID();

		intrusionsDB.deleteIntrusion(id);
		intrusionPicturesDB.deleteAllIntrusionPicture(id);
	}

	public Intrusion getIntrusion(String id) {
		Intrusion i = intrusionsDB.getIntrusion(id);
		List<Picture> pics = intrusionPicturesDB.getAllIntrusionPicture(id);

		i.setImages(pics);

		return i;
	}

	public boolean dayOfIntrusion(String theday, String themonth, String theyear) {
		return dayOfIntrusion(CalendarManager.getDateFormat(theday, themonth,
				theyear));
	}

	public boolean dayOfIntrusion(String date_month_year) {
		List<Intrusion> list = intrusionsDB
				.getAllIntrusionsFromDay(date_month_year);

		return !(list == null || list.size() == 0);
	}

	public static IntrusionsDatabase getInstance(Context c) {
		if (INSTANCE == null)
			INSTANCE = new IntrusionsDatabase(c);
		return INSTANCE;
	}

	public Picture getIntruserPicture(String id) {
		if (intrusionPicturesDB != null) {
			return intrusionPicturesDB.getIntruserPicture(Integer.parseInt(id));
		}
		return null;
	}
}
