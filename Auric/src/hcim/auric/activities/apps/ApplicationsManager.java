package hcim.auric.activities.apps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

public class ApplicationsManager {

	public static List<ApplicationData> getInstalledApps(
			PackageManager packageManager) {
		List<ApplicationData> result = new ArrayList<ApplicationData>();
		List<ApplicationInfo> list = filter(
				packageManager
						.getInstalledApplications(PackageManager.GET_META_DATA),
				packageManager);

		for (ApplicationInfo info : list) {
			result.add(new ApplicationData(info, packageManager));
		}
		
		return result;
	}
	
	public static List<ApplicationData> getInstalledAppsSortedByName(
			PackageManager packageManager) {
		List<ApplicationData> result = getInstalledApps(packageManager);
		
		return sort(result);
	}

	private static List<ApplicationData> sort(List<ApplicationData> list) {
		ApplicationData[] array = list
				.toArray(new ApplicationData[list.size()]);
		Arrays.sort(array, new Comparator<ApplicationData>() {

			@Override
			public int compare(ApplicationData lhs, ApplicationData rhs) {
				return lhs.compareTo(rhs);
			}

		});
		ArrayList<ApplicationData> result = new ArrayList<ApplicationData>();
		for (ApplicationData app : array) {
			result.add(app);
		}
		return result;
	}

	private static List<ApplicationInfo> filter(List<ApplicationInfo> list,
			PackageManager packageManager) {
		ArrayList<ApplicationInfo> applist = new ArrayList<ApplicationInfo>();
		for (ApplicationInfo info : list) {
			try {
				if (null != packageManager
						.getLaunchIntentForPackage(info.packageName)) {
					applist.add(info);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return applist;
	}

}
