package com.example.swiftslotz.fragments.pageFragments;

import android.os.Bundle;
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

import androidx.fragment.app.Fragment;

import com.example.swiftslotz.utilities.Appointment;
import com.example.swiftslotz.utilities.AppointmentManager;
import com.example.swiftslotz.R;
import com.example.swiftslotz.utilities.User;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddAppointmentFragment extends Fragment {

    CompactCalendarView calendarView;
    EditText appointmentTitleEditText;
    EditText appointmentEditText;
    Button addAppointmentButton;
    Button selectTimeButton;
    TextView selectedTimeTextView, monthTextView, yearTextView;
    EditText appointmentDurationEditText;
    Spinner unitSpinner;
    String firebaseKey;
    ImageButton scrollLeftButton, scrollRightButton;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_appointment, container, false);


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

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.units_array, android.R.layout.simple_spinner_item);
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
        addAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String appointmentTitle = appointmentTitleEditText.getText().toString();
                String appointmentText = appointmentEditText.getText().toString();
                String selectedTimeString = selectTimeButton.getText().toString().replace("Selected Time: ", "");
                String selectedDateString = sdf.format(selectedDate.getTime());
                int appointmentDuration = Integer.parseInt(appointmentDurationEditText.getText().toString());
                String unit = unitSpinner.getSelectedItem().toString();

                if (appointmentTitle.isEmpty() || appointmentText.isEmpty() || selectedTimeString.isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter appointment title, details, and time", Toast.LENGTH_SHORT).show();
                    return;
                }

                int durationInMinutes;
                try {
                    if (unit.equals("H")) {
                        durationInMinutes = appointmentDuration * 60;
                        Toast.makeText(getActivity(), "Hour format inserted", Toast.LENGTH_SHORT).show();
                    } else {
                        durationInMinutes = appointmentDuration;
                        Toast.makeText(getActivity(), "Inserted format: " + unit, Toast.LENGTH_SHORT).show();
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

                // Initialize the AppointmentManager
                AppointmentManager appointmentManager = new AppointmentManager(getActivity());

                // Call addAppointment() method of the AppointmentManager
                appointmentManager.addAppointmentRequest(appointment,firebaseKey);
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
                selectTimeButton.setText("Selected Time: " + formattedTime);
            }
        });

        materialTimePicker.show(getParentFragmentManager(), "MATERIAL_TIME_PICKER");
    }
}
