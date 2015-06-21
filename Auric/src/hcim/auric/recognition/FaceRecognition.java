package hcim.auric.recognition;

import hcim.auric.database.SettingsPreferences;
import hcim.auric.database.configs.PicturesDatabase;
import hcim.auric.utils.FileManager;
import hcim.auric.utils.LogUtils;
import hcim.auric.utils.StringGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
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
 *         ://github.com/ayuso2013/face-recognition/
 *         blob/master/src/org/opencv/javacv/facerecognition/FdActivity.java}
 * 
 */
public class FaceRecognition {

	private static FaceRecognition INSTANCE;

	private static final String MY_PICTURE_TYPE = "myface";
	private static final String INTRUDER_PICTURE_TYPE = "intruder";
	private static final String UNKNOWN_PICTURE_TYPE = "unknown";

	private static final int MIN = 0;

	private File cascadeFile;
	private CascadeClassifier faceDetector;
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
	 * @return maximum difference between to faces
	 */
	public int getMax() {
		return new SettingsPreferences(context).getFaceRecognitionMax();
	}

	/**
	 * 
	 * @param name
	 * @return true if name starts with "OWNER"
	 */
	public static boolean matchsOwnerName(String name) {
		return name.startsWith(StringGenerator.getOwnerPrefix());
	}

	public static String getMyPictureType() {
		return MY_PICTURE_TYPE;
	}

	public static String getIntruderPictureType() {
		return INTRUDER_PICTURE_TYPE;
	}

	public static String getUnknownPictureType() {
		return UNKNOWN_PICTURE_TYPE;
	}
	
	/**
	 * Add a Picture to the Database
	 * 
	 * @param grayBitmap
	 * @param name
	 * @return true if succeeded, false otherwise
	 */
	public boolean trainGrayPicture(Bitmap grayBitmap, String name) {
		Mat grayMat = OpenCVUtils.bitmapToMat(grayBitmap);
		Mat equalizedMat = equalizeMat(grayMat);

		MatOfRect faces = detect(equalizedMat);
		Rect[] facesArray = faces.toArray();

		boolean result;
		if (facesArray == null || facesArray.length == 0) {
			result = false;
		} else {
			Mat cropped = equalizedMat.submat(facesArray[0]); // crop
			recognizer.addPerson(cropped, name);

			cropped.release();
			result = true;
		}

		faces.release();
		equalizedMat.release();

		grayMat.release();

		return result;
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
		Mat grayMat = OpenCVUtils.bitmapToMat(grayBitmap);
		Mat equalizedMat = equalizeMat(grayMat);

		MatOfRect faces = detect(equalizedMat);
		Rect[] facesArray = faces.toArray();

		boolean result;
		if (facesArray == null || facesArray.length == 0) {
			result = false;
		} else {
			Mat cropped = equalizedMat.submat(facesArray[0]); // crop
			recognizer.addPerson(cropped, name);

			cropped.release();
			result = true;
		}

		faces.release();
		equalizedMat.release();

		grayMat.release();

		return result;
	}

	/**
	 * Train a Mat already gray and cropped
	 * 
	 * @param croppedGrayMat
	 *            : cropped gray Mat of a face previously detected
	 * @param name
	 *            : face's name
	 */
	public void trainMat(Mat croppedGrayMat, String name) {
		LogUtils.info("croppedGrayMat= channels:" + croppedGrayMat.channels()
				+ ", type:" + croppedGrayMat.type() + ",depth:"
				+ croppedGrayMat.depth());
		Mat equalizedMat = equalizeMat(croppedGrayMat);

		recognizer.addPerson(equalizedMat, name);

		equalizedMat.release();
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

	public void stopTrain() {
		recognizer.train();
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
		Mat gray16uc4 = OpenCVUtils.bitmapToMat(grayBitmap);
		Mat grayMat = new Mat(gray16uc4.height(), gray16uc4.width(), CvType.CV_8UC1);
		
		Imgproc.cvtColor(gray16uc4, grayMat, Imgproc.COLOR_BGRA2GRAY);
		Mat equalizedMat = equalizeMat(grayMat);

		MatOfRect faces = detect(equalizedMat);
		Rect[] facesArray = faces.toArray();

		RecognitionResult result;
		if (facesArray == null || facesArray.length == 0) {
			result = new RecognitionResult();
		} else {
			result = recognize(facesArray, equalizedMat);
		}

		grayMat.release();
		faces.release();
		equalizedMat.release();

		return result;
	}

	public RecognitionResult recognizeMat(Mat graySubMat) {
		String match = recognizer.predict(graySubMat);
		int difference = recognizer.getProb();

		// recognizing face
		PicturesDatabase db = PicturesDatabase.getInstance(context);
		boolean matchOwner = db.isMyPicture(match);
		boolean targetDifference = MIN <= difference && difference <= getMax();
		boolean recognized = matchOwner && targetDifference;

		RecognitionResult result = new RecognitionResult(true, recognized,
				match, difference);

		return result;
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

	public boolean detectFace(Bitmap rgbBitmap) {
		Bitmap grayBitmap = convertToGray(rgbBitmap);

		Mat rgbaMat = OpenCVUtils.bitmapToMat(rgbBitmap);
		Mat grayMat = OpenCVUtils.bitmapToMat(grayBitmap);

		Rect[] facesArray = getDetectedFaces(grayMat);

		boolean result = true;
		if (facesArray == null) {
			result = false;
		}

		rgbaMat.release();
		grayMat.release();

		return result;
	}

	public Rect[] getDetectedFaces(Mat grayMat) {
		Mat equalizedMat = equalizeMat(grayMat);

		MatOfRect faces = detect(equalizedMat);
		Rect[] facesArray = faces.toArray();

		if (facesArray == null || facesArray.length == 0) {
			return null;
		}

		faces.release();
		equalizedMat.release();

		return facesArray;
	}

	private MatOfRect detect(Mat gray) {
		MatOfRect faces = new MatOfRect();

		if (faceDetector != null)
			faceDetector.detectMultiScale(gray, faces, 1.1, 2, 2, new Size(
					absoluteFaceSize, absoluteFaceSize), new Size());

		return faces;
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

	private Mat equalizeMat(Mat gray) {
		Mat result = new Mat(gray.height(), gray.width(), CvType.CV_8UC1);
		Imgproc.equalizeHist(gray, result);
		LogUtils.info("equalized");
		return result;
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
		loaderCallback = new Loader(c);

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

	/**
	 * Class AuricBaseLoaderCallback that extends BaseLoaderCallback
	 * 
	 * @author Joana Velho
	 * 
	 */
	private class Loader extends BaseLoaderCallback {
		private Context c;

		/**
		 * Constructor
		 * 
		 * @param c
		 *            : Application context
		 */
		Loader(Context c) {
			super(c);
			this.c = c;
		}

		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				recognizer = new PersonRecognizer(path);
				recognizer.train();

				try {
					InputStream inFace = c.getResources().openRawResource(
							R.raw.lbpcascade_frontalface);
					File cascadeDir = c.getDir("cascadeFace",
							Context.MODE_PRIVATE);
					cascadeFile = new File(cascadeDir, "lbpcascade.xml");
					FileOutputStream outFace = new FileOutputStream(cascadeFile);

					byte[] buffer = new byte[4096];
					int bytesRead;
					while ((bytesRead = inFace.read(buffer)) != -1) {
						outFace.write(buffer, 0, bytesRead);
					}
					inFace.close();
					outFace.close();

					faceDetector = new CascadeClassifier(
							cascadeFile.getAbsolutePath());
					if (faceDetector.empty()) {
						faceDetector = null;
					}
					cascadeDir.delete();

				} catch (IOException e) {
					LogUtils.exception(e);
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
