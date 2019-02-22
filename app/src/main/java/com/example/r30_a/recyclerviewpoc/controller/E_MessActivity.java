package com.example.r30_a.recyclerviewpoc.controller;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.r30_a.recyclerviewpoc.R;

public class E_MessActivity extends AppCompatActivity {

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

        txv_EmessName = (TextView)findViewById(R.id.txv_EmessName);
        txv_EmessPhone = (TextView)findViewById(R.id.txv_EmessPhone);
        txv_EmessAddress = (TextView)findViewById(R.id.txv_EmessAddress);
        txv_EmessEmail = (TextView)findViewById(R.id.txv_EmessEMail);
        img_EmessAvatar = (ImageView) findViewById(R.id.img_EmessAvatar);

        txv_EmessName.setText(sf.getString("name",""));
        txv_EmessPhone.setText(sf.getString("phoneNum",""));
        txv_EmessEmail.setText(sf.getString("email_custom",""));
        txv_EmessAddress.setText(sf.getString("city","") + sf.getString("street",""));

        String img_avatarBase64 = (sf.getString("avatar",""));
        if(!TextUtils.isEmpty(img_avatarBase64)){
            byte[] bytes = Base64.decode(img_avatarBase64,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            img_EmessAvatar.setImageBitmap(bitmap);
        }





    }
}
