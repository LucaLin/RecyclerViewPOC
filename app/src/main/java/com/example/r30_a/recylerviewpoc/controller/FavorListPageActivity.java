package com.example.r30_a.recylerviewpoc.controller;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.r30_a.recylerviewpoc.R;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

public class FavorListPageActivity extends AppCompatActivity {

    JsonObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favor_list_page);

        try {
            jsonObject = new JsonObject();
            jsonObject.get("favorList");
            int i = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
