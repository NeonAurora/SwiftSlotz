package com.example.swiftslotz.fragments.pageFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.swiftslotz.R;
import com.example.swiftslotz.fragments.bottomBarFragments.AppointmentsFragment;
import com.example.swiftslotz.utilities.Appointment;
import com.example.swiftslotz.utilities.AppointmentManager;
import com.example.swiftslotz.adapters.RequestedAppointmentsAdapter;
import java.util.ArrayList;
import java.util.List;

public class RequestedAppointmentsFragment extends Fragment {

    private List<Appointment> requestedAppointments;
    private RequestedAppointmentsAdapter requestedAppointmentsAdapter;
    private RecyclerView requestedAppointmentsRecyclerView;
    AppointmentManager appointmentManager;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requestedAppointmentsAdapter.setOnLastAppointmentApprovedListener(new RequestedAppointmentsAdapter.OnLastAppointmentApprovedListener() {
            @Override
            public void onLastAppointmentApproved() {
                // Navigate to AppointmentsFragment
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame, new AppointmentsFragment());
                transaction.commit();
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_requested_appointments, container, false);

        requestedAppointmentsRecyclerView = view.findViewById(R.id.requestedAppointmentsRecyclerView);
        requestedAppointments = new ArrayList<>();
        AppointmentManager appointmentManager1 = new AppointmentManager(getActivity());
        requestedAppointmentsAdapter = new RequestedAppointmentsAdapter(getActivity(), requestedAppointments,appointmentManager1);
        appointmentManager=new AppointmentManager(getActivity(), requestedAppointments, requestedAppointmentsAdapter);
        requestedAppointmentsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        requestedAppointmentsRecyclerView.setAdapter(requestedAppointmentsAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        appointmentManager.fetchRequestedAppointmentsFromDatabase();
    }
}