package hcim.auric.database;

import hcim.auric.detector.DetectorManager;
import hcim.auric.record.RecorderManager;
import hcim.auric.strategy.StrategyManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SettingsPreferences {
	/************* CODES ************/
	private static final String PREVIOUSLY_STARTED = "auric_prev_started";
	private static final String DETECTOR_TYPE = "auric_detector_type";
	private static final String STRATEGY_TYPE = "auric_strategy_type";
	private static final String RECORDER_TYPE = "auric_recorder_type";
	private static final String CAMERA_PERIOD = "auric_camera_period";
	private static final String FACE_RECOG_MAX = "auric_face_recog_max";
	private static final String NUMBER_PICS = "auric_number_pics";
	private static final String HAS_PASSCODE = "auric_has_passcode";
	private static final String PASSCODE = "auric_passcode";
	private static final String HIDE_NOTIFICATION = "auric_hide_not";
	private static final String SHOW_ONLY_INT = "auric_show_only_int";
	private static final String INTRUSION_DETECTION_ON = "auric_int_det_on";

	/************* DEFAULTS ************/
	private static final int DEFAULT_FR_MAX = 80;
	private static final int DEFAULT_CAMERA_PERIOD = 15000; // miliseconds
	private static final int DEFAULT_NUMBER_PICS = 1;
	
	private SharedPreferences prefs;

	public SettingsPreferences(Context context) {
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public String getDetectorType() {
		return prefs.getString(DETECTOR_TYPE, DetectorManager.getDefault());
	}

	public void setDetectorType(String newDetector) {
		SharedPreferences.Editor edit = prefs.edit();
		edit.putString(DETECTOR_TYPE, newDetector);
		edit.commit();
	}

	public String getRecorderType() {
		return prefs.getString(RECORDER_TYPE, RecorderManager.getDefault());
	}

	public void setRecorderType(String newRecorder) {
		SharedPreferences.Editor edit = prefs.edit();
		edit.putString(RECORDER_TYPE, newRecorder);
		edit.commit();
	}

	public String getStrategyType() {
		return prefs.getString(STRATEGY_TYPE, StrategyManager.getDefault());
	}

	public void setStrategyType(String newStrategy) {
		SharedPreferences.Editor edit = prefs.edit();
		edit.putString(STRATEGY_TYPE, newStrategy);
		edit.commit();
	}

	public boolean hasPasscode() {
		return prefs.getBoolean(HAS_PASSCODE, false);
	}

	public void setHasPasscode(boolean b) {
		SharedPreferences.Editor edit = prefs.edit();
		edit.putBoolean(HAS_PASSCODE, b);
		edit.commit();
	}

	public String getPasscode() {
		return prefs.getString(PASSCODE, "");
	}

	public void setPasscode(String s) {
		SharedPreferences.Editor edit = prefs.edit();
		edit.putString(PASSCODE, s);
		edit.commit();
	}

	public int getCameraPeriod() {
		return prefs.getInt(CAMERA_PERIOD, DEFAULT_CAMERA_PERIOD);
	}

	public void setCameraPeriod(int newCameraPeriod) {
		SharedPreferences.Editor edit = prefs.edit();
		edit.putInt(CAMERA_PERIOD, newCameraPeriod);
		edit.commit();
	}

	public int getFaceRecognitionMax() {
		return prefs.getInt(FACE_RECOG_MAX, DEFAULT_FR_MAX);
	}

	public void setFaceRecognitionMax(int newFaceRecognitionMax) {
		SharedPreferences.Editor edit = prefs.edit();
		edit.putInt(FACE_RECOG_MAX, newFaceRecognitionMax);
		edit.commit();
	}

	public int getNumberOfPicturesPerDetection() {
		return prefs.getInt(NUMBER_PICS, DEFAULT_NUMBER_PICS);
	}

	public void setNumberOfPicturesPerDetection(int newNumberPics) {
		SharedPreferences.Editor edit = prefs.edit();
		edit.putInt(NUMBER_PICS, newNumberPics);
		edit.commit();
	}

	public boolean hasPreviouslyStarted() {
		return prefs.getBoolean(PREVIOUSLY_STARTED, false);
	}

	public void setPreviouslyStarted() {
		SharedPreferences.Editor edit = prefs.edit();
		edit.putBoolean(PREVIOUSLY_STARTED, true);
		edit.commit();
	}

	public boolean hideNotification() {
		return prefs.getBoolean(HIDE_NOTIFICATION, false);
	}

	public void setHideNotification(boolean b) {
		SharedPreferences.Editor edit = prefs.edit();
		edit.putBoolean(HIDE_NOTIFICATION, b);
		edit.commit();
	}

	public boolean isIntrusionDetectorActive() {
		return prefs.getBoolean(INTRUSION_DETECTION_ON, false);
	}

	public void setIntrusionDetectorActive(boolean b) {
		SharedPreferences.Editor edit = prefs.edit();
		edit.putBoolean(INTRUSION_DETECTION_ON, b);
		edit.commit();
	}

	public boolean showOnlyIntrusionSessions() {
		return prefs.getBoolean(SHOW_ONLY_INT, false);
	}

	public void setShowOnlyIntrusionSessions(boolean b) {
		SharedPreferences.Editor edit = prefs.edit();
		edit.putBoolean(SHOW_ONLY_INT, b);
		edit.commit();
	}
}
