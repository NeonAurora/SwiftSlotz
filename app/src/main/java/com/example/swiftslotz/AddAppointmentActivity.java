package com.example.swiftslotz;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddAppointmentActivity extends BaseActivity {

    CompactCalendarView calendarView;
    EditText appointmentTitleEditText;
    EditText appointmentEditText;
    Button addAppointmentButton;
    Button selectTimeButton;
    TextView selectedTimeTextView;
    DatabaseReference db;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);
        calendarView = findViewById(R.id.calendarView);
        appointmentTitleEditText = findViewById(R.id.appointmentTitleEditText);
        appointmentEditText = findViewById(R.id.appointmentEditText);
        addAppointmentButton = findViewById(R.id.addAppointmentButton);
        selectTimeButton = findViewById(R.id.selectTimeButton);
        selectedTimeTextView = findViewById(R.id.selectedTimeTextView);

        //db = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL).getReference("appointments");

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
                String appointmentTitle = appointmentTitleEditText.getText().toString();
                String appointmentText = appointmentEditText.getText().toString();
                String selectedTimeString = selectedTimeTextView.getText().toString().replace("Selected Time: ", "");
                String selectedDateString = sdf.format(selectedDate.getTime());

                if (appointmentTitle.isEmpty() || appointmentText.isEmpty() || selectedTimeString.isEmpty()) {
                    Toast.makeText(AddAppointmentActivity.this, "Please enter appointment title, details, and time", Toast.LENGTH_SHORT).show();
                    return;
                }

                Appointment appointment = new Appointment();
                appointment.setTitle(appointmentTitle);
                appointment.setDate(selectedDateString);
                appointment.setTime(selectedTimeString);
                appointment.setDetails(appointmentText);

                // Initialize the AppointmentManager
                AppointmentManager appointmentManager = new AppointmentManager(AddAppointmentActivity.this);

                // Call addAppointment() method of the AppointmentManager
                appointmentManager.addAppointment(appointment);
            }
        });


        selectTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker();
            }
        });
    }

    private void showTimePicker() {
        MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Appointment Time")
                .build();

        materialTimePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour = materialTimePicker.getHour();
                int minute = materialTimePicker.getMinute();
                String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
                selectedTimeTextView.setText("Selected Time: " + formattedTime);
            }
        });

        materialTimePicker.show(getSupportFragmentManager(), "MATERIAL_TIME_PICKER");
    }
}
