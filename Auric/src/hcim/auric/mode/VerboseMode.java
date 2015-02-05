package hcim.auric.mode;

import hcim.auric.audit.AuditTask;
import hcim.auric.audit.VerboseAuditTask;
import hcim.auric.receiver.OriginalReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class VerboseMode extends AbstractMode{

	public VerboseMode(Context c) {
		super(c);

		task = new VerboseAuditTask(c);

		receiver = new OriginalReceiver((AuditTask) task);

		filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
	}

}
