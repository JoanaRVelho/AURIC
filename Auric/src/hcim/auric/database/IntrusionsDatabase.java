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
	private SQLiteIntruderPictures intrusionPicturesDB;

	public static IntrusionsDatabase getInstance(Context c) {
		if (INSTANCE == null)
			INSTANCE = new IntrusionsDatabase(c);
		return INSTANCE;
	}

	public List<Intrusion> getIntrusionsDataFromADay(String date) {
		if(intrusionsDB != null)
			return intrusionsDB.getAllIntrusionsFromDay(date);

		return null;
	}

	public void insertIntrusionData(Intrusion i) {
		if (intrusionsDB != null){
			intrusionsDB.addIntrusion(i);
			Log.i(TAG, i.toString());
		}
	}

	
	public Intrusion getIntrusion(String id) {
		Intrusion i = intrusionsDB.getIntrusion(id);
		List<Picture> pics = intrusionPicturesDB.getAllPictures(id);
	
		i.setImages(pics);
	
		return i;
	}

	public void deleteIntrusion(String id) {
		intrusionsDB.deleteIntrusion(id);
		intrusionPicturesDB.deleteAllPictures(id);
	}

	public Picture getPictureOfTheIntruder(String pictureID) {
		if (intrusionPicturesDB != null) {
			return intrusionPicturesDB.getPicture(pictureID);
		}
		return null;
	}

	public void insertPictureOfTheIntruder(String intrusionID, Bitmap img) {
		if (intrusionPicturesDB != null) {
			Picture p = new Picture(StringGenerator.generateString(),
					FaceRecognition.UNKNOWN_PICTURE_TYPE, img);
	
			intrusionPicturesDB.insertPicture(intrusionID, p);
		}
	}

	public void updatePictureType(Picture p) {
		if (intrusionPicturesDB != null) {
			intrusionPicturesDB.updatePictureType(p);
		}
	}

	public boolean dayOfIntrusion(String theday, String themonth, String theyear) {
		return dayOfIntrusion(CalendarManager.getDateFormat(theday, themonth,
				theyear));
	}

	public boolean dayOfIntrusion(String date_month_year) {
		List<Intrusion> list = intrusionsDB
				.getAllIntrusionsFromDay(date_month_year);
		
		for(Intrusion i : list){
			Log.d(TAG, i.toString());
		}
	
		return !(list == null || list.size() == 0);
	}

	private IntrusionsDatabase(Context c) {
		intrusionsDB = new SQLiteIntrusionData(c);
		intrusionPicturesDB = new SQLiteIntruderPictures(c);
	}
}
