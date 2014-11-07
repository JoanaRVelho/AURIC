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
import hcim.auric.record.screen.LogManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.hcim.intrusiondetection.R;

public class SettingsActivity extends Activity {
	private static final int REQUEST_IMAGE_CAPTURE = 1;
	private static final String TAG = "AURIC";

	private Context context;
	private ConfigurationDatabase configDB;
	private PicturesDatabase picturesDB;

	private Switch onOff;

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

	private Spinner logSpinner;
	private String currentLogType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_view);

		context = getApplicationContext();
		configDB = ConfigurationDatabase.getInstance(context);
		picturesDB = PicturesDatabase.getInstance(context);

		initView();
		setElementsVisibility();

		stopBackgroundService();
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
			String name = PicturesDatabase.MAIN_PICTURE;

			FaceRecognition recognition = FaceRecognition.getInstance(context);
			boolean result = recognition.trainPicture(imageBitmap, name);

			if (result) {
				if (myPicture == null) { // first config
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

	private boolean pictureChecked() {
//		if (configDB.getMode() == ConfigurationDatabase.ORIGINAL_MODE)
//			return picturesDB.getMyPicture() != null;
//		
//		return true;
		return picturesDB.getMyPicture() != null;
	}

	private boolean readyToStart() {
		return (onOff.isChecked() && settingsChecked() && pictureChecked());
	}

	/**
	 * 
	 * @return false if accessibility service is needed and not enabled, true
	 *         otherwise
	 */
	private boolean settingsChecked() {
		String type = configDB.getLogType();

		if (LogManager.hasAccessibilityService(type)) {
			return LogManager.accessibilityServiceEnabled(context, type);
		}

		return true;
	}

	@Override
	public void finish() {
		printStatus();

		if (readyToStart()) {
			startBackgroundService();
			super.finish();
		} else {
			if (!onOff.isChecked()) {
				super.finish();
			} else {
				if (!pictureChecked()) {
					Toast.makeText(this, "Missing Picture!", Toast.LENGTH_LONG)
							.show();
				} else if (!settingsChecked()) {
					startActivity(new Intent(
							Settings.ACTION_ACCESSIBILITY_SETTINGS));
				}
			}
		}
	}

	private void printStatus() {
		Log.i(TAG,
				"SettingsActivity - STATUS: "
						+ (configDB.isIntrusionDetectorActive() ? "ON" : "OFF"));
		Log.i(TAG, "SettingsActivity - MODE: " + configDB.getMode());
		Log.i(TAG, "SettingsActivity - LOG TYPE: " + configDB.getLogType());
		Log.i(TAG,
				"SettingsActivity - HAS PICTURE: "
						+ (picturesDB.getMyPicture() != null));
	}

	private void initView() {
		initOnOffSwitch();
		initModeSection();
		initPictureSection();
		initPasscodeSection();
		initLogOptions();
	}

	private void initOnOffSwitch() {
		onOff = (Switch) findViewById(R.id.on_off);
		onOff.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				configDB.setIntrusionDetectorActivity(onOff.isChecked());
				setElementsVisibility();
			}
		});

		boolean b = configDB.isIntrusionDetectorActive();
		onOff.setChecked(b);
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
				Intent i = new Intent(SettingsActivity.this,
						RecognizedPicturesGrid.class);
				startActivity(i);
			}
		});

		if (picturesDB.numberOfIntrusions() > 1) {
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
					Intent i = new Intent(SettingsActivity.this,
							InsertPasscode.class);
					startActivity(i);
				} else {
					Intent i = new Intent(SettingsActivity.this,
							ConfirmAndTurnOffPasscode.class);
					startActivity(i);
				}
			}
		});

		changePasscode = (Button) findViewById(R.id.change_passcode);
		changePasscode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (passcodeSwitch.isChecked()) {
					Intent i = new Intent(SettingsActivity.this,
							ConfirmAndChangePasscode.class);
					startActivity(i);
				}
			}
		});
	}

	private void initLogOptions() {
		logSpinner = (Spinner) findViewById(R.id.log_options);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.log_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		logSpinner.setAdapter(adapter);
		logSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				changeLogType();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
			}

		});

		selectCurrentLogType();
	}

	private void setElementsVisibility() {
		View scroll = (View) findViewById(R.id.config_scroll);

		if (onOff.isChecked())
			scroll.setVisibility(View.VISIBLE);
		else
			scroll.setVisibility(View.INVISIBLE);
	}

	private void selectCurrentMode() {
		currentMode = configDB.getMode();

		Log.d(TAG, "SettingsActivity - MODE: " + currentMode);

		if (currentMode.equals(ConfigurationDatabase.WIFI_MODE)) {
			modeSpinner.setSelection(1);
		}
		if (currentMode.equals(ConfigurationDatabase.ORIGINAL_MODE)) {
			modeSpinner.setSelection(0);
		}
	}

	private void selectCurrentLogType() {
		currentLogType = configDB.getLogType();
		Log.d(TAG, "SettingsActivity - LOG: " + currentLogType);

		Resources res = getResources();
		String[] options = res.getStringArray(R.array.log_array);

		for (int i = 0; i < options.length; i++) {
			if (options[i].equals(currentLogType)) {
				logSpinner.setSelection(i);
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
	}

	private void changeLogType() {
		String selected = (String) logSpinner.getSelectedItem();

		if (currentLogType.equals(selected)) {
			return;
		}

		configDB.setLogType(selected);
		currentLogType = selected;
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
		if (onOff.isChecked())
			stopService(new Intent(SettingsActivity.this,
					BackgroundService.class));
	}

	private void startBackgroundService() {
		if (onOff.isChecked())
			startService(new Intent(SettingsActivity.this,
					BackgroundService.class));
	}

	private void addPictures() {
		FaceRecognition recognition = FaceRecognition.getInstance(context);
		Context context = getApplicationContext();
		String name;
		Bitmap grayBitmap;

		name = "pic_a";
		grayBitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.g);
		recognition.trainPicture(grayBitmap, name);

		name = "pic_b";
		grayBitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.p);
		recognition.trainPicture(grayBitmap, name);

		name = "pic_c";
		grayBitmap = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.m);
		recognition.trainPicture(grayBitmap, name);
	}
}
