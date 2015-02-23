package hcim.auric.activities.settings;

import hcim.auric.activities.apps.ListAppsActivity;
import hcim.auric.activities.passcode.ConfirmAndChangePasscode;
import hcim.auric.activities.passcode.ConfirmAndTurnOffPasscode;
import hcim.auric.activities.passcode.InsertPasscode;
import hcim.auric.detector.DetectorManager;
import hcim.auric.record.RecorderManager;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.hcim.intrusiondetection.R;

public class GeneralFragment extends Fragment {
	private SettingsActivity activity;

	private Switch onOff;

	private TextView modeTitle;
	private Spinner modeSpinner;
	private String currentDetectorType;
	// private CheckBox deviceSharing;
	private String selectedDetectorType;

	private Button changePasscode;
	private Switch passcodeSwitch;

	private TextView recorderTitle;
	private Spinner recorderSpinner;
	private String currentRecorderType;
	
	private CheckBox hide;

	// private TextView periodTitle;
	// private TextView periodDesc;
	// private NumberPicker picker;

	// private CheckBox recordCheckBox

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		activity = (SettingsActivity) getActivity();

		RelativeLayout result = (RelativeLayout) inflater.inflate(
				R.layout.fragment_general, container, false);
		initView(result);

		return result;
	}

	@Override
	public void onResume() {
		if (passcodeSwitch != null) {
			boolean b = activity.configDB.hasPasscode();
			passcodeSwitch.setChecked(b);

			if (b)
				changePasscode.setVisibility(View.VISIBLE);
			else {
				changePasscode.setVisibility(View.GONE);
			}
		}
		super.onResume();
	}

	private void initView(RelativeLayout result) {
		initOnOffSwitch(result);
		initModeSection(result);
		initPasscodeSection(result);
		initRecorderOptions(result);
		// initNumberPicker(result);
		initHideNotification(result);

		if (onOff.isChecked()) {
			setVisibility(View.GONE);
		} else {
			setVisibility(View.VISIBLE);
		}
	}

	// private void initNumberPicker(RelativeLayout result) {
	// picker = (NumberPicker) result.findViewById(R.id.rate_number_picker);
	// picker.setMinValue(0);
	// picker.setMaxValue(60);
	// int value = activity.configDB.getCameraPeriod() / 1000;
	// picker.setValue(value);
	// picker.setWrapSelectorWheel(false);
	// picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
	// picker.setOnValueChangedListener(new OnValueChangeListener() {
	//
	// @Override
	// public void onValueChange(NumberPicker picker, int oldVal,
	// int newVal) {
	// activity.configDB.setCameraPeriod(newVal * 1000);
	// }
	// });
	// periodTitle = (TextView) result.findViewById(R.id.rate_title);
	// periodDesc = (TextView) result.findViewById(R.id.rate_desc);
	// }

	private void initHideNotification(RelativeLayout result) {
		hide = (CheckBox) result.findViewById(R.id.hide_not);
		hide.setChecked(activity.configDB.hideNotification());
		hide.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					activity.configDB.setHideNotification(isChecked);
				}
			}
		});
	}

	private void initOnOffSwitch(RelativeLayout result) {
		onOff = (Switch) result.findViewById(R.id.on_off);
		boolean on = activity.configDB.isIntrusionDetectorActive();
		onOff.setChecked(on);

		onOff.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				activity.configDB.setIntrusionDetectorActivity(isChecked);

				if (isChecked)
					showConfirmDialog();
				else {
					activity.stopBackgroundService();
					setVisibility(View.VISIBLE);
				}
			}

		});
	}

	private void initModeSection(RelativeLayout result) {
		// init spinner
		modeSpinner = (Spinner) result.findViewById(R.id.mode_spinner);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity,
				android.R.layout.simple_spinner_dropdown_item,
				DetectorManager.getTypesOfDetectors());
		// ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
		// activity, R.array.mode_array,
		// android.R.layout.simple_spinner_item);
		// adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		modeSpinner.setAdapter(adapter);
		selectCurrentDetector();

		modeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				changeDetectorType();

				if (activity.configDB.getDetectorType().equals(
						DetectorManager.APPS)) {
					startActivity(new Intent(activity, ListAppsActivity.class));
				}
				// enableCheckBox();
			};

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
			}

		});

		// deviceSharing = (CheckBox) result.findViewById(R.id.checkBox1);
		// enableCheckBox();

		modeTitle = (TextView) result.findViewById(R.id.mode_title);
	}

	private void initPasscodeSection(RelativeLayout result) {
		// passcode
		passcodeSwitch = (Switch) result.findViewById(R.id.switch_passcode);
		passcodeSwitch.setChecked(activity.configDB.hasPasscode());
		passcodeSwitch.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (passcodeSwitch.isChecked()) {
					Intent i = new Intent(activity, InsertPasscode.class);
					startActivity(i);
				} else {
					Intent i = new Intent(activity,
							ConfirmAndTurnOffPasscode.class);
					startActivity(i);
				}
			}
		});

		changePasscode = (Button) result.findViewById(R.id.change_passcode);
		changePasscode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (passcodeSwitch.isChecked()) {
					Intent i = new Intent(activity,
							ConfirmAndChangePasscode.class);
					startActivity(i);
				}
			}
		});
	}

	private void initRecorderOptions(RelativeLayout result) {
		recorderSpinner = (Spinner) result.findViewById(R.id.log_options);
		// ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
		// activity, R.array.log_array,
		// android.R.layout.simple_spinner_item);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity,
				android.R.layout.simple_spinner_dropdown_item,
				RecorderManager.getTypesOfRecorders());
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		recorderSpinner.setAdapter(adapter);
		recorderSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				changeRecorderType();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
			}

		});

		// recordCheckBox = (CheckBox) result.findViewById(R.id.checkBoxRecord);
		// recordCheckBox
		// .setOnCheckedChangeListener(new OnCheckedChangeListener() {
		//
		// @Override
		// public void onCheckedChanged(CompoundButton buttonView,
		// boolean isChecked) {
		// activity.configDB.setRecordAllInteractions(isChecked);
		// }
		// });

		selectCurrentRecordOptions();

		recorderTitle = (TextView) result.findViewById(R.id.log_type_title);
	}

	private void selectCurrentDetector() {
		currentDetectorType = activity.configDB.getDetectorType();
		List<String> list = DetectorManager.getTypesOfDetectors();

		for (int i = 0; i < list.size(); i++) {
			if (currentDetectorType.equals(list.get(i)))
				modeSpinner.setSelection(i);
		}
	}

	private void selectCurrentRecordOptions() {
		currentRecorderType = activity.configDB.getRecorderType();
		// boolean recordAll = activity.configDB.getRecodeAllInteractions();

		List<String> list = RecorderManager.getTypesOfRecorders();

		for (int i = 0; i < list.size(); i++) {
			if (list.equals(currentRecorderType)) {
				recorderSpinner.setSelection(i);
			}
		}

		// recordCheckBox.setChecked(recordAll);
	}

	private void changeDetectorType() {
		selectedDetectorType = (String) modeSpinner.getSelectedItem();

		if (currentDetectorType.equals(selectedDetectorType))
			return;

		activity.configDB.setDetectorType(selectedDetectorType);
		// activity.configDB.enableDeviceSharing(deviceSharing.isChecked());

		currentDetectorType = selectedDetectorType;
	}

	private void changeRecorderType() {
		String selected = (String) recorderSpinner.getSelectedItem();

		if (currentRecorderType.equals(selected)) {
			return;
		}

		activity.configDB.setRecorderType(selected);
		currentRecorderType = selected;
	}

	// private void enableCheckBox() {
	// String selectedMode = (String) modeSpinner.getSelectedItem();
	//
	// if (selectedMode != null
	// && selectedMode.equals(IntrusionDetectorManager.FACE_RECOGNITION)) {
	// deviceSharing.setEnabled(true);
	// deviceSharing
	// .setChecked(activity.configDB.isDeviceSharingEnabled());
	// deviceSharing
	// .setOnCheckedChangeListener(new OnCheckedChangeListener() {
	//
	// @Override
	// public void onCheckedChanged(CompoundButton buttonView,
	// boolean isChecked) {
	// activity.configDB.enableDeviceSharing(isChecked);
	// }
	// });
	//
	// } else {
	// deviceSharing.setChecked(false);
	// deviceSharing.setEnabled(false);
	// }
	//
	// }

	private void setVisibility(int v) {
		modeTitle.setVisibility(v);
		modeSpinner.setVisibility(v);
		// deviceSharing.setVisibility(v);
		recorderTitle.setVisibility(v);
		recorderSpinner.setVisibility(v);
		// periodTitle.setVisibility(v);
		// periodDesc.setVisibility(v);
		// recordCheckBox.setVisibility(v);
		// picker.setVisibility(v);
		hide.setVisibility(v);
	}

	private void showConfirmDialog() {
		String msg = "Confirm the following settings:\n\nDetection: "
				+ activity.configDB.getDetectorType()
				// + "\nDevice Sharing: "
				// + (activity.configDB.isDeviceSharingEnabled() ? "Yes" : "No")
				+ "\nRecording: " + activity.configDB.getRecorderType();

		if (activity.configDB.getRecodeAllInteractions())
			msg += " and record all interactions";

		AlertDialog.Builder alertDialog;
		alertDialog = new AlertDialog.Builder(activity);
		alertDialog.setTitle("Auric Service");
		alertDialog.setMessage(msg);
		alertDialog.setPositiveButton("Confirm",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						activity.startBackgroundService();
						activity.finish();
					}
				});
		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						onOff.setChecked(false);
					}
				});

		alertDialog.show();

	}
}
