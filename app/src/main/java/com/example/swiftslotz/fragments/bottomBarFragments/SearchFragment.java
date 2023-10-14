package com.example.swiftslotz.fragments.bottomBarFragments;

import android.os.Bundle;
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
import com.example.swiftslotz.utilities.UserAdapter;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchEditText = view.findViewById(R.id.searchEditText);
        searchResultsRecyclerView = view.findViewById(R.id.searchResultsRecyclerView);
        Button searchButton = view.findViewById(R.id.searchButton);

        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        usersDb = FirebaseDatabase.getInstance(BuildConfig.FIREBASE_DATABASE_URL).getReference("users");
        searchResults = new ArrayList<>();

        userAdapter = new UserAdapter(searchResults, firebaseKeys, getParentFragmentManager());
        searchResultsRecyclerView.setAdapter(userAdapter);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchEditText.getText().toString().trim();
                if (!query.isEmpty()) {
                    searchForUser(query);
                }
            }
        });

        return view;
    }

    private void searchForUser(String query) {
        usersDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                searchResults.clear();
                firebaseKeys.clear();
                boolean userFound = false;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    String key = userSnapshot.getKey();
                    if (user != null && user.getUsername().toLowerCase().contains(query.toLowerCase())) {
                        searchResults.add(user);
                        firebaseKeys.add(key);
                        userFound = true;
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
}
