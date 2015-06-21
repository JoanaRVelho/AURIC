package hcim.auric.utils;

import android.util.Log;

public class LogUtils {
	public static final String TAG = "AURIC";

	public static void exception(Exception e) {
		Log.e(TAG, e.getMessage());
	}

	public static void debug(String s) {
		Log.d(TAG, s);
	}
	
	public static void info(String s) {
		Log.i(TAG, s);
	}
	
	public static void error(String s) {
		Log.e(TAG, s);
	}

}
