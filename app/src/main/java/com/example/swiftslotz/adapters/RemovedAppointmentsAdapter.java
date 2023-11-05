package com.example.swiftslotz.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swiftslotz.R;
import com.example.swiftslotz.utilities.Appointment;

import java.util.List;

public class RemovedAppointmentsAdapter extends RecyclerView.Adapter<RemovedAppointmentsAdapter.ViewHolder> {
    private List<Appointment> removedAppointments;
    private Context context;

    public RemovedAppointmentsAdapter(Context context, List<Appointment> removedAppointments) {
        this.context = context;
        this.removedAppointments = removedAppointments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.removed_appointment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = removedAppointments.get(position);
        holder.removedAppointmentTitle.setText(appointment.getTitle());
        holder.removedAppointmentDate.setText(appointment.getDate());
        holder.removedAppointmentTime.setText(appointment.getTime());
        holder.removedAppointmentDetails.setText(appointment.getDetails());
    }

    @Override
    public int getItemCount() {
        return removedAppointments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView removedAppointmentTitle, removedAppointmentDate, removedAppointmentTime, removedAppointmentDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            removedAppointmentTitle = itemView.findViewById(R.id.removedAppointmentTitle);
            removedAppointmentDate = itemView.findViewById(R.id.removedAppointmentDate);
            removedAppointmentTime = itemView.findViewById(R.id.removedAppointmentTime);
            removedAppointmentDetails = itemView.findViewById(R.id.removedAppointmentDetails);
        }
    }
}
