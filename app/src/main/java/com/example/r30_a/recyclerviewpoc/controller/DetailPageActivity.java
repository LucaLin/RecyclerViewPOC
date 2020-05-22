package com.example.r30_a.recyclerviewpoc.controller;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.r30_a.recyclerviewpoc.R;
import com.example.r30_a.recyclerviewpoc.fragment.DetailPageFragment;
import com.example.r30_a.recyclerviewpoc.fragment.FavorListFragment;
import com.example.r30_a.recyclerviewpoc.fragment.UpdateContactFragment;
import com.example.r30_a.recyclerviewpoc.helper.MyDBHelper;

import com.example.r30_a.recyclerviewpoc.util.Util;

public class DetailPageActivity extends AppCompatActivity {

    String name;
    String number;
    String phoneNumber;
    String note;
    String city;
    String street;
    String email_home, email_company, email_other, email_custom;
    long id;

    private DrawerLayout drawerLayout;//側邊選單
    private NavigationView navigationView;
    private Toolbar toolbar;
    Context context;
    Toast toast;
    SharedPreferences sp;

    MyDBHelper myDBHelper;
    byte[] bytes;

    SharedPreferences sf;
    String userName;
    ImageView img_headerAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_page);
        initView();
    }

    private void initView() {

        findViewAndGetData();

        Fragment fragment = DetailPageFragment.newInstance(String.valueOf(id), number, name, phoneNumber, bytes, note, city, street,
                email_home, email_company, email_other, email_custom);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.commit();

        Util.setDrawer(this, drawerLayout, toolbar, R.layout.drawer_header, userName, navigationView);
    }

    private void findViewAndGetData() {
//        DB相關與sf
        context = DetailPageActivity.this;
        toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        myDBHelper = MyDBHelper.getInstance(this);
        sf = getSharedPreferences("profile", MODE_PRIVATE);
        sp = getSharedPreferences("favorTags", MODE_PRIVATE);

        toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.detail_menu);
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);

        userName = sf.getString("name", "");
        Util.favorIdSet = sp.getStringSet("favorTags", null);
//        get Intent data

        final Intent intent = getIntent();
        id = intent.getLongExtra("id", 0);
        name = intent.getStringExtra("name");
        number = String.valueOf(intent.getIntExtra("number", 0));
        phoneNumber = intent.getStringExtra("phoneNumber");
        bytes = intent.getByteArrayExtra("avatar");
        note = intent.getStringExtra("note");
        city = intent.getStringExtra("city");
        street = intent.getStringExtra("street");

        email_home = intent.getStringExtra("email_home");
        email_company = intent.getStringExtra("email_company");
        email_other = intent.getStringExtra("email_other");
        email_custom = intent.getStringExtra("email_custom");

//      findView and setView
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(navigationItemSelectedListener);
        drawerLayout = findViewById(R.id.drawerLayout);

        TextView txv_header = navigationView.getHeaderView(0).findViewById(R.id.txvHeaderTitle);
        img_headerAvatar = navigationView.getHeaderView(0).findViewById(R.id.img_headerAvatar);
        txv_header.setText(getResources().getString(R.string.welcomeBack) + userName);

        img_headerAvatar.setImageBitmap(Util.getBitmap_avatar(sf.getString("avatar", "")));

    }

    public NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            drawerLayout.closeDrawer(GravityCompat.START);//選單按完之後收起抽屜

            switch (item.getItemId()) {
                case R.id.allContact:
                    finish();
                    break;

                case R.id.favorContact:
                    Fragment favorFragment = new FavorListFragment();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frameLayout, favorFragment);
                    transaction.commit();
                    break;

                case R.id.settings:
                    startActivity(new Intent(context, SettingPageActivity.class));
                    break;
            }
            return true;
        }
    };

    public Toolbar.OnMenuItemClickListener onMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.p1_update:
                    Fragment fragment = UpdateContactFragment.newInstance(String.valueOf(id), name, phoneNumber, bytes, note, city, street,
                            email_home, email_company, email_other, email_custom);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frameLayout, fragment);
                    transaction.commit();

                    break;
                case R.id.p2_addFavor:
                    if (!Util.favorIdSet.contains(String.valueOf(id))) {

                        ContentValues values = new ContentValues();
                        values.put(MyDBHelper.FAVOR_TAG, 1);
                        myDBHelper.getWritableDatabase().update(MyDBHelper.TABLE_NAME, values,
                                MyDBHelper.CONTACT_ID + "=?", new String[]{String.valueOf(id)});

                        Util.favorIdSet.add(String.valueOf(id));
                        toast.setText(R.string.favorDone);
                        toast.show();
                        finish();

                    } else {
                        toast.setText(R.string.alreadyaddfavor);
                        toast.show();
                    }
                    break;
                case R.id.p3_delete:
                    AlertDialog.Builder builder = new AlertDialog.Builder(DetailPageActivity.this);
                    builder.setTitle(R.string.attention)
                            .setMessage(R.string.deleteOrNot)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        //使用id來找原始資料
                                        Cursor c = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                                Util.phoneNumberProjection,
                                                "contact_id =?",
                                                new String[]{String.valueOf(id)},
                                                null);
                                        if (c.moveToFirst()) {

                                            getContentResolver().delete(ContactsContract.RawContacts.CONTENT_URI, "contact_id =?", new String[]{String.valueOf(id)});
                                            toast.setText(R.string.deleteOK);
                                            toast.show();
                                            finish();
                                        }
                                    } catch (Exception e) {
                                        e.getMessage();
                                    }
                                }
                            })
                            .setNegativeButton(R.string.no, null)
                            .show();

                    break;
            }
            return true;
        }
    };

}
