package com.example.softwaresolutionssquad.views;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.softwaresolutionssquad.models.InventoryItem;
import com.example.softwaresolutionssquad.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * A fragment for adding a new item to the inventory or editing an existing one.
 */
public class AddItemFragment extends Fragment {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String ITEM_KEY = "item";

    private EditText purchaseDateEditText;
    private EditText descriptionEditText;
    private EditText makeEditText;
    private EditText modelEditText;

    private CollectionReference itemsRef;
    private EditText serialNumberEditText;
    private EditText estimatedValueEditText;
    private EditText commentEditText;
    private Button nextButton;
    private Button cancelButton;
    private InventoryItem currentItem;

    private TextView title;
    private final LocalDate currentDate = LocalDate.now();

    private static final int PICK_IMAGE_REQUEST = 1000;
    private static final int PERMISSION_REQUEST_STORAGE = 2000;

    private RecyclerView attachedImages;
    private Button selectImage, takePhoto;
    private Uri imageUri;
    private ArrayList<String> imageUrisList = new ArrayList<>();
    ImageAdapter imageAdapter;
    private StorageReference storageRef;

    public AddItemFragment() {
        // Required empty public constructor
    }

    /**
     * Creates a new instance of AddItemFragment with an InventoryItem pre-populated if provided.
     * @param item InventoryItem to be edited, null if it's a new item.
     * @return A new instance of fragment AddItemFragment.
     */
    public static AddItemFragment newInstance(InventoryItem item) {
        AddItemFragment fragment = new AddItemFragment();
        Bundle args = new Bundle();
        args.putSerializable(ITEM_KEY, item);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentItem = (InventoryItem) getArguments().getSerializable(ITEM_KEY);
            Log.d("item", currentItem.getTags().toString());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment's layout
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);
        initializeUiElements(view);
        itemsRef =  ((MainActivity)getActivity()).getDb().collection("Item");
        // Initialize UI elements
        purchaseDateEditText = view.findViewById(R.id.edtPurchaseDate);
        descriptionEditText = view.findViewById(R.id.edtDescription);
        makeEditText = view.findViewById(R.id.edtMake);
        modelEditText = view.findViewById(R.id.edtModel);
        serialNumberEditText = view.findViewById(R.id.edtSerialNumber);
        estimatedValueEditText = view.findViewById(R.id.editTextNumberDecimal);
        commentEditText = view.findViewById(R.id.edtCommentTitle);
        nextButton = view.findViewById(R.id.btnNext);
        cancelButton = view.findViewById(R.id.btnCancel);
        title = view.findViewById(R.id.title);

        imageAdapter = new ImageAdapter(getContext(), imageUrisList);
        attachedImages = view.findViewById(R.id.recyclerViewImages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        attachedImages.setLayoutManager(layoutManager);
        attachedImages.setAdapter(imageAdapter);

        selectImage = view.findViewById(R.id.btnSelectImage);
        takePhoto = view.findViewById(R.id.btnTakePhoto);
        storageRef = FirebaseStorage.getInstance().getReference();

        selectImage.setOnClickListener(v -> selectImage());
        takePhoto.setOnClickListener(v -> checkCameraPermissionAndOpenCamera());

        // Set an onClickListener for the purchase date EditText to show a date picker
        purchaseDateEditText.setOnClickListener(v -> {
            new DatePickerDialog(getContext(), dateSetListener, currentDate.getYear(),
                    currentDate.getMonthValue(),
                    currentDate.getDayOfMonth()).show();
        });

        // Set an onClickListener for the Next button
        nextButton.setOnClickListener(v -> {
            // TODO: Logic for saving the data and transitioning to the next screen

            // Extract data from UI elements
            String date = purchaseDateEditText.getText().toString().trim();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Replace with your date format
            Date officialDate = null;
            try {
                officialDate = dateFormat.parse(date);
            } catch (ParseException e) {
                officialDate = null;
            }
            String description = descriptionEditText.getText().toString().trim();

            String make = makeEditText.getText().toString().trim();
            String model = modelEditText.getText().toString().trim();
            String serialNumber = serialNumberEditText.getText().toString().trim();
            String estimated_val = estimatedValueEditText.getText().toString().trim();

                // Validate data
                if (officialDate == null || estimated_val.equals("")) { // Use .equals() for string comparison
                    // Show an alert dialog if validation fails
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Alert Title"); // Optional title
                    builder.setMessage("Please at least fill in the value and date!");
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    return;
                }
                Double official_estimated_value = Double.parseDouble(estimated_val);
                String comm = commentEditText.getText().toString().trim();
                String documentID = retrieveDocId(currentItem);
                ArrayList<String> imageUrl = imageUrisList != null ? imageUrisList : new ArrayList<>();

                InventoryItem itemToSave;
                Boolean newItem = false;
                if(currentItem != null) {
                    // Update the existing item's properties
                    currentItem.setPurchaseDate(officialDate);
                    currentItem.setDescription(description);
                    currentItem.setMake(make);
                    currentItem.setModel(model);
                    currentItem.setSerialNumber(serialNumber);
                    currentItem.setEstimatedValue(official_estimated_value);
                    currentItem.setComment(comm);
                    currentItem.setDocId(documentID);
                    currentItem.setImageUrl(imageUrl);
                    itemToSave = currentItem;
                } else {
                    newItem = true;
                    // It's a new item
                    itemToSave = new InventoryItem(officialDate, description, make, model, serialNumber, official_estimated_value, comm, documentID, imageUrl);
                }

            if (getActivity() instanceof MainActivity) {
                AddItemNextFragment nextFragment = new AddItemNextFragment(itemToSave, newItem);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frag_container, nextFragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });

        // Set an onClickListener for the Cancel button
        cancelButton.setOnClickListener(v -> {


            // Show the HomeFragment
            if (getActivity() instanceof MainActivity) {
                HomeFragment homeFragment = new HomeFragment();
                ((MainActivity) getActivity()).setFragment(homeFragment);
            }
        });

        // Prepopulate fields if currentItem is not null (i.e., we're editing an existing item)
        if (currentItem != null) {
            title.setText("Update Item");
            prepopulateFields(currentItem);
        }
        return view;
    }

    private final ActivityResultLauncher<String> requestCameraPermissionLauncher =
        registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                takePhoto();
            } else {
                Toast.makeText(getContext(), "Permission denied to capture photos", Toast.LENGTH_SHORT).show();
            }
        });

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

    private final ActivityResultLauncher<Intent> takePhotoLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    imageUrisList.add(imageUri.toString());
                    imageAdapter.notifyDataSetChanged();
                    uploadImageToFirebase(imageUri);
                }
            }
    );

    private void checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            takePhoto();
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        pickImageLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                ArrayList<String> newImages = new ArrayList<>();
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
//                for (String uriString : newImages) {
//                    uploadImageToFirebase(Uri.parse(uriString));
//                    imageAdapter.notifyDataSetChanged();
//                }
            });

    private void uploadImageToFirebase(Uri uri) {
        if (uri != null) {
            // Initialize Firebase Storage with the specific URL
            FirebaseStorage storage = FirebaseStorage.getInstance("gs://softwaresolutionssquad.appspot.com");
            StorageReference storageRef = storage.getReference();

            // Create a unique filename for the image
            String fileName = "images/" + System.currentTimeMillis() + "-" + getFileExtension(uri);
            StorageReference fileRef = storageRef.child(fileName);
            // Upload the file to Firebase Storage
            fileRef.putFile(uri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // On successful upload, get the download URL
                        fileRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                            // Here you get the image download URL
                            String imageUrl = downloadUri.toString();
                            imageUrisList.add(imageUrl);
                            System.err.println("SUCCESS");
                            // You can now use this URL to save it to Firestore or do other actions
                            // For instance, you might want to set this URL to a class variable
                            // that is later saved along with other item data to Firestore
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle unsuccessful uploads
                        // You can display a message or perform other actions
                    })
                    .addOnProgressListener(snapshot -> {
                        // If you want, you can show upload progress here
                    });
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    /**
     * Initializes the UI elements of the fragment.
     * @param view The inflated view of the fragment.
     */
    private void initializeUiElements(View view) {
        purchaseDateEditText = view.findViewById(R.id.edtPurchaseDate);
        descriptionEditText = view.findViewById(R.id.edtDescription);
        makeEditText = view.findViewById(R.id.edtMake);
        modelEditText = view.findViewById(R.id.edtModel);
        serialNumberEditText = view.findViewById(R.id.edtSerialNumber);
        estimatedValueEditText = view.findViewById(R.id.editTextNumberDecimal);
        commentEditText = view.findViewById(R.id.edtCommentTitle);
        nextButton = view.findViewById(R.id.btnNext);
        cancelButton = view.findViewById(R.id.btnCancel);
    }



    /**
     * Shows a date picker dialog for selecting a purchase date.
     */
    private void showDatePicker() {
        if (currentItem != null && currentItem.getPurchaseDate() != null) {
            currentItem.getPurchaseDate();
        }

        new DatePickerDialog(getContext(), dateSetListener, currentDate.getYear(),
                currentDate.getMonthValue() - 1, // Month is 0-indexed in DatePickerDialog
                currentDate.getDayOfMonth()).show();
    }



    /**
     * Shows an alert dialog with a specified title and message.
     * @param title   The title of the alert dialog.
     * @param message The message of the alert dialog.
     */
    private void showAlertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title);
        builder.setMessage(message);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private String retrieveDocId(InventoryItem item) {
        if (item != null) {
            return item.getDocId();
        } else {
            DocumentReference newDocRef = ((MainActivity)getActivity()).getDb().collection("Item").document();
            return newDocRef.getId();
        }
    }

    private void prepopulateFields(InventoryItem item) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        purchaseDateEditText.setText(sdf.format(item.getPurchaseDate()));
        descriptionEditText.setText(item.getDescription());
        makeEditText.setText(item.getMake());
        modelEditText.setText(item.getModel());
        serialNumberEditText.setText(item.getSerialNumber());
        estimatedValueEditText.setText(String.valueOf(item.getEstimatedValue()));
        commentEditText.setText(item.getComment());

        imageUrisList = new ArrayList<>(item.getImageUrl());
        System.err.println(imageUrisList);
    }

    private final DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
        LocalDate dateSet = LocalDate.of(year, monthOfYear, dayOfMonth);
        updateLabel(dateSet);
    };

    private void updateLabel(LocalDate dateSet) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_FORMAT);
        purchaseDateEditText.setText(dateSet.format(dtf));
    }


}