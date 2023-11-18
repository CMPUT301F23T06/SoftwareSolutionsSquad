package com.example.softwaresolutionssquad.views;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.softwaresolutionssquad.models.InventoryItem;
import com.example.softwaresolutionssquad.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    private OnNewItemSubmission listener;

    private TextView title;
    private final LocalDate currentDate = LocalDate.now();

    private static final int PICK_IMAGE_REQUEST = 1000;
    private static final int PERMISSION_REQUEST_STORAGE = 2000;

    private ImageView itemPicture;
    private Button selectImage;
    private Uri imageUri;
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the fragment's layout
        View view = inflater.inflate(R.layout.fragment_add_item, container, false);
        initializeUiElements(view);
        setOnClickListeners();
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

        // Initialize the ImageView and Firebase Storage
        itemPicture = view.findViewById(R.id.itemPicture);
        selectImage = view.findViewById(R.id.btnSelectImage);
        storageRef = FirebaseStorage.getInstance().getReference();

        selectImage.setOnClickListener(v -> selectImage());


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
                String imageUrl = imageUri != null ? imageUri.toString() : "";

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
                    listener.onUpdatePressed(itemToSave);
                } else {
                    newItem = true;
                    // It's a new item
                    itemToSave = new InventoryItem(officialDate, description, make, model, serialNumber, official_estimated_value, comm, documentID, imageUrl);
//                    createNewItem(itemToSave);
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

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    selectImage();
                } else {
                    Toast.makeText(getContext(), "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
            });

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    itemPicture.setImageURI(imageUri);

                    // Add the OnClickListener here
                    itemPicture.setOnClickListener(v -> showFullScreenImage(imageUri));


                    uploadImageToFirebase(imageUri);
                }
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

    private void showFullScreenImage(Uri imageUri) {
        Dialog fullScreenDialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        fullScreenDialog.setContentView(R.layout.fragment_view_item_photo);

        ImageView imageView = fullScreenDialog.findViewById(R.id.fullscreenImageView);
        ImageButton closeButton = fullScreenDialog.findViewById(R.id.closeButton);

        Glide.with(this).load(imageUri).into(imageView);

        closeButton.setOnClickListener(v -> fullScreenDialog.dismiss());
        imageView.setOnClickListener(v -> fullScreenDialog.dismiss()); // Optional: allows closing by clicking the image

        fullScreenDialog.show();
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
     * Sets on click listeners for interactive UI elements.
     */
    private void setOnClickListeners() {
        purchaseDateEditText.setOnClickListener(v -> showDatePicker());
        nextButton.setOnClickListener(v -> saveItem());
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
     * Saves the item to the inventory, either by creating a new entry or updating an existing one.
     */
    private void saveItem() {
        String date = purchaseDateEditText.getText().toString().trim();
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        Date officialDate = null;

        try {
            officialDate = dateFormat.parse(date);
        } catch (ParseException e) {
            // Handle the error state
        }

        if (officialDate == null || estimatedValueEditText.getText().toString().trim().isEmpty()) {
            showAlertDialog("Error", "Please fill in the value and date!");
            return;
        }

        // Before creating or updating the InventoryItem
        Double estimatedValue = Double.parseDouble(estimatedValueEditText.getText().toString().trim());
        String imageUrl = imageUri != null ? imageUri.toString() : "";
        InventoryItem itemToSave = createOrUpdateItem(officialDate, estimatedValue, imageUrl);

        if (itemToSave != null) {
            if (currentItem != null) {
                listener.onUpdatePressed(itemToSave);
            } else {
                createNewItem(itemToSave);
            }
        }
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

    /**
     * Creates or updates an InventoryItem with the provided details.
     * @param officialDate    The official purchase date of the item.
     * @param estimatedValue  The estimated value of the item.
     * @return The created or updated InventoryItem.
     */
    private InventoryItem createOrUpdateItem(Date officialDate, double estimatedValue, String imageUrl) {
        String description = descriptionEditText.getText().toString().trim();
        String make = makeEditText.getText().toString().trim();
        String model = modelEditText.getText().toString().trim();
        String serialNumber = serialNumberEditText.getText().toString().trim();
        String comment = commentEditText.getText().toString().trim();
        String documentID = retrieveDocId(currentItem);

        if (currentItem != null) {
            // Update the existing item's properties
            currentItem.setPurchaseDate(officialDate);
            currentItem.setDescription(description);
            currentItem.setMake(make);
            currentItem.setModel(model);
            currentItem.setSerialNumber(serialNumber);
            currentItem.setEstimatedValue(estimatedValue);
            currentItem.setComment(comment);
            currentItem.setDocId(documentID);
            currentItem.setImageUrl(imageUrl);

            // Add the OnClickListener here
            itemPicture.setOnClickListener(v -> showFullScreenImage(imageUri));
            return currentItem;
        } else {
            // It's a new item
            return new InventoryItem(officialDate, description, make, model, serialNumber, estimatedValue, comment, documentID, imageUrl);
        }
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

        // Load the image if there is a URL
        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(item.getImageUrl())
                    .into(itemPicture);

            // Add the OnClickListener here
            itemPicture.setOnClickListener(v -> showFullScreenImage(imageUri));
        }
    }

    private final DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
        LocalDate dateSet = LocalDate.of(year, monthOfYear, dayOfMonth);
        updateLabel(dateSet);
    };

    private void updateLabel(LocalDate dateSet) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_FORMAT);
        purchaseDateEditText.setText(dateSet.format(dtf));
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


    /**
     * Interface for communicating with the activity when an item is saved.
     */
    public interface OnNewItemSubmission {
        void onUpdatePressed(InventoryItem updatedItem);
    }
    public void setListener(OnNewItemSubmission listener) {
        this.listener = listener;
    }
}