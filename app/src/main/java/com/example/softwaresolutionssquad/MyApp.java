package com.example.softwaresolutionssquad;

import android.app.Application;

public class MyApp extends Application {
    private UserViewModel userViewModel;

    @Override
    public void onCreate() {
        super.onCreate();
        userViewModel = new UserViewModel();
    }

    public UserViewModel getUserViewModel() {
        return userViewModel;
    }
}
