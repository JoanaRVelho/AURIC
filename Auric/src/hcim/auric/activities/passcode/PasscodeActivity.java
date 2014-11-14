package hcim.auric.activities.passcode;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.hcim.intrusiondetection.R;

public abstract class PasscodeActivity extends Activity {
	protected static final String TAG = "AURIC";

	private EditText edit;
	private TextView msg;
	private String insertedPasscode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.insert_password);

		msg = (TextView) findViewById(R.id.passcode_message);

		edit = (EditText) findViewById(R.id.password_edit);

		Button done = (Button) findViewById(R.id.done);
		done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Editable editable = edit.getText();

				if (editable != null) {
					insertedPasscode = editable.toString();
					
					if (insertedPasscode != null
							&& insertedPasscode.length() != 0)
						afterEnteringPasscode(insertedPasscode);
				}
			}
		});
		
		initActivity();
	}

	@Override
	protected void onPause() {
		insertedPasscode = "";
		super.onPause();
	}

	@Override
	protected void onStop() {
		insertedPasscode = "";
		super.onStop();
	}

	protected void resetView() {
		edit.setText("");
	}

	protected void setMessage(String s) {
		msg.setText(s);
	}

	protected abstract void initActivity();

	protected abstract void afterEnteringPasscode(String enteredPasscode);
}