package com.example.r30_a.recylerviewpoc.controller;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.widget.SearchView;
import android.support.v7.widget.Toolbar;

import com.example.r30_a.recylerviewpoc.R;
import com.example.r30_a.recylerviewpoc.adapter.MyAdapter;
import com.example.r30_a.recylerviewpoc.helper.MyDBHelper;
import com.example.r30_a.recylerviewpoc.model.ContactData;
import com.example.r30_a.recylerviewpoc.util.CommonUtil;
import com.google.gson.JsonObject;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FavorListPageActivity extends AppCompatActivity {

    JsonObject jsonObject;
    MyDBHelper myDBHelper;
    SQLiteDatabase db;
    ArrayList<ContactData> favorList;
    SwipeMenuRecyclerView contact_RecyclerView;
    MyAdapter adapter;
    SearchView searchView;
    private DrawerLayout drawerLayout;//側邊選單
    private NavigationView navigationView;
    private Toolbar toolbar;
    View headerView;

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

    }

}
