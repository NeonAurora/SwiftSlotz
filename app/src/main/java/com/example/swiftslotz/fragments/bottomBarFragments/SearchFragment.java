package com.example.swiftslotz.fragments.bottomBarFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.swiftslotz.BuildConfig;
import com.example.swiftslotz.R;
import com.example.swiftslotz.utilities.User;
import com.example.swiftslotz.adapters.UserAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private EditText searchEditText;
    private RecyclerView searchResultsRecyclerView;
    private DatabaseReference usersDb;
    private List<User> searchResults;
    private UserAdapter userAdapter;
    private List<String> firebaseKeys = new ArrayList<>();
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String currentUserId = currentUser != null ? currentUser.getUid() : null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchEditText = view.findViewById(R.id.searchEditText);
        Button searchButton = view.findViewById(R.id.searchButton);
        searchResultsRecyclerView = view.findViewById(R.id.searchResultsRecyclerView);

        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        usersDb = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL).getReference("users");
        searchResults = new ArrayList<>();

        userAdapter = new UserAdapter(getActivity(),searchResults, firebaseKeys, getParentFragmentManager());
        searchResultsRecyclerView.setAdapter(userAdapter);



        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchEditText.getText().toString().trim();
                if (!query.isEmpty()) {
                    searchForUser(query.toLowerCase());
                }else{
                    getAllUser();
                }
            }
        });

        return view;
    }

    private void searchForUser(String query) {
        Log.d("SearchFragment", "User ID" + currentUserId);
        usersDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                searchResults.clear();
                firebaseKeys.clear();
                boolean userFound = false;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String key = userSnapshot.getKey();
                    if (!key.equals(currentUserId)) { // Exclude the current user
                        User user = userSnapshot.getValue(User.class);
                        boolean cond = user != null && (user.getUsername().toLowerCase().contains(query) ||
                                user.getFirstName().toLowerCase().contains(query) ||
                                user.getLastName().toLowerCase().contains(query) ||
                                user.getOccupation().toLowerCase().contains(query) ||
                                user.getAddress().toLowerCase().contains(query));
                        if (cond) {
                            searchResults.add(user);
                            firebaseKeys.add(key);
                            userFound = true;
                        }
                    }
                }
                if (!userFound) {
                    Toast.makeText(getContext(), "No user with this username found", Toast.LENGTH_SHORT).show();
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to fetch data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getAllUser() {
        usersDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                searchResults.clear();
                firebaseKeys.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    if (!userSnapshot.getKey().equals(currentUserId)) {
                        User user = userSnapshot.getValue(User.class);
                        String key = userSnapshot.getKey();
                        searchResults.add(user);
                        firebaseKeys.add(key);
                    }

                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to fetch data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
