package com.example.softwaresolutionssquad;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class LoginActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference users;
    private Button loginButton;
    private TextView createAccount;
    private EditText usernameInput;
    private EditText passwordInput;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        users = db.collection("User");

        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.login_page_login_button);
        usernameInput = findViewById(R.id.login_page_login_email_edittext);
        passwordInput = findViewById(R.id.login_page_login_password_edittext);
        createAccount = findViewById(R.id.login_page_create_account_click_text);

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
            Toast.makeText(LoginActivity.this, "Please enter both username and password.", Toast.LENGTH_SHORT).show();
            return;
        }

        String hashedPassword = hashPassword(inputPassword);
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
                    Toast.makeText(LoginActivity.this, "Incorrect username or password.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoginActivity.this, "Login failed. Please try again later.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String hashPassword(String passwordToHash) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(passwordToHash.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
