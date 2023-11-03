package com.example.swiftslotz.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swiftslotz.R;
import com.example.swiftslotz.utilities.AppointmentManager;

import java.util.List;

public class InvolvedUsersAdapter extends RecyclerView.Adapter<InvolvedUsersAdapter.ViewHolder> {

    private List<String> involvedUsersFirebaseKeys;
    private AppointmentManager appointmentManager;
    private Context context;

    public InvolvedUsersAdapter(Context context, List<String> involvedUsersFirebaseKeys) {
        this.context = context;
        this.involvedUsersFirebaseKeys = involvedUsersFirebaseKeys;
        this.appointmentManager = new AppointmentManager(context);  // Initialize this as per your setup
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.involved_user_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String firebaseKey = involvedUsersFirebaseKeys.get(position);

        // Fetch and set the involved user's name
        appointmentManager.getUserNameFromFirebaseKey(firebaseKey, userName -> {
            holder.involvedUserName.setText(userName);
        });
    }

    @Override
    public int getItemCount() {
        return involvedUsersFirebaseKeys.size();
    }

    public void updateInvolvedUsers(List<String> newInvolvedUsers) {
        this.involvedUsersFirebaseKeys = newInvolvedUsers;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView involvedUserName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            involvedUserName = itemView.findViewById(R.id.involved_user_name);
        }
    }
}



