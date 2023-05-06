package com.example.swiftslotz;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
    }
    private void writeToDatabase() {
        // Get a reference to the Firebase Realtime Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("test");

        // Write a simple test object to the database
        myRef.setValue("Hello, World!");
    }

}
