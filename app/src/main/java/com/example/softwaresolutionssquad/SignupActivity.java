package com.example.softwaresolutionssquad;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class SignupActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference users;
    private Button signupButton;
    private TextView loginButton;
    private EditText nameInput;
    private EditText usernameInput;
    private EditText passwordInput;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        users = db.collection("User");

        setContentView(R.layout.activity_signup);

        signupButton = findViewById(R.id.signup_page_signup_button);
        loginButton = findViewById(R.id.signup_page_existing_user_click_text);
        nameInput = findViewById(R.id.signup_page_name_edittext);
        usernameInput = findViewById(R.id.signup_page_signup_email_edittext);
        passwordInput = findViewById(R.id.signup_page_signup_password_edittext);

        loginButton.setOnClickListener(v -> { finish(); });
        signupButton.setOnClickListener(v -> SignupListener());
    }

    private void SignupListener() {
        String username = usernameInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String name = nameInput.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty() || name.isEmpty()) {
            Toast.makeText(SignupActivity.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentReference userRef = users.document(username);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot user = task.getResult();
                if (user != null && user.exists()) {
                    Toast.makeText(SignupActivity.this, "Username already exists. Please try a different one.", Toast.LENGTH_SHORT).show();
                } else {
                    // Hash the password before storing it
                    String hashedPassword = Utils.hashPassword(password);

                    if (hashedPassword != null) {
                        // create user
                        Map<String, Object> newUser = new HashMap<>();
                        newUser.put("username", username);
                        newUser.put("displayName", name);
                        newUser.put("password", hashedPassword); // Store the hashed password

                        users.document(username).set(newUser).addOnSuccessListener(unused -> {
                            Toast.makeText(SignupActivity.this, "Sign up successful!", Toast.LENGTH_SHORT).show();
                            // Directing to login page or dashboard after sign up, based on your flow
                            // For instance:
                            // Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                            // startActivity(intent);
                            finish();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(SignupActivity.this, "Sign up failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        Toast.makeText(SignupActivity.this, "An error occurred during password hashing.", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(SignupActivity.this, "Failed to check existing users. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
