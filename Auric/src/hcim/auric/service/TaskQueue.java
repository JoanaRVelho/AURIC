package hcim.auric.service;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * 
 * @author Joana Velho
 * 
 */
public class TaskQueue {

	private LinkedBlockingQueue<TaskMessage> queue;

	public TaskQueue() {
		queue = new LinkedBlockingQueue<TaskMessage>();
	}

	public void addTaskMessage(TaskMessage t) {
		// if (ignoreTaskMessage(t)) {
		// return;
		// }
		queue.add(t);
	}

	// private boolean ignoreTaskMessage(TaskMessage t) {
	// return AuditTask.isScreenOff() && t.getID() != AuditTask.ACTION_ON;
	// }

	public TaskMessage getNext() {
		try {
			return queue.take();
		} catch (InterruptedException e) {
			return null;
		}
	}

	public boolean isEmpty() {
		return queue.isEmpty();
	}

	public void clear() {
		queue.clear();
	}
}
