package hcim.auric.activities.settings;

import hcim.auric.activities.images.SlideShowRecognizedPictures;
import hcim.auric.database.ConfigurationDatabase;
import hcim.auric.database.PicturesDatabase;
import hcim.auric.recognition.FaceRecognition;
import hcim.auric.record.log_type.LogManager;
import hcim.auric.service.BackgroundService;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.hcim.intrusiondetection.R;

public class SettingsActivity extends FragmentActivity implements
		ActionBar.TabListener {
	private static final String TAG = "AURIC";

	private ActionBar actionbar;
	private ViewPager viewpager;
	private FragmentPageAdapter adapter;

	protected ConfigurationDatabase configDB;
	protected PicturesDatabase picsDB;
	protected FaceRecognition faceRecognition;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_activity);
		
		configDB = ConfigurationDatabase.getInstance(this);
		picsDB = PicturesDatabase.getInstance(this);
		faceRecognition = FaceRecognition.getInstance(this);

		viewpager = (ViewPager) findViewById(R.id.pager);
		adapter = new FragmentPageAdapter(getSupportFragmentManager());

		actionbar = getActionBar();
		viewpager.setAdapter(adapter);
		if (actionbar != null)
			actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		for (int i = 0; i < adapter.getCount(); i++) {
			actionbar.addTab(actionbar.newTab()
					.setText(adapter.getDescription(i)).setTabListener(this));
		}

		viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				actionbar.setSelectedNavigationItem(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		stopBackgroundService();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		viewpager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void finish() {
		printStatus();
		
		if (readyToStart()) {
			startBackgroundService();
			super.finish();
		} else {
			if (!configDB.isIntrusionDetectorActive()) {
				super.finish();
			} else {
				if (!settingsChecked()) {
					settingsDialog();
				}
			}
		}
	}

	private void settingsDialog() {
		AlertDialog.Builder alertDialog;
		alertDialog = new AlertDialog.Builder(SettingsActivity.this);
		alertDialog.setTitle("Auric Service");
		alertDialog.setMessage("It is necessary to activate Auric's Accessibility Service. ");
		alertDialog.setNeutralButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						startActivity(new Intent(
								Settings.ACTION_ACCESSIBILITY_SETTINGS));
					}
				});

		alertDialog.show();
	}

	/**
	 * 
	 * @return false if accessibility service is needed and not enabled, true
	 *         otherwise
	 */
	private boolean settingsChecked() {
		String type = configDB.getLogType();

		if (LogManager.hasAccessibilityService(type)) {
			return LogManager.accessibilityServiceEnabled(this, type);
		}

		return true;
	}

	private boolean readyToStart() {
		return (configDB.isIntrusionDetectorActive() && settingsChecked());
	}

	private void printStatus() {
		Log.i(TAG,
				"SettingsActivity - STATUS: "
						+ (configDB.isIntrusionDetectorActive() ? "ON" : "OFF"));
		Log.i(TAG, "SettingsActivity - MODE: " + configDB.getMode());
		Log.i(TAG, "SettingsActivity - DV:" + configDB.isDeviceSharingEnabled());
		Log.i(TAG, "SettingsActivity - LOG TYPE: " + configDB.getLogType());
		Log.i(TAG, "SettingsActivity - FACE RECOGNITION MAX PARAM: "
				+ configDB.getFaceRecognitionMax());
		Log.i(TAG, "SettingsActivity - CAMERA PERIOD PARAM: "
				+ configDB.getCameraPeriod());
	}

	private void stopBackgroundService() {
		if (configDB.isIntrusionDetectorActive())
			stopService(new Intent(this, BackgroundService.class));
	}

	private void startBackgroundService() {
		if (configDB.isIntrusionDetectorActive())
			startService(new Intent(this, BackgroundService.class));
	}

	public void goToRecognizedPictures() {
		Intent i = new Intent(SettingsActivity.this,
				SlideShowRecognizedPictures.class);
		startActivity(i);
	}

}
