package com.appsinventiv.amrozarider.Activities;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.appsinventiv.amrozarider.R;
import com.appsinventiv.amrozarider.Utils.SharedPrefs;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    DatabaseReference mDatabase;
    private String token;

    CardView assignedOrders;
    TextView details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        details = findViewById(R.id.details);
        assignedOrders = findViewById(R.id.assignedOrders);

        details.setText("Welcome Rider,\n\nName: " + SharedPrefs.getRider().getName() + "\n\nPhone: " + SharedPrefs.getRider().getMobile());
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        token = task.getResult();
                        if (SharedPrefs.getRider() != null) {
                            mDatabase = FirebaseDatabase.getInstance().getReference();
                            mDatabase.child("Riders").child(SharedPrefs.getRider().getMobile()).child("fcmKey").setValue(token);
                        }
                    }
                });
        assignedOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ListOfOrders.class));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
