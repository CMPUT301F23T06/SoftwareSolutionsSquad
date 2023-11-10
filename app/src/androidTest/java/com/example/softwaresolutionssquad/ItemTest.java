package com.example.softwaresolutionssquad;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.example.softwaresolutionssquad.models.InventoryItem;
import com.example.softwaresolutionssquad.models.InventoryModel;
import com.example.softwaresolutionssquad.views.InventoryListAdapter;
import com.example.softwaresolutionssquad.views.MainActivity;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;


import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import static java.lang.Thread.sleep;

import static kotlin.jvm.internal.Intrinsics.checkNotNull;

import android.view.View;
import android.widget.DatePicker;

import java.time.LocalDate;


@RunWith(AndroidJUnit4.class)
public class ItemTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);
    private String uniqueDescription;


    // Set up method to navigate to the AddItemFragment
    @Before
    public void setUp() throws InterruptedException {
        sleep(2500);
        // Assuming the add button is visible on the MainActivity and has a specific ID
        onView(withId(R.id.add_icon)).perform(click());
        // We wait until the AddItemFragment is displayed
        onView(withId(R.id.edtPurchaseDate)).check(matches(isDisplayed()));
    }

    @Test
    public void testAddNewItem() throws InterruptedException {
        // Assume we have a unique description for each test run, for example using a timestamp
        uniqueDescription = "New Camera " + System.currentTimeMillis();

        // Input the item details into the AddItemFragment's EditText fields
        // Open the DatePickerDialog
        onView(withId(R.id.edtPurchaseDate)).perform(click());

        // Set the date to a day before the current date
        onView(withClassName(Matchers.equalTo(DatePicker.class.getName())))
                .perform(setDate(LocalDate.now().getYear(),
                        LocalDate.now().getMonthValue(),
                        LocalDate.now().minusDays(1).getDayOfMonth()));

        // Confirm the date selection
        onView(withText("OK")).perform(click());

        // Confirm the date selection
        onView(withId(R.id.edtDescription)).perform(typeText(uniqueDescription), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.edtMake)).perform(typeText("Canon"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.edtModel)).perform(typeText("EOS"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.edtSerialNumber)).perform(typeText("123456789"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.editTextNumberDecimal)).perform(typeText("999.99"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.edtCommentTitle)).perform(typeText("Great condition!"), ViewActions.closeSoftKeyboard());

        // Click the Next/Save button to save the item
        onView(withId(R.id.btnNext)).perform(click());
        sleep(4000);

        // Now verify the item was added
        onData(withItemContent(uniqueDescription)) // Custom matcher to find the item with the uniqueDescription
                .inAdapterView(withId(R.id.inventory_list_view)) // Replace with your ListView id
                .check(matches(isDisplayed())); // Check that the item is displayed
    }

    // Custom matcher method to find an item in the ListView with the given content
    public static Matcher<Object> withItemContent(final String expectedContent) {
        checkNotNull(expectedContent);
        return new BoundedMatcher<Object, InventoryItem>(InventoryItem.class) {
            @Override
            public boolean matchesSafely(InventoryItem item) {
                // Assuming InventoryItem has a method to get the description or relevant text
                return expectedContent.equals(item.getDescription());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with item content: " + expectedContent);
            }
        };
    }


    // Custom action to set the date in a DatePicker
    public static ViewAction setDate(final int year, final int monthOfYear, final int dayOfMonth) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return allOf(isAssignableFrom(DatePicker.class), isDisplayed());
            }

            @Override
            public String getDescription() {
                return "set date on DatePicker";
            }

            @Override
            public void perform(UiController uiController, View view) {
                DatePicker datePicker = (DatePicker) view;
                datePicker.updateDate(year, monthOfYear - 1, dayOfMonth);
            }
        };
    }
}

