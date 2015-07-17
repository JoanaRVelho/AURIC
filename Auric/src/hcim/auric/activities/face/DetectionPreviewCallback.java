package hcim.auric.activities.face;

import hcim.auric.Picture;
import hcim.auric.data.PicturesDatabase;
import hcim.auric.data.SettingsPreferences;
import hcim.auric.recognition.FaceRecognition;
import hcim.auric.utils.HeterogeneityManager;
import hcim.auric.utils.OpenCVUtils;
import hcim.auric.utils.StringGenerator;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.view.View;

/**
 * {@link android.view.View} and {@link android.hardware.Camera.PreviewCallback}
 * used by {@link hcim.auric.activities.face.DetectionActivity}
 * 
 * @author Joana Velho
 * 
 */
public class DetectionPreviewCallback extends View implements PreviewCallback {
	private volatile Rect face;
	private volatile Mat rgbSubMat, graySubMat;
	private FaceRecognition faceRecognition;
	private volatile boolean stop;
	private int screenWidth;
	private DetectionActivity activity;
	private PicturesDatabase picDB;
	private int idx;
	private String name;
	private static final int MAX = 10;

	/**
	 * Constructor
	 * 
	 * @param activity
	 */
	public DetectionPreviewCallback(DetectionActivity activity) {
		super(activity);
		this.activity = activity;

		picDB = PicturesDatabase.getInstance(activity);
		faceRecognition = FaceRecognition.getInstance(activity);

		SettingsPreferences s = new SettingsPreferences(activity);
		boolean hasStarted = s.hasPreviouslyStarted();
		name = hasStarted ? StringGenerator.generateOwnerName()
				: StringGenerator.getOwnerPrefix();

		screenWidth = HeterogeneityManager.getScreenWidthPixels(activity);
		idx = 0;
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		if (stop)
			return;

		stop = true;
		faceDetection(data, camera);
		stop = false;
	}

	/**
	 * Process face detection and training
	 * 
	 * @param data
	 *            : picture data
	 * @param camera
	 *            : camera
	 */
	protected void faceDetection(byte[] data, Camera camera) {
		Mat[] mats = OpenCVUtils.getMatArray(data, camera);
		Rect[] faces = faceRecognition.getDetectedFaces(mats[0]);
		face = OpenCVUtils.chooseBestRect(faces, screenWidth);

		if (face != null) {
			if (graySubMat != null)
				graySubMat.release();

			if (rgbSubMat != null)
				rgbSubMat.release();

			graySubMat = mats[0].submat(face);
			rgbSubMat = mats[1].submat(face);

			String label = faceRecognition.trainMat(graySubMat, name);

			Bitmap img = OpenCVUtils.matToBitmap(rgbSubMat);

			Picture result = new Picture(label,
					FaceRecognition.getMyPictureType(), img);

			idx++;
			activity.bar.setProgress(idx * 10);
			if (idx == MAX) {
				picDB.addPicture(result);
				faceRecognition.stopTrain();
				activity.setUpDone();
			}
		}

		mats[0].release();
		mats[1].release();
	}
}