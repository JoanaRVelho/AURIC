package hcim.auric.record.events;

import hcim.auric.accessibility.EventManager;
import hcim.auric.utils.Converter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.accessibility.AccessibilityEvent;

/**
 * 
 * @author Joana Velho
 * 
 */
public class EventBasedLog {
	private List<EventBasedLogItem> list;
	private String intrusionID;
	private int lastEditIdx;
	private String lastEditText;

	public EventBasedLog(String intrusionID) {
		this.intrusionID = intrusionID;
		this.list = new ArrayList<EventBasedLogItem>();
		this.lastEditIdx = -1;
		this.lastEditText = null;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EventBasedLog [intrusionID=");
		builder.append(intrusionID);
		builder.append(" list={");

		for (EventBasedLogItem i : list) {
			builder.append(i.getAppName());
		}

		builder.append("}]");

		return builder.toString();
	}

	public String getIntrusionID() {
		return intrusionID;
	}

	public void addItem(AccessibilityEvent event, Context c) {
		if (EventManager.isEditText(event.getEventType())) {
			managingEditText(event);
		}

		EventBasedLogItem item = new EventBasedLogItem(event, c);
		list.add(item);
	}

	private void managingEditText(AccessibilityEvent event) {
		if (lastEditText != null) {
			EventBasedLogItem last = list.get(lastEditIdx);

			if (last.getPackageName().equals(event.getPackageName().toString())) {
				CharSequence seq = event.getBeforeText();

				if (seq != null) {
					String before = seq.toString();

					if (lastEditText.equals(before)) {
						list.remove(lastEditIdx);
					}
				}
			}
		}
		String s = Converter.listCharSequenceToString(event.getText());

		if (!s.equals("")) {
			lastEditIdx = list.size();
			lastEditText = s;
		}
	}

	public void addItem(EventBasedLogItem item) {
		list.add(item);
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

	public boolean isListEmpty() {
		return list.isEmpty();
	}

	public int size() {
		return list.size();
	}

	public EventBasedLogItem getItem(int i) {
		return list.get(i);
	}
}
