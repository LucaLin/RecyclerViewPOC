package com.example.r30_a.recylerviewpoc.controller;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import android.widget.Toast;

import com.example.r30_a.recylerviewpoc.R;
import com.example.r30_a.recylerviewpoc.adapter.MyAdapter;
import com.example.r30_a.recylerviewpoc.model.ContactData;
import com.example.r30_a.recylerviewpoc.util.CommonUtil;

import com.yanzhenjie.recyclerview.swipe.Closeable;
import com.yanzhenjie.recyclerview.swipe.OnSwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import static com.example.r30_a.recylerviewpoc.util.CommonUtil.isCellPhoneNumber;

public class ContactsPageActivity extends AppCompatActivity{

    Toast toast;
    ArrayList<ContactData> myContactList;
    ArrayList<ContactData> tempList;//聯絡人清單表
    ArrayList<ContactData> searchList ;

    private Cursor cursor;//搜尋資料的游標
    private ContactData contactData;//用來儲存資料的物件
    private ContentResolver resolver;
    public static final Uri SIM_URI = Uri.parse("content://icc/adn");//讀取sim卡資料的uri string
    String[] phoneNumberProjection = new String[]{//欲搜尋的欄位區塊
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Photo.PHOTO_ID
            };
    String tempId = "";//聯絡人id的暫存
    public static final int REQUEST_CODE = 1;
    private CommonUtil commonUtil;
//    RecyclerView contact_RecyclerView;
    SwipeMenuRecyclerView contact_RecyclerView;
    MyAdapter adapter;
    EditText edt_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_page);
        initView();

        myContactList = getContactList(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,phoneNumberProjection);
        setContactList(contact_RecyclerView,adapter,myContactList);

        contact_RecyclerView.setSwipeMenuCreator(new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {



                SwipeMenuItem update_item = new SwipeMenuItem(ContactsPageActivity.this);
                setMenuItem(update_item, 240,240,R.string.update,16, Color.parseColor("#00FF00"));
                SwipeMenuItem delete_item = new SwipeMenuItem(ContactsPageActivity.this);
                setMenuItem(delete_item,240,240,R.string.delete,16,Color.parseColor("#FF0000"));


                swipeRightMenu.addMenuItem(update_item);
                swipeRightMenu.addMenuItem(delete_item);


            }
        });



        contact_RecyclerView.setSwipeMenuItemClickListener(new OnSwipeMenuItemClickListener() {
            @Override
            public void onItemClick(Closeable closeable, int adapterPosition, int menuPosition, int direction) {

                switch (menuPosition){
                    //更新
                    case 0:
                        Intent intent = new Intent();
                        intent.putExtra("id",String.valueOf(myContactList.get(adapterPosition).getId()));
                        intent.putExtra("name", myContactList.get(adapterPosition).getName());
                        intent.putExtra("phone", myContactList.get(adapterPosition).getPhoneNum());
                        intent.setClass(ContactsPageActivity.this, UpdateDataActivity.class);
                        startActivityForResult(intent, ContactsPageActivity.REQUEST_CODE);
                        break;
                    //刪除
                    case 1:
                        deleteContact(myContactList.get(adapterPosition).getId(),phoneNumberProjection);
                        break;


                }
            }
        });

    }

    private SwipeMenuItem setMenuItem(SwipeMenuItem item, int width, int height, int text_resID, int textSize, int color) {

        item.setWidth(width)
                .setHeight(height)
                .setText(text_resID)
                .setTextSize(textSize)
                .setBackgroundColor(color);

        return item;
    }


    @Override
    protected void onResume() {
        super.onResume();

//           setContactList(contact_RecyclerView,adapter,getContactList(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,phoneNumberProjection));
        adapter = new MyAdapter(this,getContactList(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,phoneNumberProjection));
        contact_RecyclerView.setLayoutManager(new LinearLayoutManager(this));
        contact_RecyclerView.setAdapter(adapter);

    }

    private void initView() {
        resolver = this.getContentResolver();
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        commonUtil = new CommonUtil();
        myContactList = new ArrayList<>();
//        contact_RecyclerView = (RecyclerView) findViewById(R.id.contact_RecyclerView);
        contact_RecyclerView = (SwipeMenuRecyclerView) findViewById(R.id.contact_RecyclerView);
        edt_search = (EditText)findViewById(R.id.edt_search);
        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(start > 1){
                    searchList = new ArrayList<>();
                    for(int i = 0;i< myContactList.size();i++){
                        String num = myContactList.get(i).getPhoneNum().substring(0,start+1);
                        if(num.equals(String.valueOf(s))){
                            searchList.add(myContactList.get(i));

                        }
                        setContactList(contact_RecyclerView,adapter,searchList);
                    }
                }else {
                    setContactList(contact_RecyclerView, adapter, myContactList);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


    }


    public ArrayList<ContactData> getContactList( Uri uri, String[] projecction){
        tempList = new ArrayList<>();
        try {

            String name;
            String mobileNum;

            cursor = resolver.query(uri, projecction, null, null, null);

            //直接取contacts中的號碼資料區，再從號碼欄去抓對應的name跟number
            if (cursor != null) {
                while (cursor != null && cursor.moveToNext()) {
                    //抓取id用來判別是否有重覆資料抓取

                    long id = cursor.getLong(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));

                        name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        mobileNum = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));


                    if (!TextUtils.isEmpty(mobileNum) && !isCellPhoneNumber(mobileNum)) {
                        continue;
                    } else {
                        addContactToList(id,mobileNum,name, get_Avatar(resolver,id), tempList);
                    }
                }
                cursor.close();
                return tempList;
            } else {
                toast.setText(R.string.noData);
                toast.show();
                return tempList;
            }
        }catch (Exception e){
            e.getMessage();
        }
        return tempList;


    }

    public static Bitmap get_Avatar(ContentResolver resolver, long contact_ID){
        Bitmap bitmap = null;

        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,contact_ID);
        Uri phontUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = resolver.query(phontUri,new String[]{ContactsContract.Contacts.Photo.PHOTO},null,null,null);

        if(cursor == null){
            return null;
        }
        try {
            if(cursor.moveToNext()){
                byte[] data = cursor.getBlob(0);
                if(data != null){
                    InputStream inputStream = new ByteArrayInputStream(data);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                }
            }
        }finally {
            cursor.close();
        }
        return bitmap;

    }

    /*新增聯絡人到手機清單*/
    private void addContactToList(long id, String phoneNumber, String name, Bitmap avatar, ArrayList list) {

        if (!tempId.equals(String.valueOf(id))) {

            contactData = new ContactData();

            contactData.setId(id);
            contactData.setName(name);
            contactData.setPhoneNum(commonUtil.getFormatPhone(phoneNumber));

            if(avatar != null){
            contactData.setImg_avatar(avatar);
            }

            tempId = String.valueOf(id);
            list.add(contactData);
        }

    }


    //更新通訊錄清單的方法
    private void setContactList(RecyclerView recyclerView, MyAdapter adapter, ArrayList<ContactData> list) {

        adapter = new MyAdapter(this,list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));//設定排版樣式
        recyclerView.setAdapter(adapter);
    }

    /*刪除聯絡人*/
    private void deleteContact(long id, String[] projection) {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED){
            try {

                //使用id來找原始資料
                Cursor c = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        projection,
                        "contact_id =?",
                        new String[]{String.valueOf(id)},
                        null);
                if (c.moveToFirst()) {
                    resolver.delete(ContactsContract.RawContacts.CONTENT_URI, "contact_id =?", new String[]{String.valueOf(id)});
                    toast.setText(R.string.deleteOK);
                    toast.show();
                    setContactList(contact_RecyclerView,adapter,getContactList(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,phoneNumberProjection));

                }
            } catch (Exception e) {
                e.getMessage();
            }
        }else {
            toast.setText(R.string.permissonRequest);toast.show();
        }
    }

    //取回更新後的資料，做更新聯絡人的處理
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE){
            if(resultCode == RESULT_OK){
                String contact_id = data.getStringExtra("id");
                String updateName = data.getStringExtra("Name");
                String updatePhone = data.getStringExtra("Phone");
                String oldName = data.getStringExtra("oldName");

                Cursor c = resolver.query(ContactsContract.Data.CONTENT_URI,
                        new String[]{ContactsContract.Data.RAW_CONTACT_ID},
                        ContactsContract.Contacts.DISPLAY_NAME + " =?",
                        new String[]{ oldName },null);

                c.moveToFirst();
                String raw_contact_id = c.getString(c.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID));
                c.close();

                try{

                    ContentValues values = new ContentValues();
                    values.put(ContactsContract.CommonDataKinds.Phone.NUMBER,updatePhone);
                    values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
                    resolver.update(ContactsContract.Data.CONTENT_URI,
                            values,
                            ContactsContract.Data.RAW_CONTACT_ID+" =?" +" AND "+ ContactsContract.Data.MIMETYPE + " =?" ,
                            new String[]{raw_contact_id, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE});

                    values = new ContentValues();
                    values.put(ContactsContract.Contacts.DISPLAY_NAME,updateName);
                    resolver.update(
                            ContactsContract.RawContacts.CONTENT_URI,
                            values, ContactsContract.Data.CONTACT_ID+" =?",
                            new String[]{contact_id});

                }catch (Exception e){
                    e.getMessage();
                }
            }
        }

    }



}
