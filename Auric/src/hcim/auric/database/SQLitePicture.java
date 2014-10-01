package hcim.auric.database;

import hcim.auric.recognition.FaceRecognition;
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

public class SQLitePicture extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "PictureDB";

	private static final String TABLE_PICTURES = "pictures";
	private static final String KEY_ID = "id";
	private static final String KEY_TYPE = "type";
	private static final String KEY_PICTURE = "picture";
	private static final String[] COLUMNS = { KEY_ID, KEY_TYPE, KEY_PICTURE };

	public SQLitePicture(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_PICTURES_TABLE = "CREATE TABLE pictures ( " + KEY_ID
				+ " TEXT, " + KEY_TYPE + " TEXT, " + KEY_PICTURE + " BLOB )";

		db.execSQL(CREATE_PICTURES_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PICTURES);

		this.onCreate(db);
	}

	public int numberOfPictures() {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.rawQuery("SELECT  * FROM " + TABLE_PICTURES, null);
		int result = cursor.getCount();

		cursor.close();
		db.close();

		return result;

	}

	public Picture getPicture(String pictureID) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_PICTURES, COLUMNS, KEY_ID + "='"
				+ pictureID + "'", null, null, null, null, null);

		if (cursor != null)
			cursor.moveToFirst();

		if (cursor.getCount() <= 0)
			return null;

		String id = cursor.getString(0);
		String type = cursor.getString(1);
		byte[] blob = cursor.getBlob(2);

		Picture picture = new Picture(id, type,
				(Bitmap) Converter.byteArrayToBitmap(blob));

		db.close();
		return picture;
	}

	public String getPictureType(String id) {
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "SELECT * FROM " + TABLE_PICTURES
				+ " WHERE " + KEY_ID + " = '" + id + "'";

		Cursor cursor = db.rawQuery(query, null);

		if (cursor != null)
			cursor.moveToFirst();

		if (cursor.getCount() <= 0)
			return null;

		String result = cursor.getString(1);
		db.close();
		
		return result;
	}

	public void insertPicture(Picture pic) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_ID, pic.getID());
		values.put(KEY_TYPE, pic.getType());
		values.put(KEY_PICTURE, Converter.bitmapToByteArray(pic.getImage()));

		db.insert(TABLE_PICTURES, null, values);

		db.close();
	}

	public int updatePicture(Picture pic) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_TYPE, pic.getType());
		values.put(KEY_PICTURE, Converter.bitmapToByteArray(pic.getImage()));

		int i = db.update(TABLE_PICTURES, values, KEY_ID + " = '" + pic.getID()
				+ "'", null);

		db.close();

		return i;
	}

	public void removePicture(Picture pic) {
		SQLiteDatabase db = this.getWritableDatabase();

		db.delete(TABLE_PICTURES, KEY_ID + "= '" + pic.getID() + "'", null);

		db.close();
	}

	/**
	 * Get all pictures except the source pictures
	 * 
	 * @return
	 */
	public List<Picture> getAllPictures() {
		List<Picture> result = new ArrayList<Picture>();

		String query = "SELECT  * FROM " + TABLE_PICTURES;

		SQLiteDatabase db = this.getReadableDatabase();
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

	public List<Picture> getMyPictures() {
		List<Picture> result = new ArrayList<Picture>();

		String query = "SELECT  * FROM " + TABLE_PICTURES + " WHERE "
				+ KEY_TYPE + " ='" + FaceRecognition.MY_PICTURE_TYPE + "'";

		SQLiteDatabase db = this.getReadableDatabase();
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

	public List<Picture> getIdentifiedIntrudersPictures() {
		List<Picture> result = new ArrayList<Picture>();

		String query = "SELECT  * FROM " + TABLE_PICTURES + " WHERE "
				+ KEY_TYPE + " ='" + FaceRecognition.INTRUDER_PICTURE_TYPE
				+ "'";

		SQLiteDatabase db = this.getReadableDatabase();
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
}