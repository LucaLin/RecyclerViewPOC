package com.example.r30_a.recylerviewpoc.controller;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.r30_a.recylerviewpoc.R;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;

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

        if(PermissionsUtil.hasPermission(this, Manifest.permission.READ_CONTACTS) &&
                PermissionsUtil.hasPermission(this,Manifest.permission.WRITE_CONTACTS))
        switch (v.getId()){
            case R.id.btnContactPage:
                startActivity(new Intent(this,ContactsPageActivity.class));
                break;

            case R.id.btnSettingPage:
                startActivity(new Intent(this,SettingPageActivity.class));
                break;

        }else {
            PermissionsUtil.requestPermission(this, new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permission) {}
                @Override
                public void permissionDenied(@NonNull String[] permission) {}
            },new String[]{Manifest.permission.READ_CONTACTS,
                           Manifest.permission.WRITE_CONTACTS,
                           });

        }

    }
}
