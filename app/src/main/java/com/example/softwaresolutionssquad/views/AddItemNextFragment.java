package com.example.softwaresolutionssquad.views;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.softwaresolutionssquad.R;
import com.example.softwaresolutionssquad.models.InventoryItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class AddItemNextFragment extends Fragment implements AddItemTagFragment.OnFragmentInteractionListener {
    private Button backBtn;
    private Button addTagBtn;
    private TextView titleTextView;
    private final InventoryItem item;
    private CollectionReference itemsRef;
    private final Boolean newItem;
    private Button createBtn;
    private GridView tagGrid;
    private ArrayList<String> tags = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private Context context;
    private RecyclerView attachedImages;
    private Button selectImage, takePhoto;
    private Uri imageUri;
    String imageUrl;
    private ArrayList<String> imageUrisList;
    private final ArrayList<String> newImages = new ArrayList<>();
    ImageAdapter imageAdapter;

    /**
     * Constructor for AddItemNextFragment. Initializes the fragment with a given InventoryItem and a flag indicating if it's a new item.
     * @param item The InventoryItem to be displayed or edited in this fragment.
     * @param newItem A boolean flag indicating whether the item is new or existing.
     */
    public AddItemNextFragment(InventoryItem item, boolean newItem) {
        this.item = item;
        this.newItem = newItem;
        this.imageUrisList = item.getImageUrl();
    }

    /**
     * Called when the fragment is first attached to its context. Initializes context.
     * @param context The context to which the fragment is attached.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    /**
     * Creates and returns the view hierarchy associated with the fragment.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_item_next, container, false);
        backBtn = view.findViewById(R.id.btnBack);
//        cancelBtn = view.findViewById(R.id.btnCancel);
        addTagBtn = view.findViewById(R.id.btnAddTag);
        createBtn = view.findViewById(R.id.btnCreate);
        tagGrid = view.findViewById(R.id.tagGridView);
        titleTextView = view.findViewById(R.id.itemTitle);

        imageAdapter = new ImageAdapter(getContext(), imageUrisList, newImages);
        attachedImages = view.findViewById(R.id.recyclerViewImages);
        int horizontalSpaceHeight = 2;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        attachedImages.setLayoutManager(layoutManager);

        attachedImages.setAdapter(imageAdapter);
        attachedImages.addItemDecoration(new HorizontalSpaceItemDecoration(horizontalSpaceHeight));

        selectImage = view.findViewById(R.id.btnSelectImage);
        takePhoto = view.findViewById(R.id.btnTakePhoto);

        selectImage.setOnClickListener(v -> selectImage());
        takePhoto.setOnClickListener(v -> checkCameraPermissionAndOpenCamera());

        tags = item.getTags();
        Log.d("tags", tags.toString());
        if (tags.size() > 0) {
            tagGrid.setVisibility(View.VISIBLE);
        }

        adapter = new ItemTagAdapter(context, tags);
        tagGrid.setAdapter(adapter);

        if (!newItem) {
            createBtn.setText("Update");
            titleTextView.setText("View/Update Item");
        }

        itemsRef =  ((MainActivity)getActivity()).getDb().collection("Item");

        addTagBtn.setOnClickListener(v -> {
            AddItemTagFragment addItemTagFragment = new AddItemTagFragment();
            addItemTagFragment.setListener(AddItemNextFragment.this);
            addItemTagFragment.show(getActivity().getSupportFragmentManager(), "ADD_ITEM_TAG");
        });

        backBtn.setOnClickListener(v -> {
            if (getActivity() != null) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        createBtn.setOnClickListener(v -> {
                uploadImageToFirebase(newImages, uploaded -> {
                    imageUrisList.removeAll(newImages);
                    item.setImageUrl(imageUrisList);
                    if (newItem) {
                        createNewItem(item);
                    } else {
                        onUpdatePressed(item);
                    }
                    if (getActivity() != null) {
                        if (newItem) {
                            Toast.makeText(getActivity(), "Item added successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Item updated successfully!", Toast.LENGTH_SHORT).show();
                        }
                        HomeFragment homeFragment = new HomeFragment();
                        ((MainActivity) getActivity()).setFragment(homeFragment);
                        Activity activity = getActivity();
                        BottomNavigationView bottomNavigationView = activity.findViewById(R.id.navigation_bar);
                        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
                    }
                });
        });
        return view;
    }

    /**
     * Creates a new InventoryItem in the Firestore database.
     * @param newItem The InventoryItem object to be created in the database.
     */
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

    /**
     * Updates an existing InventoryItem in the Firestore database.
     * @param updatedItem The InventoryItem object with updated information to be saved in the database.
     */
    public void onUpdatePressed(InventoryItem updatedItem) {
        // Use the ID from the updatedItem to reference the Firestore document
        itemsRef.document(updatedItem.getDocId()).set(updatedItem)
                .addOnSuccessListener(aVoid -> {
                    Log.d("UpdateItem", "DocumentSnapshot successfully updated!");
                })
                .addOnFailureListener(e -> Log.w("UpdateItem", "Error updating document", e));
    }

    /**
     * Handles the selection of tags when the OK button is pressed in the AddItemTagFragment.
     * @param selectedTags The list of selected tags.
     */
    @Override
    public void onOkPressed(ArrayList<String> selectedTags) {
        for (String tag: selectedTags) {
            if (!tags.contains(tag)) {
                tags.add(tag);
            }
        }
        if (tags.size() > 0) {
            tagGrid.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();

    }

    private final ActivityResultLauncher<String> requestCameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    takePhoto();
                } else {
                    Toast.makeText(getContext(), "Permission denied to capture photos", Toast.LENGTH_SHORT).show();
                }
            });

    /**
     * Creates a new image file for capturing a photo, providing a unique file name.
     * @return The Uri of the newly created image file.
     * @throws IOException If there is an error creating the file.
     */
    private Uri createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",   /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        return FileProvider.getUriForFile(getContext(), "com.example.softwaresolutionssquad.fileprovider", image);
    }

    /**
     * Initiates the camera intent to take a photo, storing the result in a file.
     */
    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            imageUri = createImageFile();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            takePhotoLauncher.launch(intent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Opens the Camera to take a picture
     */
    private final ActivityResultLauncher<Intent> takePhotoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    newImages.add(imageUri.toString());
                    imageUrisList.add(imageUri.toString());
                    imageAdapter.notifyDataSetChanged();

                }
            }
    );

    /**
     * Checks for camera permission and, if granted, opens the camera to take a photo.
     */
    private void checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            takePhoto();
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    /**
     * Opens an intent to select an image from the device's storage.
     */
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        pickImageLauncher.launch(intent);
    }

    /**
     * Adds an image to image list after the user selects an image
     */
    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    if (result.getData().getClipData() != null) {
                        ClipData cd = result.getData().getClipData();
                        for (int i = 0; i < cd.getItemCount(); i++) {
                            imageUri = cd.getItemAt(i).getUri();
                            newImages.add(imageUri.toString());
                            imageUrisList.add(imageUri.toString());
                            imageAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });

    /**
     * Uploads a list of image URIs to Firebase Storage and invokes a callback upon completion.
     * @param uriStrings The list of URI strings representing the images to upload.
     * @param listener The callback to be invoked when the upload is complete.
     */
    private void uploadImageToFirebase(ArrayList<String> uriStrings, UploadCompleteListener listener) {
        if (uriStrings != null && uriStrings.size() > 0) {
            // Initialize Firebase Storage with the specific URL
            FirebaseStorage storage = FirebaseStorage.getInstance("gs://softwaresolutionssquad.appspot.com");
            StorageReference storageRef = storage.getReference();
            AtomicInteger uploadCount = new AtomicInteger(0);

            for (String uriString : uriStrings) {
                Uri uri = Uri.parse(uriString);
                // Create a unique filename for the image
                String fileName = "images/" + System.currentTimeMillis() + "-" + getFileExtension(uri);
                StorageReference fileRef = storageRef.child(fileName);
                // Upload the file to Firebase Storage

                fileRef.putFile(uri)
                        .addOnSuccessListener(taskSnapshot -> {
                            // On successful upload, get the download URL
                            fileRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                                // Here you get the image download URL
                                imageUrl = downloadUri.toString();
                                imageUrisList.add(imageUrl);
                                if (uploadCount.incrementAndGet() == uriStrings.size()) {
                                    if (listener != null) {
                                        listener.onUploadComplete(uriStrings);
                                    }
                                }
                            });
                        })
                        .addOnFailureListener(e -> {

                        })
                        .addOnProgressListener(snapshot -> {

                        });
            }
        } else {
            listener.onUploadComplete(uriStrings);
        }
    }

    /**
     * Retrieves the file extension of a given Uri.
     * @param uri The Uri of the file whose extension is to be determined.
     * @return The file extension as a String.
     */
    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }
}