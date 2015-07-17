package hcim.auric.activities.face;

import hcim.auric.data.SettingsPreferences;
import hcim.auric.recognition.FaceDatabase;
import hcim.auric.utils.LogUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.hcim.intrusiondetection.R;

/**
 * Android Activity that process face detection and training
 * 
 * @author Joana Velho
 * 
 */
public class DetectionActivity extends Activity {
	private PortraitCameraPreview cameraPreview;
	private ViewGroup rootView;
	private DetectionPreviewCallback preview;
	protected ProgressBar bar;
	private boolean done;
	private long time;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.portrait_camera);
		done = false;

		preview = new DetectionPreviewCallback(this);
		cameraPreview = new PortraitCameraPreview(this, preview);
		rootView = (ViewGroup) findViewById(R.id.root_layout);
		rootView.addView(cameraPreview);
		rootView.addView(preview);

		bar = (ProgressBar) findViewById(R.id.progressBar1);
		bar.setVisibility(View.VISIBLE);
		bar.setProgress(0);
		time = System.currentTimeMillis();
	}

	@Override
	public void finish() {// user pressed back button
		if (done)
			super.finish();
	}

	/**
	 * Called when set up is done
	 */
	protected void setUpDone() {
		time = System.currentTimeMillis() - time;
		LogUtils.debug("training your pics = " + time);

		SettingsPreferences settings = new SettingsPreferences(this);
		if (!settings.hasPreviouslyStarted()) {
			long t1 = System.currentTimeMillis();
			FaceDatabase.build(this);
			long t2 = System.currentTimeMillis();
			LogUtils.debug("default pics time = " + (t2 - t1) + " ms");
			settings.setPreviouslyStarted();
		}

		rootView.removeAllViews();
		showMessageDialog();
		done = true;
	}

	/**
	 * shows an alert dialog informing that set up is done
	 */
	private void showMessageDialog() {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				AlertDialog.Builder alertDialog;
				alertDialog = new AlertDialog.Builder(DetectionActivity.this);
				alertDialog.setTitle("Picture Set Up");
				alertDialog.setMessage("Picture set up is complete.");
				alertDialog.setNeutralButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								DetectionActivity.super.finish();
							}
						});
				alertDialog.show();
			}
		});
	}
}