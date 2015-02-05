package hcim.auric.database.configs;

import android.content.Context;
import android.content.res.Resources;

import com.hcim.intrusiondetection.R;

public class ConfigurationDatabase {
	public static final String TAG = "AURIC";

	/** Log type **/
	public static String SCREENCAST_ROOT_LOG;
	public static String EVENT_LOG;

	/** Mode **/
	public static String ORIGINAL_MODE;
	public static String WIFI_MODE;
	public static String VERBOSE_MODE;

	private static ConfigurationDatabase INSTANCE;
	private SQLiteState stateDB;

	private String mode;
	private String log;

	private String defaultLog;
	private String defaultMode;

	private static final int DEFAULT_FR_MAX = 80;
	private static final int DEFAULT_CAMERA_PERIOD = 5000;

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
		log = getLogType();
		
		int cameraPeriod = getCameraPeriod();
		int faceRecognitionMax = getFaceRecognitionMax();

		if (mode == null) {
			stateDB.insertMode(defaultMode);
			mode = defaultMode;
		}
		if (log == null) {
			stateDB.insertLogType(defaultLog);
			log = defaultLog;
		}
		if(cameraPeriod == -1){
			stateDB.insertCameraPeriod(DEFAULT_CAMERA_PERIOD);
			cameraPeriod = DEFAULT_CAMERA_PERIOD;
		}
		if(faceRecognitionMax == -1){
			stateDB.insertFaceRecognitionMax(DEFAULT_FR_MAX);
			cameraPeriod = DEFAULT_FR_MAX;
		}
	}

	public int getCameraPeriod() {
		return stateDB.getCameraPeriod();
	}

	public void setCameraPeriod(int camera) {
		if (stateDB != null) {
			if (stateDB.getCameraPeriod() == -1)
				stateDB.insertCameraPeriod(camera);
			else
				stateDB.updateCameraPeriod(camera);
		}
	}

	public int getFaceRecognitionMax() {
		return stateDB.getFaceRecognitionMax();
	}

	public void setFaceRecognitionMax(int max) {
		if (stateDB != null) {
			if (stateDB.getFaceRecognitionMax() == -1)
				stateDB.insertFaceRecognitionMax(max);
			else
				stateDB.updateFaceRecognitionMax(max);
		}
	}

	public boolean hideNotification() {
		return stateDB.hideNotification();
	}

	public void setHideNotification(boolean b) {
		if (stateDB != null) {
			stateDB.updateHideNotification(b);
		}
	}

	public boolean isIntrusionDetectorActive() {
		if (stateDB != null) {
			return stateDB.isIntrusionDetectorActive();
		}
		return false;
	}

	public void setIntrusionDetectorActivity(boolean b) {
		if (stateDB != null) {
			stateDB.setIntrusionDetectorState(b);
		}
	}

	private void initDefaultOptions(Context c) {
		Resources r = c.getResources();

		String[] elements = r.getStringArray(R.array.mode_array);
		ORIGINAL_MODE = elements[0];
		VERBOSE_MODE = elements[1];
		WIFI_MODE = elements[2];
		defaultMode = VERBOSE_MODE;

		elements = r.getStringArray(R.array.log_array);
		SCREENCAST_ROOT_LOG = elements[1];
		EVENT_LOG = elements[0];
		defaultLog = EVENT_LOG;
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

	public String getLogType() {
		if (stateDB != null) {
			return stateDB.getLogType();
		}
		return null;
	}

	public void setLogType(String log) {
		if (stateDB != null) {
			if (stateDB.getLogType() != null)
				stateDB.updateLogType(log);
			else
				stateDB.insertLogType(log);
		}
	}
}
