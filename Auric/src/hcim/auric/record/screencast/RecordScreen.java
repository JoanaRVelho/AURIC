package hcim.auric.record.screencast;

import hcim.auric.utils.FileManager;
import hcim.auric.utils.LogUtils;

import java.io.File;
import java.io.OutputStream;

public class RecordScreen extends Thread{
	private int idx;

	private volatile boolean stopRecording = false;
	private String sessionID;
	private FileManager fileManager;

	public RecordScreen(FileManager fileManager, String sessionID) {
		this.fileManager = fileManager;
		this.sessionID = sessionID;
		this.idx = 0;

		File f = new File(fileManager.getSessionDirectory(sessionID));
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
			LogUtils.info("stopRecording="+ stopRecording);
			filename = fileManager.getScreenshotPath(sessionID, idx);
			takeScreenshot(filename);
			LogUtils.info("screenshot " + filename + " taken");
			idx++;
		}
	}

	private void takeScreenshot(String filename) {
		try {
			Process sh = Runtime.getRuntime().exec("su", null, null);

			OutputStream os = sh.getOutputStream();
			String cmd = "/system/bin/screencap -p " + filename;
			LogUtils.info( cmd);
			os.write(cmd.getBytes("ASCII"));
			os.flush();
			os.close();

			sh.waitFor();
		} catch (Exception e) {
			if(e != null && e.getMessage() != null)
				LogUtils.exception(e);
		}
	}
}
