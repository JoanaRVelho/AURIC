package hcim.auric.recognition;

import hcim.auric.database.PicturesDatabase;
import hcim.auric.utils.FileManager;

import java.io.File;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.Log;

/**
 * https://github.com/ayuso2013/face-recognition
 */
public class FaceRecognition {

	private static FaceRecognition INSTANCE;

	private static final String TAG = "AURIC";

	public static final long MAX_IMG = 10;
	public static final String MY_PICTURE_TYPE = "myface";
	public static final String INTRUDER_PICTURE_TYPE = "intruder";
	public static final String UNKNOWN_PICTURE_TYPE = "unknown";

	private CascadeClassifier faceDetector;
	private int absoluteFaceSize = 0;

	private OpenCVBaseLoaderCallback loaderCallback;
	private String path;
	private PersonRecognizer recognizer;
	private int countImages = 0;
	private Context context;

	public static FaceRecognition getInstance(Context c) {
		if (INSTANCE == null) {
			INSTANCE = new FaceRecognition(c);
		}
		return INSTANCE;
	}

	/**
	 * Add a Picture to the Database
	 * 
	 * @param rgbBitmap
	 * @param name
	 * @return true if succeeded, false otherwise
	 */
	public boolean trainPicture(Bitmap rgbBitmap, String name) {
		Bitmap grayBitmap = convertToGray(rgbBitmap);

		Mat rgbMat = new Mat();
		Mat grayMat = new Mat();

		Utils.bitmapToMat(rgbBitmap, rgbMat);
		Utils.bitmapToMat(grayBitmap, grayMat);

		MatOfRect faces = detect(grayMat);
		Rect[] facesArray = faces.toArray();

		if (facesArray == null || facesArray.length == 0) {
			return false;
		} else {
			train(facesArray, name, rgbMat);
			return true;
		}
	}

	public boolean recognizePicture(Bitmap rgbBitmap) {
		Bitmap grayBitmap = convertToGray(rgbBitmap);

		Mat rgbMat = new Mat();
		Mat grayMat = new Mat();

		Utils.bitmapToMat(rgbBitmap, rgbMat);
		Utils.bitmapToMat(grayBitmap, grayMat);

		MatOfRect faces = detect(grayMat);
		Rect[] facesArray = faces.toArray();

		if (facesArray == null || facesArray.length == 0) {
			Log.d(TAG, "Face Recognition - face detection failed");
			return false;
		} else {
			Log.d(TAG, "Face Recognition - face detected");
			int result = recognize(facesArray, grayMat);
			Log.d(TAG, "Face Recognition - result = " + result);

			return result > 0 && result < 80;
		}
	}

	public boolean detectFace(Bitmap rgbBitmap) {
		Bitmap grayBitmap = convertToGray(rgbBitmap);

		Mat rgbMat = new Mat();
		Mat grayMat = new Mat();

		Utils.bitmapToMat(rgbBitmap, rgbMat);
		Utils.bitmapToMat(grayBitmap, grayMat);

		MatOfRect faces = detect(grayMat);
		Rect[] facesArray = faces.toArray();

		if (facesArray == null || facesArray.length == 0) {
			Log.d(TAG, "Face Recognition - face detection failed");
			return false;
		}
		return true;
	}

	public void stopTrain() {
		recognizer.train();
	}

	public void untrainPicture(String picID) {
		// TODO Auto-generated method stub
	}

	private FaceRecognition(Context c) {
		this.context = c;
		this.loaderCallback = new OpenCVBaseLoaderCallback(c);
		this.faceDetector = loaderCallback.getCascadeClassifier();
		this.recognizer = loaderCallback.getPersonRecognizer();

		if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, context,
				loaderCallback)) {
			Log.e(TAG, "Face Recognition - Cannot connect to OpenCV Manager");
		}

		FileManager fileManager = new FileManager(c);
		path = fileManager.getOpenCVDirectory();

		new Labels(path);

		File f = new File(path);
		if (!f.exists()) {
			boolean success = f.mkdirs();
			if (!success) {
				Log.e(TAG, "Face Recognition - Error creating directory");
			} else {
				Log.i(TAG, "Face Recognition - Directory created");
			}
		} else {
			Log.i(TAG, "Face Recognition - Directory already exists");
		}
	}

	private MatOfRect detect(Mat gray) {
		MatOfRect faces = new MatOfRect();

		if (faceDetector != null)
			faceDetector.detectMultiScale(gray, faces, 1.1, 2, 2, new Size(
					absoluteFaceSize, absoluteFaceSize), new Size());

		return faces;
	}

	private void train(Rect[] facesArray, String name, Mat rgbMat) {
		Mat m = new Mat();
		Rect r = facesArray[0];

		m = rgbMat.submat(r);

		if (countImages < MAX_IMG) {
			recognizer.add(m, name);
			countImages++;
		}

	}

	private int recognize(Rect[] facesArray, Mat grayMat) {
		Mat m = new Mat();
		m = grayMat.submat(facesArray[0]);
		String resultString = recognizer.predict(m);
		PicturesDatabase db = PicturesDatabase.getInstance(context);

		Log.d(TAG, "Face Recognition - matches " + resultString);

		int result = -1;
		if (db.isMyPicture(resultString)) {
			result = recognizer.getProb();
		}
		return result;
	}

	private static Bitmap convertToGray(Bitmap img) {
		int width, height;
		height = img.getHeight();
		width = img.getWidth();

		Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);
		Canvas c = new Canvas(bmpGrayscale);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(img, 0, 0, paint);

		return bmpGrayscale;
	}
}
