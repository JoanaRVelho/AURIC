package hcim.auric.accessibility;

import android.util.SparseArray;
import android.view.accessibility.AccessibilityEvent;

/**
 * Manages types of accessibility events that
 * {@link hcim.auric.accessibility.AuricAccessibilityService} will receive.
 * 
 * @author Joana Velho
 * 
 */
public class EventManager {

	/**
	 * Contain event types that
	 * {@link hcim.auric.accessibility.AuricAccessibilityService} will receive
	 * and add a prefix.
	 */
	private static SparseArray<String> events;

	static {
		events = new SparseArray<String>();

		events.put(AccessibilityEvent.TYPE_VIEW_CLICKED, "User tapped on ");
		events.put(AccessibilityEvent.TYPE_VIEW_LONG_CLICKED,
				"User long tapped on ");
		events.put(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED, "Text changed: ");
		// events.put(AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED,
		// "Text selection changed: ");
		events.put(AccessibilityEvent.TYPE_VIEW_SELECTED, "User selected: ");
		events.put(AccessibilityEvent.TYPE_VIEW_SCROLLED, "User scrolled");
		events.put(AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED,
				"New Notification: ");
		events.put(AccessibilityEvent.TYPE_VIEW_CLICKED, "User selected: ");
	}

	/**
	 * 
	 * @return event types that
	 *         {@link hcim.auric.accessibility.AuricAccessibilityService} is
	 *         interested in.
	 */
	public static int getEventTypes() {
		int result = AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
				| AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;
		for (int i = 0; i < events.size(); i++) {
			result = result | events.keyAt(i);
		}

		return result;
	}

	/**
	 * 
	 * @param eventType
	 *            : event type
	 * @return prefix of event type
	 */
	public static String getEventPrefix(int eventType) {
		return events.get(eventType);
	}

	/**
	 * Checks if event has prefix.
	 * 
	 * @param eventType
	 *            : type of event
	 * @return true if has prefix, false otherwise.
	 */
	public static boolean hasDetails(int eventType) {
		return events.indexOfKey(eventType) >= 0;
	}

	/**
	 * Checks if if represents an event of changing the text of an
	 * {@link android.widget.EditText}.
	 * 
	 * @param eventType
	 *            : event type
	 * @return true if represents an event of changing the text of an
	 *         {@link android.widget.EditText}.
	 */
	public static boolean isEditText(int eventType) {
		return eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED
				|| eventType == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED;
	}
}
