package com.example.softwaresolutionssquad;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.example.softwaresolutionssquad.controllers.Utils;
import com.example.softwaresolutionssquad.views.LoginActivity;
import com.example.softwaresolutionssquad.views.MainActivity;
import com.example.softwaresolutionssquad.views.SignupActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class LoginTest {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TEST_USER = "UiTestUser@test.com";
    private static final String PASSWORD = "UiTestPass";
    private final Map<String, Object> userData = new HashMap<String, Object>() {{
        put("username", TEST_USER);
        put("password", Utils.hashPassword(PASSWORD));
        put("displayName", "UI Test");
    }};

    @Rule
    public ActivityScenarioRule<LoginActivity> activityScenarioRule = new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testCreateAccount() {
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
    public void testLoginButtonWithEmptyFields() {
        onView(withId(R.id.login_page_login_button)).perform(click());
        onView(withId(R.id.login_page_error_message)).check(matches(withText("Please enter both username and password.")));
    }

    @Test
    public void testLoginButtonWithEmailOnly() {
        onView(withId(R.id.login_page_login_email_edittext)).perform(typeText(TEST_USER), closeSoftKeyboard());
        onView(withId(R.id.login_page_login_button)).perform(click());

        onView(withId(R.id.login_page_error_message)).check(matches(withText("Please enter both username and password.")));
    }

    @Test
    public void testLoginButtonWithPasswordOnly() {
        onView(withId(R.id.login_page_login_password_edittext)).perform(typeText(PASSWORD), closeSoftKeyboard());
        onView(withId(R.id.login_page_login_button)).perform(click());

        onView(withId(R.id.login_page_error_message)).check(matches(withText("Please enter both username and password.")));
    }

    @Test
    public void testLoginWithInvalidCredentials() {
        onView(withId(R.id.login_page_login_email_edittext)).perform(typeText("invalidUser"), closeSoftKeyboard());
        onView(withId(R.id.login_page_login_password_edittext)).perform(typeText("invalidPass"), closeSoftKeyboard());
        onView(withId(R.id.login_page_login_button)).perform(click());

        onView(withId(R.id.login_page_error_message)).check(matches(withText("Invalid username or password.")));
    }

    @Test
    public void testLogin() throws InterruptedException {
        // Arrange
        Intents.init();
        db.collection("User").document(TEST_USER).set(userData);
        // Act
        onView(withId(R.id.login_page_login_email_edittext)).perform(typeText(TEST_USER), closeSoftKeyboard());
        onView(withId(R.id.login_page_login_password_edittext)).perform(typeText(PASSWORD), closeSoftKeyboard());
        onView(withId(R.id.login_page_login_button)).perform(click());
        Thread.sleep(2000);
        // Assert
        intended(hasComponent(MainActivity.class.getName()));
        // Clean
        db.collection("User").document(TEST_USER).delete();
        Intents.release();
    }
}
