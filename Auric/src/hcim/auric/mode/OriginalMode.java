package hcim.auric.mode;

import hcim.auric.audit.AuditTask;
import hcim.auric.audit.AuditTaskWithDeviceSharing;
import hcim.auric.database.ConfigurationDatabase;
import hcim.auric.receiver.OriginalReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class OriginalMode extends AbstractMode {

	public OriginalMode(Context c) {
		super(c);

		boolean sharing = ConfigurationDatabase.getInstance(c).isDeviceSharingEnabled();

		task = sharing ? new AuditTaskWithDeviceSharing(context)
				: new AuditTask(context);

		receiver = new OriginalReceiver((AuditTask) task);

		filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
	}
}
