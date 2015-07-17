package hcim.auric.recognition;

import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import hcim.auric.utils.LogUtils;
import hcim.auric.utils.OpenCVUtils;
import hcim.auric.utils.StringGenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;

import com.googlecode.javacv.cpp.opencv_contrib.FaceRecognizer;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_core.MatVector;
import com.googlecode.javacv.cpp.opencv_imgproc;

/**
 * 
 * @author Joana Velho
 * 
 *         An adaptation of {@link https
 *         ://github.com/ayuso2013/face-recognition/
 *         blob/master/src/org/opencv/javacv
 *         /facerecognition/PersonRecognizer.java}
 * 
 */
public class PersonRecognizer {

	private static final int WIDTH = 128;
	private static final int HEIGHT = 128;

	private FaceRecognizer faceRecognizer;
	private String path;
	private LabelRepository labelsFile;

	public PersonRecognizer(String path) {
		faceRecognizer = com.googlecode.javacv.cpp.opencv_contrib
				.createLBPHFaceRecognizer(2, 8, 8, 8, 200);
		// faceRecognizer = com.googlecode.javacv.cpp.opencv_contrib
		// .createFisherFaceRecognizer(0,200);
		// faceRecognizer = com.googlecode.javacv.cpp.opencv_contrib
		// .createEigenFaceRecognizer(80, 200);
		this.path = path;
		labelsFile = new LabelRepository(path);
	}

	/**
	 * Add new person's face to database
	 * 
	 * @param m
	 *            : face's data
	 * @param description
	 *            : face's description
	 */
	public String addPerson(Mat m, String description) {
		Bitmap bmp = OpenCVUtils.matToBitmap(m);
		bmp = Bitmap.createScaledBitmap(bmp, WIDTH, HEIGHT, false);
		int count = numberOfPictures();

		FileOutputStream f;
		try {
			f = new FileOutputStream(new File(path, description + "-" + count
					+ ".jpg"), true);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, f);
			f.close();

		} catch (Exception e) {
			LogUtils.exception(e);
		}
		return description + "-" + count;
	}

	public int numberOfPictures() {
		FilenameFilter filter = new FilenameFilter() {
			@SuppressLint("DefaultLocale")
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".jpg");
			};
		};
		File root = new File(path);
		return root.list(filter).length;
	}

	public boolean train() {
		File root = new File(path);

		FilenameFilter jpgFilter = new FilenameFilter() {
			@SuppressLint("DefaultLocale")
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".jpg");
			};
		};

		File[] imageFiles = root.listFiles(jpgFilter);

		MatVector images = new MatVector(imageFiles.length);

		int[] labels = new int[imageFiles.length];

		int counter = 0;
		int label;

		IplImage img = null;
		// IplImage grayImg;

		int i1 = path.length();

		for (File image : imageFiles) {
			String p = image.getAbsolutePath();
			// img = cvLoadImage(p);
			img = cvLoadImage(p, Highgui.CV_LOAD_IMAGE_GRAYSCALE);

			int i2 = p.lastIndexOf("-"); // <name>-<number>
			// int i3 = p.lastIndexOf("."); // .jpg
			// int icount = Integer.parseInt(p.substring(i2 + 1, i3));
			// if (count < icount)
			// count++;

			String description = p.substring(i1 + 1, i2);

			if (labelsFile.getLabelNumber(description) < 0)
				labelsFile.addLabel(description,
						labelsFile.numberOfSubjects() + 1);

			label = labelsFile.getLabelNumber(description);

			// grayImg = IplImage.create(img.width(), img.height(),
			// IPL_DEPTH_8U,
			// 1);
			// cvCvtColor(img, grayImg, CV_BGR2GRAY);
			// images.put(counter, grayImg);
			images.put(counter, img);

			labels[counter] = label;

			counter++;
		}
		if (counter > 0)
			if (labelsFile.numberOfSubjects() > 1)
				faceRecognizer.train(images, labels);
		labelsFile.store();
		return true;
	}

	public RecognitionResult predict(Mat m, int min, int max) {
		int subjectNumber[] = new int[1];
		double confidence[] = new double[1];
		IplImage ipl = matToIplImage(m, WIDTH, HEIGHT);

		faceRecognizer.predict(ipl, subjectNumber, confidence);
		
		int difference;
		String match;

		if (subjectNumber[0] != -1) {
			difference = (int) confidence[0];
			match = labelsFile.getLabelName(subjectNumber[0]);
		} else {
			difference = -1;
			match = "Unkown";
		}
		// recognizing face
		boolean matchOwner = match.startsWith(StringGenerator.getOwnerPrefix());
		boolean targetDifference = min <= difference && difference <= max;
		boolean recognized = matchOwner && targetDifference;

		RecognitionResult result = new RecognitionResult(true, recognized,
				match, difference);
		return result;
	}

	public void untrain(String picID) {
		labelsFile.removeLabel(picID);
	}

	private IplImage matToIplImage(Mat m, int width, int heigth) {
		Bitmap bmp = OpenCVUtils.matToBitmap(m);

		return bitmapToIplImage(bmp, width, heigth);
	}

	private IplImage bitmapToIplImage(Bitmap bmp, int width, int height) {

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
}