package com.getgenieapp.android.Extras;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.getgenieapp.android.Activities.SplashScreenActivity;
import com.getgenieapp.android.R;

public class NotificationHandler {
    Context context;

    public NotificationHandler(Context context) {
        this.context = context;
    }

    public static int MessageNotificationId = 1;

    static int size = 0;

    static String[] msgEvents = null;
    public static boolean keep = true;

    public void notification(int mId, String title, String msg) {

        Intent intent = new Intent(context, SplashScreenActivity.class);
        intent.putExtra("action", "auto");

        PendingIntent pIntent = PendingIntent.getActivity(context, 0,
                intent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.genie_android_icons_97x97)
                        .setContentTitle(title)
                        .setContentText(msg)
                        .setAutoCancel(false);

        mBuilder.setContentIntent(pIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(mId, mBuilder.build());
    }

    public void newNotification(int mId, String title, String msg, String bigTitle, String msgExtra, boolean status) {
        Intent intent = new Intent(context, SplashScreenActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, 0);

        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();

        String[] events = new String[1];
        size = events.length;

        events[0] = new String(msgExtra);
        // Sets a title for the Inbox style big view
        inboxStyle.setBigContentTitle(bigTitle);
        // Moves events into the big view
        for (int i = 0; i < events.length; i++) {
            inboxStyle.addLine(events[i]);
        }
        msgEvents = events;
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.genie_android_icons_97x97)
                        .setContentTitle(title)
                        .setContentText(msg)
                        .setStyle(inboxStyle)
                        .setAutoCancel(false);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(mId, mBuilder.build());
    }

    public void cancelNotification(int mId) {
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(mId);
    }

    public void updateNotification(int mId, String title, String msg, String bigTitle, String msgExtra, boolean status) {
        if (size == 0) {
            newNotification(mId, title, msg, bigTitle, msgExtra, status);
        } else {
            if (msgEvents.length == size) {
                Intent intent = new Intent(context, SplashScreenActivity.class);

                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                        intent, 0);

                NotificationCompat.InboxStyle inboxStyle =
                        new NotificationCompat.InboxStyle();

                if (size > 0) {
                    String[] events = null;
                    if (size > 5) {
                        events = new String[size];
                    } else {
                        events = new String[size + 1];
                    }
                    if (size > 5) {
                        events[0] = msgEvents[1];
                        events[1] = msgEvents[2];
                        events[2] = msgEvents[3];
                        events[3] = msgEvents[4];
                        events[4] = msgEvents[5];
                    } else {
                        for (int i = 0; i < size; i++) {
                            events[i] = msgEvents[i];
                        }
                    }
                    if (size == 6) {
                        events[size - 1] = new String(msgExtra);
                    } else {
                        events[size] = new String(msgExtra);
                    }

                    // Sets a title for the Inbox style big view
                    inboxStyle.setBigContentTitle(bigTitle);
                    // Moves events into the big view
                    for (int i = 0; i < events.length; i++) {
                        inboxStyle.addLine(events[i]);
                    }
                    msgEvents = events;
                    size = events.length;

                }

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.genie_android_icons_97x97)
                                .setContentTitle(title)
                                .setContentText(msg)
                                .setStyle(inboxStyle)
                                .setAutoCancel(false);

                NotificationManager mNotificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(mId, mBuilder.build());
            } else {
                newNotification(mId, title, msg, bigTitle, msgExtra, status);
            }
        }
    }

    public void updateLastNotification(int mId, String title, String msg, String bigTitle, String msgExtra, boolean status) {
        if (size == 0) {
            newNotification(mId, title, msg, bigTitle, msgExtra, status);
        } else {
            if (msgEvents.length == size) {
                Intent postponeIntent = new Intent(context, SplashScreenActivity.class);
                Intent cancelIntent = new Intent(context, SplashScreenActivity.class);
                postponeIntent.putExtra("id", mId);
                cancelIntent.putExtra("id", mId);
                postponeIntent.putExtra("action", "postpone");
                cancelIntent.putExtra("action", "cancel");

                PendingIntent cpIntent = PendingIntent.getActivity(context, 0,
                        cancelIntent, 0);
                PendingIntent ppIntent = PendingIntent.getActivity(context, 0,
                        postponeIntent, 0);

                NotificationCompat.InboxStyle inboxStyle =
                        new NotificationCompat.InboxStyle();


                if (size > 0) {
                    String[] events = new String[size];
                    for (int i = 0; i < size; i++) {
                        events[i] = msgEvents[i];
                    }

                    events[size - 1] = new String(msgExtra);
                    // Sets a title for the Inbox style big view
                    inboxStyle.setBigContentTitle(bigTitle);
                    // Moves events into the big view
                    for (int i = 0; i < events.length; i++) {
                        inboxStyle.addLine(events[i]);
                    }
                    msgEvents = events;
                    size = events.length;

                }

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.genie_android_icons_97x97)
                                .setContentTitle(title)
                                .setContentText(msg)
                                .setStyle(inboxStyle)
                                .setAutoCancel(false);

                NotificationManager mNotificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                mNotificationManager.notify(mId, mBuilder.build());
            } else {
                newNotification(mId, title, msg, bigTitle, msgExtra, status);
            }
        }
    }
}