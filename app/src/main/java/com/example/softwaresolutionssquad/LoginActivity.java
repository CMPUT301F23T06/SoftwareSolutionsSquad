package com.example.softwaresolutionssquad;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

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
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();

        DocumentReference userRef = users.document(username);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot user = task.getResult();
                    if (user.exists()) {
                        if (user.get("password", String.class).equals(password)) {
                            // correct password, now logged in

                            // traverse to home page
                            Intent main = new Intent(getBaseContext(), MainActivity.class);
                            startActivity(main);
                        } else {
                            // incorrect password
                            // show "Incorrect username or password" message
                        }
                    } else {
                        // user doesnt exist, suggest sign up page?
                    }
                }
            }
        });
    }

    private static Cipher cipher;
    public static String encrypt(String input) {
        try {
            if (cipher == null) {
                KeyGenerator keygen = KeyGenerator.getInstance("AES");
                keygen.init(256);
                SecretKey key = keygen.generateKey();
                cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
                cipher.init(Cipher.ENCRYPT_MODE, key);
            }
            byte[] ciphertext = cipher.doFinal(input.getBytes());
            byte[] iv = cipher.getIV();
            return ciphertext.toString();
        } catch (Exception ex) {
            // not sure what to do if encryption breaks
            // probably throw exception and thing calling it deals with it appropriately
        }
        // figuring this out still idk
        return "";
    }
}
