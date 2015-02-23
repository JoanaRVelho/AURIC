package hcim.auric.detector;

import hcim.auric.audit.AuditQueue;
import hcim.auric.audit.IAuditTask;
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
	}

	public RecognitionResult newData(Bitmap data) {
		RecognitionResult result = faceRecognition.recognizePicture(data);
		// recognized = recognized | result.isFaceRecognized();
		results[idx] = result;

		idx++;

		if (idx == n) {
			boolean intrusion = validate();
			TaskMessage t = new TaskMessage(IAuditTask.ACTION_RESULT);
			t.setIntrusion(intrusion);
			queue.addTaskMessage(t);

			idx = 0;
			intruderCaptureTask.start(true);
		} else {
			intruderCaptureTask.start(false);
		}

		return result;
	}

	private boolean validate() {
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

	@Override
	public String type() {
		return DetectorManager.FACE_RECOGNITION;
	}
}
