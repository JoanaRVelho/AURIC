package hcim.auric.database;

import hcim.auric.calendar.CalendarManager;
import hcim.auric.intrusion.Intrusion;
import hcim.auric.intrusion.Log;

import java.util.List;

import android.graphics.Bitmap;

public class IntrusionsDatabase {
	static SQLiteIntrusion intrusionsDB; 
	static SQLiteLog logsDB;
	static SQLiteIntrusionPictures intrusionPicturesDB;

	public static void init(SQLiteIntrusion i, SQLiteIntrusionPictures p,
			SQLiteLog l) {
		intrusionsDB = i;
		logsDB = l;
		intrusionPicturesDB = p;
	}

	public static int getNumberOfLogs() {
		return 1;
	}

	public static List<Intrusion> getIntrusionsFromADay(String date) {
		List<Intrusion> list = intrusionsDB.getAllIntrusionsFromDay(date);

		if (list == null)
			return null;

		String id;
		List<Bitmap> pics;
		String log;

		for (Intrusion i : list) {
			id = i.getID();
			pics = intrusionPicturesDB.getAllIntrusionPicture(id);
			log = logsDB.getLog(id);

			i.setImages(pics);
			i.setLog(new Log(log));
		}

		return list;
	}

	public static void addIntrusion(Intrusion i) {
		String id = i.getID();
		List<Bitmap> list = i.getImages();

		intrusionsDB.addIntrusion(id, i.getDate(), i.getTime());

		if(list != null){
			for (Bitmap bitmap : list)
				intrusionPicturesDB.addIntrusionPicture(i.getID(), bitmap);
		}
		logsDB.addLog(id, i.getLog());
	}

	public static void removeIntrusion(Intrusion i) {
		String id = i.getID();

		intrusionsDB.deleteIntrusion(id);
		intrusionPicturesDB.deleteAllIntrusionPicture(id);
		logsDB.deleteLog(id);
	}

	public static Intrusion getIntrusion(String id) {
		Intrusion i = intrusionsDB.getIntrusion(id);
		List<Bitmap> pics = intrusionPicturesDB.getAllIntrusionPicture(id);
		String log = logsDB.getLog(id);

		i.setLog(new Log(log));
		i.setImages(pics);

		return i;
	}

	public static boolean dayOfIntrusion(String theday, String themonth,
			String theyear) {
		return dayOfIntrusion(CalendarManager.getDateFormat(theday, themonth, theyear));
	}

	public static boolean dayOfIntrusion(String date_month_year) {
		List<Intrusion> list =
				intrusionsDB.getAllIntrusionsFromDay(date_month_year);

		return !(list == null || list.size() == 0);
	}

}
