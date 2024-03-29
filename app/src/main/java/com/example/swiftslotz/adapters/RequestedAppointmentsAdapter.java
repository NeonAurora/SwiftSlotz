package com.example.swiftslotz.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swiftslotz.R;
import com.example.swiftslotz.utilities.Appointment;
import com.example.swiftslotz.utilities.AppointmentManager;
import com.example.swiftslotz.utilities.ClientNameCallback;

import java.util.List;

public class RequestedAppointmentsAdapter extends RecyclerView.Adapter<RequestedAppointmentsAdapter.ViewHolder> {

    public interface OnLastAppointmentApprovedListener {
        void onLastAppointmentApproved();
    }


    private List<Appointment> requestedAppointments;
    AppointmentManager appointmentManager;
    private Context context;
    private OnLastAppointmentApprovedListener listener;

    public RequestedAppointmentsAdapter(Context context, List<Appointment> requestedAppointments, AppointmentManager appointmentManager) {
        this.context = context;
        this.requestedAppointments = requestedAppointments;
        this.appointmentManager = appointmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.requested_appointment_item, parent, false);
        return new ViewHolder(view);
    }

    public void setOnLastAppointmentApprovedListener(OnLastAppointmentApprovedListener listener) {
        this.listener = listener;
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
        // Approve button click listener
        holder.approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Appointment appointment = requestedAppointments.get(position);
                String appointmentKey = appointment.getKey();
                appointmentManager.approveAppointment(appointment, appointmentKey);  // Using rootRef here

                // Check if this is the last appointment and notify the listener
                if (requestedAppointments.size() == 1 && listener != null) {
                    listener.onLastAppointmentApproved();
                }
//                SharedPreferences prefs = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
//                prefs.edit().putBoolean("refreshNeeded", true).apply();
            }
        });


        // Reject button click listener
        holder.rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String appointmentKey = requestedAppointments.get(position).getKey();
                appointmentManager.rejectAppointment(appointmentKey);  // Using rootRef here
                if (requestedAppointments.size() == 1 && listener != null) {
                    listener.onLastAppointmentApproved();
                }
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
        Button approveButton, rejectButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appointmentTitle = itemView.findViewById(R.id.appointmentTitle);
            clientName = itemView.findViewById(R.id.requestingClientName);
            appointmentDate = itemView.findViewById(R.id.appointmentDate);
            appointmentTime = itemView.findViewById(R.id.appointmentTime);
            appointmentDetails = itemView.findViewById(R.id.appointmentDetails);
            approveButton = itemView.findViewById(R.id.approveButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);
        }
    }
}
