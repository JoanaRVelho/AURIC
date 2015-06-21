package hcim.auric.camera;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;

import com.hcim.intrusiondetection.R;

public class RecognitionActivity extends Activity {
	private PortraitCameraPreview cameraPreview;
	private ViewGroup rootView;
	private RecognitionPreviewCallback previewCallback;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.portrait_camera);

		previewCallback = new RecognitionPreviewCallback(this);
		cameraPreview = new PortraitCameraPreview(this, previewCallback);

		rootView = (ViewGroup) findViewById(R.id.root_layout);
		rootView.addView(cameraPreview);
		rootView.addView(previewCallback);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void finish() {
		rootView.removeView(previewCallback);
		rootView.removeView(cameraPreview);
		super.finish();
	}

	// public void setRecognitionResult(final RecognitionResult
	// recognitionResult,
	// final Bitmap bm) {
	//
	// runOnUiThread(new Runnable() {
	//
	// @Override
	// public void run() {
	// recognitionResultTxt.setText(recognitionResult.description());
	//
	// if (bm != null) {
	// pictureTaken.setImageBitmap(bm);
	// }else{
	//
	// }
	// }
	// });
	//
	// }
}