package com.example.softwaresolutionssquad.views;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.softwaresolutionssquad.R;

public class WelcomeActivity extends AppCompatActivity {

    /**
     * Called when the activity is starting. Initializes the activity, sets up the UI layout, and configures button interactions.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button loginButton = findViewById(R.id.welcome_page_login_button);
        Button signupButton = findViewById(R.id.welcome_page_signup_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click on the login button
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click on the signup button
                Intent intent = new Intent(WelcomeActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }
}
