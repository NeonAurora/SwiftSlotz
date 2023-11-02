package com.example.swiftslotz.utilities;

import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            // Handle the data payload here
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle() != null ?
                    remoteMessage.getNotification().getTitle() : "Default Title";
            String body = remoteMessage.getNotification().getBody() != null ?
                    remoteMessage.getNotification().getBody() : "Default Body";
            NotificationDisplay.displayNotification(getApplicationContext(), title, body);
        }
    }

}

