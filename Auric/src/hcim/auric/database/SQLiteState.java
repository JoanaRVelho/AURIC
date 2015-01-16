package hcim.auric.database;

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
	private static final String KEY_MODE = "mode";
	private static final String[] COLUMNS = { KEY_ID, KEY_MODE };

	/** IDs **/
	private static final String AURIC_MODE_ID = "auric_mode";
	private static final String SHARING_ID = "share";
	private static final String PASSCODE_ID = "passcode";
	private static final String LOG_TYPE = "log_type";
	private static final String ON_OFF = "on_off";
	private static final String FR_MAX = "fr_max";
	private static final String CAM_PERIOD = "cam_period";

	/** Device Sharing Modes **/
	public static final String SHARE_MODE = "yes";
	public static final String NONSHARE_MODE = "no";

	/** ON / OFF **/
	public static final String ON = "on";
	public static final String OFF = "off";

	public SQLiteState(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String table = "CREATE TABLE " + TABLE_STATE + " ( " + KEY_ID
				+ " TEXT PRIMARY KEY, " + KEY_MODE + " TEXT )";

		db.execSQL(table);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATE);

		this.onCreate(db);
	}

	public int getFaceRecognitionMax() {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_STATE, COLUMNS, KEY_ID + "='"
				+ FR_MAX + "'", null, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		if (cursor.getCount() <= 0)
			return -1;

		String max = cursor.getString(1);
		db.close();

		return Integer.parseInt(max);
	}

	public void updateFaceRecognitionMax(int max) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, FR_MAX);
		values.put(KEY_MODE, max+"");

		db.update(TABLE_STATE, values, KEY_ID + " = '" + FR_MAX + "'", null);

		db.close();
	}
	
	public void insertFaceRecognitionMax(int max) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, FR_MAX);
		values.put(KEY_MODE, max+"");

		db.insert(TABLE_STATE, null, values);

		db.close();
		
	}

	public int getCameraPeriod() {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_STATE, COLUMNS, KEY_ID + "='"
				+ CAM_PERIOD + "'", null, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		if (cursor.getCount() <= 0)
			return -1;

		String camera = cursor.getString(1);
		db.close();

		return Integer.parseInt(camera);
	}

	public void updateCameraPeriod(int camera) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, CAM_PERIOD);
		values.put(KEY_MODE, camera+"");

		db.update(TABLE_STATE, values, KEY_ID + " = '" + CAM_PERIOD + "'", null);

		db.close();
	}
	
	public void insertCameraPeriod(int camera) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, CAM_PERIOD);
		values.put(KEY_MODE, camera+"");

		db.insert(TABLE_STATE, null, values);

		db.close();
		
	}

	public String getMode() {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_STATE, COLUMNS, KEY_ID + "='"
				+ AURIC_MODE_ID + "'", null, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		if (cursor.getCount() <= 0)
			return null;

		String mode = cursor.getString(1);
		db.close();

		return mode;
	}

	public void insertMode(String mode) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, AURIC_MODE_ID);
		values.put(KEY_MODE, mode);

		db.insert(TABLE_STATE, null, values);

		db.close();
	}

	public void updateMode(String mode) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_MODE, mode);

		db.update(TABLE_STATE, values, KEY_ID + " = '" + AURIC_MODE_ID + "'",
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
		values.put(KEY_MODE, mode);

		db.insert(TABLE_STATE, null, values);

		db.close();
	}

	public void updateDeviceSharingMode(boolean b) {
		SQLiteDatabase db = this.getWritableDatabase();

		String mode = b ? SHARE_MODE : NONSHARE_MODE;

		ContentValues values = new ContentValues();
		values.put(KEY_MODE, mode);

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
		values.put(KEY_MODE, s);

		db.insert(TABLE_STATE, null, values);

		db.close();
	}

	public void updatePasscode(String s) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_MODE, s);

		db.update(TABLE_STATE, values, KEY_ID + " = '" + PASSCODE_ID + "'",
				null);

		db.close();
	}

	public void deletePasscode() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_STATE, KEY_ID + "= '" + PASSCODE_ID + "'", null);
		db.close();
	}

	public String getLogType() {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_STATE, COLUMNS, KEY_ID + "='" + LOG_TYPE
				+ "'", null, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		if (cursor.getCount() <= 0) {
			db.close();
			return null;
		}

		String log = cursor.getString(1);

		db.close();

		return log;
	}

	public void insertLogType(String log) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, LOG_TYPE);
		values.put(KEY_MODE, log);

		db.insert(TABLE_STATE, null, values);

		db.close();
	}

	public void updateLogType(String log) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_MODE, log);

		db.update(TABLE_STATE, values, KEY_ID + " = '" + LOG_TYPE + "'", null);

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
		values.put(KEY_MODE, OFF);

		db.insert(TABLE_STATE, null, values);

		db.close();
	}

	public void setIntrusionDetectorState(boolean b) {
		SQLiteDatabase db = this.getWritableDatabase();

		String mode = b ? ON : OFF;

		ContentValues values = new ContentValues();
		values.put(KEY_MODE, mode);

		db.update(TABLE_STATE, values, KEY_ID + " = '" + ON_OFF + "'", null);

		db.close();
	}

}