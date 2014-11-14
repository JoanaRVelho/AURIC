package hcim.auric.database;

import hcim.auric.record.screen.event_based.EventBasedLog;
import hcim.auric.record.screen.event_based.EventBasedLogItem;
import hcim.auric.utils.Converter;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteEventBasedLog extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "AuricTextLogDB";

	private static final String TABLE_LOG = "aurictextlog";
	private static final String KEY_ID = "log_id";
	private static final String KEY_INT = "intrusion";
	private static final String KEY_APP = "app_name";
	private static final String KEY_TIME = "time";
	private static final String KEY_DETAILS = "details";
	private static final String KEY_PACKAGE = "package";
	private static final String[] COLUMNS = { KEY_ID, KEY_INT, KEY_APP,
			KEY_TIME, KEY_DETAILS, KEY_PACKAGE };

	public SQLiteEventBasedLog(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_INTRUSION_TABLE = "CREATE TABLE " + TABLE_LOG + " ( "
				+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_INT
				+ " TEXT, " + KEY_APP + " TEXT, " + KEY_TIME + " TEXT, "
				+ KEY_DETAILS + " BLOB, " + KEY_PACKAGE + " TEXT )";

		db.execSQL(CREATE_INTRUSION_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOG);

		this.onCreate(db);
	}

	public void insert(String intrusionID, EventBasedLogItem t) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_INT, intrusionID);
		values.put(KEY_APP, t.getAppName());
		values.put(KEY_TIME, t.getTime());
		values.put(KEY_DETAILS, Converter.serialize(t.getDetails()));
		values.put(KEY_PACKAGE, t.getPackageName());

		db.insert(TABLE_LOG, null, values);

		db.close();
	}

	@SuppressWarnings("unchecked")
	public EventBasedLog get(String intrusionID, Context c) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(true, TABLE_LOG, COLUMNS, KEY_INT + "='"
				+ intrusionID + "'", null, null, null, null, null);

		EventBasedLog result = new EventBasedLog(intrusionID);

		EventBasedLogItem t;
		String appName, time, packageName;
		ArrayList<String> details;

		if (cursor.moveToFirst()) {
			if (cursor.getCount() <= 0) {
				db.close();
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
		db.close();

		return result;
	}

	@SuppressWarnings("unchecked")
	public EventBasedLogItem get(int id, Context c) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(true, TABLE_LOG, COLUMNS, KEY_ID + "=" + id,
				null, null, null, null, null);

		EventBasedLogItem t;
		String appName, time, packageName;
		ArrayList<String> details;

		if (cursor != null)
			cursor.moveToFirst();

		if (cursor.getCount() <= 0) {
			db.close();
			return null;
		}

		appName = cursor.getString(2);
		time = cursor.getString(3);
		details = (ArrayList<String>) Converter.deserialize(cursor.getBlob(4));
		packageName = cursor.getString(5);

		t = new EventBasedLogItem(c, id, appName, time, packageName, details);

		db.close();

		return t;
	}

	public void delete(String intrusionID) {
		SQLiteDatabase db = this.getWritableDatabase();

		db.delete(TABLE_LOG, KEY_INT + "= '" + intrusionID + "'", null);

		db.close();
	}
}