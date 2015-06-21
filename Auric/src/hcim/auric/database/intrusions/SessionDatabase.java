package hcim.auric.database.intrusions;

import hcim.auric.intrusion.Intrusion;
import hcim.auric.intrusion.Session;
import hcim.auric.recognition.FaceRecognition;
import hcim.auric.recognition.Picture;
import hcim.auric.recognition.RecognitionResult;
import hcim.auric.utils.CalendarManager;
import hcim.auric.utils.StringGenerator;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;

public class SessionDatabase {
	private static SessionDatabase INSTANCE;

	private SQLiteSession sessionDB;
	private SQLiteIntrusionData intrusionsDB;
	private SQLiteIntruderPictures intrusionsPicturesDB;

	public static SessionDatabase getInstance(Context c) {
		if (INSTANCE == null)
			INSTANCE = new SessionDatabase(c);
		return INSTANCE;
	}

	public List<Session> getAllSessionsFromADay(String date) {
		if (sessionDB != null)
			return sessionDB.getAllSessionsFromDay(date);

		return null;
	}

	public List<Session> getIntrusionSessionsFromADay(String date) {
		if (sessionDB != null)
			return sessionDB.getAllIntrusionSessionsFromDay(date);

		return null;
	}

	public void insertSession(Session s) {
		if (sessionDB != null) {
			sessionDB.insertSession(s);
		}
	}

	public Session getSession(String id) {
		return sessionDB.getSession(id);
	}

	public void printAll() {
		sessionDB.printAll();
		intrusionsDB.printAll();
	}

	public void deleteSession(String sessionID) {
		Session s = getSession(sessionID);
		if (s == null)
			return;

		List<String> intrusionIDs = s.getIntrusionIDs();
		for (String id : intrusionIDs) {
			deleteIntrusion(id);
		}

		sessionDB.deleteSession(sessionID);
	}

	public List<Intrusion> getIntrusions(Session s) {
		List<String> intrusionsIDs = s.getIntrusionIDs();
		List<Intrusion> result = new ArrayList<Intrusion>();

		for (String id : intrusionsIDs) {
			result.add(getIntrusion(id));
		}

		return result;
	}

	public List<Intrusion> getIntrusions(String sessionID) {
		Session s = getSession(sessionID);
		List<String> intrusionsIDs = s.getIntrusionIDs();
		List<Intrusion> result = new ArrayList<Intrusion>();

		for (String id : intrusionsIDs) {
			result.add(getIntrusion(id));
		}

		return result;
	}

	public boolean dayOfSession(String theday, String themonth, String theyear,
			boolean showOnlyInt) {
		return dayOfSession(
				CalendarManager.getDateFormat(theday, themonth, theyear),
				showOnlyInt);
	}

	public boolean dayOfSession(String date, boolean showOnlyInt) {
		List<Session> list = showOnlyInt ? sessionDB
				.getAllIntrusionSessionsFromDay(date) : sessionDB
				.getAllSessionsFromDay(date);

		return !(list == null || list.size() == 0);
	}

	private SessionDatabase(Context c) {
		sessionDB = new SQLiteSession(c);
		intrusionsDB = new SQLiteIntrusionData(c);
		intrusionsPicturesDB = new SQLiteIntruderPictures(c);
	}

	public String getRecorderType(String id) {
		if (sessionDB != null) {
			return sessionDB.getRecorderType(id);
		}
		return null;
	}

	public List<Intrusion> getIntrusionsDataFromADay(String date) {
		if (intrusionsDB != null)
			return intrusionsDB.getAllIntrusionsFromDay(date);

		return null;
	}

	public void insertIntrusionData(Intrusion i) {
		if (intrusionsDB != null) {
			intrusionsDB.addIntrusion(i);
		}
	}

	public Intrusion getIntrusion(String id) {
		Intrusion i = intrusionsDB.getIntrusion(id);
		if (i == null)
			return null;

		List<Picture> pics = intrusionsPicturesDB.getAllPictures(id);
		i.setImages(pics);

		return i;
	}

	public void deleteIntrusion(String id) {
		intrusionsDB.deleteIntrusion(id);
		intrusionsPicturesDB.deleteAllPictures(id);
	}

	public void deleteIntrusionPictures(String id) {
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

	public void insertPictureOfTheIntruder(String intrusionID, Bitmap img,
			RecognitionResult result) {
		if (intrusionsPicturesDB != null) {
			Picture p = new Picture(StringGenerator.generateName(),
					FaceRecognition.getUnknownPictureType(), img);

			if (result != null)
				p.setDescription(result.description());

			intrusionsPicturesDB.insertPicture(intrusionID, p);
		}
	}

	public void deletePicturesOfTheIntruder(String intrusionID) {
		if (intrusionsPicturesDB != null) {
			intrusionsPicturesDB.deleteAllPictures(intrusionID);
		}
	}

	public void updatePicturesUnknownIntrusion(String intrusionID) {
		if (intrusionsPicturesDB != null) {
			intrusionsPicturesDB.updatePicturesUnknownIntrusion(intrusionID);
		}
	}

	public void insertPictureUnknownIntrusion(Bitmap img,
			RecognitionResult result) {
		if (intrusionsPicturesDB != null) {
			Picture p = new Picture(StringGenerator.generateName(),
					FaceRecognition.getUnknownPictureType(), img);

			if (result != null)
				p.setDescription(result.description());

			intrusionsPicturesDB.insertPictureUnknownIntrusion(p);
		}
	}

	public void deletePicturesUnknownIntrusion() {
		if (intrusionsPicturesDB != null) {
			intrusionsPicturesDB.deletePicturesUnknownIntrusion();
		}
	}

	public void updatePictureType(Picture p) {
		if (intrusionsPicturesDB != null) {
			intrusionsPicturesDB.updatePictureType(p);
		}
	}

	//
	// public boolean dayOfIntrusion(String theday, String themonth, String
	// theyear) {
	// return dayOfIntrusion(CalendarManager.getDateFormat(theday, themonth,
	// theyear));
	// }
	//
	// public boolean dayOfIntrusion(String date) {
	// List<Intrusion> list = intrusionsDB.getAllIntrusionsFromDay(date);
	//
	// return !(list == null || list.size() == 0);
	// }

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

	public void deleteAll() {
		if (sessionDB != null && intrusionsDB != null) {
			sessionDB.deleteAll();
			intrusionsDB.deleteAll();
		}
	}

	public void clean() {
		deletePicturesUnknownIntrusion();

		List<Session> listSessions = sessionDB.getAllSessions();
		List<Intrusion> listIntrusions = intrusionsDB.getAllIntrusions();
		List<String> intrusions = new ArrayList<String>();

		for (Session s : listSessions) {
			intrusions.addAll(s.getIntrusionIDs());
		}

		if (intrusions.size() < listIntrusions.size()) {
			for (Intrusion i : listIntrusions) {
				if (!intrusions.contains(i.getID())) {
					deleteIntrusion(i.getID());
				}
			}
		}
	}
}
