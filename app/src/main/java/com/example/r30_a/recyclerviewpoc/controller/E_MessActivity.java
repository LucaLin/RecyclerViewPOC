package com.example.r30_a.recyclerviewpoc.controller;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.r30_a.recyclerviewpoc.R;
import com.example.r30_a.recyclerviewpoc.util.Util;
import com.facebook.share.model.ShareLinkContent;

public class E_MessActivity extends AppCompatActivity implements View.OnClickListener{

    TextView txv_EmessName, txv_EmessPhone, txv_EmessAddress, txv_EmessEmail;
    ImageView img_EmessAvatar;
    SharedPreferences sf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_e_mess);
        sf = getSharedPreferences("profile", MODE_PRIVATE);
        initView();

    }
    private void initView() {

        txv_EmessName = findViewById(R.id.txv_EmessName);
        txv_EmessPhone = findViewById(R.id.txv_EmessPhone);
        txv_EmessAddress = findViewById(R.id.txv_EmessAddress);
        txv_EmessEmail = findViewById(R.id.txv_EmessEMail);
        img_EmessAvatar = findViewById(R.id.img_EmessAvatar);

        txv_EmessName.setText(sf.getString("name", ""));
        txv_EmessPhone.setText(sf.getString("phoneNum", ""));
        txv_EmessEmail.setText(sf.getString("email_custom", ""));
        txv_EmessAddress.setText(sf.getString("city", "") + sf.getString("street", ""));

        String img_avatarBase64 = (sf.getString("avatar", ""));
        img_EmessAvatar.setImageBitmap(Util.getBitmap_avatar(img_avatarBase64));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }

    }
}
