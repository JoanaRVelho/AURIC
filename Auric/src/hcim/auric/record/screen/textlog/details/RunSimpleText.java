package hcim.auric.record.screen.textlog.details;

import hcim.auric.activities.images.IntruderPictureGrid;
import hcim.auric.database.IntrusionsDatabase;
import hcim.auric.intrusion.Intrusion;
import hcim.auric.recognition.Picture;
import hcim.auric.record.screen.mswat_lib.OnSwipeTouchListener;
import hcim.auric.record.screen.textlog.TextualLog;
import hcim.auric.record.screen.textlog.TextualLogItem;
import hcim.auric.utils.FileManager;

import java.io.File;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hcim.intrusiondetection.R;

public class RunSimpleText extends Activity {
	protected static final String TAG = "AURIC";
	public static final String EXTRA_ID = "extra";
	private static final int INTERVAL = 1000;

	private Intrusion intrusion;
	private FileManager fileManager;

	private List<TextualLogItem> listItems;
	private List<Bitmap> intruderList;
	private int idxPhotos;
	private int idxLog;

	private TextView msg, time;
	private ImageView icon;
	private ImageView background;
	private ImageView playImg;
	private ImageView intruderImg;
	private RelativeLayout logView;

	private Button trash;

	private boolean play;
	private Context context;

	private int totalTime;
	private int totalItems;
	private int totalPhotos;
	private int logPeriod;
	private int photosPeriod;
	private Handler logHandler = new Handler();
	private Runnable logRunnable = new Runnable() {

		@Override
		public void run() {
			if (play) {
				if (idxLog < totalItems) {
					updateLog();
					idxLog++;
					photosHandler.postDelayed(this, logPeriod);
				} else { // se terminou
					reset();
				}
			}
		}
	};

	private Handler photosHandler = new Handler();
	private Runnable photosRunnable = new Runnable() {

		@Override
		public void run() {
			if (play)
				if (idxPhotos < totalPhotos) {
					updatePhotos();
					idxPhotos++;
					photosHandler.postDelayed(this, photosPeriod);
				} else { // se terminou
					play = false;
					reset();
				}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fullscreen();
		setContentView(R.layout.run_simple_text);

		context = getApplicationContext();

		String intrusionID = getIntent().getStringExtra(EXTRA_ID);
		intrusion = IntrusionsDatabase.getInstance(this).getIntrusion(
				intrusionID);

		intruderList = Picture.getBitmapList(intrusion.getImages());
		fileManager = new FileManager(this);

		TextualLog log = new TextualLog(intrusionID, getPackageManager());
		TextualLog.load(fileManager, log);
		
		listItems = log.getList();
		computeTotalTime();

		initView();
		reset();
	}

	@Override
	public void finish() {
		if (intrusion.isChecked()) {
			super.finish();
		} else {
			markIntrusionAlertDialog();
		}
	}

	private void play() {
		photosHandler.post(photosRunnable);
		logHandler.post(logRunnable);
		play = true;
	}

	private void reset() {
		initLog();
		initPhotos();
		idxPhotos = 0;
		idxLog = 0;
		play = false;
		playImg.setVisibility(View.VISIBLE);
		trash.setVisibility(View.VISIBLE);
		background.setVisibility(View.VISIBLE);
		play = false;
	}

	private void computeTotalTime() {
		totalItems = listItems.size();
		totalPhotos = intruderList.size();
		totalTime = totalItems * INTERVAL;
		logPeriod = totalTime / totalItems;
		photosPeriod = totalTime / totalPhotos;
	}

	private void initLog() {
		TextualLogItem t = listItems.get(0);

		icon.setImageDrawable(t.getIcon());
		time.setText(t.getTime());
		msg.setText(t.getAppName());
	}

	private void updateLog() {
		TextualLogItem t = listItems.get(idxLog);

		Drawable d = t.getIcon();
		if (d != null)
			icon.setImageDrawable(d);
		else
			icon.setImageResource(R.drawable.android);

		time.setText(t.getTime());
		msg.setText(t.getAppName());
	}

	private void initPhotos() {
		Bitmap bm = (intruderList == null || totalPhotos == 0) ? null
				: intruderList.get(0);
		if (bm != null) {
			intruderImg.setImageBitmap(bm);
		}
	}

	private void updatePhotos() {
		Bitmap b = intruderList.get(idxPhotos);
		intruderImg.setImageBitmap(b);
	}

	private void fullscreen() {
		View decorView = getWindow().getDecorView();
		// Hide the status bar.
		int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
		decorView.setSystemUiVisibility(uiOptions);
		// Remember that you should never show the action bar if the
		// status bar is hidden, so hide that too if necessary.
		ActionBar actionBar = getActionBar();
		actionBar.hide();
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	private void initView() {
		time = (TextView) findViewById(R.id.time);
		msg = (TextView) findViewById(R.id.text);
		icon = (ImageView) findViewById(R.id.icon);

		logView = (RelativeLayout) findViewById(R.id.logView);
		logView.setOnTouchListener(new OnSwipeTouchListener(this) {

			public void onSingleTap() {
				if (!play) {
					play = true;
					background.setVisibility(View.INVISIBLE);
					trash.setVisibility(View.INVISIBLE);
					playImg.setVisibility(View.INVISIBLE);
					play();
				} else {
					playImg.setVisibility(View.VISIBLE);
					trash.setVisibility(View.VISIBLE);
					background.setVisibility(View.VISIBLE);
					play = false;
				}
			}

			public boolean onTouch(View v, MotionEvent event) {
				boolean result = gestureDetector.onTouchEvent(event);
				return result;
			}

		});

		background = (ImageView) findViewById(R.id.background1);
		playImg = (ImageView) findViewById(R.id.play);

		intruderImg = (ImageView) findViewById(R.id.img_intruder);
		intruderImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (playImg.getVisibility() == View.VISIBLE) {
					Intent i = new Intent(RunSimpleText.this,
							IntruderPictureGrid.class);
					i.putExtra(IntruderPictureGrid.EXTRA_ID, intrusion.getID());
					startActivity(i);
				}
			}
		});

		trash = (Button) findViewById(R.id.trash);
		trash.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				trashButtonAlertDialog();
			}
		});
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
								.getInstance(context);
						intrusion.markAsRealIntrusion();
						intDB.updateIntrusion(intrusion);

						RunSimpleText.super.finish();
					}
				});

		alertDialog.setNegativeButton("NO",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						IntrusionsDatabase intDB = IntrusionsDatabase
								.getInstance(context);
						intrusion.markAsFalseIntrusion();
						intDB.updateIntrusion(intrusion);

						RunSimpleText.super.finish();
					}
				});
		alertDialog.show();
	}

	private void trashButtonAlertDialog() {
		AlertDialog.Builder alertDialog;
		alertDialog = new AlertDialog.Builder(RunSimpleText.this);
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
		IntrusionsDatabase intDB = IntrusionsDatabase.getInstance(context);
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
