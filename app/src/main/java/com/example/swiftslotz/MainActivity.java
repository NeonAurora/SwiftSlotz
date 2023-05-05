package com.example.swiftslotz;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();
        // Initialize the RecyclerView
        RecyclerView appointmentsRecyclerView = findViewById(R.id.appointmentsRecyclerView);

        // Create a list of dummy appointments
        List<Appointment> appointments = new ArrayList<>();
        appointments.add(new Appointment("Appointment 1", "2023-06-05", "10:00 AM"));
        appointments.add(new Appointment("Appointment 2", "2023-06-07", "02:00 PM"));
        appointments.add(new Appointment("Appointment 3", "2023-06-12", "11:00 AM"));
        appointments.add(new Appointment("Appointment 4", "2023-06-15", "04:00 PM"));

        // Create an instance of AppointmentsAdapter
        AppointmentsAdapter appointmentsAdapter = new AppointmentsAdapter(appointments);

        // Set the layout manager and adapter for the RecyclerView
        appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        appointmentsRecyclerView.setAdapter(appointmentsAdapter);

        FloatingActionButton appointmentButton = findViewById(R.id.addAppointmentButton);

        appointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNotification();
            }
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "AppointmentChannel";
            String description = "Channel for appointment notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("appointmentChannel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = new Notification.Builder(this, "appointmentChannel")
                .setContentTitle("Appointment Button")
                .setContentText("Button added")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .build();

        notificationManager.notify(1, notification);
    }
}
