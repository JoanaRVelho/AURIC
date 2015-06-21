package hcim.auric.database.intrusions;

import hcim.auric.intrusion.Session;
import hcim.auric.utils.Converter;
import hcim.auric.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteSession extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "SessionIntrsionDB";

	private static final String TABLE_SESSIONS = "sessions_intrusions";
	private static final String KEY_ID = "id";
	private static final String KEY_DATE = "date";
	private static final String KEY_TIME = "time";
	private static final String KEY_INT = "intrusions";
	private static final String KEY_TAG = "tag";
	private static final String KEY_RECORD = "record_type";

	private static final String[] COLUMNS = { KEY_ID, KEY_DATE, KEY_TIME,
			KEY_RECORD, KEY_INT, KEY_TAG };

	private static final String INTRUSION = "intrusion";
	private static final String FALSE_INTRUSION = "false_intrusion";

	public SQLiteSession(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_SESSION_TABLE = "CREATE TABLE " + TABLE_SESSIONS + " ( "
				+ KEY_ID + " TEXT PRIMARY KEY, " + KEY_DATE + " TEXT, "
				+ KEY_TIME + " TEXT, " + KEY_RECORD + " TEXT, " + KEY_INT
				+ " BLOB, " + KEY_TAG + " TEXT )";

		db.execSQL(CREATE_SESSION_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSIONS);

		this.onCreate(db);
	}

	public void insertSession(Session s) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, s.getID());
		values.put(KEY_DATE, s.getDate());
		values.put(KEY_TIME, s.getTime());
		values.put(KEY_RECORD, s.getRecorderType());
		values.put(KEY_INT, Converter.serialize(s.getIntrusionIDs()));
		values.put(KEY_TAG, s.isIntrusion() ? INTRUSION : FALSE_INTRUSION);

		db.insert(TABLE_SESSIONS, null, values);

		db.close();
	}

	public Session getSession(String id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(true, TABLE_SESSIONS, COLUMNS, KEY_ID + "='"
				+ id + "'", null, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		if (cursor.getCount() <= 0) {
			db.close();
			return null;
		}
		Session result = assembleSession(cursor);
		db.close();

		return result;
	}

	public List<Session> getAllSessionsFromDay(String date) {
		List<Session> result = new ArrayList<Session>();
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(true, TABLE_SESSIONS, COLUMNS, KEY_DATE + "='"
				+ date + "'", null, null, null, null, null);

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

	public List<Session> getAllIntrusionSessionsFromDay(String date) {
		List<Session> result = new ArrayList<Session>();

		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(true, TABLE_SESSIONS, COLUMNS, KEY_DATE + "='"
				+ date + "'", null, null, null, null, null);

		Session s;

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
				s = assembleSession(cursor);
				if (s.isIntrusion()) {
					result.add(s);
				}
			} while (cursor.moveToNext());
		}
		db.close();
		return result;
	}

	public void deleteSession(String id) {
		SQLiteDatabase db = this.getWritableDatabase();

		db.delete(TABLE_SESSIONS, KEY_ID + "= '" + id + "'", null);

		db.close();
	}

	public void printAll() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + TABLE_SESSIONS, null);
		LogUtils.debug("print all sessions");

		if (cursor.moveToFirst()) {
			if (cursor.getCount() <= 0) {
				db.close();
			}
			do {
				LogUtils.debug(assembleSession(cursor).toString());

			} while (cursor.moveToNext());
		}
		db.close();
	}

	public String getRecorderType(String id) {
		Session s = getSession(id);
		return s.getRecorderType();
	}

	public void deleteAll() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE_SESSIONS);
		db.close();
	}

	@SuppressWarnings("unchecked")
	private Session assembleSession(Cursor cursor) {
		String id = cursor.getString(0);
		String date = cursor.getString(1);
		String time = cursor.getString(2);
		String recorder = cursor.getString(3);
		List<String> list = (List<String>) Converter.deserialize(cursor
				.getBlob(4));
		boolean tag = cursor.getString(5).equals(INTRUSION);

		return new Session(id, date, time, recorder, list, tag);
	}

	public List<Session> getAllSessions() {
		List<Session> result = new ArrayList<Session>();

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + TABLE_SESSIONS, null);

		if (cursor.moveToFirst()) {
			if (cursor.getCount() <= 0) {
				db.close();
			}
			do {
				result.add(assembleSession(cursor));

			} while (cursor.moveToNext());
		}
		db.close();
		
		return result;
	}
}
