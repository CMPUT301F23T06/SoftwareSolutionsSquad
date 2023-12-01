package com.example.softwaresolutionssquad.views;

import androidx.lifecycle.ViewModel;

public class UserViewModel extends ViewModel {
    private String username = "";


    public String getUsername() {
        return username;
    }



    public void setUsername(String user) {
        this.username = user;
    }

}