package hcim.auric.activities.passcode;

import hcim.auric.data.SettingsPreferences;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

public class InsertPasscode extends PasscodeActivity {

	private static final String CONFIG_PASSCODE_PHASE1 = "Type in new passcode: (at least 6 characters)";
	private static final String CONFIG_PASSCODE_PHASE2 = "Type new passcode again:";
	private static final int MIN = 6;

	private String passcode;

	@Override
	protected void initActivity() {
		setMessage(CONFIG_PASSCODE_PHASE1);
		passcode = null;
	}

	@Override
	protected void afterEnteringPasscode(String enteredPasscode) {
		if (passcode == null) {
			resetView();
			if (enteredPasscode == null || enteredPasscode.length() < MIN) {
				Toast.makeText(this,
						"Password must have at least " + MIN + " characters!",
						Toast.LENGTH_LONG).show();
			} else {
				passcode = enteredPasscode;
				setMessage(CONFIG_PASSCODE_PHASE2);
			}
		} else {
			if (passcode.equals(enteredPasscode)) {
				SettingsPreferences s = new SettingsPreferences(this);
				s.setHasPasscode(true);
				s.setPasscode(passcode);
				configurationComplete();
			} else {
				restartConfiguration();
			}
		}
	}

	private void restartConfiguration() {
		AlertDialog.Builder alertDialog;
		alertDialog = new AlertDialog.Builder(InsertPasscode.this);
		alertDialog.setTitle("Different Passcode");
		alertDialog
				.setMessage("The passcodes inserted are different!\nAURIC will restart passcode configutation.");
		alertDialog.setNeutralButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						passcode = null;
						setMessage(CONFIG_PASSCODE_PHASE1);
						resetView();
					}
				});

		alertDialog.show();
	}

	private void configurationComplete() {
		AlertDialog.Builder alertDialog;
		alertDialog = new AlertDialog.Builder(InsertPasscode.this);
		alertDialog.setTitle("Passcode Configuration Complete");
		alertDialog
				.setMessage("Passcode configuration is completed! Do not forget your passcode!");
		alertDialog.setNeutralButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});

		alertDialog.show();
	}

}
