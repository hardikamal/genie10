package com.supergenieapp.android.Extras;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.View;
import android.widget.RemoteViews;

import com.supergenieapp.android.Activities.BaseActivity;
import com.supergenieapp.android.Activities.SplashScreenActivity;
import com.supergenieapp.android.GenieApplication;
import com.supergenieapp.android.Objects.Chat;
import com.supergenieapp.android.R;

public class NotificationHandler {
    Context context;
    SharedPreferences sharedPreferences;

    public NotificationHandler(Context context) {
        this.context = context;
        this.sharedPreferences = GenieApplication.getInstance().getSecurePrefs();
    }

    static int total = 0;
    static int size = 0;
    static int previousChatId = -1;

    static String[] msgEvents = null;

    public void resetNotification() {
        previousChatId = -1;
        size = 0;
        total = 0;
        msgEvents = null;
    }

    public void notification(int mId, String msg) {
        Intent resultIntent = new Intent(context, SplashScreenActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        stackBuilder.addParentStack(SplashScreenActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews expandedView = new RemoteViews(context.getPackageName(),
                R.layout.notification_small);
        expandedView.setTextViewText(R.id.title, context.getString(R.string.app_name));
        expandedView.setTextViewText(R.id.t1, msg);
        expandedView.setTextViewText(R.id.time, new Utils(context).getCurrentTime());

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.icons_97x97)
                .setAutoCancel(true)
                .setContent(expandedView)
                .setSound(defaultSoundUri)
                .setContentIntent(resultPendingIntent).build();

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(mId, notification);
    }

    public void newNotification(int mId, String msgExtra, int chatId) {
        if (previousChatId != 0) {
            if (previousChatId == chatId || previousChatId == -1) {
                previousChatId = chatId;
            } else {
                previousChatId = 0;
            }
        }

        total++;
        Intent resultIntent = new Intent(context, BaseActivity.class);
        resultIntent.putExtra("page", "message");
        sharedPreferences.edit().putInt("catid", previousChatId).apply();

        String[] events = new String[1];
        size = events.length;

        events[0] = msgExtra;

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        stackBuilder.addParentStack(SplashScreenActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews expandedView = new RemoteViews(context.getPackageName(),
                R.layout.notification_small);
        expandedView.setTextViewText(R.id.title, context.getString(R.string.app_name));
        expandedView.setTextViewText(R.id.time, new Utils(context).getCurrentTime());

        for (int i = 0; i < events.length; i++) {
            if (i == 0) {
                expandedView.setTextViewText(R.id.t1, events[0]);
            }
        }

        msgEvents = events;

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.icons_97x97)
                .setAutoCancel(true)
                .setContent(expandedView)
                .setSound(defaultSoundUri)
                .setContentIntent(resultPendingIntent).build();

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(mId, notification);
    }

    public void cancelNotification(int mId) {
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(mId);
    }

    public void updateNotification(int mId, String msgExtra, int chatId) {
        if (size == 0 || msgEvents == null) {
            newNotification(mId, msgExtra, chatId);
        } else {
            if (msgEvents.length == size) {
                if (previousChatId != 0) {
                    if (previousChatId == chatId || previousChatId == -1) {
                        previousChatId = chatId;
                    } else {
                        previousChatId = 0;
                    }
                }
                total++;
                Intent resultIntent = new Intent(context, BaseActivity.class);
                resultIntent.putExtra("page", "message");
                sharedPreferences.edit().putInt("catid", previousChatId).apply();

                TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

                stackBuilder.addParentStack(SplashScreenActivity.class);

                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                        0, PendingIntent.FLAG_UPDATE_CURRENT);

                RemoteViews expandedView = new RemoteViews(context.getPackageName(),
                        R.layout.notification_small);
                expandedView.setTextViewText(R.id.title, context.getString(R.string.app_name));
                expandedView.setTextViewText(R.id.time, new Utils(context).getCurrentTime());

                RemoteViews expandedBigView = new RemoteViews(context.getPackageName(),
                        R.layout.notification_big);
                expandedBigView.setTextViewText(R.id.title, context.getString(R.string.app_name));
                expandedBigView.setTextViewText(R.id.time, new Utils(context).getCurrentTime());

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
                        events[size - 1] = msgExtra;
                    } else {
                        events[size] = msgExtra;
                    }
                    expandedView.setTextViewText(R.id.t1, total + context.getString(R.string.unreadmsg));
                    expandedBigView.setTextViewText(R.id.count, String.valueOf(total));
                    // Moves events into the big view
                    for (int i = 0; i < events.length; i++) {
                        if (i == 0) {
                            expandedBigView.setViewVisibility(R.id.t1, View.VISIBLE);
                            expandedBigView.setTextViewText(R.id.t1, events[i]);
                        } else if (i == 1) {
                            expandedBigView.setViewVisibility(R.id.t2, View.VISIBLE);
                            expandedBigView.setTextViewText(R.id.t2, events[i]);
                        } else if (i == 2) {
                            expandedBigView.setViewVisibility(R.id.t3, View.VISIBLE);
                            expandedBigView.setTextViewText(R.id.t3, events[i]);
                        } else if (i == 3) {
                            expandedBigView.setViewVisibility(R.id.t4, View.VISIBLE);
                            expandedBigView.setTextViewText(R.id.t4, events[i]);
                        } else if (i == 4) {
                            expandedBigView.setViewVisibility(R.id.t5, View.VISIBLE);
                            expandedBigView.setTextViewText(R.id.t5, events[i]);
                        } else if (i == 5) {
                            expandedBigView.setViewVisibility(R.id.t6, View.VISIBLE);
                            expandedBigView.setTextViewText(R.id.t6, events[i]);
                        }
                    }
                    msgEvents = events;
                    size = events.length;

                }

                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Notification notification = new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.icons_97x97)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContent(expandedView)
                        .setContentIntent(resultPendingIntent).build();

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    notification.bigContentView = expandedBigView;
                }

                NotificationManager notificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                notificationManager.notify(mId, notification);

            } else {
                newNotification(mId, msgExtra, chatId);
            }
        }
    }

    public void promotionNotification(int id, String msg) {
        Intent resultIntent = new Intent(context, SplashScreenActivity.class);
        resultIntent.putExtra("intent", "true");

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        stackBuilder.addParentStack(SplashScreenActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews expandedView = new RemoteViews(context.getPackageName(),
                R.layout.notification_small);

        expandedView.setTextViewText(R.id.title, context.getString(R.string.app_name));
        expandedView.setTextViewText(R.id.t1, msg);
        expandedView.setTextViewText(R.id.time, new Utils(context).getCurrentTime());

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.icons_97x97)
                .setAutoCancel(true)
                .setContent(expandedView)
                .setSound(defaultSoundUri)
                .setContentIntent(resultPendingIntent).build();

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(id, notification);
    }
}