package com.amg.bakingapptest.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService.RemoteViewsFactory;

import com.amg.bakingapptest.R;
import com.amg.bakingapptest.module.Ingredient;
import com.amg.bakingapptest.module.Recipe;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import static com.amg.bakingapptest.contentprovider.RecipeContract.BASE_URI;
import static com.amg.bakingapptest.contentprovider.RecipeContract.RECIPE_COLUMNS;

public class WidgetRemoteFactory implements RemoteViewsFactory {
    private Context mContext;
    private ArrayList<Recipe> recipes;

    public WidgetRemoteFactory(Context applicationContext) {
        mContext = applicationContext;
    }


    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        recipes = getFavorites();
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public int getCount() {
        return recipes.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        Recipe mItem = recipes.get(position);
        ArrayList<Ingredient> ingredients = mItem.getIngredients();
        RemoteViews remoteView = new RemoteViews(mContext.getPackageName(), R.layout.app_widget_item);
        try {
            if (mItem.getImage().isEmpty()) {
                remoteView.setImageViewResource(R.id.image, R.drawable.ic_insert_image);
            } else {
                remoteView.setImageViewBitmap(R.id.image, BitmapFactory.decodeStream(new URL(mItem.getImage()).openConnection().getInputStream()));
            }
        } catch (IOException e) {
        }
        remoteView.setTextViewText(R.id.name, mItem.getName());
        remoteView.setTextViewText(R.id.details, "Servings : ");
        remoteView.setTextViewText(R.id.servings, mItem.getServings());
        for (int i = 0; i < ingredients.size(); i++) {
            RemoteViews subRemoteView = new RemoteViews(mContext.getPackageName(), R.layout.ingredient_layout);
            subRemoteView.setTextViewText(R.id.name, ingredients.get(i).getIngredient());
            subRemoteView.setTextColor(R.id.name, Color.BLACK);
            subRemoteView.setTextViewText(R.id.details, ingredients.get(i).getMeasure() + " : ");
            subRemoteView.setTextColor(R.id.details, Color.BLACK);
            subRemoteView.setTextViewText(R.id.servings, ingredients.get(i).getQuantity() + "");
            subRemoteView.setTextColor(R.id.servings, Color.BLACK);
            remoteView.addView(R.id.ingerdients, subRemoteView);
        }

        Intent intent = new Intent();
        intent.putExtra("recipe", mItem);
        intent.putExtra("step_id", position);
        remoteView.setOnClickFillInIntent(R.id.root, intent);
        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public ArrayList<Recipe> getFavorites() {
        Cursor cursor = mContext.getContentResolver().query(BASE_URI, RECIPE_COLUMNS, null, null, null);
        ArrayList<Recipe> favorites = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            try {
                Recipe movie = null;
                do {
                    movie = new Recipe(new JSONObject(cursor.getString(1)));
                    favorites.add(movie);
                } while (cursor.moveToNext());
            } catch (JSONException e) {
            }
            cursor.close();
        }
        return favorites;
    }
}
