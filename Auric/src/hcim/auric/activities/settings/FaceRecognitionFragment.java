package hcim.auric.activities.settings;

import hcim.auric.activities.face.RecognitionActivity;
import hcim.auric.activities.images.GridOfRecognizedPictures;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.ScrollView;

import com.hcim.intrusiondetection.R;

public class FaceRecognitionFragment extends Fragment {
	private NumberPicker picker;
	private SettingsActivity activity;
	private Button testCamera, edit;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ScrollView result = (ScrollView) inflater.inflate(
				R.layout.fragment_face_recog, container, false);

		activity = (SettingsActivity) getActivity();

		picker = (NumberPicker) result.findViewById(R.id.numberPicker1);
		picker.setMinValue(0);
		picker.setMaxValue(500);
		picker.setValue(activity.settings.getFaceRecognitionMax());
		picker.setWrapSelectorWheel(false);

		picker.setOnValueChangedListener(new OnValueChangeListener() {

			@Override
			public void onValueChange(NumberPicker picker, int oldVal,
					int newVal) {
				activity.settings.setFaceRecognitionMax(newVal);
			}
		});

		testCamera = (Button) result.findViewById(R.id.testfr);
		testCamera.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), RecognitionActivity.class);
				startActivity(i);
			}
		});

		edit = (Button) result.findViewById(R.id.edit);
		edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(),
						GridOfRecognizedPictures.class);
				startActivity(i);
			}
		});

		return result;
	}
}
