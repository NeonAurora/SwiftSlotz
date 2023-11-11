package com.example.swiftslotz.fragments.pageFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.swiftslotz.R;
import com.example.swiftslotz.utilities.Appointment;
import com.example.swiftslotz.adapters.InvolvedUsersAdapter;

public class AppointmentDetailsFragment extends Fragment {

    private Appointment appointment;
    private RecyclerView involvedUsersRecyclerView;
    private InvolvedUsersAdapter involvedUsersAdapter;

    public static AppointmentDetailsFragment newInstance(Appointment appointment) {
        AppointmentDetailsFragment fragment = new AppointmentDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable("appointment", appointment); // Ensure Appointment is Serializable
        fragment.setArguments(args);
        return fragment;
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

        // Initialize UI elements based on the updated layout
        TextView titleView = view.findViewById(R.id.appointment_title_details);
        TextView dateView = view.findViewById(R.id.appointment_date_details);
        TextView timeView = view.findViewById(R.id.appointment_time_details);
        TextView detailsView = view.findViewById(R.id.appointment_details_details);
        TextView durationView = view.findViewById(R.id.appointment_duration_details);
        ImageView imageView = view.findViewById(R.id.appointment_image);
        involvedUsersRecyclerView = view.findViewById(R.id.involved_users_recyclerview);
        involvedUsersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        involvedUsersAdapter = new InvolvedUsersAdapter(getContext(), appointment.getInvolvedUsers());
        involvedUsersRecyclerView.setAdapter(involvedUsersAdapter);

        Button goBackButton = view.findViewById(R.id.go_back_button_details);

        // Set appointment details
        titleView.setText(appointment.getTitle());
        dateView.setText(appointment.getDate());
        timeView.setText(appointment.getTime());
        detailsView.setText(appointment.getDetails());
        durationView.setText(String.valueOf(appointment.getDuration()) + " minutes");

        goBackButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.popBackStack();
        });

        if (appointment.getImageUrl() != null && !appointment.getImageUrl().isEmpty()) {
            imageView.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(appointment.getImageUrl())
                    .into(imageView);
        }

        return view;
    }
}
