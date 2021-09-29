package com.group_7.mhd.mohammed;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group_7.mhd.mohammed.Common.Common;
import com.group_7.mhd.mohammed.Database.Database;
import com.group_7.mhd.mohammed.Interface.ItemClickListener;
import com.group_7.mhd.mohammed.Model.Foods;
import com.group_7.mhd.mohammed.Model.Order;
import com.group_7.mhd.mohammed.ViewHolder.FoodViewHolder;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class SearchActivity extends AppCompatActivity {

    //Firebase RecyclerAdapter
    FirebaseRecyclerAdapter<Foods, FoodViewHolder> adapter;
    //search functionality
    FirebaseRecyclerAdapter<Foods, FoodViewHolder> searchadapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference foodList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //init paper
        Paper.init(this);


        //Firebase
        database = FirebaseDatabase.getInstance();
        foodList = database.getReference(Common.FOOD_TABLE);

        recyclerView = findViewById(R.id.recycler_search);
        //recyclerView.setHasFixedSize(true);
        /*layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);*/
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));

        //search
        materialSearchBar = (MaterialSearchBar)findViewById(R.id.searchBar);
        materialSearchBar.setHint("Enter your food");
        //materialSearchBar.setSpeechMode(false);

        LoadSuggest();
        materialSearchBar.setLastSuggestions(suggestList);
        // materialSearchBar =(MaterialSearchBar) findViewById(R.id.searchBar);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                List<String> suggest = new ArrayList<>();
                for(String search:suggestList){

                    if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);

                }
                materialSearchBar.setLastSuggestions(suggest);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

                if(!enabled)
                    recyclerView.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {

                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

        //load all food
        loadAllFoods();
    }

    private void loadAllFoods() {
        adapter = new FirebaseRecyclerAdapter<Foods, FoodViewHolder>(Foods.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList ////Like : Select * from Food where MenuId =
        ){
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, final Foods model, final int position) {
                viewHolder.food_name.setText(model.getName());
                Picasso.get().load(model.getImage()).into(viewHolder.food_image);

                //quick cart
                /*if (!isExist) {*/
                viewHolder.quick_cart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean isExist = new Database(getBaseContext()).checkFoodExist(adapter.getRef(position).getKey(), Common.currentUser.getPhone());
                        if (!isExist) {
                            new Database(getBaseContext()).addToCart(new Order(
                                    Common.currentUser.getPhone(),
                                    adapter.getRef(position).getKey(),
                                    model.getName(),
                                    "1",
                                    model.getPrice(),
                                    model.getDiscount(),
                                    model.getImage()
                            ));

                        } else {
                            new Database(getBaseContext()).increaseCart(Common.currentUser.getPhone(), adapter.getRef(position).getKey());
                        }
                        Toast.makeText(SearchActivity.this, R.string.add, Toast.LENGTH_SHORT).show();
                    }

                });

                /*else
                {
                    new Database(getBaseContext()).increaseCart(Common.currentUser.getPhone(),adapter.getRef(position).getKey());
                }
*/

                final Foods local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Start new Activity
                        Intent foodDetail = new Intent(SearchActivity.this, FoodDetails.class);
                        //Save food id to activity
                        foodDetail.putExtra("FoodId", adapter.getRef(position).getKey());
                        startActivity(foodDetail);
                    }
                });
            }
        };

        //Set Adapter
        /*Log.d(TAG, "loaded List Food: "+adapter.getItemCount());
        Toast.makeText(this, "loaded List Food: "+adapter.getItemCount(), Toast.LENGTH_SHORT).show();*/
        recyclerView.setAdapter(adapter);
    }

    private void LoadSuggest() {
        foodList.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
                        {
                            Foods item = postSnapshot.getValue(Foods.class);
                            //assert item != null;
                            suggestList.add(item.getName());
                        }
                        materialSearchBar.setLastSuggestions(suggestList);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void startSearch(CharSequence text) {

        searchadapter = new FirebaseRecyclerAdapter<Foods, FoodViewHolder>(
                Foods.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList.orderByChild("name").equalTo(text.toString())
        ) {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Foods model, int position) {

                viewHolder.food_name.setText(model.getName());
                Picasso.get().load(model.getImage()).into(viewHolder.food_image);

                final Foods local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Start new Activity
                        Intent foodDetail = new Intent(SearchActivity.this, FoodDetails.class);
                        //Save food id to activity
                        foodDetail.putExtra("FoodId", searchadapter.getRef(position).getKey());
                        startActivity(foodDetail);
                    }
                });
            }
        };

        recyclerView.setAdapter(searchadapter);
    }
/*
    @Override
    protected void onStop() {
        if (adapter != null)
            adapter.stopListening();
        if (searchadapter != null)
            searchadapter.stop
        super.onStop();
    }*/
}
