package hcim.auric.database.configs;

import hcim.auric.detector.DetectorManager;
import hcim.auric.record.RecorderManager;
import hcim.auric.strategy.StrategyManager;
import android.content.Context;

public class ConfigurationDatabase {
	public static final String TAG = "AURIC";

	public static String VERBOSE_MODE;

	private static ConfigurationDatabase INSTANCE;
	private SQLiteState stateDB;

	private String defaultRecorder;
	private String defaultDetector;
	private String defaultStrategy;

	private static final int DEFAULT_FR_MAX = 80;
	private static final int DEFAULT_CAMERA_PERIOD = 10000; // miliseconds
	private static final int DEFAULT_NUMBER_PICS = 3;

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

		String detectorType = getDetectorType();
		String recorderType = getRecorderType();
		String strategyType = getStrategyType();

		int cameraPeriod = getCameraPeriod();
		int faceRecognitionMax = getFaceRecognitionMax();
		int numberPics = getNumberOfPicturesPerDetection();

		if (detectorType == null) {
			stateDB.insertDetectorType(defaultDetector);
			detectorType = defaultDetector;
		}
		if (recorderType == null) {
			stateDB.insertRecorderType(defaultRecorder);
			recorderType = defaultRecorder;
		}
		if (strategyType == null) {
			stateDB.insertStrategyType(defaultStrategy);
			strategyType = defaultStrategy;
		}
		if (cameraPeriod == -1) {
			stateDB.insertCameraPeriod(DEFAULT_CAMERA_PERIOD);
		}
		if (faceRecognitionMax == -1) {
			stateDB.insertFaceRecognitionMax(DEFAULT_FR_MAX);
		}
		if (numberPics == -1) {
			stateDB.insertNumberOfPicturesPerDetection(DEFAULT_NUMBER_PICS);
		}
	}
	
	public boolean showAllSessions(){
		if(stateDB != null){
			return stateDB.showAllSessions();
		}
		return false;
	}
	
	public void setShowAllSessions(boolean b){
		if(stateDB != null){
			stateDB.updateShowAllSessions(b);
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
		defaultDetector = DetectorManager.getDefault();
		defaultRecorder = RecorderManager.getDefault();
		defaultStrategy = StrategyManager.getDefault();
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

	public String getDetectorType() {
		if (stateDB != null) {
			return stateDB.getDetectorType();
		}
		return null;
	}

	public void setDetectorType(String type) {
		if (stateDB != null) {
			stateDB.updateDetector(type);
		}
	}

	public String getRecorderType() {
		if (stateDB != null) {
			return stateDB.getRecorderType();
		}
		return null;
	}

	public void setRecorderType(String type) {
		if (stateDB != null) {
			if (stateDB.getRecorderType() != null) {
				stateDB.updateRecorderType(type);
			}
		}
	}

	public String getStrategyType() {
		if (stateDB != null) {
			return stateDB.getStrategyType();
		}
		return defaultStrategy;
	}

	public void setStrategyType(String type) {
		if (stateDB != null) {
			stateDB.updateStrategyType(type);
		}
	}

	public int getNumberOfPicturesPerDetection() {
		if (stateDB != null) {
			int result = stateDB.getNumberOfPicturesPerDetection();

			return result;
		}
		return 1;
	}

	public void setNumberOfPicturesPerDetection(int n) {
		if (stateDB != null) {
			stateDB.updateNumberOfPicturesPerDetection(n);
		}
	}

	public boolean getRecodeAllInteractions() {
		if (stateDB != null) {
			return stateDB.getRecordAllInteractions();
		}
		return true;
	}

	public void setRecordAllInteractions(boolean b) {
		if (stateDB != null) {
			stateDB.updateRecordAllInteractions(b);
		}
	}
}
