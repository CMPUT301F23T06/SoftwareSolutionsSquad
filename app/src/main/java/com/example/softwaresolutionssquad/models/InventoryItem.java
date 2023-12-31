package com.example.softwaresolutionssquad.models;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
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
    private final ArrayList<String> tags;
    private String docId;
    private ArrayList<String> imageUrl;
    // only used to include username when adding item to database
    private String username;
    private boolean isSelected;

    /**
     * Constructor for InventoryItem with initialization of all fields except tags.
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
    public InventoryItem(Date purchaseDate, String description, String make, String model, String serialNumber,
                         double estimatedValue, String comment, String docId, ArrayList<String> imageUrl, String username) {
        this.purchaseDate = purchaseDate;
        this.description = description;
        this.make = make;
        this.model = model;
        this.serialNumber = serialNumber;
        this.estimatedValue = estimatedValue;
        this.comment = comment;
        this.tags = new ArrayList<>();
        this.docId = docId;
        this.imageUrl = imageUrl;
        this.username = username;
    }

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
     * @param tags           array list of tags on the item
     * @param docId          document identifier for the item
     */
    public InventoryItem(Date purchaseDate, String description, String make, String model, String serialNumber, double estimatedValue,
                         String comment, ArrayList<String> tags, String docId, ArrayList<String> imageUrl, String username) {
        this.purchaseDate = purchaseDate;
        this.description = description;
        this.make = make;
        this.model = model;
        this.serialNumber = serialNumber;
        this.estimatedValue = estimatedValue;
        this.comment = comment;
        this.tags = tags;
        this.docId = docId;
        this.imageUrl = imageUrl;
        this.username = username;
    }

    /**
     * Default constructor for InventoryItem.
     */
    public InventoryItem() {
        // Default constructor
        this.tags = new ArrayList<>();
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

    public ArrayList<String> getTags() { return tags; }

    /**
     * Sorts the tags of the item
     */
    public ArrayList<String> getSortedTags() {
        ArrayList<String> sortedTags = tags;
        Collections.sort(sortedTags);
        return sortedTags;
    }


    /**
     * Adds a tag to the items tag list
     */
    public void addTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }


    public boolean getSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public void setUsername(String username) { this.username = username; }

    public String getUsername() { return username; }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public ArrayList<String> getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(ArrayList<String> imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Returns a string representation of the InventoryItem.
     *
     * @return a string representation of the InventoryItem's properties
     */
    @NonNull
    @Override
    public String toString() {
        return "InventoryItem{" +
                "purchaseDate=" + purchaseDate +
                ", docId='" + docId + '\'' +
                ", imageUrl='" + String.join(",", imageUrl) + '\'' +
                ", description='" + description + '\'' +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", estimatedValue=" + estimatedValue +
                ", comment='" + comment + '\'' +
                ", tags='" + String.join("|", tags)  +'\'' +
                ", isSelected=" + isSelected +
                '}';
    }
}
