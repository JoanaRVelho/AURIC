package hcim.auric.intrusiondetection;

import hcim.auric.database.IntrusionsDatabase;
import hcim.auric.intrusion.Intrusion;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.hcim.intrusiondetection.R;

public class IntrusionView extends Activity {
	String intrusion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		View decorView = getWindow().getDecorView();
//		int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
//		decorView.setSystemUiVisibility(uiOptions);
//		ActionBar actionBar = getActionBar();
//		actionBar.hide();
		
		setContentView(R.layout.intrusion_view);

		Bundle extras = getIntent().getExtras();
		intrusion = extras.getString("id");

		Button picturesButton = (Button) findViewById(R.id.pictures_button);
		Button replay   = (Button) findViewById(R.id.replay);

		TextView t = (TextView) findViewById(R.id.textView1);
		t.setText(intrusion);

		picturesButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(IntrusionView.this, CapturedPictureView.class);
				intent.putExtra("id", intrusion);
				startActivity(intent);
			}

		}); 

		replay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClassName("swat.record", "swat.record.RunInteraction");
				Intrusion i = IntrusionsDatabase.getIntrusion(intrusion);
				intent.putExtra("interaction", i.getLog().getId());
				startActivity(intent);
			}

		}); 
	}
}
