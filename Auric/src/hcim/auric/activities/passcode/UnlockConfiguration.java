package hcim.auric.activities.passcode;

import hcim.auric.activities.ConfigurationsActivity;
import android.content.Intent;

public class UnlockConfiguration extends Unlock{

	@Override
	protected void passcodeCorrect() {
		Intent i = new Intent(this, ConfigurationsActivity.class);
		startActivityForResult(i, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		finish();
	}
}
