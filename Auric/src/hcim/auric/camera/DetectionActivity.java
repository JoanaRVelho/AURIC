package hcim.auric.camera;

import hcim.auric.database.SettingsPreferences;
import hcim.auric.database.configs.PicturesDatabase;
import hcim.auric.recognition.FaceRecognition;
import hcim.auric.recognition.Picture;
import hcim.auric.utils.LogUtils;
import hcim.auric.utils.StringGenerator;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hcim.intrusiondetection.R;

/**
 * 
 * @author Joana Velho
 * 
 */
public class DetectionActivity extends Activity {
	private static final String FAIL = "Failed. Please try again.";
	private static final int SET_UP_VALUE = 3;

	public static final int CODE_SET_UP = 1;
	public static final int CODE_EDIT = 2;
	public static final String EXTRA_ID = "extra";

	private PortraitCameraPreview cameraPreview;
	private ViewGroup rootView;
	private ImageView captureButton;
	private DetectionPreviewCallback preview;
	private List<Picture> list;
	private PicturesDatabase picsDB;
	private FaceRecognition fr;
	private ImageView pictureTaken;
	private ProgressBar bar;
	private TextView msg;

	private int mode;

	// private boolean finish;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.portrait_camera);

		Bundle extras = getIntent().getExtras();
		mode = extras.getInt(EXTRA_ID);

		fr = FaceRecognition.getInstance(this);

		preview = new DetectionPreviewCallback(this, fr);
		cameraPreview = new PortraitCameraPreview(this, preview);
		rootView = (ViewGroup) findViewById(R.id.root_layout);
		rootView.addView(cameraPreview);
		rootView.addView(preview);

		bar = (ProgressBar) findViewById(R.id.progressBar1);
		pictureTaken = (ImageView) findViewById(R.id.mat_gray);
		pictureTaken.setVisibility(View.VISIBLE);
		msg = (TextView) findViewById(R.id.recog_result);
		msg.setVisibility(View.VISIBLE);
		captureButton = (ImageView) findViewById(R.id.button1);
		captureButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Picture p = preview.takePicture(getName());

				if (p == null) {
					Toast.makeText(DetectionActivity.this, FAIL,
							Toast.LENGTH_SHORT).show();
					return;
				} else {
					LogUtils.debug(p.getID());
					list.add(p);
					pictureTaken.setImageBitmap(p.getImage());
					updateMessage();

					if (mode == CODE_SET_UP && list.size() == SET_UP_VALUE) {
						configDone();
						checkConfigDone();
					}
				}
			}

			private String getName() {
				if (mode == CODE_EDIT)
					return StringGenerator.generateOwnerName();
				return StringGenerator.generateOwnerName(list.size());
			}
		});

		list = new ArrayList<Picture>();
		picsDB = PicturesDatabase.getInstance(this);
		// finish = false;
		updateMessage();
	}

	@Override
	public void finish() {// user pressed back button
		// if (finish)
		// return;
		// if (mode == CODE_EDIT) {
		// configDone();
		// finish = true;
		// } else {
		// // superFinish();
		// }
		configDone();
	//	rootView.removeView(preview);
	//	rootView.removeView(cameraPreview);
		super.finish();
	}

	private void updateMessage() {
		if (mode == CODE_EDIT) {
			msg.setText("Pictures Taken: " + list.size());
		} else {
			int n = SET_UP_VALUE - list.size();
			msg.setText(n + " missing picture");
			if (n > 1) {
				msg.append("s");
			}
		}
	}

	private void configDone() {
		rootView.removeAllViews();
		pictureTaken.setVisibility(View.INVISIBLE);
		captureButton.setVisibility(View.INVISIBLE);
		bar.setVisibility(View.VISIBLE);
		msg.setVisibility(View.INVISIBLE);

		new Thread() {

			@Override
			public void run() {
				if (list.isEmpty()) {
					DetectionActivity.super.finish();
				} else {
					fr.stopTrain();
					addPictures();
					// showMessageDialog();
					DetectionActivity.super.finish();
				}
			}
		}.start();
	}

	// private void showMessageDialog() {
	// runOnUiThread(new Runnable() {
	//
	// @Override
	// public void run() {
	//
	// if (mode == CODE_SET_UP) {
	// AlertDialog.Builder alertDialog;
	// alertDialog = new AlertDialog.Builder(
	// DetectionActivity.this);
	// alertDialog.setTitle("Picture Configuration");
	// alertDialog
	// .setMessage("Picture configuration is complete.");
	// alertDialog.setNeutralButton("OK",
	// new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog,
	// int which) {
	// superFinish();
	// }
	// });
	// alertDialog.show();
	// } else {
	// AlertDialog.Builder alertDialog;
	// alertDialog = new AlertDialog.Builder(
	// DetectionActivity.this);
	// alertDialog.setTitle("Picture Configuration");
	// alertDialog
	// .setMessage(list.size() + " new pictures added.");
	// alertDialog.setNeutralButton("OK",
	// new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog,
	// int which) {
	// finish();
	// }
	// });
	// alertDialog.show();
	// }
	// }
	// });
	// }

	private void checkConfigDone() {
		SettingsPreferences settings = new SettingsPreferences(this);
		settings.setPreviouslyStarted();
	}

	private void addPictures() {
		for (Picture p : list) {
			picsDB.addPicture(p);
			LogUtils.debug("add pic............................" + p.getID());
		}
		picsDB.printList();
	}

	public void setButtonVisibility(int i) {
		captureButton.setVisibility(i);
	}

	public void setPreviewImg(final Bitmap bm) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				pictureTaken.setImageBitmap(bm);
			}
		});
	}
}