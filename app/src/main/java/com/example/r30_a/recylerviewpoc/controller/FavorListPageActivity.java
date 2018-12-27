package com.example.r30_a.recylerviewpoc.controller;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.support.v7.widget.Toolbar;

import com.example.r30_a.recylerviewpoc.R;
import com.example.r30_a.recylerviewpoc.adapter.MyAdapter;
import com.example.r30_a.recylerviewpoc.adapter.MyDecoration;
import com.example.r30_a.recylerviewpoc.helper.MyDBHelper;
import com.example.r30_a.recylerviewpoc.model.ContactData;
import com.example.r30_a.recylerviewpoc.util.CommonUtil;
import com.google.gson.JsonObject;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FavorListPageActivity extends AppCompatActivity {


    MyDBHelper myDBHelper;
    SQLiteDatabase db;
    ArrayList<ContactData> favorList = new ArrayList<>();
    ArrayList<ContactData> searchList = new ArrayList<>();
    SwipeMenuRecyclerView contact_RecyclerView;
    MyAdapter adapter;
    SearchView searchView;
    private DrawerLayout drawerLayout;//側邊選單
    private NavigationView navigationView;
    private Toolbar toolbar;
    View headerView;
    MyDecoration decoration;

    private RecyclerView.ItemDecoration itemDecoration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favor_list_page);
        initView();

    }

    private void initView() {

        myDBHelper = MyDBHelper.getInstance(this);
        db = myDBHelper.getReadableDatabase();
        favorList = new ArrayList<>();
//        decoration = new MyDecoration(this, new MyDecoration.DecorationCallBack() {
//            @Override
//            public long getGroupId(int pos) {
//                return Character.toUpperCase(favorList.get(pos).getName().charAt(0));
//            }
//
//            @Override
//            public String getGroupFirstLine(int pos) {
//                return favorList.get(pos).getName().substring(0,1).toUpperCase();
//            }
//        });



        contact_RecyclerView = (SwipeMenuRecyclerView)findViewById(R.id.contact_RecyclerView);
        navigationView = (NavigationView)findViewById(R.id.navigationView);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        searchView = (SearchView)findViewById(R.id.searchView);
        toolbar = (Toolbar)findViewById(R.id.toolbar);

        CommonUtil.setDrawer(FavorListPageActivity.this,drawerLayout,toolbar,R.layout.drawer_header,navigationView);

        Cursor cursor = myDBHelper.getReadableDatabase().query(MyDBHelper.TABLE_NAME,
                null,null,null,null,null, null);

        if(cursor.getCount() != 0){
            while (cursor.moveToNext()){
                ContactData  data = new ContactData();
                data.setNumber( Integer.parseInt(cursor.getString(cursor.getColumnIndex(MyDBHelper.NUMBER))));
                data.setName(cursor.getString(cursor.getColumnIndex(MyDBHelper.NAME)));
                data.setPhoneNum(cursor.getString(cursor.getColumnIndex(MyDBHelper.PHONE_NUMBER)));

                String avatar_base64 = cursor.getString(cursor.getColumnIndex(MyDBHelper.IMG_AVATAR));
                if(!avatar_base64.equals("")){
                byte[] bytes = Base64.decode(avatar_base64,Base64.DEFAULT);
                Bitmap img_avatar = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                data.setImg_avatar(img_avatar);
                }

                favorList.add(data);
            }

        }
        adapter = new MyAdapter(this,favorList);
        CommonUtil.setContactList(FavorListPageActivity.this,contact_RecyclerView,adapter,favorList);

        //contact_RecyclerView.addItemDecoration(decoration);
        contact_RecyclerView.setSwipeMenuCreator(new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
                //建立右菜單刪除按鈕
                SwipeMenuItem delete_item = CommonUtil.setMenuItem(FavorListPageActivity.this,200,300,R.drawable.icons8_trash_48,16, Color.parseColor("#dd0000"));
                swipeRightMenu.addMenuItem(delete_item);
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawer(GravityCompat.START);//選單按完之後收起抽屜

                switch (item.getItemId()){
                    case R.id.allContact:
                        finish();
                        break;

                    case R.id.favorContact:break;

                    case R.id.settings:
                        startActivity(new Intent(FavorListPageActivity.this,SettingPageActivity.class));
                        break;
                }
                return true;
            }
        });

        searchView = (SearchView)findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {return false;}
            @Override
            public boolean onQueryTextChange(String newText) {
                //根據搜尋結果顯示欲搜尋資料
                if(newText.length()>0){
                    contact_RecyclerView.removeItemDecoration(itemDecoration);
                    searchList.clear();
                    for(int i = 0;i< favorList.size();i++){
                        String num = favorList.get(i).getPhoneNum().substring(0,newText.length());
                        String name = favorList.get(i).getName();
                        if(num.equals(newText) || (name.length() >= newText.length() &&
                                name.substring(0,newText.length()).equals(newText))  ){
                            searchList.add(favorList.get(i));
                        }
                        CommonUtil.setContactList(FavorListPageActivity.this,contact_RecyclerView,adapter,searchList);
                    }
                }else {
                    contact_RecyclerView.addItemDecoration(itemDecoration);
                    searchList.clear();
                    CommonUtil.setContactList(FavorListPageActivity.this,contact_RecyclerView, adapter, favorList);
                }
                return true;
            }
        });

    }

}
