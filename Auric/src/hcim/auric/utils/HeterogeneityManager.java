package hcim.auric.utils;

import java.io.File;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class HeterogeneityManager {

	private static final float DEFAULT_TEXT_SIZE = 50;
	private static final float DEFAULT_SCREEN_SIZE = 1080;
	private static final int DEFAULT_STROKE_WIDTH = 5;

	/**
	 * Checks if phone is rooted. Code from
	 * http://stackoverflow.com/questions/3424195/
	 * determining-if-an-android-device-is-rooted-programatically
	 * 
	 * @return true if is rooted, false otherwise
	 */
	public static boolean isRooted() {
		String buildTags = android.os.Build.TAGS;
		if (buildTags != null && buildTags.contains("test-keys")) {
			return true;
		}
		try {
			File file = new File("/system/app/Superuser.apk");
			if (file.exists()) {
				return true;
			}
		} catch (Exception e) {
		}

		return canExecuteCommand("/system/xbin/which su")
				|| canExecuteCommand("/system/bin/which su")
				|| canExecuteCommand("which su");
	}

	private static boolean canExecuteCommand(String command) {
		boolean executedSuccesfully;
		try {
			Runtime.getRuntime().exec(command);
			executedSuccesfully = true;
		} catch (Exception e) {
			executedSuccesfully = false;
		}
		return executedSuccesfully;
	}

	public static int[] getScreenSizePixels(Context context) {
		Resources resources = context.getResources();
		Configuration config = resources.getConfiguration();
		DisplayMetrics dm = resources.getDisplayMetrics();

		double screenWidthInPixels = (double) config.screenWidthDp * dm.density;
		double screenHeightInPixels = screenWidthInPixels * dm.heightPixels
				/ dm.widthPixels;
		int[] widthHeightInPixels = new int[2];
		widthHeightInPixels[0] = (int) (screenWidthInPixels + .5);
		widthHeightInPixels[1] = (int) (screenHeightInPixels + .5);
		return widthHeightInPixels;
	}

	public static int getScreenWidthPixels(Context context) {
		Resources resources = context.getResources();
		Configuration config = resources.getConfiguration();
		DisplayMetrics dm = resources.getDisplayMetrics();

		double width = (double) config.screenWidthDp * dm.density;
		return (int) (width + .5);
	}

	public static int getScreenHeightPixels(Context context) {
		Resources resources = context.getResources();
		Configuration config = resources.getConfiguration();
		DisplayMetrics dm = resources.getDisplayMetrics();

		double width = (double) config.screenWidthDp * dm.density;
		double height = width * dm.heightPixels / dm.widthPixels;

		return (int) (height + .5);
	}

	public static float getCameraTextSize(Context context) {
		int w = getScreenWidthPixels(context);
		return w * DEFAULT_TEXT_SIZE / DEFAULT_SCREEN_SIZE;
	}

	public static float getCameraStrokeWidth(Context context) {
		int w = getScreenWidthPixels(context);
		return w * DEFAULT_STROKE_WIDTH / DEFAULT_SCREEN_SIZE;
	}
}