package com.example.softwaresolutionssquad;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.example.softwaresolutionssquad.views.AddItemFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;



public class MainActivity extends AppCompatActivity {

    private Fragment HomeFragment;
    private Fragment ItemFragment;
    private Fragment TagFragment;
    private Fragment ProfileFragment;

    // The onCreate method is called when the Activity is starting
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // Set the user interface layout for this Activity

        HomeFragment = new HomeFragment();
        ItemFragment = new AddItemFragment();
        ProfileFragment = new ProfileFragment();
        TagFragment = new TagFragment();

        setFragment(HomeFragment);

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation_bar);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.navigation_home) {
            // Handle Home menu item click by setting the HomeFragment
            setFragment(HomeFragment);
            return true;
        } else if (item.getItemId() == R.id.navigation_tag) {
            // Handle Tag menu item click by setting the TagFragment
            setFragment(TagFragment);
            return true;
        } else if (item.getItemId() == R.id.navigation_add) {
            // Handle Add Item menu item click by setting the AddItemFragment
            setFragment(ItemFragment);
            return true;
        } else if (item.getItemId() == R.id.navigation_profile) {
            // Handle Profile menu item click by setting the ProfileFragment
            setFragment(ProfileFragment);
            return true;
        } else {
            // Handle other cases or pass to the superclass for default handling
            return false;
        }
            }
        });

    }

    private void setFragment(Fragment fragment) {
        FrameLayout fragmentContainer = findViewById(R.id.frag_container);
        fragmentContainer.setVisibility(View.VISIBLE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frag_container, fragment);
        transaction.commit();
    }
}