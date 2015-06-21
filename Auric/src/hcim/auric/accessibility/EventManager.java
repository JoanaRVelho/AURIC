package hcim.auric.accessibility;

import android.util.SparseArray;
import android.view.accessibility.AccessibilityEvent;

public class EventManager {

	private static SparseArray<String> events;

	static {
		events = new SparseArray<String>();

		events.put(AccessibilityEvent.TYPE_VIEW_CLICKED, "User tapped on ");
		events.put(AccessibilityEvent.TYPE_VIEW_LONG_CLICKED,
				"User long tapped on ");
		events.put(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED, "Text changed: ");
//		events.put(AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED,
//				"Text selection changed: ");
		events.put(AccessibilityEvent.TYPE_VIEW_SELECTED, "User selected: ");
		events.put(AccessibilityEvent.TYPE_VIEW_SCROLLED, "User scrolled");
		events.put(AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED,
				"New Notification: ");
		events.put(AccessibilityEvent.TYPE_VIEW_CLICKED, "User selected: ");
	}

	public static int getEventTypes() {
		int result = AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
				| AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
		for (int i = 0; i < events.size(); i++) {
			result = result | events.keyAt(i);
		}

		return result;
	}

	public static String getPrefix(int eventType) {
		return events.get(eventType);
	}

	public static boolean hasDetails(int eventType) {
		return events.indexOfKey(eventType) >= 0;
	}

	public static boolean isEditText(int eventType) {
		return eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED
				|| eventType == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED;
	}
}
