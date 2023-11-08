package com.example.softwaresolutionssquad;

import java.io.Serializable;
import java.util.Date;

/**
 * The InventoryItem class represents an item in an inventory,
 * including details such as purchase date, description, make,
 * model, serial number, estimated value, and a comment about the item.
 */
public class InventoryItem implements Serializable {
    // Properties of the inventory item
    private Date purchaseDate;   // The date on which the item was purchased
    private String description;  // A brief description of the item
    private String make;         // The make of the item
    private String model;        // The model of the item
    private String serialNumber; // The serial number for the item
    private double estimatedValue; // The estimated value of the item
    private String comment;      // A comment about the item
    private String docId;
    private boolean isSelected = false; // Keep track of whether the item is selected with the checkbox or not
    /**
     * Constructs an InventoryItem with all its details initialized.
     *
     * @param purchaseDate    The date the item was purchased
     * @param description     The item's description
     * @param make            The make of the item
     * @param model           The model of the item
     * @param serialNumber    The serial number of the item
     * @param estimatedValue  The estimated value of the item
     * @param comment         A comment about the item
     */
    public InventoryItem(Date purchaseDate, String description, String make, String model, String serialNumber, double estimatedValue, String comment, String docId) {
        this.purchaseDate = purchaseDate;
        this.description = description;
        this.make = make;
        this.model = model;
        this.serialNumber = serialNumber;
        this.estimatedValue = estimatedValue;
        this.comment = comment;
        this.docId = docId;
    }

    public InventoryItem() {
    }

    public InventoryItem() { };
    // Accessor (getter) and mutator (setter) methods for each property

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public double getEstimatedValue() {
        return estimatedValue;
    }
    public void setEstimatedValue(double estimatedValue) {
        this.estimatedValue = estimatedValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean getSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getDocId() { return docId; }

    public void setDocId(String docId) { this.docId = docId; }

    /**
     * Returns a string representation of the InventoryItem,
     * including all its properties.
     *
     * @return A string representation of the InventoryItem
     */
    @Override
    public String toString() {
        return "InventoryItem{" +
                "purchaseDate=" + purchaseDate +
                ", documentId='" + docId + '\'' +
                ", description='" + description + '\'' +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", estimatedValue=" + estimatedValue +
                ", comment='" + comment + '\'' +
                '}';
    }
}
