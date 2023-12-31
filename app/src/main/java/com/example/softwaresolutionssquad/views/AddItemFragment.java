package com.example.softwaresolutionssquad.views;

import android.app.DatePickerDialog;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.softwaresolutionssquad.models.InventoryItem;
import com.example.softwaresolutionssquad.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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

    private TextView title;
    private final LocalDate currentDate = LocalDate.now();

    private StorageReference storageRef;
    private ImageButton scanDescription;
    private ImageButton scanSerial;

    public String currentUser;

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
        itemsRef =  ((MainActivity)getActivity()).getDb().collection("Item");
        // Initialize required data
        MyApp app = (MyApp) requireActivity().getApplication();
        this.currentUser = app.getUserViewModel().getUsername();

        // Initialize the ScanDescription and ScanSerial buttons
        scanDescription.setOnClickListener(v -> openScanIntent(descriptionEditText));
        scanSerial.setOnClickListener(v -> openScanIntent(serialNumberEditText));

        // Set an onClickListener for the purchase date EditText to show a date picker
        purchaseDateEditText.setOnClickListener(v -> { showDatePicker(); });

        // Set an onClickListener for the Next button
        nextButton.setOnClickListener(v -> {

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
                    builder.setTitle("Error"); // Optional title
                    builder.setMessage("Please at least fill in the value and date!");
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    return;
                }
                Double official_estimated_value = Double.parseDouble(estimated_val);
                // Format the value to two decimal places
                String formattedValue = String.format(Locale.getDefault(), "%.2f", official_estimated_value);
                // Parse the formatted value back to double
                official_estimated_value = Double.parseDouble(formattedValue);

                String comm = commentEditText.getText().toString().trim();
                String documentID = retrieveDocId(currentItem);
//                ArrayList<String> imageUrl = imageUrisList != null ? imageUrisList : new ArrayList<>();

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
//                    currentItem.setImageUrl(imageUrl);
                    itemToSave = currentItem;
                } else {
                    newItem = true;
                    // It's a new item
                    itemToSave = new InventoryItem(officialDate, description, make, model, serialNumber, official_estimated_value, comm, documentID, new ArrayList<>(), currentUser);

                }

            if (getActivity() instanceof MainActivity) {
                AddItemNextFragment nextFragment = new AddItemNextFragment(itemToSave, newItem);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frag_container, nextFragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });

        if (currentItem == null) {
            cancelButton.setVisibility(View.GONE);
        }
        cancelButton.setOnClickListener(v -> {
            // Show the HomeFragment
            if (getActivity() instanceof MainActivity) {
                HomeFragment homeFragment = new HomeFragment();
                ((MainActivity) getActivity()).setFragment(homeFragment);
            }
        });

        // Prepopulate fields if currentItem is not null (i.e., we're editing an existing item)
        if (currentItem != null) {
            title.setText("View/Update Item");
            prepopulateFields(currentItem);
        }
        return view;
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
        title = view.findViewById(R.id.title);
        storageRef = FirebaseStorage.getInstance().getReference();
        scanDescription = view.findViewById(R.id.btnScanDescription);
        scanSerial = view.findViewById(R.id.btnScanSerial);
    }



    /**
     * Shows a date picker dialog for selecting a purchase date.
     */
    private void showDatePicker() {
        if (currentItem != null && currentItem.getPurchaseDate() != null) {
            currentItem.getPurchaseDate();
        }

        DatePickerDialog dpd = new DatePickerDialog(getContext(), R.style.DatePickerDialogTheme, dateSetListener, currentDate.getYear(),
                currentDate.getMonthValue() - 1, // Month is 0-indexed in DatePickerDialog
                currentDate.getDayOfMonth());

        Instant currentDate = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();
        dpd.getDatePicker().setMaxDate(currentDate.toEpochMilli());
        dpd.show();
    }

    /**
     * Gets item id from FireStore
     */
    private String retrieveDocId(InventoryItem item) {
        if (item != null) {
            return item.getDocId();
        } else {
            DocumentReference newDocRef = ((MainActivity)getActivity()).getDb().collection("Item").document();
            return newDocRef.getId();
        }
    }

    /**
     * Populates the field when we load an already existing item
     */
    private void prepopulateFields(InventoryItem item) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        purchaseDateEditText.setText(sdf.format(item.getPurchaseDate()));
        descriptionEditText.setText(item.getDescription());
        makeEditText.setText(item.getMake());
        modelEditText.setText(item.getModel());
        serialNumberEditText.setText(item.getSerialNumber());
        estimatedValueEditText.setText(String.format(Locale.getDefault(), "%.2f", item.getEstimatedValue()));
        commentEditText.setText(item.getComment());
    }

    /**
     * Updates the date based on user selection
     */
    private final DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
        LocalDate dateSet = LocalDate.of(year, monthOfYear + 1, dayOfMonth);
        updateLabel(dateSet);
    };


    private void updateLabel(LocalDate dateSet) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_FORMAT);
        purchaseDateEditText.setText(dateSet.format(dtf));
    }

    /**
     * Opens the Fragment for Scanning
     */
    private void openScanIntent(EditText textToAutofill) {
        ScanFragment scanFragment = new ScanFragment(textToAutofill);
        scanFragment.show(getActivity().getSupportFragmentManager(), "ADD_TAG");
    }

}