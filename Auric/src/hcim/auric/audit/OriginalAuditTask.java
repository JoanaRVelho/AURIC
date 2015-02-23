package hcim.auric.audit;

import hcim.auric.detector.IDetector;
import hcim.auric.record.IRecorder;
import hcim.auric.strategy.IStrategy;

public class OriginalAuditTask extends AuditTask {

	protected IStrategy strategy;

	public OriginalAuditTask(AuditQueue queue, IStrategy strategy) {
		super(queue);

		this.strategy = strategy;
	}

	@Override
	protected void onMessageReceived(TaskMessage taskMessage) {
		String id = taskMessage.getID();

		if (id.equals(ACTION_OFF)) {
			strategy.actionOff();
		}
		if (id.equals(ACTION_ON)) {
			strategy.actionOn();
		}
		if (id.equals(ACTION_NEW_PICTURE)) {
			if (!isScreenOff())
				strategy.actionNewPicture(taskMessage.getPic());
		}
		if (id.equals(ACTION_RESULT)) {
			if (!isScreenOff())
				strategy.actionResult(taskMessage.isIntrusion());
		}
	}

	@Override
	public IStrategy getStrategy() {
		return strategy;
	}

	@Override
	public IRecorder getRecorder() {
		return strategy.getRecorder();
	}

	@Override
	public IDetector getDetector() {
		return strategy.getDetector();
	}

}
