package com.example.softwaresolutionssquad.views;

import android.app.Application;

public class MyApp extends Application {
    private UserViewModel userViewModel;

    /**
     * Called when the application is starting. Initializes the application-level resources, including the UserViewModel.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        userViewModel = new UserViewModel();
    }

    /**
     * Provides access to the UserViewModel instance for managing user-related data.
     * @return The UserViewModel instance.
     */
    public UserViewModel getUserViewModel() {
        return userViewModel;
    }
}
