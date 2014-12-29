package hcim.auric.record.screen.event_based;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Joana Velho
 *
 */
public class EventBasedLog {
	static final String TAG = "AURIC";

	private List<EventBasedLogItem> list;
	private String intrusionID;

	public EventBasedLog(String intrusionID) {
		this.intrusionID = intrusionID;
		this.list = new ArrayList<EventBasedLogItem>();
	}

	public String getIntrusionID() {
		return intrusionID;
	}

	public void setIntrusionID(String intrusionID) {
		this.intrusionID = intrusionID;
	}

	public void addItem(EventBasedLogItem t) {
		list.add(t);
	}

	public void filter() {
		EventBasedLogItem first = null;
		List<EventBasedLogItem> newList = new ArrayList<EventBasedLogItem>();

		for (EventBasedLogItem t : list) {
			if (first == null) {
				first = t;
			} else {
				if (first.getAppName().equals(t.getAppName())) {
					first.mergeDetails(t);
				} else {
					newList.add(first);
					first = t;
				}
			}
		}

		if (first != null)
			newList.add(first);

		list = newList;
	}
	
	public List<EventBasedLogItem> getList() {
		return list;
	}
}
