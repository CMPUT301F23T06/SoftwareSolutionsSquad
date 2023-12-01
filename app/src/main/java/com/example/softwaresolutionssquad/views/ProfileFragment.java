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

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_layout, container, false);
        itemCountTextView = view.findViewById(R.id.itemAmount);
        tagCountTextView = view.findViewById(R.id.tagAmount);
        logoutBar = view.findViewById(R.id.logoutBar);
        tagsRef = ((MainActivity) getActivity()).getDb().collection("Tags");
        itemsRef = ((MainActivity)  getActivity()).getDb().collection("Item");
        MyApp myApp = (MyApp) requireActivity().getApplication();
        UserViewModel userViewModel = myApp.getUserViewModel();
        userName = userViewModel.getUsername();
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
