package hcim.auric.activities.passcode;

import hcim.auric.data.SettingsPreferences;
import android.content.Intent;
import android.widget.Toast;

public class Unlock extends PasscodeActivity {
	private static final String UNLOCK_MESSAGE = "Type in passcode:";
	public static final String EXTRA_ID = "extra";

	private boolean check;

	@Override
	protected void initActivity() {
		setMessage(UNLOCK_MESSAGE);
	}

	@Override
	protected void afterEnteringPasscode(String enteredPasscode) {
		SettingsPreferences s = new SettingsPreferences(this);
		String passcode = s.getPasscode();

		check = passcode.equals(enteredPasscode);
		if (check) {
			finish();
		}else{
			Toast.makeText(this, "Wrong Passcode", Toast.LENGTH_SHORT).show();
			resetView();
		}
	}

	@Override
	public void finish() {
		Intent data = new Intent();
		data.putExtra(EXTRA_ID, check);
		setResult(RESULT_OK, data);
		super.finish();
	}
}
