package hcim.auric.database;

import hcim.auric.intrusion.Intrusion;
import hcim.auric.intrusion.IntrusionFactory;

import java.util.ArrayList;
import java.util.LinkedList;
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
	private static final String KEY_LOG = "log_type";
	private static final String KEY_TAG = "tag";
	private static final String[] COLUMNS = { KEY_ID, KEY_DATE, KEY_TIME,
			KEY_TAG, KEY_LOG };

	public SQLiteIntrusionData(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_INTRUSION_TABLE = "CREATE TABLE " + TABLE_INTRUSIONS
				+ " ( " + KEY_ID + " TEXT PRIMARY KEY, " + KEY_DATE + " TEXT, "
				+ KEY_TIME + " TEXT, " + KEY_TAG + " INTEGER, " + KEY_LOG
				+ " TEXT )";

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
		values.put(KEY_LOG, i.getLogType());

		db.insert(TABLE_INTRUSIONS, null, values);

		db.close();
	}

	public Intrusion getIntrusion(String id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(true, TABLE_INTRUSIONS, COLUMNS, KEY_ID + "='"
				+ id + "'", null, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		if (cursor.getCount() <= 0)
			return null;

		Intrusion intrusion = IntrusionFactory.createIntrusion(
				cursor.getString(0), cursor.getString(1), cursor.getString(2),
				cursor.getInt(3), cursor.getString(4));

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
		List<Intrusion> result = new LinkedList<Intrusion>();

		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(true, TABLE_INTRUSIONS, COLUMNS, KEY_DATE
				+ "='" + date + "'", null, null, null, null, null);

		Intrusion i = null;

		if (cursor == null)
			return null;

		if (cursor.moveToFirst()) {
			if (cursor.getCount() <= 0)
				return null;
			do {
				i = IntrusionFactory.createIntrusion(cursor.getString(0),
						cursor.getString(1), cursor.getString(2),
						cursor.getInt(3), cursor.getString(4));

				result.add(i);
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

	public List<Intrusion> getFalseIntrusions() {
		List<Intrusion> result = new ArrayList<Intrusion>();

		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(true, TABLE_INTRUSIONS, COLUMNS, KEY_TAG + "="
				+ Intrusion.FALSE_INTRUSION, null, null, null, null, null);

		Intrusion i = null;

		if (cursor.moveToFirst()) {
			if (cursor.getCount() <= 0)
				return null;
			do {
				i = IntrusionFactory.createIntrusion(cursor.getString(0),
						cursor.getString(1), cursor.getString(2),
						cursor.getInt(3), cursor.getString(4));
				result.add(i);

			} while (cursor.moveToNext());
		}
		db.close();

		return result;
	}

	public int numberOfFalseIntrusions() {
		List<Intrusion> list = getFalseIntrusions();

		return list == null ? 0 : list.size();
	}

	public List<Intrusion> getRealIntrusions() {
		List<Intrusion> result = new ArrayList<Intrusion>();

		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(true, TABLE_INTRUSIONS, COLUMNS, KEY_TAG + "="
				+ Intrusion.REAL_INTRUSION, null, null, null, null, null);

		Intrusion i = null;

		if (cursor.moveToFirst()) {
			if (cursor.getCount() <= 0)
				return null;
			do {
				i = IntrusionFactory.createIntrusion(cursor.getString(0),
						cursor.getString(1), cursor.getString(2),
						cursor.getInt(3), cursor.getString(4));
				result.add(i);

			} while (cursor.moveToNext());
		}
		db.close();

		return result;
	}

	public int numberOfRealIntrusions() {
		List<Intrusion> list = getRealIntrusions();

		return list.size();
	}

	public List<Intrusion> getUncheckedIntrusions() {
		List<Intrusion> result = new ArrayList<Intrusion>();

		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(true, TABLE_INTRUSIONS, COLUMNS, KEY_TAG + "="
				+ Intrusion.UNCHECKED, null, null, null, null, null);

		Intrusion i = null;

		if (cursor.moveToFirst()) {
			if (cursor.getCount() <= 0)
				return null;
			do {
				i = IntrusionFactory.createIntrusion(cursor.getString(0),
						cursor.getString(1), cursor.getString(2),
						cursor.getInt(3), cursor.getString(4));
				result.add(i);

			} while (cursor.moveToNext());
		}
		db.close();

		return result;
	}

	public int numberOfUncheckedIntrusions() {
		List<Intrusion> list = getUncheckedIntrusions();

		return list.size();
	}

	public void updateIntrusionTag(Intrusion i) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_TAG, i.getTag());
		db.update(TABLE_INTRUSIONS, values, KEY_ID + "='" + i.getID() + "'",
				null);

		db.close();

	}
}