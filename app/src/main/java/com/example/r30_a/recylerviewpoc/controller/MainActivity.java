package com.example.r30_a.recylerviewpoc.controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.r30_a.recylerviewpoc.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button btn_toContactPage, btn_toSettingPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }

    private void initView() {

        btn_toContactPage = (Button)findViewById(R.id.btnContactPage);
        btn_toSettingPage = (Button)findViewById(R.id.btnSettingPage);
        btn_toContactPage.setOnClickListener(this);
        btn_toSettingPage.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btnContactPage:
                startActivity(new Intent(this,ContactsPageActivity.class));
                break;

            case R.id.btnSettingPage:
                startActivity(new Intent(this,SettingPageActivity.class));
                break;

        }

    }
}
