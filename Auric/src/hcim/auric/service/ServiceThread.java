package hcim.auric.service;

import hcim.auric.strategy.IStrategy;
import hcim.auric.utils.LogUtils;

public class ServiceThread extends Thread {

	protected IStrategy strategy;
	private TaskQueue queue;
	private boolean running;
	private volatile boolean screenOn;

	public boolean isRunning() {
		return running;
	}

	public ServiceThread(TaskQueue queue, IStrategy strategy) {
		this.queue = queue;
		this.strategy = strategy;
	}

	public void startTask() {
		running = true;
		queue.clear();
		queue.addTaskMessage(new TaskMessage(TaskMessage.ACTION_ON));
		this.start();
	}

	public void stopTask() {
		queue.addTaskMessage(new TaskMessage(TaskMessage.ACTION_OFF));
	}

	public void stopDestroyTask() {
		queue.addTaskMessage(new TaskMessage(TaskMessage.ACTION_OFF_DESTROY));
	}

	@Override
	public void run() {
		LogUtils.info("AuditTask - start running");
		TaskMessage msg;
		String id;
		while (running) {
			if (!queue.isEmpty()) {
				msg = queue.getNext();

				if (msg != null) {
					id = msg.getID();
					LogUtils.info("AuditTask - task = " + id);
					if (screenOn) { // process message only if screen is on
						if (id.equals(TaskMessage.ACTION_OFF)) {
						//	AuricEvents.stop();
							strategy.actionOff();
							screenOn = false;
							LogUtils.info("screen off");
						}
						if (id.equals(TaskMessage.ACTION_OFF_DESTROY)) {
							running = false;
						//	AuricEvents.stop();
							strategy.actionOff();

							strategy.getDetector().destroy();
							strategy.getRecorder().destroy();
						}
						if (id.equals(TaskMessage.ACTION_RESULT)) {
							strategy.actionResult(msg.isIntrusion());
						}
					} else { // only process ACTION_ON if screen is off
						if (id.equals(TaskMessage.ACTION_ON)) {
						//	AuricEvents.start();
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
