package com.group_7.mhd.mohammed.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.group_7.mhd.mohammed.Interface.ItemClickListener;
import com.group_7.mhd.mohammed.R;
import com.rey.material.widget.CheckBox;

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView txtOrderId, txtOrderStatus, txtOrderPhone, txtOrderAddresslat, txtOrderAddresslon, txtOrderDate, txtPrice;

    private ItemClickListener itemClickListener;

    public ImageView btn_delete,btn_pay,imglogo;

    public CheckBox chkpayemnt;


    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public OrderViewHolder(View itemView) {
        super(itemView);

        txtOrderId = itemView.findViewById(R.id.order_id);
        txtOrderStatus = itemView.findViewById(R.id.order_status);
        txtOrderPhone = itemView.findViewById(R.id.order_phone);
        txtOrderAddresslat = itemView.findViewById(R.id.order_addresslat);
        txtOrderAddresslon = itemView.findViewById(R.id.order_addresslon);
        txtOrderDate=itemView.findViewById(R.id.order_date);
        txtPrice = (TextView)itemView.findViewById(R.id.order_price);

        btn_delete = itemView.findViewById(R.id.btn_delete);
        btn_pay = itemView.findViewById(R.id.btn_pay);


        chkpayemnt = itemView.findViewById(R.id.chkpayment);

        imglogo = (ImageView) itemView.findViewById(R.id.imagelogo);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}
