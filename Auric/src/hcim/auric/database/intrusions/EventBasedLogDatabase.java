package hcim.auric.database.intrusions;

import hcim.auric.record.events.EventBasedLog;
import hcim.auric.record.events.EventBasedLogItem;

import java.util.List;

import android.content.Context;

public class EventBasedLogDatabase {
	private static EventBasedLogDatabase INSTANCE;
	private SQLiteEventBasedLog logDB;

	public static EventBasedLogDatabase getInstance(Context c) {
		if (INSTANCE == null) {
			INSTANCE = new EventBasedLogDatabase(c);
		}

		return INSTANCE;
	}

	private EventBasedLogDatabase(Context c) {
		logDB = new SQLiteEventBasedLog(c);
	}

	public void insert(EventBasedLog log) {
		if (logDB != null) {
			String intrusionID = log.getIntrusionID();
			List<EventBasedLogItem> list = log.getList();
			
			for(EventBasedLogItem t : list){
				logDB.insert(intrusionID, t);
			}
		}
	}

	public EventBasedLog get(String intrusionID, Context c) {
		EventBasedLog result = null;

		if (logDB != null) {
			result = logDB.get(intrusionID, c);
		}
		
		return result;
	}
	
	public void delete(String intrusionID){
		if(logDB != null){
			logDB.delete(intrusionID);
		}
	}

	public EventBasedLogItem get(int id, Context c) {
		if(logDB != null){
			return logDB.get(id, c);
		}
		return null;
	}
}
