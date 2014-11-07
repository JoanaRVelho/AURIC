package hcim.auric.record.screen.textlog.timeline;

import hcim.auric.database.IntrusionsDatabase;
import hcim.auric.intrusion.Intrusion;
import hcim.auric.record.screen.textlog.TextualLog;
import hcim.auric.record.screen.textlog.TextualLogItem;
import hcim.auric.utils.FileManager;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;

import com.hcim.intrusiondetection.R;

public class TimelineActivity extends Activity {

	public static final String EXTRA_ID = "extra";
	// private static final String TAG = "AURIC";
	private Intrusion intrusion;
	private FileManager fileManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.run_timeline);

		String intrusionID = getIntent().getStringExtra(EXTRA_ID);
		intrusion = IntrusionsDatabase.getInstance(this).getIntrusion(
				intrusionID);

		fileManager = new FileManager(this);

		TextualLog log = new TextualLog(intrusionID, getPackageManager());
		TextualLog.load(fileManager, log);
		List<TextualLogItem> list = log.getList();

		TimelineAdapter adapter = new TimelineAdapter(list,
				intrusion.getImages(), this);

		GridView layout = (GridView) findViewById(R.id.timeline_layout);
		layout.setAdapter(adapter);
		layout.setEnabled(true);

		Button trash = (Button) findViewById(R.id.trash);
		trash.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				trashButtonAlertDialog();
			}
		});
	}

//	private void addItems(List<TextualLogItem> list, LinearLayout layout) {
//		for (int position = 0; position < list.size(); position++) {
//			TextualLogItem item = list.get(position);
//
//			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//			GridLayout view = new GridLayout(this);
//			view = (GridLayout) inflater.inflate(R.layout.timeline_item, null);
//
//			ImageView icon = (ImageView) view.findViewById(R.id.icon);
//			TextView text = (TextView) view.findViewById(R.id.text);
//			TextView time = (TextView) view.findViewById(R.id.time);
//			ImageView intruder = (ImageView) view.findViewById(R.id.intruder);
//			ImageView back = (ImageView) view.findViewById(R.id.int_background);
//
//			intruder.setImageBitmap(intrusion.getImages().get(0).getImage());
//
//			Drawable d = item.getIcon();
//
//			if (d != null)
//				icon.setImageDrawable(d);
//			else
//				icon.setImageResource(R.drawable.android);
//
//			text.setText(item.getAppName());
//			time.setText(item.getTime());
//
//			view.setEnabled(false);
//
//			if (position != list.size() - 1) {
//				TextualLogItem next = list.get(position + 1);
//				int marginTop = next.distance(list.get(position));
//				back.getLayoutParams().height += marginTop;
//			}
//			layout.addView(view);
//		}
//	}

	// private void setMargins(int position, List<TextualLogItem> list){
	// if (position != 0) {
	// TextualLogItem prev = list.get(position - 1);
	// int marginTop = list.get(position).marginTop(prev);
	//
	// LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
	// LinearLayout.LayoutParams.FILL_PARENT,
	// LinearLayout.LayoutParams.WRAP_CONTENT);
	// if (position == (list.size() - 1))
	// layoutParams.setMargins(0, marginTop, 0, 10);
	// else
	// layoutParams.setMargins(0, marginTop, 0, 0);
	//
	// layout.addView(view, layoutParams);
	// } else {
	//
	// }
	// }

	@Override
	public void finish() {
		if (intrusion.isChecked()) {
			super.finish();
		} else {
			markIntrusionAlertDialog();
		}
	}

	private void markIntrusionAlertDialog() {
		AlertDialog.Builder alertDialog;
		alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("Intrusion");
		alertDialog.setMessage("Is this a real intrusion?");
		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						IntrusionsDatabase intDB = IntrusionsDatabase
								.getInstance(TimelineActivity.this);
						intrusion.markAsRealIntrusion();
						intDB.updateIntrusion(intrusion);

						TimelineActivity.super.finish();
					}
				});

		alertDialog.setNegativeButton("NO",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						IntrusionsDatabase intDB = IntrusionsDatabase
								.getInstance(TimelineActivity.this);
						intrusion.markAsFalseIntrusion();
						intDB.updateIntrusion(intrusion);

						TimelineActivity.super.finish();
					}
				});
		alertDialog.show();
	}

	private void trashButtonAlertDialog() {
		AlertDialog.Builder alertDialog;
		alertDialog = new AlertDialog.Builder(TimelineActivity.this);
		alertDialog.setTitle("Delete Intrusion Log");
		alertDialog
				.setMessage("Are you sure that you want to delete this intrusion log?");
		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						delete();
					}
				});
		alertDialog.setNegativeButton("NO", null);
		alertDialog.show();
	}

	private void delete() {
		IntrusionsDatabase intDB = IntrusionsDatabase.getInstance(this);
		intDB.deleteIntrusion(intrusion.getID(), false);

		File dir = new File(
				fileManager.getIntrusionDirectory(intrusion.getID()));
		for (File f : dir.listFiles()) {
			f.delete();
		}
		dir.delete();

		super.finish();
	}
}
