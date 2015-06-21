package hcim.auric.general_activities.passcode;

import android.content.Intent;

public class ConfirmAndChangePasscode extends AbstractConfirmPasscode {

	@Override
	protected void afterConfirmPasscode() {
		Intent i = new Intent(this, InsertPasscode.class);
		startActivityForResult(i, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.finish();
	}
}
