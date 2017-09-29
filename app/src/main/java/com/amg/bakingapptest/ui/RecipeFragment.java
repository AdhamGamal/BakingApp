package com.amg.bakingapptest.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amg.bakingapptest.R;
import com.amg.bakingapptest.adapter.RecipeAdapter;
import com.amg.bakingapptest.module.Recipe;

import org.json.JSONArray;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class RecipeFragment extends Fragment {

    RecyclerView recyclerView;
    private static ArrayList<Recipe> recipes = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_reciept_list, container, false);
        if (MainActivity.isTablet) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        if (recipes.size() > 0) {
            recyclerView.setAdapter(new RecipeAdapter(recipes, getContext()));
        } else {
            new FetchRecipes().execute();
        }
        return recyclerView;
    }

    class FetchRecipes extends AsyncTask<Void, Void, ArrayList<Recipe>> {

        @Override
        protected ArrayList<Recipe> doInBackground(Void... params) {
            recipes = new ArrayList<>();
            try {
                JSONArray recieptArray = new JSONArray(new OkHttpClient().
                        newCall(new Request.Builder().url("https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json").build()).
                        execute().body().string().toString());
                for (int i = 0; i < recieptArray.length(); i++) {
                    recipes.add(new Recipe(recieptArray.getJSONObject(i)));
                }
            } catch (Exception e) {
            }
            return recipes;
        }

        @Override
        protected void onPostExecute(ArrayList<Recipe> recipes) {
            recyclerView.setAdapter(new RecipeAdapter(recipes, getContext()));
        }
    }
}
