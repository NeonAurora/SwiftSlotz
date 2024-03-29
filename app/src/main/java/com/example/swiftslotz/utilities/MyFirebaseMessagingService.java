package com.example.swiftslotz.utilities;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();
            Log.d(TAG, "Message data payload: " + data);

            // Extract title, body, and username from the data payload
            String title = data.get("title") != null ? data.get("title") : "Default Title";
            String username = data.get("username") != null ? data.get("username") : "Unknown User";
            String bodyMessage = data.get("body") != null ? data.get("body") : "Default Body";
            String body;

            if(username.equals("Unknown User")) {
                body = bodyMessage;
                Log.e("Caution", "Username is Unknown User");
            } else {
                body = "From " + username + ": " + bodyMessage;
            }

            // Display the notification
            NotificationDisplay.displayNotification(getApplicationContext(), title, body);
        } else {
            Log.e("err", "Remote Message Data Payload is Empty");
        }
    }

}
