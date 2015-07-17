package hcim.auric.activities.replay;

import hcim.auric.Intrusion;
import hcim.auric.Picture;
import hcim.auric.activities.images.SlideShowIntrusionPictures;
import hcim.auric.data.SessionDatabase;
import hcim.auric.utils.FileManager;
import hcim.auric.utils.LogUtils;
import hcim.auric.utils.OnSwipeTouchListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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

public class Screencast extends Activity {
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

	private int idxPhotos;

	private String sessionID;
	private boolean play;

	private Handler logHandler = new Handler();
	private Runnable logRunnable = new Runnable() {

		@Override
		public void run() {
			if (play) {
				if (explorer.hasNext()) {
					Bitmap b = explorer.nextScreenshot();
					logView.setImageBitmap(b);
					timerTextView.setText(explorer.getCount() + "/" + totalScreenshots);
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
					Bitmap b = intruderList.get(idxPhotos);
					intruderImg.setImageBitmap(b);
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

	private List<Intrusion> listIntrusion;
	private ScreenshotExplorer explorer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fullscreen();
		setContentView(R.layout.run_screencast);

		sessionDB = SessionDatabase.getInstance(this);

		sessionID = getIntent().getStringExtra(EXTRA_ID);
		listIntrusion = sessionDB.getIntrusionsFromSession(sessionID);
		intruderList = getIntrudersPictures();

		LogUtils.debug("session: " + sessionID);
		LogUtils.debug("list: " + listIntrusion.toString());

		explorer = new ScreenshotExplorer(listIntrusion, new FileManager(this));

		totalPhotos = intruderList.size();
		totalScreenshots = explorer.numberOfScreenshots();
		LogUtils.debug("total Screenshots=" + totalScreenshots);

		computeTotalTime();
		initView();
		reset();
	}

	private List<Bitmap> getIntrudersPictures() {
		List<Bitmap> result = new ArrayList<Bitmap>();
		List<Picture> pics;
		for (Intrusion intrusion : listIntrusion) {
			pics = intrusion.getImages();

			if (pics != null) {
				result.addAll(Picture.getBitmapList(pics));
			}
		}
		return result;
	}

	private void play() {
		idxPhotos++;
		photosHandler.postDelayed(photosRunnable, photosPeriod);
		logHandler.postDelayed(logRunnable, logPeriod);
		play = true;
	}

	private void reset() {
		initLog();
		initPhotos();
		idxPhotos = 0;
		explorer.reset();

		playImg.setVisibility(View.VISIBLE);
		trash.setVisibility(View.VISIBLE);
		timerTextView.setVisibility(View.INVISIBLE);
		background.setVisibility(View.VISIBLE);
		play = false;
	}

	private void computeTotalTime() {
		totalTime = totalScreenshots * INTERVAL;
		logPeriod = totalTime / totalScreenshots;
		photosPeriod = totalTime / totalPhotos;
	}

	private void initLog() {
		Bitmap b = explorer.getFirstScreenshot();
		logView.setImageBitmap(b);
		timerTextView.setText(0 + "/" + totalScreenshots);
	}

	private void initPhotos() {
		Bitmap bm = (intruderList == null || totalPhotos == 0) ? null
				: intruderList.get(0);
		if (bm != null) {
			intruderImg.setImageBitmap(bm);
		}
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
					ArrayList<String> list = new ArrayList<String>();
					for (Intrusion i : listIntrusion) {
						list.add(i.getID());
					}
					Intent i = new Intent(Screencast.this,
							SlideShowIntrusionPictures.class);
					i.putExtra(SlideShowIntrusionPictures.EXTRA_ID, list);
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

	private void trashButtonAlertDialog() {
		AlertDialog.Builder alertDialog;
		alertDialog = new AlertDialog.Builder(Screencast.this);
		alertDialog.setTitle("Delete Intrusion Log");
		alertDialog
				.setMessage("Are you sure that you want to delete this intrusion log?");
		alertDialog.setPositiveButton("YES",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						delete();
						Screencast.super.finish();
					}

				});
		alertDialog.setNegativeButton("NO", null);
		alertDialog.show();
	}

	private void delete() {
		FileManager fileManager = new FileManager(this);
		for (Intrusion intrusion : listIntrusion) {
			File dir = new File(fileManager.getIntrusionDirectory(intrusion
					.getID()));
			for (File f : dir.listFiles()) {
				f.delete();
			}
			dir.delete();
		}
		sessionDB.deleteSession(sessionID);
	}
}
