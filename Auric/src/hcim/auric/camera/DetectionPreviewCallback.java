package hcim.auric.camera;

import hcim.auric.recognition.FaceRecognition;
import hcim.auric.recognition.OpenCVUtils;
import hcim.auric.recognition.Picture;
import hcim.auric.utils.HeterogeneityManager;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.view.View;

/**
 * 
 * @author Joana Velho
 * 
 */
public class DetectionPreviewCallback extends View implements PreviewCallback {
	private static final int SKY = Color.rgb(0, 153, 204);

	private volatile Rect face;
	private volatile Mat rgbSubMat, graySubMat;
	private FaceRecognition faceRecognition;
	private Paint paint;
	private volatile boolean stop;
	private int screenWidth;
	private DetectionActivity activity;


	public DetectionPreviewCallback(DetectionActivity activity,
			FaceRecognition fr) {
		super(activity);
		this.activity = activity;

		screenWidth = HeterogeneityManager.getScreenWidthPixels(activity);
		float stroke = HeterogeneityManager.getCameraStrokeWidth(activity);
		
		faceRecognition = fr;

		paint = new Paint();
		paint.setColor(SKY);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(stroke);
	}

	public void stopPreviewCallback() {
		this.stop = true;
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		if (stop)
			return;

		stop = true;
		faceDetection(data, camera);
		stop = false;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (face == null) {
			return;
		}

		float leftX = face.x;
		float topY = face.y;
		float rightX = face.width + face.x;
		float bottomY = face.height + face.y;

		canvas.drawRect(leftX, topY, rightX, bottomY, paint);
	}

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

			activity.setButtonVisibility(View.VISIBLE);
		} else {
			activity.setButtonVisibility(View.INVISIBLE);
		}

		mats[0].release();
		mats[1].release();

		invalidate();
	}

	public Picture takePicture(String name) {
		if (stop)
			return null;

		stop = true;
		Picture result = null;

		if (rgbSubMat != null && graySubMat != null) {
			Mat rgb = rgbSubMat;
			Mat gray = graySubMat;
			rgbSubMat = null;
			graySubMat = null;

			faceRecognition.trainMat(gray, name); // train gray 
			Bitmap img = OpenCVUtils.matToBitmap(rgb);

			result = new Picture(name, FaceRecognition.getMyPictureType(), img);

			rgb.release();
			gray.release();
		}

		stop = false;
		return result;
	}
}
