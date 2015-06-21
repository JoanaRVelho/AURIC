package hcim.auric.record.screencast;

import hcim.auric.database.intrusions.SessionDatabase;
import hcim.auric.general_activities.images.SlideShowIntrusionPictures;
import hcim.auric.intrusion.Intrusion;
import hcim.auric.intrusion.Session;
import hcim.auric.recognition.Picture;
import hcim.auric.utils.FileManager;
import hcim.auric.utils.OnSwipeTouchListener;

import java.io.File;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
public class RunScreencast extends Activity {
	public static final String EXTRA_ID = "extra";

	private static final int INTERVAL = 1000;

	private SessionDatabase sessionDB;

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

	private String sessionID, intrusionID;

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

		sessionDB = SessionDatabase.getInstance(this);

		sessionID = getIntent().getStringExtra(EXTRA_ID);
		Session s = sessionDB.getSession(sessionID);
		intrusionID = s.getIntrusionIDs().get(0);
		Intrusion intrusion = sessionDB.getIntrusion(intrusionID);

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
		File dir = new File(fileManager.getSessionDirectory(sessionID));
		if (dir.exists()) {
			totalScreenshots = dir.list().length;
			totalPhotos = intruderList.size();
			totalTime = totalScreenshots * INTERVAL;
			logPeriod = totalTime / totalScreenshots;
			photosPeriod = totalTime / totalPhotos;
		}
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
		String screenshot = fileManager.getScreenshotPath(sessionID, number);
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
		int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
		decorView.setSystemUiVisibility(uiOptions);
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
					i.putExtra(SlideShowIntrusionPictures.EXTRA_ID, sessionID);
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

	protected void trashButtonAlertDialog() {
		AlertDialog.Builder alertDialog;
		alertDialog = new AlertDialog.Builder(RunScreencast.this);
		alertDialog.setTitle("Delete Intrusion Log");
		alertDialog
				.setMessage("Are you sure that you want to delete this intrusion log?");
		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						delete();
						RunScreencast.super.finish();
					}

				});
		alertDialog.setNegativeButton("NO", null);
		alertDialog.show();
	}

	protected void delete() {
		sessionDB.deleteIntrusion(intrusionID);

		File dir = new File(fileManager.getSessionDirectory(sessionID));
		for (File f : dir.listFiles()) {
			f.delete();
		}
		dir.delete();
	}

	// @Override
	// protected void initProgressBar() {
	// bar = (ProgressBar) findViewById(R.id.progressBar1);
	// bar.setVisibility(View.GONE);
	// }

}
