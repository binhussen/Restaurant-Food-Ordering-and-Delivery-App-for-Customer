package com.group_7.mhd.mohammed.ViewHolder;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.andremion.counterfab.CounterFab;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.group_7.mhd.mohammed.Cart;
import com.group_7.mhd.mohammed.Common.Common;
import com.group_7.mhd.mohammed.Database.Database;
import com.group_7.mhd.mohammed.Interface.ItemClickListener;
import com.group_7.mhd.mohammed.Model.Order;
import com.group_7.mhd.mohammed.R;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener{

    public TextView txt_cart_name, txt_price, txt_vate;
    public ElegantNumberButton btn_quantity;
    public ImageView cart_image, btn_remove;

    private ItemClickListener itemClickListener;

    public void setTxt_cart_name(TextView txt_cart_name) {
        this.txt_cart_name = txt_cart_name;
    }

    public CartViewHolder(View itemView) {
        super(itemView);
        txt_cart_name = itemView.findViewById(R.id.cart_item_name);
        txt_price = itemView.findViewById(R.id.cart_item_price);
        txt_vate = itemView.findViewById(R.id.cart_vate_price);
        btn_quantity = itemView.findViewById(R.id.btn_quantity);
        cart_image = itemView.findViewById(R.id.cart_image);
        btn_remove = itemView.findViewById(R.id.btn_delete);

        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCreateContextMenu(final ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select action");
        contextMenu.add(0,0,getAdapterPosition(), Common.DELETE);

        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contextMenu.add(0,0,getAdapterPosition(), Common.DELETE);
            }
        });
    }
}

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder>{

    private List<Order> listData = new ArrayList<>();
    //private Context context;
    private Cart cart;
    //Constructor


    public CartAdapter(List<Order> listData, /*Context context,*/ Cart cart) {
        this.listData = listData;
      //  this.context = context;
        this.cart = cart;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(cart);
        View itemView = inflater.inflate(R.layout.cart_layout, parent, false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CartViewHolder holder, final int position) {

        /*TextDrawable drawable = TextDrawable.builder().
                buildRound(""+listData.get(position).getQuantity(), Color.RED);
        holder.img_cart_count.setImageDrawable(drawable);*/

        Picasso.get(/*cart.getBaseContext()*/)
                .load(listData.get(position).getImage())
                .resize(90,90)
                .centerCrop()
                .into(holder.cart_image);

        holder.btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        holder.btn_quantity.setNumber(String.valueOf(listData.get(position).getQuantity()));
        holder.btn_quantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                Order order = listData.get(position);
                order.setQuantity(String.valueOf(newValue));
                new Database(cart).updateCart(order);

                double price;
                try {
                    price = (Double.parseDouble(listData.get(position).getPrice()))*(Double.parseDouble(listData.get(position).getQuantity()));
                } catch (NumberFormatException e) {
                    price=0;
                }
                double vat = (price/100)*15;

                holder.txt_price.setText(Common.fmt(price));
                holder.txt_vate.setText(Common.fmt(vat));
                //Calculate total price
                double total = 0;
                double deliver = 0;
                List<Order> orders = new Database(cart).getCarts(Common.currentUser.getPhone());
                for (Order item : orders){
                    total += (Double.parseDouble(item.getPrice()))*(Double.parseDouble(item.getQuantity()));
                    deliver += Double.parseDouble(item.getQuantity())*5;
                }
                Locale locale = new Locale("en", "US");
                NumberFormat fmt = NumberFormat.getCurrencyInstance();
                cart.textTotalPrice.setText(String.valueOf(Common.fmt(total)));
                Common.DELIVER = deliver;
            }
        });
        Locale locale = new Locale("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance();

        double price;
        try {
             price = (Double.parseDouble(listData.get(position).getPrice()))*(Double.parseDouble(listData.get(position).getQuantity()));
        } catch (NumberFormatException e) {
             price=0;
        }
        double vat = (price/100)*15;

        holder.txt_price.setText(Common.fmt(price));
        holder.txt_vate.setText(Common.fmt(vat));
        holder.txt_cart_name.setText(listData.get(position).getProductName());
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}
