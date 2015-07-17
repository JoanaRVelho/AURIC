package hcim.auric.data;

import hcim.auric.Picture;
import hcim.auric.recognition.FaceRecognition;
import hcim.auric.utils.LogUtils;

import java.util.List;

import android.content.Context;

public class PicturesDatabase {
	private static PicturesDatabase INSTANCE;
	private PictureTable pictures;

	public static PicturesDatabase getInstance(Context c) {
		if (INSTANCE == null) {
			INSTANCE = new PicturesDatabase(c);
		}

		return INSTANCE;
	}

	private PicturesDatabase(Context c) {
		pictures = new PictureTable(c);
	}

	public void addPicture(Picture p) {
		pictures.insertPicture(p);
	}

	public List<Picture> getAllPictures() {
		return pictures.getAllPictures();
	}

	public List<Picture> getMyPictures() {
		return pictures.getMyPictures();
	}

	public List<Picture> getIntrudersPictures() {
		return pictures.getIdentifiedIntrudersPictures();
	}

	public boolean isMyPicture(String id) {
		String type = pictures.getPictureType(id);

		return type != null && type.equals(FaceRecognition.getMyPictureType());
	}

	public void removePicture(Picture p) {
		pictures.removePicture(p);
	}

	public Picture getPicture(String id) {
		return pictures.getPicture(id);
	}

	public void printList() {
		List<Picture> list = pictures.getAllPictures();
		StringBuilder s = new StringBuilder();
		for (Picture p : list) {
			s.append(p.toString() + "\n");
		}

		LogUtils.debug(s.toString());
	}

	public boolean hasPicture(String id) {
		return getPicture(id) != null;
	}

	public void setPictureType(Picture p) {
		pictures.setPictureType(p);
	}
}
