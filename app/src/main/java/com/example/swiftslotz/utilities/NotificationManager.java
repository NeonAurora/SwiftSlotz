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
import java.util.Iterator;

public class NotificationManager {
    private static final String SERVER_KEY = "AAAAjUMreT4:APA91bHGA5nH7pazY8Mc3gBRrgrPQIJn2MhyixiRaJSmV4jjnWc44r4hj1GbqwXzBuyLjQJ_FcE4gZddEDI7jmaBAPUAbrRP3wdSPE_IzynTW2BO7J9GON28vaBMMBrmEKU8TF74wmTY";
    private static final String FCM_SEND_ENDPOINT = "https://fcm.googleapis.com/fcm/send";

    public static void sendFCMNotification(final String userFcmToken, final String title, final String body, final JSONObject additionalData) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    JSONObject root = new JSONObject();
                    JSONObject data = new JSONObject();
                    data.put("title", title);
                    data.put("body", body);
                    if (additionalData != null) {
                        for (Iterator<String> it = additionalData.keys(); it.hasNext(); ) {
                            String key = it.next();
                            data.put(key, additionalData.get(key));
                        }
                    }
                    root.put("data", data);
                    root.put("to", userFcmToken);

                    return sendHttpRequest(FCM_SEND_ENDPOINT, root.toString());
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
    private static String sendHttpRequest(String urlString, String payload) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Authorization", "key=" + SERVER_KEY);
        httpURLConnection.setRequestProperty("Content-Type", "application/json");

        OutputStreamWriter wr = new OutputStreamWriter(httpURLConnection.getOutputStream());
        wr.write(payload);
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
    }

}
