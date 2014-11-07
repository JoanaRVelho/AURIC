package hcim.auric.record.screen.mswat_lib;

import hcim.auric.utils.FileManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeoutException;

import mswat.core.CoreController;
import mswat.core.activityManager.Node;
import mswat.core.macro.Touch;
import mswat.interfaces.ContentReceiver;
import mswat.interfaces.IOReceiver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.SparseArray;

import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.exceptions.RootDeniedException;
import com.stericson.RootTools.execution.Command;

public class RecordInteraction extends BroadcastReceiver implements IOReceiver,
		ContentReceiver {
	private static final String TAG = "AURIC";

	private static final long SCREENSHOT_PERIOD = 1000;

	private SparseArray<Queue<Touch>> interaction;
	private int idContent;
	private int idIO;
	private int monitor;
	private int idScreenShot = -1;
	private int idScreenTouch = 0;
	private long time;
	private FileManager fileManager;

	private boolean recording;
	private String intrusionID;

	@Override
	public int registerIOReceiver() {
		return CoreController.registerIOReceiver(this);
	}

	@Override
	public void onUpdateIO(int device, int type, int code, int value,
			int timestamp) {

		if (device == monitor) {

			if (code == 54 && value > CoreController.M_HEIGHT)
				interaction.get(idScreenTouch).add(
						new Touch(type, code,
								(int) (-CoreController.M_HEIGHT + 10),
								timestamp));
			else
				interaction.get(idScreenTouch).add(
						new Touch(type, code, value, timestamp));
		}

	}

	@Override
	public void onTouchReceived(int type) {

	}

	@Override
	public int registerContentReceiver() {
		return CoreController.registerContentReceiver(this);
	}

	@Override
	public void onUpdateContent(ArrayList<Node> content) {
		if (time == -1)
			time = System.currentTimeMillis();
		else {
			if ((System.currentTimeMillis() - time) < SCREENSHOT_PERIOD) {
				return;
			} else
				time = System.currentTimeMillis();
		}
		screenShot();
	}

	@Override
	public int getType() {
		return DESCRIBABLE;
	}

	private void screenShot() {
		idScreenShot++;
		interaction.put(idScreenShot, new LinkedList<Touch>());
		new Thread(new Runnable() {
			public void run() {

				if (RootTools.isAccessGiven()) { /* magic root code here */
				}
				try {
					if (idScreenShot != -1) {

						Command command = new Command(0,
								"/system/bin/screencap -p " + fileManager.getScreenshot(intrusionID, idScreenShot)) {
							@Override
							public void output(int id, String line) {

							}

							@Override
							public void commandCompleted(int arg0, int arg1) {
								idScreenTouch = idScreenShot;
							}

							@Override
							public void commandOutput(int arg0, String arg1) {

							}

							@Override
							public void commandTerminated(int arg0, String arg1) {
								idScreenTouch = idScreenShot;
							}
						};
						RootTools.getShell(true).add(command);
					}
				} catch (IOException e) {
					Log.e(TAG, e.getMessage());
				} catch (TimeoutException e) {
					Log.e(TAG, e.getMessage());
				} catch (RootDeniedException e) {
					Log.e(TAG, e.getMessage());
				}
			}
		}).start();

	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("swat_interaction")) {
			boolean run = intent.getBooleanExtra("logging", false);
			if (run) {
				recording = true;
				intrusionID = intent.getStringExtra("timestamp");

				fileManager = new FileManager(context);
//				filepath = context.getExternalFilesDir(null)
//						+ "/intlog/intrusions/" + folder;
				File f = new File(fileManager.getIntrusionDirectory(intrusionID));
				f.mkdirs();

				time = -1;
				monitor = CoreController.monitorTouch();

				interaction = new SparseArray<Queue<Touch>>();
				screenShot();

				idContent = registerContentReceiver();
				idIO = registerIOReceiver();
				Log.d("RESCUE", "REGISTER ID IO:" + idIO);
				Log.d("RESCUE", "REGISTER ID Content:" + idContent);

			} else {
				writeLog();
				CoreController.unregisterIOReceiver(idIO);
				CoreController.unregisterContent(idContent);
				Log.d("RESCUE", "UNREGISTER ID IO:" + idIO);
				Log.d("RESCUE", "UNREGISTER ID Content:" + idContent);
				idScreenShot = -1;
				idScreenTouch = 0;

			}

		} else {
			if (recording) {
				writeLog();
				CoreController.unregisterIOReceiver(idIO);
				CoreController.unregisterContent(idContent);
				Log.d("RESCUE", "UNREGISTER ID IO:" + idIO);
				Log.d("RESCUE", "UNREGISTER ID Content:" + idContent);
				idScreenShot = -1;
				idScreenTouch = 0;
			}

		}

	}

	private void writeLog() {
		Queue<Touch> touches;
		ArrayList<String> interactions = new ArrayList<String>();
		for (int i = 0; i <= idScreenShot; i++) {
			touches = interaction.get(i);
			if (touches == null)
				return;
			Touch t;
			while ((t = touches.poll()) != null) {

				interactions.add(i + "," + t.getType() + "," + t.getCode()
						+ "," + t.getValue() + "," + t.getTimestamp());
			}
		}

		File log = new File(fileManager.getIntrusionLog(intrusionID));
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(log));

			for (String s : interactions) {
				writer.write(s);
				writer.newLine();
			}
			writer.flush();
			writer.close();

		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
	}

}
