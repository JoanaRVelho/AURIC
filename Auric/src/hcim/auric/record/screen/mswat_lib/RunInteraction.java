package hcim.auric.record.screen.mswat_lib;

import hcim.auric.activities.images.IntrusionPicturesSlideShow;
import hcim.auric.database.IntrusionsDatabase;
import hcim.auric.intrusion.Intrusion;
import hcim.auric.recognition.Picture;
import hcim.auric.record.screen.SeverityAdapter;
import hcim.auric.utils.FileManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

import mswat.core.macro.Touch;
import mswat.touch.TouchRecognizer;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.hcim.intrusiondetection.R;

@SuppressLint("InflateParams") public class RunInteraction extends Activity {
	private static final String TAG = "AURIC";
	public static final String EXTRA_ID = "extra";

	private static final int PREV = 0;
	private static final int NEXT = 1;
	private static final int PLAY = 2;
	private static final int STOP = 3;
	private static final int PLAY_ALL = 4;

	private static final int UPDATE_IMAGE = 0;
	private static final int RESET = 1;

	private Spinner spinnerSeverity;
	private Intrusion intrusion;
	private SparseArray<Queue<Touch>> interaction;

	private ImageView img;
	private ImageView background;
	private ImageView playImg;
	private ImageView intruderImg;
	private LinearLayout slideIndicator;
	private int swipeIndex;
	private ArrayList<ImageView> swipeClues;
	private List<Bitmap> intruderList;
	private ImageView trash;
	private TextView timerTextView;

	private int currentImage;
	private FileManager fileManager;
	private IntrusionsDatabase intDB;

	private int navCommand;
	private boolean play;
	private boolean reseting;
	private DrawingView dv;

	private final int interval = 1000; // 1 Second
	private int time = -1;
	private int totalTime = -1;

	private Handler handlerTimer = new Handler();
	private Runnable runnable = new Runnable() {
		public void run() {
			time--;
			timerTextView.setText((totalTime - time) + "/" + totalTime + "s");
			if (time >= 0) {
				screenshotHandler.postDelayed(runnable, interval);

			} else {
				timerTextView.setText("");
			}

		}
	};

	private int intruserImgIndex = 0;
	private int delay;
	private int numberImg;
	private Handler photoHandler = new Handler();
	private Runnable photoRunnable = new Runnable() {
		public void run() {
			intruserImgIndex++;

			Bitmap bm = intruderList.get(intruserImgIndex);
			if (bm != null) {
				intruderImg.setImageBitmap(bm);
			}

			if (intruserImgIndex < numberImg - 1 && time >= 0) {
				photoHandler.postDelayed(photoRunnable, delay);

			} else {
				intruserImgIndex = 0;
			}

		}
	};

	// timestamp of last interaction
	private double lastTouchTime;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		Intent intent = getIntent();

		intDB = IntrusionsDatabase.getInstance(this);

		String intrusionID = intent.getStringExtra(EXTRA_ID);
		intrusion = intDB.getIntrusion(intrusionID);

		intruderList = Picture.getBitmapList(intrusion.getImages());

		dv = new DrawingView(this);
		dv.setZOrderOnTop(true);

		setContentView(R.layout.run_interaction);
		getWindow().addContentView(
				dv,
				new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
						ViewGroup.LayoutParams.FILL_PARENT));

		fileManager = new FileManager(this);

		img = (ImageView) findViewById(R.id.screenshot);

		intruderImg = (ImageView) findViewById(R.id.img_intruder);
		intruderImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (playImg.getVisibility() == View.VISIBLE) {
					Intent i = new Intent(RunInteraction.this,
							IntrusionPicturesSlideShow.class);
					i.putExtra(IntrusionPicturesSlideShow.EXTRA_ID, intrusion.getID());
					startActivity(i);
				}
			}
		});

		Bitmap bm = (intruderList == null || intruderList.size() == 0) ? null
				: intruderList.get(0);
		if (bm != null) {
			intruderImg.setImageBitmap(bm);

		}

		trash = (ImageView) findViewById(R.id.trash);
		trash.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				trashButtonAlertDialog();
			}
		});

		timerTextView = (TextView) findViewById(R.id.timer);
		timerTextView.setVisibility(View.INVISIBLE);

		slideIndicator = (LinearLayout) findViewById(R.id.slideIndicator);
		swipeClues = new ArrayList<ImageView>();
		swipeIndex = 0;

		setControls();
		loadInteraction();
	}

	private void setControls() {
		background = (ImageView) findViewById(R.id.background1);
		playImg = (ImageView) findViewById(R.id.play);
		Bitmap bm = BitmapFactory.decodeFile(fileManager.getScreenshot(
				intrusion.getID(), 0));
		img.setImageBitmap(bm);
		img.setOnTouchListener(new OnSwipeTouchListener(this) {

			public void onSwipeRight() {
				if (currentImage > 0) {
					navCommand = PREV;

					swipeClues.get(swipeIndex).setImageResource(
							R.drawable.circle);
					swipeIndex--;
					if (swipeIndex < 0)
						swipeIndex = 0;
					swipeClues.get(swipeIndex).setImageResource(
							R.drawable.circle_selected);

					boolean result;
					do {
						currentImage--;
						result = runImage(currentImage, -1);
					} while (!result && currentImage > 0);
				}

			}

			public void onSwipeLeft() {
				navCommand = NEXT;

				swipeClues.get(swipeIndex).setImageResource(R.drawable.circle);
				swipeIndex++;
				if (swipeIndex >= swipeClues.size())
					swipeIndex--;
				swipeClues.get(swipeIndex).setImageResource(
						R.drawable.circle_selected);

				boolean result;
				int threshold = 10;
				int aux = 0;
				do {
					currentImage++;
					aux++;
					result = runImage(currentImage, -1);
				} while (!result && aux < threshold);

			}

			public void onSingleTap() {
				if (!play) {
					navCommand = PLAY_ALL;
					runImage(currentImage, -1);
					play = true;
					background.setVisibility(View.INVISIBLE);
					trash.setVisibility(View.INVISIBLE);
					timerTextView.setVisibility(View.VISIBLE);
					playImg.setVisibility(View.INVISIBLE);
					slideIndicator.setVisibility(View.INVISIBLE);

				} else {
					playImg.setVisibility(View.VISIBLE);
					trash.setVisibility(View.VISIBLE);
					timerTextView.setVisibility(View.INVISIBLE);
					slideIndicator.setVisibility(View.VISIBLE);
					background.setVisibility(View.VISIBLE);
					navCommand = STOP;
					runImage(currentImage, -1);
					play = false;
				}

			}

			public boolean onTouch(View v, MotionEvent event) {
				boolean result = gestureDetector.onTouchEvent(event);
				return result;
			}

		});

	}

	private void loadInteraction() {
		File f = new File(fileManager.getIntrusionLog(intrusion.getID()));
		interaction = new SparseArray<Queue<Touch>>();
		Scanner scanner;
		int index = -1;

		try {
			scanner = new Scanner(f);
			Queue<Touch> q = new LinkedList<Touch>();
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String split[] = line.split(",");

				if (split.length > 1) {
					if (Integer.parseInt(split[0]) != index) {
						q = new LinkedList<Touch>();
						index++;
						interaction.put(index, q);
						if (!reseting)
							addSwipeClue();
						q.add(new Touch(Integer.parseInt(split[1]), Integer
								.parseInt(split[2]),
								Integer.parseInt(split[3]), Double
										.parseDouble(split[4])));
					} else
						q.add(new Touch(Integer.parseInt(split[1]), Integer
								.parseInt(split[2]),
								Integer.parseInt(split[3]), Double
										.parseDouble(split[4])));
				}
				lastTouchTime = Double.parseDouble(split[4]);
			}

		} catch (FileNotFoundException e) {
			Log.e(TAG, e.getMessage());
		}

	}

	private void addSwipeClue() {
		ImageView dot = new ImageView(this);
		if (swipeClues.size() != 0)
			dot.setImageResource(R.drawable.circle);
		else
			dot.setImageResource(R.drawable.circle_selected);

		dot.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		slideIndicator.addView(dot);
		swipeClues.add(dot);

	}

	/**
	 * Runs the interaction (changes screenshot, draws touches) Executes comands
	 * next prev
	 * 
	 * @param index
	 * @param lastTime
	 * @return
	 */
	public boolean runImage(final int index, final long lastTime) {
		currentImage = index;

		switch (navCommand) {
		case NEXT:
		case PREV:

			if (!existsImage(index)) {
				return false;
			} else {
				Message msg = screenshotHandler.obtainMessage();
				msg.what = UPDATE_IMAGE;
				msg.obj = index;
				screenshotHandler.sendMessage(msg);
				return true;
			}
		case PLAY:
		case PLAY_ALL:
			if (interaction.get(index) != null
					&& interaction.get(index).peek() != null && time <= -1) {
				setTimerReproduction(index);
				loadIntruserRecord();
			}
			play(index, lastTime);

			break;
		case STOP:
			time = -1;
			reset(true);
			break;
		}

		return true;
	}

	@Override
	public void finish() {
		if (intrusion.isChecked()) {
			super.finish();
		} else {
			markIntrusionAlertDialog();
		}
	}

	private void setTimerReproduction(int index) {
		time = (int) ((lastTouchTime / 1000) - (interaction.get(index).peek()
				.getTimestamp() / 1000));
		totalTime = time;
		handlerTimer.postDelayed(runnable, interval);

	}

	public void reset(boolean image) {
		reseting = true;
		if (image) {
			currentImage = 0;
			Message msg = screenshotHandler.obtainMessage();
			msg.what = RESET;
			msg.obj = 0;
			screenshotHandler.sendMessage(msg);
		}
		loadInteraction();
	}

	public void play(final int index, final long lastTime) {
		final Queue<Touch> q = interaction.get(index);
		if ((navCommand == PLAY && existsImage(index) && lastTime != -1)
				|| navCommand == STOP) {
			Log.d("RESCUE", "STTOOPED");
			return;
		}
		if (q != null && q.size() > 0) {
			Message msg = screenshotHandler.obtainMessage();
			msg.what = UPDATE_IMAGE;
			msg.obj = index;
			screenshotHandler.sendMessage(msg);
		}
		new Thread() {
			public void run() {

				try {
					if (q != null) {
						Touch t;
						long delay = lastTime;
						int x = -1;
						while ((t = q.poll()) != null && navCommand != STOP) {
							if (delay != -1) {

								sleep((long) (t.getTimestamp() - delay));

							}
							delay = (long) t.getTimestamp();
							if (t.getCode() == TouchRecognizer.ABS_MT_POSITION_X) {
								x = t.getValue();
							} else {
								if (t.getCode() == TouchRecognizer.ABS_MT_POSITION_Y) {
									dv.onTouch(x, t.getValue());
								}
							}

						}
						if (index < (interaction.size() - 1)
								&& navCommand != STOP) {
							currentImage++;
							runImage(index + 1, delay);
						} else {
							reset(true);
						}

					}
				} catch (InterruptedException e) {
					Log.e(TAG, e.getMessage());
				}
			}
		}.start();
	}

	/**
	 * Changes the background screenshot
	 */
	@SuppressLint("HandlerLeak")
	final Handler screenshotHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == UPDATE_IMAGE) {
				Bitmap bm = getBitmap(msg);

				if (bm != null) {
					img.setImageBitmap(bm);
					img.invalidate();
				}

			} else {
				if (msg.what == RESET) {
					swipeClues.get(swipeIndex).setImageResource(
							R.drawable.circle);
					swipeIndex = 0;
					swipeClues.get(swipeIndex).setImageResource(
							R.drawable.circle_selected);
					playImg.setVisibility(View.VISIBLE);
					trash.setVisibility(View.VISIBLE);
					timerTextView.setVisibility(View.INVISIBLE);
					slideIndicator.setVisibility(View.VISIBLE);
					background.setVisibility(View.VISIBLE);
					play = false;
				}

				Bitmap bm = getBitmap(msg);

				if (bm != null) {
					img.setImageBitmap(bm);
					img.invalidate();
				}
			}
			super.handleMessage(msg);
		}

		private Bitmap getBitmap(Message msg) {
			int number = (int) ((Integer) (msg.obj));
			String screenshot = fileManager.getScreenshot(intrusion.getID(),
					number);
			return BitmapFactory.decodeFile(screenshot);
		}
	};

	private boolean existsImage(int index) {
		String screenshot = fileManager.getScreenshot(intrusion.getID(), index);
		File f = new File(screenshot);
		return f.exists();
	}

	/**
	 * Starts the reproduction of the recordings of the intruser
	 * 
	 * @param totalTime2
	 */
	private void loadIntruserRecord() {
		numberImg = intruderList.size();
		intruserImgIndex = 0;

		delay = totalTime / numberImg * 1000;
		photoHandler.postDelayed(photoRunnable, delay);
	}

	private View spinnerView() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout view = new LinearLayout(this);
		view = (LinearLayout) inflater.inflate(R.layout.severity, null);

		spinnerSeverity = (Spinner) view.findViewById(R.id.severity_spinner);
		spinnerSeverity.setAdapter(new SeverityAdapter(this));

		return view;
	}

	private void markIntrusionAlertDialog() {
		AlertDialog.Builder alertDialog;
		alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle("Severity of the intrusion");
		alertDialog.setView(spinnerView());
		alertDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						intrusion.setTag((int) spinnerSeverity
								.getSelectedItemPosition());
						intDB.updateIntrusion(intrusion);

						RunInteraction.super.finish();
					}
				});
		alertDialog.show();
	}

	private void trashButtonAlertDialog() {
		AlertDialog.Builder alertDialog;
		alertDialog = new AlertDialog.Builder(this);
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
		intDB.deleteIntrusion(intrusion.getID(), false);

		File dir = new File(
				fileManager.getIntrusionDirectory(intrusion.getID()));
		for (File f : dir.listFiles()) {
			f.delete();
		}
		dir.delete();

		super.finish();
	}

	/**
	 * Handles the circle drawing that represents the touch
	 * 
	 * @author andre
	 * 
	 */
	class DrawingView extends SurfaceView {

		private final SurfaceHolder surfaceHolder;
		private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		private final Paint paintRed = new Paint(Paint.ANTI_ALIAS_FLAG);
		private final Paint paintBlack = new Paint(Paint.ANTI_ALIAS_FLAG);

		public DrawingView(Context context) {
			super(context);
			surfaceHolder = getHolder();
			surfaceHolder.setFormat(PixelFormat.TRANSPARENT);
			paint.setColor(Color.WHITE);
			paint.setStyle(Style.FILL);
			paintBlack.setColor(Color.BLACK);
			paintBlack.setStyle(Style.FILL);
			paintRed.setColor(Color.RED);
			paintRed.setStyle(Style.FILL);
		}

		public boolean onTouch(int x, int y) {
			if (surfaceHolder.getSurface().isValid()) {
				Canvas canvas = surfaceHolder.lockCanvas();
				canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);

				if (y < 0) {
					y = -y;
					canvas.drawCircle(x, y, 16, paintBlack);
					canvas.drawCircle(x, y, 13, paintRed);
				} else {
					canvas.drawCircle(x, y, 16, paintBlack);
					canvas.drawCircle(x, y, 13, paint);
				}
				surfaceHolder.unlockCanvasAndPost(canvas);
			}

			return false;
		}

	}
}
