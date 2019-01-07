package com.example.r30_a.recylerviewpoc.controller;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.r30_a.recylerviewpoc.R;
import com.example.r30_a.recylerviewpoc.util.CommonUtil;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button btn_toContactPage, btn_toSettingPage;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //第一次使用的話先開歡迎畫面
        if(CommonUtil.isFirstTimeUse(this)){
            Intent intent = new Intent(this,WelcomeActivity.class);
            startActivity(intent);
            finish();
        }
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initView() {

        btn_toContactPage = (Button)findViewById(R.id.btnContactPage);
        btn_toSettingPage = (Button)findViewById(R.id.btnSettingPage);
        btn_toContactPage.setOnClickListener(this);
        btn_toSettingPage.setOnClickListener(this);
        builder = new AlertDialog.Builder(this);
        builder.setTitle("loading")
                .setView(R.layout.layout_progress_view)
                .create();

        if(     PermissionsUtil.hasPermission(this,Manifest.permission.CALL_PHONE) &&
                PermissionsUtil.hasPermission(this,Manifest.permission.SEND_SMS) &&
                PermissionsUtil.hasPermission(this, Manifest.permission.READ_CONTACTS) &&
                PermissionsUtil.hasPermission(this,Manifest.permission.WRITE_CONTACTS)&&
                PermissionsUtil.hasPermission(this,Manifest.permission.CAMERA) &&
                PermissionsUtil.hasPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)&&
                PermissionsUtil.hasPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)&&
                PermissionsUtil.hasPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)&&
                PermissionsUtil.hasPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)){
        }else {
            PermissionsUtil.requestPermission(this, new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permission) {}
                @Override
                public void permissionDenied(@NonNull String[] permission) {}
            },new String[]{Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.SEND_SMS,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION

            });

        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btnContactPage:
                startActivity(new Intent(this,ContactPageActivity.class));
                break;

            case R.id.btnSettingPage:
                startActivity(new Intent(this,SettingPageActivity.class));
                break;

            }
    }
}
