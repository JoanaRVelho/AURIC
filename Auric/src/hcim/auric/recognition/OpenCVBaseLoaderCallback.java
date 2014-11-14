package hcim.auric.recognition;

import hcim.auric.utils.FileManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.objdetect.CascadeClassifier;

import android.content.Context;
import android.util.Log;

import com.hcim.intrusiondetection.R;

public class OpenCVBaseLoaderCallback extends BaseLoaderCallback {
	private static final String TAG = "AURIC";
	private Context c;
	private PersonRecognizer recognizer;
	private CascadeClassifier faceDetector;

	public OpenCVBaseLoaderCallback(Context c) {
		super(c);
		this.c = c;
	}

	public PersonRecognizer getPersonRecognizer() {
		return recognizer;
	}

	public CascadeClassifier getCascadeClassifier() {
		return faceDetector;
	}

	@Override
	public void onManagerConnected(int status) {
		switch (status) {
		case LoaderCallbackInterface.SUCCESS: {
			Log.d(TAG, "Face Recognition - OpenCV loaded successfully");

			FileManager fileManager = new FileManager(c);
			String path = fileManager.getOpenCVDirectory();

			recognizer = new PersonRecognizer(path);
			recognizer.load();

			try {
				InputStream is = c.getResources().openRawResource(
						R.raw.lbpcascade_frontalface);
				File cascadeDir = c.getDir("cascade", Context.MODE_PRIVATE);
				File cascadeFile = new File(cascadeDir, "lbpcascade.xml");
				FileOutputStream os = new FileOutputStream(cascadeFile);

				byte[] buffer = new byte[4096];
				int bytesRead;
				while ((bytesRead = is.read(buffer)) != -1) {
					os.write(buffer, 0, bytesRead);
				}
				is.close();
				os.close();

				faceDetector = new CascadeClassifier(
						cascadeFile.getAbsolutePath());
				if (faceDetector.empty()) {
					Log.e(TAG,
							"Face Recognition - Failed to load cascade classifier");
					faceDetector = null;
				} else
					Log.d(TAG,
							"Face Recognition - Loaded cascade classifier from "
									+ cascadeFile.getAbsolutePath());

				cascadeDir.delete();

			} catch (IOException e) {
				Log.e(TAG,
						"Face Recognition - Failed to load cascade. Exception thrown: "
								+ e);
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
