package com.example.softwaresolutionssquad;

import static org.junit.Assert.*;

import com.example.softwaresolutionssquad.models.InventoryItem;

import org.junit.Before;
import org.junit.Test;
import java.util.Date;

public class InventoryItemTest {

    private InventoryItem item;
    private Date purchaseDate;
    private final String description = "Laptop";
    private final String make = "Dell";
    private final String model = "XPS";
    private final String serialNumber = "12345XYZ";
    private final double estimatedValue = 1200.00;
    private final String comment = "Office use";
    private final String docId = "DOC123456";
    private final String imageUrl = "content://com.google.android.apps.photos.contentprovider/-1/1/content%3A%2F%2Fmedia%2Fexternal%2Fimages%2Fmedia%2F1000000093/ORIGINAL/NONE/image%2Fjpeg/564331100";
    private final boolean isSelected = true;

    @Before
    public void setUp() {
        purchaseDate = new Date();
        item = new InventoryItem(purchaseDate, description, make, model, serialNumber, estimatedValue, comment, docId, imageUrl);
        item.setSelected(isSelected);
    }

    @Test
    public void testGetPurchaseDate() {
        assertEquals(purchaseDate, item.getPurchaseDate());
    }

    @Test
    public void testSetPurchaseDate() {
        Date newDate = new Date();
        item.setPurchaseDate(newDate);
        assertEquals(newDate, item.getPurchaseDate());
    }

    @Test
    public void testGetDescription() {
        assertEquals(description, item.getDescription());
    }

    @Test
    public void testSetDescription() {
        String newDescription = "Gaming Laptop";
        item.setDescription(newDescription);
        assertEquals(newDescription, item.getDescription());
    }

    @Test
    public void testGetMake() {
        assertEquals(make, item.getMake());
    }

    @Test
    public void testSetMake() {
        String newMake = "HP";
        item.setMake(newMake);
        assertEquals(newMake, item.getMake());
    }

    @Test
    public void testGetModel() {
        assertEquals(model, item.getModel());
    }

    @Test
    public void testSetModel() {
        String newModel = "Envy";
        item.setModel(newModel);
        assertEquals(newModel, item.getModel());
    }

    @Test
    public void testGetSerialNumber() {
        assertEquals(serialNumber, item.getSerialNumber());
    }

    @Test
    public void testSetSerialNumber() {
        String newSerialNumber = "98765ZYX";
        item.setSerialNumber(newSerialNumber);
        assertEquals(newSerialNumber, item.getSerialNumber());
    }

    @Test
    public void testGetEstimatedValue() {
        assertEquals(estimatedValue, item.getEstimatedValue(), 0.0);
    }

    @Test
    public void testSetEstimatedValue() {
        double newEstimatedValue = 1500.00;
        item.setEstimatedValue(newEstimatedValue);
        assertEquals(newEstimatedValue, item.getEstimatedValue(), 0.0);
    }

    @Test
    public void testGetComment() {
        assertEquals(comment, item.getComment());
    }

    @Test
    public void testSetComment() {
        String newComment = "Personal use";
        item.setComment(newComment);
        assertEquals(newComment, item.getComment());
    }

    @Test
    public void testGetDocId() {
        assertEquals(docId, item.getDocId());
    }

    @Test
    public void testSetDocId() {
        String newDocId = "DOC654321";
        item.setDocId(newDocId);
        assertEquals(newDocId, item.getDocId());
    }

    @Test
    public void testIsSelected() {
        assertEquals(isSelected, item.getSelected());
    }

    @Test
    public void testSetSelected() {
        item.setSelected(false);
        assertFalse(item.getSelected());
    }

    @Test
    public void testToString() {
        String expectedString = "InventoryItem{" +
                "purchaseDate=" + purchaseDate +
                ", docId='" + docId + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", description='" + description + '\'' +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", estimatedValue=" + estimatedValue +
                ", comment='" + comment + '\'' +
                ", tags=''" +
                ", isSelected=" + isSelected +
                '}';
        assertEquals(expectedString, item.toString());
    }
}
