package hcim.auric.activities.setup;

import hcim.auric.database.PicturesDatabase;
import hcim.auric.recognition.FaceRecognition;
import hcim.auric.recognition.Picture;

import java.util.ArrayList;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.objdetect.CascadeClassifier;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hcim.intrusiondetection.R;

public class SetUp extends Activity implements CvCameraViewListener2 {

	private static final String TAG = "AURIC";

	private static final Scalar FACE_RECT_BLUE = new Scalar(0, 0, 255, 255);
	private static final int FRONT_CAMERA = 1;
	private static final int BACK_CAMERA = 2;
	private static final int NUMBER_PICTURES = 3;
	public static final String NAME = "OWNER";
	public static final String MISSING = " missing pictures";
	private static final int CODE = 0;

	private Mat matRgba;
	private Mat matGray;
	private Mat lastMat;
	private CascadeClassifier cascadeClassifier;

	private float relativeFaceSize = 0.2f;
	private int absoluteFaceSize = 0;

	private CameraView cameraView;
	private FaceRecognition faceRecognition;
	private int currentCamera;

	private Button takePicture, switchCam;
	private ProgressBar bar;
	private TextView msg;

	private ArrayList<Picture> list;

	private Thread run = new Thread() {

		@Override
		public void run() {
			lastMat.release();
			trainAllPictures();
			cameraView.disableView();
			cameraView.release();
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					showMessageDialog();
				}
			});
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.train_surface_view);

		cameraView = (CameraView) findViewById(R.id.surface_view);
		cameraView.setCvCameraViewListener(this);

		faceRecognition = FaceRecognition.getInstance(this);
		cascadeClassifier = faceRecognition.getCascadeClassifier();
		currentCamera = BACK_CAMERA;

		list = new ArrayList<Picture>();

		takePicture = (Button) findViewById(R.id.takepicture);
		takePicture.setVisibility(View.INVISIBLE);
		Log.d("AURIC", "INVISIBLE");
		switchCam = (Button) findViewById(R.id.switch_cam);
		bar = (ProgressBar) findViewById(R.id.progressBar1);
		bar.setVisibility(View.INVISIBLE);
		msg = (TextView) findViewById(R.id.missing);
		msg.setText(NUMBER_PICTURES + MISSING);
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

		cameraView.enableView();
	}

	public void onDestroy() {
		super.onDestroy();
		cameraView.disableView();
	}

	public void onCameraViewStarted(int width, int height) {
		matGray = new Mat();
		matRgba = new Mat();
	}

	public void onCameraViewStopped() {
		matGray.release();
		matRgba.release();
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

	public void takePicture(View v) {
		if (lastMat != null) {
			Bitmap img = Bitmap.createBitmap(lastMat.width(), lastMat.height(),
					Bitmap.Config.ARGB_8888);
			Utils.matToBitmap(lastMat, img);

			String name = NAME + list.size();
			String type = FaceRecognition.MY_PICTURE_TYPE;

			if (faceRecognition.detectFace(img)) {
				list.add(new Picture(name, type, img));
			}

			if (list.size() == NUMBER_PICTURES) {
				run.start();

				msg.setVisibility(View.INVISIBLE);
				bar.setVisibility(View.VISIBLE);
				takePicture.setVisibility(View.INVISIBLE);
				Log.d("AURIC", "INVISIBLE");
				switchCam.setVisibility(View.INVISIBLE);
			} else {
				int n = NUMBER_PICTURES - list.size();
				msg.setText(n + MISSING);
			}
		}
	}

	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		if (list.size() == NUMBER_PICTURES) {
			matRgba = inputFrame.rgba();
			return matRgba;
		} else {
			Log.d("AURIC", "lenght=" + list.size());
			matRgba = inputFrame.rgba();
			matGray = inputFrame.gray();

			if (absoluteFaceSize == 0) {
				int height = matGray.rows();
				if (Math.round(height * relativeFaceSize) > 0) {
					absoluteFaceSize = Math.round(height * relativeFaceSize);
				}
			}

			MatOfRect faces = new MatOfRect();

			if (cascadeClassifier != null)
				cascadeClassifier.detectMultiScale(matGray, faces, 1.1, 2, 2,
						new Size(absoluteFaceSize, absoluteFaceSize),
						new Size());

			Rect[] facesArray = faces.toArray();

			if (facesArray.length > 0) {
				if (lastMat != null)
					lastMat.release();

				lastMat = new Mat();
				Rect r = facesArray[0];

				lastMat = matRgba.submat(r);
			}

			final int length = facesArray.length;
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (length > 0) {
						takePicture.setVisibility(View.VISIBLE);
						Log.d("AURIC", "VISIBLE");
					} else {
						takePicture.setVisibility(View.INVISIBLE);
						Log.d("AURIC", "INVISIBLE");
					}

				}
			});

			for (int i = 0; i < facesArray.length; i++)
				Core.rectangle(matRgba, facesArray[i].tl(), facesArray[i].br(),
						FACE_RECT_BLUE, 3);

			faces.release();
			matGray.release();

			return matRgba;
		}
	}

	private void showMessageDialog() {
		AlertDialog.Builder alertDialog;
		alertDialog = new AlertDialog.Builder(SetUp.this);
		alertDialog.setTitle("Picture Configuration");
		alertDialog.setMessage("Picture configuration is complete.");
		alertDialog.setNeutralButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
		alertDialog.show();
	}

	private void trainAllPictures() {
		PicturesDatabase db = PicturesDatabase.getInstance(this);

		for (Picture p : list) {
			if (faceRecognition.trainPicture(p.getImage(), p.getID())) {
				db.addPicture(p);
				Log.d(TAG, "Picture " + p.getID() + " added");
			}
		}
		addOtherPictures();
		faceRecognition.stopTrain();
		configDone();
	}

	private void addOtherPictures() {
		FaceRecognition recognition = FaceRecognition.getInstance(this);
		String name;
		Bitmap bitmap;

		name = "pic_a";
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.g);
		recognition.trainPicture(bitmap, name);

		name = "pic_b";
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.p);
		recognition.trainPicture(bitmap, name);

		name = "pic_c";
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.m);
		recognition.trainPicture(bitmap, name);
	}

	private void configDone() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		SharedPreferences.Editor edit = prefs.edit();
		edit.putBoolean(getString(R.string.pref_previously_started),
				Boolean.TRUE);
		edit.commit();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CODE)
			finish();
	}
}
