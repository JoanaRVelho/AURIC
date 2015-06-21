package hcim.auric.general_activities.settings;

import hcim.auric.camera.RecognitionActivity;
import hcim.auric.general_activities.images.GridOfRecognizedPictures;
import hcim.auric.general_activities.images.SlideShowRecognizedPictures;
import hcim.auric.recognition.Picture;

import java.util.List;

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
	private Button viewAll, testCamera, testPhoto, edit;

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

		viewAll = (Button) result.findViewById(R.id.viewall);
		viewAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(),
						SlideShowRecognizedPictures.class));
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

		testPhoto = (Button) result.findViewById(R.id.testphoto);
		testPhoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), TestPhoto.class);
				startActivity(i);
			}
		});

		return result;
	}

	@Override
	public void onResume() {
		List<Picture> list = activity.picsDB.getMyPictures();

		if (list == null || list.isEmpty())
			viewAll.setVisibility(View.GONE);
		else
			viewAll.setVisibility(View.VISIBLE);

		super.onResume();
	}
}
