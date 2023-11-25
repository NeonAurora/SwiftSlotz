package com.example.swiftslotz.fragments.pageFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.swiftslotz.BuildConfig;
import com.example.swiftslotz.R;
import com.example.swiftslotz.utilities.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileViewFragment extends Fragment {

    private String userKey;
    private DatabaseReference userDb;
    private TextView usernameTextView, firstNameTextView, lastNameTextView, emailTextView, phoneTextView, occupationTextView, addressTextView;
    private ImageView profileImageView;
    Button seekAppointmentButton;

    public ProfileViewFragment() {
        // Required empty public constructor
    }

    public static ProfileViewFragment newInstance(String userKey) {
        ProfileViewFragment fragment = new ProfileViewFragment();
        Bundle args = new Bundle();
        args.putString("userKey", userKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userKey = getArguments().getString("userKey");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_view, container, false);
        initializeViews(view);
        fetchUserDetails();

        seekAppointmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToAddAppointmentFragment();
            }
        });

        return view;
    }

    private void initializeViews(View view) {
        usernameTextView = view.findViewById(R.id.user_username_profile_view);
        firstNameTextView = view.findViewById(R.id.profileViewEditFirstName);
        lastNameTextView = view.findViewById(R.id.profileViewEditLastName);
        emailTextView = view.findViewById(R.id.profileViewEditEmail);
        phoneTextView = view.findViewById(R.id.profileViewEditPhoneNumber);
        occupationTextView = view.findViewById(R.id.profileViewEditCompany);
        addressTextView = view.findViewById(R.id.profileViewEditAddress);
        profileImageView = view.findViewById(R.id.profileViewImage);
        seekAppointmentButton = view.findViewById(R.id.seekAppointmentButton);
    }

    private void fetchUserDetails() {
        userDb = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL).getReference("users").child(userKey);
        userDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null && isAdded()) {
                        displayUserData(user);
                    }
                } else {
                    Toast.makeText(getActivity(), "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to load user data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToAddAppointmentFragment() {
        AddAppointmentFragment addAppointmentFragment = new AddAppointmentFragment();

        Bundle bundle = new Bundle();
        bundle.putString("firebaseKey", userKey); // Pass the user key to AddAppointmentFragment
        addAppointmentFragment.setArguments(bundle);

        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, addAppointmentFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void displayUserData(User user) {
        usernameTextView.setText(user.getUsername());
        firstNameTextView.setText(user.getFirstName());
        lastNameTextView.setText(user.getLastName());
        emailTextView.setText(user.getEmail());
        phoneTextView.setText(user.getPhone());
        occupationTextView.setText(user.getOccupation());
        addressTextView.setText(user.getAddress());

        if (user.getProfileImageUrl() != null && !user.getProfileImageUrl().isEmpty()) {
            Glide.with(this).load(user.getProfileImageUrl()).into(profileImageView);
        }
    }
}
