package com.appsinventiv.amrozarider.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.appsinventiv.amrozarider.ApplicationClass;
import com.appsinventiv.amrozarider.Models.Customer;
import com.appsinventiv.amrozarider.Models.Product;
import com.appsinventiv.amrozarider.Models.RiderModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;


/**
 * Created by AliAh on 20/02/2018.
 */

public class SharedPrefs {


    private SharedPrefs() {

    }


    public static void setCartCount(String count) {
        preferenceSetter("cartCount", count);
    }

    public static String getCartCount() {
        return preferenceGetter("cartCount");
    }


    public static void setProductsMap(HashMap<String, Product> itemList) {

        Gson gson = new Gson();
        String json = gson.toJson(itemList);
        preferenceSetter("setProductsMap", json);
    }

    public static HashMap<String, Product> getProductsMap() {
        Gson gson = new Gson();

        HashMap<String, Product> retMap = new Gson().fromJson(
                preferenceGetter("setProductsMap"), new TypeToken<HashMap<String, Product>>() {
                }.getType()
        );

        return retMap;
    }

    public static void setAdminPhone(String fcmKey) {
        preferenceSetter("adminPhone", fcmKey);
    }

    public static String getAdminPhone() {
        return preferenceGetter("adminPhone");
    }

    public static void setUser(Customer model) {

        Gson gson = new Gson();
        String json = gson.toJson(model);
        preferenceSetter("customerModel", json);
    }

    public static Customer getUser() {
        Gson gson = new Gson();
        Customer customer = gson.fromJson(preferenceGetter("customerModel"), Customer.class);

        return customer;
    }

    public static void setRider(RiderModel model) {

        Gson gson = new Gson();
        String json = gson.toJson(model);
        preferenceSetter("setRider", json);
    }

    public static RiderModel getRider() {
        Gson gson = new Gson();
        RiderModel customer = gson.fromJson(preferenceGetter("setRider"), RiderModel.class);

        return customer;
    }


    public static void preferenceSetter(String key, String value) {
        SharedPreferences pref = ApplicationClass.getInstance().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String preferenceGetter(String key) {
        SharedPreferences pref;
        String value = "";
        pref = ApplicationClass.getInstance().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        value = pref.getString(key, "");
        return value;
    }

    public static void logout() {
        SharedPreferences pref = ApplicationClass.getInstance().getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }
}
