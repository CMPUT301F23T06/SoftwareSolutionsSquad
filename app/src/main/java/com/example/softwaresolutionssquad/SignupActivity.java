package com.example.softwaresolutionssquad;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();
        String name = nameInput.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            return;
        }

        DocumentReference userRef = users.document(username);

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot user = task.getResult();
                    if (user.exists()) {
                        // this username is not unique - it already exists
                    } else {
                        // create user
                        Map<String,Object> newUser = new HashMap<>();
                        newUser.put("username", username);
                        newUser.put("displayName", name);
                        newUser.put("password", password);
                        users.document(username).set(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                // show sign up successful message

                                // if we want them to still login after signing up,
                                // finish() will return to login page, if they should already be
                                // logged in now, we need to change this
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // show sign up fail message
                            }
                        });
                    }
                } else {
                    // task was unsuccessful show generic error
                }
            }
        });
    }
}
