package hcim.auric.activities.apps;

import java.io.Serializable;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

public class ApplicationData implements Comparable<Object>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;
	private String packageName;
	private boolean target;

	public ApplicationData(ApplicationInfo info, PackageManager packageManager) {
		this.name = info.loadLabel(packageManager).toString();
		this.packageName = info.packageName;
		this.target = false;
	}

	public String getName() {
		return name;
	}

	public String getPackageName() {
		return packageName;
	}

	public boolean isTarget() {
		return target;
	}

	public void setTarget(boolean selected) {
		this.target = selected;
	}

	@Override
	public String toString() {
		return "ApplicationData [name=" + name + ", packageName=" + packageName
				+ ", target=" + target + "]";
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
