package com.example.swiftslotz.fragments.pageFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.swiftslotz.R;
import com.example.swiftslotz.utilities.Appointment;
import com.example.swiftslotz.utilities.AppointmentManager;
import com.example.swiftslotz.utilities.BaseActivity;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ModifyAppointmentFragment extends Fragment {

    private EditText appointmentTitleEditText, appointmentDetailsEditText;
    private Button updateAppointmentButton, selectTimeButton;
    ImageButton scrollLeftButton, scrollRightButton;
    private CompactCalendarView calendarView;
    private AppointmentManager appointmentManager;
    private String appointmentKey;
    private Date selectedDate;
    private TextView yearTextView, monthTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_modify_appointment, container, false);

        // Initialize UI Components
        appointmentTitleEditText = view.findViewById(R.id.modify_appointment_title);
        appointmentDetailsEditText = view.findViewById(R.id.modify_appointment_details);
        selectTimeButton = view.findViewById(R.id.selectTimeButtonEdit);
        updateAppointmentButton = view.findViewById(R.id.update_appointment_button);
        calendarView = view.findViewById(R.id.calendarViewEdit);
        setCalendarDateDisplay(calendarView.getFirstDayOfCurrentMonth());
        scrollLeftButton = view.findViewById(R.id.scrollLeftButtonEdit);git p
        yearTextView = view.findViewById(R.id.yearTextViewEdit);
        monthTextView = view.findViewById(R.id.monthTextViewEdit);

        appointmentManager = new AppointmentManager(getActivity());

        // Load Appointment Details
        loadAppointmentDetails();

        // Event Handlers
        selectTimeButton.setOnClickListener(v -> showTimePicker());
        updateAppointmentButton.setOnClickListener(v -> updateAppointment());
        scrollLeftButton.setOnClickListener(v -> navigateCalendar(-1));
        scrollRightButton.setOnClickListener(v -> navigateCalendar(1));

        return view;
    }

    private void loadAppointmentDetails() {
        if (getArguments() != null) {
            appointmentKey = getArguments().getString("appointment_key");
            String title = getArguments().getString("appointment_title");
            String date = getArguments().getString("appointment_date");
            String time = getArguments().getString("appointment_time");
            String details = getArguments().getString("appointment_details");

            appointmentTitleEditText.setText(title);
            appointmentDetailsEditText.setText(details);
            selectTimeButton.setText(convert24HourTo12Hour(time));

            // Set date in calendar view
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            try {
                selectedDate = sdf.parse(date);
                if (selectedDate != null) {
                    calendarView.setCurrentDate(selectedDate);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Update selectedDate on calendar day click
            calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
                @Override
                public void onDayClick(Date dateClicked) {
                    selectedDate = dateClicked;
                }

                @Override
                public void onMonthScroll(Date firstDayOfNewMonth) {
                    setCalendarDateDisplay(firstDayOfNewMonth);
                }
            });
        }
    }

    private String convert24HourTo12Hour(String time) {
        // Convert 24-hour format to 12-hour format
        try {
            SimpleDateFormat sdf24 = new SimpleDateFormat("HH:mm", Locale.getDefault());
            SimpleDateFormat sdf12 = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            Date date = sdf24.parse(time);
            return date != null ? sdf12.format(date) : "Select Time";
        } catch (ParseException e) {
            e.printStackTrace();
            return "Select Time";
        }
    }

    private void showTimePicker() {
        // Show time picker dialog
        MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
                .setMinute(Calendar.getInstance().get(Calendar.MINUTE))
                .setTitleText("Select Appointment Time")
                .build();

        materialTimePicker.addOnPositiveButtonClickListener(view -> {
            int hour = materialTimePicker.getHour();
            int minute = materialTimePicker.getMinute();
            String formattedTime = String.format(Locale.getDefault(), "%02d:%02d %s",
                    hour % 12 == 0 ? 12 : hour % 12,
                    minute,
                    hour < 12 ? "AM" : "PM");
            selectTimeButton.setText(formattedTime);
        });

        materialTimePicker.show(getParentFragmentManager(), "MATERIAL_TIME_PICKER");
    }

    private void updateAppointment() {
        // Update appointment details
        String newTitle = appointmentTitleEditText.getText().toString();
        String newDetails = appointmentDetailsEditText.getText().toString();
        String selectedTimeString = selectTimeButton.getText().toString(); // 12-hour format

        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String newDate = sdfDate.format(selectedDate);

        // Convert 12-hour format to 24-hour format for time
        SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {
            Date time = parseFormat.parse(selectedTimeString);
            String newTime = displayFormat.format(time);

            Appointment updatedAppointment = new Appointment(newTitle, newDate, newTime, newDetails);
            updatedAppointment.setKey(appointmentKey);
            appointmentManager.updateAppointment(updatedAppointment);

            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        } catch (ParseException e) {
            Toast.makeText(getActivity(), "Invalid time format", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateCalendar(int direction) {
        // direction: -1 for left (previous month), 1 for right (next month)
        Calendar calendar = Calendar.getInstance();
        if (calendarView != null) {
            calendar.setTime(calendarView.getFirstDayOfCurrentMonth());

            if (direction == -1) {
                // Navigate to the previous month
                calendar.add(Calendar.MONTH, -1);
            } else if (direction == 1) {
                // Navigate to the next month
                calendar.add(Calendar.MONTH, 1);
            }

            calendarView.setCurrentDate(calendar.getTime());
        }
    }

    private void setCalendarDateDisplay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        // SimpleDateFormat to get month name
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.getDefault());

        yearTextView.setText(String.valueOf(year));
        monthTextView.setText(monthFormat.format(date));
    }
}
