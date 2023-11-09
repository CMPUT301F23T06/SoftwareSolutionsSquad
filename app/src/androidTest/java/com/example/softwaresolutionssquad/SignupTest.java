package com.example.softwaresolutionssquad;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.os.Handler;
import android.os.Looper;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.softwaresolutionssquad.controllers.Utils;
import com.example.softwaresolutionssquad.views.LoginActivity;
import com.example.softwaresolutionssquad.views.SignupActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@RunWith(AndroidJUnit4.class)
public class SignupTest {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String TESTUSER = "UiTestUser";
    private final String PASSWORD = "UiTestPass";
    private final Map<String, Object> userData = new HashMap<String, Object>() {{
        put("username", TESTUSER);
        put("password", Utils.hashPassword(PASSWORD));
        put("displayName", "UI Test");
    }};

    @Rule
    public ActivityScenarioRule<SignupActivity> scenario = new ActivityScenarioRule<>(SignupActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() throws InterruptedException {
        // Clean up the user for the test
        CountDownLatch deleteLatch = new CountDownLatch(1);
        db.collection("User").document(TESTUSER).delete().addOnCompleteListener(task -> deleteLatch.countDown());
        deleteLatch.await(); // Wait for Firebase operation to complete

        Intents.release();
    }

    @Test
    public void TestReturnToLogin() {
        onView(withId(R.id.signup_page_existing_user_click_text)).perform(click());
        intended(hasComponent(LoginActivity.class.getName()));
    }

    @Test
    public void TestEmptyUsername() {
        onView(withId(R.id.signup_page_name_edittext)).perform(typeText(""));
        onView(withId(R.id.signup_page_signup_password_edittext)).perform(typeText(PASSWORD));
        onView(withId(R.id.signup_page_signup_button)).perform(click());
        onView(withId(R.id.signup_page_error_message))
                .check(matches(withText("Please fill in all fields.")));
    }

    @Test
    public void TestEmptyPassword() {
        onView(withId(R.id.signup_page_name_edittext)).perform(typeText("TestName"));
        onView(withId(R.id.signup_page_signup_email_edittext)).perform(typeText("test@example.com"));
        // No password input
        onView(withId(R.id.signup_page_signup_button)).perform(click());
        onView(withId(R.id.signup_page_error_message))
                .check(matches(withText("Please fill in all fields.")));
    }

    @Test
    public void TestEmptyEmail() {
        onView(withId(R.id.signup_page_name_edittext)).perform(typeText("TestName"));
        onView(withId(R.id.signup_page_signup_password_edittext)).perform(typeText(PASSWORD));
        // No email (username) input
        onView(withId(R.id.signup_page_signup_button)).perform(click());
        onView(withId(R.id.signup_page_error_message))
                .check(matches(withText("Please fill in all fields.")));
    }

    @Test
    public void TestExistingUsername() throws InterruptedException {
        // Arrange
        DocumentReference userRef = db.collection("User").document(TESTUSER);
        userRef.set(userData); // Pre-populate the database with the test user

        // Give Firebase some time to finish the operation
        CountDownLatch setupLatch = new CountDownLatch(1);
        userRef.get().addOnCompleteListener(task -> setupLatch.countDown());
        setupLatch.await();

        // Act
        onView(withId(R.id.signup_page_name_edittext)).perform(typeText("TestName"));
        onView(withId(R.id.signup_page_signup_email_edittext)).perform(typeText(TESTUSER));
        onView(withId(R.id.signup_page_signup_password_edittext)).perform(typeText(PASSWORD));
        onView(withId(R.id.signup_page_signup_button)).perform(click());

        // Assert
        onView(withId(R.id.signup_page_error_message))
                .check(matches(withText("Username already exists. Please try a different one.")));
    }

    @Test
    public void TestSignup() throws InterruptedException {
        // Arrange
        // No need to call Intents.init() because it's already done in setUp()

        // Act
        onView(withId(R.id.signup_page_name_edittext)).perform(typeText(TESTUSER));
        onView(withId(R.id.signup_page_signup_email_edittext)).perform(typeText(TESTUSER));
        onView(withId(R.id.signup_page_signup_password_edittext)).perform(typeText(PASSWORD));
        onView(withId(R.id.signup_page_signup_button)).perform(click());

        // Use an IdlingResource or other synchronization to wait for the expected condition
        // For demonstration purposes only, not recommended for production use
        CountDownLatch latch = new CountDownLatch(1);
        // Assume that a method is available to notify when the operation is done
        waitForOperationToComplete(() -> latch.countDown());
        latch.await(); // Wait for Firebase operation to complete

        // Assert
        intended(hasComponent(LoginActivity.class.getName()));
        // Proceed with additional assertions if needed
    }

    // Dummy method to represent waiting for an operation to complete
    private void waitForOperationToComplete(Runnable callback) {
        // Simulate waiting for an async operation like a network call
        new Handler(Looper.getMainLooper()).postDelayed(callback, 5000);
    }

}
