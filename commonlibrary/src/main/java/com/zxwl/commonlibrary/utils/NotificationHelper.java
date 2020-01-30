package com.zxwl.commonlibrary.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.zxwl.commonlibrary.R;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * author：pc-20171125
 * data:2019/6/14 10:04
 */
public class NotificationHelper {
    private Context context;
    NotificationCompat.Builder notificationBuilder = null;
    NotificationManager manager = null;
    private int currentProgress = 0;
    private String contentText;
    public static final int NOTIFICATION_ID = 1;

    public NotificationHelper(Context context) {
        this.context = context;
        currentProgress = 0;
    }

    public void onDestroy() {
        if (manager == null) {
            manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        }
        manager.cancel(NOTIFICATION_ID);
    }

    private static final String channelid = "version_service_id";

    public Notification getServiceNotification() {
        NotificationCompat.Builder notifcationBuilder = new NotificationCompat.Builder(context, channelid)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText("正在运行")
                .setAutoCancel(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channelid, "version_service_name", NotificationManager.IMPORTANCE_LOW);
            notificationChannel.enableLights(false);
//            notificationChannel.setLightColor(getColor(R.color.versionchecklib_theme_color));
            notificationChannel.enableVibration(false);
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(notificationChannel);
            }
        }
        return notifcationBuilder.build();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Notification createSimpleNotification(Context context) {
        NotificationChannel channel = new NotificationChannel(channelid,
                "MyApp", NotificationManager.IMPORTANCE_DEFAULT);
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
        return new NotificationCompat.Builder(context, channelid)
                .setContentTitle("")
                .setContentText("")
                .build();
    }

    /**
     * show notification
     */
    public void showNotification(int smallIconRes, String ticker, String content, Intent intent) {
        manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notificationBuilder = createNotification(smallIconRes, ticker, content, intent);
        manager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private NotificationCompat.Builder createNotification(int smallIconRes, String ticker, String content, Intent intent) {
        contentText = context.getString(R.string.versionchecklib_download_progress);

        final String CHANNEL_ID = "0", CHANNEL_NAME = "ALLEN_NOTIFICATION";
        NotificationCompat.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
            notificationChannel.enableLights(false);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(false);
            NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
        }
        builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setAutoCancel(true);
        builder.setSmallIcon(smallIconRes);
        builder.setBadgeIconType(smallIconRes);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), smallIconRes));
        //set content title
        String contentTitle = context.getString(R.string.app_name);
        builder.setContentTitle(contentTitle);
        //set ticker
        builder.setTicker(ticker);
        //set content text

        builder.setContentText(content);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);

        return builder;
    }

    /**
     * update notification progress
     *
     * @param progress the progress of notification
     */
    public void updateNotification(int progress) {
        Log.i("NotificationHelper", "NotificationHelper" + progress);
//        if ((progress - currentProgress) > 5) {
        notificationBuilder.setContentIntent(null);
        notificationBuilder.setContentText(String.format(contentText, progress));
        manager.notify(NOTIFICATION_ID, notificationBuilder.build());
        currentProgress = progress;
//        }
    }
}
