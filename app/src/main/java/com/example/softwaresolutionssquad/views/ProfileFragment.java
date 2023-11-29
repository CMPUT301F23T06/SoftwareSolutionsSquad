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

public class ProfileFragment extends Fragment {


    private TextView itemCountTextView;

    private TextView tagCountTextView;

    private LinearLayout logoutBar;

    private int tagCount = 0;

    private int itemCount = 0;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_layout, container, false);
        itemCountTextView = view.findViewById(R.id.itemAmount);
        tagCountTextView = view.findViewById(R.id.tagAmount);
        logoutBar = view.findViewById(R.id.logoutBar);
        MyApp myApp = (MyApp) requireActivity().getApplication();
        UserViewModel userViewModel = myApp.getUserViewModel();

        logoutBar.setOnClickListener(v -> {
            userViewModel.setUsername("");
            Intent logoutIntent = new Intent(requireContext(), LoginActivity.class);
            startActivity(logoutIntent);
        });
        return view;
    }
}
