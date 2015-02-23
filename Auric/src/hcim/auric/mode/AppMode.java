package hcim.auric.mode;

import hcim.auric.audit.AuditQueue;
import hcim.auric.audit.OriginalAuditTask;
import hcim.auric.database.configs.ConfigurationDatabase;
import hcim.auric.receiver.OriginalReceiver;
import hcim.auric.strategy.AppLaunchedStrategy;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class AppMode extends AbstractMode {

	public AppMode(Context c) {
		super(c);
		
		AuditQueue queue = new AuditQueue();
		ConfigurationDatabase db = ConfigurationDatabase.getInstance(c);
		int n = db.getNumberOfPicturesPerDetection();

		task = new OriginalAuditTask(queue, new AppLaunchedStrategy(context,queue, n));

		receiver = new OriginalReceiver(queue);

		filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
	}

}
