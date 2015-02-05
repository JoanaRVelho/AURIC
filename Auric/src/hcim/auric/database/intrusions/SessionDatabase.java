package hcim.auric.database.intrusions;

import hcim.auric.intrusion.Session;
import hcim.auric.utils.CalendarManager;

import java.util.List;

import android.content.Context;

public class SessionDatabase {
	private static SessionDatabase INSTANCE;
	
	private SQLiteSession sessionDB;

	public static SessionDatabase getInstance(Context c) {
		if (INSTANCE == null)
			INSTANCE = new SessionDatabase(c);
		return INSTANCE;
	}

	public List<Session> getSessionsFromADay(String date) {
		if (sessionDB != null)
			return sessionDB.getAllSessionsFromDay(date);

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

	public void printAll(){
		sessionDB.printAll();
	}
	
	public void deleteSession(String id, boolean onlyPictures) {
		if (!onlyPictures)
			sessionDB.deleteIntrusion(id);
	}

	public boolean dayOfSession(String theday, String themonth, String theyear) {
		return dayOfIntrusion(CalendarManager.getDateFormat(theday, themonth,
				theyear));
	}

	public boolean dayOfIntrusion(String date) {
		List<Session> list = sessionDB
				.getAllSessionsFromDay(date);

		return !(list == null || list.size() == 0);
	}

	private SessionDatabase(Context c) {
		sessionDB = new SQLiteSession(c);
	}
}
