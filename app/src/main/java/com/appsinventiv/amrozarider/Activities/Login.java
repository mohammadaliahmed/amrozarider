package com.appsinventiv.amrozarider.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.appsinventiv.amrozarider.Models.RiderModel;
import com.appsinventiv.amrozarider.R;
import com.appsinventiv.amrozarider.Utils.CommonUtils;
import com.appsinventiv.amrozarider.Utils.SharedPrefs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Login extends AppCompatActivity {

    DatabaseReference mDatabase;
    Button login;
    TextView register;
    EditText phone, password;
    private HashMap<String, RiderModel> map = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        password = findViewById(R.id.password);
        phone = findViewById(R.id.phone);
        login = findViewById(R.id.login);

        getUserfromserver();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phone.getText().length() == 0) {
                    phone.setError("Etner phone");
                } else if (password.getText().length() == 0) {
                    password.setError("Enter password");
                } else {
                    userLogin();
                }

            }
        });
    }

    private void userLogin() {
        if (map.containsKey(phone.getText().toString())) {
            RiderModel riderModel = map.get(phone.getText().toString());
            if (riderModel.getPassword().equals(password.getText().toString())) {
                SharedPrefs.setRider(riderModel);
                Intent i = new Intent(Login.this, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);

                finish();
            } else {
                CommonUtils.showToast("Wrong password");
            }
        } else {
            CommonUtils.showToast("No account");
        }
    }

    private void getUserfromserver() {
        mDatabase.child("Riders").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() != null) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        RiderModel riderModel = snapshot1.getValue(RiderModel.class);
                        if (riderModel != null && riderModel.getName() != null) {
                            map.put(riderModel.getMobile(), riderModel);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}
