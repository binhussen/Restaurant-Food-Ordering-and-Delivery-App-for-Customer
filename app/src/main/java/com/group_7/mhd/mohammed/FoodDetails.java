package com.group_7.mhd.mohammed;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.group_7.mhd.mohammed.Common.Common;
import com.group_7.mhd.mohammed.Database.Database;
import com.group_7.mhd.mohammed.Model.Foods;
import com.group_7.mhd.mohammed.Model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import io.paperdb.Paper;

public class FoodDetails extends AppCompatActivity {

    //int ID;
    TextView foodName, foodPrice, foodDescription;
    ImageView foodImage;
    CollapsingToolbarLayout collapsingToolbarLayout;
    CounterFab btnCart;
    ElegantNumberButton numberButton;

    String foodId = "";

    //Firebase Database
    FirebaseDatabase database;
    DatabaseReference foods;

    Foods currentFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);

        //Firebase
        database = FirebaseDatabase.getInstance();
        foods = database.getReference("Foods");

        //Init view
        numberButton = (ElegantNumberButton)findViewById(R.id.number_button);
        btnCart = findViewById(R.id.btn_Cart);

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(getBaseContext()).addToCart(new Order(
                        Common.currentUser.getPhone(),
                        foodId,
                        currentFood.getName(),
                        numberButton.getNumber(),
                        currentFood.getPrice(),
                        currentFood.getDiscount(),
                        currentFood.getImage()
                ));
                Toast.makeText(FoodDetails.this, "Added to cart", Toast.LENGTH_SHORT).show();
            }
        });

        btnCart.setCount(new Database(this).getCountCart(Common.currentUser.getPhone()));

        foodDescription = findViewById(R.id.food_description);
        foodName = findViewById(R.id.food_name);
        foodPrice = findViewById(R.id.food_price);
        foodImage = findViewById(R.id.food_image);

        collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapseAppbar);

        //init paper
        Paper.init(this);


        //Get Food Id from Intent
        if (getIntent() != null){
            foodId = getIntent().getStringExtra("FoodId");
        }
        if(!foodId.isEmpty()){
            if (Common.isConnectedToInternet(getBaseContext()))
                getDetailsFood(foodId);
            else {
                Toast.makeText(FoodDetails.this, R.string.check, Toast.LENGTH_SHORT).show();
                return;
            }
        }else {
            Toast.makeText(this, "Error: foodId = "+foodId, Toast.LENGTH_SHORT).show();
        }
    }

    // getDetailsFood() method
    private void getDetailsFood(String foodId) {
        foods.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentFood = dataSnapshot.getValue(Foods.class);

                Picasso.get().load(currentFood.getImage()).into(foodImage);
                collapsingToolbarLayout.setTitle(currentFood.getName());

                foodPrice.setText(currentFood.getPrice());
                foodName.setText(currentFood.getName());
                foodDescription.setText(currentFood.getDescription());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
