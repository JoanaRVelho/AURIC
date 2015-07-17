package hcim.auric.data;

import hcim.auric.Intrusion;
import hcim.auric.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class IntrusionTable {
	static final String TABLE = "intrusions";

	static final String KEY_ID = "id";
	static final String KEY_DATE = "date";
	static final String KEY_TIME = "time";
	static final String KEY_TAG = "tag";
	static final String KEY_SESSION = "session";

	private static final String[] COLUMNS = { KEY_ID, KEY_DATE, KEY_TIME,
			KEY_TAG, KEY_SESSION };

	static final String CREATE = "CREATE TABLE " + TABLE + " ( " + KEY_ID
			+ " TEXT PRIMARY KEY, " + KEY_DATE + " TEXT, " + KEY_TIME
			+ " TEXT, " + KEY_TAG + " INTEGER, " + KEY_SESSION + " TEXT )";

	private SQLiteAdapter adapter;

	public IntrusionTable(Context c) {
		adapter = SQLiteAdapter.getInstance(c);
	}

	public void addIntrusion(Intrusion i, String sessionID) {
		SQLiteDatabase db = adapter.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, i.getID());
		values.put(KEY_DATE, i.getDate());
		values.put(KEY_TIME, i.getTime());
		values.put(KEY_TAG, i.getTag());
		values.put(KEY_SESSION, sessionID);

		db.insert(TABLE, null, values);
	}

	public Intrusion getIntrusion(String intrusionID) {
		SQLiteDatabase db = adapter.getReadableDatabase();

		Cursor cursor = db.query(true, TABLE, COLUMNS, KEY_ID + "='"
				+ intrusionID + "'", null, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		if (cursor.getCount() <= 0) {
			return null;
		}

		Intrusion intrusion = assembleIntrusion(cursor);

		return intrusion;
	}

	public void deleteIntrusion(String intrusionID) {
		SQLiteDatabase db = adapter.getWritableDatabase();

		db.delete(TABLE, KEY_ID + "= '" + intrusionID + "'", null);
	}

	/**
	 * Intrusions from a session without pictures
	 * 
	 * @param sessionID
	 *            : session ID
	 * @return list of intrusion from a session without pictures
	 */
	public List<Intrusion> getIntrusionsFromSession(String sessionID) {
		List<Intrusion> result = new ArrayList<Intrusion>();

		SQLiteDatabase db = adapter.getReadableDatabase();

		Cursor cursor = db.query(true, TABLE, COLUMNS, KEY_SESSION + "='"
				+ sessionID + "'", null, null, null, null, null);

		if (cursor == null) {
			return null;
		}

		if (cursor.moveToFirst()) {
			if (cursor.getCount() <= 0) {
				return null;
			}
			do {
				result.add(assembleIntrusion(cursor));
			} while (cursor.moveToNext());
		}
		return result;
	}

	public List<Intrusion> getAllIntrusions() {
		List<Intrusion> result = new ArrayList<Intrusion>();

		SQLiteDatabase db = adapter.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + TABLE, null);

		if (cursor.moveToFirst()) {
			if (cursor.getCount() <= 0) {
				return null;
			}
			do {
				result.add(assembleIntrusion(cursor));

			} while (cursor.moveToNext());
		}
		return result;
	}

	public void printAll() {
		SQLiteDatabase db = adapter.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + TABLE, null);
		Intrusion i;
		LogUtils.debug("Print all intrusions:");

		if (cursor.moveToFirst()) {
			if (cursor.getCount() <= 0) {
				return;
			}
			do {
				i = assembleIntrusion(cursor);
				LogUtils.debug(i.toString());

			} while (cursor.moveToNext());
		}
	}

	public void deleteAll() {
		SQLiteDatabase db = adapter.getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE);
	}

	private Intrusion assembleIntrusion(Cursor cursor) {
		return new Intrusion(cursor.getString(0), cursor.getString(1),
				cursor.getString(2), cursor.getInt(3), cursor.getString(4));
	}

	public void deleteIntrusionsFromSession(String sessionID) {
		SQLiteDatabase db = adapter.getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE + " WHERE " + KEY_SESSION + "='"
				+ sessionID + "'");
	}
}
