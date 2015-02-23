package hcim.auric.mode;

import hcim.auric.audit.AuditQueue;
import hcim.auric.audit.OriginalAuditTask;
import hcim.auric.database.configs.ConfigurationDatabase;
import hcim.auric.receiver.OriginalReceiver;
import hcim.auric.strategy.DeviceSharingStrategy;
import hcim.auric.strategy.IStrategy;
import hcim.auric.strategy.SimpleStrategy;
import hcim.auric.strategy.StrategyManager;
import hcim.auric.strategy.VerboseStrategy;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class OriginalMode extends AbstractMode {

	public OriginalMode(Context c) {
		super(c);

		AuditQueue queue = new AuditQueue();

		IStrategy strategy = getSelectedStrategy(c, queue);
		task = new OriginalAuditTask(queue, strategy);

		receiver = new OriginalReceiver(queue);

		filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
	}

	private IStrategy getSelectedStrategy(Context c, AuditQueue queue) {
		ConfigurationDatabase db = ConfigurationDatabase.getInstance(c);
		String strategy = db.getStrategyType();
		int n = db.getNumberOfPicturesPerDetection();

		if (strategy.equals(StrategyManager.DEVICE_SHARING)) {
			return new DeviceSharingStrategy(c, queue, n);
		}
		if (strategy.equals(StrategyManager.CHECK_ONCE)) {
			return new SimpleStrategy(c, queue, n);
		}
		return new VerboseStrategy(c, queue, n);
	}
}
