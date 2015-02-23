package hcim.auric.receiver;

import hcim.auric.audit.AuditQueue;
import android.content.BroadcastReceiver;

public abstract class AbstractReceiver extends BroadcastReceiver {
	protected static final String TAG = "AURIC";

	protected AuditQueue queue;
	
	public AbstractReceiver(AuditQueue queue){
		this.queue = queue;
	}

}
