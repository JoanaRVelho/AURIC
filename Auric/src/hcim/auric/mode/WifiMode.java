package hcim.auric.mode;

import hcim.auric.audit.AuditQueue;
import hcim.auric.audit.OriginalAuditTask;
import hcim.auric.receiver.WifiDemoReceiver;
import hcim.auric.strategy.IStrategy;
import hcim.auric.strategy.WifiStrategy;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class WifiMode extends AbstractMode {

	public WifiMode(Context c) {
		super(c);

		AuditQueue queue = new AuditQueue();
		IStrategy strategy = new WifiStrategy(c, queue);

		task = new OriginalAuditTask(queue, strategy);

		receiver = new WifiDemoReceiver(queue);

		filter = new IntentFilter();
		filter.addAction("swat_interaction");
		filter.addAction(Intent.ACTION_SCREEN_OFF);
	}

}
