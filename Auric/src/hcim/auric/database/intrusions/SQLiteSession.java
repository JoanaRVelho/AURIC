package hcim.auric.database.intrusions;

import hcim.auric.intrusion.Session;
import hcim.auric.utils.Converter;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteSession extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "SessionIntrsionDB";

	private static final String TABLE_SESSIONS = "sessions_intrusions";
	private static final String KEY_ID = "id";
	private static final String KEY_DATE = "date";
	private static final String KEY_TIME = "time";
	private static final String KEY_INT = "intrusions";
	private static final String[] COLUMNS = { KEY_ID, KEY_DATE, KEY_TIME,
			KEY_INT };

	public SQLiteSession(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_SESSION_TABLE = "CREATE TABLE " + TABLE_SESSIONS
				+ " ( " + KEY_ID + " TEXT PRIMARY KEY, " + KEY_DATE + " TEXT, "
				+ KEY_TIME + " TEXT," + KEY_INT + " BLOB )";

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
		values.put(KEY_INT, Converter.serialize(s.getInteractionsIDs()));

		db.insert(TABLE_SESSIONS, null, values);

		db.close();
	}

	@SuppressWarnings("unchecked")
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

		Session result = new Session(id, cursor.getString(1),
				cursor.getString(2),
				(List<String>) Converter.deserialize(cursor.getBlob(3)));

		db.close();

		return result;
	}

	@SuppressWarnings("unchecked")
	public List<Session> getAllSessionsFromDay(String date) {
		List<Session> result = new ArrayList<Session>();

		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(true, TABLE_SESSIONS, COLUMNS, KEY_DATE + "='"
				+ date + "'", null, null, null, null, null);

		Session s = null;

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
				s = new Session(cursor.getString(0), cursor.getString(1),
						cursor.getString(2),
						(List<String>) Converter.deserialize(cursor.getBlob(3)));

				result.add(s);
			} while (cursor.moveToNext());
		}
		db.close();
		return result;
	}

	public void deleteIntrusion(String id) {
		SQLiteDatabase db = this.getWritableDatabase();

		db.delete(TABLE_SESSIONS, KEY_ID + "= '" + id + "'", null);

		db.close();
	}

	@SuppressWarnings("unchecked")
	public void printAll() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + TABLE_SESSIONS, null);
		Session s;
		Log.d("AURIC", "print all sessions");

		if (cursor.moveToFirst()) {
			if (cursor.getCount() <= 0) {
				db.close();
			}
			do {
				s = new Session(cursor.getString(0), cursor.getString(1),
						cursor.getString(2),
						(List<String>) Converter.deserialize(cursor.getBlob(3)));
				Log.d("AURIC", s.toString());

			} while (cursor.moveToNext());
		}
		db.close();
	}
}
