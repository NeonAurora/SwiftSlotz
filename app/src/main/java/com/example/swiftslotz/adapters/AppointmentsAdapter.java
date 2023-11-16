package com.example.swiftslotz.adapters;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.MenuItem;

import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swiftslotz.R;
import com.example.swiftslotz.utilities.Appointment;
import com.example.swiftslotz.utilities.AppointmentManager;
import com.example.swiftslotz.utilities.ClientNameCallback;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.ViewHolder> {

    private List<Appointment> appointments;
    private OnAppointmentInteractionListener listener;
    AppointmentManager appointmentManager;
    private FirebaseAuth mAuth;
    private String appointmentKey;

    public AppointmentsAdapter(List<Appointment> appointments, OnAppointmentInteractionListener listener, AppointmentManager appointmentManager) {
        mAuth = FirebaseAuth.getInstance();
        this.appointments = appointments;
        this.listener = listener;
        this.appointmentManager = appointmentManager;
    }

    public interface OnAppointmentInteractionListener {
        void onEditAppointment(Appointment appointment);
        void onDeleteAppointment(Appointment appointment);
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

        Appointment appointment = appointments.get(position);
        appointmentKey = appointment.getKey();
        Log.d("isCurrentUserHost", isCurrentUserHost(appointment) + "");
        if (isCurrentUserHost(appointment)) {
            popupMenu.getMenu().add(Menu.NONE, R.id.action_set_constraints, Menu.NONE, "Set Constraints");
        }


        //check whether current user is host of appointment
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Appointment appointment = appointments.get(position);
                switch (item.getItemId()) {
                    case R.id.action_edit:
                        if (listener != null) {
                            listener.onEditAppointment(appointment);
                        }
                        return true;
                    case R.id.action_delete:
                        if (listener != null) {
                            listener.onDeleteAppointment(appointment);
                        }
                        return true;
                    case R.id.action_set_constraints:
                        showDialogToSetConstraint(view.getContext(), appointmentKey);
                        return true;
                    default:
                    return false;
                }
            }
        });
        popupMenu.show();
    }

    private boolean isCurrentUserHost(Appointment appointment) {
        String currentUserId = mAuth.getCurrentUser().getUid();
        return currentUserId.equals(appointment.getHostUserFirebaseKey());
    }

    private void showDialogToSetConstraint(Context context, String appointmentKey) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_set_constraints, null);

        EditText durationEditText = dialogView.findViewById(R.id.durationEditText);
        Spinner unitSpinner = dialogView.findViewById(R.id.unitSpinner);
        Button confirmButton = dialogView.findViewById(R.id.confirmButton);

        // Set up the spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.units_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitSpinner.setAdapter(adapter);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout((int) (getScreenWidth(context) * 0.8), WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCanceledOnTouchOutside(true);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String durationStr = durationEditText.getText().toString();
                String unit = unitSpinner.getSelectedItem().toString();

                if (durationStr.isEmpty()) {
                    Toast.makeText(context, "Please enter a duration", Toast.LENGTH_SHORT).show();
                    return;
                }

                int duration;
                try {
                    duration = Integer.parseInt(durationStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(context, "Invalid duration format", Toast.LENGTH_SHORT).show();
                    return;
                }

                int durationInMinutes = unit.equals("H") ? duration * 60 : duration;

                // Assuming you have the appointmentKey and AppointmentManager instance
                appointmentManager.setAppointmentTimeConstraint(appointmentKey, durationInMinutes);

                dialog.dismiss();
            }
        });


        dialog.show();
    }

    private int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);
        holder.appointmentTitle.setText(appointment.getTitle());
        holder.appointmentDate.setText(appointment.getDate());
        holder.appointmentTime.setText(appointment.getTime());
        holder.appointmentDetails.setText(appointment.getDetails());
        holder.progressBar.setProgress(appointment.getProgressPercentage());

        appointmentManager.getClientNameFromKey(appointment.getRequestingUserFirebaseKey(), new ClientNameCallback() {
            @Override
            public void onClientNameReceived(String clientName) {
                holder.acceptedClientName.setText(clientName);
            }

            @Override
            public void onError(String error) {
                Log.e("ClientNameFetchError", "Error fetching client name: " + error);
            }
        });



        holder.optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v, holder.getAdapterPosition());
            }
        });

        holder.detailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String appointmentKey = appointments.get(position).getKey();

                ClipboardManager clipboard = (ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("appointmentKey", appointmentKey);

                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(view.getContext(), "successfully copied", Toast.LENGTH_SHORT).show();
                }
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
        TextView acceptedClientName;

        TextView appointmentDetails;
        ProgressBar progressBar;

        ImageButton optionsButton, detailsButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appointmentTitle = itemView.findViewById(R.id.appointmentTitle);
            appointmentDate = itemView.findViewById(R.id.appointmentDate);
            appointmentTime = itemView.findViewById(R.id.appointmentTime);
            appointmentDetails = itemView.findViewById(R.id.appointmentDetails);
            acceptedClientName = itemView.findViewById(R.id.acceptedClientName);
            optionsButton = itemView.findViewById(R.id.appointmentOptions);
            detailsButton = itemView.findViewById(R.id.details);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
