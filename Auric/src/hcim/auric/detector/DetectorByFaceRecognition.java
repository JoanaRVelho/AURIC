package hcim.auric.detector;

import hcim.auric.audit.AuditQueue;
import hcim.auric.audit.AuditTask;
import hcim.auric.audit.IntruderCaptureTask;
import hcim.auric.audit.TaskMessage;
import hcim.auric.recognition.FaceRecognition;
import hcim.auric.recognition.RecognitionResult;
import android.content.Context;
import android.graphics.Bitmap;

public class DetectorByFaceRecognition implements IDetector {

	private AuditQueue queue;
	private FaceRecognition faceRecognition;
	private IntruderCaptureTask intruderCaptureTask;
	private RecognitionResult[] results;

	private int idx;
	private int n;

	public DetectorByFaceRecognition(Context context, AuditQueue queue, int n) {
		this.queue = queue;
		this.idx = 0;
		this.n = n;

		this.results = new RecognitionResult[n];
		this.faceRecognition = FaceRecognition.getInstance(context);
		this.intruderCaptureTask = new IntruderCaptureTask(queue, context);
	}

	@Override
	public void start() {
		intruderCaptureTask.start(false);
	}

	@Override
	public void stop() {
		intruderCaptureTask.stop();
		idx = 0;
	}

	public RecognitionResult newData(Bitmap data) {
		if (intruderCaptureTask == null)
			return null;

		RecognitionResult result = faceRecognition.recognizePicture(data);
		results[idx] = result;

		idx++;

		if (idx == n) {
			boolean intrusion = ownerNotRecognized();
			TaskMessage t = new TaskMessage(AuditTask.ACTION_RESULT);
			t.setIntrusion(intrusion);
			queue.addTaskMessage(t);

			idx = 0;

			intruderCaptureTask.start(true);
		} else {
			intruderCaptureTask.start(false);
		}

		return result;
	}

	/**
	 * Is an intrusion only if a face don't match the owner
	 * 
	 * @return true if a face don't match the owner, false otherwise
	 */
	protected boolean otherPersonRecognized() {
		int notDetected = 0;

		for (RecognitionResult r : results) {
			if (!r.isFaceDetected())
				notDetected++;
			else if (r.matchOwner()) {
				return false;
			}
		}

		if (notDetected == n)
			return false;

		return true;
	}

	/**
	 * Is an intrusion only if a face is not detected or doesn't match the owner
	 * 
	 * @return true if a face don't match the owner, false otherwise
	 */
	protected boolean dontMatchOwner() {
		for (RecognitionResult r : results) {
			if (r.isFaceDetected() && r.matchOwner())
				return false;
		}
		return true;
	}

	/**
	 * Is an intrusion if didn't recognized the owner at least once
	 * 
	 * @return true if didn't recognized the owner at least once, false
	 *         otherwise
	 */
	protected boolean ownerNotRecognized() {
		boolean recognized = false;

		for (RecognitionResult r : results) {
			recognized = recognized | r.isFaceRecognized();
		}

		return !recognized;
	}

	// private boolean validate() {
	// last = !last;
	//
	// return last;
	// }

	@Override
	public String type() {
		return DetectorManager.FACE_RECOGNITION;
	}

	@Override
	public void destroy() {
		stop();
		intruderCaptureTask = null;
	}
}
