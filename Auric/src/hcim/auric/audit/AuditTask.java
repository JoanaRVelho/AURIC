package hcim.auric.audit;

import hcim.auric.recognition.FaceRecognition;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

public abstract class AuditTask extends AbstractAuditTask {
	public static final String ACTION_OFF = "OFF";
	public static final String ACTION_ON = "ON";

	protected FaceRecognition recognizer;
	protected boolean startLog;

	protected AuditTask(Context c) {
		super(c);

		recognizer = FaceRecognition.getInstance(context);
		startLog = false;
	}

	@Override
	public void run() {
		Log.d(TAG, "AuditTask - start");

		TaskMessage taskMessage;
		String id;
		while (true) {

			if (!queue.isEmpty()) {
				try {
					taskMessage = queue.take();
					id = taskMessage.getID();

					Log.d(TAG, "AuditTask - task = " + id);

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
					Log.e(TAG, "Audit Task - " + e.getMessage());
				}

			}
		}
	}

	public abstract void actionOn();

	public abstract void actionOff();

	public abstract void actionNewPicture(Bitmap capturedFace);
}
