package hcim.auric.activities.settings;

import hcim.auric.activities.images.RecognizedPicturesSlideShow;
import hcim.auric.activities.setup.TestFaceRecognition;
import hcim.auric.recognition.FaceRecognition;
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
import android.widget.RelativeLayout;

import com.hcim.intrusiondetection.R;

public class FaceRecognitionFragment extends Fragment {

	private NumberPicker picker;
	private Button viewAll, test;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		RelativeLayout result = (RelativeLayout) inflater.inflate(
				R.layout.fragment_face_recog, container, false);
		
		picker = (NumberPicker) result.findViewById(R.id.numberPicker1);
		picker.setMinValue(0);
		picker.setMaxValue(500);
		picker.setValue(FaceRecognition.MAX);
		picker.setWrapSelectorWheel(false);

		picker.setOnValueChangedListener(new OnValueChangeListener() {

			@Override
			public void onValueChange(NumberPicker picker, int oldVal,
					int newVal) {
				FaceRecognition.MAX = newVal;
			}
		});

		viewAll = (Button) result.findViewById(R.id.viewall);
		viewAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(),
						RecognizedPicturesSlideShow.class));
			}
		});

		test = (Button) result.findViewById(R.id.testfr);
		test.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), TestFaceRecognition.class);
				startActivity(i);
			}
		});

		return result;
	}
}
