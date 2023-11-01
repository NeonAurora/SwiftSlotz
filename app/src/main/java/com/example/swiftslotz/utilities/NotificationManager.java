package com.example.swiftslotz.utilities;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class NotificationManager {
    private static final String SERVER_KEY = "AAAAjUMreT4:APA91bHGA5nH7pazY8Mc3gBRrgrPQIJn2MhyixiRaJSmV4jjnWc44r4hj1GbqwXzBuyLjQJ_FcE4gZddEDI7jmaBAPUAbrRP3wdSPE_IzynTW2BO7J9GON28vaBMMBrmEKU8TF74wmTY";  // Replace with your actual server key

    public static void sendFCMNotification(final String userFcmToken) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    URL url = new URL("https://fcm.googleapis.com/fcm/send");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("Authorization", "key=" + SERVER_KEY);
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");

                    JSONObject root = new JSONObject();
                    JSONObject data = new JSONObject();
                    data.put("title", "New Appointment Request");
                    data.put("body", "You have a new appointment request.");
                    root.put("data", data);
                    root.put("to", userFcmToken);

                    OutputStreamWriter wr = new OutputStreamWriter(httpURLConnection.getOutputStream());
                    wr.write(root.toString());
                    wr.flush();

                    InputStream is = httpURLConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    String line;
                    StringBuilder response = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    return response.toString();

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    Log.d("FCM", "Server response: " + result);
                }
            }
        }.execute();
    }

}
