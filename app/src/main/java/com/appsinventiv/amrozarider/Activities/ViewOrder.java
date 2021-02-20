package com.appsinventiv.amrozarider.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appsinventiv.amrozarider.Adapters.OrderedProductsAdapter;
import com.appsinventiv.amrozarider.Models.OrderModel;
import com.appsinventiv.amrozarider.Models.ProductCountModel;
import com.appsinventiv.amrozarider.Models.RiderModel;
import com.appsinventiv.amrozarider.R;
import com.appsinventiv.amrozarider.Utils.CommonUtils;
import com.appsinventiv.amrozarider.Utils.NotificationAsync;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewOrder extends AppCompatActivity {
    TextView orderId, orderTime, quantity, price, username, phone, address, city, info, day, timeChosen;
    String orderIdFromIntent;
    DatabaseReference mDatabase;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    OrderedProductsAdapter adapter;
    ArrayList<ProductCountModel> list = new ArrayList<>();
    Button orderCompleted, orderShipped;
    OrderModel model;

    String s_orderId, s_quantity, s_price, s_username;
    String userFcmKey;
    ImageView viewOnMap;
    TextView assignedTo;
    TextView storeName;


    Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        this.setTitle("Order View");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Intent intent = getIntent();
        orderIdFromIntent = intent.getStringExtra("orderId");
        orderId = findViewById(R.id.order_id);
        orderTime = findViewById(R.id.order_time);
        assignedTo = findViewById(R.id.assignedTo);
        spinner = findViewById(R.id.spinner);

        quantity = findViewById(R.id.order_quantity);
        price = findViewById(R.id.order_price);
        storeName = findViewById(R.id.storeName);
        viewOnMap = findViewById(R.id.viewOnMap);


        info = findViewById(R.id.info);
        username = findViewById(R.id.ship_username);
        phone = findViewById(R.id.ship_phone);
        address = findViewById(R.id.ship_address);
        timeChosen = findViewById(R.id.timeChosen);
        day = findViewById(R.id.day);


        orderCompleted = findViewById(R.id.completed);
        orderShipped = findViewById(R.id.shipped);

        viewOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String uri = "http://maps.google.com/maps?saddr=" + 31.5123929  + "," + 74.2144306 + "&daddr=" + model.getLat() + "," +model.getLon() ;
                String uri = "https://maps.google.com/?daddr=" + model.getLat() + "," + model.getLon();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });
        orderShipped.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                markOrderAsShipped();
            }
        });

        orderCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                markOrderAsComplete();
            }
        });


        recyclerView = (RecyclerView) findViewById(R.id.recylerview);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        mDatabase.child("Orders").child(orderIdFromIntent).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    model = dataSnapshot.getValue(OrderModel.class);
                    if (model != null) {
                        orderId.setText("" + model.getOrderId());
                        orderTime.setText("" + CommonUtils.getFormattedDate(model.getTime()));
                        quantity.setText("" + model.getCountModelArrayList().size());
                        price.setText("Rs " + model.getTotalPrice());
                        username.setText("" + model.getCustomer().getName());
                        phone.setText("" + model.getCustomer().getPhone());
                        storeName.setText("" + model.getCustomer().getStoreName());
                        assignedTo.setText("Assigned To: " + model.getAssignedTo());
                        address.setText(model.getCustomer().getAddress());
                        info.setText("Instructions: " + model.getInstructions());
                        day.setText("Day: " + model.getDate());
                        timeChosen.setText("Time : " + model.getChosenTime());
                        list = model.getCountModelArrayList();
                        adapter = new OrderedProductsAdapter(ViewOrder.this, list);
                        recyclerView.setAdapter(adapter);


                        s_orderId = model.getOrderId();
                        s_quantity = "" + model.getCountModelArrayList().size();
                        s_price = "" + model.getTotalPrice();
                        s_username = model.getCustomer().getPhone();

                        userFcmKey = model.getCustomer().getFcmKey();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void markOrderAsComplete() {
        showAlertDialogButtonClicked("Completed");
    }

    private void markOrderAsShipped() {
        showAlertDialogButtonClicked("Shipped");
    }

    public void showAlertDialogButtonClicked(final String message) {

        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(ViewOrder.this);
        builder.setTitle("Alert");
        builder.setMessage("Do you want to mark this order as " + message + "?");

        // add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDatabase.child("Orders").child(orderIdFromIntent).child("orderStatus").setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        CommonUtils.showToast("Order marked as " + message);
                        NotificationAsync notificationAsync = new NotificationAsync(ViewOrder.this);
                        String notification_title = "You order has been " + message;
                        String notification_message = "Click to view";
                        notificationAsync.execute("ali", userFcmKey, notification_title, notification_message, "Order", "abc");
                        Intent i = new Intent(ViewOrder.this, ListOfOrders.class);
                        startActivity(i);
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            }
        });
        builder.setNegativeButton("Cancel", null);

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
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
