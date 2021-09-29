package com.group_7.mhd.mohammed.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.andremion.counterfab.CounterFab;
import com.group_7.mhd.mohammed.Interface.ItemClickListener;
import com.group_7.mhd.mohammed.R;

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView food_name,food_price;
    public ImageView food_image;
    public CounterFab quick_cart;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public FoodViewHolder(View itemView) {
        super(itemView);

        food_name = itemView.findViewById(R.id.food_name_fi);
        food_image = itemView.findViewById(R.id.food_image_fi);
        food_price = itemView.findViewById(R.id.food_price_fi);
        quick_cart = itemView.findViewById(R.id.btn_quick_cart);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}
