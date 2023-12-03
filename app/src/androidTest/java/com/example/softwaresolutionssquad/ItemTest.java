package com.example.softwaresolutionssquad;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.allOf;
import static java.lang.Thread.sleep;
import static kotlin.jvm.internal.Intrinsics.checkNotNull;

import android.Manifest;
import android.view.View;
import android.widget.DatePicker;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import com.example.softwaresolutionssquad.models.InventoryItem;
import com.example.softwaresolutionssquad.views.MainActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDate;


/**
 * Tests for adding and editing inventory items in the MainActivity.
 */
@RunWith(AndroidJUnit4.class)
public class ItemTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);
    private String uniqueDescription, makeValue, modelValue;

    // Flag to track if cleanUp is called from testEditItem
    private boolean isEditTest = false;
    UiDevice device = UiDevice.getInstance(getInstrumentation());
    /**
     * Sets up the test environment before each test.
     */
    @Before
    public void setUp() throws InterruptedException {
        sleep(2500);
        // Assuming the add button is visible on the MainActivity and has a specific ID
        onView(withId(R.id.navigation_add)).perform(click());
        // We wait until the AddItemFragment is displayed
        onView(withId(R.id.edtPurchaseDate)).check(matches(isDisplayed()));
    }

    /**
     * Tests adding a new item to the inventory.
     */

    @Test
    public void testAddNewItem() throws InterruptedException, UiObjectNotFoundException {
        // Assume we have a unique description for each test run, for example using a timestamp
        uniqueDescription = "New Camera " + System.currentTimeMillis();
        makeValue = "Canon";
        modelValue = "EOS";

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
        onView(withId(R.id.edtMake)).perform(typeText(makeValue), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.edtModel)).perform(typeText(modelValue), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.edtSerialNumber)).perform(typeText("123456789"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.editTextNumberDecimal)).perform(typeText("999.99"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.edtCommentTitle)).perform(typeText("Great condition!"), ViewActions.closeSoftKeyboard());

        // Click the Next/Save button to move to next the item
        onView(withId(R.id.btnNext)).perform(scrollTo(), click());
        sleep(1000);

        onView(withId(R.id.btnSelectImage)).perform(click());

        device.wait(Until.hasObject(By.textContains("Pictures")), 10000);
        UiObject picturesButton = device.findObject(new UiSelector().textContains("Pictures"));
        picturesButton.click();

        device.wait(Until.hasObject(By.checkable(true)), 10000);
        UiObject image = device.findObject(new UiSelector().checkable(true));
        image.click();

        device.wait(Until.hasObject(By.textContains("Done")), 10000);
        UiObject doneButton = device.findObject(new UiSelector().textContains("Done"));
        doneButton.click();
        sleep(2000);

        onView(withId(R.id.btnTakePhoto)).perform(click());
        UiObject permissions = device.findObject(new UiSelector().textContains("Only"));

        if (permissions.waitForExists(5000)) {
            permissions.click();
        }

        device.wait(Until.hasObject(By.descContains("shutter")), 10000);

        UiObject takePhotoButton = device.findObject(new UiSelector().descriptionContains("shutter"));
        sleep(2000);
        takePhotoButton.click();
        device.wait(Until.hasObject(By.desc("done")), 10000);

        UiObject confirmButton = device.findObject(new UiSelector().descriptionContains("done"));
        confirmButton.click();
        sleep(2000);

        onView(withId(R.id.btnCreate)).perform(click());
        sleep(5000);

        // Now verify the item was added
        onData(withItemContent(uniqueDescription))
                .inAdapterView(withId(R.id.inventory_list_view))
                .check(matches(isDisplayed()));

        // Call cleanUp specifically for AddItem test
        // Only call cleanUp if not in edit test
        if (!isEditTest) {
            cleanUp(false);
        }

    }

    /**
     * Tests editing an existing item in the inventory.
     */
    @Test
    public void testEditItem() throws InterruptedException, UiObjectNotFoundException {
        // Set flag to indicate this is the EditItem test
        isEditTest = true;

        // First add a new item
        testAddNewItem();

        // Find the newly added item by its unique description and click on it to edit
        onData(withItemContent(uniqueDescription))
                .inAdapterView(withId(R.id.inventory_list_view))
                .perform(click());

        makeValue = "Fuji";
        modelValue = "F20";

        // Edit the fields with new data
        uniqueDescription = "Updated Camera " + System.currentTimeMillis();
        onView(withId(R.id.edtDescription)).perform(clearText(), typeText(uniqueDescription), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.edtMake)).perform(clearText(), typeText(makeValue), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.edtModel)).perform(clearText(), typeText(modelValue), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.edtSerialNumber)).perform(clearText(), typeText("987654321"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.editTextNumberDecimal)).perform(clearText(), typeText("100"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.edtCommentTitle)).perform(clearText(), typeText("Bad condition!"), ViewActions.closeSoftKeyboard());

        // Click the Next/Save button to move to next the item
        onView(withId(R.id.btnNext)).perform(scrollTo(), click());
        sleep(1000);

        onView(withId(R.id.btnSelectImage)).perform(click());
        device.wait(Until.hasObject(By.textContains("Pictures")), 10000);

        UiObject picturesButton = device.findObject(new UiSelector().textContains("Pictures"));
        picturesButton.click();
        device.wait(Until.hasObject(By.checkable(true)), 10000);

        UiObject image = device.findObject(new UiSelector().checkable(true));
        image.click();
        device.wait(Until.hasObject(By.textContains("Done")), 10000);

        UiObject doneButton = device.findObject(new UiSelector().textContains("Done"));
        doneButton.click();
        sleep(2000);

        onView(withId(R.id.btnTakePhoto)).perform(click());
        UiObject permissions = device.findObject(new UiSelector().textContains("Only"));

        if (permissions.waitForExists(5000)) {
            permissions.click();
        }

        device.wait(Until.hasObject(By.descContains("shutter")), 10000);

        UiObject takePhotoButton = device.findObject(new UiSelector().descriptionContains("shutter"));
        sleep(2000);
        takePhotoButton.click();
        device.wait(Until.hasObject(By.desc("done")), 10000);

        UiObject confirmButton = device.findObject(new UiSelector().descriptionContains("done"));
        confirmButton.click();
        sleep(2000);

        // Click the Save button to add the item
        onView(withId(R.id.btnCreate)).perform(click());
        sleep(5000);

        // Now verify the item was added
        onData(withItemContent(uniqueDescription))
                .inAdapterView(withId(R.id.inventory_list_view))
                .check(matches(isDisplayed()));

        // Call cleanUp specifically for EditItem test
        cleanUp(true);

        // Reset the flag
        isEditTest = false;
    }

    @Test
    public void testScanners() throws InterruptedException {
        onView(withId(R.id.btnScanDescription)).perform(click());
        onView(withId(R.id.surfaceView)).check(matches(isDisplayed()));
        onView(withText("Cancel")).perform(click());

        onView(withId(R.id.btnScanSerial)).perform(click());
        onView(withId(R.id.surfaceView)).check(matches(isDisplayed()));
        onView(withText("Cancel")).perform(click());
    }

    /**
     * Cleans up after each test.
     */
//    @After
    public void cleanUp(Boolean isFromEditTest) throws InterruptedException {

        if (!isFromEditTest || (isFromEditTest && isEditTest)) {
            sleep(10000);
            onData(withItemContent(uniqueDescription))
                    .inAdapterView(withId(R.id.inventory_list_view))
                    .onChildView(withId(R.id.checkItem))
                    .perform(click());

            sleep(1500);

            // Click the delete button to delete the item
            onView(withId(R.id.delete_button)).perform(click());
        }
    }

    // filtering, date, keyword, model, make tests in progress

    /**
     * Custom matcher method to find an item in the ListView with the given content
     */
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

    /**
     * Custom action to set the date in a DatePicker
     */
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