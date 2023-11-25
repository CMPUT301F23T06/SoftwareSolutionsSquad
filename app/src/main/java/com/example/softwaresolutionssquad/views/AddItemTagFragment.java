package com.example.softwaresolutionssquad.views;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.softwaresolutionssquad.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AddItemTagFragment extends DialogFragment {

        private ListView tagListView;

        private androidx.appcompat.widget.AppCompatButton okBtn;
        private androidx.appcompat.widget.AppCompatButton cancelBtn;

        private String userName;


        private OnFragmentInteractionListener listener;

        public interface OnFragmentInteractionListener {
                void onOkPressed(ArrayList<String> selectedTags);
        }

        public void setListener(OnFragmentInteractionListener listener) {
                this.listener = (OnFragmentInteractionListener) listener;
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
                View view = inflater.inflate(R.layout.fragment_add_tag_item, container, false);

                tagListView = view.findViewById(R.id.tagListView);
                okBtn = view.findViewById(R.id.okButton);
                cancelBtn = view.findViewById(R.id.cancelButton);

                MyApp myApp = (MyApp) requireActivity().getApplication();
                UserViewModel userViewModel = myApp.getUserViewModel();
                userName = userViewModel.getUsername();

                cancelBtn.setOnClickListener(v -> {
                        dismiss();
                });

                okBtn.setOnClickListener(v -> {
                        // Get selected tags
                        SparseBooleanArray checked = tagListView.getCheckedItemPositions();
                        ArrayList<String> selectedTags = new ArrayList<>();
                        for (int i = 0; i < checked.size(); i++) {
                                int position = checked.keyAt(i);
                                if (checked.valueAt(i)) {
                                        selectedTags.add(tagListView.getItemAtPosition(position).toString());
                                }
                        }

                        listener.onOkPressed(selectedTags);
                        dismiss();
                });

                fetchFromFireStore();

                return view;
        }




        private void fetchFromFireStore() {
                CollectionReference tagsRef = FirebaseFirestore.getInstance().collection("Tags");

                tagsRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                                List<String> tags = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                        // Assuming tags are stored in a field named "tagName"
                                        String tag = document.getString("tag");
                                        String user = document.getString("user");
                                        if (user != null && user.equals(userName)) {
                                                tags.add(tag);
                                        }
                                }

                                // Display the fetched tags in the ListView
                                displayTags(tags);
                        } else {
                                // Handle the error
                                Log.e("TagSelectionFragment", "Error fetching tags from Firestore", task.getException());
                        }
                });
        }


        private void displayTags(List<String> tags) {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_multiple_choice, tags);
                tagListView.setAdapter(adapter);
                tagListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        }
}
