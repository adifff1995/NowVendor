package com.app.radarvendor.Utils;//package com.app.radar.Utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import com.app.radarvendor.R;

import java.util.Random;


public class MyNotificationManager {
    private static final int ID_SMALL_NOTIFICATION = 235;
    private static final int NOTIFICATION_ID = 1094;
    int notificationId = new Random().nextInt();
    private Context mCtx;
    private String CHANNEL_ID;

    public MyNotificationManager(Context mCtx) {
        this.mCtx = mCtx;
    }

    public void showSmallNotification(int id, String title, String message, Intent intent) {

        PendingIntent pendingIntent = PendingIntent.getActivity(mCtx,
                0, intent, PendingIntent.FLAG_IMMUTABLE);
        CHANNEL_ID = mCtx.getString(R.string.default_notification_channel_id);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mCtx, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_vendor_png)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mCtx);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "name";
            String description = "desc";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(notificationId, builder.build());
//        CharSequence name = mCtx.getResources().getString(R.string.app_name);
//        PendingIntent resultPendingIntent =
//                PendingIntent.getActivity(
//                        mCtx,
//                        ID_SMALL_NOTIFICATION,
//                        intent,
//                        PendingIntent.FLAG_IMMUTABLE
//                );
//
//      GlobalMethods.printGson("showSmallNotification",intent);
////        Uri sound = Uri.parse("android.resource://com.canny.canny.cannycustomer/"+ R.raw.notifysnd);
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);//                .setOngoing(true)
//        String channelId = mCtx.getString(R.string.default_notification_channel_id);
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mCtx, channelId);
//        Notification notification;
//        notification = mBuilder
//                .setSmallIcon(R.drawable.logo_vendor)
//                .setLargeIcon(BitmapFactory.decodeResource(mCtx.getResources(),R.drawable.logo_vendor))
//                .setContentIntent(resultPendingIntent)
//                .setTicker(title)
//                .setWhen(0)//هاد عنوان
//                .setContentTitle(title)
//                .setSound(defaultSoundUri)
//                .setContentText(message)
//                .setAutoCancel(true)
//                .setPriority(NotificationManager.IMPORTANCE_HIGH)
//                .build();
//
//
//        notification.flags |= Notification.FLAG_AUTO_CANCEL;
//
//        NotificationManager notificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        // Since android Oreo notification channel is needed.
//        assert notificationManager != null;
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(channelId,
//                    "Channel human readable title",
//                    NotificationManager.IMPORTANCE_HIGH);
//            channel.setShowBadge(false);
//            channel.setDescription(message);
//            channel.enableLights(false);
//            channel.enableVibration(false);
//            channel.setSound(defaultSoundUri, null);
//            notificationManager.createNotificationChannel(channel);
//        }
//        notificationManager.notify(id, notification);
    }


}

