package com.thegads.uliza.fcm;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.thegads.uliza.activity.MessageActivity;
import com.thegads.uliza.app.MyApplication;
import com.thegads.uliza.model.NewMessage;
import com.thegads.uliza.util.Config;
import com.thegads.uliza.util.MessageNotification;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Freddy Genicho on 5/23/2016.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

        /*String title = bundle.getString("title");
        Boolean isBackground = Boolean.valueOf(bundle.getString("is_background"));
        String flag = bundle.getString("flag");
        String data = bundle.getString("data");
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "title: " + title);
        Log.d(TAG, "isBackground: " + isBackground);
        Log.d(TAG, "flag: " + flag);
        Log.d(TAG, "data: " + data);


        if (flag == null)
            return;

        if (MyApplication.getInstance().getPrefManager().getUser() == null) {
            // user is not logged in, skipping push notification
            Log.e(TAG, "user is not logged in, skipping push notification");
            return;
        }

        if (remoteMessage.getFrom().startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
        }


        switch (Integer.parseInt(flag)) {
            case Config.PUSH_TYPE_CHATROOM:
                // push notification belongs to a chat room
                processChatRoomPush(isBackground, remoteMessage);
                break;
        }*/


        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.


        Log.e(TAG, "From: " + remoteMessage.getFrom());
        Log.e(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        long id = saveMessage(remoteMessage);
        showNotificationMessage(remoteMessage, id);
    }

    private long saveMessage(RemoteMessage remoteMessage) {
        Log.e(TAG, "saving message from firebase notification");
        NewMessage message = new NewMessage();

        // Use DateFormat
        Date now = new Date();
        DateFormat formatter = DateFormat.getInstance(); // Date and time
        String dateStr = formatter.format(now);
        Log.e(TAG, dateStr);
        message.setTime(dateStr);
        message.setChatId(remoteMessage.getFrom());
        message.setBody(remoteMessage.getNotification().getBody());
        message.setRead(1);
        return message.save();
    }


    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(RemoteMessage remoteMessage, long id) {
        String chtId = formatTopic(remoteMessage.getFrom());
        // Use DateFormat
        Date now = new Date();
        DateFormat formatter = DateFormat.getInstance(); // Date and time
        String dateStr = formatter.format(now);
        Log.e(TAG, dateStr);
        String messageBody = remoteMessage.getNotification().getBody();

        NewMessage message = new NewMessage(chtId,messageBody,1,dateStr);
        MessageNotification.notify(this, message, 1);
    }

    private static String formatTopic(String chatId) {
        String[] parts = chatId.split("/");
        return parts[2].replaceAll("_", " ");
    }

    /**
     * Processing chat room push message
     * this message will be broadcasts to all the activities registered
     */
    private void processChatRoomPush(boolean isBackground, RemoteMessage remoteMessage) {
        if (!isBackground) {

            String chatRoomId = remoteMessage.getFrom();

            NewMessage message = new NewMessage();
            message.setBody(remoteMessage.getNotification().getBody());
            //message.setTime(System.currentTimeMillis());
            message.setChatId(remoteMessage.getFrom());
            message.setRead(1);

            // verifying whether the app is in background or foreground
            if (!MessageNotification.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("type", Config.PUSH_TYPE_CHATROOM);
                pushNotification.putExtra("message", (Serializable) message);
                pushNotification.putExtra("chat_room_id", chatRoomId);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                MessageNotification notificationUtils = new MessageNotification();
                notificationUtils.playNotificationSound();
            } else {
                // app is in background. show the message in notification try
                //showNotificationMessage(remoteMessage);
            }


        } else {
            // the push notification is silent, may be other operations needed
            // like inserting it in to SQLite
            NewMessage message = new NewMessage();
            message.setBody(remoteMessage.getNotification().getBody());

            // Use DateFormat
            Date now = new Date();
            DateFormat formatter = DateFormat.getInstance(); // Date and time
            String dateStr = formatter.format(now);
            Log.e(TAG, dateStr);
            message.setTime(dateStr);
            message.setChatId(remoteMessage.getFrom());
            message.setRead(1);
            message.save();
        }
    }

}
