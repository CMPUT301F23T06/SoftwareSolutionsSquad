package com.example.softwaresolutionssquad.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents an item in an inventory with properties such as
 * purchase date, description, make, model, serial number, estimated value, and a comment.
 */
public class InventoryItem implements Serializable {

    private Date purchaseDate;
    private String description;
    private String make;
    private String model;
    private String serialNumber;
    private double estimatedValue;
    private String comment;
    private String docId;
    private boolean isSelected;

    /**
     * Constructor for InventoryItem with initialization of all fields.
     *
     * @param purchaseDate   the date the item was purchased
     * @param description    the description of the item
     * @param make           the make of the item
     * @param model          the model of the item
     * @param serialNumber   the serial number of the item
     * @param estimatedValue the estimated value of the item
     * @param comment        additional comments about the item
     * @param docId          document identifier for the item
     */
    public InventoryItem(Date purchaseDate, String description, String make, String model,
                         String serialNumber, double estimatedValue, String comment, String docId) {
        this.purchaseDate = purchaseDate;
        this.description = description;
        this.make = make;
        this.model = model;
        this.serialNumber = serialNumber;
        this.estimatedValue = estimatedValue;
        this.comment = comment;
        this.docId = docId;
    }

    /**
     * Default constructor for InventoryItem.
     */
    public InventoryItem() {
        // Default constructor
    }

    // Accessors and Mutators for each property
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

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    /**
     * Returns a string representation of the InventoryItem.
     *
     * @return a string representation of the InventoryItem's properties
     */
    @Override
    public String toString() {
        return "InventoryItem{" +
                "purchaseDate=" + purchaseDate +
                ", docId='" + docId + '\'' +
                ", description='" + description + '\'' +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", estimatedValue=" + estimatedValue +
                ", comment='" + comment + '\'' +
                ", isSelected=" + isSelected +
                '}';
    }
}