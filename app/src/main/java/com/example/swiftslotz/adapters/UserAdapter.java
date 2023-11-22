package com.example.swiftslotz.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swiftslotz.R;
import com.example.swiftslotz.fragments.pageFragments.AddAppointmentFragment;
import com.example.swiftslotz.utilities.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    Context context;
    private List<User> userList;
    private List<String> firebaseKeys;
    private FragmentManager fragmentManager;

    public UserAdapter(Context context,List<User> userList, List<String> firebaseKeys, FragmentManager fragmentManager) {
        this.context=context;
        this.userList = userList;
        this.firebaseKeys = firebaseKeys;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_detail_modal, parent, false);
        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        String firebaseGeneratedKey = firebaseKeys.get(position);

        holder.sl_username.setText(user.getUsername());
        holder.sl_firstname.setText(user.getFirstName());
        holder.sl_lastname.setText(user.getLastName());
        holder.sl_company.setText(user.getOccupation());
        holder.sl_address.setText(user.getAddress());

        holder.sl_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+user.getPhone()));
                context.startActivity(intent);
            }
        });

        holder.sl_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + user.getEmail()));
                context.startActivity(Intent.createChooser(emailIntent, "Chooser Title"));
            }
        });

        holder.sl_addApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        public TextView sl_username,sl_firstname,sl_lastname,sl_company,sl_address;

        public ImageView sl_call,sl_email,sl_addApp;
        public UserViewHolder(View view) {
            super(view);
            sl_username = view.findViewById(R.id.sl_username);
            sl_firstname = view.findViewById(R.id.sl_firstname);
            sl_lastname = view.findViewById(R.id.sl_lastname);
            sl_company = view.findViewById(R.id.sl_company);
            sl_address = view.findViewById(R.id.sl_address);
            sl_call=view.findViewById(R.id.sl_call);
            sl_email=view.findViewById(R.id.sl_email);
            sl_addApp=view.findViewById(R.id.sl_addApp);
        }
    }
}
