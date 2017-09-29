package com.amg.bakingapptest.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amg.bakingapptest.R;
import com.amg.bakingapptest.adapter.IngredientAdapter;
import com.amg.bakingapptest.module.Ingredient;
import com.amg.bakingapptest.module.Recipe;
import com.amg.bakingapptest.module.Step;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class StepsListActivity extends AppCompatActivity {

    private static Recipe recipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps_list);

        setTitle(getTitle());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().hasExtra("recipe")) {
            recipe = getIntent().getParcelableExtra("recipe");
        }
        setTitle(recipe.getName());
        ArrayList<Step> steps = recipe.getSteps();
        ArrayList<Ingredient> ingredients = recipe.getIngredients();

        RecyclerView ingredientsRecyclerView = (RecyclerView) findViewById(R.id.ingredientslist);
        ingredientsRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL));
        ingredientsRecyclerView.setAdapter(new IngredientAdapter(ingredients, getBaseContext()));


        RecyclerView stepsRecyclerView = (RecyclerView) findViewById(R.id.steps_list);
        stepsRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        stepsRecyclerView.setAdapter(new StepAdapter(steps, getBaseContext()));

        if (findViewById(R.id.steps_detail_container) != null) {
            MainActivity.isTablet = true;
            Bundle arguments = new Bundle();
            arguments.putParcelable("recipe", recipe);
            arguments.putInt("step_id", 0);
            StepsDetailFragment fragment = new StepsDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.steps_detail_container, fragment)
                    .commit();

        }
    }

    public class StepAdapter extends RecyclerView.Adapter<StepAdapter.ViewHolder> {

        private final List<Step> mValues;
        private final Context context;

        public StepAdapter(List<Step> items, Context context) {
            mValues = items;
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_layout, parent, false));
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.onBind(position);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private Step mItem;
            private final View mView;
            private final ImageView mImageView;
            private final TextView mNameView;
            private final TextView mDetailsView;
            private final TextView mServingsView;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.image);
                mNameView = (TextView) view.findViewById(R.id.name);
                mDetailsView = (TextView) view.findViewById(R.id.details);
                mServingsView = (TextView) view.findViewById(R.id.servings);
            }

            public void onBind(int position) {
                mItem = mValues.get(position);
                if (mItem.getThumbnailURL().isEmpty()) {
                    mImageView.setImageDrawable(context.getDrawable(R.drawable.ic_insert_image));
                } else {
                    Picasso.with(context).load(mItem.getThumbnailURL()).error(context.getDrawable(R.drawable.ic_insert_image)).into(mImageView);
                }
                mNameView.setText(mItem.getShortDescription());
                mDetailsView.setText("Step : ");
                mServingsView.setText(mItem.getId() + "");
                mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (MainActivity.isTablet) {
                            Bundle arguments = new Bundle();
                            arguments.putParcelable("recipe", recipe);
                            arguments.putInt("step_id", mItem.getId());
                            StepsDetailFragment fragment = new StepsDetailFragment();
                            fragment.setArguments(arguments);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.steps_detail_container, fragment)
                                    .commit();
                        } else {
                            Intent intent = new Intent(context, StepsDetailActivity.class);
                            intent.putExtra("recipe", recipe);
                            intent.putExtra("step_id", mItem.getId());
                            context.startActivity(intent);
                        }
                    }
                });
            }
        }
    }
}
