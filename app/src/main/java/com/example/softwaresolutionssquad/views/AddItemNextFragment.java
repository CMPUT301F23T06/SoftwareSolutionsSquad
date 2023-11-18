package com.example.softwaresolutionssquad.views;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.softwaresolutionssquad.R;
import com.example.softwaresolutionssquad.models.InventoryItem;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import java.text.DateFormat;
import java.util.Date;

public class AddItemNextFragment extends Fragment {
    private Button backBtn;
    private Button cancelBtn;

    private InventoryItem item;

    private CollectionReference itemsRef;

    private Boolean newItem;

    private Button createBtn;

    public AddItemNextFragment(InventoryItem item, boolean newItem) {
        this.item = item;
        this.newItem = newItem;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_item_next, container, false);
        backBtn = view.findViewById(R.id.btnBack);
        cancelBtn = view.findViewById(R.id.btnCancel);
        createBtn = view.findViewById(R.id.btnCreate);
        if (!newItem) {
            createBtn.setText("Update");
        }
        itemsRef =  ((MainActivity)getActivity()).getDb().collection("Item");
        cancelBtn.setOnClickListener(v -> {
            if (getActivity() != null) {
                HomeFragment homeFragment = new HomeFragment();
                ((MainActivity) getActivity()).setFragment(homeFragment);
            }
        });

        backBtn.setOnClickListener(v -> {
            if (getActivity() != null) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        createBtn.setOnClickListener(v -> {
            if (newItem) {
                createNewItem(item);
            } else {
                onUpdatePressed(item);
            }

            if (getActivity() != null) {
                HomeFragment homeFragment = new HomeFragment();
                ((MainActivity) getActivity()).setFragment(homeFragment);
            }

        });
        return view;
    }

    public void createNewItem(InventoryItem newItem) {
        // Get a new document reference from Firestore, which has an auto-generated ID
        DocumentReference newDocRef = itemsRef.document();

        // Set the document ID inside the new item object
        newItem.setDocId(newDocRef.getId()); // Make sure InventoryItem has a method to set its ID

        // Set the new item in the Firestore document
        newDocRef.set(newItem)
                .addOnSuccessListener(aVoid -> {
                    Log.d("AddItem", "DocumentSnapshot written with ID: " + newDocRef.getId());
                });
    }

    public void onUpdatePressed(InventoryItem updatedItem) {
        // Use the ID from the updatedItem to reference the Firestore document
        itemsRef.document(updatedItem.getDocId()).set(updatedItem)
                .addOnSuccessListener(aVoid -> {
                    Log.d("UpdateItem", "DocumentSnapshot successfully updated!");
                })
                .addOnFailureListener(e -> Log.w("UpdateItem", "Error updating document", e));
    }
}
