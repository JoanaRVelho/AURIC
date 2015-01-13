package hcim.auric.recognition;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

public class Picture {

	private String id;
	private Bitmap img;
	private String type;

	public Picture(String id, String type, Bitmap img) {
		this.type = type;
		this.img = img;
		this.id = id;
	}

	public String getID() {
		return id;
	}

	public Bitmap getImage() {
		return img;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public static Bitmap[] getImages(Picture[] array) {
		Bitmap[] result = new Bitmap[array.length];

		for (int i = 0; i < result.length; i++) {
			result[i] = array[i].getImage();
		}
		return result;
	}

	public static String[] getNamesArray(Picture[] array) {
		String[] result = new String[array.length];

		for (int i = 0; i < result.length; i++) {
			result[i] = array[i].getID();
		}
		return result;
	}

	public static List<String> getNamesList(Picture[] array) {
		List<String> result = new ArrayList<String>();

		for (int i = 0; i < array.length; i++) {
			result.add(array[i].getID());
		}
		return result;
	}

	public static List<Bitmap> getBitmapList(List<Picture> list) {
		List<Bitmap> result = new ArrayList<Bitmap>();

		for (Picture p : list) {
			result.add(p.getImage());
		}
		return result;
	}

	public static List<Picture> getPictureList(List<Bitmap> images) {
		List<Picture> result = new ArrayList<Picture>();

		for (Bitmap b : images) {
			result.add(new Picture(null, null, b));
		}
		return result;
	}

	public void setID(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Picture [id=" + id + ", type=" + type + "]";
	}

	public void setBitmap(Bitmap bitmap) {
		this.img = bitmap;
	}

}
