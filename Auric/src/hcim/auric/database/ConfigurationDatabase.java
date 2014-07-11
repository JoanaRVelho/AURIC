package hcim.auric.database;

import hcim.auric.recognition.Picture;

import java.util.List;

public class ConfigurationDatabase {
	public static final String MY_PICTURE_ID = "myface";
	public static final String NEGATIVE_PICTURE_ID = "negative";

	static Picture myPicture;
	static Picture negativePicture;
	static List<Picture> allPictures;
	static SQLitePicture pictureDB;

	public static void init(SQLitePicture db) {
		pictureDB = db;
		myPicture = getMyPicture();
		negativePicture = getNegativePicture();
		allPictures = getAllPictures();
	}

	public static Picture getMyPicture() {
		if (myPicture == null)
			if( pictureDB != null)
				myPicture = pictureDB.getPicture(MY_PICTURE_ID);

		return myPicture;
	}

	public static void setMyPicture(Picture imageBitmap) {
		if (myPicture == null)
			pictureDB.addPicture(imageBitmap);
		else
			pictureDB.updatePicture(imageBitmap);

		myPicture = imageBitmap;
	}

	public static List<Picture> getAllPictures() {
		if (allPictures == null)
			if( pictureDB != null)
				allPictures = pictureDB.getAllPictures();

		return allPictures;
	}

	public static Picture getNegativePicture() {
		if (negativePicture == null)
			if( pictureDB != null)
				negativePicture = pictureDB.getPicture(NEGATIVE_PICTURE_ID);

		return negativePicture;
	}

	public static void setNegativePicture(Picture imageBitmap) {
		if (negativePicture == null)
			pictureDB.addPicture(imageBitmap);
		else
			pictureDB.updatePicture(imageBitmap);

		negativePicture = imageBitmap;
	}
}
