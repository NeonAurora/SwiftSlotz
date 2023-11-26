package com.example.swiftslotz.adapters;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.MenuItem;

import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swiftslotz.R;
import com.example.swiftslotz.utilities.Appointment;
import com.example.swiftslotz.utilities.AppointmentManager;
import com.example.swiftslotz.utilities.ClientNameCallback;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.ViewHolder> {

    private List<Appointment> appointments;
    private OnAppointmentInteractionListener listener;
    AppointmentManager appointmentManager;
    private FirebaseAuth mAuth;
    private String appointmentKey;
    private Context context;
    private RecyclerView appointmentsRecyclerView;

    public AppointmentsAdapter(List<Appointment> appointments, OnAppointmentInteractionListener listener, AppointmentManager appointmentManager, Context context, RecyclerView appointmentsRecyclerView) {
        mAuth = FirebaseAuth.getInstance();
        this.appointments = appointments;
        this.listener = listener;
        this.appointmentManager = appointmentManager;
        this.context = context;
        this.appointmentsRecyclerView = appointmentsRecyclerView;
    }

    public interface OnAppointmentInteractionListener {
        void onEditAppointment(Appointment appointment);
        void onLeaveAppointment(Appointment appointment, int position);
        void onDeleteAppointment(String appointmentKey);
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
            popupMenu.getMenu().add(Menu.NONE, R.id.action_delete, Menu.NONE, "Delete");
            popupMenu.getMenu().removeItem(R.id.action_leave);
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
                    case R.id.action_leave:
                        if (listener != null) {
                            listener.onLeaveAppointment(appointment,position);
                            removeItemWithAnimation(position);
                        }
                        return true;
                    case R.id.action_set_constraints:
                        showDialogToSetConstraint(view.getContext(), appointmentKey);
                        return true;
                    case R.id.action_delete:
                        listener.onDeleteAppointment(appointmentKey);
                        removeItemWithAnimation(position);
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

    private void changeBackgroundGradient(View view) {
        Drawable backgroundImage = ContextCompat.getDrawable(view.getContext(), R.drawable.notif_back_image);

        GradientDrawable gradientDrawable = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[] {
                        Color.parseColor("#aa466cdd"), // 100% opacity
                        Color.parseColor("#8093a9eb"), // 50% opacity
                        Color.parseColor("#00e9eefb")  // 10% opacity
                }
        );

        Drawable[] layers = {backgroundImage, gradientDrawable};
        LayerDrawable layerDrawable = new LayerDrawable(layers);

        view.setBackground(layerDrawable);
    }

    public void removeItemWithAnimation(int position) {
        ViewHolder holder = (ViewHolder) this.appointmentsRecyclerView.findViewHolderForAdapterPosition(position);
        if (holder != null) {
            Log.d("AppointmentsAdapter", "removeItemWithAnimation: holder != null");
            Animation anim = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.slide_out_right);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    // Do nothing
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    appointments.remove(position);
                    notifyItemRemoved(position);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // Do nothing
                }
            });
            holder.itemView.startAnimation(anim);
        } else {
            appointments.remove(position);
            notifyItemRemoved(position);
        }
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

    public void updateProgressSmoothly(LinearProgressIndicator progressBar, int newProgress) {
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", newProgress);
        animation.setDuration(500); // Set duration in milliseconds (e.g., 200ms)
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.start();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appointment = appointments.get(holder.getAdapterPosition());
        holder.appointmentTitle.setText(appointment.getTitle());
        holder.appointmentDate.setText(appointment.getDate());
        holder.appointmentTime.setText(appointment.getTime());
        holder.appointmentDetails.setText(appointment.getDetails());

        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.slide_in_left);
        long startOffset = 200 + (holder.getAdapterPosition() * 100);
        animation.setStartOffset(startOffset);

        if (!appointment.isAnimated()) {

            holder.itemView.startAnimation(animation);

            new Handler(Looper.getMainLooper()).postDelayed(() -> appointment.setAnimated(true), 50);
        }

//        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.slide_in_left);
//        long startOffset = 500 + (position * 100);
//        animation.setStartOffset(startOffset);
//        holder.itemView.startAnimation(animation);

        if(appointment.isLinearProgressVisible()){
            holder.linearProgressBar.setVisibility(View.VISIBLE);
            updateProgressSmoothly(holder.linearProgressBar, appointment.getLinearProgressPercentage());
        } else {
            holder.linearProgressBar.setVisibility(View.GONE);
        }

        if(appointment.isCircularProgressVisible()) {
            holder.circularProgressBar.setVisibility(View.VISIBLE);
            holder.circularProgressIndicator2.setVisibility(View.VISIBLE);
            holder.circularProgressBar.setProgress(appointment.getCircularProgressPercentage());
        } else {
            holder.circularProgressBar.setVisibility(View.GONE);
            holder.circularProgressIndicator2.setVisibility(View.GONE);
        }

        if (appointment.getTimeToStart() <= 0) {
            changeBackgroundGradient(holder.constraintLayout);
        } else {
            // Reset to original background if needed
            holder.constraintLayout.setBackgroundResource(R.drawable.notify_background);
        }



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
                String appointmentKey = appointments.get(holder.getAdapterPosition()).getKey();

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
        ConstraintLayout     constraintLayout;

        TextView appointmentDetails;
        LinearProgressIndicator linearProgressBar;
        CircularProgressIndicator circularProgressBar, circularProgressIndicator2;

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
            linearProgressBar = itemView.findViewById(R.id.material_progress_bar);
            circularProgressBar = itemView.findViewById(R.id.circular_progress_bar);
            circularProgressIndicator2 = itemView.findViewById(R.id.circular_progress_bar2);
            constraintLayout = itemView.findViewById(R.id.appointmentItemContainer);
        }
    }
}
