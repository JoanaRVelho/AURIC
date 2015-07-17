package hcim.auric.utils;

import java.io.File;

import android.content.Context;
import android.os.StatFs;

public class FileManager {

	private static final String PNG = ".png";
	public static final String TAG = "AURIC";

	private String base;

	public FileManager(Context c) {
		this.base = c.getExternalFilesDir(null).toString();
	}

	/**
	 * @return private external path
	 */
	public String getPrivateExternal() {
		return base;
	}

	/**
	 * @return field study folder path
	 */

	/**
	 * @return opencv's directory path
	 */
	public String getOpenCVDirectory() {
		return getPrivateExternal() + File.separator + "face_recognition";
	}

	/**
	 * 
	 * @return base path
	 */
	private String getSessionsRootDirectory() {
		return getPrivateExternal() + File.separator + "sessions";
	}

	public void deleteSessions() {
		File root = new File(getSessionsRootDirectory());
		File[] intrusionDirs = root.listFiles();
		if (intrusionDirs != null) {
			File[] screenshots;
			for (File dir : intrusionDirs) {
				screenshots = dir.listFiles();
				if (screenshots != null) {
					for (File png : screenshots) {
						png.delete();
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param intrusionID
	 * @return intrusion directory path
	 */
	public String getIntrusionDirectory(String intrusionID) {
		return getSessionsRootDirectory() + File.separator + intrusionID;
	}

	/**
	 * 
	 * @param intrusionID
	 * @param number
	 *            : screenshot number
	 * @return screenshot path
	 */
	public String getScreenshotPath(String intrusionID, int number) {
		return getIntrusionDirectory(intrusionID) + File.separator + number
				+ PNG;
	}

	public boolean hasSpaceAvailableInOpenCVDirectory(int sizeInBytes) {
		return hasSpaceAvailable(getOpenCVDirectory(), sizeInBytes);
	}

	public boolean hasSpaceAvailableInScreenshotsDirectory(int sizeInBytes) {
		return hasSpaceAvailable(getOpenCVDirectory(), sizeInBytes);
	}

	/**
	 * Checks if there is enough space to store data
	 * 
	 * @param directory
	 *            : directory path where the data is going to be stored
	 * @param sizeInBytes
	 *            : size of data to be stored in bytes
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private static boolean hasSpaceAvailable(String directory, int sizeInBytes) {
		StatFs stat = new StatFs(directory);
		long bytesAvailable = (long) stat.getBlockSize()
				* (long) stat.getAvailableBlocks();
		return bytesAvailable < sizeInBytes;
	}
}
