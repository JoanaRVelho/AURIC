package hcim.auric.general_activities.settings;

import hcim.auric.accessibility.AuricEvents;
import hcim.auric.database.SettingsPreferences;
import hcim.auric.database.configs.PicturesDatabase;
import hcim.auric.database.intrusions.SessionDatabase;
import hcim.auric.recognition.FaceRecognition;
import hcim.auric.service.AccessibilityNotification;
import hcim.auric.service.BackgroundService;
import hcim.auric.utils.LogUtils;

import java.util.Timer;
import java.util.TimerTask;

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

import com.hcim.intrusiondetection.R;

public class SettingsActivity extends FragmentActivity implements
		ActionBar.TabListener {
	private static final int WARNING = 30000;
	private ActionBar actionbar;
	private ViewPager viewpager;
	private FragmentPageAdapter adapter;

	protected SettingsPreferences settings;
	protected PicturesDatabase picsDB;
	protected FaceRecognition faceRecognition;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_activity);

		settings = new SettingsPreferences(this);
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

		if (settings.isIntrusionDetectorActive()) {
			if (!accessibilityServiceEnabled())
				settingsDialog();
			else
				super.finish();
		} else {
			super.finish();
		}
	}

	private void settingsDialog() {
		AlertDialog.Builder alertDialog;
		alertDialog = new AlertDialog.Builder(SettingsActivity.this);
		alertDialog.setTitle("Auric Service");
		alertDialog
				.setMessage("It is necessary to activate Auric's Accessibility Service. ");
		alertDialog.setNeutralButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						SettingsActivity.super.finish();
						startActivity(new Intent(
								Settings.ACTION_ACCESSIBILITY_SETTINGS));
						TimerTask not = new TimerTask() {

							@Override
							public void run() {
								if (launchNotification()) {
									AccessibilityNotification notification = new AccessibilityNotification(
											SettingsActivity.this);
									notification.notifyUser();
								}
							}

							private boolean launchNotification() {
								String recorder = settings.getRecorderType();

								return AuricEvents
										.hasAccessibilityService(recorder)
										&& !AuricEvents
												.accessibilityServiceEnabled(SettingsActivity.this);
							}
						};
						Timer timer = new Timer();
						timer.schedule(not, WARNING);
					}
				});

		alertDialog.show();
	}

	/**
	 * 
	 * @return false if accessibility service is needed and not enabled, true
	 *         otherwise
	 */
	private boolean accessibilityServiceEnabled() {
		String recorder = settings.getRecorderType();

		if (AuricEvents.hasAccessibilityService(recorder)) {
			return AuricEvents.accessibilityServiceEnabled(this);
		}

		return true;
	}

	private void printStatus() {
		LogUtils.info("SettingsActivity - STATUS: "
				+ (settings.isIntrusionDetectorActive() ? "ON" : "OFF"));
		LogUtils.info("SettingsActivity - MODE: " + settings.getDetectorType());
		LogUtils.info("SettingsActivity - Strategy: "
				+ settings.getStrategyType());
		LogUtils.info("SettingsActivity - LOG TYPE: "
				+ settings.getRecorderType());
		LogUtils.info("SettingsActivity - FACE RECOGNITION MAX PARAM: "
				+ settings.getFaceRecognitionMax());
		LogUtils.info("SettingsActivity - CAMERA PERIOD PARAM: "
				+ settings.getCameraPeriod());
		LogUtils.info("SettingsActivity - Number Pictures: "
				+ settings.getNumberOfPicturesPerDetection());
	}

	protected void stopBackgroundService() {
		stopService(new Intent(this, BackgroundService.class));

		// keep DB clean when service is stopped
		SessionDatabase sessionDB = SessionDatabase.getInstance(this);
		sessionDB.clean();
	}

	protected void startBackgroundService() {
		startService(new Intent(this, BackgroundService.class));
	}
}
