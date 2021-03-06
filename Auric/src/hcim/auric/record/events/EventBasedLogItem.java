package hcim.auric.record.events;

import hcim.auric.Picture;
import hcim.auric.accessibility.EventManager;
import hcim.auric.utils.Converter;
import hcim.auric.utils.TimeManager;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.view.accessibility.AccessibilityEvent;

/**
 * 
 * @author Joana Velho
 * 
 */
public class EventBasedLogItem {
	private static final String PHONE = "com.android.phone";

	private int id;
	private String appName;
	private String time;
	private ArrayList<String> details;
	private List<Picture> pictures;
	private Drawable icon;
	private int eventType;

	// attributes used for showing results
	private int colorRes;
	private String intrusionID;

	private String packageName;

	public EventBasedLogItem(AccessibilityEvent event, Context context) {
		this.time = TimeManager.getTime(event.getEventTime());
		this.packageName = event.getPackageName().toString();
		this.appName = getAppName(packageName, context);
		this.details = new ArrayList<String>();

		this.eventType = event.getEventType();

		if (EventManager.hasDetails(eventType)) {
			String s = processDetails(event);

			if (s != null && !s.equals(""))
				details.add(s);
		} else if (packageName.equals(PHONE)) {
			String aux = Converter.listCharSequenceToString(event.getText());
			if (aux != null && !aux.equals("")) {
				details.add(aux);
			}
		}
	}

	public static String processDetails(AccessibilityEvent event) {
		CharSequence seq = event.getContentDescription();
		String contentDescription = seq == null ? "" : seq.toString();

		String prefix = EventManager.getEventPrefix(event.getEventType());

		if (contentDescription.equals("")) {
			String aux = Converter.listCharSequenceToString(event.getText());

			if (!aux.equals("")) {
				return (prefix + "\"" + aux + "\"");
			} else
				return "";
		}
		return (prefix + "\"" + contentDescription + "\"");
	}

	public EventBasedLogItem(Context c, int id, String appName, String time,
			String packageName, ArrayList<String> details) {
		this.id = id;
		this.appName = appName;
		this.time = time;
		this.packageName = packageName;
		this.details = details;
		this.icon = getAppIcon(packageName, c);
	}

	public EventBasedLogItem(String time, List<Picture> p, int color,
			String intrusion) {
		this.pictures = p;
		this.appName = "(nothing to show)";
		this.time = time;
		this.colorRes = color;
		this.intrusionID = intrusion;
		this.packageName = null;
	}

	@Override
	public String toString() {
		return "EventBasedLogItem [id=" + id + ", appName=" + appName
				+ ", time=" + time + ", details=" + details + ", packageName="
				+ packageName + "]";
	}

	public int getId() {
		return id;
	}

	public boolean nothingToShow() {
		return packageName == null;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getAppName() {
		return appName;
	}

	public String getTime() {
		return time;
	}

	public ArrayList<String> getDetails() {
		return details;
	}

	public String detailsToString() {
		if (details == null || details.isEmpty())
			return "nothing to show";

		StringBuilder builder = new StringBuilder();

		for (String s : details) {
			builder.append(s);
			builder.append("\n\n");
		}

		return builder.toString();
	}

	public void mergeDetails(EventBasedLogItem other) {
		List<String> otherDetails = other.details;

		String last = details.isEmpty() ? null : details
				.get(details.size() - 1);

		for (String s : otherDetails) {
			if (!s.equals("")) {
				if (last == null || !last.equals(s)) {
					this.details.add(s);
					last = s;
				}
			}
		}
	}

	public Drawable getIcon() {
		return icon;
	}

	public List<Picture> getPictures() {
		return pictures;
	}

	public ArrayList<String> getPicturesIDs() {
		ArrayList<String> result = new ArrayList<String>();

		for (Picture p : pictures) {
			result.add(p.getID());
		}
		return result;
	}

	public void addPicture(Picture p) {
		if (pictures == null) {
			pictures = new ArrayList<Picture>();
		}
		pictures.add(p);
	}

	private static String getAppName(String packageName, Context c) {
		PackageManager pm = c.getPackageManager();

		ApplicationInfo info;
		try {
			info = pm.getApplicationInfo(packageName, 0);
		} catch (final NameNotFoundException e) {
			return "UNKNOWN";
		}
		if (info == null)
			return "UNKNOWN";

		CharSequence seq = pm.getApplicationLabel(info);
		if (seq == null)
			return "UNKNOWN";

		String result = seq.toString();

		if (result.equals("")) {
			return "UNKNOWN";
		}

		return result;
	}

	public static Drawable getAppIcon(String packageName, Context c) {
		PackageManager pm = c.getPackageManager();
		Drawable d = null;
		try {
			d = pm.getApplicationIcon(packageName);
		} catch (NameNotFoundException e) {
		}
		return d;
	}

	public int getColorRes() {
		return colorRes;
	}

	public void setColorRes(int colorRes) {
		this.colorRes = colorRes;
	}

	public String getIntrusionID() {
		return intrusionID;
	}

	public void setIntrusionID(String intrusionID) {
		this.intrusionID = intrusionID;
	}

}
