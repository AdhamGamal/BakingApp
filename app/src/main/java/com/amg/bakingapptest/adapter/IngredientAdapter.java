package com.amg.bakingapptest.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amg.bakingapptest.R;
import com.amg.bakingapptest.module.Ingredient;

import java.util.List;
import java.util.Random;


public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {

    private final List<Ingredient> mValues;
    private final Context context;

    public IngredientAdapter(List<Ingredient> items, Context context) {
        mValues = items;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ingredient_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private final TextView mNameView;
        private final TextView mDetailsView;
        private final TextView mServingsView;
        public Ingredient mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = (TextView) view.findViewById(R.id.name);
            mDetailsView = (TextView) view.findViewById(R.id.details);
            mServingsView = (TextView) view.findViewById(R.id.servings);
        }

        public void onBind(int position) {
            mItem = mValues.get(position);
            mView.setBackgroundColor(getColor());
            mNameView.setText(mItem.getIngredient());
            mDetailsView.setText(mItem.getMeasure()+" : ");
            mServingsView.setText(mItem.getQuantity() + "");
        }
        public int getColor() {
            float[] hsv = new float[3];
            Random rnd = new Random();
            Color.colorToHSV((Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)) + Color.BLACK), hsv);
            hsv[2] *= 0.6f;
            return Color.HSVToColor(hsv);
        }
    }

}
