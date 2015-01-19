package hcim.auric.activities.settings;

import hcim.auric.activities.passcode.ConfirmAndChangePasscode;
import hcim.auric.activities.passcode.ConfirmAndTurnOffPasscode;
import hcim.auric.activities.passcode.InsertPasscode;
import hcim.auric.database.ConfigurationDatabase;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;

import com.hcim.intrusiondetection.R;

public class GeneralFragment extends Fragment {
	private SettingsActivity activity;

	private Switch onOff;
	private Spinner modeSpinner;
	private String currentMode;
	private CheckBox deviceSharing;
	private String selectedMode;
	private Button changePasscode;
	private Switch passcodeSwitch;
	private Spinner logSpinner;
	private String currentLogType;
	private NumberPicker picker;

	private boolean isChecked;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		activity = (SettingsActivity) getActivity();

		RelativeLayout result = (RelativeLayout) inflater.inflate(
				R.layout.fragment_general, container, false);
		initView(result);

		return result;
	}

	private void initView(RelativeLayout result) {
		initModeSection(result);
		initPasscodeSection(result);
		initLogOptions(result);
		initOnOffSwitch(result);
		initNumberPicker(result);
	}

	private void initNumberPicker(RelativeLayout result) {
		picker = (NumberPicker) result.findViewById(R.id.rate_number_picker);
		picker.setMinValue(0);
		picker.setMaxValue(60);
		int value = activity.configDB.getCameraPeriod()/1000;
		picker.setValue(value);
		Log.d("AURIC", "camera period = "+ value);
		picker.setWrapSelectorWheel(false);
		picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		picker.setOnValueChangedListener(new OnValueChangeListener() {

			@Override
			public void onValueChange(NumberPicker picker, int oldVal,
					int newVal) {
				//AbstractAuditTask.CAMERA_PERIOD_MILIS = newVal * 1000;
				activity.configDB.setCameraPeriod(newVal*1000);
			}
		});

	}

	private void initOnOffSwitch(RelativeLayout result) {
		onOff = (Switch) result.findViewById(R.id.on_off);
		onOff.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				activity.configDB.setIntrusionDetectorActivity(isChecked);
			}

		});

		isChecked = activity.configDB.isIntrusionDetectorActive();
		onOff.setChecked(isChecked);
	}

	private void initModeSection(RelativeLayout result) {
		// init spinner
		modeSpinner = (Spinner) result.findViewById(R.id.mode_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				activity, R.array.mode_array,
				android.R.layout.simple_spinner_item);
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

		deviceSharing = (CheckBox) result.findViewById(R.id.checkBox1);
		enableCheckBox();
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

	private void initLogOptions(RelativeLayout result) {
		logSpinner = (Spinner) result.findViewById(R.id.log_options);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				activity, R.array.log_array,
				android.R.layout.simple_spinner_item);
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

	private void selectCurrentMode() {
		currentMode = activity.configDB.getMode();

		if (currentMode.equals(ConfigurationDatabase.WIFI_MODE)) {
			modeSpinner.setSelection(1);
		}
		if (currentMode.equals(ConfigurationDatabase.ORIGINAL_MODE)) {
			modeSpinner.setSelection(0);
		}
	}

	@Override
	public void onResume() {
		if (passcodeSwitch != null) {
			boolean b = activity.configDB.hasPasscode();
			passcodeSwitch.setChecked(b);
			
			if (b)
				changePasscode.setVisibility(View.VISIBLE);
			else {
				changePasscode.setVisibility(View.INVISIBLE);
			}
		}
		super.onResume();
	}

	private void selectCurrentLogType() {
		currentLogType = activity.configDB.getLogType();

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

		activity.configDB.setMode(selectedMode);
		activity.configDB.enableDeviceSharing(deviceSharing.isChecked());

		currentMode = selectedMode;
	}

	private void changeLogType() {
		String selected = (String) logSpinner.getSelectedItem();

		if (currentLogType.equals(selected)) {
			return;
		}

		activity.configDB.setLogType(selected);
		currentLogType = selected;
	}

	private void enableCheckBox() {
		String selectedMode = (String) modeSpinner.getSelectedItem();

		if (selectedMode != null
				&& selectedMode.equals(ConfigurationDatabase.ORIGINAL_MODE)) {
			deviceSharing.setEnabled(true);
			deviceSharing
					.setChecked(activity.configDB.isDeviceSharingEnabled());
			deviceSharing.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					activity.configDB.enableDeviceSharing(isChecked);					
				}
			});

		} else {
			deviceSharing.setChecked(false);
			deviceSharing.setEnabled(false);
		}

	}
}
