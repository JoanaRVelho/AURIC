package hcim.auric.activities.passcode;

import hcim.auric.database.ConfigurationDatabase;

public class ConfirmAndTurnOffPasscode extends AbstractConfirmPasscode {

	@Override
	protected void afterConfirmPasscode() {
		ConfigurationDatabase db = ConfigurationDatabase.getInstance(this);
		db.deletePasscode();

		super.finish();
	}

}
