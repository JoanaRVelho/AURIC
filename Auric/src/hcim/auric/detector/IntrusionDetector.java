package hcim.auric.detector;

import hcim.auric.camera.FrontPictureCallback;
import hcim.auric.data.SessionDatabase;
import hcim.auric.data.SettingsPreferences;
import hcim.auric.recognition.FaceRecognition;
import hcim.auric.recognition.RecognitionResult;
import hcim.auric.service.TaskMessage;
import hcim.auric.service.TaskQueue;
import hcim.auric.utils.Converter;

import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Intrusion Detector that uses face recognition to identify third-parties. It
 * is also an Observer of {@link FrontPictureCallback}
 * 
 * @author Joana Velho
 * 
 */
public class IntrusionDetector implements IDetector, Observer {
	private TaskQueue queue;
	private FaceRecognition faceRecognition;
	private IntruderCapture capture;
	private SessionDatabase sessionDB;

	/**
	 * Constructor
	 * 
	 * @param context
	 *            : application context
	 * @param queue
	 *            : message queue where the intrusion result is going to be sent
	 */
	public IntrusionDetector(Context context, TaskQueue queue) {
		this.queue = queue;
		FrontPictureCallback callback = new FrontPictureCallback();
		callback.addObserver(this);

		this.faceRecognition = FaceRecognition.getInstance(context);
		SettingsPreferences settings = new SettingsPreferences(context);
		sessionDB = SessionDatabase.getInstance(context);

		this.capture = new IntruderCapture(callback, settings.getCameraPeriod());
	}

	@Override
	public void start() {
		capture.start(false);
	}

	@Override
	public void stop() {
		capture.stop();
	}

	@Override
	public String type() {
		return DetectorManager.FACE_RECOGNITION;
	}

	@Override
	public void destroy() {
		stop();
		capture = null;
	}

	@Override
	public void update(Observable observable, Object data) {
		if (capture == null)
			return;

		byte[] array = (byte[]) data;

		Bitmap original = Converter.decodeCameraDataToBitmap(array);
		Bitmap small = Converter.decodeCameraDataToSmallBitmap(array);

		RecognitionResult result = faceRecognition.recognizePicture(original);
		sessionDB.insertPictureOfIntruder(small, result);

		TaskMessage t = new TaskMessage(TaskMessage.ACTION_RESULT);
		t.setIntrusion(!result.isFaceRecognized());
		queue.addTaskMessage(t);

		capture.start(true);
	}
}
