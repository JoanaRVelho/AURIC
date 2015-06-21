package hcim.auric.detector;

import hcim.auric.audit.AuditQueue;
import hcim.auric.database.SettingsPreferences;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

public class DetectorManager {

	public static final String FACE_RECOGNITION = "Face Recognition";
	private static List<String> detectors;
	private static String DEFAULT = FACE_RECOGNITION;

	static {
		detectors = new ArrayList<String>();
		detectors.add(FACE_RECOGNITION);
	}

	public static String getDefault() {
		return DEFAULT;
	}

	public static List<String> getTypesOfDetectors() {
		return detectors;
	}

	public static IDetector getSelectedDetector(Context context,
			AuditQueue queue) {
		SettingsPreferences s = new SettingsPreferences(context);
		String type = s.getDetectorType();

		if (type.equals(FACE_RECOGNITION)) {
			int n = s.getNumberOfPicturesPerDetection();
			return new DetectorByFaceRecognition(context, queue, n);
		}

		return null;
	}

}
