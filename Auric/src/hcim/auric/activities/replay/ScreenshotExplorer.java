package hcim.auric.activities.replay;

import hcim.auric.Intrusion;
import hcim.auric.utils.FileManager;

import java.io.File;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ScreenshotExplorer {

	private FileManager fileManager;
	private List<Intrusion> list;
	private int totalScreenshots;
	private int countScreenshots;

	private int idx; // intrusion idx
	private int number; // screenshot number

	public ScreenshotExplorer(List<Intrusion> intrusionsID, FileManager manager) {
		this.fileManager = manager;
		this.list = intrusionsID;

		this.countScreenshots = 0;
		this.totalScreenshots = countScreenshots();

		idx = 0;
		number = 0;
	}

	public int numberOfScreenshots() {
		return totalScreenshots;
	}

	private int countScreenshots() {
		File dir;
		int total = 0;
		for (Intrusion i : list) {
			dir = new File(fileManager.getIntrusionDirectory(i.getID()));
			total += dir.list().length;
		}
		return total;
	}

	public int getCount() {
		return countScreenshots;
	}

	public boolean hasNext() {
		return countScreenshots < totalScreenshots;
	}

	public Bitmap nextScreenshot() {
		String intrusionID = list.get(idx).getID();
		String path = fileManager.getScreenshotPath(intrusionID, number);

		File f = new File(path);
		if (!f.exists()) {
			idx++;
			number = 0;
			path = fileManager.getScreenshotPath(intrusionID, number);
		}

		number++;
		countScreenshots++;

		return BitmapFactory.decodeFile(path);
	}

	public Bitmap getFirstScreenshot() {
		String path = fileManager.getScreenshotPath(list.get(0).getID(), 0);
		return BitmapFactory.decodeFile(path);
	}

	public void reset() {
		idx = 0;
		number = 0;
	}
}
