package hcim.auric.activities.face;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.ViewGroup;

import com.hcim.intrusiondetection.R;

/**
 * Activity for face recognition test
 * 
 * @author Joana Velho
 * 
 */
public class RecognitionActivity extends Activity {
	private PortraitCameraPreview cameraPreview;
	private ViewGroup rootView;
	private RecognitionPreviewCallback previewCallback;

	protected BufferedWriter recognitionData;
	protected BufferedWriter detectionTime;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.portrait_camera);

		previewCallback = new RecognitionPreviewCallback(this);
		cameraPreview = new PortraitCameraPreview(this, previewCallback);

		rootView = (ViewGroup) findViewById(R.id.root_layout);
		rootView.addView(cameraPreview);
		rootView.addView(previewCallback);

		try {
			recognitionData = new BufferedWriter(new FileWriter(new File(
					Environment.getExternalStoragePublicDirectory(
							Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()
							+ File.separator + "results.txt")));
			detectionTime = new BufferedWriter(new FileWriter(new File(
					Environment.getExternalStoragePublicDirectory(
							Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()
							+ File.separator + "timeDetection.txt"),true));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void finish() {
		try {
			recognitionData.close();
			detectionTime.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		rootView.removeView(previewCallback);
		rootView.removeView(cameraPreview);
		super.finish();
	}
}