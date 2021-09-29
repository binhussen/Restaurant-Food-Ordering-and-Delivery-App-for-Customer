package com.group_7.mhd.mohammed;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.group_7.mhd.mohammed.Common.Common;
import com.group_7.mhd.mohammed.Database.Database;
import com.group_7.mhd.mohammed.Model.DataMessage;
import com.group_7.mhd.mohammed.Model.MyResponse;
import com.group_7.mhd.mohammed.Model.Notification;
import com.group_7.mhd.mohammed.Model.Order;
import com.group_7.mhd.mohammed.Model.Request;
import com.group_7.mhd.mohammed.Model.Sender;
import com.group_7.mhd.mohammed.Model.Token;
import com.group_7.mhd.mohammed.Remote.APIService;
import com.group_7.mhd.mohammed.ViewHolder.CartAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Cart extends AppCompatActivity {

    private static final Pattern LOC_PATTERN =
            Pattern.compile("^"+"?[1-9][0-9]*(\\.[0-9]+)?");

    public static int chp=0;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    //Firebase Database
    FirebaseDatabase database;
    DatabaseReference requests;

    public TextView textTotalPrice;
    Button btnPlace, pay ,btnCacil;
    ImageView btnDelete;
    EditText commentt,commentd;

    List<Order> cart = new ArrayList<>();

    CartAdapter adapter;

    APIService mService;

    Place shippingAddress;

    Context mContext;

    /**//*geet latitude and longtide*/
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    LocationRequest locationRequest;
    Location mLastLocation;

    /**//**/

    private static final int MAKE_CALL_PERMISSION_REQUEST_CODE = 1;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //init service
        mService = Common.getFCMService();
        mContext = this;

        //Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference(Common.ORDER_TABLE);

        //init
        recyclerView = findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        textTotalPrice = findViewById(R.id.total_price);

        btnCacil = findViewById(R.id.btn_place_cacel);
        btnPlace = findViewById(R.id.btn_place_order);
        btnDelete = findViewById(R.id.btn_delete);

        commentt = findViewById(R.id.commentt);
        commentd = findViewById(R.id.commentd);

        /**//*location definition to get longtiude and latitude*/
        //check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED&&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CALL_PHONE
            }, Common.REQUEST_CODE);

        } else {
            buildLocationRequest();
            buildLocationCallBack();

            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

        }
        /**//**/

        //init paper
        Paper.init(this);

        btnCacil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cart.size() > 0)
                    showChoosePlace();
                else
                    Toast.makeText(Cart.this, R.string.emptycart ,Toast.LENGTH_SHORT).show();
            }
        });

        loadListFood();
    }

    private void showChoosePlace() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this/*mContext*/);
        alertDialog.setTitle("Where are you now !!");
        alertDialog.setMessage("Please Choose One");
        LayoutInflater inflater = this.getLayoutInflater();
        View order = inflater.inflate(R.layout.order,null);

        final RadioButton rdbkana = (RadioButton) order.findViewById(R.id.rdbKana);
        final RadioButton rdbDelivery = (RadioButton) order.findViewById(R.id.rdbDelivery);

        alertDialog.setView(order); //Add edit Text to aleart dialog
        alertDialog.setIcon(R.drawable.ic_payment_black_24dp);

        //create Dialog and show
        final AlertDialog dialog = alertDialog.create();
        dialog.show();

        //Get AlertDialog from dialog
        final AlertDialog diagview = ((AlertDialog) dialog);
        Button ok = (Button) diagview.findViewById(R.id.ok);
        Button cancel = (Button) diagview.findViewById(R.id.cancel);


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rdbkana.isChecked()){
                    showKana();
                }else if (rdbDelivery.isChecked()){
                    showAlertDialog();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    //Method showAlertDialog()
    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this/*mContext*/);
        alertDialog.setMessage(R.string.addre);

        LayoutInflater inflater = this.getLayoutInflater();
        /*LayoutInflater inflater = LayoutInflater.from(this);*/
        View order_address = inflater.inflate(R.layout.order_address,null);

        final MaterialEditText edtAddresslat = (MaterialEditText)order_address.findViewById(R.id.edtAddreslat);
        final MaterialEditText edtAddresslon = (MaterialEditText)order_address.findViewById(R.id.edtAddreslon);

        /*SupportPlaceAutocompleteFragment edtAddressfrag = (SupportPlaceAutocompleteFragment)getChildFragmentManager()
                        .findFragmentById(R.id.place_autocomplete_fragment);*/
        /*PlaceAutocompleteFragment edtAddressfrag = (PlaceAutocompleteFragment)getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        //hide search item bed=fore fragment
        edtAddressfrag.getView().findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE);
        //set hint for autocomlete edit text
        ((EditText)edtAddressfrag.getView().findViewById(R.id.place_autocomplete_search_input))
                .setHint("Enter Your Address");
        //set text size
        ((EditText)edtAddressfrag.getView().findViewById(R.id.place_autocomplete_search_input))
                .setTextSize(14);
        //get address from place complete
        edtAddressfrag.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                shippingAddress = place;
            }

            @Override
            public void onError(Status status) {
                Log.e("ERROR",status.getStatusMessage());
            }
        });

*/

        final EditText pay_password = (EditText)order_address.findViewById(R.id.edt_pay_password);

        final TextView foodPrice = (TextView)order_address.findViewById(R.id.food_price);
        final TextView VatePrice = (TextView)order_address.findViewById(R.id.vate_price);
        final TextView pay_balance = (TextView)order_address.findViewById(R.id.total_price);
        final TextView Delivey = (TextView)order_address.findViewById(R.id.delivery_price);
        final TextView surcharge = (TextView)order_address.findViewById(R.id.surcharge_price);

        if (Double.toString(mLastLocation.getLatitude())!=null && Double.toString(mLastLocation.getLongitude())!=null)
        {
            edtAddresslat.setText(Double.toString(mLastLocation.getLatitude()));
            edtAddresslon.setText(Double.toString(mLastLocation.getLongitude()));
        }

        pay = (Button) order_address.findViewById(R.id.btn_payment);

        double price = Double.parseDouble(textTotalPrice.getText().toString());
        double vat = (price/100)*15;
        double surcharg = (price/100)*6;
        double total = vat+price+Common.DELIVER+surcharg;
        foodPrice.setText(Common.fmt(price)+" "+foodPrice.getText().toString());
        VatePrice.setText(Common.fmt(vat)+" "+VatePrice.getText().toString());
        Delivey.setText(Common.fmt(Common.DELIVER)+" "+Delivey.getText().toString());
        surcharge.setText(Common.fmt(surcharg)+" "+surcharge.getText().toString());

        pay_balance.setText(Common.fmt(total)+" "+pay_balance.getText().toString());
        pay_balance.setEnabled(false);

        alertDialog.setView(order_address); //Add edit Text to aleart dialog
        alertDialog.setIcon(R.drawable.ic_payment_black_24dp);

        if (checkPermission(Manifest.permission.CALL_PHONE)) {
            pay.setEnabled(true);
        } else {
            pay.setEnabled(false);
            ActivityCompat.requestPermissions(Cart.this, new String[]{Manifest.permission.CALL_PHONE},MAKE_CALL_PERMISSION_REQUEST_CODE);
        }

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cbepass = pay_password.getText().toString();

                if (!cbepass.isEmpty()||cbepass.length()==4){
                    if (checkPermission(Manifest.permission.CALL_PHONE)) {
                        String Hash = Uri.encode("#");
                        String request_balance=pay_balance.getText().toString();
                        String dial = "tel:"+"*847*1*0923692424*"+request_balance+"*"+cbepass+"*1"+Hash;
                        /*startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));*/
                        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
                    } else {
                        Toast.makeText(Cart.this,"Permission Call Phone Denied", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    pay_password.setError(getString(R.string.err_cbe));
                }
            }
        });

        //create Dialog and show
        final AlertDialog dialog = alertDialog.create();
        dialog.show();

        //Get AlertDialog from dialog
        final AlertDialog diagview = ((AlertDialog) dialog);
        Button ok = (Button) diagview.findViewById(R.id.ok);
        Button cancel = (Button) diagview.findViewById(R.id.cancel);


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String addlat = edtAddresslat.getText().toString().trim();
                String addlon = edtAddresslon.getText().toString().trim();
                /**//**/

                /**//**/

                if ((!addlat.isEmpty()&&LOC_PATTERN.matcher(addlat).matches())){

                    if ((!addlon.isEmpty()&&LOC_PATTERN.matcher(addlon).matches())){
                        Request request = new Request(
                                Common.currentUser.getPhone(),
                                Common.currentUser.getName(),
                                edtAddresslat.getText().toString(),
                                edtAddresslon.getText().toString(),
                                pay_balance.getText().toString(),
                                "0",
                                "CBE",
                                "",
                                "true",
                                cart
                        );

                        //Submit to Firebase
                        //We will using System.CurrentMills to key
                        String order_number = String.valueOf(System.currentTimeMillis());
                        requests.child(order_number)
                                .setValue(request);

                        //Delete cart
                        new Database(getBaseContext()).cleanCart(Common.currentUser.getPhone());
                        sendNotificationOrder(order_number);
                        /*Toast.makeText(Cart.this, "Thank you, Order Place.", Toast.LENGTH_SHORT).show();*/
                        dialog.dismiss();
                        finish();
                    }
                    edtAddresslon.setError(getString(R.string.err_address));
                }else {
                    edtAddresslat.setError(getString(R.string.err_address));
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });

    }

    //Method showkana()
    private void showKana() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this/*mContext*/);
        alertDialog.setTitle("");
        alertDialog.setMessage(R.string.addre);
        LayoutInflater inflater = this.getLayoutInflater();
        View order_table = inflater.inflate(R.layout.order_table,null);

        final MaterialEditText edttable = (MaterialEditText)order_table.findViewById(R.id.edtTable);

        final EditText pay_password = (EditText)order_table.findViewById(R.id.edt_pay_password);

        final TextView foodPrice = (TextView)order_table.findViewById(R.id.food_price);
        final TextView VatePrice = (TextView)order_table.findViewById(R.id.vate_price);
        final TextView pay_balance = (TextView)order_table.findViewById(R.id.total_price);
        final TextView surcharge = (TextView)order_table.findViewById(R.id.surcharge_price);

        final RadioButton rdbCBE = (RadioButton)order_table.findViewById(R.id.rdbCBE);
        final RadioButton rdbkana = (RadioButton)order_table.findViewById(R.id.rdbCOD);

        pay = (Button) order_table.findViewById(R.id.btn_payment);

        double price = Double.parseDouble(textTotalPrice.getText().toString());
        double surcharg = (price/100)*6;
        double vat = (price/100)*15;
        double total = surcharg+vat+price;
        foodPrice.setText(Common.fmt(price)+" "+foodPrice.getText().toString());
        VatePrice.setText(Common.fmt(vat)+" "+VatePrice.getText().toString());
        surcharge.setText(Common.fmt(surcharg)+" "+surcharge.getText().toString());

        pay_balance.setText(Common.fmt(total)+" "+pay_balance.getText().toString());
        pay_balance.setEnabled(false);

        alertDialog.setView(order_table); //Add edit Text to aleart dialog
        alertDialog.setIcon(R.drawable.ic_payment_black_24dp);

        if (checkPermission(Manifest.permission.CALL_PHONE)) {
            pay.setEnabled(true);
        } else {
            pay.setEnabled(false);
            ActivityCompat.requestPermissions(Cart.this, new String[]{Manifest.permission.CALL_PHONE},MAKE_CALL_PERMISSION_REQUEST_CODE);
        }

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cbepass = pay_password.getText().toString();

                if (!cbepass.isEmpty()||cbepass.length()==4){
                    if (checkPermission(Manifest.permission.CALL_PHONE)) {
                        String Hash = Uri.encode("#");
                        String request_balance=pay_balance.getText().toString();
                        String dial = "tel:"+"*847*1*0923692424*"+request_balance+"*"+cbepass+"*1"+Hash;
                        /*startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(dial)));*/
                        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
                    } else {
                        Toast.makeText(Cart.this,"Permission Call Phone Denied", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    pay_password.setError(getString(R.string.err_cbe));
                }
            }
        });

        //create Dialog and show
        final AlertDialog dialog = alertDialog.create();
        dialog.show();

        //Get AlertDialog from dialog
        final AlertDialog diagview = ((AlertDialog) dialog);
        Button ok = (Button) diagview.findViewById(R.id.ok);
        Button cancel = (Button) diagview.findViewById(R.id.cancel);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String table = edttable.getText().toString().trim();

                if (!table.isEmpty()){
                    String method = "CBE";
                    if (rdbkana.isChecked()){
                        method = "COD";
                    }
                    Request request = new Request(
                            Common.currentUser.getPhone(),
                            Common.currentUser.getName(),
                            "",
                            "",
                            pay_balance.getText().toString(),
                            "0",
                            method,
                            edttable.getText().toString(),
                            "false",
                            cart
                    );

                    //Submit to Firebase
                    //We will using System.CurrentMills to key
                    String order_number = String.valueOf(System.currentTimeMillis());
                    requests.child(order_number)
                            .setValue(request);

                    //Delete cart
                    new Database(getBaseContext()).cleanCart(Common.currentUser.getPhone());
                    sendNotificationOrder(order_number);
                    /*Toast.makeText(Cart.this, "Thank you, Order Place.", Toast.LENGTH_SHORT).show();*/
                    finish();
                }else {
                    edttable.setError("Please Enter Table Number");
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();


            }
        });

    }

    private void sendNotificationOrder(final String order_number) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query data = tokens.orderByChild("isServerToken").equalTo(true);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot:dataSnapshot.getChildren())
                {
                    Token serverToken = postSnapShot.getValue(Token.class);

                    /////////some debug here
                    //create pay laoad
                    /*Notification notification = new Notification("MHD", "you have new order "+order_number);
                    Sender content = new Sender(serverToken.getToken(),notification);
*/
                    Map<String, String> content = new HashMap<>();
                    content.put("title","Kana Restaurant");
                    content.put("Message","Your Order need Driver");
                    DataMessage dataMessage = new DataMessage(serverToken.getToken(),content);

                    String test = new Gson().toJson(dataMessage);
                    Log.d("content ",test);

                    mService.sendNotification(dataMessage)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                                    //only yun when get result
                                    if (response.code() == 200)
                                    {
                                        if (response.body().success ==1) {
                                            Toast.makeText(Cart.this, R.string.thano, Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                        else
                                        {
                                            Toast.makeText(Cart.this, R.string.failed, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                    Log.e("ERROR ",t.getMessage());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //Method for load food
    private void loadListFood() {
        cart = new Database(this).getCarts(Common.currentUser.getPhone());
        adapter = new CartAdapter(cart, this);
        //refresh
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);


        //Calculate total price
        double price = 0;
        Common.DELIVER=0;
        for (Order order : cart){
            try {
                price += (Double.parseDouble(order.getPrice()))*(Double.parseDouble(order.getQuantity()));
            } catch (NumberFormatException e) {
                price=0;
            }
            Common.DELIVER+=Double.parseDouble(order.getQuantity())*5;
        }

        textTotalPrice.setText(Common.fmt(price));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Common.REQUEST_CODE: {
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        buildLocationRequest();
                        buildLocationCallBack();

                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    }
                    else
                    {
                        Toast.makeText(this,"You Should assign Permission !",Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            default:
                break;
        }
    }

    private void buildLocationCallBack() {
        locationCallback = new LocationCallback(){

            @Override
            public void onLocationResult(LocationResult locationResult) {
                mLastLocation = locationResult.getLastLocation();
                /*Toast.makeText(Cart.this,new StringBuilder("")
                        .append(mLastLocation.getLatitude())
                        .append("/")
                        .append(mLastLocation.getLongitude())
                        .toString(),Toast.LENGTH_SHORT).show();*/

                super.onLocationResult(locationResult);
            }
        };
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(10f);
        /*locationRequest.setPriority(5000);*/
        locationRequest.setFastestInterval(3000);
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        if (item.getTitle().equals(Common.DELETE))
            deleteCart(item.getOrder());
        return true;
    }

    private void deleteCart(int position) {
        //remove item at list<oredr> by position
        cart.remove(position);
        //delete all data from sqlite
        new Database(this).cleanCart(Common.currentUser.getPhone());
        //update new data fromlist<oreder> to sqlite
        for (Order item:cart)
            new Database(this).addToCart(item);
        //refresh
        loadListFood();
    }

    private boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(this,permission)== PackageManager.PERMISSION_GRANTED;
    }
}






















