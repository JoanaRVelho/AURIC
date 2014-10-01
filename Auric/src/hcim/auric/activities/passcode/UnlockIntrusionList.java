package hcim.auric.activities.passcode;

import hcim.auric.activities.IntrusionsListActivity;
import android.content.Intent;
import android.os.Bundle;

public class UnlockIntrusionList extends Unlock{

	@Override
	protected void passcodeCorrect() {
		Bundle extras = getIntent().getExtras();
		String value1 = extras.getString("value1");
		
		Intent i = new Intent(this, IntrusionsListActivity.class);
		i.putExtra("value1", value1);
		startActivityForResult(i, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		finish();
	}

}
