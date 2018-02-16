package mcgyvers.mobitrip;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import mcgyvers.mobitrip.dataModels.Trip;

/**
 * Created by edson on 14/12/17.
 *
 * class that creates and modifies notifications
 */

public class Notifications {

    NotificationManager notificationManager;
    String id = "channel_01";

    public Notifications(){




    }

    public void notify(Context context, Trip trip){
        Intent resultIntent = new Intent(context,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        //TODO:finish this pendingIntent

        notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, id)
                    .setSmallIcon(R.drawable.ic_end_trip)
                    .setContentTitle("Your trip is beggining shortly")
                    .setContentText(trip.getOrigin() + " - " + trip.getDestination() + "\n" + trip.getDate())
                    .addAction(R.drawable.ic_current_trip, "Start Trip", pendingIntent);



        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        builder.setContentIntent(resultPendingIntent);
        int notificationId = 01;
        notificationManager.notify(notificationId, builder.build());





    }




}
