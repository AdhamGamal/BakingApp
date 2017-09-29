package com.amg.bakingapptest.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.amg.bakingapptest.R;

public class StepsDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            Intent intent = getIntent();
            if (intent.hasExtra("recipe")) {
                arguments.putParcelable("recipe", intent.getParcelableExtra("recipe"));
                arguments.putInt("step_id", intent.getIntExtra("step_id", 0));
            }
            StepsDetailFragment fragment = new StepsDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.steps_detail_container, fragment)
                    .commit();
        }
    }
}
