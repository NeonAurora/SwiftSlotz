package com.example.swiftslotz.fragments.bottomBarFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.swiftslotz.BuildConfig;
import com.example.swiftslotz.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private ImageView userAvatar;
    private TextView userName;
    private TextView userEmail;

    private FirebaseAuth mAuth;
    private DatabaseReference userDb;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        userAvatar = view.findViewById(R.id.user_avatar);
        userName = view.findViewById(R.id.user_name);
        userEmail = view.findViewById(R.id.user_email);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            userDb = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL).getReference("users").child(userId);

            // Fetch user details from Firebase
            fetchUserDetails();
        }
        else {
            Toast.makeText(getActivity(), "User not signed in", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void fetchUserDetails() {
        userDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if (dataSnapshot.hasChild("firstName") && dataSnapshot.hasChild("email")) {
                        String name = dataSnapshot.child("firstName").getValue(String.class);
                        Log.d("User name is ", name);
                        String email = dataSnapshot.child("email").getValue(String.class);
                        Log.d("User email is", email);

                        // Update the UI with the user's details
                        userName.setText(name);
                        userEmail.setText(email);
                    } else {
                        Toast.makeText(getActivity(), "User data not available", Toast.LENGTH_SHORT).show();
                    }

                    // TODO: Load the user's avatar into userAvatar using an image loading library like Glide or Picasso
                    // Glide.with(getContext()).load(avatarUrl).into(userAvatar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to load user details", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
