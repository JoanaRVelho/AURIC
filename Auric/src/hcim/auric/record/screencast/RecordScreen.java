package hcim.auric.record.screencast;

import hcim.auric.utils.FileManager;
import hcim.auric.utils.LogUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

import android.os.Environment;

public class RecordScreen extends Thread {
	private int idx;

	private volatile boolean stopRecording = false;
	private String sessionID;
	private FileManager fileManager;
	private BufferedWriter buff;

	public RecordScreen(FileManager fileManager, String sessionID) {
		this.fileManager = fileManager;
		this.sessionID = sessionID;
		this.idx = 0;

		File f = new File(fileManager.getIntrusionDirectory(sessionID));
		f.mkdirs();
		
		try {
			buff = new BufferedWriter(new FileWriter(new File(
					Environment.getExternalStoragePublicDirectory(
							Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()
							+ File.separator + "screenshot.txt")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void stopThread() {
		interrupt();
		stopRecording = true;
	}

	@Override
	public void run() {
		String filename;
		while (!stopRecording) {
			// LogUtils.info("stopRecording="+ stopRecording);
			filename = fileManager.getScreenshotPath(sessionID, idx);
			takeScreenshot(filename);
			// LogUtils.info("screenshot " + filename + " taken");
			idx++;
		}
		try {
			buff.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void takeScreenshot(String filename) {
		long t = System.currentTimeMillis();
		try {
			Process sh = Runtime.getRuntime().exec("su", null, null);

			OutputStream os = sh.getOutputStream();
			String cmd = "/system/bin/screencap -p " + filename;
			//LogUtils.info( cmd);
			os.write(cmd.getBytes("ASCII"));
			os.flush();
			os.close();

			sh.waitFor();
		} catch (Exception e) {
			if(e != null && e.getMessage() != null)
				LogUtils.exception(e);
		}
		t = System.currentTimeMillis()-t;
		
		try {
			buff.append("" + t);
			buff.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
