package com.example.softwaresolutionssquad.views;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserViewModel extends ViewModel {
    private String username = "";
    private String name = "";

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public void setUsername(String user) {
        this.username = user;
    }

    public void setName(String name) {
        this.name = name;
    }
}