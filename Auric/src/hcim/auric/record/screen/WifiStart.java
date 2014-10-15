package hcim.auric.record.screen;

import mswat.core.CoreController;
import mswat.interfaces.IOReceiver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class WifiStart extends BroadcastReceiver implements IOReceiver {

	Context c;

	@Override
	public int registerIOReceiver() {
		return CoreController.registerIOReceiver(this);

	}

	@Override
	public void onUpdateIO(int device, int type, int code, int value,
			int timestamp) {
	}

	@Override
	public void onTouchReceived(int type) {
		if (type == 0) {

			Intent intent = new Intent();
			intent.setAction("swat_interaction");
			intent.putExtra("logging", true);
			String time = System.currentTimeMillis() + "";
			intent.putExtra("timestamp", time);
			c.sendBroadcast(intent);
		} else {
			Intent intent = new Intent();
			intent.setAction("swat_interaction");
			intent.putExtra("logging", false);
			c.sendBroadcast(intent);
		}

	}

	@Override
	public void onReceive(Context context, Intent intent) {
		c = context;
		registerIOReceiver();
	}

}
