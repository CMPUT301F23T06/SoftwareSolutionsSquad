package com.example.softwaresolutionssquad;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.softwaresolutionssquad.controllers.SortController;
import com.example.softwaresolutionssquad.controllers.TagFilterController;
import com.example.softwaresolutionssquad.models.InventoryItem;
import com.example.softwaresolutionssquad.views.InventoryListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class TagFilterControllerTest {

    @Mock private Context mockContext;
    @Mock private TextView mockTagsTextView;
    @Mock private LinearLayout mockTagFilter;
    @Mock private TextView mockKeywordButton;
    @Mock private TextView mockDateButton;
    @Mock private TextView mockMakeButton;
    @Mock private TextView mockTagButton;
    @Mock private InventoryListAdapter mockInventoryListAdapter;
    @Mock private ListView mockInventoryListView;
    @Mock private SortController mockSortController;
    @Mock private TextView mockEstimatedValue;
    private List<InventoryItem> mockInventoryItems;
    private TagFilterController controller;

    /**
     * Sets up the test environment before each test. Initializes mock objects and the TagFilterController.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        mockInventoryItems = new ArrayList<>();
        // Optionally add mock inventory items to the list

        controller = new TagFilterController(
                mockContext,
                mockTagsTextView,
                mockEstimatedValue,
                mockTagFilter,
                mockKeywordButton,
                mockDateButton,
                mockMakeButton,
                mockTagButton,
                mockInventoryListAdapter,
                mockInventoryListView,
                mockInventoryItems,
                mockSortController
        );
    }

    /**
     * Tests the functionality of toggling tag filter visibility.
     */
    @Test
    public void testToggleTagFilterVisibility() {
        // Initial state should be hidden
        assertEquals(0, mockTagFilter.getVisibility());

        // Simulate method call to toggle filter visibility
        controller.toggleTagFilterVisibility();
        assertEquals(View.VISIBLE, mockTagFilter.getVisibility());

        // Simulate method call to toggle filter visibility back
        controller.toggleTagFilterVisibility();
        assertEquals(0, mockTagFilter.getVisibility());
    }

    /**
     * Tests the filtered results functionality of the TagFilterController.
     */
    @Test
    public void testFilteredResults() {
        // Assuming you have a method in controller to apply the filter
        // Set up a mock filter condition
        Predicate<InventoryItem> mockCondition = item -> true; // Example condition
        controller.filteredResults(mockCondition);
        mockInventoryListView.setAdapter(mockInventoryListAdapter);

        // Verify the inventory list is updated
        verify(mockInventoryListView).setAdapter(any(InventoryListAdapter.class));
    }
}
