package com.example.swiftslotz.fragments.bottomBarFragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.swiftslotz.BuildConfig;
import com.example.swiftslotz.fragments.pageFragments.AddAppointmentFragment;
import com.example.swiftslotz.fragments.pageFragments.ModifyAppointmentFragment;
import com.example.swiftslotz.fragments.pageFragments.RequestedAppointmentsFragment;
import com.example.swiftslotz.utilities.Appointment;
import com.example.swiftslotz.utilities.AppointmentManager;
import com.example.swiftslotz.adapters.AppointmentsAdapter;
import com.example.swiftslotz.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AppointmentsFragment extends Fragment implements AppointmentsAdapter.OnAppointmentInteractionListener, AppointmentManager.OnAppointmentsFetchedListener {

    private List<Appointment> appointments;
    private AppointmentsAdapter appointmentsAdapter;
    private RecyclerView appointmentsRecyclerView;
    private AppointmentManager appointmentManager;
    private TextView badgeTextView;

    private FirebaseAuth mAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getContext() != null) {
            appointmentManager = new AppointmentManager(getContext(), appointments, appointmentsAdapter);
            appointmentManager.setOnAppointmentsFetchedListener(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_appointments, container, false);
        mAuth = FirebaseAuth.getInstance();

        appointmentsRecyclerView = view.findViewById(R.id.appointmentsRecyclerView);
        badgeTextView = view.findViewById(R.id.badge_text_view);
        appointments = new ArrayList<>();
        AppointmentManager appointmentManager1 = new AppointmentManager(getActivity());
        appointmentsAdapter = new AppointmentsAdapter(appointments, this, appointmentManager1);
        appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        appointmentsRecyclerView.setAdapter(appointmentsAdapter);

        appointmentManager = new AppointmentManager(getActivity(), appointments, appointmentsAdapter);
        appointmentManager.setOnAppointmentsFetchedListener(this);

        FloatingActionButton appointmentButton = view.findViewById(R.id.incomingRequestsAppointmentButton);
        appointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddAppointmentFragment addAppointmentFragment = new AddAppointmentFragment();
                RequestedAppointmentsFragment requestedAppointmentsFragment = new RequestedAppointmentsFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, requestedAppointmentsFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        appointmentManager.fetchCountOfRequestedAppointments(new AppointmentManager.RequestedAppointmentCountCallback() {

            @Override
            public void onFetched(int count) {
                updateBadge(count);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(getContext(), "Error fetching count of requested appointments: " + error, Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    private void updateBadge(int count) {
        if (badgeTextView != null) {
            if (count > 0) {
                badgeTextView.setText(String.valueOf(count));
                badgeTextView.setVisibility(View.VISIBLE);
            } else {
                badgeTextView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (appointments != null) {
            appointments.clear();
        }
        appointmentManager.fetchAppointmentsFromDatabase();
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences prefs = getActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        boolean refreshNeeded = prefs.getBoolean("refreshNeeded", false);
        if (refreshNeeded) {
            appointmentManager.fetchAppointmentsFromDatabase();
            prefs.edit().remove("refreshNeeded").apply();
        }
    }


    @Override
    public void onEditAppointment(Appointment appointment) {
        startModifyAppointmentFragment(appointment.getId(), appointment.getTitle(), appointment.getDate(), appointment.getTime(), appointment.getDetails(), appointment.getKey());
    }

    @Override
    public void onDeleteAppointment(Appointment appointment) {
        appointmentManager.deleteAppointment(appointment);
    }

    @Override
    public void onAppointmentsFetched(List<Appointment> fetchedAppointments) {
        sortAndDisplayAppointments(fetchedAppointments);
    }

    private void sortAndDisplayAppointments(List<Appointment> fetchedAppointments) {
        // Calculate time to start for each appointment and sort
        List<Appointment> sortedAppointments = new ArrayList<>(fetchedAppointments);
        for (Appointment appointment : sortedAppointments) {
            appointment.setTimeToStart(calculateTimeToStart(appointment));
        }
        Collections.sort(sortedAppointments, (a1, a2) -> Long.compare(a1.getTimeToStart(), a2.getTimeToStart()));

        // Update the adapter
        appointments.clear();
        appointments.addAll(sortedAppointments);
        appointmentsAdapter.notifyDataSetChanged();
    }

    private long calculateTimeToStart(Appointment appointment) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        try {
            Date appointmentDate = sdf.parse(appointment.getDate() + " " + appointment.getTime());
            if (appointmentDate != null) {
                long diffInMillis = appointmentDate.getTime() - System.currentTimeMillis();
                return diffInMillis / 1000; // Convert milliseconds to seconds
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Long.MAX_VALUE; // Return a large value if parsing fails
    }


    public void startModifyAppointmentFragment(int appointmentId, String title, String date, String time, String details, String key) {
        ModifyAppointmentFragment modifyAppointmentFragment = new ModifyAppointmentFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("appointment_id", appointmentId);
        bundle.putString("appointment_title", title);
        bundle.putString("appointment_date", date);
        bundle.putString("appointment_time", time);
        bundle.putString("appointment_details", details);
        bundle.putString("appointment_key", key);
        modifyAppointmentFragment.setArguments(bundle);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, modifyAppointmentFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
