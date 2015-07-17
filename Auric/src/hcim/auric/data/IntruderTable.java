package hcim.auric.data;

import hcim.auric.Picture;
import hcim.auric.utils.Converter;
import hcim.auric.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

public class IntruderTable {
	static final String TABLE = "intruder_pictures";
	static final String KEY_ID = "id";
	static final String KEY_INT = "intrusion";
	static final String KEY_PICTURE = "picture";
	static final String KEY_TYPE = "type";
	static final String KEY_DESCRIPTION = "description";
	static final String[] COLUMNS = { KEY_ID, KEY_INT, KEY_PICTURE, KEY_TYPE,
			KEY_DESCRIPTION };

	private static final String UNKNOWN = "DUMMY";

	static final String CREATE = "CREATE TABLE " + TABLE + " ( " + KEY_ID
			+ " TEXT PRIMARY KEY, " + KEY_INT + " TEXT, " + KEY_PICTURE
			+ " BLOB, " + KEY_TYPE + " TEXT, " + KEY_DESCRIPTION + " TEXT )";

	private SQLiteAdapter adapter;

	public IntruderTable(Context c) {
		this.adapter = SQLiteAdapter.getInstance(c);
	}

	public void insertPicture(String intrusionID, Picture p) {
		SQLiteDatabase db = adapter.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, p.getID());
		values.put(KEY_INT, intrusionID);
		values.put(KEY_TYPE, p.getType());
		values.put(KEY_PICTURE, Converter.bitmapToByteArray(p.getImage()));
		values.put(KEY_DESCRIPTION, p.getDescription());

		db.insert(TABLE, null, values);
	}

	public void updatePictureType(Picture p) {
		SQLiteDatabase db = adapter.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_TYPE, p.getType());
		db.update(TABLE, values, KEY_ID + "='" + p.getID() + "'", null);
	}

	public Picture getPicture(String pictureID) {
		SQLiteDatabase db = adapter.getReadableDatabase();

		Cursor cursor = db.query(TABLE, COLUMNS, KEY_ID + "='" + pictureID
				+ "'", null, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		if (cursor.getCount() <= 0) {
			return null;
		}
		byte[] blob = cursor.getBlob(2);
		String type = cursor.getString(3);
		String desc = cursor.getString(4);

		Picture picture = new Picture(pictureID, type,
				(Bitmap) Converter.byteArrayToBitmap(blob));
		picture.setDescription(desc);

		return picture;
	}

	/**
	 * Gets all intrusions from a given day
	 * 
	 * @param date
	 *            : the day
	 * @return list of intrusions
	 */
	public List<Picture> getPictures(String intrusionID) {
		List<Picture> result = new ArrayList<Picture>();
		Bitmap b;
		String type, id, desc;
		Picture p;

		SQLiteDatabase db = adapter.getReadableDatabase();

		Cursor cursor = db.query(true, TABLE, COLUMNS, KEY_INT + " = '"
				+ intrusionID + "'", null, null, null, null, null);

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
		return result;
	}

	public void deletePictures(String intrusionID) {
		SQLiteDatabase db = adapter.getWritableDatabase();

		db.delete(TABLE, KEY_INT + "= '" + intrusionID + "'", null);
	}

	public void insertPictureUnknownIntrusion(Picture p) {
		insertPicture(UNKNOWN, p);
	}

	public void updatePicturesUnknownIntrusion(String intrusionID) {
		SQLiteDatabase db = adapter.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_INT, intrusionID);
		db.update(TABLE, values, KEY_INT + "='" + UNKNOWN + "'", null);
	}

	public void deletePicturesUnknownIntrusion() {
		deletePictures(UNKNOWN);
	}

	public void deleteAll() {
		SQLiteDatabase db = adapter.getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE);
	}

	public void deletePicturesFromSession(String sessionID) {
		SQLiteDatabase db = adapter.getWritableDatabase();
		db.execSQL("DELETE FROM " + TABLE + " WHERE " + KEY_INT
				+ " IN ( SELECT " + IntrusionTable.KEY_ID + " FROM "
				+ IntrusionTable.TABLE + " WHERE " + IntrusionTable.KEY_SESSION
				+ "='" + sessionID + "')");
	}

	public void printAll() {
		SQLiteDatabase db = adapter.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from " + TABLE, null);
		LogUtils.debug("Print all intruders:");

		if (cursor.moveToFirst()) {
			if (cursor.getCount() <= 0) {
				return;
			}
			do {
				LogUtils.debug("PICTURE: id=" + cursor.getString(0)
						+ " intrusion=" + cursor.getString(1));

			} while (cursor.moveToNext());
		}
	}
}
