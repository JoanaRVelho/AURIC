package hcim.auric.database;

import hcim.auric.intrusion.Intrusion;
import hcim.auric.intrusion.Log;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.hcim.intrusiondetection.R;

public class Database {
	private static int width, height;
	
	public static int getWidth() {
		return width;
	}

	public static void setWidth(int w) {
		width = w;
	}

	public static int getHeight() {
		return height;
	}

	public static void setHeight(int h) {
		height = h;
	}

	public static void init(Context context) {
		SQLitePicture picDB = new SQLitePicture(context);
		SQLiteIntrusion intDB = new SQLiteIntrusion(context);
		SQLiteIntrusionPictures intPicDB = new SQLiteIntrusionPictures(context);
		SQLiteLog logDB = new SQLiteLog(context);

		ConfigurationDatabase.init(picDB);
		IntrusionsDatabase.init(intDB, intPicDB, logDB);
	//	addFakeData(context);
	}

	static void addFakeData(Context context) {
		List<Bitmap> images = new ArrayList<Bitmap>();
		Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.ic_launcher);
		Bitmap icon3 = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.gear);
		images.add(icon);
		images.add(icon3);

		Intrusion i = new Intrusion("10-June-2014 10:00:00", "10-June-2014",
				"10:00:00");
		Intrusion i2 = new Intrusion("10-June-2014 21:44:54", "10-June-2014",
				"21:44:54");
		Intrusion i3 = new Intrusion("13-May-2014 23:15:55", "13-May-2014",
				"23:15:55");
		
		i.setImages(images);
		i2.setImages(images);
		i3.setImages(images);
		
		Log l = new Log("timestamp");
		
		i.setLog(l);
		i2.setLog(l);
		i3.setLog(l);

		IntrusionsDatabase.addIntrusion(i);
		IntrusionsDatabase.addIntrusion(i2);
		IntrusionsDatabase.addIntrusion(i3);

	}
	
}
