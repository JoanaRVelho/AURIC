package hcim.auric.audit;

import hcim.auric.database.intrusions.SessionDatabase;
import hcim.auric.intrusion.Interaction;
import hcim.auric.intrusion.Intrusion;
import hcim.auric.intrusion.Session;
import hcim.auric.recognition.FaceRecognition;
import hcim.auric.recognition.RecognitionResult;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

public class VerboseAuditTask extends AuditTask {

	public static final String ACTION_OFF = "OFF";
	public static final String ACTION_ON = "ON";

	protected FaceRecognition recognizer;
	protected boolean startLog;
	protected Session currentSession;
	protected SessionDatabase sessionDB;

	public VerboseAuditTask(Context context) {
		super(context);

		sessionDB = SessionDatabase.getInstance(context);
		recognizer = FaceRecognition.getInstance(context);
		startLog = false;
	}

	@Override
	public void run() {
		Log.d(TAG, "AuditTaskVerbose - start");

		TaskMessage taskMessage;
		String id;
		while (true) {

			if (!queue.isEmpty()) {
				try {
					taskMessage = queue.take();
					id = taskMessage.getID();

					Log.d(TAG, "AuditTaskVerbose - task = " + id);

					if (id.equals(ACTION_OFF)) {
						screenOff = true;
						actionOff();
					}
					if (id.equals(ACTION_ON)) {
						screenOff = false;
						actionOn();
					}
					if (id.equals(ACTION_NEW_PICTURE)) {
						actionNewPicture(taskMessage.getPic());
					}

				} catch (InterruptedException e) {
					Log.e(TAG, "AuditTaskVerbose - " + e.getMessage());
				}

			}
		}
	}

	public void actionOn() {
		if (currentSession == null) {
			currentSession = new Session();
			camera.takePicture();
		}
	}

	public void actionOff() {
		if (currentSession != null) {
			stopInt();

			stopTimerTask();
			sessionDB.insertSession(currentSession);
			currentSession = null;
		}
	}

	public void actionNewPicture(Bitmap capturedFace) {
		RecognitionResult result = recognizer.recognizePicture(capturedFace);
		boolean intrusion = !result.isFaceRecognized();

		Log.d(TAG, "AuditTaskVerbose - intrusion=" + intrusion);

		if (currentIntrusion == null) { // first picture
			startTimerTask(true);
			startInt(capturedFace, intrusion);
		} else {
			boolean xor = intrusion ^ (currentIntrusion instanceof Interaction);
			if (!xor) {
				stopInt();
				startInt(capturedFace, intrusion);
			} else {
				intrusionsDB.insertPictureOfTheIntruder(
						currentIntrusion.getID(), capturedFace);
			}
		}
	}

	private void stopInt() {
		log.stopLogging();
		intrusionsDB.insertIntrusionData(currentIntrusion);

		if (!(currentIntrusion instanceof Interaction))
			notifier.cancelNotification();

		currentIntrusion = null;
	}

	private void startInt(Bitmap capturedFace, boolean intrusion) {
		if (intrusion) {
			Log.d(TAG, "AuditTaskVerbose - new intrusion");
			currentIntrusion = new Intrusion(log.type());
			notifier.notifyUser();
		} else {
			Log.d(TAG, "AuditTaskVerbose - new interaction");
			currentIntrusion = new Interaction(log.type());
		}

		intrusionsDB.insertPictureOfTheIntruder(currentIntrusion.getID(),
				capturedFace);

		log.startLogging(currentIntrusion.getID());
		intrusionsDB.insertPictureOfTheIntruder(currentIntrusion.getID(),
				capturedFace);
		currentSession.addInteraction(currentIntrusion);
	}

}
