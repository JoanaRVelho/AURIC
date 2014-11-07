package hcim.auric.utils;

import java.io.File;

import android.content.Context;

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
	 * @return opencv's directory path 
	 */
	public String getOpenCVDirectory() {
		return getPrivateExternal() + File.separator + "face_recognition";
	}


	private String getIntrusionRootDirectory() {
		return getPrivateExternal() + File.separator + "intrusions";
	}


	/**
	 * 
	 * @param intrusionID
	 * @return intrusion directory path 
	 */
	public String getIntrusionDirectory(String intrusionID) {
		return getIntrusionRootDirectory() + File.separator + intrusionID;
	}

	/**
	 * 
	 * @param intrusionID 
	 * @param number : screenshot number
	 * @return screenshot path
	 */
	public String getScreenshot(String intrusionID, int number) {
		return getIntrusionDirectory(intrusionID) + File.separator + number
				+ PNG;
	}
	
	/**
	 * 
	 * @param intrusionID 
	 * @return log path
	 */
	public String getIntrusionLog(String intrusionID){
		return getIntrusionDirectory(intrusionID) + File.separator + "log"; 
	}
}
