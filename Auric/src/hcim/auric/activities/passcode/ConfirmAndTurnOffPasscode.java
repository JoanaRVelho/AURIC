package hcim.auric.activities.passcode;

import hcim.auric.data.SettingsPreferences;

public class ConfirmAndTurnOffPasscode extends AbstractConfirmPasscode {

	@Override
	protected void afterConfirmPasscode() {
		SettingsPreferences s = new SettingsPreferences(this);
		s.setHasPasscode(false);
		s.setPasscode("");

		super.finish();
	}

}
