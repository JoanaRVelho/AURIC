package hcim.auric.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoStart extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent startServiceIntent = new Intent(context, BackgroundService.class);
		context.startService(startServiceIntent);
	}


/*    <receiver android:name="hcim.auric.service.AutoStart" >
        <intent-filter>
            <action android:name="android.intent.action.BOOT_COMPLETED" />
        </intent-filter>
    </receiver>*/
}
