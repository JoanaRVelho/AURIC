package hcim.auric.audit;

import android.util.Log;


public abstract class AuditTask extends Thread implements IAuditTask {
	private AuditQueue queue;

	private static boolean screenOff;

	public AuditTask(AuditQueue queue) {
		this.queue = queue;
	}

	@Override
	public void interrupt() {
		getRecorder().stop();
		getRecorder().destroy();
		getDetector().stop();
	}

	@Override
	public void stopTask() {
		interrupt();
	}
	
	@Override
	public void startTask() {
		this.start();
	}

	public static boolean isScreenOff() {
		return screenOff;
	}

	@Override
	public void run() {
		TaskMessage taskMessage;
		String id;
		while (true) {

			if (!queue.isEmpty()) {
				taskMessage = queue.getNext();

				if (taskMessage != null) {
					id = taskMessage.getID();
					Log.i(TAG, "AuditTask - task = " + id);

					if (id.equals(ACTION_OFF)) {
						screenOff = true;
					}
					if (id.equals(ACTION_ON)) {
						screenOff = false;
					}

					onMessageReceived(taskMessage);
				}
			}
		}
	}

	protected abstract void onMessageReceived(TaskMessage taskMessage);
}
