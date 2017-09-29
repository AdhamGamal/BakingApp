package com.amg.bakingapptest.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.amg.bakingapptest.R;


public class MainActivity extends AppCompatActivity {

    public static boolean isTablet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            RecipeFragment fragment = new RecipeFragment();
            if (findViewById(R.id.bakesGrid) != null) {
                isTablet = true;
                fragmentManager.beginTransaction()
                        .add(R.id.bakesGrid, fragment)
                        .commit();
            } else {
                fragmentManager.beginTransaction()
                        .add(R.id.bakesframe, fragment)
                        .commit();
            }
        }
    }
}