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

        // Add assertions or verifications as needed
    }
    private void verifyFirstItemTags() {
        activityScenarioRule.getScenario().onActivity(activity -> {
            ListView listView = activity.findViewById(R.id.inventory_list_view);
             InventoryListAdapter adapter = (InventoryListAdapter) listView.getAdapter();
            InventoryItem item1 =  adapter.getItem(0);
            assertEquals(item1.getTags().get(0), "a");
            assertEquals(item1.getTags().get(1), "b");
            InventoryItem item2 =  adapter.getItem(1);
            assertEquals(item2.getTags().get(0), "a");
            assertEquals(item2.getTags().get(1), "b");

            // Perform checks here
            // Additional assertions to verify updates...
            int itemCount = adapter.getCount();
            for (int i = 0; i < itemCount; i++) {
                InventoryItem item = adapter.getItem(i);
                items.add(item);
            }
        });
    }
    private void simulateTagSelection() {
        // Simulate selecting tags
        // Adjust this section based on how your tags are displayed and interacted with
        // For example:
        onData(anything())
                .inAdapterView(withId(R.id.tagListView))
                .atPosition(0)
                .perform(click());
        onData(anything())
                .inAdapterView(withId(R.id.tagListView))
                .atPosition(1)
                .perform(click());
    }

    private List<InventoryItem> getMockInventoryData() {
        // Generate and return mock data
        List<InventoryItem> mockData = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            mockData.add(new InventoryItem(new Date(), "Description " + i, "Make " + i, "Model " + i, "SN" + i, 100.0 * i, "Comment " + i, "doc" + i, new ArrayList<>(), TEST_USER));
        }
        return mockData;
    }
}
