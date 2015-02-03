package hcim.auric.database;

import hcim.auric.activities.apps.ApplicationData;
import hcim.auric.utils.Converter;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteTargetApps extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "applicationsDB";

	private static final String TABLE_APPS = "applications";
	private static final String KEY_ID = "id";
	private static final String KEY_DATA = "app_data";
	private static final String[] COLUMNS = { KEY_ID, KEY_DATA };

	public SQLiteTargetApps(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_PICTURES_TABLE = "CREATE TABLE " + TABLE_APPS + " ( "
				+ KEY_ID + " TEXT, " + KEY_DATA + " BLOB )";

		db.execSQL(CREATE_PICTURES_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_APPS);
		this.onCreate(db);
	}

	public int numberOfApps() {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.rawQuery("SELECT  * FROM " + TABLE_APPS, null);
		int result = cursor.getCount();

		cursor.close();
		db.close();

		return result;

	}

	public void insertApplication(ApplicationData app) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, app.getPackageName());
		values.put(KEY_DATA, Converter.serialize(app));

		db.insert(TABLE_APPS, null, values);

		db.close();
	}

	public void removeApplication(ApplicationData app) {
		SQLiteDatabase db = this.getWritableDatabase();

		db.delete(TABLE_APPS, KEY_ID + "= '" + app.getPackageName() + "'", null);

		db.close();
	}

	public ApplicationData getApplication(String packageName) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_APPS, COLUMNS, KEY_ID + "='"
				+ packageName + "'", null, null, null, null, null);

		if (cursor == null)
			return null;
		
		cursor.moveToFirst();

		if (cursor.getCount() <= 0)
			return null;

		ApplicationData app = (ApplicationData) Converter.deserialize(cursor
				.getBlob(1));

		db.close();
		return app;
	}

	/**
	 * Get all target applications
	 * 
	 * @return
	 */
	public List<ApplicationData> getAllApplications() {
		List<ApplicationData> result = new ArrayList<ApplicationData>();

		String query = "SELECT  * FROM " + TABLE_APPS;

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		ApplicationData app = null;

		if (cursor.moveToFirst()) {
			if (cursor.getCount() <= 0)
				return null;
			do {
				app = (ApplicationData) Converter
						.deserialize(cursor.getBlob(1));
				result.add(app);

			} while (cursor.moveToNext());
		}
		db.close();

		return result;
	}

	public boolean hasApplication(String packageName) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_APPS, COLUMNS, KEY_ID + "='"
				+ packageName + "'", null, null, null, null, null);

		if (cursor == null)
			return false;

		cursor.moveToFirst();

		if (cursor.getCount() <= 0)
			return false;

		db.close();
		return true;
	}
}
