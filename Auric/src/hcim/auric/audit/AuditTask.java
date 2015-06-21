package hcim.auric.audit;

import hcim.auric.accessibility.AuricEvents;
import hcim.auric.strategy.IStrategy;
import hcim.auric.utils.LogUtils;

public class AuditTask extends Thread {

	public final static String ACTION_NEW_PICTURE = "NEW PICTURE";
	public final static String ACTION_NEW_APP = "NEW APP";
	public final static String ACTION_RESULT = "RESULT";
	public final static String ACTION_OFF = "OFF";
	public final static String ACTION_OFF_DESTROY = "OFF and DESTROY";
	public final static String ACTION_ON = "ON";

	protected IStrategy strategy;
	private AuditQueue queue;
	private boolean running;
	private volatile boolean screenOn;

	public boolean isRunning() {
		return running;
	}

	public AuditTask(AuditQueue queue, IStrategy strategy) {
		this.queue = queue;
		this.strategy = strategy;
	}

	public void startTask() {
		running = true;
		queue.clear();
		queue.addTaskMessage(new TaskMessage(ACTION_ON));
		this.start();
	}

	public void stopTask() {
		queue.addTaskMessage(new TaskMessage(ACTION_OFF));
	}

	public void stopDestroyTask() {
		queue.addTaskMessage(new TaskMessage(ACTION_OFF_DESTROY));
	}

	@Override
	public void run() {
		LogUtils.info("AuditTask - start running");
		TaskMessage taskMessage;
		String id;
		while (running) {
			if (!queue.isEmpty()) {
				taskMessage = queue.getNext();

				if (taskMessage != null) {
					id = taskMessage.getID();
					LogUtils.info("AuditTask - task = " + id);
					if (screenOn) { // process message only if screen is on
						if (id.equals(ACTION_OFF)) {
							AuricEvents.stop();
							strategy.actionOff();
							screenOn = false;
							LogUtils.info("screen off");
						}
						if (id.equals(ACTION_OFF_DESTROY)) {
							running = false;
							AuricEvents.stop();
							strategy.actionOff();

							strategy.getDetector().destroy();
							strategy.getRecorder().destroy();
						}

						if (id.equals(ACTION_NEW_PICTURE)) {
							strategy.actionNewData(taskMessage.getData());
						}
						if (id.equals(ACTION_RESULT)) {
							strategy.actionResult(taskMessage.isIntrusion());
						}
					} else { // only process ACTION_ON if screen is off
						if (id.equals(ACTION_ON)) {
							AuricEvents.start();
							strategy.actionOn();
							screenOn = true;
							LogUtils.info("screen on");
						}
					}
				}
			}
		}
		LogUtils.info("AuditTask - stop running");
		queue.clear();
	}
}
