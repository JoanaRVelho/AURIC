package hcim.auric.data;

import hcim.auric.Session;
import hcim.auric.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SessionTable {

	static final String TABLE = "sessions_intrusions";
	private static final String KEY_ID = "id";
	private static final String KEY_DATE = "date";
	private static final String KEY_TIME = "time";
	private static final String KEY_TAG = "tag";
	private static final String KEY_RECORD = "record_type";

	private static final String[] COLUMNS = { KEY_ID, KEY_DATE, KEY_TIME,
			KEY_RECORD, KEY_TAG };

	private static final String INTRUSION = "intrusion";
	private static final String FALSE_INTRUSION = "false_intrusion";

	static final String CREATE = "CREATE TABLE " + TABLE + " ( " + KEY_ID
			+ " TEXT PRIMARY KEY, " + KEY_DATE + " TEXT, " + KEY_TIME
			+ " TEXT, " + KEY_RECORD + " TEXT, " + KEY_TAG + " TEXT )";
	private SQLiteAdapter adapter;

	public SessionTable(Context c) {
		adapter = SQLiteAdapter.getInstance(c);
	}

	public void insertSession(Session s) {
		SQLiteDatabase db = adapter.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, s.getID());
		values.put(KEY_DATE, s.getDate());
		values.put(KEY_TIME, s.getTime());
		values.put(KEY_RECORD, s.getRecorderType());
		values.put(KEY_TAG, s.isIntrusion() ? INTRUSION : FALSE_INTRUSION);

		db.insert(TABLE, null, values);
	}

	public void deleteSession(String id) {
		SQLiteDatabase db = adapter.getWritableDatabase();

		db.delete(TABLE, KEY_ID + "= '" + id + "'", null);
	}

	public Session getSession(String id) {
		SQLiteDatabase db = adapter.getReadableDatabase();

		Cursor cursor = db.query(true, TABLE, COLUMNS,
				KEY_ID + "='" + id + "'", null, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		if (cursor.getCount() <= 0) {
			return null;
		}
		Session result = assembleSession(cursor);

		return result;
	}

	public List<Session> getSessionsFromDay(String date) {
		List<Session> result = new ArrayList<Session>();
		SQLiteDatabase db = adapter.getReadableDatabase();

		Cursor cursor = db.query(true, TABLE, COLUMNS, KEY_DATE + "='" + date
				+ "'", null, null, null, null, null);

		if (cursor == null) {
			return null;
		}

		if (cursor.moveToFirst()) {
			if (cursor.getCount() <= 0) {
				return null;
			}
			do {
				result.add(assembleSession(cursor));
			} while (cursor.moveToNext());
		}
		return result;
	}

	public List<Session> getIntrusionSessionsFromDay(String date) {
		List<Session> result = new ArrayList<Session>();

		SQLiteDatabase db = adapter.getReadableDatabase();

		Cursor cursor = db.query(true, TABLE, COLUMNS, KEY_DATE + "='" + date
				+ "' AND " + KEY_TAG + "='" + INTRUSION + "'", null, null,
				null, null, null);

		Session s;

		if (cursor == null) {
			return null;
		}

		if (cursor.moveToFirst()) {
			if (cursor.getCount() <= 0) {
				return null;
			}
			do {
				s = assembleSession(cursor);
				result.add(s);
			} while (cursor.moveToNext());
		}
		return result;
	}

	public void printAll() {
		SQLiteDatabase db = adapter.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + TABLE, null);
		LogUtils.debug("print all sessions");

		if (cursor.moveToFirst()) {
			if (cursor.getCount() <= 0) {
				return;
			}
			do {
				LogUtils.debug(assembleSession(cursor).toString());

			} while (cursor.moveToNext());
		}
	}

	public String getRecorderType(String id) {
		Session s = getSession(id);
		return s.getRecorderType();
	}

	public void deleteAll() {
		SQLiteDatabase db = adapter.getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE);
	}

	public boolean exists(String sessionID) {
		return getSession(sessionID) != null;
	}

	private Session assembleSession(Cursor cursor) {
		String id = cursor.getString(0);
		String date = cursor.getString(1);
		String time = cursor.getString(2);
		String recorder = cursor.getString(3);
		boolean tag = cursor.getString(4).equals(INTRUSION);

		return new Session(id, date, time, recorder, tag);
	}

}