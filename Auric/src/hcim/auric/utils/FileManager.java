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
	public String getFieldStudyFolder() {
		String result = getPrivateExternal() + File.separator + "field_study";
		
		File dir = new File(result);
		if(!dir.exists())
			dir.mkdir();
		return result;
	}
	
	public String getOpenAppFile(){
		return getFieldStudyFolder() + File.separator + "openFile.txt";
	}

	public boolean isFieldStudyDataStored(String sessionID) {
		File f = new File(getFieldStudyFile(sessionID));
		return f.exists();
	}

	/**
	 * @return field study folder path
	 */
	public String getFieldStudyFile(String sessionID) {
		return getFieldStudyFolder() + File.separator + sessionID;
	}

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

	/**
	 * 
	 * @param sessionID
	 * @return session directory path
	 */
	public String getSessionDirectory(String sessionID) {
		return getSessionsRootDirectory() + File.separator + sessionID;
	}

	/**
	 * 
	 * @param sessionID
	 * @param number
	 *            : screenshot number
	 * @return screenshot path
	 */
	public String getScreenshotPath(String sessionID, int number) {
		return getSessionDirectory(sessionID) + File.separator + number + PNG;
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
