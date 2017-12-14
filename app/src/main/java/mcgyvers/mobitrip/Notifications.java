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

    public void notify(Context context){

        notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, id)
                    .setSmallIcon(R.drawable.cast_ic_notification_small_icon)
                    .setContentTitle("Your trip is beggining shortly")
                    .setContentText("Would you like to start your trip now");

        Intent resultIntent = new Intent(context,MainActivity.class);

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
