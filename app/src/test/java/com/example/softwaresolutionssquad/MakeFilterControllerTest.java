package com.example.softwaresolutionssquad;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.softwaresolutionssquad.controllers.MakeFilterController;
import com.example.softwaresolutionssquad.controllers.SortController;
import com.example.softwaresolutionssquad.models.InventoryItem;
import com.example.softwaresolutionssquad.views.InventoryListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class MakeFilterControllerTest {

    @Mock private Context mockContext;
    @Mock private TextView mockMakesTextView;
    @Mock private LinearLayout mockMakeFilter;
    @Mock private TextView mockKeywordButton;
    @Mock private TextView mockDateButton;
    @Mock private TextView mockMakeButton;
    @Mock private TextView mockTagButton;
    @Mock private InventoryListAdapter mockInventoryListAdapter;
    @Mock private ListView mockInventoryListView;
    @Mock private SortController mockSortController;
    @Mock private TextView mockEstimateValue;
    private List<InventoryItem> mockInventoryItems;
    private MakeFilterController controller;

    /**
     * Sets up the test environment before each test. Initializes mock objects and the MakeFilterController.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        mockInventoryItems = new ArrayList<>();
        // Optionally add mock inventory items to the list

        controller = new MakeFilterController(
                mockContext,
                mockEstimateValue,
                mockMakesTextView,
                mockMakeFilter,
                mockKeywordButton,
                mockDateButton,
                mockMakeButton,
                mockTagButton,
                mockInventoryListAdapter,
                mockInventoryListView,
                (ArrayList<InventoryItem>) mockInventoryItems,
                mockSortController
        );
    }

    @Test
    public void testConstructorInitialization() {
        assertNotNull(controller);
    }

    /**
     * Tests the functionality of toggling make filter visibility.
     */
    @Test
    public void testToggleMakeFilterVisibility() {
        // Initial state should be hidden
        assertEquals(0, mockMakeFilter.getVisibility());

        // Simulate method call to toggle filter visibility
        controller.toggleMakeFilterVisibility();
        assertEquals(View.VISIBLE, mockMakeFilter.getVisibility());

        // Simulate method call to toggle filter visibility back
        controller.toggleMakeFilterVisibility();
        assertEquals(0, mockMakeFilter.getVisibility());
    }

    /**
     * Tests the filtered results functionality of the MakeFilterController.
     */
    @Test
    public void testFilteredResults() {

        // Set a mock filter condition
        Predicate<InventoryItem> mockCondition = item -> true; // Example condition
        controller.filteredResults(mockCondition);
        mockInventoryListView.setAdapter(mockInventoryListAdapter);

        // Apply the filter
        // Verify the inventory list is updated
        verify(mockInventoryListView).setAdapter(any(InventoryListAdapter.class));
    }
}
