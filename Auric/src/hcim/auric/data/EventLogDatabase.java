package hcim.auric.data;

import hcim.auric.record.events.EventBasedLog;
import hcim.auric.record.events.EventBasedLogItem;

import java.util.List;

import android.content.Context;

/**
 * Event Log Database has a {@link EventLogTable} 
 * @author Joana Velho
 *
 */
public class EventLogDatabase {
	private static EventLogDatabase INSTANCE;
	private EventLogTable tableLog;

	public static EventLogDatabase getInstance(Context c) {
		if (INSTANCE == null) {
			INSTANCE = new EventLogDatabase(c);
		}

		return INSTANCE;
	}

	private EventLogDatabase(Context c) {
		tableLog = new EventLogTable(c);
	}

	public void insert(EventBasedLog log) {
		String intrusionID = log.getIntrusionID();
		List<EventBasedLogItem> list = log.getList();

		for (EventBasedLogItem t : list) {
			tableLog.insert(intrusionID, t);
		}
	}

	public EventBasedLog get(String intrusionID, Context c) {
		return tableLog.get(intrusionID, c);
	}

	public void delete(String intrusionID) {
		tableLog.delete(intrusionID);
	}

	public EventBasedLogItem get(int id, Context c) {
		return tableLog.get(id, c);
	}
}
