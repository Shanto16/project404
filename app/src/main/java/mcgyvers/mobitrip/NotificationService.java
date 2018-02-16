package mcgyvers.mobitrip;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.google.gson.Gson;

import mcgyvers.mobitrip.dataModels.Trip;

/**
 * Created by edson on 21/12/17.
 */
public class NotificationService extends IntentService{
    public static final String ACTION = "com.mcgyvers.mobitrip.NotificationService";

    Notifications notifications = new Notifications();

    public NotificationService(){
        super("notification-service");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // open to dispute about what data should we get here
        WakefulBroadcastReceiver.completeWakefulIntent(intent);
        String data = intent.getStringExtra("trip");
        Trip trip = new Gson().fromJson(data,Trip.class);

        sendNotification(trip);

        // testing LocalBroadcast for sending Broadcasts
        Intent in = new Intent(ACTION);
        in.putExtra("resultCode", Activity.RESULT_OK);
        in.putExtra("resultValue", "ok");
        LocalBroadcastManager.getInstance(this).sendBroadcast(in);
    }

    private void sendNotification(Trip trip){
        notifications.notify(getApplicationContext(), trip);
    }
}