package com.amg.bakingapptest.contentprovider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.amg.bakingapptest.contentprovider.RecipeContract.RECIPE_NAME;
import static com.amg.bakingapptest.contentprovider.RecipeContract.RECIPE_JSON;
import static com.amg.bakingapptest.contentprovider.RecipeContract.TABLE_NAME;

public class RecipeDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "recipe.db";
    private static final int DATABASE_VERSION = 1;

    public RecipeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                RECIPE_NAME + " TEXT NOT NULL PRIMARY KEY, " +
                RECIPE_JSON + " TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
