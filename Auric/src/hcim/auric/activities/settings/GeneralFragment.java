package hcim.auric.activities.settings;

import hcim.auric.activities.passcode.ConfirmAndChangePasscode;
import hcim.auric.activities.passcode.ConfirmAndTurnOffPasscode;
import hcim.auric.activities.passcode.InsertPasscode;
import hcim.auric.data.SessionDatabase;
import hcim.auric.detector.DetectorManager;
import hcim.auric.record.RecorderManager;
import hcim.auric.strategy.StrategyManager;
import hcim.auric.utils.FileManager;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.hcim.intrusiondetection.R;

public class GeneralFragment extends Fragment {
	private SettingsActivity activity;

	private Switch onOff;

	private TextView detectorTitle;
	private Spinner detectorSpinner;
	private CheckBox hide;

	private Button changePasscode;
	private Switch passcodeSwitch;

	private TextView recorderTitle;
	private Spinner recorderSpinner;

	private Spinner strategySpinner;
	private TextView strategyTitle;

	private Button deleteAll;

	private NumberPicker picker;
	private TextView periodDesc;

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
			boolean b = activity.settings.hasPasscode();
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
		String recorder = activity.settings.getRecorderType();
		String detector = activity.settings.getDetectorType();
		String strategy = activity.settings.getStrategyType();

		initOnOffSwitch(result);
		initDetectorSection(result, detector);
		initPasscodeSection(result);
		initRecorderOptions(result, recorder);
		initNumberPicker(result);
		initHideNotification(result);
		initDeleteButton(result);
		initStrategySection(result, strategy);

		if (onOff.isChecked()) {
			setVisibility(View.GONE);
		} else {
			setVisibility(View.VISIBLE);
		}
	}

	private void initDetectorSection(RelativeLayout result, String detector) {
		detectorSpinner = (Spinner) result.findViewById(R.id.mode_spinner);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity,
				android.R.layout.simple_spinner_dropdown_item,
				DetectorManager.getTypesOfDetectors());
		detectorSpinner.setAdapter(adapter);
		selectCurrentDetector(detector);

		detectorSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				String selected = (String) detectorSpinner.getSelectedItem();
				activity.settings.setDetectorType(selected);
			};

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
			}

		});

		detectorTitle = (TextView) result.findViewById(R.id.mode_title);
	}

	private void selectCurrentDetector(String current) {
		List<String> list = DetectorManager.getTypesOfDetectors();

		for (int i = 0; i < list.size(); i++) {
			if (current.equals(list.get(i))) {
				detectorSpinner.setSelection(i);
				return;
			}
		}
	}

	private void initRecorderOptions(RelativeLayout result, String recorder) {
		recorderSpinner = (Spinner) result.findViewById(R.id.log_options);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity,
				android.R.layout.simple_spinner_dropdown_item,
				RecorderManager.getTypesOfRecorders());
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		recorderSpinner.setAdapter(adapter);
		selectCurrentRecorder(recorder);

		recorderSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				String selected = (String) recorderSpinner.getSelectedItem();
				activity.settings.setRecorderType(selected);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
			}

		});
		recorderTitle = (TextView) result.findViewById(R.id.log_type_title);
	}

	private void selectCurrentRecorder(String current) {
		List<String> list = RecorderManager.getTypesOfRecorders();

		for (int i = 0; i < list.size(); i++) {
			if (current.equals(list.get(i))) {
				recorderSpinner.setSelection(i);
				return;
			}
		}
	}

	private void initStrategySection(RelativeLayout result, String strategy) {
		strategySpinner = (Spinner) result.findViewById(R.id.strategy_spinner);
		strategyTitle = (TextView) result.findViewById(R.id.strategy_title);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity,
				android.R.layout.simple_spinner_dropdown_item,
				StrategyManager.getTypesOfStrategies());
		strategySpinner.setAdapter(adapter);
		selectCurrentStrategy(strategy);

		strategySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {
				String selected = (String) strategySpinner.getSelectedItem();
				activity.settings.setStrategyType(selected);
			};

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
			}

		});
	}

	private void selectCurrentStrategy(String current) {
		List<String> list = StrategyManager.getTypesOfStrategies();

		for (int i = 0; i < list.size(); i++) {
			if (current.equals(list.get(i))) {
				strategySpinner.setSelection(i);
				return;
			}
		}
	}

	private void initDeleteButton(RelativeLayout result) {
		deleteAll = (Button) result.findViewById(R.id.deleteAll);
		deleteAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialogDeleteAll();
			}

			private void showDialogDeleteAll() {
				AlertDialog.Builder alertDialog;
				alertDialog = new AlertDialog.Builder(activity);
				alertDialog.setTitle("Delete");
				alertDialog
						.setMessage("Are you sure that you want to delete all sessions?");
				alertDialog.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								deleteAll();
							}
						});
				alertDialog.setNegativeButton("No", null);

				alertDialog.show();
			}
		});
	}

	private void deleteAll() {
		SessionDatabase db = SessionDatabase.getInstance(activity);
		db.deleteAll();
		
		FileManager manager = new FileManager(activity);
		manager.deleteSessions();
	}

	private void initNumberPicker(RelativeLayout result) {
		picker = (NumberPicker) result.findViewById(R.id.rate_number_picker);
		picker.setMinValue(0);
		picker.setMaxValue(60);
		int value = activity.settings.getCameraPeriod() / 1000;
		picker.setValue(value);
		picker.setWrapSelectorWheel(false);
		picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		picker.setOnValueChangedListener(new OnValueChangeListener() {

			@Override
			public void onValueChange(NumberPicker picker, int oldVal,
					int newVal) {
				activity.settings.setCameraPeriod(newVal * 1000);
			}
		});
		periodDesc = (TextView) result.findViewById(R.id.rate_desc);
	}

	private void initHideNotification(RelativeLayout result) {
		hide = (CheckBox) result.findViewById(R.id.hide_not);
		hide.setChecked(activity.settings.hideNotification());

		hide.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				activity.settings.setHideNotification(isChecked);
			}
		});
	}

	private void initOnOffSwitch(RelativeLayout result) {
		onOff = (Switch) result.findViewById(R.id.on_off);
		boolean on = activity.settings.isIntrusionDetectorActive();
		onOff.setChecked(on);

		onOff.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				activity.settings.setIntrusionDetectorActive(isChecked);

				if (isChecked)
					showConfirmDialog();
				else {
					activity.stopBackgroundService();
					setVisibility(View.VISIBLE);
				}
			}

		});
	}

	private void initPasscodeSection(RelativeLayout result) {
		passcodeSwitch = (Switch) result.findViewById(R.id.switch_passcode);
		passcodeSwitch.setChecked(activity.settings.hasPasscode());
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

	private void setVisibility(int v) {
		detectorTitle.setVisibility(v);
		detectorSpinner.setVisibility(v);
		recorderTitle.setVisibility(v);
		recorderSpinner.setVisibility(v);
		periodDesc.setVisibility(v);
		picker.setVisibility(v);
		hide.setVisibility(v);

		strategySpinner.setVisibility(v);
		strategyTitle.setVisibility(v);

		deleteAll.setVisibility(v);
	}

	private void showConfirmDialog() {
		String msg = "Confirm the following settings:\n\nDetection: "
				+ activity.settings.getDetectorType() + "\nStrategy: "
				+ activity.settings.getStrategyType() + "\nRecording: "
				+ activity.settings.getRecorderType();

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
