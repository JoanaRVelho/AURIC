package hcim.auric.activities.apps;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

public class ApplicationData implements Comparable<Object>{

	private String name;
	private String packageName;
	private Drawable icon;
	private boolean selected;

	public ApplicationData(ApplicationInfo info, PackageManager packageManager) {
		this.name = info.loadLabel(packageManager).toString();
		this.packageName = info.packageName;
		this.icon = info.loadIcon(packageManager);
		this.selected = false;
	}

	public String getName() {
		return name;
	}

	public String getPackageName() {
		return packageName;
	}

	public Drawable getIcon() {
		return icon;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public String toString() {
		return "ApplicationInfo [name=" + name + ", packageName=" + packageName
				+ "]";
	}

	@Override
	public int compareTo(Object another) {
		if (another instanceof ApplicationData) {
			ApplicationData app = (ApplicationData) another;

			return this.getName().compareTo(app.getName());
		}
		return 0;
	}
}
