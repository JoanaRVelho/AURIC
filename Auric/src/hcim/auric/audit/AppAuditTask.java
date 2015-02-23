package hcim.auric.audit;

import hcim.auric.detector.IDetector;
import hcim.auric.record.IRecorder;
import hcim.auric.strategy.AppLaunchedStrategy;
import hcim.auric.strategy.IStrategy;

public class AppAuditTask extends AuditTask {

	private AppLaunchedStrategy strategy;

	public AppAuditTask(AuditQueue queue, AppLaunchedStrategy strategy) {
		super(queue);
		this.strategy = strategy;
	}

	@Override
	protected void onMessageReceived(TaskMessage taskMessage) {
		String id = taskMessage.getID();

		if (id.equals(ACTION_ON)) {
			strategy.actionOn();
		}
		if (id.equals(ACTION_OFF)) {
			strategy.actionOff();
		}
		if (id.equals(ACTION_NEW_APP)) {
			strategy.actionAppLaunched(taskMessage.isIntrusion());
		}
		if (id.equals(ACTION_RESULT)) {
			strategy.actionResult(taskMessage.isIntrusion());
		}
		if (id.equals(ACTION_NEW_PICTURE)) {
			strategy.actionNewPicture(taskMessage.getPic());
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