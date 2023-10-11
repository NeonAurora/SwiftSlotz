package com.example.swiftslotz.utilities;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
                Bundle bundle = new Bundle();
                bundle.putSerializable("selectedUser", user);
                bundle.putString("firebaseKey", firebaseGeneratedKey);

                AddAppointmentFragment addAppointmentFragment = new AddAppointmentFragment();
                addAppointmentFragment.setArguments(bundle);

                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.content_frame, addAppointmentFragment);
                transaction.addToBackStack(null);
                transaction.commit();
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
}
