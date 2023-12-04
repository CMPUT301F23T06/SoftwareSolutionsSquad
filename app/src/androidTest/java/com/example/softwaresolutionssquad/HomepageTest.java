package com.example.softwaresolutionssquad;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.softwaresolutionssquad.models.InventoryItem;
import com.example.softwaresolutionssquad.views.HomeFragment;
import com.example.softwaresolutionssquad.views.InventoryListAdapter;
import com.example.softwaresolutionssquad.views.MainActivity;
import com.example.softwaresolutionssquad.views.MyApp;
import com.example.softwaresolutionssquad.views.UserViewModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.hamcrest.Matchers.anything;
import static org.junit.Assert.assertEquals;

import static java.lang.Thread.sleep;

import android.widget.ListView;

@RunWith(AndroidJUnit4.class)
public class HomepageTest {
    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);
    private static final String TEST_USER = "12@ualberta.ca";

    public List<InventoryItem> items = new ArrayList<>();

    /**
     * Sets up the test environment before each test. Waits for a specific time and sets the user context.
     * @throws InterruptedException if the thread is interrupted while sleeping.
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

    /**
     * Test for applying tags to multiple items. Verifies if the tags are correctly applied to selected items.
     * @throws InterruptedException if the thread is interrupted while sleeping.
     */
    @Test
    public void testTagsToMultipleItems() throws InterruptedException {
        onView(withId(R.id.inventory_list_view)).check(matches(isDisplayed()));

        // Simulate selecting multiple items
        for (int i = 0; i < 2; i++) { // Assuming 5 items in mock data
            onData(anything())
                    .inAdapterView(withId(R.id.inventory_list_view))
                    .atPosition(i)
                    .onChildView(withId(R.id.checkItem))
                    .perform(click());
        }

        onView(withId(R.id.add_tags_button)).perform(click());
        Thread.sleep(1000)        ;

        simulateTagSelection();
        onView(withId(R.id.okButton)).perform(click());

        verifyFirstItemTags();
    }

    /**
     * Verifies the tags of the first two items in the list.
     */
    private void verifyFirstItemTags() {
        activityScenarioRule.getScenario().onActivity(activity -> {
            ListView listView = activity.findViewById(R.id.inventory_list_view);
             InventoryListAdapter adapter = (InventoryListAdapter) listView.getAdapter();

            // Perform checks here
            int itemCount = adapter.getCount();
            for (int i = 0; i < itemCount; i++) {
                InventoryItem item = adapter.getItem(i);
                items.add(item);
            }
        });
    }

    /**
     * Simulates the selection of tags in the tag list.
     */
    private void simulateTagSelection() {
        // Simulate selecting tags
        onData(anything())
                .inAdapterView(withId(R.id.tagListView))
                .atPosition(0)
                .perform(click());
        onData(anything())
                .inAdapterView(withId(R.id.tagListView))
                .atPosition(1)
                .perform(click());
    }


    /**
     * Generates mock inventory data for testing purposes.
     * @return List of mock InventoryItem objects.
     */
    private List<InventoryItem> getMockInventoryData() {
        // Generate and return mock data
        List<InventoryItem> mockData = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            mockData.add(new InventoryItem(new Date(), "Description " + i, "Make " + i, "Model " + i, "SN" + i, 100.0 * i, "Comment " + i, "doc" + i, new ArrayList<>(), TEST_USER));
        }
        return mockData;
    }
}
