package hcim.auric.detector;

import hcim.auric.audit.AuditQueue;
import hcim.auric.database.configs.ConfigurationDatabase;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;

public class DetectorManager {

	public static final String FACE_RECOGNITION = "Face Recognition";
	public static final String WIFI = "Lab Test";
	public static final String APPS = "Face Recognition + Applications";
	private static List<String> detectors;
	private static String DEFAULT = FACE_RECOGNITION;

	static {
		detectors = new ArrayList<String>();
		detectors.add(FACE_RECOGNITION);
	//	detectors.add(WIFI);
	//	detectors.add(APPS);
	}

	public static String getDefault() {
		return DEFAULT;
	}

	public static List<String> getTypesOfDetectors() {
		return detectors;
	}

	public static IDetector getSelectedDetector(Context context,
			AuditQueue queue) {
		ConfigurationDatabase db = ConfigurationDatabase.getInstance(context);
		String type = db.getDetectorType();

		if (type.equals(FACE_RECOGNITION)) {
			int n = db.getNumberOfPicturesPerDetection();
			return new DetectorByFaceRecognition(context, queue, n);
		}

		return null;
	}

}
