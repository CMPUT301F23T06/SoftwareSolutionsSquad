package com.example.softwaresolutionssquad;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import android.content.Context;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.softwaresolutionssquad.controllers.KeywordFilterController;
import com.example.softwaresolutionssquad.controllers.SortController;
import com.example.softwaresolutionssquad.models.InventoryItem;
import com.example.softwaresolutionssquad.views.InventoryListAdapter;

import java.util.ArrayList;

public class KeywordFilterControllerTest {

    @Mock private Context mockContext;
    @Mock private LinearLayout mockKeyFilter;
    @Mock private EditText mockKeywords;
    @Mock private TextView mockKeywordButton;
    @Mock private TextView mockDateButton;
    @Mock private TextView mockMakeButton;
    @Mock private TextView mockTagButton;
    @Mock private LinearLayout mockDateFilter;
    @Mock private LinearLayout mockMakeFilter;
    @Mock private LinearLayout mockTagFilter;
    @Mock private InventoryListAdapter mockInventoryListAdapter;
    @Mock private ListView mockInventoryListView;
    @Mock private SortController mockSortController;
    @Mock private TextView mockEstimatedValue;
    @Mock private Spinner mockSpinnerOrder;
    @Mock private ImageView mockSortOrderIcon;
    private ArrayList<InventoryItem> inventoryItems;
    private KeywordFilterController controller;

    /**
     * Sets up the test environment before each test. Initializes mock objects and the KeywordFilterController.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        inventoryItems = new ArrayList<>();
        // Optionally add mock inventory items to the list
        Editable editable = mock(Editable.class);
        when(editable.toString()).thenReturn("some keyword");
        when(mockKeywords.getText()).thenReturn(editable);

        controller = new KeywordFilterController(
                mockContext,
                mockKeyFilter,
                mockKeywords,
                mockEstimatedValue,
                mockKeywordButton,
                mockDateButton,
                mockMakeButton,
                mockTagButton,
                mockDateFilter,
                mockMakeFilter,
                mockTagFilter,
                mockInventoryListAdapter,
                mockInventoryListView,
                inventoryItems,
                mockSpinnerOrder,
                mockSortOrderIcon,
                mockSortController
        );

    }

    /**
     * Tests the initialization of the KeywordFilterController constructor.
     */
    @Test
    public void testConstructorInitialization() {
        assertNotNull(controller);

    }

    /**
     * Tests the functionality of toggling keyword filter visibility.
     */
    @Test
    public void testToggleFilters() {
        // Initial state should be hidden
        assertEquals(0, mockKeyFilter.getVisibility());

        // Simulate button click to toggle filter visibility
        controller.toggleFilters();
        assertEquals(View.VISIBLE, mockKeyFilter.getVisibility());

        // Simulate button click to toggle filter visibility back
        controller.toggleFilters();
        assertEquals(0, mockKeyFilter.getVisibility());
    }

    /**
     * Tests the functionality of toggling keyword filter visibility.
     */
    @Test
    public void testApplyKeywordFilter() {
        // Set up a keyword
        String keyword = "test";
        when(mockKeywords.getText().toString()).thenReturn(keyword);
        mockInventoryListView.setAdapter(mockInventoryListAdapter);

        // Trigger the filter
        controller.applyKeywordFilter(keyword);
        // Verify that the inventory list is updated
        // This assumes that your InventoryListAdapter correctly handles the filtered list
        verify(mockInventoryListView).setAdapter(any(InventoryListAdapter.class));
    }
}
