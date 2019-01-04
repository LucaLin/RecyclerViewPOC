package com.example.r30_a.recylerviewpoc.controller;

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
import android.util.Base64;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.r30_a.recylerviewpoc.R;
import com.example.r30_a.recylerviewpoc.fragment.DetailPageFragment;
import com.example.r30_a.recylerviewpoc.fragment.UpdateContactFragment;
import com.example.r30_a.recylerviewpoc.helper.MyFavorDBHelper;
import com.example.r30_a.recylerviewpoc.util.CommonUtil;

public class DetailPageActivity extends AppCompatActivity {

    String name,number,phoneNumber,note;
    long id;

    private DrawerLayout drawerLayout;//側邊選單
    private NavigationView navigationView;
    private Toolbar toolbar;
    Context context;
    Toast toast;
    SharedPreferences sp;
    MyFavorDBHelper myFavorDBHelper;
    byte[] bytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_page);

        initView();
    }

    private void initView() {
        myFavorDBHelper = MyFavorDBHelper.getInstance(this);

        context = DetailPageActivity.this;
        toast = Toast.makeText(context,"",Toast.LENGTH_SHORT);
        final Intent intent = getIntent();
        id = intent.getLongExtra("id",0);
        name = intent.getStringExtra("name");
        number = String.valueOf(intent.getIntExtra("number",0));
        phoneNumber = intent.getStringExtra("phoneNumber");
        bytes = intent.getByteArrayExtra("avatar");
        note = intent.getStringExtra("note");

        Fragment fragment = DetailPageFragment.newInstance(String.valueOf(id),number,name,phoneNumber,bytes,note);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout,fragment);
        transaction.commit();

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
                        finish();
                        break;

                    case R.id.favorContact:

                        finish();
                        break;

                    case R.id.settings:
                        startActivity(new Intent(context,SettingPageActivity.class));
                        break;
                }
                return true;
            }
        });
        sp = getSharedPreferences("favorTags",MODE_PRIVATE);
        CommonUtil.favorIdSet = sp.getStringSet("favorTags",null);

        toolbar.inflateMenu(R.menu.detail_menu);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()){
                    case R.id.p1_update:
                        Fragment fragment = UpdateContactFragment.newInstance(String.valueOf(id),name,phoneNumber,bytes,note);
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frameLayout,fragment);
                        transaction.commit();

                        break;
                    case R.id.p2_addFavor:
                        if(!CommonUtil.favorIdSet.contains(String.valueOf(id))) {
                            ContentValues values = new ContentValues(5);

                            values.put(MyFavorDBHelper.NUMBER,number);
                            values.put(MyFavorDBHelper.NAME,name);
                            values.put(MyFavorDBHelper.PHONE_NUMBER,phoneNumber);
                            if(bytes != null && bytes.length >0){
                                String img_base64 = Base64.encodeToString(bytes,Base64.DEFAULT);
                                values.put(MyFavorDBHelper.IMG_AVATAR,img_base64);
                            } else {
                                values.put(MyFavorDBHelper.IMG_AVATAR,"");
                            }
                            myFavorDBHelper.getWritableDatabase().insert(MyFavorDBHelper.TABLE_NAME,null,values);
                            CommonUtil.isDataChanged = true;
                            CommonUtil.favorIdSet.add(String.valueOf(id));
                            toast.setText(R.string.favorDone);toast.show();
                            finish();

                        }else {
                            toast.setText(R.string.alreadyaddfavor);toast.show();
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
                                                    CommonUtil.phoneNumberProjection,
                                                    "contact_id =?",
                                                    new String[]{String.valueOf(id)},
                                                    null);
                                            if (c.moveToFirst()) {

                                                getContentResolver().delete(ContactsContract.RawContacts.CONTENT_URI, "contact_id =?", new String[]{String.valueOf(id)});
                                                toast.setText(R.string.deleteOK);
                                                toast.show();
                                                CommonUtil.isDataChanged = true;
                                                finish();
                                            }
                                        } catch (Exception e) {
                                            e.getMessage();
                                        }
                                    }
                                })
                                .setNegativeButton(R.string.no,null)
                                .show();

                        break;
                }

                return true;
            }
        });
    }
}
