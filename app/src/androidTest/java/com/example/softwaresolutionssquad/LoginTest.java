package com.example.softwaresolutionssquad;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import androidx.test.espresso.intent.Intents;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class LoginTest {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String TESTUSER = "UiTestUser";
    private final String PASSWORD = "UiTestPass";
    private final Map<String, Object> userData = new HashMap() {
        { put("username", TESTUSER); put("password", Utils.hashPassword(PASSWORD)); put("displayName", "UI Test"); }
    };
    @Rule
    public ActivityScenarioRule<LoginActivity> scenario = new
            ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void TestCreateAccount() {
        // Arrange
        Intents.init();
        // Act
        onView(withId(R.id.login_page_create_account_click_text)).perform(click());
        // Assert
        intended(hasComponent(SignupActivity.class.getName()));
        // Clean
        Intents.release();
    }

    @Test
    public void TestLogin() {
        // Arrange
        Intents.init();
        db.collection("User").document(TESTUSER).set(userData);
        // Act
        onView(withId(R.id.login_page_login_email_edittext)).perform(typeText(TESTUSER));
        onView(withId(R.id.login_page_login_password_edittext)).perform(typeText(PASSWORD));
        onView(withId(R.id.login_page_login_button)).perform(click());
        // Assert
        intended(hasComponent(MainActivity.class.getName()));
        // Clean
        db.collection("User").document(TESTUSER).delete();
        Intents.release();
    }
}
