package hcim.auric.recognition;

import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.util.Log;

import com.googlecode.javacv.cpp.opencv_contrib.FaceRecognizer;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_core.MatVector;
import com.googlecode.javacv.cpp.opencv_imgproc;

/**
 * https://github.com/ayuso2013/face-recognition
 */
public class PersonRecognizer {

	private static final String TAG = "AURIC";

	public final static int MAXIMG = 100;
	private static final int WIDTH = 128;
	private static final int HEIGHT = 128;

	private FaceRecognizer faceRecognizer;
	private String path;
	private int count = 0;
	private Labels labelsFile;

	private int prob = 999;

	public PersonRecognizer(String path) {
		faceRecognizer = com.googlecode.javacv.cpp.opencv_contrib
				.createLBPHFaceRecognizer(2, 8, 8, 8, 200);
		this.path = path;
		labelsFile = new Labels(path);

	}

	void add(Mat m, String description) {
		Bitmap bmp = Bitmap.createBitmap(m.width(), m.height(),
				Bitmap.Config.ARGB_8888);

		Utils.matToBitmap(m, bmp);
		bmp = Bitmap.createScaledBitmap(bmp, WIDTH, HEIGHT, false);

		FileOutputStream f;
		try {
			f = new FileOutputStream(new File(path, description + "-" + count
					+ ".jpg"), true);
			count++;
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, f);
			f.close();

		} catch (Exception e) {
			Log.e(TAG,
					"Person Recognizer - " + e.getCause() + " "
							+ e.getMessage());
		}
	}

	boolean train() {
		File root = new File(path);

		FilenameFilter pngFilter = new FilenameFilter() {
			@SuppressLint("DefaultLocale")
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".jpg");
			};
		};

		File[] imageFiles = root.listFiles(pngFilter);

		MatVector images = new MatVector(imageFiles.length);

		int[] labels = new int[imageFiles.length];

		int counter = 0;
		int label;

		IplImage img = null;
		IplImage grayImg;

		int i1 = path.length();

		for (File image : imageFiles) {
			String p = image.getAbsolutePath();
			img = cvLoadImage(p);

			int i2 = p.lastIndexOf("-");
			int i3 = p.lastIndexOf(".");
			int icount = Integer.parseInt(p.substring(i2 + 1, i3));
			if (count < icount)
				count++;

			String description = p.substring(i1 + 1, i2);

			if (labelsFile.get(description) < 0)
				labelsFile.add(description, labelsFile.max() + 1);

			label = labelsFile.get(description);

			grayImg = IplImage.create(img.width(), img.height(), IPL_DEPTH_8U,
					1);

			cvCvtColor(img, grayImg, CV_BGR2GRAY);

			images.put(counter, grayImg);

			labels[counter] = label;

			counter++;
		}
		if (counter > 0)
			if (labelsFile.max() > 1)
				faceRecognizer.train(images, labels);
		labelsFile.save();
		return true;
	}

	boolean canPredict() {
		if (labelsFile.max() > 1)
			return true;
		else
			return false;

	}

	public String predict(Mat m) {
		if (!canPredict())
			return "";
		int n[] = new int[1];
		double p[] = new double[1];
		IplImage ipl = matToIplImage(m, WIDTH, HEIGHT);

		faceRecognizer.predict(ipl, n, p);

		if (n[0] != -1)
			prob = (int) p[0];
		else
			prob = -1;

		if (n[0] != -1)
			return labelsFile.get(n[0]);
		else
			return "Unkown";
	}

	IplImage matToIplImage(Mat m, int width, int heigth) {

		Bitmap bmp = Bitmap.createBitmap(m.width(), m.height(),
				Bitmap.Config.ARGB_8888);

		Utils.matToBitmap(m, bmp);
		return bitmapToIplImage(bmp, width, heigth);

	}

	IplImage bitmapToIplImage(Bitmap bmp, int width, int height) {

		if ((width != -1) || (height != -1)) {
			Bitmap bmp2 = Bitmap.createScaledBitmap(bmp, width, height, false);
			bmp = bmp2;
		}

		IplImage image = IplImage.create(bmp.getWidth(), bmp.getHeight(),
				IPL_DEPTH_8U, 4);

		bmp.copyPixelsToBuffer(image.getByteBuffer());

		IplImage grayImg = IplImage.create(image.width(), image.height(),
				IPL_DEPTH_8U, 1);

		cvCvtColor(image, grayImg, opencv_imgproc.CV_BGR2GRAY);

		return grayImg;
	}

	void saveBitmap(Bitmap bmp, String path) {
		FileOutputStream file;
		try {
			file = new FileOutputStream(path, true);

			bmp.compress(Bitmap.CompressFormat.JPEG, 100, file);
			file.close();
		} catch (Exception e) {
			Log.e(TAG, "Person Recognizer - " + e.getMessage() + e.getCause());
		}

	}

	public void load() {
		train();
	}

	public int getProb() {
		return prob;
	}

	public void untrain(String picID) {
		labelsFile.remove(picID);
	}
}