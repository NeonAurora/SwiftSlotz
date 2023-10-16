package com.example.swiftslotz.utilities;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swiftslotz.R;
import com.example.swiftslotz.fragments.pageFragments.AddAppointmentFragment;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private List<String> firebaseKeys;
    private FragmentManager fragmentManager;

    public UserAdapter(List<User> userList, List<String> firebaseKeys, FragmentManager fragmentManager) {
        this.userList = userList;
        this.firebaseKeys = firebaseKeys;
        this.fragmentManager = fragmentManager;
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
        String firebaseGeneratedKey = firebaseKeys.get(position);
        holder.usernameTextView.setText(user.getUsername());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUserDetailModal(v.getContext(), user, firebaseGeneratedKey);
            }
        });
    }

    private void showUserDetailModal(Context context, User user, String firebaseKey) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.user_detail_modal);

        // Set the dimensions of the dialog
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;  // Set width to 100% of screen width
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;  // Set height to wrap content
        dialog.getWindow().setAttributes(lp);

        // Initialize TextViews and Buttons
        TextView firstNameTextView = dialog.findViewById(R.id.firstNameTextView);
        TextView lastNameTextView = dialog.findViewById(R.id.lastNameTextView);
        TextView usernameTextView = dialog.findViewById(R.id.usernameTextView);
        TextView emailTextView = dialog.findViewById(R.id.emailTextView);
        TextView phoneTextView = dialog.findViewById(R.id.phoneTextView);
        TextView companyTextView = dialog.findViewById(R.id.companyTextView);
        TextView addressTextView = dialog.findViewById(R.id.addressTextView);

        // Set user details
        firstNameTextView.setText("First Name: " + user.getFirstName());
        lastNameTextView.setText("Last Name: " + user.getLastName());
        usernameTextView.setText("Username: " + user.getUsername());
        emailTextView.setText("Email: " + user.getEmail());
        phoneTextView.setText("Phone: " + user.getPhone());
        companyTextView.setText("Occupation: " + user.getOccupation());
        addressTextView.setText("Address: " + user.getAddress());

        Button getAppointmentButton = dialog.findViewById(R.id.getAppointmentButton);
        Button goBackButton = dialog.findViewById(R.id.goBackButton);

        getAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Bundle bundle = new Bundle();
                bundle.putSerializable("selectedUser", user);
                bundle.putString("firebaseKey", firebaseKey);

                AddAppointmentFragment addAppointmentFragment = new AddAppointmentFragment();
                addAppointmentFragment.setArguments(bundle);

                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.content_frame, addAppointmentFragment);
                transaction.addToBackStack(null);
                transaction.commit();
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
}
