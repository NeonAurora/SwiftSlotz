package com.example.swiftslotz.utilities;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swiftslotz.R;

import java.util.List;

public class RequestedAppointmentsAdapter extends RecyclerView.Adapter<RequestedAppointmentsAdapter.ViewHolder> {

    private List<Appointment> requestedAppointments;
    AppointmentManager appointmentManager;

    public RequestedAppointmentsAdapter(List<Appointment> requestedAppointments, AppointmentManager appointmentManager) {
        this.requestedAppointments = requestedAppointments;
        this.appointmentManager = appointmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.requested_appointment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = requestedAppointments.get(position);
        holder.appointmentTitle.setText(appointment.getTitle());
        holder.appointmentDate.setText(appointment.getDate());
        holder.appointmentTime.setText(appointment.getTime());
        holder.appointmentDetails.setText(appointment.getDetails());

        // Fetch client name using Firebase key
        appointmentManager.getClientNameFromKey(appointment.getRequestingUserFirebaseKey(), new ClientNameCallback() {
            @Override
            public void onClientNameReceived(String clientName) {
                holder.clientName.setText(clientName);
            }

            @Override
            public void onError(String error) {
                Log.e("ClientNameFetchError", "Error fetching client name: " + error);
            }
        });

        // Approve button click listener
        holder.approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Appointment appointment = requestedAppointments.get(position);
                String appointmentKey = appointment.getKey();
                appointmentManager.approveAppointment(appointment, appointmentKey);  // Using rootRef here
            }
        });

        // Reject button click listener
        holder.rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String appointmentKey = requestedAppointments.get(position).getKey();
                appointmentManager.rejectAppointment(appointmentKey);  // Using rootRef here
            }
        });
    }

    @Override
    public int getItemCount() {
        return requestedAppointments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView appointmentTitle;
        TextView clientName;
        TextView appointmentDate;
        TextView appointmentTime;
        TextView appointmentDetails;
        ImageButton approveButton, rejectButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appointmentTitle = itemView.findViewById(R.id.appointmentTitle);
            clientName = itemView.findViewById(R.id.clientName);
            appointmentDate = itemView.findViewById(R.id.appointmentDate);
            appointmentTime = itemView.findViewById(R.id.appointmentTime);
            appointmentDetails = itemView.findViewById(R.id.appointmentDetails);
            approveButton = itemView.findViewById(R.id.approveButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);
        }
    }
}
