package com.example.softwaresolutionssquad;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.softwaresolutionssquad.controllers.DateFilterController;
import com.example.softwaresolutionssquad.models.InventoryItem;
import com.example.softwaresolutionssquad.views.InventoryListAdapter;

import java.time.LocalDate;
import java.util.ArrayList;

public class DateFilterControllerTest {

    @Mock private Context mockContext;
    @Mock private LinearLayout mockDateFilterLayout;
    @Mock private EditText mockStartDateEditText;
    @Mock private EditText mockEndDateEditText;
    @Mock private TextView mockDateFilterButton;
    @Mock private TextView mockKeywordFilterButton;
    @Mock private TextView mockMakeFilterButton;
    @Mock private TextView mockTagFilterButton;
    @Mock private LinearLayout mockKeywordFilterLayout;
    @Mock private LinearLayout mockMakeFilterLayout;
    @Mock private LinearLayout mockTagFilterLayout;
    @Mock private InventoryListAdapter mockInventoryListAdapter;
    @Mock private ListView mockInventoryListView;
    private ArrayList<InventoryItem> inventoryItems;
    private DateFilterController controller;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        inventoryItems = new ArrayList<>();
        // Optionally add mock inventory items to the list

        controller = new DateFilterController(
                mockContext,
                mockDateFilterLayout,
                mockStartDateEditText,
                mockEndDateEditText,
                mockDateFilterButton,
                mockKeywordFilterButton,
                mockMakeFilterButton,
                mockTagFilterButton,
                mockKeywordFilterLayout,
                mockMakeFilterLayout,
                mockTagFilterLayout,
                mockInventoryListAdapter,
                mockInventoryListView,
                inventoryItems
        );
    }

    @Test
    public void testConstructorInitialization() {
        assertNotNull(controller);
        // Add more assertions as needed to verify initial state
    }

    @Test
    public void testToggleFilters() {
        // Initial state should be hidden
        assertEquals(0, mockDateFilterLayout.getVisibility());

        // Simulate button click to toggle filter visibility
        controller.toggleFilters();
        assertEquals(View.VISIBLE, mockDateFilterLayout.getVisibility());

        // Simulate button click to toggle filter visibility back
        controller.toggleFilters();
        assertEquals(0, mockDateFilterLayout.getVisibility());
    }

    @Test
    public void testDefaultDates() {
        ArgumentCaptor<String> startDateCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> endDateCaptor = ArgumentCaptor.forClass(String.class);

        // Call the method that should set default dates
        controller.setDefaultDates();

        // Capture the dates set on the EditText fields
        verify(mockStartDateEditText).setText(startDateCaptor.capture());
        verify(mockEndDateEditText).setText(endDateCaptor.capture());

        assertEquals("1900-01-01", startDateCaptor.getValue());
        assertEquals(LocalDate.now().toString(), endDateCaptor.getValue());
    }

    // More test methods can be added as needed

}
