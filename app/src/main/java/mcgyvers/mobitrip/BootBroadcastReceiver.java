package mcgyvers.mobitrip;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import mcgyvers.mobitrip.NotificationService;

/**
 * Created by edson on 21/12/17.
 * WakefulBroadcastReceiver ensures the device does not go back to sleep
 * during the startup of the service
 */


public class BootBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Launch the specified service when this message is received
        Intent startServiceIntent = new Intent(context, NotificationService.class);
        startWakefulService(context, startServiceIntent);
    }
}
