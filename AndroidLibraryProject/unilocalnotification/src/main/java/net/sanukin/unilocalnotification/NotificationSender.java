package net.sanukin.unilocalnotification;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;

// add for android O notification
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
//======================================

import java.util.Calendar;
import com.unity3d.player.UnityPlayer;

/**
 * Created by sanukiwataru on 2017/09/17.
 */

public class NotificationSender {
    private boolean isInitilized = false;
    private NotificationManager mManager;
    public static final String ANDROID_CHANNEL_ID = "com.chikeandroid.tutsplustalerts.ANDROID";
    public static final String IOS_CHANNEL_ID = "com.chikeandroid.tutsplustalerts.IOS";
    public static final String ANDROID_CHANNEL_NAME = "ANDROID CHANNEL";
    public static final String IOS_CHANNEL_NAME = "IOS CHANNEL";

    public static void initilizeAndroid8(string android_chanel_ID, String ios_chanel_ID){
        ANDROID_CHANNEL_ID = android_chanel_ID;
        IOS_CHANNEL_ID = ios_chanel_ID;
        // create android channel
        NotificationChannel androidChannel = new NotificationChannel(ANDROID_CHANNEL_ID, ANDROID_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        // Sets whether notifications posted to this channel should display notification lights
        androidChannel.enableLights(true);
        // Sets whether notification posted to this channel should vibrate.
        androidChannel.enableVibration(true);
        // Sets the notification light color for notifications posted to this channel
        androidChannel.setLightColor(Color.GREEN);
        // Sets whether notifications posted to this channel appear on the lockscreen or not
        androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(androidChannel);

        // create ios channel
        NotificationChannel iosChannel = new NotificationChannel(IOS_CHANNEL_ID, IOS_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        iosChannel.enableLights(true);
        iosChannel.enableVibration(true);
        iosChannel.setLightColor(Color.GRAY);
        iosChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        getManager().createNotificationChannel(iosChannel);

        isInitilized = true;
    }

    private NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    // ...
    public Notification.Builder getAndroidChannelNotification(String title, String body) {
        return new Notification.Builder(getApplicationContext(), ANDROID_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(android.R.drawable.stat_notify_more)
                .setAutoCancel(true);
    }

    public Notification.Builder getIosChannelNotification(String title, String body) {
        return new Notification.Builder(getApplicationContext(), IOS_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(android.R.drawable.stat_notify_more)
                .setAutoCancel(true);
    }

    public static void sendAndroid8Notification(string title, string text, int delay, ){
        if(!isInitilized){

            this.initilizeAndroid8("","");

        }

        Notification.Builder nb = getAndroidChannelNotification(title, text);

        getManager().notify(101, nb.build());

    }

    /**
     * Set local notification
     * @param title notification title
     * @param message notification message
     * @param delay seconds to notify when counting from now
     * @param requestCode
     */
    public static void setNotification(String title, String message, int delay, int requestCode) {

        // Get Context
        Context context = getContext();

        // Create intent
        Intent intent = new Intent(context, NotificationReceiver.class);

        // Register notification info
        intent.putExtra("MESSAGE", message);
        intent.putExtra("TITLE", title);
        intent.putExtra("REQUEST_CODE", requestCode);

        // create sender
        PendingIntent sender = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create notification firing time
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, delay);

        // Register notification
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
    }

    /**
     * Notification permitted check
     * @return if notification permitted
     */
    public static boolean isNotificationPermitted() {
        return NotificationManagerCompat.from(getContext()).areNotificationsEnabled();
    }

    /**
     * Return if pending intent registered
     * @param requestCode request code
     * @return if notification registered
     */
    public static boolean hasPendingIntent(int requestCode) {

        Context context = getContext();

        boolean hasPendingIntent = (PendingIntent.getBroadcast(context, requestCode,
                new Intent(context, NotificationReceiver.class),
                PendingIntent.FLAG_NO_CREATE) != null);

        return  hasPendingIntent;
    }

    /**
     * Cancel registered notification
     * @param requestCode Pending intent request code you want to cancel
     */
    public static void cancel(int requestCode) {

        Context context = getContext();

        // get pending intent
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // cancel it
        alarmManager.cancel(sender);
    }

    /**
     * Open app notification settings
     */
    public static void openAppSettings(){

        Activity activity = UnityPlayer.currentActivity;

        Intent intent = new Intent();
        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");

        //for Android 5-7
        intent.putExtra("app_package", activity.getPackageName());
        intent.putExtra("app_uid", activity.getApplicationInfo().uid);

        // for Android O
        intent.putExtra("android.provider.extra.APP_PACKAGE", activity.getPackageName());

        activity.startActivity(intent);
    }

    /**
     * Get current context
     * @return current context
     */
    private static Context getContext() {
        return UnityPlayer.currentActivity.getApplicationContext();
    }
}
