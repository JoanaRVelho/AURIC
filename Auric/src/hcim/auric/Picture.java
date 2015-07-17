package hcim.auric;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

public class Picture {

	private String id;
	private Bitmap img;
	private String type;
	private String description;

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

	public String getDescription() {
		if(description == null){
			return "";
		}
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public static List<Bitmap> getBitmapList(List<Picture> list) {
		List<Bitmap> result = new ArrayList<Bitmap>();

		for (Picture p : list) {
			result.add(p.getImage());
		}
		return result;
	}

	@Override
	public String toString() {
		return "Picture [id=" + id + ", type=" + type + "]";
	}
}
