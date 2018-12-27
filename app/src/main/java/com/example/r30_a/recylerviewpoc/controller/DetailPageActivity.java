package com.example.r30_a.recylerviewpoc.controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.r30_a.recylerviewpoc.R;
import com.example.r30_a.recylerviewpoc.util.CommonUtil;

public class DetailPageActivity extends AppCompatActivity {

    String name,phoneNumber;
    ImageView img_avatar;
    long id;

    TextView txvName,txvPhoneNumber;

    private DrawerLayout drawerLayout;//側邊選單
    private NavigationView navigationView;
    private Toolbar toolbar;
    Context context;
    Toast toast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_page);

        initView();

    }


    private void initView() {

        txvName = (TextView)findViewById(R.id.txv_detailName);
        txvPhoneNumber = (TextView)findViewById(R.id.txv_detailPhone);
        img_avatar = (ImageView)findViewById(R.id.detail_img_avatar);
        context = DetailPageActivity.this;
        toast = Toast.makeText(context,"",Toast.LENGTH_SHORT);
        final Intent intent = getIntent();
        id = intent.getLongExtra("id",0);
        name = intent.getStringExtra("name");
        phoneNumber = intent.getStringExtra("phoneNumber");
        final byte[] bytes = intent.getByteArrayExtra("avatar");
        if(bytes != null && bytes.length>0){
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            img_avatar.setImageBitmap(bitmap);
        }

        txvName.setText(name);
        txvPhoneNumber.setText(phoneNumber);

        navigationView = (NavigationView)findViewById(R.id.navigationView);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        CommonUtil.setDrawer(this,drawerLayout,toolbar,R.layout.drawer_header,navigationView);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawer(GravityCompat.START);//選單按完之後收起抽屜

                switch (item.getItemId()){
                    case R.id.allContact:
                        startActivity(new Intent(context,ContactsPageActivity.class));
                        finish();
                        break;

                    case R.id.favorContact:
                        startActivity(new Intent(context,FavorListPageActivity.class));
                        finish();
                        break;

                    case R.id.settings:
                        startActivity(new Intent(context,SettingPageActivity.class));
                        break;
                }
                return true;
            }
        });
        toolbar.inflateMenu(R.menu.detail_menu);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()){
                    case R.id.p1_update:
                        Intent intent_update = new Intent(DetailPageActivity.this, UpdateDataActivity.class);
                        intent_update.putExtra("id",String.valueOf(id));
                        intent_update.putExtra("name", name);
                        intent_update.putExtra("phone", phoneNumber);
                        intent_update.putExtra("avatar", bytes);
                        //intent.putExtra("note",list.get(pos).getNote());

                        //startActivityForResult(intent, ContactsPageActivity.REQUEST_CODE);
                        startActivity(intent_update);
                        break;
                    case R.id.p2_addFavor:

                        break;
                    case R.id.p3_delete:

                        break;
                }

                return true;
            }
        });
    }
}
