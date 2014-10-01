package hcim.auric.activities;

import hcim.auric.activities.images.RecognizedPicturesGrid;
import hcim.auric.activities.passcode.ConfirmAndChangePasscode;
import hcim.auric.activities.passcode.ConfirmAndTurnOffPasscode;
import hcim.auric.activities.passcode.InsertPasscode;
import hcim.auric.authentication.BackgroundService;
import hcim.auric.database.ConfigurationDatabase;
import hcim.auric.database.PicturesDatabase;
import hcim.auric.recognition.FaceRecognition;
import hcim.auric.recognition.Picture;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.hcim.intrusiondetection.R;

public class ConfigurationsActivity extends Activity {
	private static final int REQUEST_IMAGE_CAPTURE = 1;
	private static final String TAG = "AURIC";

	private Context context;
	private ConfigurationDatabase configDB;
	private PicturesDatabase picturesDB;

	private Spinner modeSpinner;
	private String currentMode;
	private CheckBox deviceSharing;
	private String selectedMode;

	private Button changePicture;
	private Button allPictures;
	private ImageView img;
	private Bitmap myPicture;

	private Button changePasscode;
	private Switch passcodeSwitch;

	private Spinner cameraCapturesOptions;
	private String currentCameraCapture;

	private Spinner screenshotOptions;
	private String currentScreenshot;

	private boolean startService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configurations_view);

		context = getApplicationContext();
		configDB = ConfigurationDatabase.getInstance(context);
		picturesDB = PicturesDatabase.getInstance(context);
		
		startService = false;

		initView();
	}

	@Override
	protected void onResume() {
		passcodeSwitch.setChecked(configDB.hasPasscode());

		super.onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
			Bitmap imageBitmap = (Bitmap) extras.get("data");
			String name = FaceRecognition.MY_PICTURE_TYPE;

			FaceRecognition recognition = FaceRecognition.getInstance(context);
			boolean result = recognition.trainPicture(imageBitmap, name);

			if (result) {
				if (myPicture == null) { // first config
					Log.d(TAG, "Configurations View - add all pictures");
					addPictures();
				}
				myPicture = imageBitmap;
				img.setImageBitmap(imageBitmap);

				// store
				picturesDB.setMyPicture(myPicture);
				recognition.stopTrain();

			} else {
				Toast.makeText(getApplicationContext(),
						"Face Detection Failed! Take another picture!",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void finish() {
		if (startService)
			startBackgroundService();

		notifyMode();
		super.finish();
	}

	private void initView() {
		initModeSection();
		initPictureSection();
		initPasscodeSection();
		initCameraCaptureOptions();
		initScreenshotsOptions();
	}

	private void initModeSection() {
		// init spinner
		modeSpinner = (Spinner) findViewById(R.id.mode_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.mode_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		modeSpinner.setAdapter(adapter);
		selectCurrentMode();
		modeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				stopBackgroundService();
				startService = true;
				changeMode();
				enableCheckBox();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
			}

		});

		deviceSharing = (CheckBox) findViewById(R.id.checkBox1);
		enableCheckBox();
	}

	private void initPictureSection() {
		// get data from database
		Picture p = picturesDB.getMyPicture();
		currentMode = configDB.getMode();
		myPicture = p == null ? null : p.getImage();

		// init image view
		img = (ImageView) findViewById(R.id.mypicture);
		if (myPicture != null) {
			img.setImageBitmap(myPicture);
		}

		// init change picture button
		changePicture = (Button) findViewById(R.id.change_picture_button);
		changePicture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dispatchTakePictureIntent();
			}
		});
		
		allPictures = (Button) findViewById(R.id.all_imgs_button);
		allPictures.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(ConfigurationsActivity.this, RecognizedPicturesGrid.class);
				startActivity(i);
			}
		});
		
		if(picturesDB.numberOfIntrusions() > 1 ){
			allPictures.setEnabled(true);
			allPictures.setVisibility(View.VISIBLE);
		}
	}

	private void initPasscodeSection() {
		// passcode
		passcodeSwitch = (Switch) findViewById(R.id.passcode_switch);
		passcodeSwitch.setChecked(configDB.hasPasscode());
		passcodeSwitch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (passcodeSwitch.isChecked()) {
					Intent i = new Intent(ConfigurationsActivity.this,
							InsertPasscode.class);
					startActivity(i);
				} else {
					Log.d("AURIC", "change");
					Intent i = new Intent(ConfigurationsActivity.this,
							ConfirmAndTurnOffPasscode.class);
					startActivity(i);
				}
			}
		});

		changePasscode = (Button) findViewById(R.id.change_passcode);
		changePasscode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(passcodeSwitch.isChecked()){
					Intent i = new Intent(ConfigurationsActivity.this,
							ConfirmAndChangePasscode.class);
					startActivity(i);
				}
			}
		});
	}

	private void initCameraCaptureOptions() {
		cameraCapturesOptions = (Spinner) findViewById(R.id.camera_capture_opt);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.camera_capture_array,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		cameraCapturesOptions.setAdapter(adapter);
		cameraCapturesOptions
		.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				startService = true;
				stopBackgroundService();
				changeCameraCaptureOptions();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
			}

		});

		selectCurrentCameraOptions();
	}

	private void initScreenshotsOptions() {
		screenshotOptions = (Spinner) findViewById(R.id.screenshot_opt);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.screenshot_array,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		screenshotOptions.setAdapter(adapter);
		screenshotOptions
		.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				startService = true;
				stopBackgroundService();
				changeScreenshotsOptions();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
			}

		});

		selectCurrentScreenshotsOptions();
	}

	private void selectCurrentMode() {
		currentMode = configDB.getMode();

		Log.d(TAG, "Configurations View - mode=" + currentMode);

		if (currentMode.equals(ConfigurationDatabase.NONE)) {
			modeSpinner.setSelection(0);
		}
		if (currentMode.equals(ConfigurationDatabase.WIFI_MODE)) {
			modeSpinner.setSelection(1);
		}
		if (currentMode.equals(ConfigurationDatabase.ORIGINAL_MODE)) {
			modeSpinner.setSelection(2);
		}
	}

	private void selectCurrentCameraOptions() {
		currentCameraCapture = configDB.getCameraCaptureOption() + " miliseconds";
		
		Log.d("AURIC","camera = "+ currentCameraCapture);
		Resources res = getResources();
		String[] options = res.getStringArray(R.array.camera_capture_array);
		for (int i = 0; i < options.length; i++) {
			if (options[i].equals(currentCameraCapture)) {
				cameraCapturesOptions.setSelection(i);
			}
		}
	}

	private void selectCurrentScreenshotsOptions() {
		currentScreenshot = configDB.getScreenshotOptions() + " miliseconds";
		Log.d("AURIC","screen = "+ currentScreenshot);
		Resources res = getResources();
		String[] options = res.getStringArray(R.array.screenshot_array);
		for (int i = 0; i < options.length; i++) {
			if (options[i].equals(currentScreenshot)) {
				screenshotOptions.setSelection(i);
				
			}
		}
	}

	private void changeMode() {
		selectedMode = (String) modeSpinner.getSelectedItem();

		if (currentMode.equals(selectedMode))
			return;

		configDB.setMode(selectedMode);
		configDB.enableDeviceSharing(deviceSharing.isChecked());

		currentMode = selectedMode;
		
		if (currentMode.equals(ConfigurationDatabase.NONE))
			startService = false;
	}

	private void changeCameraCaptureOptions() {
		String selected = (String) cameraCapturesOptions.getSelectedItem();
		String[] split = selected.split(" ");
		int newCameraCaptureOpt = Integer.parseInt(split[0]);

		if (currentCameraCapture.equals(selected)) {
			return;
		}
		
		configDB.setCameraCaptureOption(newCameraCaptureOpt);
		currentCameraCapture = selected;
	}

	private void changeScreenshotsOptions() {
		String selected = (String) screenshotOptions.getSelectedItem();
		String[] split = selected.split(" ");
		int newScreenshotOpt = Integer.parseInt(split[0]);

		if (currentScreenshot.equals(screenshotOptions)) {
			return;
		}
		
		configDB.setScreenshotOptions(newScreenshotOpt);
		currentScreenshot = selected;
	}

	private void addPictures() {
		FaceRecognition recognition = FaceRecognition.getInstance(context);
		Context context = getApplicationContext();
		String name;
		Bitmap grayBitmap;
		boolean result;

		name = "pic_a";
		grayBitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.g);
		result = recognition.trainPicture(grayBitmap, name);
		Log.d(TAG, "Configurations View - train: " + name + result);

		name = "pic_b";
		grayBitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.p);
		result = recognition.trainPicture(grayBitmap, name);
		Log.d(TAG, "Configurations View - train: " + name + result);

		name = "pic_c";
		grayBitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.m);
		result = recognition.trainPicture(grayBitmap, name);
		Log.d(TAG, "Configurations View - train: " + name + result);
	}

	private void notifyMode() {
		String msg = null;

		if (currentMode.equals(ConfigurationDatabase.NONE)) {
			msg = "Mode: none";
		}
		if (currentMode.equals(ConfigurationDatabase.WIFI_MODE)) {
			msg = "Mode: Laboratory Test Mode";
		}
		if (currentMode.equals(ConfigurationDatabase.ORIGINAL_MODE)) {
			if (deviceSharing.isChecked())
				msg = "Mode: Original Mode with Device Sharing";
			else
				msg = "Mode: Original Mode without Device Sharing";
		}

		if (msg != null) {
			Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG)
			.show();
		}
	}

	private void dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
		}
	}

	private void enableCheckBox() {
		String selectedMode = (String) modeSpinner.getSelectedItem();

		if (selectedMode != null
				&& selectedMode.equals(ConfigurationDatabase.ORIGINAL_MODE)) {
			deviceSharing.setEnabled(true);
		} else {
			deviceSharing.setChecked(false);
			deviceSharing.setEnabled(false);
		}
	}

	private void stopBackgroundService() {
		if (!currentMode.equals(ConfigurationDatabase.NONE))
			stopService(new Intent(ConfigurationsActivity.this,
					BackgroundService.class));
	}

	private void startBackgroundService() {
		if (!currentMode.equals(ConfigurationDatabase.NONE))
			startService(new Intent(ConfigurationsActivity.this,
					BackgroundService.class));
	}
}
