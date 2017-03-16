package in.youngpioneer.dps;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.artifex.mupdfdemo.YoungPioneerMain;
import com.artifex.mupdfdemo.downloadManager.DownloadHandler;
import com.google.android.gms.gcm.GcmListenerService;

import java.util.List;

/**
 * A service that listens to GCM notifications.
 */
public class PushListenerService extends GcmListenerService {

    public static DownloadHandler handler;

    private static final String LOG_TAG = PushListenerService.class.getSimpleName();

    // Intent action used in local broadcast
    public static final String ACTION_SNS_NOTIFICATION = "sns-notification";
    // Intent keys
    public static final String INTENT_SNS_NOTIFICATION_FROM = "from";
    public static final String INTENT_SNS_NOTIFICATION_DATA = "data";


    /**
     * Helper method to extract SNS message from bundle.
     *
     * @param data bundle
     * @return message string from SNS push notification
     */
    public static String getMessage(Bundle data) {
        // If a push notification is sent as plain text, then the message appears in "default".
        // Otherwise it's in the "message" for JSON format.
        return data.containsKey("default") ? data.getString("default") : data.getString(
            "message", "");
    }

    private static boolean isForeground(Context context) {
        // Gets a list of running processes.
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();

        // On some versions of android the first item in the list is what runs in the foreground,
        // but this is not true on all versions.  Check the process importance to see if the app
        // is in the foreground.
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : tasks) {
            if (ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND == appProcess.importance
                && packageName.equals(appProcess.processName)) {
                return true;
            }
        }
        return false;
    }

    private void displayNotification(final String message) {
        Intent notificationIntent = new Intent(this, YoungPioneerMain.class);
        notificationIntent.setFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        int requestID = (int) System.currentTimeMillis();
        PendingIntent contentIntent = PendingIntent.getActivity(this, requestID, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT);

        // Display a notification with an icon, message as content, and default sound. It also
        // opens the app when the notification is clicked.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setSmallIcon(
                R.mipmap.push)
                .setContentTitle("Dps Herald")
                .setContentText(message)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setContentIntent(contentIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, builder.build());
    }


    private void broadcast(final String from, final Bundle data) {
        Intent intent = new Intent(ACTION_SNS_NOTIFICATION);
        intent.putExtra(INTENT_SNS_NOTIFICATION_FROM, from);
        intent.putExtra(INTENT_SNS_NOTIFICATION_DATA, data);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs. For Set of keys use
     * data.keySet().
     */
    @Override
    public void onMessageReceived(final String from, final Bundle data) {
        String message = getMessage(data);
        Log.d(LOG_TAG, "From: " + from);
        Log.d(LOG_TAG, "Message: " + message);
        long unixTime = System.currentTimeMillis() / 1000L;
        String unixTimeS=String.valueOf(unixTime);
        try {
            YoungPioneerMain.Pushstore.insertProvider(from, message, unixTimeS);
        }
        catch (NullPointerException e)
        {
            e.printStackTrace();
        }
        Log.i("Values ","Before "+ YoungPioneerMain.Pushstore.getAllMessages().toString());

        if (message.contains("update")) {
            //YoungPioneerMain.mReference.downloadFromUrl();
            setUpdateAvailable(true);
        }
        if(message.contains("get")&&message.contains("---"))
        {
            //send "get---"URL TO PDF" "
            Log.i("DOWNLOAD","LINK is to come "+message);

            String[] splitted=message.split("---");

            Log.i("DOWNLOAD","LINK is "+splitted[1]);
            YoungPioneerMain.mReference.downloadFromUrlString(splitted[1]);
        }
        // Display a notification in the notification center if the app is in the background.
        // Otherwise, send a local broadcast to the app and let the app handle it.


        else {
            if (isForeground(this)) {
                // broadcast notification
                broadcast(from, data);
            } else {
                displayNotification(message);
            }
        }
    }
    String update ="isUpdateAvailable";

    private void setUpdateAvailable(boolean isAvailable)
    {
        SharedPreferences prefs = YoungPioneerMain.mReference.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean(update,isAvailable);
        edit.apply();
    }
}
