package com.supergenieapp.android.Extras;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;

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
        Intent intent = new Intent(context, SplashScreenActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0,
                intent, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.genie_android_icons_97x97)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(msg)
                        .setAutoCancel(true)
                        .setContentIntent(pIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(mId, mBuilder.build());
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
        Intent intent = new Intent(context, BaseActivity.class);
        intent.putExtra("page", "message" + "," + previousChatId);
        sharedPreferences.edit().putInt("catid", previousChatId).apply();

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, 0);

        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();

        String[] events = new String[1];
        size = events.length;

        events[0] = new String(msgExtra);
        // Sets a title for the Inbox style big view
        inboxStyle.setBigContentTitle(context.getString(R.string.app_name));
        // Moves events into the big view
        for (int i = 0; i < events.length; i++) {
            inboxStyle.addLine(events[i]);
        }
        msgEvents = events;
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.genie_android_icons_97x97)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(msgExtra)
                        .setStyle(inboxStyle)
                        .setNumber(total)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(mId, mBuilder.build());
    }

    public void cancelNotification(int mId) {
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(mId);
    }

    public void updateNotification(int mId, String msg, String msgExtra, int chatId) {
        if (size == 0) {
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
                Intent intent = new Intent(context, BaseActivity.class);
                intent.putExtra("page", "message" + "," + previousChatId);
                sharedPreferences.edit().putInt("catid", previousChatId).apply();

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
                    inboxStyle.setBigContentTitle(context.getString(R.string.app_name));
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
                                .setContentTitle(context.getString(R.string.app_name))
                                .setContentText(msg)
                                .setStyle(inboxStyle)
                                .setNumber(total)
                                .setAutoCancel(true)
                                .setContentIntent(pendingIntent);

                NotificationManager mNotificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                mNotificationManager.notify(mId, mBuilder.build());

            } else {
                newNotification(mId, msgExtra, chatId);
            }
        }
    }

    public void updateLastNotification(int mId, String msg, String msgExtra, int chatId) {
        if (size == 0) {
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
                Intent intent = new Intent(context, BaseActivity.class);
                intent.putExtra("page", "message");
                sharedPreferences.edit().putInt("catid", previousChatId).apply();

                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                        intent, 0);

                NotificationCompat.InboxStyle inboxStyle =
                        new NotificationCompat.InboxStyle();


                if (size > 0) {
                    String[] events = new String[size];
                    for (int i = 0; i < size; i++) {
                        events[i] = msgEvents[i];
                    }

                    events[size - 1] = new String(msgExtra);
                    // Sets a title for the Inbox style big view
                    inboxStyle.setBigContentTitle(context.getString(R.string.app_name));
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
                                .setContentTitle(context.getString(R.string.app_name))
                                .setContentText(msg)
                                .setStyle(inboxStyle)
                                .setNumber(total)
                                .setAutoCancel(true)
                                .setContentIntent(pendingIntent);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    mBuilder.setFullScreenIntent(pendingIntent, true);
                }

                NotificationManager mNotificationManager =
                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                mNotificationManager.notify(mId, mBuilder.build());
            } else {
                newNotification(mId, msgExtra, chatId);
            }
        }
    }
}