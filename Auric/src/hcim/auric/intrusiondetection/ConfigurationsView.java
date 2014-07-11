package hcim.auric.intrusiondetection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.hcim.intrusiondetection.R;

public class ConfigurationsView extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		View decorView = getWindow().getDecorView();
//		int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
//		decorView.setSystemUiVisibility(uiOptions);
//		ActionBar actionBar = getActionBar();
//		actionBar.hide();
		
		setContentView(R.layout.configurations_view);

		Button myPicture = (Button) findViewById(R.id.change_picture_button);
		myPicture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(ConfigurationsView.this, MyFaceConfigView.class);
				startActivity(i);
			}
		});
		
		Button negative = (Button) findViewById(R.id.change_negative);
		negative.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(ConfigurationsView.this, OtherFaceConfigView.class);
				startActivity(i);
			}
		});
	}
	
	
}
