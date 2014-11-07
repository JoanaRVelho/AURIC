package hcim.auric.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class Converter {

	public static byte[] serialize(Object obj) {
		try {
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			ObjectOutputStream o = new ObjectOutputStream(b);
			o.writeObject(obj);
			return b.toByteArray();
		} catch (IOException e) {
			return null;
		}
	}

	public static Object deserialize(byte[] bytes) {
		try {
			ByteArrayInputStream b = new ByteArrayInputStream(bytes);
			ObjectInputStream o = new ObjectInputStream(b);
			return o.readObject();
		} catch (IOException e) {
			return null;
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public static Bitmap byteArrayToBitmap(byte[] array) {
		Bitmap bitmap = BitmapFactory.decodeByteArray(array, 0, array.length);
		return bitmap;
	}

	public static byte[] bitmapToByteArray(Bitmap bitmap) {
		ByteArrayOutputStream blob = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.PNG, 0, blob);
		byte[] result = blob.toByteArray();

		return result;
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable == null)
			return null;
		
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		}

		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		return bitmap;
	}

	public static String listCharSequenceToString(List<CharSequence> text) {
		StringBuilder sb = new StringBuilder();
		for (CharSequence s : text) {
			sb.append(s);
		}
		return sb.toString();
	}
}
