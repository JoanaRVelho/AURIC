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
import android.graphics.Matrix;
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

	public static Bitmap decodeCameraDataToBitmap(byte[] data) {
		BitmapFactory.Options config = new BitmapFactory.Options();
		config.inPreferredConfig = Bitmap.Config.RGB_565;

		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
				config);
		bitmap = rotateBitmap(bitmap);
		return bitmap;
	}

	public static Bitmap decodeCameraDataToSmallBitmap(byte[] data) {
		BitmapFactory.Options config = new BitmapFactory.Options();
		config.inPreferredConfig = Bitmap.Config.RGB_565;
		config.inSampleSize = 2;

		Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length, config);
		bm = rotateBitmap(bm);
		return bm;
	}

	public static Bitmap rotateBitmap(Bitmap source) {
		return rotateBitmap(source, 270);
	}

	private static Bitmap rotateBitmap(Bitmap source, float angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
				source.getHeight(), matrix, true);
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
		if (text == null || text.isEmpty())
			return "";

		StringBuilder sb = new StringBuilder();
		for (CharSequence s : text) {
			if (!(s.length() == 1 && s.charAt(0) == '\n')) {
				sb.append(s);
				sb.append("\n");
			}
		}

		if (sb.charAt(sb.length() - 1) == '\n')
			return sb.substring(0, sb.length() - 1);

		return sb.toString();
	}
}
