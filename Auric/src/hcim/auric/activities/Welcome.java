package hcim.auric.activities;

import hcim.auric.activities.face.DetectionActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.hcim.intrusiondetection.R;

public class Welcome extends Activity {

	public static final int CODE = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
	}

	public void startConfiguration(View v) {
		Intent i = new Intent(this, DetectionActivity.class);
		startActivityForResult(i, CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CODE)
			finish();
	}

	@Override
	public void finish() {
		Intent data = new Intent();
		setResult(RESULT_OK, data);
		super.finish();
	}
}
