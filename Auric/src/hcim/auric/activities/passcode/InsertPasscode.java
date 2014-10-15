package hcim.auric.activities.passcode;

import hcim.auric.database.ConfigurationDatabase;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;


public class InsertPasscode extends PasscodeActivity{

	private static final String CONFIG_PASSCODE_PHASE1 = "Type in new passcode:";
	private static final String CONFIG_PASSCODE_PHASE2 = "Type new passcode again:";

	private String passcode;
	
	@Override
	protected void initActivity() {
		setMessage(CONFIG_PASSCODE_PHASE1);
		passcode = null;
	}

	@Override
	protected void afterEnteringPasscode(String enteredPasscode) {
		Log.d("AURIC", "entered pass="+enteredPasscode);
		Log.d("AURIC", "passcode="+passcode);
		
		if(passcode == null){
			passcode = enteredPasscode;
			setMessage(CONFIG_PASSCODE_PHASE2);
			resetView();
		}else{
			if(passcode.equals(enteredPasscode)){
				ConfigurationDatabase db = ConfigurationDatabase.getInstance(this);
				db.setPasscode(passcode);
				configurationComplete();
			}
			else{
				restartConfiguration();
			}
		}
	}

	private void restartConfiguration() {
		AlertDialog.Builder alertDialog;
		alertDialog = new AlertDialog.Builder(InsertPasscode.this);
		alertDialog.setTitle("Different Passcode");
		alertDialog.setMessage("The passcodes inserted are different!\nAURIC will restart passcode configutation.");
		alertDialog.setNeutralButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
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
		alertDialog.setMessage("Passcode configuration is completed! Do not forget your passcode!");
		alertDialog.setNeutralButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
						finish();
					}
				});

		alertDialog.show();
	}

}