package com.example.swiftslotz.fragments.bottomBarFragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.swiftslotz.BuildConfig;
import com.example.swiftslotz.R;
import com.example.swiftslotz.activities.LogoutActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private TextView firstName;
    private TextView lastName;
    private TextView username;
    private TextView email;
    private TextView phone;
    private TextView occupation;
    private TextView address;

    private FirebaseAuth mAuth;
    private DatabaseReference userDb;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firstName = view.findViewById(R.id.user_firstName);
        lastName = view.findViewById(R.id.user_lastName);
        username = view.findViewById(R.id.user_username);
        email = view.findViewById(R.id.user_email);
        phone = view.findViewById(R.id.user_phone);
        occupation = view.findViewById(R.id.user_occupation);
        address = view.findViewById(R.id.user_address);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            userDb = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL).getReference("users").child(userId);

            fetchUserDetails();
        } else {
            Toast.makeText(getActivity(), "User not signed in", Toast.LENGTH_SHORT).show();
        }

        Button logoutButton = view.findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LogoutActivity.class);
                startActivity(intent);
            }
        });


        return view;
    }

    private void fetchUserDetails() {
        userDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    firstName.setText(dataSnapshot.child("firstName").getValue(String.class));
                    lastName.setText(dataSnapshot.child("lastName").getValue(String.class));
                    username.setText(dataSnapshot.child("username").getValue(String.class));
                    email.setText(dataSnapshot.child("email").getValue(String.class));
                    phone.setText(dataSnapshot.child("phone").getValue(String.class));
                    occupation.setText(dataSnapshot.child("occupation").getValue(String.class));
                    address.setText(dataSnapshot.child("address").getValue(String.class));
                } else {
                    Toast.makeText(getActivity(), "Failed to load user details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to load user details", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
