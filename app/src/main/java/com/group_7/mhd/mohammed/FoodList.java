package com.group_7.mhd.mohammed;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Context;
import com.group_7.mhd.mohammed.Common.Common;
import com.group_7.mhd.mohammed.Database.Database;
import com.group_7.mhd.mohammed.Interface.ItemClickListener;
import com.group_7.mhd.mohammed.Model.Category;
import com.group_7.mhd.mohammed.Model.Foods;
import com.group_7.mhd.mohammed.Model.Order;
import com.group_7.mhd.mohammed.ViewHolder.FoodViewHolder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.group_7.mhd.mohammed.ViewHolder.MenuViewHolder;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class FoodList extends AppCompatActivity {

    private static final String TAG = "FoodList";

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference foodList,category;

    String categoryId = "";

    //FirebaseRecyclerAdapter
    FirebaseRecyclerAdapter<Foods, FoodViewHolder> adapter;

    //search fenctionality
    FirebaseRecyclerAdapter<Foods, FoodViewHolder> searchadapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    CounterFab fab;

    TextView cat_name;
    ImageView cat_image;
    Category currentCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        //Firebase
        database = FirebaseDatabase.getInstance();
        foodList = database.getReference(Common.FOOD_TABLE);
        category = database.getReference(Common.CATEGORY_TABLE);

        cat_name = findViewById(R.id.cat_name);
        cat_image = findViewById(R.id.cat_image);

        //init paper
        Paper.init(this);


        recyclerView = findViewById(R.id.recycler_food);
        //recyclerView.setHasFixedSize(true);
        /*layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);*/
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));

        //Get intent here from Home_Activity to get CategoryId
        if (getIntent() != null){
            categoryId = getIntent().getStringExtra("CategoryId");
        }

        if(!categoryId.isEmpty() && categoryId != null){
            if (Common.isConnectedToInternet(getBaseContext())) {
                getCategoryDetail(categoryId);
                loadListFood(categoryId);
            }
            else {
                Toast.makeText(FoodList.this, R.string.check, Toast.LENGTH_SHORT).show();
                return;
            }
        }

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
                                Intent foodDetail = new Intent(FoodList.this, FoodDetails.class);
                                //Save food id to activity
                                foodDetail.putExtra("FoodId", searchadapter.getRef(position).getKey());
                                startActivity(foodDetail);
                            }
                        });
                    }
                };

                recyclerView.setAdapter(searchadapter);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //ix click back buton from oo and dono see catagoy
        if (adapter!= null)
            adapter.startListening();
    }

    private void LoadSuggest() {
        foodList.orderByChild("menuId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
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

    //loadadListFood() method implementation
    private void loadListFood(String categoryId) {
        adapter = new FirebaseRecyclerAdapter<Foods, FoodViewHolder>(Foods.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList.orderByChild("menuId").equalTo(categoryId) ////Like : Select * from Food where MenuId =
                ){
            @Override
            protected void populateViewHolder(final FoodViewHolder viewHolder, final Foods model, final int position) {
                viewHolder.food_name.setText(model.getName());
                Picasso.get().load(model.getImage()).into(viewHolder.food_image);
                viewHolder.food_price.setText(model.getPrice());
                //quick cart
                /*if (!isExist) {*/
                    viewHolder.quick_cart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            boolean isExist = new Database(getBaseContext()).checkFoodExist(adapter.getRef(position).getKey(),Common.currentUser.getPhone());
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
                            Toast.makeText(FoodList.this, R.string.add, Toast.LENGTH_SHORT).show();
                        }

                    });
                viewHolder.quick_cart.setCount(new Database(getBaseContext()).getCountCart(Common.currentUser.getPhone()));
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
                        Intent foodDetail = new Intent(FoodList.this, FoodDetails.class);
                        //Save food id to activity
                        foodDetail.putExtra("FoodId", adapter.getRef(position).getKey());
                        startActivity(foodDetail);
                    }
                });
            }
        };

        //Set Adapter
        Log.d(TAG, "loaded List Food: "+adapter.getItemCount());
        Toast.makeText(this, R.string.load+adapter.getItemCount(), Toast.LENGTH_SHORT).show();
        recyclerView.setAdapter(adapter);
    }
    // getCatagory detail() method
    private void getCategoryDetail(String categoryId) {
        category.child(categoryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentCategory = dataSnapshot.getValue(Category.class);

                Picasso.get().load(currentCategory.getImage()).into(cat_image);
                cat_name.setText(currentCategory.getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
