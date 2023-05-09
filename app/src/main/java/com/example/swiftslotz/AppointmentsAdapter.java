package com.example.swiftslotz;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.MenuItem;

import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.PopupMenu;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.ViewHolder> {

    private List<Appointment> appointments;

    public AppointmentsAdapter(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_item, parent, false);
        return new ViewHolder(view);
    }
    private void showPopupMenu(View view, int position) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.inflate(R.menu.appointment_options);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_edit:
                        // Handle edit action
                        return true;
                    case R.id.action_delete:
                        // Handle delete action
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);
        holder.appointmentTitle.setText(appointment.getTitle());
        holder.appointmentDate.setText(appointment.getDate());
        holder.appointmentTime.setText(appointment.getTime());

        holder.optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v, holder.getAdapterPosition());
            }
        });
    }


    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView appointmentTitle;
        TextView appointmentDate;
        TextView appointmentTime;
        ImageButton optionsButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appointmentTitle = itemView.findViewById(R.id.appointmentTitle);
            appointmentDate = itemView.findViewById(R.id.appointmentDate);
            appointmentTime = itemView.findViewById(R.id.appointmentTime);
            optionsButton = itemView.findViewById(R.id.appointmentOptions);
        }
    }
}

