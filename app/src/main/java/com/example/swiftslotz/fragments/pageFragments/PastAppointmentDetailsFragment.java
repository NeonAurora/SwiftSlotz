package com.example.swiftslotz.fragments.pageFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swiftslotz.R;
import com.example.swiftslotz.utilities.Appointment;
import com.example.swiftslotz.adapters.InvolvedUsersAdapter;
import com.example.swiftslotz.adapters.ImageAdapter;
import com.example.swiftslotz.utilities.AppointmentManager;

public class PastAppointmentDetailsFragment extends Fragment {

    private Appointment appointment;
    private RecyclerView involvedUsersRecyclerView, imagesRecyclerView;
    private InvolvedUsersAdapter involvedUsersAdapter;
    private ImageAdapter imageAdapter;
    private AppointmentManager appointmentManager;

    public static PastAppointmentDetailsFragment newInstance(Appointment appointment, AppointmentManager appointmentManager) {
        PastAppointmentDetailsFragment fragment = new PastAppointmentDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable("appointment", appointment); // Ensure Appointment is Serializable
        fragment.setAppointmentManager(appointmentManager);
        fragment.setArguments(args);
        return fragment;
    }

    public void setAppointmentManager(AppointmentManager appointmentManager) {
        this.appointmentManager = appointmentManager;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            appointment = (Appointment) getArguments().getSerializable("appointment");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointment_details, container, false);

        // Initialize UI elements
        TextView titleView = view.findViewById(R.id.appointment_title_details);
        TextView dateView = view.findViewById(R.id.appointment_date_details);
        TextView timeView = view.findViewById(R.id.appointment_time_details);
        TextView detailsView = view.findViewById(R.id.appointment_details_details);
        TextView hostUserView = view.findViewById(R.id.hosted_by_user_textview_details);
        TextView durationView = view.findViewById(R.id.appointment_duration_details);
        involvedUsersRecyclerView = view.findViewById(R.id.involved_users_recyclerview);
        imagesRecyclerView = view.findViewById(R.id.appointment_images_recyclerview);

        // Set up RecyclerViews
        involvedUsersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Log.d("AppointmentIUDetails", "onCreateView: " + appointment.getInvolvedUsers().toString());
        involvedUsersAdapter = new InvolvedUsersAdapter(getContext(), appointment.getInvolvedUsers());
        involvedUsersRecyclerView.setAdapter(involvedUsersAdapter);

        imagesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Log.d("AppointmentImageDetails", "onCreateView: " + appointment.getImageUrls().toString());
        imageAdapter = new ImageAdapter(getContext(), appointment.getImageUrls(), appointmentManager, appointment.getKey()); // Assuming getImageUrls() returns List<String>
        imagesRecyclerView.setAdapter(imageAdapter);

        Button goBackButton = view.findViewById(R.id.go_back_button_details);

        // Set appointment details
        titleView.setText(appointment.getTitle());
        dateView.setText(appointment.getDate());
        timeView.setText(appointment.getTime());
        detailsView.setText(appointment.getDetails());
        durationView.setText(String.valueOf(appointment.getDuration()) + " minutes");

        // Fetch and set the Host user's name
        appointmentManager.getUserNameFromFirebaseKey(appointment.getHostUserFirebaseKey(), userName -> {
            hostUserView.setText(userName);
        });

        goBackButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.popBackStack();
        });

        return view;
    }
}
