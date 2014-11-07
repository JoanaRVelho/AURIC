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
	private static final String TAG = "AURIC";

	private static IntrusionsDatabase INSTANCE;

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
			intrusionsDB.addIntrusion(i);
			Log.i(TAG, i.toString());
		}
	}

	public Intrusion getIntrusion(String id) {
		Intrusion i = intrusionsDB.getIntrusion(id);
		List<Picture> pics = intrusionsPicturesDB.getAllPictures(id);

		i.setImages(pics);

		return i;
	}

	public void deleteIntrusion(String id, boolean onlyPictures) {
		if(!onlyPictures)
			intrusionsDB.deleteIntrusion(id);
		
		intrusionsPicturesDB.deleteAllPictures(id);
	}
	
	public void updateIntrusion(Intrusion i){
		if(intrusionsDB != null)
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
			Picture p = new Picture(StringGenerator.generateString(),
					FaceRecognition.UNKNOWN_PICTURE_TYPE, img);

			intrusionsPicturesDB.insertPicture(intrusionID, p);
		}
	}

	public void updatePictureType(Picture p) {
		if (intrusionsPicturesDB != null) {
			Log.d(TAG, "update picture type");
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

		for (Intrusion i : list) {
			Log.d(TAG, i.toString());
		}

		return !(list == null || list.size() == 0);
	}

	public List<Intrusion> getFalseIntrusions() {
		List<Intrusion> result = null;

		if (intrusionsDB != null && intrusionsPicturesDB != null) {
			result = intrusionsDB.getFalseIntrusions();

			for (Intrusion i : result) {
				i.setImages(intrusionsPicturesDB.getAllPictures(i.getID()));
			}
		}
		return result;
	}

	public int numberOfFalseIntrusions() {
		if (intrusionsDB != null) {
			return intrusionsDB.numberOfFalseIntrusions();
		}
		
		return -1;
	}

	public List<Intrusion> getRealIntrusions() {
		List<Intrusion> result = null;

		if (intrusionsDB != null && intrusionsPicturesDB != null) {
			result = intrusionsDB.getRealIntrusions();

			for (Intrusion i : result) {
				i.setImages(intrusionsPicturesDB.getAllPictures(i.getID()));
			}
		}
		return result;
	}

	public int numberOfRealIntrusions() {
		if (intrusionsDB != null) {
			return intrusionsDB.numberOfRealIntrusions();
		}
		
		return -1;
	}

	public List<Intrusion> getUncheckedIntrusions() {
		List<Intrusion> result = null;

		if (intrusionsDB != null && intrusionsPicturesDB != null) {
			result = intrusionsDB.getUncheckedIntrusions();

			for (Intrusion i : result) {
				i.setImages(intrusionsPicturesDB.getAllPictures(i.getID()));
			}
		}
		return result;
	}

	public int numberOfUncheckedIntrusions() {
		if (intrusionsDB != null) {
			return intrusionsDB.numberOfUncheckedIntrusions();
		}
		
		return -1;
	}
	
	private IntrusionsDatabase(Context c) {
		intrusionsDB = new SQLiteIntrusionData(c);
		intrusionsPicturesDB = new SQLiteIntruderPictures(c);
	}
}
