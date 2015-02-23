package hcim.auric.accessibility;

import android.util.SparseArray;
import android.view.accessibility.AccessibilityEvent;

public class EventManager {

	private static SparseArray<String> events;

	static {
		events = new SparseArray<String>();

		events.put(AccessibilityEvent.TYPE_VIEW_CLICKED, "User tapped on ");
		events.put(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED, "Text changed: ");
		events.put(AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED,
				"Text changed: ");
		events.put(AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED,
				"Window changed to ");
	}

	public static int getEventTypes() {
		int result = 0;
		for (int i = 0; i < events.size(); i++) {
			result |= events.keyAt(i);
		}
		return result;
	}

	public static String getPrefix(int eventType) {
		return events.get(eventType);
	}
}
