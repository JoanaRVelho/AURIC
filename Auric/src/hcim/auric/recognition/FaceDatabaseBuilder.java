package hcim.auric.recognition;

import hcim.auric.utils.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.hcim.intrusiondetection.R;

public class FaceDatabaseBuilder {
	private static int[][] RESOURCES;
	private FaceRecognition faceRecognition;
	private Context c;

	public FaceDatabaseBuilder(Context c) {
		this.c = c;
		this.faceRecognition = FaceRecognition.getInstance(c);
	}

	private byte[] readPGM(int resourceID) {
		InputStream inFace = c.getResources().openRawResource(resourceID);

		byte[] result = new byte[10318];
		try {
			inFace.read(result);
			inFace.close();
		} catch (IOException e) {
			LogUtils.exception(e);
		}

		return result;
	}

	private static Bitmap getBitmapFromPgm(byte[] decodedString, int width,
			int height/*, int dataOffset*/) {
		// Create pixel array, and expand 8 bit gray to ARGB_8888
		int[] pixels = new int[width * height];
		int i = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int gray = decodedString[/*dataOffset + */i] & 0xff;
				pixels[i] = 0xff000000 | gray << 16 | gray << 8 | gray;
	
				i++;
			}
		}
		Bitmap pgm = Bitmap.createBitmap(pixels, width, height,
				android.graphics.Bitmap.Config.ARGB_8888);
		return pgm;
	}

	private void loadSubject(int resourceID, String name) {
		byte[] pgm = readPGM(resourceID);
		Bitmap gray = getBitmapFromPgm(pgm, 92, 112);
		faceRecognition.trainGrayPicture(gray, name);
		gray.recycle();
	}

	private void build() {
		String s = "s";

		for (int i = 0; i < RESOURCES.length; i++) {
			for (int j = 0; j < RESOURCES.length; j++) {
				loadSubject(RESOURCES[i][j], s + i);
			}
		}
	}

}
