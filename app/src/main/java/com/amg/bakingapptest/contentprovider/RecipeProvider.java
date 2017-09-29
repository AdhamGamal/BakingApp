package com.amg.bakingapptest.contentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import static com.amg.bakingapptest.contentprovider.RecipeContract.BASE_URI;
import static com.amg.bakingapptest.contentprovider.RecipeContract.TABLE_NAME;

public class RecipeProvider extends ContentProvider {
    private RecipeDbHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new RecipeDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor cursor = dbHelper.getReadableDatabase().query(
                TABLE_NAME, projection, selection, selectionArgs,
                null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return "";
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        long id = dbHelper.getWritableDatabase().insert(TABLE_NAME, null, values);
        if (id <= 0) {
            throw new android.database.SQLException("Failed to insert row into " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return BASE_URI;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int rowsDeleted = dbHelper.getWritableDatabase().delete(
                TABLE_NAME, selection, selectionArgs);

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int rowsUpdated = dbHelper.getWritableDatabase().update(TABLE_NAME, values, selection,
                selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
