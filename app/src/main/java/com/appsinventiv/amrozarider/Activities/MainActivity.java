package com.appsinventiv.amrozarider.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.appsinventiv.amrozarider.Utils.CommonUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.appsinventiv.amrozarider.R;
import com.appsinventiv.amrozarider.Utils.SharedPrefs;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    DatabaseReference mDatabase;
    private String token;

    CardView assignedOrders;
    TextView details;
    private double lng;
    private double lat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        getPermissions();
        details = findViewById(R.id.details);
        assignedOrders = findViewById(R.id.assignedOrders);

        mDatabase = FirebaseDatabase.getInstance().getReference();

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

                            mDatabase.child("Riders").child(SharedPrefs.getRider().getMobile()).child("fcmKey").setValue(token);
                        }
                    }
                });
        assignedOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ListOfOrders.class));
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                lng = extras.getDouble("Longitude");
                lat = extras.getDouble("Latitude");
                if (SharedPrefs.getRider() != null) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("lat", lat);
                    map.put("lon", lng);
                    mDatabase.child("Riders").child(SharedPrefs.getRider().getMobile()).updateChildren(map);
                }


            }

        }
    }

    private void getPermissions() {
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        } else {
            Intent intent = new Intent(MainActivity.this, GPSTrackerActivity.class);
            startActivityForResult(intent, 1);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
