package com.appsinventiv.amrozarider.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appsinventiv.amrozarider.Adapters.OrdersAdapter;
import com.appsinventiv.amrozarider.Models.OrderModel;
import com.appsinventiv.amrozarider.R;
import com.appsinventiv.amrozarider.Utils.SharedPrefs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class ListOfOrders extends AppCompatActivity {

    RecyclerView recycler_orders;
    ArrayList<OrderModel> arrayList = new ArrayList<>();
    String orderStatus;
    OrdersAdapter adapter;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_orders);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.setTitle("Assinged Orders");

        recycler_orders = (RecyclerView) findViewById(R.id.recycler_orders);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler_orders.setLayoutManager(layoutManager);
        adapter = new OrdersAdapter(this, arrayList);

        recycler_orders.setAdapter(adapter);
        getDataFromServer();
    }

    private void getDataFromServer() {
        mDatabase.child("Riders").child(SharedPrefs.getRider().getMobile()).child("assignedOrders").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        String id = snapshot1.getValue(String.class);
                        getOrdersFromServer(id);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getOrdersFromServer(String id) {

        mDatabase.child("Orders").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {

                    OrderModel model = dataSnapshot.getValue(OrderModel.class);
                    if (model != null) {
                        arrayList.add(model);

                    }

                    Collections.sort(arrayList, new Comparator<OrderModel>() {
                        @Override
                        public int compare(OrderModel listData, OrderModel t1) {
                            Long ob1 = listData.getTime();
                            Long ob2 = t1.getTime();

                            return ob2.compareTo(ob1);

                        }
                    });
                    adapter.setItemList(arrayList);
//                    adapter.notifyDataSetChanged();
                } else {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {


            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
