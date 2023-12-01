package com.example.swiftslotz.fragments.pageFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.swiftslotz.BuildConfig;
import com.example.swiftslotz.utilities.Appointment;
import com.example.swiftslotz.utilities.AppointmentManager;
import com.example.swiftslotz.R;
import com.example.swiftslotz.utilities.BaseActivity;
import com.example.swiftslotz.utilities.User;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class AddAppointmentFragment extends Fragment {

    CompactCalendarView calendarView;
    EditText appointmentTitleEditText, appointmentEditText, appointmentDurationEditText;
    Button addAppointmentButton, selectTimeButton;
    ImageButton scrollLeftButton, scrollRightButton;
    TextView monthTextView, yearTextView;
    Spinner unitSpinner;
    String firebaseKey;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private boolean isUserAvailable;

    private List<String> userActiveDays;
    private String userActiveHoursStart;
    private String userActiveHoursEnd;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_appointment, container, false);
        if (getActivity() != null) {
            ((BaseActivity) getActivity()).updateBottomNavigationForFragment("FragmentX");
        }


        calendarView = view.findViewById(R.id.calendarView);
        appointmentTitleEditText = view.findViewById(R.id.appointmentTitleEditText);
        appointmentEditText = view.findViewById(R.id.appointmentEditText);
        addAppointmentButton = view.findViewById(R.id.addAppointmentButton);
        selectTimeButton = view.findViewById(R.id.selectTimeButton);
        appointmentDurationEditText = view.findViewById(R.id.appointmentDurationEditText);
        unitSpinner = view.findViewById(R.id.unitSpinner);
        scrollLeftButton = view.findViewById(R.id.scrollLeftButton);  // New initialization
        scrollRightButton = view.findViewById(R.id.scrollRightButton);
        monthTextView = view.findViewById(R.id.monthTextView);
        yearTextView = view.findViewById(R.id.yearTextView);
        Calendar cal = Calendar.getInstance();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.units_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(adapter);

        final Calendar selectedDate = Calendar.getInstance();
        monthTextView.setText(new SimpleDateFormat("MMMM", Locale.getDefault()).format(cal.getTime()));
        yearTextView.setText(Integer.toString(cal.get(Calendar.YEAR)));

        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                selectedDate.setTime(dateClicked);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(firstDayOfNewMonth);
                int month = cal.get(Calendar.MONTH);
                int year = cal.get(Calendar.YEAR);

                monthTextView.setText(new SimpleDateFormat("MMMM", Locale.getDefault()).format(cal.getTime()));
                yearTextView.setText(Integer.toString(year));
            }
        });

        scrollLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarView.scrollLeft();
            }
        });

        scrollRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarView.scrollRight();
            }
        });

        Bundle bundle = getArguments();
        if (bundle != null) {
            User selectedUser = (User) bundle.getSerializable("selectedUser");
            firebaseKey = bundle.getString("firebaseKey");

            // Use selectedUser and firebaseKey as needed
        }

        fetchUserActiveInfo(firebaseKey);
        addAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int appointmentDuration;
                String appointmentTitle = appointmentTitleEditText.getText().toString();
                String appointmentText = appointmentEditText.getText().toString();
                String selectedTimeString = selectTimeButton.getText().toString().replace("Selected Time: ", "");
                String selectedDateString = sdf.format(selectedDate.getTime());
                String unit = unitSpinner.getSelectedItem().toString();

                if(appointmentDurationEditText.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), "Please enter appointment duration", Toast.LENGTH_SHORT).show();
                    return;
                } else if (Integer.parseInt(appointmentDurationEditText.getText().toString()) <= 0) {
                    Toast.makeText(getActivity(), "Appointment duration must be greater than 0", Toast.LENGTH_SHORT).show();
                    return;
                } else if (Integer.parseInt(appointmentDurationEditText.getText().toString()) > 24 && unit.equals("Hour")) {
                    Toast.makeText(getActivity(), "Appointment duration must be less than 24 hours", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Toast.makeText(getActivity(), "Appointment duration: " + appointmentDurationEditText.getText().toString(), Toast.LENGTH_SHORT).show();
                    appointmentDuration = Integer.parseInt(appointmentDurationEditText.getText().toString());
                }

                if (appointmentTitle.isEmpty() || appointmentText.isEmpty() || selectedTimeString.isEmpty() || selectedTimeString.equals("Select Time") || appointmentDurationEditText.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter appointment title, details, and time", Toast.LENGTH_SHORT).show();
                    return;
                }

                SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                try {
                    Date date = parseFormat.parse(selectedTimeString);
                    selectedTimeString = displayFormat.format(date);
                } catch (ParseException e) {
                    Toast.makeText(getActivity(), "Invalid time format", Toast.LENGTH_SHORT).show();
                    return;
                }

                int durationInMinutes;
                try {
                    if (unit.equals("Hour")) {
                        durationInMinutes = appointmentDuration * 60;
                    } else {
                        durationInMinutes = appointmentDuration;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(getActivity(), "Invalid duration", Toast.LENGTH_SHORT).show();
                    return;
                }



                Appointment appointment = new Appointment();
                appointment.setTitle(appointmentTitle);
                appointment.setDate(selectedDateString);
                appointment.setTime(selectedTimeString);
                appointment.setDetails(appointmentText);
                appointment.setDuration(durationInMinutes);

                isUserAvailable = isUserAvailable(selectedDateString, selectedTimeString);

                // Initialize the AppointmentManager
                if(isUserAvailable){
                    Toast.makeText(getActivity(), "User is available", Toast.LENGTH_SHORT).show();
                    AppointmentManager appointmentManager = new AppointmentManager(getActivity());
                    appointmentManager.addAppointmentRequest(appointment,firebaseKey);
                } else {
                    Toast.makeText(getActivity(), "User is not available. Would like to initiate a force Request?", Toast.LENGTH_SHORT).show();
                }
            }
        });


        selectTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePicker();
            }
        });

        return view;
    }

    /**
     *  this shows the time for an appointment
     */
    private void showTimePicker() {
        MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Appointment Time")
                .build();

        materialTimePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour = materialTimePicker.getHour();
                int minute = materialTimePicker.getMinute();
                String amPm = hour < 12 ? "AM" : "PM";
                if (hour > 12) hour -= 12;
                else if (hour == 0) hour = 12;
                String formattedTime = String.format(Locale.getDefault(), "%02d:%02d %s", hour, minute, amPm);
                selectTimeButton.setText("Selected Time: " + formattedTime);
            }
        });

        materialTimePicker.show(getParentFragmentManager(), "MATERIAL_TIME_PICKER");
    }

    private void fetchUserActiveInfo(String userId) {
        DatabaseReference userDb = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL).getReference("users").child(userId);

        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Fetch and store active days
                    List<String> activeDaysList = dataSnapshot.child("activeDays").getValue(new GenericTypeIndicator<List<String>>() {});
                    userActiveDays = new ArrayList<>(activeDaysList);
                    if(userActiveDays == null){
                        Log.d("AddAppointmentFragment", "Active days: " + "null");
                        userActiveDays = new ArrayList<>();
                    } else {
                        userActiveDays = new ArrayList<>(activeDaysList);
                        Log.d("AddAppointmentFragment", "Active days: " + userActiveDays.toString());
                    }
                    // Fetch and store active hours start and end
                    userActiveHoursStart = dataSnapshot.child("activeHoursStart").getValue(String.class);
                    userActiveHoursEnd = dataSnapshot.child("activeHoursEnd").getValue(String.class);
                } else {
                    // Handle the case where user data doesn't exist
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors
            }
        });
    }

    private boolean isUserAvailable(String selectedDateString, String selectedTimeString) {
        try {
            // Parse the selected date and time
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Log.d("AddAppointmentFragment", "Selected date: " + selectedDateString + ", Selected time: " + selectedTimeString);
            Date selectedDate = dateFormat.parse(selectedDateString);
            Date selectedTime = timeFormat.parse(selectedTimeString);

            Calendar calendar = Calendar.getInstance();
            if (selectedDate != null) {
                calendar.setTime(selectedDate);
            }

            // Get the day of week
            String dayOfWeek = new SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.getTime());
            Log.d("AddAppointmentFragment", "Day of week: " + dayOfWeek);

            // Check if the day is in user's active days
            if (!userActiveDays.contains(dayOfWeek)) {
                return false; // User is not active on this day
            }

            // Compare the times
            if (selectedTime != null) {
                Date activeStart = timeFormat.parse(userActiveHoursStart);
                Log.d("AddAppointmentFragment", "Active start: " + activeStart);
                Date activeEnd = timeFormat.parse(userActiveHoursEnd);
                Log.d("AddAppointmentFragment", "Active end: " + activeEnd);

                if (activeStart != null && activeEnd != null) {
                    return (selectedTime.after(activeStart) || selectedTime.equals(activeStart)) && selectedTime.before(activeEnd);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }
}
