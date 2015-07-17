package hcim.auric.data;

import hcim.auric.record.events.EventBasedLog;
import hcim.auric.record.events.EventBasedLogItem;
import hcim.auric.utils.Converter;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class EventLogTable {
	static final String TABLE = "Event_Log";
	
	private static final String KEY_ID = "log_id";
	private static final String KEY_INT = "intrusion";
	private static final String KEY_APP = "app_name";
	private static final String KEY_TIME = "time";
	private static final String KEY_DETAILS = "details";
	private static final String KEY_PACKAGE = "package";
	private static final String[] COLUMNS = { KEY_ID, KEY_INT, KEY_APP,
			KEY_TIME, KEY_DETAILS, KEY_PACKAGE };

	static final String CREATE = "CREATE TABLE " + TABLE + " ( " + KEY_ID
	+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_INT + " TEXT, "
	+ KEY_APP + " TEXT, " + KEY_TIME + " TEXT, " + KEY_DETAILS
	+ " BLOB, " + KEY_PACKAGE + " TEXT )";

	private SQLiteAdapter adapter;

	public EventLogTable(Context c) {
		this.adapter = SQLiteAdapter.getInstance(c);
	}

	public void insert(String intrusionID,
			EventBasedLogItem t) {
		SQLiteDatabase db = adapter.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_INT, intrusionID);
		values.put(KEY_APP, t.getAppName());
		values.put(KEY_TIME, t.getTime());
		values.put(KEY_DETAILS, Converter.serialize(t.getDetails()));
		values.put(KEY_PACKAGE, t.getPackageName());

		db.insert(TABLE, null, values);

		 ;
	}

	@SuppressWarnings("unchecked")
	public EventBasedLog get(String intrusionID, Context c) {
		SQLiteDatabase db = adapter.getReadableDatabase();

		Cursor cursor = db.query(true, TABLE, COLUMNS, KEY_INT + "='"
				+ intrusionID + "'", null, null, null, null, null);

		EventBasedLog result = new EventBasedLog(intrusionID);

		EventBasedLogItem t;
		String appName, time, packageName;
		ArrayList<String> details;

		if (cursor.moveToFirst()) {
			if (cursor.getCount() <= 0) {
				return null;
			}
			do {
				appName = cursor.getString(2);
				time = cursor.getString(3);
				details = (ArrayList<String>) Converter.deserialize(cursor
						.getBlob(4));
				packageName = cursor.getString(5);

				t = new EventBasedLogItem(c, cursor.getInt(0), appName, time,
						packageName, details);
				result.addItem(t);

			} while (cursor.moveToNext());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public EventBasedLogItem get(int id, Context c) {
		SQLiteDatabase db = adapter.getReadableDatabase();

		Cursor cursor = db.query(true, TABLE, COLUMNS, KEY_ID + "=" + id, null,
				null, null, null, null);

		EventBasedLogItem t;
		String appName, time, packageName;
		ArrayList<String> details;

		if (cursor != null)
			cursor.moveToFirst();

		if (cursor.getCount() <= 0) {
			return null;
		}

		appName = cursor.getString(2);
		time = cursor.getString(3);
		details = (ArrayList<String>) Converter.deserialize(cursor.getBlob(4));
		packageName = cursor.getString(5);

		t = new EventBasedLogItem(c, id, appName, time, packageName, details);

		return t;
	}

	public void delete(String intrusionID) {
		SQLiteDatabase db = adapter.getWritableDatabase();

		db.delete(TABLE, KEY_INT + "= '" + intrusionID + "'", null);
	}
}
