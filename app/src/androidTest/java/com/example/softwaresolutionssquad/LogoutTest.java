package com.example.softwaresolutionssquad;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.example.softwaresolutionssquad.models.InventoryItem;
import com.example.softwaresolutionssquad.views.MainActivity;
import com.example.softwaresolutionssquad.R;
import com.example.softwaresolutionssquad.views.MyApp;
import com.example.softwaresolutionssquad.views.UserViewModel;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static com.example.softwaresolutionssquad.ItemTest.withItemContent;
import static org.hamcrest.Matchers.allOf;

import static java.lang.Thread.sleep;
import static kotlin.jvm.internal.Intrinsics.checkNotNull;

import android.view.View;
import android.widget.DatePicker;

import java.time.LocalDate;


/**
 * Tests for adding and editing inventory items in the MainActivity.
 */
@RunWith(AndroidJUnit4.class)
public class LogoutTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);
    private String uniqueDescription, makeValue, modelValue;
    private static final String TEST_USER = "pratham@test.com";
    /**
     * Sets up the test environment before each test.
     */
    @Before
    public void setUp() throws InterruptedException {
        sleep(2500);
        activityScenarioRule.getScenario().onActivity(a -> {
            MyApp myApp = (MyApp) a.getApplication();
            UserViewModel userViewModel = myApp.getUserViewModel();
            userViewModel.setUsername(TEST_USER);
        });
    }

    @Test
    public void testTagFragmentUI() {
        // Navigate to the ProfileFragment
        onView(withId(R.id.navigation_profile)).perform(click());

        // Check if the views are displayed
        onView(withId(R.id.itemAmount)).check(matches(isDisplayed()));
        onView(withId(R.id.tagAmount)).check(matches(isDisplayed()));
        onView(withId(R.id.logoutBar)).check(matches(isDisplayed()));

        // Perform a click on the logoutBar
        onView(withId(R.id.logoutBar)).perform(click());

        // Check if the login screen is displayed after logout
        onView(withId(R.id.login_page_login_textview)).check(matches(isDisplayed()));
    }
}
