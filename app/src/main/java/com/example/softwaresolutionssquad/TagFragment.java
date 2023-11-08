package com.example.softwaresolutionssquad;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TagFragment extends Fragment {

    public TagFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("tag", "hello");
        // You can create a simple layout for the dummy fragment or leave it empty.
        return inflater.inflate(R.layout.fragment_dummy, container, false);
    }
}
