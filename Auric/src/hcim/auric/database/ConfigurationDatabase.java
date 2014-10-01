package hcim.auric.database;

import android.content.Context;
import android.content.res.Resources;

import com.hcim.intrusiondetection.R;

public class ConfigurationDatabase {
	public static final String TAG = "AURIC";

	private static ConfigurationDatabase INSTANCE;

	/** Mode **/
	public static String ORIGINAL_MODE;;
	public static String WIFI_MODE;
	public static String NONE;

	private SQLiteState stateDB;

	private String mode;
	private int screenshotOpt;
	private int cameraCapture;

	private int defaultScreenshotOpts;
	private int defaultCameraCapture;
	private String defaultMode;

	public static ConfigurationDatabase getInstance(Context c) {
		if (INSTANCE == null) {
			INSTANCE = new ConfigurationDatabase(c);
		}

		return INSTANCE;
	}

	private ConfigurationDatabase(Context c) {
		stateDB = new SQLiteState(c);

		// default options
		initDefaultOptions(c);

		mode = getMode();
		cameraCapture = getCameraCaptureOption();
		screenshotOpt = getScreenshotOptions();

		if (mode == null) {
			stateDB.insertMode(defaultMode);
			mode = defaultMode;
		}
		if (cameraCapture <= 0) {
			stateDB.insertCameraCaptureOption(defaultCameraCapture);
			cameraCapture = defaultCameraCapture;
		}
		if (screenshotOpt <= 0) {
			stateDB.insertScreenshotOption(defaultScreenshotOpts);
			screenshotOpt = defaultScreenshotOpts;
		}
	}

	private void initDefaultOptions(Context c) {
		Resources r = c.getResources();

		String firstElem = r.getStringArray(R.array.camera_capture_array)[0];
		String[] split = firstElem.split(" ");
		defaultCameraCapture = Integer.parseInt(split[0]);

		firstElem = r.getStringArray(R.array.screenshot_array)[0];
		split = firstElem.split(" ");
		defaultScreenshotOpts = Integer.parseInt(split[0]);

		String[] elements = r.getStringArray(R.array.mode_array);
		NONE = elements[0];
		WIFI_MODE = elements[1];
		ORIGINAL_MODE = elements[2];
		defaultMode = NONE;

	}

	public String getMode() {
		if (mode == null) {
			if (stateDB != null) {
				mode = stateDB.getMode();
			}
		}
		return mode;
	}

	public void setMode(String newMode) {
		if (stateDB != null) {
			stateDB.updateMode(newMode);
			mode = newMode;
		}
	}

	public boolean isDeviceSharingEnabled() {
		if (stateDB != null)
			return stateDB.getDeviceSharingMode();
		return false;
	}

	public void enableDeviceSharing(boolean b) {
		if (stateDB != null)
			stateDB.updateDeviceSharingMode(b);
	}

	public String getPasscode() {
		if (stateDB != null) {
			return stateDB.getPasscode();
		}
		return null;
	}

	public void setPasscode(String s) {
		if (stateDB != null) {
			if (hasPasscode()) {
				stateDB.updatePasscode(s);
			} else {
				stateDB.insertPasscode(s);
			}
		}
	}

	public boolean hasPasscode() {
		return getPasscode() != null;
	}

	public void deletePasscode() {
		stateDB.deletePasscode();
	}

	public int getCameraCaptureOption() {
		if (cameraCapture <= 0) {
			if (stateDB != null) {
				cameraCapture = stateDB.getCameraCaptureOption();
			}
		}
		return cameraCapture;
	}

	public void setCameraCaptureOption(int period) {
		if (stateDB != null) {
			stateDB.updateCameraCaptureOption(period);
			cameraCapture = period;
		}
	}

	public int getScreenshotOptions() {
		if (screenshotOpt <= 0) {
			if (stateDB != null) {
				screenshotOpt = stateDB.getScreenshotOption();
			}
		}
		return screenshotOpt;
	}

	public void setScreenshotOptions(int s) {
		if (stateDB != null) {
			stateDB.updateScreenshotOption(s);
			screenshotOpt = s;
		}
	}



}
