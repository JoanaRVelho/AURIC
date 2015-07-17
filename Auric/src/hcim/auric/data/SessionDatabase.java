package hcim.auric.data;

import hcim.auric.Intrusion;
import hcim.auric.Picture;
import hcim.auric.Session;
import hcim.auric.recognition.FaceRecognition;
import hcim.auric.recognition.RecognitionResult;
import hcim.auric.utils.CalendarManager;
import hcim.auric.utils.StringGenerator;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;

public class SessionDatabase {
	private static SessionDatabase INSTANCE;

	private IntruderTable intruders;
	private IntrusionTable intrusions;
	private SessionTable sessions;

	private SessionDatabase(Context c) {
		intruders = new IntruderTable(c);
		intrusions = new IntrusionTable(c);
		sessions = new SessionTable(c);
	}

	public static SessionDatabase getInstance(Context c) {
		if (INSTANCE == null)
			INSTANCE = new SessionDatabase(c);
		return INSTANCE;
	}

	public List<Session> getSessionsFromADay(String date) {
		return sessions.getSessionsFromDay(date);
	}

	public List<Session> getIntrusionSessionsFromADay(String date) {
		return sessions.getIntrusionSessionsFromDay(date);
	}

	public void insertSession(Session s) {
		sessions.insertSession(s);
	}

	public Session getSession(String id) {
		return sessions.getSession(id);
	}

	public void printAll() {
		sessions.printAll();
		intrusions.printAll();
		intruders.printAll();
	}

	public void deleteSession(String sessionID) {
		intruders.deletePicturesFromSession(sessionID);
		intrusions.deleteIntrusionsFromSession(sessionID);
		sessions.deleteSession(sessionID);
	}

	public List<Intrusion> getIntrusionsFromSession(String sessionID) {
		List<Intrusion> result = intrusions.getIntrusionsFromSession(sessionID);

		for (Intrusion intrusion : result) {
			intrusion.setImages(intruders.getPictures(intrusion.getID()));
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
		List<Session> list;

		if (showOnlyInt)
			list = sessions.getIntrusionSessionsFromDay(date);
		else
			list = sessions.getSessionsFromDay(date);

		return !(list == null || list.size() == 0);
	}

	public String getRecorderType(String id) {
		return sessions.getRecorderType(id);
	}

	public void insertIntrusion(Intrusion i, String sessionID) {
		intrusions.addIntrusion(i, sessionID);
	}

	public Intrusion getIntrusion(String id) {
		Intrusion i = intrusions.getIntrusion(id);
		if (i == null)
			return null;

		List<Picture> pics = intruders.getPictures(id);
		i.setImages(pics);

		return i;
	}

	public Picture getPictureOfIntruder(String pictureID) {
		return intruders.getPicture(pictureID);
	}

	public void updatePicturesOfIntruder(String intrusionID) {
		intruders.updatePicturesUnknownIntrusion(intrusionID);
	}

	public void insertPictureOfIntruder(Bitmap img, RecognitionResult result) {
		Picture p = new Picture(StringGenerator.generateName(),
				FaceRecognition.getUnknownPictureType(), img);

		if (result != null)
			p.setDescription(result.description());

		intruders.insertPictureUnknownIntrusion(p);
	}

	public void deletePicturesUnknownIntrusion() {
		intruders.deletePicturesUnknownIntrusion();
	}

	public void deleteAll() {
		sessions.deleteAll();
		intrusions.deleteAll();
		intruders.deleteAll();
	}

	public void clean() {
		List<Intrusion> list = intrusions.getAllIntrusions();
		
		for(Intrusion i : list){
			//intrusion without session
			if(!sessions.exists(i.getSession())){
				intrusions.deleteIntrusion(i.getID());
				intruders.deletePictures(i.getID());
			}
		}
	}
}
