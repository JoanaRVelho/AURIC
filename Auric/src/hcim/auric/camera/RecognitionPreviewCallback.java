package hcim.auric.camera;

import hcim.auric.recognition.FaceRecognition;
import hcim.auric.recognition.OpenCVUtils;
import hcim.auric.recognition.RecognitionResult;
import hcim.auric.utils.HeterogeneityManager;
import hcim.auric.utils.LogUtils;

import org.opencv.core.Mat;
import org.opencv.core.Rect;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.view.View;

public class RecognitionPreviewCallback extends View implements PreviewCallback {
	private static final int GREEN = Color.rgb(18, 174, 4);
	private static final int RED = Color.rgb(214, 23, 23);
	private static final float OFFSET_Y = 80;
	private static final float OFFSET_X = 10;

	private volatile Rect face;
	private volatile Mat rgbSubMat, graySubMat;
	private FaceRecognition faceRecognition;
	private Paint paint;
	private RecognitionResult result;
	private long timestamp;
	private int screenWidth;

	// private RecognitionActivity activity;

	public RecognitionPreviewCallback(Activity activity) {
		super(activity);
		this.faceRecognition = FaceRecognition.getInstance(activity);
		float textSize = HeterogeneityManager.getCameraTextSize(activity);
		float strokeSize = HeterogeneityManager.getCameraStrokeWidth(activity);
		screenWidth = HeterogeneityManager.getScreenWidthPixels(activity);

		this.paint = new Paint();
		this.paint.setStyle(Style.STROKE);
		paint.setTextSize(textSize);
		this.paint.setStrokeWidth(strokeSize);
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		long ti = System.currentTimeMillis();
		faceRecognition(data, camera);
		long tf = System.currentTimeMillis();
		long time = tf - ti;
		timestamp = tf;
		LogUtils.debug("executing time " + time);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (face == null || result == null) {
			return;
		}
		long x = (System.currentTimeMillis() - timestamp);
		LogUtils.debug("between recog and draw = " + x);
		LogUtils.debug(result.toString());

		if (result.isFaceRecognized()) {
			paint.setColor(GREEN);

		} else {
			paint.setColor(RED);
		}

		float leftX = face.x;
		float topY = face.y;
		float rightX = face.width + face.x;
		float bottomY = face.height + face.y;

		canvas.drawRect(leftX, topY, rightX, bottomY, paint);
		canvas.drawText(result.smallDescription(), leftX + OFFSET_X, bottomY
				+ OFFSET_Y, paint);
	}

	protected void faceRecognition(byte[] data, Camera camera) {
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

			result = faceRecognition.recognizeMat(graySubMat);
		}

		mats[0].release();
		mats[1].release();

		invalidate();
	}

}
