package hcim.auric.recognition;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import android.graphics.Bitmap;
import android.hardware.Camera;

public class OpenCVUtils {

	// private static final int MIN_SIZE = 650;

	public static Bitmap matToBitmap(Mat m) {
		Bitmap b = Bitmap.createBitmap(m.width(), m.height(),
				Bitmap.Config.ARGB_8888);
		Utils.matToBitmap(m, b);

		return b;
	}

	public static Mat bitmapToMat(Bitmap b) {
		Mat m = new Mat();
		Utils.bitmapToMat(b, m);
		return m;
	}

	public static Mat[] getMatArray(byte[] data, Camera camera) {
		Camera.Parameters parameters = camera.getParameters();
		int width = parameters.getPreviewSize().width;
		int height = parameters.getPreviewSize().height;

		Mat yuv = new Mat(height + height / 2, width, CvType.CV_8UC1);
		Mat rgba = new Mat(height, width, CvType.CV_8UC1);
		yuv.put(0, 0, data);
		Imgproc.cvtColor(yuv, rgba, Imgproc.COLOR_YUV2RGB_NV21);
		
		Mat rgbaResult = rotateMat(rgba);
		Mat grayResult = new Mat(rgbaResult.height(), rgbaResult.width(),
				CvType.CV_8UC1);
		Imgproc.cvtColor(rgbaResult, grayResult, Imgproc.COLOR_BGR2GRAY);

		yuv.release();
		rgba.release();
		Mat[] result = { grayResult, rgbaResult };
		return result;
	}

	public static Mat rotateMat(Mat src) {
		Mat dst = new Mat(src.cols(), src.rows(), src.type());
		Core.flip(src.t(), dst, 0);
		return dst;
	}

	public static Rect chooseBestRect(Rect[] faces, int screenWidth) {
		if (faces == null)
			return null;

		Rect result = faces[0];

		for (int i = 1; i < faces.length; i++) {
			if (faces[i].width > result.width)
				result = faces[i];
		}

		if (result.width < (screenWidth / 2))
			return null;

		return result;
	}

	// public static Rect chooseBiggestRect(Rect[] faces) {
	// if (faces == null)
	// return null;
	//
	// Rect result = faces[0];
	//
	// for (int i = 1; i < faces.length; i++) {
	// if (faces[i].width > result.width)
	// result = faces[i];
	// }
	//
	// return result;
	// }
}
