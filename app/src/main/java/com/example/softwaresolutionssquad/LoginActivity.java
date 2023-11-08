package com.example.softwaresolutionssquad;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference users;
    private Button loginButton;
    private TextView createAccount;
    private EditText usernameInput;
    private EditText passwordInput;
    private TextView errorMessageTextView;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        users = db.collection("User");

        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.login_page_login_button);
        usernameInput = findViewById(R.id.login_page_login_email_edittext);
        passwordInput = findViewById(R.id.login_page_login_password_edittext);
        createAccount = findViewById(R.id.login_page_create_account_click_text);
        errorMessageTextView = findViewById(R.id.login_page_error_message);


        createAccount.setOnClickListener(v -> {
            Intent signup = new Intent(this, SignupActivity.class);
            startActivity(signup);
        });
        loginButton.setOnClickListener(v -> LoginListener());
    }

    private void LoginListener() {
        String username = usernameInput.getText().toString().trim();
        String inputPassword = passwordInput.getText().toString().trim();

        if (username.isEmpty() || inputPassword.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }

        String hashedPassword = Utils.hashPassword(inputPassword);
        if (hashedPassword == null) {
            Toast.makeText(LoginActivity.this, "Error hashing password.", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference userRef = db.collection("User").document(username);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot user = task.getResult();
                if (user != null && user.exists() && hashedPassword.equals(user.getString("password"))) {
                    // correct password, now logged in
                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                } else {
                    // incorrect password or user does not exist
                    showError("Login failed. Please try again later.");
                }
            } else {
                showError("Login failed. Please try again later.");
            }
        });
    }

    private void showError(String error) {
        errorMessageTextView.setText(error);
        errorMessageTextView.setVisibility(View.VISIBLE);
    }
}

