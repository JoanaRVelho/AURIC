package hcim.auric.database.configs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteState extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "AuricStateDB";

	private static final String TABLE_STATE = "auricstate";

	/** Columns Names **/
	private static final String KEY_ID = "id";
	private static final String KEY_VALUE = "mode";
	private static final String[] COLUMNS = { KEY_ID, KEY_VALUE };

	/** IDs **/
	private static final String DETECTOR_TYPE = "detector_type";
	private static final String RECORDER_TYPE = "recorder_type";
	private static final String STRATEGY_TYPE = "strategy_type";

	private static final String NUMBER_PICS = "number_pics";
	private static final String RECORD_ALL = "record_all_int";
	private static final String SHARING_ID = "share";
	private static final String PASSCODE_ID = "passcode";
	private static final String ON_OFF = "on_off";
	private static final String FR_MAX = "fr_max";
	private static final String CAM_PERIOD = "cam_period";
	private static final String HIDE_NOT = "hide_notification";
	private static final String SHOW_ALL = "show_all";

	/** Device Sharing Modes **/
	public static final String SHARE_MODE = "yes";
	public static final String NONSHARE_MODE = "no";

	/** ON / OFF **/
	public static final String ON = "on";
	public static final String OFF = "off";

	public static final String TRUE = "true";
	public static final String FALSE = "false";

	public SQLiteState(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String table = "CREATE TABLE " + TABLE_STATE + " ( " + KEY_ID
				+ " TEXT PRIMARY KEY, " + KEY_VALUE + " TEXT )";

		db.execSQL(table);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATE);

		this.onCreate(db);
	}

	public int getFaceRecognitionMax() {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_STATE, COLUMNS, KEY_ID + "='" + FR_MAX
				+ "'", null, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		if (cursor.getCount() <= 0) {
			db.close();
			return -1;
		}
		String max = cursor.getString(1);
		db.close();

		return Integer.parseInt(max);
	}

	public void insertFaceRecognitionMax(int max) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, FR_MAX);
		values.put(KEY_VALUE, max + "");

		db.insert(TABLE_STATE, null, values);

		db.close();

	}

	public void updateFaceRecognitionMax(int max) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, FR_MAX);
		values.put(KEY_VALUE, max + "");

		db.update(TABLE_STATE, values, KEY_ID + " = '" + FR_MAX + "'", null);

		db.close();
	}

	public boolean hideNotification() {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_STATE, COLUMNS, KEY_ID + "='" + HIDE_NOT
				+ "'", null, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		if (cursor.getCount() <= 0) {
			db.close();
			insertHideNotification(false);
			return false;
		}
		String value = cursor.getString(1);
		db.close();

		return value.equals(TRUE);
	}

	public void insertHideNotification(boolean b) {
		SQLiteDatabase db = this.getWritableDatabase();
		String value = b ? TRUE : FALSE;

		ContentValues values = new ContentValues();
		values.put(KEY_ID, HIDE_NOT);
		values.put(KEY_VALUE, value);

		db.insert(TABLE_STATE, null, values);

		db.close();

	}

	public void updateHideNotification(boolean b) {
		SQLiteDatabase db = this.getWritableDatabase();
		String value = b ? TRUE : FALSE;

		ContentValues values = new ContentValues();
		values.put(KEY_ID, HIDE_NOT);
		values.put(KEY_VALUE, value);

		db.update(TABLE_STATE, values, KEY_ID + " = '" + HIDE_NOT + "'", null);

		db.close();
	}

	public int getCameraPeriod() {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_STATE, COLUMNS, KEY_ID + "='"
				+ CAM_PERIOD + "'", null, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		if (cursor.getCount() <= 0) {
			db.close();
			return -1;
		}

		String camera = cursor.getString(1);
		db.close();

		return Integer.parseInt(camera);
	}

	public void updateCameraPeriod(int camera) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, CAM_PERIOD);
		values.put(KEY_VALUE, camera + "");

		db.update(TABLE_STATE, values, KEY_ID + " = '" + CAM_PERIOD + "'", null);

		db.close();
	}

	public void insertCameraPeriod(int camera) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, CAM_PERIOD);
		values.put(KEY_VALUE, camera + "");

		db.insert(TABLE_STATE, null, values);

		db.close();
	}

	public String getDetectorType() {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_STATE, COLUMNS, KEY_ID + "='"
				+ DETECTOR_TYPE + "'", null, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		if (cursor.getCount() <= 0) {
			db.close();
			return null;
		}

		String mode = cursor.getString(1);
		db.close();

		return mode;
	}

	public void insertDetectorType(String mode) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, DETECTOR_TYPE);
		values.put(KEY_VALUE, mode);

		db.insert(TABLE_STATE, null, values);

		db.close();
	}

	public void updateDetector(String mode) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_VALUE, mode);

		db.update(TABLE_STATE, values, KEY_ID + " = '" + DETECTOR_TYPE + "'",
				null);

		db.close();
	}

	public String getRecorderType() {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_STATE, COLUMNS, KEY_ID + "='"
				+ RECORDER_TYPE + "'", null, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		if (cursor.getCount() <= 0) {
			db.close();
			return null;
		}

		String recorder = cursor.getString(1);

		db.close();

		return recorder;
	}

	public void insertRecorderType(String recorder) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, RECORDER_TYPE);
		values.put(KEY_VALUE, recorder);

		db.insert(TABLE_STATE, null, values);

		db.close();
	}

	public void updateRecorderType(String recorder) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_VALUE, recorder);

		db.update(TABLE_STATE, values, KEY_ID + " = '" + RECORDER_TYPE + "'",
				null);

		db.close();
	}

	public String getStrategyType() {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_STATE, COLUMNS, KEY_ID + "='"
				+ STRATEGY_TYPE + "'", null, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		if (cursor.getCount() <= 0) {
			db.close();
			return null;
		}

		String s = cursor.getString(1);

		db.close();

		return s;
	}

	public void insertStrategyType(String strategy) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, STRATEGY_TYPE);
		values.put(KEY_VALUE, strategy);

		db.insert(TABLE_STATE, null, values);

		db.close();
	}

	public void updateStrategyType(String strategy) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_VALUE, strategy);

		db.update(TABLE_STATE, values, KEY_ID + " = '" + STRATEGY_TYPE + "'",
				null);

		db.close();

	}

	public boolean getDeviceSharingMode() {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_STATE, COLUMNS, KEY_ID + "='"
				+ SHARING_ID + "'", null, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		if (cursor.getCount() <= 0) {
			db.close();
			insertDeviceSharingMode(false);
			return false;
		}

		String mode = cursor.getString(1);

		db.close();

		return mode.equals(SHARE_MODE);
	}

	public void insertDeviceSharingMode(boolean b) {
		SQLiteDatabase db = this.getWritableDatabase();

		String mode = b ? SHARE_MODE : NONSHARE_MODE;

		ContentValues values = new ContentValues();
		values.put(KEY_ID, SHARING_ID);
		values.put(KEY_VALUE, mode);

		db.insert(TABLE_STATE, null, values);

		db.close();
	}

	public void updateDeviceSharingMode(boolean b) {
		SQLiteDatabase db = this.getWritableDatabase();

		String mode = b ? SHARE_MODE : NONSHARE_MODE;

		ContentValues values = new ContentValues();
		values.put(KEY_VALUE, mode);

		db.update(TABLE_STATE, values, KEY_ID + " = '" + SHARING_ID + "'", null);

		db.close();
	}

	public String getPasscode() {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_STATE, COLUMNS, KEY_ID + "='"
				+ PASSCODE_ID + "'", null, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		if (cursor.getCount() <= 0) {
			db.close();
			return null;
		}
		String mode = cursor.getString(1);
		db.close();

		return mode;
	}

	public void insertPasscode(String s) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, PASSCODE_ID);
		values.put(KEY_VALUE, s);

		db.insert(TABLE_STATE, null, values);

		db.close();
	}

	public void updatePasscode(String s) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_VALUE, s);

		db.update(TABLE_STATE, values, KEY_ID + " = '" + PASSCODE_ID + "'",
				null);

		db.close();
	}

	public void deletePasscode() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_STATE, KEY_ID + "= '" + PASSCODE_ID + "'", null);
		db.close();
	}

	public boolean isIntrusionDetectorActive() {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_STATE, COLUMNS, KEY_ID + "='" + ON_OFF
				+ "'", null, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		if (cursor.getCount() <= 0) {
			db.close();
			turnIntrusionDetectorOff();
			return false;
		}

		String mode = cursor.getString(1);

		return mode.equals(ON);
	}

	private void turnIntrusionDetectorOff() {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, ON_OFF);
		values.put(KEY_VALUE, OFF);

		db.insert(TABLE_STATE, null, values);

		db.close();
	}

	public void setIntrusionDetectorState(boolean b) {
		SQLiteDatabase db = this.getWritableDatabase();

		String mode = b ? ON : OFF;

		ContentValues values = new ContentValues();
		values.put(KEY_VALUE, mode);

		db.update(TABLE_STATE, values, KEY_ID + " = '" + ON_OFF + "'", null);

		db.close();
	}

	public int getNumberOfPicturesPerDetection() {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_STATE, COLUMNS, KEY_ID + "='"
				+ NUMBER_PICS + "'", null, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		if (cursor.getCount() <= 0) {
			db.close();
			return -1;
		}

		String mode = cursor.getString(1);

		return Integer.parseInt(mode);
	}

	public void insertNumberOfPicturesPerDetection(int n) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, NUMBER_PICS);
		values.put(KEY_VALUE, n + "");

		db.insert(TABLE_STATE, null, values);

		db.close();
	}

	public void updateNumberOfPicturesPerDetection(int n) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_VALUE, n + "");

		db.update(TABLE_STATE, values, KEY_ID + " = '" + NUMBER_PICS + "'",
				null);

		db.close();
	}

	public boolean getRecordAllInteractions() {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_STATE, COLUMNS, KEY_ID + "='"
				+ RECORD_ALL + "'", null, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		if (cursor.getCount() <= 0) {
			db.close();
			insertRecordAllInteractions(false);
			return false;
		}

		String mode = cursor.getString(1);

		return mode.equals(TRUE);
	}

	public void insertRecordAllInteractions(boolean b) {
		SQLiteDatabase db = this.getWritableDatabase();

		String mode = b ? TRUE : FALSE;

		ContentValues values = new ContentValues();
		values.put(KEY_ID, RECORD_ALL);
		values.put(KEY_VALUE, mode);

		db.insert(TABLE_STATE, null, values);

		db.close();
	}

	public void updateRecordAllInteractions(boolean b) {
		SQLiteDatabase db = this.getWritableDatabase();

		String mode = b ? TRUE : FALSE;

		ContentValues values = new ContentValues();
		values.put(KEY_VALUE, mode);

		db.update(TABLE_STATE, values, KEY_ID + " = '" + RECORD_ALL + "'", null);

		db.close();
	}

	public boolean showAllSessions() {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_STATE, COLUMNS, KEY_ID + "='" + SHOW_ALL
				+ "'", null, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		if (cursor.getCount() <= 0) {
			db.close();
			insertShowAllSessions(true);
			return true;
		}

		String mode = cursor.getString(1);

		return mode.equals(TRUE);
	}

	private void insertShowAllSessions(boolean b) {
		SQLiteDatabase db = this.getWritableDatabase();

		String mode = b ? TRUE : FALSE;

		ContentValues values = new ContentValues();
		values.put(KEY_ID, SHOW_ALL);
		values.put(KEY_VALUE, mode);

		db.insert(TABLE_STATE, null, values);

		db.close();
	}

	public void updateShowAllSessions(boolean b) {
		SQLiteDatabase db = this.getWritableDatabase();

		String mode = b ? TRUE : FALSE;

		ContentValues values = new ContentValues();
		values.put(KEY_VALUE, mode);

		db.update(TABLE_STATE, values, KEY_ID + " = '" + SHOW_ALL + "'", null);

		db.close();
	}

}