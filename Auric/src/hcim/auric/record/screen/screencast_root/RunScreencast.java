package hcim.auric.record.screen.screencast_root;

import hcim.auric.activities.images.SlideShowIntrusionPictures;
import hcim.auric.database.IntrusionsDatabase;
import hcim.auric.recognition.Picture;
import hcim.auric.record.screen.RunInteraction;
import hcim.auric.utils.FileManager;
import hcim.auric.utils.OnSwipeTouchListener;

import java.io.File;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hcim.intrusiondetection.R;

@SuppressLint("InflateParams")
public class RunScreencast extends RunInteraction {
	protected static final String TAG = "AURIC";
	public static final String EXTRA_ID = "extra";

	private static final int INTERVAL = 1000;

	private ImageView logView;
	private ImageView background;
	private ImageView playImg;
	private ImageView intruderImg;

	private Button trash;
	private TextView timerTextView;

	private List<Bitmap> intruderList;
	private FileManager fileManager;

	private int idxPhotos;
	private int idxLog;

	private boolean play;

	private Handler logHandler = new Handler();
	private Runnable logRunnable = new Runnable() {

		@Override
		public void run() {
			if (play) {
				if (idxLog < totalScreenshots) {
					updateLog();
					idxLog++;
					photosHandler.postDelayed(this, logPeriod);
				} else { // se terminou
					play = false;
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
					reset();
				}
		}
	};
	private int totalTime;
	private int totalScreenshots;
	private int totalPhotos;
	private int logPeriod;
	private int photosPeriod;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fullscreen();
		setContentView(R.layout.run_screencast);

		intDB = IntrusionsDatabase.getInstance(this);

		String intrusionID = getIntent().getStringExtra(EXTRA_ID);
		intrusion = IntrusionsDatabase.getInstance(this).getIntrusion(
				intrusionID);

		intruderList = Picture.getBitmapList(intrusion.getImages());
		fileManager = new FileManager(this);

		computeTotalTime();
		initView();
		reset();
	}

	private void play() {
		idxPhotos++;
		idxLog++;
		photosHandler.postDelayed(photosRunnable, photosPeriod);
		logHandler.postDelayed(logRunnable, logPeriod);
		play = true;
	}

	private void reset() {
		initLog();
		initPhotos();
		idxPhotos = 0;
		idxLog = 0;

		playImg.setVisibility(View.VISIBLE);
		trash.setVisibility(View.VISIBLE);
		timerTextView.setVisibility(View.INVISIBLE);
		background.setVisibility(View.VISIBLE);
		play = false;
	}

	private void computeTotalTime() {
		File dir = new File(
				fileManager.getIntrusionDirectory(intrusion.getID()));
		totalScreenshots = dir.list().length;
		totalPhotos = intruderList.size();
		totalTime = totalScreenshots * INTERVAL;
		logPeriod = totalTime / totalScreenshots;
		photosPeriod = totalTime / totalPhotos;

	}

	private void initLog() {
		Bitmap b = getScreenshot(0);
		logView.setImageBitmap(b);
		timerTextView.setText(0 + "/" + totalScreenshots);
	}

	private void updateLog() {
		Bitmap b = getScreenshot(idxLog);
		logView.setImageBitmap(b);
		timerTextView.setText(idxLog + "/" + totalScreenshots);
	}

	private Bitmap getScreenshot(int number) {
		String screenshot = fileManager
				.getScreenshot(intrusion.getID(), number);
		return BitmapFactory.decodeFile(screenshot);
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
		logView = (ImageView) findViewById(R.id.screenshot);
		logView.setOnTouchListener(new OnSwipeTouchListener(this) {

			public void onSingleTap() {
				if (!play) {
					play = true;
					background.setVisibility(View.INVISIBLE);
					trash.setVisibility(View.INVISIBLE);
					timerTextView.setVisibility(View.VISIBLE);
					playImg.setVisibility(View.INVISIBLE);
					play();
				} else {
					playImg.setVisibility(View.VISIBLE);
					trash.setVisibility(View.VISIBLE);
					timerTextView.setVisibility(View.INVISIBLE);
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
					Intent i = new Intent(RunScreencast.this,
							SlideShowIntrusionPictures.class);
					i.putExtra(SlideShowIntrusionPictures.EXTRA_ID,
							intrusion.getID());
					i.putExtra(SlideShowIntrusionPictures.EXTRA_ID_IDX, 0);
					startActivity(i);
				}
			}
		});

		initPhotos();

		trash = (Button) findViewById(R.id.trash);
		trash.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				trashButtonAlertDialog();
			}
		});

		timerTextView = (TextView) findViewById(R.id.timer);
		timerTextView.setVisibility(View.INVISIBLE);
	}

	// private View spinnerView() {
	// LayoutInflater inflater = (LayoutInflater)
	// getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	// LinearLayout view = new LinearLayout(this);
	// view = (LinearLayout) inflater.inflate(R.layout.severity, null);
	//
	// spinnerSeverity = (Spinner) view.findViewById(R.id.severity_spinner);
	// spinnerSeverity.setAdapter(new SeverityAdapter(this));
	//
	// return view;
	// }
	//
	// private void markIntrusionAlertDialog() {
	// AlertDialog.Builder alertDialog;
	// alertDialog = new AlertDialog.Builder(this);
	// alertDialog.setTitle("Severity of the intrusion");
	// alertDialog.setView(spinnerView());
	// alertDialog.setPositiveButton("OK",
	// new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog, int which) {
	// intrusion.setTag((int) spinnerSeverity
	// .getSelectedItemPosition());
	// intDB.updateIntrusion(intrusion);
	//
	// RunScreencast.super.finish();
	// }
	// });
	// alertDialog.show();
	// }

	@Override
	protected void delete() {
		intDB.deleteIntrusion(intrusion.getID(), false);

		File dir = new File(
				fileManager.getIntrusionDirectory(intrusion.getID()));
		for (File f : dir.listFiles()) {
			f.delete();
		}
		dir.delete();

		super.finish();
	}

	// @Override
	// protected void initProgressBar() {
	// bar = (ProgressBar) findViewById(R.id.progressBar1);
	// bar.setVisibility(View.GONE);
	// }

}
