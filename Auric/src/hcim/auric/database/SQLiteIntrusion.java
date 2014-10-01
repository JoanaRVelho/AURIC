package hcim.auric.database;

import hcim.auric.intrusion.Intrusion;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteIntrusion extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "IntrusionDB";

	private static final String TABLE_INTRUSIONS = "intrusions";
	private static final String KEY_ID = "id";
	private static final String KEY_DATE = "date";
	private static final String KEY_TIME = "time";
	private static final String KEY_LOG = "log";
	private static final String[] COLUMNS = { KEY_ID, KEY_DATE, KEY_TIME,
			KEY_LOG };

	public SQLiteIntrusion(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_INTRUSION_TABLE = "CREATE TABLE " + TABLE_INTRUSIONS
				+ " ( " + KEY_ID + " TEXT PRIMARY KEY, " + KEY_DATE + " TEXT, "
				+ KEY_TIME + " TEXT, " + KEY_LOG + " TEXT )";

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
		values.put(KEY_LOG, i.getLog().getID());

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

		Intrusion intrusion = new Intrusion(cursor.getString(0),
				cursor.getString(1), cursor.getString(2), cursor.getString(3));

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

		SQLiteDatabase db = this.getWritableDatabase();

		Cursor cursor = db.query(true, TABLE_INTRUSIONS, COLUMNS, KEY_DATE
				+ "='" + date + "'", null, null, null, null, null);

		Intrusion i = null;

		if (cursor == null)
			return null;

		if (cursor.moveToFirst()) {
			if (cursor.getCount() <= 0)
				return null;
			do {
				i = new Intrusion(cursor.getString(0), cursor.getString(1),
						cursor.getString(2), cursor.getString(3));

				result.add(i);
			} while (cursor.moveToNext());
		}

		return result;
	}

	public void deleteIntrusion(String id) {
		SQLiteDatabase db = this.getWritableDatabase();

		db.delete(TABLE_INTRUSIONS, KEY_ID + "= '" + id + "'", null);

		db.close();
	}

}