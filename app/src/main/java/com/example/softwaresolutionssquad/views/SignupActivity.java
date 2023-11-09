package com.example.softwaresolutionssquad.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.softwaresolutionssquad.R;
import com.example.softwaresolutionssquad.controllers.Utils;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * Activity for handling user sign-up.
 */
public class SignupActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private CollectionReference users;
    private Button signupButton;
    private TextView loginButton;
    private EditText nameInput;
    private EditText usernameInput;
    private EditText passwordInput;
    private TextView errorMessageTextView;

    /**
     * Initializes the sign-up view and its components.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being
     *                           shut down then this Bundle contains the data it most recently
     *                           supplied in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Firestore database reference
        db = FirebaseFirestore.getInstance();
        users = db.collection("User");

        // Setup the UI components
        setupUIComponents();

        // Set click listeners for buttons
        setClickListeners();
    }

    /**
     * Setups UI components by binding them to their corresponding views.
     */
    private void setupUIComponents() {
        signupButton = findViewById(R.id.signup_page_signup_button);
        loginButton = findViewById(R.id.signup_page_existing_user_click_text);
        nameInput = findViewById(R.id.signup_page_name_edittext);
        usernameInput = findViewById(R.id.signup_page_signup_email_edittext);
        passwordInput = findViewById(R.id.signup_page_signup_password_edittext);
        errorMessageTextView = findViewById(R.id.signup_page_error_message);
    }

    /**
     * Sets click listeners for the interactive components.
     */
    private void setClickListeners() {
        loginButton.setOnClickListener(v -> navigateToLogin());
        signupButton.setOnClickListener(v -> signUp());
    }

    /**
     * Handles the sign-up operation with input validation and Firebase interaction.
     */
    private void signUp() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String name = nameInput.getText().toString().trim();

        // Validate the input fields
        if (username.isEmpty() || password.isEmpty() || name.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        DocumentReference userRef = users.document(username);

        // Check if the username already exists
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot user = task.getResult();
                if (user != null && user.exists()) {
                    showError("Username already exists. Please try a different one.");
                } else {
                    createUser(username, password, name);
                }
            } else {
                showError("Failed to check existing users. Try again.");
            }
        });
    }

    /**
     * Creates a new user with hashed password and stores it in the database.
     *
     * @param username The username of the new user.
     * @param password The password of the new user.
     * @param name     The display name of the new user.
     */
    private void createUser(String username, String password, String name) {
        String hashedPassword = Utils.hashPassword(password);

        if (hashedPassword != null) {
            Map<String, Object> newUser = new HashMap<>();
            newUser.put("username", username);
            newUser.put("displayName", name);
            newUser.put("password", hashedPassword);

            // Store the new user in Firestore
            users.document(username).set(newUser).addOnSuccessListener(unused -> {
                Toast.makeText(SignupActivity.this, "Sign up successful!", Toast.LENGTH_SHORT).show();
                navigateToLogin();
            }).addOnFailureListener(e -> {
                showError("Sign up failed: " + e.getMessage());
            });
        } else {
            showError("An error occurred during password hashing.");
        }
    }

    /**
     * Navigates to the Login Activity.
     */
    private void navigateToLogin() {
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Displays an error message to the user.
     *
     * @param error The error message to be displayed.
     */
    private void showError(String error) {
        errorMessageTextView.setText(error);
        errorMessageTextView.setVisibility(View.VISIBLE);
    }
}
