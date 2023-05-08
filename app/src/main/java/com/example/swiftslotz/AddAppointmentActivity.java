package com.example.swiftslotz;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class AddAppointmentActivity extends AppCompatActivity {

    CompactCalendarView calendarView;
    EditText appointmentEditText;
    Button addAppointmentButton;
    DatabaseReference db;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);

        calendarView = findViewById(R.id.calendarView);
        appointmentEditText = findViewById(R.id.appointmentEditText);
        addAppointmentButton = findViewById(R.id.addAppointmentButton);

        db = FirebaseDatabase.getInstance("https://swiftslotz-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("appointments");

        final Calendar selectedDate = Calendar.getInstance();

        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                selectedDate.setTime(dateClicked);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                // Do nothing
            }
        });

        addAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String appointmentText = appointmentEditText.getText().toString();
                String selectedDateString = sdf.format(selectedDate.getTime());

                if (appointmentText.isEmpty()) {
                    Toast.makeText(AddAppointmentActivity.this, "Please enter appointment details", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Object> appointmentData = new HashMap<>();
                appointmentData.put("date", selectedDateString);
                appointmentData.put("details", appointmentText);

                db.push().setValue(appointmentData)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(AddAppointmentActivity.this, "Appointment added successfully", Toast.LENGTH_SHORT).show();
                            appointmentEditText.setText("");
                        })
                        .addOnFailureListener(e -> Toast.makeText(AddAppointmentActivity.this, "Failed to add appointment", Toast.LENGTH_SHORT).show());
            }
        });
    }
}
