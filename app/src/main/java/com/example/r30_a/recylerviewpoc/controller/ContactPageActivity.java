package com.example.r30_a.recylerviewpoc.controller;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.r30_a.recylerviewpoc.R;
import com.example.r30_a.recylerviewpoc.fragment.AddContactFragment;
import com.example.r30_a.recylerviewpoc.fragment.ContactPageFragment;
import com.example.r30_a.recylerviewpoc.fragment.FavorListFragment;
import com.example.r30_a.recylerviewpoc.util.CommonUtil;
import com.example.r30_a.recylerviewpoc.view.SideBar;


public class ContactPageActivity extends AppCompatActivity {
    //------抽屜元件--------//
    private DrawerLayout drawerLayout;//側邊選單
    private NavigationView navigationView;
    private Toolbar toolbar;
    private SideBar sideBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_page);
        showFrag(new ContactPageFragment());

//        sideBar = new SideBar(this);
        //----------抽屜設定-----------//
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        navigationView = (NavigationView)findViewById(R.id.navigationView);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        CommonUtil.setDrawer(ContactPageActivity.this,drawerLayout,toolbar,R.layout.drawer_header,navigationView);
        //----------抽屜動作----------//
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawer(GravityCompat.START);//選單按完之後收起抽屜

                switch (item.getItemId()){
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
                        startActivity(new Intent(ContactPageActivity.this,SettingPageActivity.class));
                        break;
                }
                return true;
            }
        });
        toolbar.inflateMenu(R.menu.toolbar_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.toolbar_add:
                        showFrag(new AddContactFragment());
                        break;

                    case R.id.toolbar_allContact:
                        showFrag(new ContactPageFragment());

                        break;

                    case R.id.toolbar_favor:
                        showFrag(new FavorListFragment());
                        break;
                }

                return true;
            }
        });


    }

    private void showFrag(android.support.v4.app.Fragment fragment) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_right_in,R.anim.slide_left_out,R.anim.slide_left_in,R.anim.slide_right_out);
        transaction.replace(R.id.frameLayout,fragment);
        transaction.commit();
    }
    public static OnsearchViewListener onsearchViewListener;

    public static void setOnsearchViewListener(OnsearchViewListener listener){
        onsearchViewListener = listener;
    }

    public interface OnsearchViewListener{
        void startSearch();
    }
}
