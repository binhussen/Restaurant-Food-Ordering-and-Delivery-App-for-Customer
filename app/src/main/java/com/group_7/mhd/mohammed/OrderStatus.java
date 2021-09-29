package com.group_7.mhd.mohammed;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.group_7.mhd.mohammed.Common.Common;
import com.group_7.mhd.mohammed.Model.Order;
import com.group_7.mhd.mohammed.Model.Request;
import com.group_7.mhd.mohammed.ViewHolder.OrderViewHolder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.paperdb.Paper;

public class OrderStatus extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    //Firebase Database
    FirebaseDatabase database;
    DatabaseReference requests;

    Button pay;
    private static final int MAKE_CALL_PERMISSION_REQUEST_CODE = 1;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Orders");

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);

        //init paper
        Paper.init(this);


        //Fitebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference(Common.ORDER_TABLE);

        recyclerView = findViewById(R.id.listOrder);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //I change the value from
        loadOrders(Common.currentUser.getPhone());

        /*if(getIntent()==null)
            loadOrders(Common.currentUser.getPhone());
        else
            loadOrders(getIntent().getStringExtra("userPhone"))*/;
    }

    //loadOrders() method
    private void loadOrders(String phone) {
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests.orderByChild("phone").equalTo(phone)
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, final Request model, final int position) {
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderAddresslat.setText(model.getAddresslat());
                viewHolder.txtOrderAddresslon.setText(model.getAddresslon());
                viewHolder.txtOrderPhone.setText(model.getPhone());
                viewHolder.txtOrderDate.setText(Common.getDate(Long.parseLong(adapter.getRef(position).getKey())));
                viewHolder.txtPrice.setText(model.getTotal());

                if (getItem(position).getPaymentMethod().equals("COD")){
                    viewHolder.chkpayemnt.setChecked(true);
                }

                if (getItem(position).getTackAway()!=null){

                    if (getItem(position).getTackAway().equals("false")) {
                        Picasso.get(/*cart.getBaseContext()*/)
                                .load(R.drawable.table)
                                .resize(70,70)
                                .centerCrop()
                                .into(viewHolder.imglogo);

                    }else if (getItem(position).getTackAway().equals("true")){
                        Picasso.get(/*cart.getBaseContext()*/)
                                .load(R.drawable.shipper)
                                .resize(70,70)
                                .centerCrop()
                                .into(viewHolder.imglogo);
                    }
                }

                viewHolder.btn_pay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (adapter.getItem(position).getStatus().equals("0"))
                            pay_Order(adapter.getItem(position).getTotal());
                        else
                            Toast.makeText(OrderStatus.this,"You have already payed.",Toast.LENGTH_SHORT).show();
                    }
                });
                viewHolder.btn_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (adapter.getItem(position).getStatus().equals("0"))
                            deleteOrder(adapter.getRef(position).getKey());
                        else
                            Toast.makeText(OrderStatus.this,"You can not delete ths order.",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        Toast.makeText(this, R.string.value, Toast.LENGTH_SHORT).show();
        recyclerView.setAdapter(adapter);
    }

    private void pay_Order(String key) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderStatus.this/*mContext*/);
        alertDialog.setMessage(R.string.chosep);

        /*LayoutInflater inflater = this.getLayoutInflater();*/
        LayoutInflater inflater = LayoutInflater.from(OrderStatus.this);
        View order_Payment = inflater.inflate(R.layout.order_payment,null);

        final TextView pay_balance = (TextView)order_Payment.findViewById(R.id.edt_pay_balance);
        final EditText pay_password = (EditText)order_Payment.findViewById(R.id.edt_pay_password);
        pay = (Button) order_Payment.findViewById(R.id.btn_payment);

        pay_balance.setText(key.toString());
        pay_balance.setEnabled(false);

        alertDialog.setView(order_Payment); //Add edit Text to aleart dialog
        alertDialog.setIcon(R.drawable.ic_payment_black_24dp);

        if (checkPermission(Manifest.permission.CALL_PHONE)) {
            pay.setEnabled(true);
        } else {
            pay.setEnabled(false);
            ActivityCompat.requestPermissions(OrderStatus.this, new String[]{Manifest.permission.CALL_PHONE},MAKE_CALL_PERMISSION_REQUEST_CODE);
        }

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cbepass = pay_password.getText().toString();

                if (!TextUtils.isEmpty(cbepass)){
                    if (checkPermission(Manifest.permission.CALL_PHONE)) {
                        String Hash = Uri.encode("#");
                        String request_balance=pay_balance.getText().toString();
                        String dial = "tel:"+"*847*1*0923692424*"+request_balance+"*"+cbepass+"*1"+Hash;
                        /*startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));*/
                        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
                    } else {
                        Toast.makeText(OrderStatus.this,"Permission Call Phone Denied", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(OrderStatus.this,"Please enter Password",Toast.LENGTH_SHORT).show();
                }
            }
        });
        alertDialog.show();
    }

    private void deleteOrder(final String key) {

        AlertDialog.Builder CheckBuild = new AlertDialog.Builder(OrderStatus.this);
        CheckBuild.setIcon(R.drawable.no);
        CheckBuild.setTitle("Error!");
        CheckBuild.setMessage("do you went to delete this order ?");

        //Builder Retry Button

        CheckBuild.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                //Exit The Activity
                dialogInterface.dismiss();
            }

        });
        CheckBuild.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                requests.child(key)
                        .removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(OrderStatus.this,new StringBuilder("Order")
                                .append(key)
                                .append(" has been deleted!").toString(),Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(OrderStatus.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });
        AlertDialog alertDialog = CheckBuild.create();
        alertDialog.show();

    }

    private boolean checkPermission(String permission)
    {
        return ContextCompat.checkSelfPermission(this,permission)== PackageManager.PERMISSION_GRANTED;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}



















