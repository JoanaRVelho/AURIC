package hcim.auric.recognition;

import hcim.auric.database.PicturesDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
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

import com.hcim.intrusiondetection.R;

/**
 * https://github.com/ayuso2013/face-recognition
 */
public class FaceRecognition {

	private static FaceRecognition INSTANCE;

	private static final String TAG = "AURIC";

	static final int MY_PICTURE = 1;
	static final int OTHER_PICTURE = 2;
	static final int CHECK = 3;
	static final long MAX_IMG = 10;

	public static final String MY_PICTURE_TYPE = "myface";
	public static final String INTRUDER_PICTURE_TYPE = "intruder";

	private File cascadeFile;
	private CascadeClassifier faceDetector;
	private int absoluteFaceSize = 0;

	private BaseLoaderCallback loaderCallback;
	String path = "";
	PersonRecognizer recognizer;
	int[] labels = new int[(int) MAX_IMG];
	int countImages = 0;
	Labels labelsFile;
	private Context context;

	public static FaceRecognition getInstance(Context c) {
		if (INSTANCE == null) {
			String filesDir = c.getFilesDir().toString();
			INSTANCE = new FaceRecognition(c, filesDir);
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
	
	public boolean detectFace(Bitmap rgbBitmap){
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

	MatOfRect detect(Mat gray) {
		MatOfRect faces = new MatOfRect();

		if (faceDetector != null)
			faceDetector.detectMultiScale(gray, faces, 1.1, 2, 2, new Size(
					absoluteFaceSize, absoluteFaceSize), new Size());

		return faces;
	}

	FaceRecognition(Context c, String filesDir) {
		this.context = c;
		loaderCallback = new MyBaseLoaderCallback(c);

		if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, context,
				loaderCallback)) {
			Log.e(TAG, "Face Recognition - Cannot connect to OpenCV Manager");
		}

		path = filesDir + "/facerecogOCV/";

		labelsFile = new Labels(path);

		boolean success = (new File(path)).mkdirs();
		if (!success) {
			Log.e(TAG, "Face Recognition - Error creating directory");
		}
	}

	void train(Rect[] facesArray, String name, Mat rgbMat) {
		Mat m = new Mat();
		Rect r = facesArray[0];

		m = rgbMat.submat(r);

		if (countImages < MAX_IMG) {
			recognizer.add(m, name);
			countImages++;
		}

	}

	int recognize(Rect[] facesArray, Mat grayMat) {
		Mat m = new Mat();
		m = grayMat.submat(facesArray[0]);
		String resultString = recognizer.predict(m);
		PicturesDatabase db = PicturesDatabase.getInstance(context);

		int result = -1;
		if (db.isMyPicture(resultString)) {
			result = recognizer.getProb();
		} else {
			Log.d(TAG, "Face Recognition - matches " + resultString);
		}
		return result;
	}

	static Bitmap convertToGray(Bitmap img) {
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

	private class MyBaseLoaderCallback extends BaseLoaderCallback {
		private Context c;

		MyBaseLoaderCallback(Context c) {
			super(c);
			this.c = c;
		}

		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				Log.d(TAG, "Face Recognition - OpenCV loaded successfully");

				recognizer = new PersonRecognizer(path);
				recognizer.load();

				try {
					InputStream is = c.getResources().openRawResource(
							R.raw.lbpcascade_frontalface);
					File cascadeDir = c.getDir("cascade", Context.MODE_PRIVATE);
					cascadeFile = new File(cascadeDir, "lbpcascade.xml");
					FileOutputStream os = new FileOutputStream(cascadeFile);

					byte[] buffer = new byte[4096];
					int bytesRead;
					while ((bytesRead = is.read(buffer)) != -1) {
						os.write(buffer, 0, bytesRead);
					}
					is.close();
					os.close();

					faceDetector = new CascadeClassifier(
							cascadeFile.getAbsolutePath());
					if (faceDetector.empty()) {
						Log.e(TAG,
								"Face Recognition - Failed to load cascade classifier");
						faceDetector = null;
					} else
						Log.d(TAG,
								"Face Recognition - Loaded cascade classifier from "
										+ cascadeFile.getAbsolutePath());

					cascadeDir.delete();

				} catch (IOException e) {
					Log.e(TAG,
							"Face Recognition - Failed to load cascade. Exception thrown: "
									+ e);
				}
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}
	}

	public void untrainPicture(String picID) {
		// TODO Auto-generated method stub

	}
}
