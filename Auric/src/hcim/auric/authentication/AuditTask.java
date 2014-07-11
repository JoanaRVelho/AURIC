package hcim.auric.authentication;

import hcim.auric.camera.CameraManager;
import hcim.auric.camera.FrontPictureCallback;
import hcim.auric.database.IntrusionsDatabase;
import hcim.auric.intrusion.Intrusion;
import hcim.auric.recognition.FaceRecognition;

import java.util.concurrent.LinkedBlockingQueue;

import android.graphics.Bitmap;
import android.util.Log;

public class AuditTask extends Thread {

	public static final String ACTION_OFF = "OFF";
	public static final String ACTION_ON = "ON";
	public static final String ACTION_INTRUSION_DETECTION = "DETECT";

	private boolean startLog;
	private CameraManager camera;
	private Intrusion currentIntrusion;
	private FaceRecognition recognizer;
	//private PeriodicAuthentication periodicThread;

	private LinkedBlockingQueue<TaskMessage> queue;

	public AuditTask() {
		Log.d("SCREEN", "start task");
		queue = new LinkedBlockingQueue<TaskMessage>();
		startLog = false;
		currentIntrusion = null;
		camera = new CameraManager(new FrontPictureCallback(this));
		recognizer = FaceRecognition.getInstance();
	}

	@Override
	public void run() {
		TaskMessage task;
		String id;
		while (true) {
			if (!queue.isEmpty()) {
				try {
					task = queue.take();
					id = task.getID();
					
					Log.d("SCREEN", "task="+id);

					if (id.equals(ACTION_OFF)) {
						actionOff();
					}
					if (id.equals(ACTION_ON)) {
						actionOn();
					}
					if (id.equals(ACTION_INTRUSION_DETECTION)) {
						actionIntrusionDetected(task.getPic());
					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		}
	}

	public void actionIntrusionDetected(Bitmap capturedFace) {
		Log.d("SCREEN","action I D" );
		boolean intrusion = !recognizer.recognizePicture(capturedFace);

		Log.d("SCREEN", "intrusion=" + intrusion);

		if (intrusion) {
			if (!startLog) { // iniciar auditoria
				startLog = true;
				
				Log.d("SCREEN", "iniciar auditoria - new Intrusion");
				currentIntrusion = new Intrusion();
				currentIntrusion.addImage(capturedFace);
				currentIntrusion.startLogging();
				Log.d("SCREEN", "start logging");

			//	 periodicThread = new PeriodicAuthentication(camera);
			//	 periodicThread.start();
			} else { // continuar auditoria
				Log.d("SCREEN", "continuar auditoria");
				if (currentIntrusion == null) {
					return;
				}
				currentIntrusion.addImage(capturedFace);
			}
		} else { 
			if (startLog) {  // parar auditoria
				Log.d("SCREEN", "parar auditoria");
				if (currentIntrusion == null) {
					return;
				}
				currentIntrusion.stopLogging();
				IntrusionsDatabase.removeIntrusion(currentIntrusion);
				currentIntrusion = null;

				startLog = false;

				Log.d("SCREEN", "stop logging");
			}
		}
	}

	public void actionOff() {
		if (startLog) {
//			if (periodicThread != null) {
//				periodicThread.stop();
//				periodicThread = null;
//				Log.d("SCREEN", "stop periodic thread");
//			}

			currentIntrusion.stopLogging();

			Intrusion i = currentIntrusion;
			currentIntrusion = null;
			IntrusionsDatabase.addIntrusion(i);
			Log.d("SCREEN", "stop logging");
		}

		startLog = false;
		Log.d("SCREEN", "start log=" + startLog);
	}

	public void actionOn() {
		if (!startLog) {
			Log.d("SCREEN", "take picture");
			camera.takePicture();
		}
	}

	public void addTaskMessage(String task, Bitmap p) {
		TaskMessage m = new TaskMessage(task, p);
		queue.add(m);
	}
}
