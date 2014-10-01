package hcim.auric.activities.passcode;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hcim.intrusiondetection.R;

public abstract class PasscodeActivity extends Activity {
	protected static final String TAG = "AURIC";

	private StringBuilder insertedPasscode;
	private ImageView[] passcodeImages;
	private TextView msg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.passcode_view);

		insertedPasscode = new StringBuilder();
		passcodeImages = new ImageView[4];
		passcodeImages[0] = (ImageView) findViewById(R.id.passcode1);
		passcodeImages[1] = (ImageView) findViewById(R.id.passcode2);
		passcodeImages[2] = (ImageView) findViewById(R.id.passcode3);
		passcodeImages[3] = (ImageView) findViewById(R.id.passcode4);
		
		msg = (TextView) findViewById(R.id.passcode_message);

		initActivity();
	}

	@Override
	protected void onPause() {
		if (insertedPasscode != null){
			insertedPasscode.delete(0, insertedPasscode.length());
		}
		super.onPause();
	}

	@Override
	protected void onStop() {
		if (insertedPasscode != null)
			insertedPasscode.delete(0, insertedPasscode.length());
		super.onStop();
	}

	public void onClickButtons(View v) {
		Button b = (Button) findViewById(v.getId());

		if (insertedPasscode == null)
			insertedPasscode = new StringBuilder();

		if (b.getText().toString().equals("del")) {
			if (insertedPasscode.length() > 0) {
				insertedPasscode.deleteCharAt(insertedPasscode.length() - 1);
				Log.d(TAG, "pass = " + insertedPasscode.toString());
				
				int idx = insertedPasscode.length();
				passcodeImages[idx].setImageResource(R.drawable.passcode_off);
			}
		} else {
			insertedPasscode.append(b.getText());
			int idx = insertedPasscode.length();
			passcodeImages[idx-1].setImageResource(R.drawable.passcode_on);
		}
		
		if(insertedPasscode.length() == 4){
			String pass = insertedPasscode.toString();
			Log.d(TAG, "passcode=" + pass);
			insertedPasscode.delete(0, insertedPasscode.length());
			afterEnteringPasscode(pass);
		}
	}

	protected void resetView() {
		for(ImageView i : passcodeImages){
			i.setImageResource(R.drawable.passcode_off);
		}
	}

	protected void setMessage(String s) {
		msg.setText(s);
	}

	protected abstract void initActivity();

	protected abstract void afterEnteringPasscode(String enteredPasscode);
}