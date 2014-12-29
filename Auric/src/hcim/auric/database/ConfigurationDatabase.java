package hcim.auric.database;

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

	private static ConfigurationDatabase INSTANCE;
	private SQLiteState stateDB;

	private String mode;
	private String log;

	private String defaultLog;
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
		log = getLogType();

		if (mode == null) {
			stateDB.insertMode(defaultMode);
			mode = defaultMode;
		}
		if (log == null) {
			stateDB.insertLogType(defaultLog);
			log = defaultLog;
		}
	}

	public boolean isIntrusionDetectorActive() {
		if(stateDB != null){
			return stateDB.isIntrusionDetectorActive();
		}
		return false;
	}
	
	public void setIntrusionDetectorActivity(boolean b){
		if(stateDB != null){
			stateDB.setIntrusionDetectorState(b);
		}
	}

	private void initDefaultOptions(Context c) {
		Resources r = c.getResources();

		String[] elements = r.getStringArray(R.array.mode_array);
		ORIGINAL_MODE = elements[0];
		WIFI_MODE = elements[1];
		defaultMode = ORIGINAL_MODE;

		elements = r.getStringArray(R.array.log_array);
	//	MSWAT_LIB_LOG = elements[2];
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
