package hcim.auric.database.intrusions;

import hcim.auric.intrusion.Intrusion;
import hcim.auric.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteIntrusionData extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "IntrusionDB";

	private static final String TABLE_INTRUSIONS = "intrusions";
	private static final String KEY_ID = "id";
	private static final String KEY_DATE = "date";
	private static final String KEY_TIME = "time";
	private static final String KEY_TAG = "tag";
	private static final String[] COLUMNS = { KEY_ID, KEY_DATE, KEY_TIME,
			KEY_TAG };

	public SQLiteIntrusionData(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_INTRUSION_TABLE = "CREATE TABLE " + TABLE_INTRUSIONS
				+ " ( " + KEY_ID + " TEXT PRIMARY KEY, " + KEY_DATE + " TEXT, "
				+ KEY_TIME + " TEXT, " + KEY_TAG + " INTEGER )";

		db.execSQL(CREATE_INTRUSION_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_INTRUSIONS);

		this.onCreate(db);
	}

	public void addIntrusion(Intrusion i) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, i.getID());
		values.put(KEY_DATE, i.getDate());
		values.put(KEY_TIME, i.getTime());
		values.put(KEY_TAG, i.getTag());

		db.insert(TABLE_INTRUSIONS, null, values);

		db.close();
	}

	public Intrusion getIntrusion(String id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(true, TABLE_INTRUSIONS, COLUMNS, KEY_ID + "='"
				+ id + "'", null, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		if (cursor.getCount() <= 0) {
			db.close();
			return null;
		}

		Intrusion intrusion = assembleIntrusion(cursor);

		db.close();

		return intrusion;
	}

	/**
	 * Gets all intrusions from a given day
	 * 
	 * @param date
	 *            : the day
	 * @return list of intrusions
	 */
	public List<Intrusion> getAllIntrusionsFromDay(String date) {
		List<Intrusion> result = new ArrayList<Intrusion>();

		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(true, TABLE_INTRUSIONS, COLUMNS, KEY_DATE
				+ "='" + date + "'", null, null, null, null, null);

		if (cursor == null) {
			db.close();
			return null;
		}

		if (cursor.moveToFirst()) {
			if (cursor.getCount() <= 0) {
				db.close();
				return null;
			}
			do {
				result.add(assembleIntrusion(cursor));
			} while (cursor.moveToNext());
		}
		db.close();
		return result;
	}

	public void deleteIntrusion(String id) {
		SQLiteDatabase db = this.getWritableDatabase();

		db.delete(TABLE_INTRUSIONS, KEY_ID + "= '" + id + "'", null);

		db.close();
	}

	public void printAll() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + TABLE_INTRUSIONS, null);
		Intrusion i;
		LogUtils.debug("Print all intrusions:");

		if (cursor.moveToFirst()) {
			if (cursor.getCount() <= 0) {
				db.close();
			}
			do {
				i = assembleIntrusion(cursor);
				LogUtils.debug(i.toString());

			} while (cursor.moveToNext());
		}
		db.close();
	}

	public List<Intrusion> getIntrusions(int severity) {
		List<Intrusion> result = new ArrayList<Intrusion>();

		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(true, TABLE_INTRUSIONS, COLUMNS, KEY_TAG + "="
				+ severity, null, null, null, null, null);

		Intrusion i = null;

		if (cursor.moveToFirst()) {
			if (cursor.getCount() <= 0) {
				db.close();
				return null;
			}
			do {
				i = assembleIntrusion(cursor);
				result.add(i);

			} while (cursor.moveToNext());
		}
		db.close();

		return result;
	}

	public List<Intrusion> getAllIntrusions() {
		List<Intrusion> result = new ArrayList<Intrusion>();
	
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + TABLE_INTRUSIONS, null);
	
		if (cursor.moveToFirst()) {
			if (cursor.getCount() <= 0) {
				db.close();
			}
			do {
				result.add(assembleIntrusion(cursor));
	
			} while (cursor.moveToNext());
		}
		db.close();
	
		return result;
	}

	private Intrusion assembleIntrusion(Cursor cursor) {
		return new Intrusion(cursor.getString(0), cursor.getString(1),
				cursor.getString(2), cursor.getInt(3));
	}

	public int numberOfIntrusions(int severity) {
		List<Intrusion> list = getIntrusions(severity);

		return list == null ? 0 : list.size();
	}

	public void updateIntrusionTag(Intrusion i) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_TAG, i.getTag());
		db.update(TABLE_INTRUSIONS, values, KEY_ID + "='" + i.getID() + "'",
				null);

		db.close();

	}

	public void deleteAll() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE_INTRUSIONS);
		db.close();
	}
}