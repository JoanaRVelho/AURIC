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
	private static final String CAMERA_CAPTURE_ID = "camera";
	private static final String SCREENSHOT_ID = "screenshot";
	private static final String PASSCODE_ID = "passcode";

	/** Device Sharing Modes **/
	public static final String SHARE_MODE = "yes";
	public static final String NONSHARE_MODE = "no";

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
			updateDeviceSharingMode(false);
			return false;
		}

		String mode = cursor.getString(1);

		if (mode == null) {
			updateDeviceSharingMode(false);
			return false;
		}
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

		if (cursor.getCount() <= 0)
			return null;

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

	public int getCameraCaptureOption() {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_STATE, COLUMNS, KEY_ID + "='"
				+ CAMERA_CAPTURE_ID + "'", null, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		if (cursor.getCount() <= 0)
			return -1;

		String mode = cursor.getString(1);
		db.close();

		return Integer.parseInt(mode);
	}

	public void insertCameraCaptureOption(int period) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, CAMERA_CAPTURE_ID);
		values.put(KEY_MODE, period + "");

		db.insert(TABLE_STATE, null, values);

		db.close();
	}

	public void updateCameraCaptureOption(int period) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_MODE, period + "");

		db.update(TABLE_STATE, values, KEY_ID + " = '" + CAMERA_CAPTURE_ID
				+ "'", null);

		db.close();
	}

	public int getScreenshotOption() {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_STATE, COLUMNS, KEY_ID + "='"
				+ SCREENSHOT_ID + "'", null, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		if (cursor.getCount() <= 0)
			return -1;

		String mode = cursor.getString(1);
		
		db.close();

		return Integer.parseInt(mode);
	}

	public void insertScreenshotOption(int period) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, SCREENSHOT_ID);
		values.put(KEY_MODE, period + "");

		db.insert(TABLE_STATE, null, values);

		db.close();
	}

	public void updateScreenshotOption(int s) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_MODE, s + "");

		db.update(TABLE_STATE, values, KEY_ID + " = '" + SCREENSHOT_ID + "'",
				null);

		db.close();
	}

	public void deletePasscode() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_STATE, KEY_ID + "= '" + PASSCODE_ID+ "'", null);
		db.close();
	}
}