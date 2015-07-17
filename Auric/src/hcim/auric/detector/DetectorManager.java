package hcim.auric.detector;

import hcim.auric.data.SettingsPreferences;
import hcim.auric.service.TaskQueue;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

/**
 * Manages different intrusion detectors.
 * 
 * @author Joana Velho
 * 
 */
public class DetectorManager {
	public static final String FACE_RECOGNITION = "Face Recognition";
	private static List<String> detectors;
	private static String DEFAULT = FACE_RECOGNITION;

	static {
		detectors = new ArrayList<String>();
		detectors.add(FACE_RECOGNITION);
	}

	/**
	 * Gets default type of intrusion detector
	 * 
	 * @return default intrusion detector
	 */
	public static String getDefault() {
		return DEFAULT;
	}

	/**
	 * Lists supported intrusion detectors
	 * 
	 * @return list of supported detectors
	 */
	public static List<String> getTypesOfDetectors() {
		return detectors;
	}

	/**
	 * Creates an intrusion detector selected by user.
	 * 
	 * @param context
	 *            : application context
	 * @param queue
	 *            : task queue
	 * @return intrusion detector selected
	 */
	public static IDetector getSelectedDetector(Context context, TaskQueue queue) {
		SettingsPreferences s = new SettingsPreferences(context);
		String type = s.getDetectorType();

		if (type.equals(FACE_RECOGNITION)) {
			return new IntrusionDetector(context, queue);
		}
		return null;
	}
}
