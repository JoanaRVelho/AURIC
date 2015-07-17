package hcim.auric.strategy;

import hcim.auric.Intrusion;
import hcim.auric.Session;
import hcim.auric.detector.IntrusionDetector;
import hcim.auric.service.TaskQueue;
import hcim.auric.utils.LogUtils;
import android.content.Context;

/**
 * Operating Strategy that records all interaction regardless the intrusion
 * detection result.
 * 
 * @author Joana Velho
 * 
 */
public class GreedyStrategy extends AbstractStrategy {
	private boolean first;

	public GreedyStrategy(Context context, TaskQueue queue) {
		super(context);
		detector = new IntrusionDetector(context, queue);
	}

	public void actionOn() {
		if (currentSession == null) {
			detector.start();
			currentSession = new Session(recorder.type());
			currentIntrusion = new Intrusion();
			recorder.start(currentIntrusion.getID());
			first = true;
			LogUtils.debug("session = " + currentSession.getID());
			LogUtils.debug("new intrusion = " + currentIntrusion.getID());
		}
	}

	public void actionOff() {
		detector.stop();

		if (!first) {// if first intrusion result won't appear
			if (currentSession != null) {
				stopInt();

				sessionsDB.insertSession(currentSession);
				LogUtils.debug(currentSession.toString());
			}
		}
		currentSession = null;
		currentIntrusion = null;
		sessionsDB.deletePicturesUnknownIntrusion();
	}

	@Override
	public void actionResult(boolean intrusion) {
		LogUtils.debug("intrusion=" + intrusion);

		if (first) { // first detection result
			first = false;
			if (intrusion) {
				notifier.notifyUser();
				currentSession.flagAsIntrusion();
			} else {
				currentIntrusion.markAsFalse();
			}

			sessionsDB.updatePicturesOfIntruder(currentIntrusion.getID());
		} else {
			boolean xor = intrusion ^ currentIntrusion.isFalseIntrusion();
			if (!xor) {
				stopInt();
				startInt(intrusion);
			} else {
				sessionsDB.updatePicturesOfIntruder(currentIntrusion
						.getID());
			}
		}
	}

	private void startInt(boolean intrusion) {
		currentIntrusion = new Intrusion();
		if (intrusion) {
			notifier.notifyUser();
			currentSession.flagAsIntrusion();
		} else {
			currentIntrusion.markAsFalse();
		}

		recorder.start(currentIntrusion.getID());
		sessionsDB.updatePicturesOfIntruder(currentIntrusion.getID());
		
		LogUtils.debug("new intrusion = " + currentIntrusion.getID());
	}

	private void stopInt() {
		recorder.stop();

		if (currentIntrusion != null) {
			sessionsDB.insertIntrusion(currentIntrusion, currentSession.getID());

			if (!currentIntrusion.isFalseIntrusion()) {
				notifier.cancelNotification();
			}
		}
		currentIntrusion = null;
	}
}
