package hcim.auric.service;

import hcim.auric.database.SettingsPreferences;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * This class starts service after booting. When receives the boot complete
 * event it will start auric background service if it is on.
 * 
 * @author Joana Velho
 * 
 */
public class AutoStart extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		SettingsPreferences s = new SettingsPreferences(context);
		if (s.isIntrusionDetectorActive()) {
			Intent startServiceIntent = new Intent(context,
					BackgroundService.class);
			context.startService(startServiceIntent);
		}
	}
}
