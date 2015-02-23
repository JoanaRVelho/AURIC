package hcim.auric.database.configs;

import hcim.auric.activities.apps.ApplicationData;

import java.util.List;

import android.content.Context;
import android.util.Log;

public class TargetAppDatabase {
	private static TargetAppDatabase INSTANCE;
	private SQLiteTargetApps targetApps;

	public static TargetAppDatabase getInstance(Context c) {
		if (INSTANCE == null) {
			INSTANCE = new TargetAppDatabase(c);
		}

		return INSTANCE;
	}

	private TargetAppDatabase(Context c) {
		targetApps = new SQLiteTargetApps(c);
	}

	public void insertApplications(List<ApplicationData> apps) {
		if (targetApps != null) {

			for (ApplicationData app : apps) {
				targetApps.insertApplication(app);
			}
		}
	}

	public void insertApplication(ApplicationData app) {
		if (targetApps != null) {
			targetApps.insertApplication(app);
		}
	}

	public void updateApplication(ApplicationData app) {
		if (targetApps != null) {
			targetApps.updateApplication(app);
		}
	}

	public void removeApplication(ApplicationData app) {
		if (targetApps != null) {
			targetApps.removeApplication(app);
		}
	}

	public ApplicationData getApplication(String id) {
		if (targetApps != null) {
			return targetApps.getApplication(id);
		}
		return null;
	}

	public List<ApplicationData> getAllApplications() {
		if (targetApps != null) {
			return targetApps.getAllApplications();
		}
		return null;
	}

	public void printList() {
		if (targetApps != null) {
			List<ApplicationData> list = targetApps.getAllApplications();
			StringBuilder s = new StringBuilder();
			for (ApplicationData app : list) {
				s.append(app.toString() + "\n");
			}
			Log.i("AURIC", s.toString());
		}
	}

	public boolean hasApplication(String packageName) {
		if (targetApps != null) {
			return targetApps.hasApplication(packageName);
		}
		return false;
	}

	public boolean isTargetApplication(String packageName) {
		if (targetApps != null) {
			ApplicationData app = getApplication(packageName);
			if (app != null)
				return app.isTarget();
		}
		return false;
	}
}
