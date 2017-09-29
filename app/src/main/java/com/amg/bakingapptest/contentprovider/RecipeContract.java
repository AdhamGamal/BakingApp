package com.amg.bakingapptest.contentprovider;

import android.net.Uri;

public class RecipeContract {
    public static final String TABLE_NAME = "recipes";

    private static final String AUTHORITY = "com.amg.bakingapp";

    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);

    public static final String RECIPE_NAME = "recipe_name";
    public static final String RECIPE_JSON = "recipe_json";

    public static final String[] RECIPE_COLUMNS = {
            RECIPE_NAME,
            RECIPE_JSON,
    };
}
