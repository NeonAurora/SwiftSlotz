package com.example.swiftslotz.utilities;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swiftslotz.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;

    public UserAdapter(List<User> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list_item, parent, false);
        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.usernameTextView.setText(user.getUsername());

        // Adding click listener
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUserDetailModal(v.getContext(), user);
            }
        });
    }


    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView usernameTextView;

        public UserViewHolder(View view) {
            super(view);
            usernameTextView = view.findViewById(R.id.usernameTextView);
        }
    }

    private void showUserDetailModal(Context context, User user) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.user_detail_modal);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        // Set the dimensions of the dialog
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = (int)(width * 0.95);  // Set width to 90% of screen width
        lp.height = (int)(height * 0.4);  // Set height to 70% of screen height
        dialog.getWindow().setAttributes(lp);

        // Initialize TextViews
        TextView firstNameTextView = dialog.findViewById(R.id.firstNameTextView);
        TextView lastNameTextView = dialog.findViewById(R.id.lastNameTextView);
        TextView usernameTextView = dialog.findViewById(R.id.usernameTextView);
        TextView emailTextView = dialog.findViewById(R.id.emailTextView);
        TextView phoneTextView = dialog.findViewById(R.id.phoneTextView);
        TextView companyTextView = dialog.findViewById(R.id.companyTextView);
        TextView addressTextView = dialog.findViewById(R.id.addressTextView);

        // Initialize Buttons
        Button getAppointmentButton = dialog.findViewById(R.id.getAppointmentButton);
        Button goBackButton = dialog.findViewById(R.id.goBackButton);

        // Set user details
        firstNameTextView.setText("First Name: " + user.getFirstName());
        lastNameTextView.setText("Last Name: " + user.getLastName());
        usernameTextView.setText("Username: " + user.getUsername());
        emailTextView.setText("Email: " + user.getEmail());
        phoneTextView.setText("Phone: " + user.getPhone());
        companyTextView.setText("Company: " + user.getCompany());
        addressTextView.setText("Address: " + user.getAddress());

        // Set button click listeners
        getAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement your logic here for getting an appointment
                dialog.dismiss();
            }
        });

        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}

