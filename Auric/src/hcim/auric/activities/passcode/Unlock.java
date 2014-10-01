package hcim.auric.activities.passcode;

import hcim.auric.database.ConfigurationDatabase;


public abstract class Unlock extends PasscodeActivity {	
	private static final String UNLOCK_MESSAGE = "Type in passcode:";
	
	@Override
	protected void initActivity() {
		setMessage(UNLOCK_MESSAGE);
		
//		Bundle extras = getIntent().getExtras();
//		String value1 = extras.getString();
	}

	@Override
	protected void afterEnteringPasscode(String enteredPasscode) {
		ConfigurationDatabase db = ConfigurationDatabase.getInstance(this);
		String passcode = db.getPasscode();
		
		if(passcode.equals(enteredPasscode)){
			passcodeCorrect();
		}
		else{
			finish();
		}
	}

	protected abstract void passcodeCorrect();
}
