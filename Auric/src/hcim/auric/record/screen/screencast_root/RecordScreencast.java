package hcim.auric.record.screen.screencast_root;

import hcim.auric.utils.FileManager;

import java.io.File;
import java.io.OutputStream;

import android.util.Log;

public class RecordScreencast extends Thread{
	private static final String TAG = "AURIC";

	private int idx;

	private volatile boolean stopRecording = false;
	private String intrusionID;
	private FileManager fileManager;

	public RecordScreencast(FileManager fileManager, String intrusionID) {
		this.fileManager = fileManager;
		this.intrusionID = intrusionID;
		this.idx = 0;

		File f = new File(fileManager.getIntrusionDirectory(intrusionID));
		f.mkdirs();
	}

	public void stopThread() {
		interrupt();
		stopRecording = true;
	}

	@Override
	public void run() {
		String filename;
		while (!stopRecording) {
			Log.i(TAG, "stopRecording="+ stopRecording);
			filename = fileManager.getScreenshot(intrusionID, idx);
			takeScreenshot(filename);
			Log.i(TAG, "screenshot " + filename + " taken");
			idx++;
		}
	}

	private void takeScreenshot(String filename) {
		try {
			Process sh = Runtime.getRuntime().exec("su", null, null);

			OutputStream os = sh.getOutputStream();
			String cmd = "/system/bin/screencap -p " + filename;
			Log.i(TAG, cmd);
			os.write(cmd.getBytes("ASCII"));
			os.flush();
			os.close();

			sh.waitFor();
		} catch (Exception e) {
			if(e != null && e.getMessage() != null)
				Log.e(TAG, e.getMessage());
		}
	}
}
