package hcim.auric.activities.setup;

import hcim.auric.database.ConfigurationDatabase;
import hcim.auric.recognition.FaceRecognition;
import hcim.auric.recognition.PersonRecognizer;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.hcim.intrusiondetection.R;

public class TestFaceRecognition extends Activity implements
		CvCameraViewListener2 {

	private static final String SEARCH = "search";
	
	private static final Scalar FACE_RECT_GREEN = new Scalar(0, 255, 0, 255);
	private static final Scalar FACE_RECT_RED = new Scalar(255, 0, 0, 255);
	
	private static final int FRONT_CAMERA = 1;
	private static final int BACK_CAMERA = 2;

	private Mat mRgba;
	private Mat mGray;
	private CascadeClassifier cascadeClassifier;

	private float relativeFaceSize = 0.2f;
	private int absoluteFaceSize = 0;
	private int recognitionResult = 999;

	private String resultName;
	private CameraView cameraView;
	private FaceRecognition faceRecognition;
	private PersonRecognizer personRecognizer;
	private int currentCamera;
	private TextView result;

	private int max;

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			if (msg != null && msg.obj != null && msg.obj.equals(SEARCH)) {
				if (resultName != null
						&& resultName.startsWith(FaceRecognition.NAME)) {
					result.setText("Match the owner - Difference="
							+ recognitionResult);
				} else {
					result.setText("Does not match the owner");
				}
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.test_surface_view);

		cameraView = (CameraView) findViewById(R.id.surface_view);
		cameraView.setCvCameraViewListener(this);

		faceRecognition = FaceRecognition.getInstance(this);
		cascadeClassifier = faceRecognition.getCascadeClassifier();
		currentCamera = BACK_CAMERA;

		result = (TextView) findViewById(R.id.result_msg);
		
		max = ConfigurationDatabase.getInstance(this).getFaceRecognitionMax();
	}

	@Override
	protected void onStart() {
		if (cascadeClassifier == null)
			cascadeClassifier = faceRecognition.getCascadeClassifier();
		super.onStart();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (cameraView != null)
			cameraView.disableView();
	}

	@Override
	public void onResume() {
		super.onResume();

		if (cascadeClassifier == null)
			cascadeClassifier = faceRecognition.getCascadeClassifier();

		if (personRecognizer == null)
			personRecognizer = faceRecognition.getRecognizer();

		cameraView.enableView();
	}

	public void onDestroy() {
		super.onDestroy();
		cameraView.disableView();
	}

	public void onCameraViewStarted(int width, int height) {
		mGray = new Mat();
		mRgba = new Mat();
	}

	public void onCameraViewStopped() {
		mGray.release();
		mRgba.release();
	}

	public void switchCamera(View v) {
		if (currentCamera == BACK_CAMERA) {
			currentCamera = FRONT_CAMERA;
			cameraView.setFrontCamera();
		} else {
			currentCamera = BACK_CAMERA;
			cameraView.setBackCamera();
		}
	}

	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		mRgba = inputFrame.rgba();
		mGray = inputFrame.gray();

		if (absoluteFaceSize == 0) {
			int height = mGray.rows();
			if (Math.round(height * relativeFaceSize) > 0) {
				absoluteFaceSize = Math.round(height * relativeFaceSize);
			}
		}

		MatOfRect faces = new MatOfRect();

		if (cascadeClassifier != null)
			cascadeClassifier.detectMultiScale(mGray, faces, 1.1, 2, 2,
					new Size(absoluteFaceSize, absoluteFaceSize), new Size());

		Rect[] facesArray = faces.toArray();

		if ((facesArray.length >= 1)) {
			int i = 0;
			Mat m = new Mat();
			m = mGray.submat(facesArray[i]);

			Message msg = new Message();
			msg.obj = SEARCH;
			handler.sendMessage(msg);

			resultName = personRecognizer.predict(m);
			recognitionResult = personRecognizer.getProb();
			msg = new Message();
			msg.obj = SEARCH;
			handler.sendMessage(msg);

			if (resultName.startsWith(FaceRecognition.NAME)) {
				if (recognitionResult < 0)
					Core.rectangle(mRgba, facesArray[i].tl(),
							facesArray[i].br(), FACE_RECT_RED, 3);
				else if (recognitionResult <= max)
					Core.rectangle(mRgba, facesArray[i].tl(),
							facesArray[i].br(), FACE_RECT_GREEN, 3);
				else
					Core.rectangle(mRgba, facesArray[i].tl(),
							facesArray[i].br(), FACE_RECT_RED, 3);
			} else {
				Core.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(),
						FACE_RECT_RED, 3);
			}

			faces.release();
		}
		return mRgba;
	}
}
