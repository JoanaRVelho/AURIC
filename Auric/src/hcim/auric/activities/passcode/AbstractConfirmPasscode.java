package hcim.auric.activities.passcode;

import hcim.auric.database.ConfigurationDatabase;
import android.app.AlertDialog;
import android.content.DialogInterface;

public abstract class AbstractConfirmPasscode extends PasscodeActivity {
	private static final String CONFIRM = "Type in password:";

	@Override
	protected void initActivity() {
		setMessage(CONFIRM);
		resetView();
	}

	@Override
	protected void afterEnteringPasscode(String enteredPasscode) {
		ConfigurationDatabase db = ConfigurationDatabase.getInstance(this);
		String p = db.getPasscode();

		if (p != null && p.equals(enteredPasscode)) {
			afterConfirmPasscode();
		} else {
			AlertDialog.Builder alertDialog;
			alertDialog = new AlertDialog.Builder(AbstractConfirmPasscode.this);
			alertDialog.setTitle("Wrong Passcode");
			alertDialog.setMessage("Wrong Passcode!");
			alertDialog.setNeutralButton("OK",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});

			alertDialog.show();
		}
	}
	
	protected abstract void afterConfirmPasscode();
}
