package hcim.auric.record.screen.event_based;

import hcim.auric.recognition.Picture;
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

public class EventBasedLogItem {
	static final String TAG = "AURIC";

	private int id;
	private String appName;
	private String time;
	private ArrayList<String> details;
	private List<Picture> pictures;
	private Drawable icon;

	private String packageName;

	public EventBasedLogItem(AccessibilityEvent event, Context context) {
		this.time = TimeManager.getTime(event.getEventTime());
		this.packageName = event.getPackageName().toString();
		this.appName = getAppName(packageName, context);
		this.details = new ArrayList<String>();
		String aux = Converter.listCharSequenceToString(event.getText());

		if (!aux.equals(""))
			this.details.add(aux);
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

	@Override
	public String toString() {
		return "EventBasedLogItem [id=" + id + ", appName=" + appName
				+ ", time=" + time + ", details=" + details + ", packageName="
				+ packageName + "]";
	}

	public int getId() {
		return id;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String text) {
		this.appName = text;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public ArrayList<String> getDetails() {
		return details;
	}

	public void setDetails(ArrayList<String> details) {
		this.details = details;
	}

	public String detailsToString() {
		StringBuilder builder = new StringBuilder();

		for (String s : details) {
			builder.append("User tapped on ");
			builder.append(s);
			builder.append("\n\n");
		}

		return builder.toString();
	}

	public void mergeDetails(EventBasedLogItem other) {
		List<String> otherDetails = other.details;

		for (String s : otherDetails) {
			if (!s.equals("")) {
				this.details.add(s);
			}
		}
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public void addDetails(String d) {
		details.add(d);

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

		String result = pm.getApplicationLabel(info).toString();

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
}
