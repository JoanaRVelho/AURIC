package hcim.auric.strategy;

import hcim.auric.Intrusion;
import hcim.auric.Session;
import hcim.auric.detector.IntrusionDetector;
import hcim.auric.service.TaskQueue;
import android.content.Context;

/**
 * During a session, this task is always checking who the operator is and
 * records only if an intrusion is detected.
 * 
 * @author Joana Velho
 * 
 */
public class DeviceSharingStrategy extends AbstractStrategy {

	public DeviceSharingStrategy(Context context, TaskQueue queue) {
		super(context);

		detector = new IntrusionDetector(context, queue);
	}

	@Override
	public void actionOn() {
		detector.start();

		currentSession = new Session(getRecorder().type());
	}

	@Override
	public void actionOff() {
		recorder.stop();
		detector.stop();

		notifier.cancelNotification();

		if (currentSession.isIntrusion()) {
			sessionsDB.insertSession(currentSession);
		}

		sessionsDB.deletePicturesUnknownIntrusion();
		currentSession = null;
		currentIntrusion = null;
	}

	@Override
	public void actionResult(boolean intrusion) {
		if (intrusion) {
			if (currentIntrusion == null) {
				currentIntrusion = new Intrusion();
				sessionsDB.insertIntrusion(currentIntrusion, currentSession.getID());
				sessionsDB.updatePicturesOfIntruder(currentIntrusion
						.getID());

				currentSession.flagAsIntrusion();
				recorder.start(currentIntrusion.getID());
				notifier.notifyUser();
			} else {
				sessionsDB.updatePicturesOfIntruder(currentIntrusion
						.getID());
				notifier.notifyUser();
			}
		} else { // it's device owner
			sessionsDB.deletePicturesUnknownIntrusion();
			if (currentIntrusion != null) {
				recorder.stop();
				notifier.cancelNotification();
			}
		}
	}
}
