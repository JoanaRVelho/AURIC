package hcim.auric.database.configs;

import hcim.auric.recognition.FaceRecognition;
import hcim.auric.recognition.Picture;

import java.util.List;

import android.content.Context;
import android.util.Log;

public class PicturesDatabase {
	public static final String TAG = "AURIC";

	private static PicturesDatabase INSTANCE;
	private SQLitePicture pictureDB;

	public static PicturesDatabase getInstance(Context c) {
		if (INSTANCE == null) {
			INSTANCE = new PicturesDatabase(c);
		}

		return INSTANCE;
	}

	private PicturesDatabase(Context c) {
		pictureDB = new SQLitePicture(c);
	}

	public void addPicture(Picture p) {
		if (pictureDB != null) {
			pictureDB.insertPicture(p);
			Log.d(TAG,
					"addPicture: Picture ID=" + p.getID() + ", type="
							+ p.getType());
		}
		printList();
	}

	public List<Picture> getAllPictures() {
		if (pictureDB != null) {
			return pictureDB.getAllPictures();
		}
		return null;
	}

	public List<Picture> getMyPictures() {
		if (pictureDB != null) {
			return pictureDB.getMyPictures();
		}
		return null;
	}

	public List<Picture> getIntrudersPictures() {
		if (pictureDB != null) {
			return pictureDB.getIdentifiedIntrudersPictures();
		}
		return null;
	}

	public boolean isMyPicture(String id) {
		if (pictureDB != null) {
			String type = pictureDB.getPictureType(id);

			return type != null && type.equals(FaceRecognition.MY_PICTURE_TYPE);
		}
		return false;
	}

	public void removePicture(Picture p) {
		if (pictureDB != null) {
			pictureDB.removePicture(p);
			Log.d(TAG,
					"removePicture: Picture ID=" + p.getID() + ", type="
							+ p.getType());
		}
	}

	public Picture getPicture(String id) {
		if (pictureDB != null) {
			return pictureDB.getPicture(id);
		}
		return null;
	}
	
	public void printList(){
		if (pictureDB != null) {
			List<Picture> list = pictureDB.getAllPictures();
			StringBuilder s = new StringBuilder();
			for(Picture p :list){
				s.append(p.toString()+"\n");
			}
			Log.i("AURIC",s.toString());
		}
	}

	public boolean hasPicture(String id) {		
		return getPicture(id) != null;
	}

	public void setPictureType(Picture p) {
		if(pictureDB != null){
			pictureDB.setPictureType(p);
		}
		
	}
}
