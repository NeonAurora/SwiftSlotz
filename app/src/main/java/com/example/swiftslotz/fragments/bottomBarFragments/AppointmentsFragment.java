package com.example.swiftslotz.fragments.bottomBarFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.swiftslotz.fragments.pageFragments.AddAppointmentFragment;
import com.example.swiftslotz.fragments.pageFragments.ModifyAppointmentFragment;
import com.example.swiftslotz.fragments.pageFragments.RequestedAppointmentsFragment;
import com.example.swiftslotz.utilities.Appointment;
import com.example.swiftslotz.utilities.AppointmentManager;
import com.example.swiftslotz.adapters.AppointmentsAdapter;
import com.example.swiftslotz.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AppointmentsFragment extends Fragment implements AppointmentsAdapter.OnAppointmentInteractionListener {

    private List<Appointment> appointments;
    private AppointmentsAdapter appointmentsAdapter;
    private RecyclerView appointmentsRecyclerView;
    private AppointmentManager appointmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_appointments, container, false);

        appointmentsRecyclerView = view.findViewById(R.id.appointmentsRecyclerView);
        appointments = new ArrayList<>();
        AppointmentManager appointmentManager1 = new AppointmentManager(getActivity());
        appointmentsAdapter = new AppointmentsAdapter(appointments, this, appointmentManager1);
        appointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        appointmentsRecyclerView.setAdapter(appointmentsAdapter);

        appointmentManager = new AppointmentManager(getActivity(), appointments, appointmentsAdapter);

        FloatingActionButton appointmentButton = view.findViewById(R.id.addAppointmentButton);
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
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        appointmentManager.fetchAppointmentsFromDatabase();
    }

    @Override
    public void onEditAppointment(Appointment appointment) {
        startModifyAppointmentFragment(appointment.getId(), appointment.getTitle(), appointment.getDate(), appointment.getTime(), appointment.getDetails(), appointment.getKey());
    }

    @Override
    public void onDeleteAppointment(Appointment appointment) {
        appointmentManager.deleteAppointment(appointment);
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
