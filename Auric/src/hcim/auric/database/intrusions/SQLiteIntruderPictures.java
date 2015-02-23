package hcim.auric.database.intrusions;

import hcim.auric.recognition.Picture;
import hcim.auric.utils.Converter;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

public class SQLiteIntruderPictures extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "IntrusionPicturesDB";

	private static final String TABLE_INTRUSIONS = "intrusion_pictures";
	private static final String KEY_ID = "id";
	private static final String KEY_INT = "intrusion";
	private static final String KEY_PICTURE = "picture";
	private static final String KEY_TYPE = "type";
	private static final String KEY_DESCRIPTION = "description";
	private static final String[] COLUMNS = { KEY_ID, KEY_INT, KEY_PICTURE,
			KEY_TYPE, KEY_DESCRIPTION };
	
	
	private static final String DUMMY = "DUMMY";

	public SQLiteIntruderPictures(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_INTRUSION_TABLE = "CREATE TABLE " + TABLE_INTRUSIONS
				+ " ( " + KEY_ID + " TEXT PRIMARY KEY, " + KEY_INT + " TEXT, "
				+ KEY_PICTURE + " BLOB, " + KEY_TYPE + " TEXT, " + KEY_DESCRIPTION + " TEXT )";

		db.execSQL(CREATE_INTRUSION_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_INTRUSIONS);

		this.onCreate(db);
	}

	public void insertPicture(String intrusionID, Picture p) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, p.getID());
		values.put(KEY_INT, intrusionID);
		values.put(KEY_TYPE, p.getType());
		values.put(KEY_PICTURE, Converter.bitmapToByteArray(p.getImage()));
		values.put(KEY_DESCRIPTION, p.getDescription());

		db.insert(TABLE_INTRUSIONS, null, values);

		db.close();
	}

	public void updatePictureType(Picture p) {
		SQLiteDatabase db = this.getWritableDatabase();
	
		ContentValues values = new ContentValues();
		values.put(KEY_TYPE, p.getType());
		db.update(TABLE_INTRUSIONS, values, KEY_ID + "='" + p.getID() + "'",
				null);
	
		db.close();
	}

	public Picture getPicture(String pictureID) {
		SQLiteDatabase db = this.getReadableDatabase();
	
		Cursor cursor = db.query(TABLE_INTRUSIONS, COLUMNS, KEY_ID + "='"
				+ pictureID + "'", null, null, null, null, null);
	
		if (cursor != null)
			cursor.moveToFirst();
	
		if (cursor.getCount() <= 0)
			return null;
	
		byte[] blob = cursor.getBlob(2);
		String type = cursor.getString(3);
		String desc = cursor.getString(4);
	
		Picture picture = new Picture(pictureID, type,
				(Bitmap) Converter.byteArrayToBitmap(blob));
		picture.setDescription(desc);
	
		db.close();
		return picture;
	}

	/**
	 * Gets all intrusions from a given day
	 * 
	 * @param date
	 *            : the day
	 * @return list of intrusions
	 */
	public List<Picture> getAllPictures(String intrusionID) {
		List<Picture> result = new ArrayList<Picture>();
		Bitmap b;
		String type, id, desc;
		Picture p;

		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(true, TABLE_INTRUSIONS, COLUMNS, KEY_INT
				+ " = '" + intrusionID + "'", null, null, null, null, null);

		if (cursor == null)
			return null;

		if (cursor.moveToFirst()) {
			if (cursor.getCount() <= 0)
				return null;
			do {
				id = cursor.getString(0);
				b = (Bitmap) Converter.byteArrayToBitmap(cursor.getBlob(2));
				type = cursor.getString(3);
				desc = cursor.getString(4);
				p = new Picture(id, type, b);
				p.setDescription(desc);

				result.add(p);
			} while (cursor.moveToNext());
		}

		db.close();

		return result;
	}

	public void deleteAllPictures(String intrusionID) {
		SQLiteDatabase db = this.getWritableDatabase();

		db.delete(TABLE_INTRUSIONS, KEY_INT + "= '" + intrusionID + "'", null);

		db.close();
	}

	public void insertPictureUnknownIntrusion(Picture p) {
		insertPicture(DUMMY, p);		
	}

	public void updatePicturesUnknownIntrusion(String intrusionID) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_INT, intrusionID);
		db.update(TABLE_INTRUSIONS, values, KEY_INT + "='" + DUMMY + "'",
				null);

		db.close();
	}

	public void deletePicturesUnknownIntrusion() {
		deleteAllPictures(DUMMY);
		
	}
}