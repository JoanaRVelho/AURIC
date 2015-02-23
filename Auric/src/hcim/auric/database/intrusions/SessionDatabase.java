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
	}

	public void deleteSession(String id) {
		sessionDB.deleteIntrusion(id);
	}

	public boolean dayOfSession(String theday, String themonth, String theyear,
			boolean showAll) {
		return dayOfSession(
				CalendarManager.getDateFormat(theday, themonth, theyear),
				showAll);
	}

	public boolean dayOfSession(String date, boolean showAll) {
		List<Session> list = showAll ? sessionDB.getAllSessionsFromDay(date)
				: sessionDB.getAllIntrusionSessionsFromDay(date);
		
		return !(list == null || list.size() == 0);
	}

	private SessionDatabase(Context c) {
		sessionDB = new SQLiteSession(c);
	}

	public String getLogType(String id) {
		if (sessionDB != null) {
			return sessionDB.getRecorderType(id);
		}
		return null;
	}
}
