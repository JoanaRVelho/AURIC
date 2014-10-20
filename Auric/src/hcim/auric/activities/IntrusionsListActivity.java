package hcim.auric.activities;

import hcim.auric.database.IntrusionsDatabase;
import hcim.auric.intrusion.Intrusion;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hcim.intrusiondetection.R;

public class IntrusionsListActivity extends Activity {

	private Context context;
	private IntrusionsDatabase intrusionsDB;
	private String date;
	private LinearLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intrusions_list);

		context = this.getApplicationContext();
		intrusionsDB = IntrusionsDatabase.getInstance(context);

		layout = (LinearLayout) findViewById(R.id.listlogs);

		Bundle extras = getIntent().getExtras();
		date = extras.getString("value1");

		TextView t = (TextView) findViewById(R.id.textView1);
		t.setText(date + " Intrusion");

		List<Intrusion> intrusions = intrusionsDB
				.getIntrusionsDataFromADay(date);

		if (intrusions != null) {
			if (intrusions.size() > 1)
				t.append("s");

			addButtons(intrusions);
		}
	}

	@Override
	protected void onResume() {
		// remove all
		if (((LinearLayout) layout).getChildCount() > 0)
			((LinearLayout) layout).removeAllViews();

		List<Intrusion> intrusions = intrusionsDB
				.getIntrusionsDataFromADay(date);

		if (intrusions == null || intrusions.size() == 0)
			finish();

		addButtons(intrusions);

		super.onResume();
	}

	private void addButtons(final List<Intrusion> intrusions) {
		runOnUiThread(new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				Drawable realInt = getResources().getDrawable(
						R.drawable.mark_real);
				Drawable falseInt = getResources().getDrawable(
						R.drawable.mark_false);
				Drawable unchecked = getResources().getDrawable(
						R.drawable.mark_new);

				for (Intrusion i : intrusions) {
					Button b = new Button(context);
					b.setText("Intrusion " + i.getTime());
					b.setTextColor(Color.WHITE);
					switch(i.getTag()){
					case Intrusion.UNCHECKED: 
						b.setBackgroundDrawable(unchecked);
						break;
					case Intrusion.FALSE_INTRUSION: 
						b.setBackgroundDrawable(falseInt);
						break;
					case Intrusion.REAL_INTRUSION: 
						b.setBackgroundDrawable(realInt);
						break;
					}
					
					layout.addView(b);
					b.setOnClickListener(new IntrusionClickListener(i.getID()));
				}
			}
		});
	}

	class IntrusionClickListener implements OnClickListener {
		private String intrusion;

		public IntrusionClickListener(String intrusion) {
			this.intrusion = intrusion;
		}

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(IntrusionsListActivity.this,
					RunInteraction.class);
			intent.putExtra("interaction", intrusion);
			startActivity(intent);
		}
	}
}
