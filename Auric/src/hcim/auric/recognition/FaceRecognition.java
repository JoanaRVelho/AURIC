package hcim.auric.recognition;

import hcim.auric.database.configs.ConfigurationDatabase;
import hcim.auric.database.configs.PicturesDatabase;
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

import com.hcim.intrusiondetection.R;

/**
 * 
 * @author Joana Velho
 * 
 *         An adaptation of {@link https
 *         ://github.com/ayuso2013/face-recognition}
 * 
 */
public class FaceRecognition {

	private static FaceRecognition INSTANCE;

	public static final String TAG = "AURIC";
	public static final String MY_PICTURE_TYPE = "myface";
	public static final String INTRUDER_PICTURE_TYPE = "intruder";
	public static final String UNKNOWN_PICTURE_TYPE = "unknown";
	private static final String OWNER = "OWNER";

	private static final int MIN = 0;

	private File cascadeFile;
	private CascadeClassifier cascadeClassifier;
	private int absoluteFaceSize = 0;

	private BaseLoaderCallback loaderCallback;
	private String path;
	private PersonRecognizer recognizer;

	private Context context;

	public static FaceRecognition getInstance(Context c) {
		if (INSTANCE == null) {
			INSTANCE = new FaceRecognition(c);
		}

		return INSTANCE;
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
	 * 
	 * @return maximum difference between to faces
	 */
	public int getMax() {
		return ConfigurationDatabase.getInstance(context)
				.getFaceRecognitionMax();
	}

	/**
	 * 
	 * @param name
	 * @return true if name starts with "OWNER"
	 */
	public static boolean matchsOwnerName(String name) {
		return name.startsWith(OWNER);
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
	public RecognitionResult recognizePicture(Bitmap rgbBitmap) {
		Bitmap grayBitmap = convertToGray(rgbBitmap);

		Mat rgbMat = new Mat();
		Mat grayMat = new Mat();

		Utils.bitmapToMat(rgbBitmap, rgbMat);
		Utils.bitmapToMat(grayBitmap, grayMat);

		MatOfRect faces = detect(grayMat);
		Rect[] facesArray = faces.toArray();

		RecognitionResult result;
		if (facesArray == null || facesArray.length == 0) {
			result = new RecognitionResult();
		} else {
			result = recognize(facesArray, grayMat);
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
			result = false;
		}

		rgbMat.release();
		grayMat.release();
		faces.release();

		return result;
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
		recognizer.untrain(picID);
	}

	public BaseLoaderCallback getLoaderCallback() {
		return loaderCallback;
	}

	/**
	 * Constructor
	 * 
	 * @param c
	 *            : Application context
	 */
	private FaceRecognition(Context c) {
		this.context = c;
		loaderCallback = new AuricBaseLoaderCallback(c);

		if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, context,
				loaderCallback)) {
			// Log.e(TAG,
			// "Face Recognition - Cannot connect to OpenCV Manager");
		}

		getMax();

		FileManager fileManager = new FileManager(c);
		path = fileManager.getOpenCVDirectory();

		File f = new File(path);
		if (!f.exists()) {
			boolean success = f.mkdirs();
			if (!success) {
				// Log.e(TAG, "Face Recognition - Error creating directory");
			} else {
				// Log.i(TAG, "Face Recognition - Directory created");
			}
		} else {
			// Log.i(TAG, "Face Recognition - Directory already exists");
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

	/**
	 * Auxiliary method of {@link recognizePicture}
	 * 
	 * @param facesArray
	 *            : an array of {@link org.opencv.core.Rect} that represents the
	 *            faces detected
	 * @param grayMat
	 *            : gray scale {@link org.opencv.core.Mat}
	 * @return a {@link RecognitionResult} object
	 * @requires facesArray != null && facesArray.length > 0 && grayMat != null
	 */
	private RecognitionResult recognize(Rect[] facesArray, Mat grayMat) {
		Mat m = new Mat();
		m = grayMat.submat(facesArray[0]);
		String match = recognizer.predict(m);
		int difference = recognizer.getProb();

		// recognizing face
		PicturesDatabase db = PicturesDatabase.getInstance(context);
		boolean matchOwner = db.isMyPicture(match);
		boolean targetDifference = MIN <= difference && difference <= getMax();
		boolean recognized = matchOwner && targetDifference;

		RecognitionResult result = new RecognitionResult(true, recognized,
				match, difference);

		m.release();

		return result;
	}

	/**
	 * Converts a RGB bitmap into a gray bitmap
	 * 
	 * @param img
	 *            : a RGB bitmap
	 * @return gray bitmap
	 */
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

	/**
	 * Class AuricBaseLoaderCallback that extends BaseLoaderCallback
	 * 
	 * @author Joana Velho
	 * 
	 */
	private class AuricBaseLoaderCallback extends BaseLoaderCallback {
		private Context c;

		/**
		 * Constructor
		 * 
		 * @param c
		 *            : Application context
		 */
		AuricBaseLoaderCallback(Context c) {
			super(c);
			this.c = c;
		}

		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				// Log.d(TAG, "Face Recognition - OpenCV loaded successfully");

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
						// Log.e(TAG,
						// "Face Recognition - Failed to load cascade classifier");
						cascadeClassifier = null;
					} else {
						// Log.d(TAG,
						// "Face Recognition - Loaded cascade classifier from "
						// + cascadeFile.getAbsolutePath());
					}
					cascadeDir.delete();

				} catch (IOException e) {
					// Log.e(TAG, "Face Recognition - Failed to load cascade. "
					// + "Exception thrown: " + e);
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
