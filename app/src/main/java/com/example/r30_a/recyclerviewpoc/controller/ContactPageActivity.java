package com.example.r30_a.recyclerviewpoc.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.r30_a.recyclerviewpoc.R;
import com.example.r30_a.recyclerviewpoc.fragment.ContactPageFragment;
import com.example.r30_a.recyclerviewpoc.fragment.FavorListFragment;
import com.example.r30_a.recyclerviewpoc.util.Util;

public class ContactPageActivity extends AppCompatActivity {
    //------抽屜元件--------//
    private DrawerLayout drawerLayout;//側邊選單
    private NavigationView navigationView;
    private Toolbar toolbar;
    private Context context;
    SharedPreferences sf;
    String userName;
    ImageView img_headerAvatar;
    Bitmap bitmap_avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_page);

        showFrag(new ContactPageFragment());//顯示預設的聯絡人清單頁
        context = ContactPageActivity.this;
        sf = getSharedPreferences("profile", MODE_PRIVATE);

        //----------抽屜設定-----------//
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        navigationView = (NavigationView) findViewById(R.id.navigationView);

        //--------設定抬頭個人照 && 名字----//
        userName = sf.getString("name", "");
        String img_avatarBase64 = sf.getString("avatar", "");
        if (img_avatarBase64.length() > 0) {
            byte[] avatar_bytes = Base64.decode(img_avatarBase64, Base64.DEFAULT);
            bitmap_avatar = BitmapFactory.decodeByteArray(avatar_bytes, 0, avatar_bytes.length);
        }

        TextView txvHeaderView = navigationView.getHeaderView(0).findViewById(R.id.txvHeaderTitle);
        img_headerAvatar = navigationView.getHeaderView(0).findViewById(R.id.img_headerAvatar);
        txvHeaderView.setText(getResources().getString(R.string.welcomeBack) + userName);
        if (bitmap_avatar != null) {
            img_headerAvatar.setImageBitmap(bitmap_avatar);
        }


        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        Util.setDrawer( ContactPageActivity.this, drawerLayout, toolbar, R.layout.drawer_header, userName, navigationView);
        //----------抽屜動作----------//
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawer(GravityCompat.START);//選單按完之後收起抽屜

                switch (item.getItemId()) {
                    //電子名片
                    case R.id.my_eMess:
                        startActivity(new Intent(ContactPageActivity.this,E_MessActivity.class));
                        break;

                    //全部清單
                    case R.id.allContact:
                        showFrag(new ContactPageFragment());

                        break;
                    //常用清單
                    case R.id.favorContact:
                        showFrag(new FavorListFragment());
                        break;
                    //更多設定
                    case R.id.settings:
                        startActivity(new Intent(ContactPageActivity.this, SettingPageActivity.class));
                        break;
                }
                return true;
            }
        });
        //--------工具列設定-------//
        toolbar.inflateMenu(R.menu.toolbar_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
//                    case R.id.toolbar_add:
//                        showFrag(new AddContactFragment());
//                        break;

                    case R.id.toolbar_allContact://所有聯絡人
                        showFrag(new ContactPageFragment());
                        break;

                    case R.id.toolbar_favor://最愛清單
                        showFrag(new FavorListFragment());
                        break;
                }

                return true;
            }
        });
    }

    //顯示分頁的方法
    private void showFrag(android.support.v4.app.Fragment fragment) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out);
        transaction.replace(R.id.frameLayout, fragment);
        transaction.commit();
    }

}
