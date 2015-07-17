package hcim.auric.data;

import hcim.auric.Picture;
import hcim.auric.recognition.FaceRecognition;
import hcim.auric.utils.Converter;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

public class PictureTable {
	static final String TABLE = "pictures";
	private static final String KEY_ID = "id";
	private static final String KEY_TYPE = "type";
	private static final String KEY_PICTURE = "picture";
	private static final String[] COLUMNS = { KEY_ID, KEY_TYPE, KEY_PICTURE };

	public static final String CREATE = "CREATE TABLE " + TABLE + " ( "
			+ KEY_ID + " TEXT, " + KEY_TYPE + " TEXT, " + KEY_PICTURE
			+ " BLOB )";
	
	private SQLiteAdapter adapter;

	public PictureTable(Context c) {
		adapter = SQLiteAdapter.getInstance(c);
	}

	public int numberOfPictures() {
		SQLiteDatabase db = adapter.getReadableDatabase();

		Cursor cursor = db.rawQuery("SELECT  * FROM " + TABLE, null);
		int result = cursor.getCount();

		cursor.close();
		db.close();

		return result;

	}

	public Picture getPicture(String pictureID) {
		SQLiteDatabase db = adapter.getReadableDatabase();

		Cursor cursor = db.query(TABLE, COLUMNS, KEY_ID + "='" + pictureID
				+ "'", null, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		if (cursor.getCount() <= 0)
			return null;

		String id = cursor.getString(0);
		String type = cursor.getString(1);
		byte[] blob = cursor.getBlob(2);

		Picture picture = new Picture(id, type,
				(Bitmap) Converter.byteArrayToBitmap(blob));

		// db.close();
		return picture;
	}

	public String getPictureType(String id) {
		SQLiteDatabase db = adapter.getReadableDatabase();
		String query = "SELECT * FROM " + TABLE + " WHERE " + KEY_ID + " = '"
				+ id + "'";

		Cursor cursor = db.rawQuery(query, null);

		if (cursor != null)
			cursor.moveToFirst();

		if (cursor.getCount() <= 0) {
			// db.close();
			return null;
		}
		String result = cursor.getString(1);
		// db.close();

		return result;
	}

	public void insertPicture(Picture pic) {
		SQLiteDatabase db = adapter.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, pic.getID());
		values.put(KEY_TYPE, pic.getType());
		values.put(KEY_PICTURE, Converter.bitmapToByteArray(pic.getImage()));

		db.insert(TABLE, null, values);

		// db.close();
	}

	public int updatePicture(Picture pic) {
		SQLiteDatabase db = adapter.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_TYPE, pic.getType());
		values.put(KEY_PICTURE, Converter.bitmapToByteArray(pic.getImage()));

		int i = db.update(TABLE, values, KEY_ID + " = '" + pic.getID() + "'",
				null);

		// db.close();

		return i;
	}

	public void removePicture(Picture pic) {
		SQLiteDatabase db = adapter.getWritableDatabase();

		db.delete(TABLE, KEY_ID + "= '" + pic.getID() + "'", null);

		// db.close();
	}

	/**
	 * Get all pictures except the source pictures
	 * 
	 * @return
	 */
	public List<Picture> getAllPictures() {
		List<Picture> result = new ArrayList<Picture>();

		String query = "SELECT  * FROM " + TABLE;

		SQLiteDatabase db = adapter.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		Picture pic = null;

		if (cursor.moveToFirst()) {
			if (cursor.getCount() <= 0) {
				// db.close();
				return null;
			}
			do {
				pic = new Picture(cursor.getString(0), cursor.getString(1),
						(Bitmap) Converter.byteArrayToBitmap(cursor.getBlob(2)));
				result.add(pic);

			} while (cursor.moveToNext());
		}
		// db.close();

		return result;
	}

	public List<Picture> getMyPictures() {
		List<Picture> result = new ArrayList<Picture>();

		String query = "SELECT  * FROM " + TABLE + " WHERE " + KEY_TYPE + " ='"
				+ FaceRecognition.getMyPictureType() + "'";

		SQLiteDatabase db = adapter.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		Picture pic = null;

		if (cursor.moveToFirst()) {
			if (cursor.getCount() <= 0) {
				// db.close();
				return null;
			}
			do {
				pic = new Picture(cursor.getString(0), cursor.getString(1),
						(Bitmap) Converter.byteArrayToBitmap(cursor.getBlob(2)));
				result.add(pic);

			} while (cursor.moveToNext());
		}
		// db.close();

		return result;
	}

	public List<Picture> getIdentifiedIntrudersPictures() {
		List<Picture> result = new ArrayList<Picture>();

		String query = "SELECT  * FROM " + TABLE + " WHERE " + KEY_TYPE + " ='"
				+ FaceRecognition.getIntruderPictureType() + "'";

		SQLiteDatabase db = adapter.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);

		Picture pic = null;

		if (cursor.moveToFirst()) {
			if (cursor.getCount() <= 0)
				return null;
			do {
				pic = new Picture(cursor.getString(0), cursor.getString(1),
						(Bitmap) Converter.byteArrayToBitmap(cursor.getBlob(2)));
				result.add(pic);

			} while (cursor.moveToNext());
		}
		return result;
	}

	public void setPictureType(Picture p) {
		SQLiteDatabase db = adapter.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_TYPE, p.getType());
		db.update(TABLE, values, KEY_ID + "='" + p.getID() + "'", null);

	}
}
