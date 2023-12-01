package com.example.swiftslotz.fragments.pageFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swiftslotz.BuildConfig;
import com.example.swiftslotz.R;
import com.example.swiftslotz.fragments.bottomBarFragments.AppointmentsFragment;
import com.example.swiftslotz.utilities.Appointment;
import com.example.swiftslotz.utilities.AppointmentManager;
import com.example.swiftslotz.adapters.RequestedAppointmentsAdapter;
import com.example.swiftslotz.utilities.BaseActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RequestedAppointmentsFragment extends Fragment {

    private List<Appointment> requestedAppointments;
    private RequestedAppointmentsAdapter requestedAppointmentsAdapter;
    private RecyclerView requestedAppointmentsRecyclerView;
    AppointmentManager appointmentManager;
    private TextView tvNoRequestedAppointments;
    private Button goBackButton;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String userId = mAuth.getCurrentUser().getUid();
    private DatabaseReference userDb = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL).getReference("users").child(userId).child("RequestedAppointments");

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requestedAppointmentsAdapter.setOnLastAppointmentApprovedListener(new RequestedAppointmentsAdapter.OnLastAppointmentApprovedListener() {
            @Override
            public void onLastAppointmentApproved() {
                tvNoRequestedAppointments.setVisibility(View.VISIBLE);
                goBackButton.setVisibility(View.VISIBLE);
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_requested_appointments, container, false);

        if (getActivity() != null) {
            ((BaseActivity) getActivity()).updateBottomNavigationForFragment("FragmentX");
        }

        requestedAppointmentsRecyclerView = view.findViewById(R.id.requestedAppointmentsRecyclerView);
        tvNoRequestedAppointments = view.findViewById(R.id.tvNoRequestedAppointments);
        goBackButton = view.findViewById(R.id.goBackButton);
        requestedAppointments = new ArrayList<>();
        AppointmentManager appointmentManager1 = new AppointmentManager(getActivity());
        requestedAppointmentsAdapter = new RequestedAppointmentsAdapter(getActivity(), requestedAppointments,appointmentManager1);
        appointmentManager = new AppointmentManager(getActivity(), requestedAppointments, requestedAppointmentsAdapter);

//        appointmentManager.fetchRequestedAppointmentsFromDatabase();
            userDb.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    requestedAppointments.clear();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String today = sdf.format(new Date());

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Appointment appointment = snapshot.getValue(Appointment.class);
                        if (appointment != null) {
                            appointment.setKey(snapshot.getKey());
                            requestedAppointments.add(appointment);
                        }
                    }
                    if(requestedAppointments.size()==0){
                        tvNoRequestedAppointments.setVisibility(View.VISIBLE);
                        goBackButton.setVisibility(View.VISIBLE);
                    }
                    else{
                        tvNoRequestedAppointments.setVisibility(View.GONE);
                        goBackButton.setVisibility(View.GONE);
                    }
                    if (requestedAppointmentsAdapter != null) {
                        requestedAppointmentsAdapter.notifyDataSetChanged();
                    } else {
                        Log.e("RequestedAppointments", "Adapter is null");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext(), "Failed to fetch data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        requestedAppointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        requestedAppointmentsRecyclerView.setAdapter(requestedAppointmentsAdapter);

        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                AppointmentsFragment appointmentsFragment = new AppointmentsFragment();
                fragmentTransaction.replace(R.id.content_frame, appointmentsFragment);
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    private void updateUIBasedOnRequests(List<Appointment> appointments) {
        if (appointments.isEmpty()) {
            tvNoRequestedAppointments.setVisibility(View.VISIBLE);
            goBackButton.setVisibility(View.VISIBLE);
        } else {
            tvNoRequestedAppointments.setVisibility(View.GONE);
            goBackButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }
}