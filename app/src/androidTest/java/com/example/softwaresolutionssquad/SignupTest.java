package com.example.softwaresolutionssquad;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.app.Activity;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

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
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String TESTUSER = "UiTestUser";
    private final String PASSWORD = "UiTestPass";
    private final Map<String, Object> userData = new HashMap() {
        { put("username", TESTUSER); put("password", Utils.hashPassword(PASSWORD)); put("displayName", "UI Test"); }
    };
    @Rule
    public ActivityScenarioRule<SignupActivity> scenario = new
            ActivityScenarioRule<>(SignupActivity.class);

    @Test
    public void TestReturnToLogin() {
        // Arrange
        Intents.init();
        // Act
        onView(withId(R.id.signup_page_existing_user_click_text)).perform(click());
        // Assert
        intended(hasComponent(LoginActivity.class.getName()));
        // Clean
        Intents.release();
    }

    @Test
    public void TestSignup() {
        try {
            // Arrange
            Intents.init();
            // Act
            onView(withId(R.id.signup_page_signup_email_edittext)).perform(typeText(TESTUSER));
            onView(withId(R.id.signup_page_signup_password_edittext)).perform(typeText(PASSWORD));
            onView(withId(R.id.signup_page_name_edittext)).perform(typeText(TESTUSER));
            onView(withId(R.id.signup_page_signup_button)).perform(click());
            // Assert
            // why sleep? LoginActivity is not instantaneous as it adds user to database
            Thread.sleep(1000);
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
            Thread.sleep(1000);
        } catch (Exception ex) {

        } finally {
            // Clean
            db.collection("User").document(TESTUSER).delete();
            Intents.release();
        }
    }
}
