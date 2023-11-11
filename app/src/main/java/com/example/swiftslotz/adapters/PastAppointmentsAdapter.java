package com.example.swiftslotz.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swiftslotz.R;
import com.example.swiftslotz.utilities.Appointment;

import java.util.List;

public class PastAppointmentsAdapter extends RecyclerView.Adapter<PastAppointmentsAdapter.ViewHolder> {
    private List<Appointment> pastAppointments;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private OnUploadButtonClickListener onUploadButtonClickListener;

    public interface OnItemClickListener {
        void onItemClick(Appointment appointment);
    }

    public interface OnUploadButtonClickListener {
        void onUploadButtonClick(Appointment appointment);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnUploadButtonClickListener(OnUploadButtonClickListener onUploadButtonClickListener) {
        this.onUploadButtonClickListener = onUploadButtonClickListener;
    }

    public PastAppointmentsAdapter(Context context, List<Appointment> pastAppointments) {
        this.context = context;
        this.pastAppointments = pastAppointments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.past_appointment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = pastAppointments.get(position);
        holder.pastAppointmentTitle.setText(appointment.getTitle());
        holder.pastAppointmentDate.setText(appointment.getDate());
        holder.pastAppointmentTime.setText(appointment.getTime());
        holder.pastAppointmentDetails.setText(appointment.getDetails());
    }

    @Override
    public int getItemCount() {
        return pastAppointments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView pastAppointmentTitle, pastAppointmentDate, pastAppointmentTime, pastAppointmentDetails;
        Button uploadImageButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pastAppointmentTitle = itemView.findViewById(R.id.pastAppointmentTitle);
            pastAppointmentDate = itemView.findViewById(R.id.pastAppointmentDate);
            pastAppointmentTime = itemView.findViewById(R.id.pastAppointmentTime);
            pastAppointmentDetails = itemView.findViewById(R.id.pastAppointmentDetails);
            uploadImageButton = itemView.findViewById(R.id.uploadImageButton);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION)
                        onItemClickListener.onItemClick(pastAppointments.get(getAdapterPosition()));
                }
            });

            uploadImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onUploadButtonClickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION)
                        onUploadButtonClickListener.onUploadButtonClick(pastAppointments.get(getAdapterPosition()));
                }
            });
        }
    }
}

