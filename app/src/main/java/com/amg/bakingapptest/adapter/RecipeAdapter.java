package com.amg.bakingapptest.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amg.bakingapptest.R;
import com.amg.bakingapptest.module.Recipe;
import com.amg.bakingapptest.ui.StepsListActivity;
import com.ldoublem.thumbUplib.ThumbUpView;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.amg.bakingapptest.contentprovider.RecipeContract.BASE_URI;
import static com.amg.bakingapptest.contentprovider.RecipeContract.RECIPE_COLUMNS;
import static com.amg.bakingapptest.contentprovider.RecipeContract.RECIPE_JSON;
import static com.amg.bakingapptest.contentprovider.RecipeContract.RECIPE_NAME;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private final List<Recipe> mValues;
    private final Context context;

    public RecipeAdapter(List<Recipe> items, Context context) {
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
        private Recipe mItem;
        private final View mView;
        private final ImageView mImageView;
        private final TextView mNameView;
        private final TextView mDetailsView;
        private final TextView mServingsView;
        private final ThumbUpView mThumbUpView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.image);
            mNameView = (TextView) view.findViewById(R.id.name);
            mDetailsView = (TextView) view.findViewById(R.id.details);
            mServingsView = (TextView) view.findViewById(R.id.servings);
            mThumbUpView = (ThumbUpView) view.findViewById(R.id.like);
        }

        public void onBind(int position) {
            mItem = mValues.get(position);
            if (mItem.getImage().isEmpty()) {
                mImageView.setImageDrawable(context.getDrawable(R.drawable.ic_insert_image));
            } else {
                Picasso.with(context).load(mItem.getImage()).error(context.getDrawable(R.drawable.ic_insert_image)).into(mImageView);
            }
            mNameView.setText(mItem.getName());
            mDetailsView.setText("Servings : ");
            mServingsView.setText(mItem.getServings());

            mThumbUpView.setVisibility(View.VISIBLE);
            if (isFavorite(mItem.getName())) {
                mThumbUpView.setLike();
            }
            mThumbUpView.setOnThumbUp(new ThumbUpView.OnThumbUp() {
                @Override
                public void like(boolean like) {
                    if (like) {
                        ContentValues values = new ContentValues();
                        values.put(RECIPE_NAME, mItem.getName());
                        values.put(RECIPE_JSON, mItem.getRecipeJson().toString());
                        context.getContentResolver().insert(BASE_URI, values);
                    } else {
                        context.getContentResolver().delete(
                                BASE_URI,
                                RECIPE_NAME + " = ?",
                                new String[]{mItem.getName()}
                        );
                    }
                }
            });

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, StepsListActivity.class);
                    intent.putExtra("recipe", mItem);
                    context.startActivity(intent);
                }
            });
        }

        public boolean isFavorite(String name) {
            Cursor cursor = context.getContentResolver().query(BASE_URI, RECIPE_COLUMNS, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    if (cursor.getString(0).equals(name)) {
                        cursor.close();
                        return true;
                    }
                } while (cursor.moveToNext());
                cursor.close();
            }
            return false;
        }
    }
}
