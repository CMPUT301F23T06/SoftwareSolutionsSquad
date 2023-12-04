package com.example.softwaresolutionssquad;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static java.lang.Thread.sleep;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.softwaresolutionssquad.controllers.Utils;
import com.example.softwaresolutionssquad.views.LoginActivity;
import com.example.softwaresolutionssquad.views.SignupActivity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class SignupTest {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String TESTUSER = "UiTestUser";
    private final String PASSWORD = "UiTestPass";
    private final Map<String, Object> userData = new HashMap() {
        { put("username", TESTUSER); put("password", Utils.hashPassword(PASSWORD)); put("displayName", "UI Test"); }
    };
    @Rule
    public ActivityScenarioRule<SignupActivity> scenario = new
            ActivityScenarioRule<>(SignupActivity.class);

    /**
     * Tests the functionality of returning to the login screen from the signup screen.
     * @throws InterruptedException if the thread is interrupted while sleeping.
     */
    @Test
    public void TestReturnToLogin() throws InterruptedException {
        // Arrange
        Intents.init();
        // Act
        onView(withId(R.id.signup_page_existing_user_click_text)).perform(click());
        // Assert
        intended(hasComponent(LoginActivity.class.getName()));
        // Clean
        Intents.release();
    }

    /**
     * Tests the signup functionality with an empty name field.
     */
    @Test
    public void TestEmptyName() {
        // Arrange
        Intents.init();
        // Act
        onView(withId(R.id.signup_page_name_edittext)).perform(typeText("TestName"));
        onView(withId(R.id.signup_page_signup_password_edittext)).perform(typeText(PASSWORD), closeSoftKeyboard());
        onView(withId(R.id.signup_page_signup_button)).perform(click());
        // Assert
        onView(withId(R.id.signup_page_error_message))
                .check(matches(withText("Please fill in all fields.")));
        // Clean
        Intents.release();
    }

    /**
     * Tests the signup functionality with an empty password field.
     */
    @Test
    public void TestEmptyPassword() {
        // Arrange
        Intents.init();
        // Act
        onView(withId(R.id.signup_page_name_edittext)).perform(typeText("TestName"));
        onView(withId(R.id.signup_page_signup_username_edittext)).perform(typeText(TESTUSER), closeSoftKeyboard());
        onView(withId(R.id.signup_page_signup_button)).perform(click());
        // Assert
        onView(withId(R.id.signup_page_error_message))
                .check(matches(withText("Please fill in all fields.")));
        // Clean
        Intents.release();
    }

    /**
     * Tests the signup functionality with an empty username field.
     */
    @Test
    public void TestEmptyUsername() {
        // Arrange
        Intents.init();
        // Act
        onView(withId(R.id.signup_page_name_edittext)).perform(typeText("TestName"));
        onView(withId(R.id.signup_page_signup_password_edittext)).perform(typeText(PASSWORD), closeSoftKeyboard());
        // No username input
        onView(withId(R.id.signup_page_signup_button)).perform(click());
        // Assert
        onView(withId(R.id.signup_page_error_message))
                .check(matches(withText("Please fill in all fields.")));
        // Clean
        Intents.release();
    }

    /**
     * Tests the signup functionality with an existing username in the database.
     * @throws InterruptedException if the thread is interrupted while sleeping.
     */
    @Test
    public void TestExistingUsername() throws InterruptedException {
        // Arrange
        DocumentReference userRef = db.collection("User").document(TESTUSER);
        userRef.set(userData); // Pre-populate the database with the test user
        Intents.init();
        // Act
        onView(withId(R.id.signup_page_name_edittext)).perform(typeText("TestName"), closeSoftKeyboard());
        onView(withId(R.id.signup_page_signup_username_edittext)).perform(typeText(TESTUSER), closeSoftKeyboard());
        onView(withId(R.id.signup_page_signup_password_edittext)).perform(typeText(PASSWORD), closeSoftKeyboard());
        onView(withId(R.id.signup_page_signup_button)).perform(click());
        Thread.sleep(2000);
        // Assert
        onView(withId(R.id.signup_page_error_message))
                .check(matches(withText("Username already exists. Please try a different one.")));
        // Clean
        db.collection("User").document(TESTUSER).delete();
        Intents.release();
    }

    /**
     * Tests the signup functionality with valid inputs.
     */
    @Test
    public void TestSignup() {
        try {
            // Arrange
            Intents.init();
            // Act
            onView(withId(R.id.signup_page_name_edittext)).perform(typeText(TESTUSER));
            onView(withId(R.id.signup_page_signup_username_edittext)).perform(typeText(TESTUSER));
            onView(withId(R.id.signup_page_signup_password_edittext)).perform(typeText(PASSWORD), closeSoftKeyboard());
            onView(withId(R.id.signup_page_signup_button)).perform(click());
            // Assert
            // why sleep? LoginActivity is not instantaneous as it adds user to database
            sleep(1000);
            intended(hasComponent(LoginActivity.class.getName()));
            DocumentReference userRef = db.collection("User").document(TESTUSER);

            userRef.get().addOnCompleteListener(task -> {
                Assert.assertTrue(task.isSuccessful());
                DocumentSnapshot user = task.getResult();
                Assert.assertNotNull(user);
                Assert.assertTrue(user.exists());
                Assert.assertEquals(Utils.hashPassword(PASSWORD), user.getString("password"));
                Assert.assertEquals(TESTUSER, user.getString("displayName"));
            });
            // why sleep? Testing values in the database is not instantaneous
            sleep(1000);
        } catch (Exception ex) {

        } finally {
            // Clean
            db.collection("User").document(TESTUSER).delete();
            Intents.release();
        }
    }
}