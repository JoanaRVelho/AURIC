package hcim.auric.general_activities.passcode;

import hcim.auric.database.SettingsPreferences;

public class ConfirmAndTurnOffPasscode extends AbstractConfirmPasscode {

	@Override
	protected void afterConfirmPasscode() {
		SettingsPreferences s = new SettingsPreferences(this);
		s.setHasPasscode(false);
		s.setPasscode("");

		super.finish();
	}

}
