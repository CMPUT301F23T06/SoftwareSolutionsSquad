package com.example.softwaresolutionssquad.views;

import androidx.lifecycle.ViewModel;

/**
 * View Model to access the logged in username from any other class in the app
 */
public class UserViewModel extends ViewModel {
    private String username = "";

    /**
     * Getter for username
     */
    public String getUsername() {
        return username;
    }


    /**
     * Setter for username
     */
    public void setUsername(String user) {
        this.username = user;
    }

}