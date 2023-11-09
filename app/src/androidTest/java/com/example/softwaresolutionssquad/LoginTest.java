package com.example.softwaresolutionssquad;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class LoginTest {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TEST_USER = "UiTestUser";
    private static final String PASSWORD = "UiTestPass";
    private final Map<String, Object> userData = new HashMap<String, Object>() {{
        put("username", TEST_USER);
        put("password", Utils.hashPassword(PASSWORD));
        put("displayName", "UI Test");
    }};

    @Rule
    public ActivityScenarioRule<LoginActivity> activityScenarioRule = new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void setUp() {
        Intents.init(); // Initialize Intents here, before each test
    }

    @After
    public void cleanUp() {
        db.collection("User").document(TEST_USER).delete();
        Intents.release(); // Release Intents here, after each test
    }

    @Test
    public void testCreateAccount() {
        onView(withId(R.id.login_page_create_account_click_text)).perform(click());
        intended(hasComponent(SignupActivity.class.getName()));
    }

    @Test
    public void testLoginButtonWithEmptyFields() {
        onView(withId(R.id.login_page_login_button)).perform(click());
        onView(withId(R.id.login_page_error_message)).check(matches(withText("Please enter both username and password.")));
    }

    @Test
    public void testLoginButtonWithEmailOnly() {
        onView(withId(R.id.login_page_login_email_edittext)).perform(typeText(TEST_USER));
        onView(withId(R.id.login_page_login_button)).perform(click());
        onView(withId(R.id.login_page_error_message)).check(matches(withText("Please enter both username and password.")));
    }

    @Test
    public void testLoginButtonWithPasswordOnly() {
        onView(withId(R.id.login_page_login_password_edittext)).perform(typeText(PASSWORD));
        onView(withId(R.id.login_page_login_button)).perform(click());
        onView(withId(R.id.login_page_error_message)).check(matches(withText("Please enter both username and password.")));
    }

    @Test
    public void testLoginWithInvalidCredentials() {
        onView(withId(R.id.login_page_login_email_edittext)).perform(typeText("invalidUser"));
        onView(withId(R.id.login_page_login_password_edittext)).perform(typeText("invalidPass"));
        onView(withId(R.id.login_page_login_button)).perform(click());
        onView(withId(R.id.login_page_error_message)).check(matches(withText("Login failed. Please try again later.")));
    }

    @Test
    public void testLogin() {
        db.collection("User").document(TEST_USER).set(userData);
        onView(withId(R.id.login_page_login_email_edittext)).perform(typeText(TEST_USER));
        onView(withId(R.id.login_page_login_password_edittext)).perform(typeText(PASSWORD));
        onView(withId(R.id.login_page_login_button)).perform(click());
        intended(hasComponent(MainActivity.class.getName()));
        // The user clean up is handled in the @After cleanUp method
    }
}
