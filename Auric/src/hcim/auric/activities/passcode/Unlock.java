package hcim.auric.activities.passcode;

import hcim.auric.database.ConfigurationDatabase;
import android.content.Intent;

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
		ConfigurationDatabase db = ConfigurationDatabase.getInstance(this);
		String passcode = db.getPasscode();

		check = passcode.equals(enteredPasscode);
		if (check) {
			finish();
		}
	}

	@Override
	public void finish() {
		Intent data = new Intent();
		data.putExtra(EXTRA_ID, check);
		setResult(RESULT_OK, data);
		super.finish();
		super.finish();
	}
}
