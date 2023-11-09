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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * LoginActivity handles user authentication against Firebase's Firestore.
 */
public class LoginActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private Button loginButton;
    private TextView createAccountTextView;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private TextView errorMessageTextView;

    /**
     * Initializes the login activity, view components and Firestore references.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();

        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.login_page_login_button);
        usernameEditText = findViewById(R.id.login_page_login_email_edittext);
        passwordEditText = findViewById(R.id.login_page_login_password_edittext);
        createAccountTextView = findViewById(R.id.login_page_create_account_click_text);
        errorMessageTextView = findViewById(R.id.login_page_error_message);

        setUpCreateAccount();
        setUpLoginButton();
    }

    /**
     * Sets up the create account text view click listener.
     */
    private void setUpCreateAccount() {
        createAccountTextView.setOnClickListener(v -> {
            Intent signupIntent = new Intent(this, SignupActivity.class);
            startActivity(signupIntent);
        });
    }

    /**
     * Sets up the login button click listener.
     */
    private void setUpLoginButton() {
        loginButton.setOnClickListener(v -> attemptLogin());
    }

    /**
     * Attempts to log in the user and handles authentication logic.
     */
    private void attemptLogin() {
        String username = usernameEditText.getText().toString().trim();
        String inputPassword = passwordEditText.getText().toString().trim();

        if (username.isEmpty() || inputPassword.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }

        String hashedPassword = Utils.hashPassword(inputPassword);
        if (hashedPassword == null) {
            Toast.makeText(LoginActivity.this, "Error hashing password.", Toast.LENGTH_SHORT).show();
            return;
        }

        authenticateUser(username, hashedPassword);
    }

    /**
     * Authenticates the user against the Firestore database.
     *
     * @param username       The username input by the user.
     * @param hashedPassword The hashed password for security purposes.
     */
    private void authenticateUser(String username, String hashedPassword) {
        DocumentReference userRef = db.collection("User").document(username);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot user = task.getResult();
                if (user != null && user.exists() && hashedPassword.equals(user.getString("password"))) {
                    navigateToMain();
                } else {
                    showError("Invalid username or password.");
                }
            } else {
                showError("An error occurred. Please try again later.");
            }
        });
    }

    /**
     * Navigates the user to the main activity upon successful login.
     */
    private void navigateToMain() {
        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    /**
     * Shows an error message to the user.
     *
     * @param error The error message to be displayed.
     */
    private void showError(String error) {
        errorMessageTextView.setText(error);
        errorMessageTextView.setVisibility(View.VISIBLE);
    }
}
