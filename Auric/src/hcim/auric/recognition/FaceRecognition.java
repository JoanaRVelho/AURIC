package hcim.auric.recognition;

import hcim.auric.database.ConfigurationDatabase;
import hcim.auric.database.PicturesDatabase;
import hcim.auric.utils.FileManager;

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
	public static final String MY_PICTURE_TYPE = "myface";
	public static final String INTRUDER_PICTURE_TYPE = "intruder";
	public static final String UNKNOWN_PICTURE_TYPE = "unknown";
	public static final String NAME = "OWNER";

	private static final int MIN = 0;

	public int getMax() {
		return ConfigurationDatabase.getInstance(context)
				.getFaceRecognitionMax();
	}

	private File cascadeFile;
	private CascadeClassifier cascadeClassifier;
	private int absoluteFaceSize = 0;

	private BaseLoaderCallback loaderCallback;
	private String path;
	private PersonRecognizer recognizer;
	private int lastResult;
	private boolean matchOwner;

	private Context context;

	public static FaceRecognition getInstance(Context c) {
		if (INSTANCE == null) {
			INSTANCE = new FaceRecognition(c);
		}

		return INSTANCE;
	}

	/**
	 * Should only be called after recognizePicture
	 * 
	 * @return the difference between the picture used previously and its match
	 */
	public int getLastResult() {
		return lastResult;
	}

	/**
	 * Should only be called after recognizePicture
	 * 
	 * @return true if the picture matches to a picture of the owner, false
	 *         otherwise
	 */
	public boolean lastResultMatchOwner() {
		return matchOwner;
	}

	/**
	 * 
	 * @return the object PersonRecognizer used
	 */
	public PersonRecognizer getRecognizer() {
		return recognizer;
	}

	/**
	 * 
	 * @return the object CascadeClassifier used
	 */
	public CascadeClassifier getCascadeClassifier() {
		return cascadeClassifier;
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

		boolean result;
		if (facesArray == null || facesArray.length == 0) {
			result = false;
		} else {
			train(facesArray, name, rgbMat);
			result = true;
		}

		rgbMat.release();
		grayMat.release();
		faces.release();

		return result;
	}

	/**
	 * Check if the bitmap is a picture of the owner pictures
	 * 
	 * @param rgbBitmap
	 * @return true if the bitmap matches a picture of the owner and the
	 *         difference between the two images is between FaceRecognition.MIN
	 *         and FaceRecognition.MAX.
	 */
	public boolean recognizePicture(Bitmap rgbBitmap) {
		int max = getMax();
		Bitmap grayBitmap = convertToGray(rgbBitmap);

		Mat rgbMat = new Mat();
		Mat grayMat = new Mat();

		Utils.bitmapToMat(rgbBitmap, rgbMat);
		Utils.bitmapToMat(grayBitmap, grayMat);

		MatOfRect faces = detect(grayMat);
		Rect[] facesArray = faces.toArray();

		boolean result;
		if (facesArray == null || facesArray.length == 0) {
			Log.d(TAG, "Face Recognition - face detection failed");
			result = false;
		} else {
			Log.d(TAG, "Face Recognition - face detected");
			int recognitionResult = recognize(facesArray, grayMat);
			Log.d(TAG, "Face Recognition - result = " + recognitionResult);

			result = recognitionResult >= MIN && recognitionResult <= max;
			lastResult = recognitionResult;
		}

		rgbMat.release();
		grayMat.release();
		faces.release();

		return result;
	}

	public boolean detectFace(Bitmap rgbBitmap) {
		Bitmap grayBitmap = convertToGray(rgbBitmap);

		Mat rgbMat = new Mat();
		Mat grayMat = new Mat();

		Utils.bitmapToMat(rgbBitmap, rgbMat);
		Utils.bitmapToMat(grayBitmap, grayMat);

		MatOfRect faces = detect(grayMat);
		Rect[] facesArray = faces.toArray();

		boolean result = true;
		if (facesArray == null || facesArray.length == 0) {
			Log.d(TAG, "Face Recognition - face detection failed");
			result = false;
		}

		rgbMat.release();
		grayMat.release();
		faces.release();

		return result;
	}

	private Mat gray(Mat rgb) {
		Mat gray = rgb.clone();

		for (int i = 0; i < gray.height(); i++) {
			for (int j = 0; j < gray.width(); j++) {
				double y = 0.3 * gray.get(i, j)[0] + 0.59 * gray.get(i, j)[1]
						+ 0.11 * gray.get(i, j)[2];
				gray.put(i, j, new double[] { y, y, y, 255 });
			}
		}

		return gray;
	}

	public Rect[] detectFaces(Mat rgbMat) {
		Mat grayMat = gray(rgbMat);

		MatOfRect faces = detect(grayMat);
		Rect[] facesArray = faces.toArray();

		return facesArray;
	}

	public void stopTrain() {
		recognizer.train();
	}

	public void untrainPicture(String picID) {
		File root = new File(path);
		String[] files = root.list();

		for (String filename : files) {
			if (filename.startsWith(picID)) {
				File f = new File(path, filename);
				f.delete();
			}
		}
		// labels.remove(picID);
		recognizer.untrain(picID);
	}

	public BaseLoaderCallback getLoaderCallback() {
		return loaderCallback;
	}

	private FaceRecognition(Context c) {
		this.context = c;
		loaderCallback = new MyBaseLoaderCallback(c);

		if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, context,
				loaderCallback)) {
			Log.e(TAG, "Face Recognition - Cannot connect to OpenCV Manager");
		}
		
		getMax();

		FileManager fileManager = new FileManager(c);
		path = fileManager.getOpenCVDirectory();

		// labels = new Labels(path);

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

		if (cascadeClassifier != null)
			cascadeClassifier.detectMultiScale(gray, faces, 1.1, 2, 2,
					new Size(absoluteFaceSize, absoluteFaceSize), new Size());

		return faces;
	}

	private void train(Rect[] facesArray, String name, Mat rgbMat) {
		Mat m = new Mat();
		Rect r = facesArray[0];

		m = rgbMat.submat(r);

		// if (countImages < MAX_IMG) {
		recognizer.add(m, name);
		// countImages++;
		// }

		m.release();
	}

	private int recognize(Rect[] facesArray, Mat grayMat) {
		Mat m = new Mat();
		m = grayMat.submat(facesArray[0]);
		String resultString = recognizer.predict(m);
		PicturesDatabase db = PicturesDatabase.getInstance(context);

		Log.d(TAG, "Face Recognition - matches " + resultString);

		int result = -1;
		matchOwner = false;
		if (db.isMyPicture(resultString)) {
			result = recognizer.getProb();
			matchOwner = true;
		}

		m.release();

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

					cascadeClassifier = new CascadeClassifier(
							cascadeFile.getAbsolutePath());
					if (cascadeClassifier.empty()) {
						Log.e(TAG,
								"Face Recognition - Failed to load cascade classifier");
						cascadeClassifier = null;
					} else {
						Log.d(TAG,
								"Face Recognition - Loaded cascade classifier from "
										+ cascadeFile.getAbsolutePath());
					}
					cascadeDir.delete();

				} catch (IOException e) {
					Log.e(TAG, "Face Recognition - Failed to load cascade. "
							+ "Exception thrown: " + e);
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

}
