package com.example.softwaresolutionssquad.views;// Dummy fragment for the "Profile" page
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.softwaresolutionssquad.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfileFragment extends Fragment {


    private TextView itemCountTextView;

    private TextView tagCountTextView;

    private LinearLayout logoutBar;

    private CollectionReference itemsRef;

    private CollectionReference tagsRef;

    private int tagCount;

    private int itemCount;

    private String userName;

    private TextView user;
<<<<<<< HEAD
=======

    /**
     * Required empty constructor for instantiating the fragment.
     */
>>>>>>> main
    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Called to have the fragment instantiate its user interface view. Initializes UI components and sets up interactions.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, the fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_layout, container, false);
        itemCountTextView = view.findViewById(R.id.itemAmount);
        tagCountTextView = view.findViewById(R.id.tagAmount);
        logoutBar = view.findViewById(R.id.logoutBar);
        user = view.findViewById(R.id.textUser);
        tagsRef = ((MainActivity) getActivity()).getDb().collection("Tags");
        itemsRef = ((MainActivity)  getActivity()).getDb().collection("Item");
        MyApp myApp = (MyApp) requireActivity().getApplication();
        UserViewModel userViewModel = myApp.getUserViewModel();
        userName = userViewModel.getUsername();
        user.setText(String.format("Hello, %s", userName));
        Log.d("user", userName);
        getTagCount();
        getItemCount();

        logoutBar.setOnClickListener(v -> {
            userViewModel.setUsername("");
            Intent logoutIntent = new Intent(requireContext(), LoginActivity.class);
            startActivity(logoutIntent);
        });
        return view;
    }

    /**
     * Retrieves the count of items associated with the current user from Firestore and updates the UI.
     */
    private void getItemCount() {
        itemsRef.whereEqualTo("username", userName).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                itemCount = 0;
                for (QueryDocumentSnapshot _ : task.getResult()) {
                    itemCount += 1;
                }
                itemCountTextView.setText(String.valueOf(itemCount));

            } else {
                // Handle the error
                Log.e("Item get Profile Fragment", "Error fetching tags from Firestore", task.getException());
            }
        });

    }

    /**
     * Retrieves the count of tags associated with the current user from Firestore and updates the UI.
     */
    private void getTagCount() {
        tagsRef.whereEqualTo("user", userName).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                tagCount = 0;
                for (QueryDocumentSnapshot _ : task.getResult()) {
                    tagCount += 1;
                }
                tagCountTextView.setText(String.valueOf(tagCount));

            } else {
                // Handle the error
                Log.e("Tag get Profile Fragment", "Error fetching tags from Firestore", task.getException());
            }
        });
    }
}
