package hcim.auric.database;

import hcim.auric.calendar.CalendarManager;
import hcim.auric.intrusion.Intrusion;
import hcim.auric.recognition.FaceRecognition;
import hcim.auric.recognition.Picture;
import hcim.auric.utils.StringGenerator;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

public class IntrusionsDatabase {
	private static IntrusionsDatabase INSTANCE;
	
	public static final int NONE = 0, LOW = 1, MODERATE = 2, HIGH = 3;

	private SQLiteIntrusionData intrusionsDB;
	private SQLiteIntruderPictures intrusionsPicturesDB;

	public static IntrusionsDatabase getInstance(Context c) {
		if (INSTANCE == null)
			INSTANCE = new IntrusionsDatabase(c);
		return INSTANCE;
	}

	public List<Intrusion> getIntrusionsDataFromADay(String date) {
		if (intrusionsDB != null)
			return intrusionsDB.getAllIntrusionsFromDay(date);

		return null;
	}

	public void insertIntrusionData(Intrusion i) {
		if (intrusionsDB != null) {
			Log.i("AURIC", "addddddddddd");
			intrusionsDB.addIntrusion(i);
		}
	}
	
	public Intrusion getIntrusion(String id) {
		Intrusion i = intrusionsDB.getIntrusion(id);
		List<Picture> pics = intrusionsPicturesDB.getAllPictures(id);

		i.setImages(pics);

		return i;
	}

	public void printAll(){
		intrusionsDB.printAll();
	}
	
	public void deleteIntrusion(String id, boolean onlyPictures) {
		if (!onlyPictures)
			intrusionsDB.deleteIntrusion(id);

		intrusionsPicturesDB.deleteAllPictures(id);
	}

	public void updateIntrusion(Intrusion i) {
		if (intrusionsDB != null)
			intrusionsDB.updateIntrusionTag(i);
	}

	public Picture getPictureOfTheIntruder(String pictureID) {
		if (intrusionsPicturesDB != null) {
			return intrusionsPicturesDB.getPicture(pictureID);
		}
		return null;
	}

	public void insertPictureOfTheIntruder(String intrusionID, Bitmap img) {
		if (intrusionsPicturesDB != null) {
			Picture p = new Picture(StringGenerator.generate(),
					FaceRecognition.UNKNOWN_PICTURE_TYPE, img);

			intrusionsPicturesDB.insertPicture(intrusionID, p);
		}
	}

	public void updatePictureType(Picture p) {
		if (intrusionsPicturesDB != null) {
			intrusionsPicturesDB.updatePictureType(p);
		}
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

	public List<Intrusion> getIntrusions(int severity) {
		List<Intrusion> result = null;

		if (intrusionsDB != null && intrusionsPicturesDB != null) {
			result = intrusionsDB.getIntrusions(severity);

			for (Intrusion i : result) {
				i.setImages(intrusionsPicturesDB.getAllPictures(i.getID()));
			}
		}
		return result;
	}

	public int numberOfIntrusions(int severity) {
		if (intrusionsDB != null)
			return intrusionsDB.numberOfIntrusions(severity);
		
		return 0;
	}

	private IntrusionsDatabase(Context c) {
		intrusionsDB = new SQLiteIntrusionData(c);
		intrusionsPicturesDB = new SQLiteIntruderPictures(c);
	}
}
