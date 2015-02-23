package hcim.auric.utils;

import android.util.Log;

public class LogUtils {
	public static final String TAG = "AURIC";

	public static void exception(Exception e) {
		Log.e(TAG, e.getMessage());
	}

	public static void d(String s) {
		Log.d(TAG, s);
	}
	
	public static void i(String s) {
		Log.i(TAG, s);
	}

}
