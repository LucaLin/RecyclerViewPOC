package com.example.r30_a.recylerviewpoc.controller;

import android.Manifest;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.text.TextUtils;

import android.widget.EditText;

import android.widget.SearchView;
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
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.RawContacts;

import static com.example.r30_a.recylerviewpoc.util.CommonUtil.isCellPhoneNumber;

public class ContactsPageActivity extends AppCompatActivity{

    Toast toast;
    ArrayList<ContactData> myContactList;
    ArrayList<ContactData> tempList;
    ArrayList<ContactData> Now_ContactList = new ArrayList<>();
    ArrayList<ContactData> searchList = new ArrayList<>();
    int number=0;

    private Cursor cursor;//搜尋資料的游標
    private ContactData contactData;//用來儲存資料的物件
    private ContentResolver resolver;
    public static final Uri SIM_URI = Uri.parse("content://icc/adn");//讀取sim卡資料的uri string
    String[] phoneNumberProjection = new String[]{//欲搜尋的欄位區塊
            Phone.CONTACT_ID, Phone.NUMBER,
            Phone.DISPLAY_NAME, Photo.PHOTO_ID
            };
    String tempId = "";//聯絡人id的暫存
    public static final int REQUEST_CODE = 1;
    private CommonUtil commonUtil;
    SwipeMenuRecyclerView contact_RecyclerView;
    MyAdapter adapter;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_page);
        initView();

        myContactList = getContactList(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,phoneNumberProjection);
        contact_RecyclerView.setSwipeMenuCreator(new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {


                //建立右菜單更新按鈕
                SwipeMenuItem update_item = new SwipeMenuItem(ContactsPageActivity.this);
                setMenuItem(update_item, 240,240,R.string.update,16, Color.parseColor("#00dd00"));
                //建立右菜單刪除按鈕
                SwipeMenuItem delete_item = new SwipeMenuItem(ContactsPageActivity.this);
                setMenuItem(delete_item,240,240,R.string.delete,16,Color.parseColor("#dd0000"));
                //建立左菜單通話按鈕
                SwipeMenuItem dial_item = new SwipeMenuItem(ContactsPageActivity.this);
                setMenuItem(dial_item, 240, 240, R.string.dial,16,Color.parseColor("#00dd00"));
                //建立左菜單簡訊按鈕
                SwipeMenuItem sms_item = new SwipeMenuItem(ContactsPageActivity.this);
                setMenuItem(sms_item,240,240, R.string.smsto, 16, Color.parseColor("#dddddd"));

                swipeRightMenu.addMenuItem(update_item);
                swipeRightMenu.addMenuItem(delete_item);
                swipeLeftMenu.addMenuItem(dial_item);
                swipeLeftMenu.addMenuItem(sms_item);

            }
        });

        contact_RecyclerView.setSwipeMenuItemClickListener(new OnSwipeMenuItemClickListener() {
            @Override
            public void onItemClick(Closeable closeable, int adapterPosition, int menuPosition, int direction) {


                if(direction == -1){//向右滑動
                switch (menuPosition){
                    //更新
                    case 0:
                        Intent intent = new Intent();
                        if(searchList.size()>0){
                            intent.putExtra("id",String.valueOf(searchList.get(adapterPosition).getId()));
                            intent.putExtra("name", searchList.get(adapterPosition).getName());
                            intent.putExtra("phone", searchList.get(adapterPosition).getPhoneNum());
                            intent.putExtra("avatar", searchList.get(adapterPosition).getImg_avatar());
                        }else {
                            intent.putExtra("id",String.valueOf(Now_ContactList.get(adapterPosition).getId()));
                            intent.putExtra("name", Now_ContactList.get(adapterPosition).getName());
                            intent.putExtra("phone", Now_ContactList.get(adapterPosition).getPhoneNum());
                            intent.putExtra("avatar", Now_ContactList.get(adapterPosition).getImg_avatar());
                        }
                        intent.setClass(ContactsPageActivity.this, UpdateDataActivity.class);
                        startActivityForResult(intent, ContactsPageActivity.REQUEST_CODE);

                        break;
                    //刪除
                    case 1:
                        deleteContact(myContactList.get(adapterPosition).getId(),phoneNumberProjection);
                        break;


                    }
                }else {
                    switch (menuPosition){//向左滑動
                        //通話
                        case 0:
                            Intent intent_dial = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + myContactList.get(adapterPosition).getPhoneNum()));
                            if (ActivityCompat.checkSelfPermission(ContactsPageActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            startActivity(intent_dial);
                            break;
                        //簡訊
                        case 1:
                            Intent intent_sms = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:"+ myContactList.get(adapterPosition).getPhoneNum()));
                        if (ActivityCompat.checkSelfPermission(ContactsPageActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        startActivity(intent_sms);
                            break;

                    }

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
        searchList.clear();
        if(Now_ContactList != null && Now_ContactList.size()>0){
            setContactList(contact_RecyclerView,adapter,Now_ContactList);
        }else {
            adapter = new MyAdapter(this,getContactList(Phone.CONTENT_URI,phoneNumberProjection));
            contact_RecyclerView.setLayoutManager(new LinearLayoutManager(this));
            contact_RecyclerView.setAdapter(adapter);
        }
    }


    private void initView() {
        resolver = this.getContentResolver();
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        commonUtil = new CommonUtil();
        myContactList = new ArrayList<>();
//        contact_RecyclerView = (RecyclerView) findViewById(R.id.contact_RecyclerView);
        contact_RecyclerView = (SwipeMenuRecyclerView) findViewById(R.id.contact_RecyclerView);

        searchView = (SearchView)findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //根據搜尋結果顯示欲搜尋資料
                if(newText.length()>0){
                    searchList.clear();
                    for(int i = 0;i< myContactList.size();i++){
                        String num = myContactList.get(i).getPhoneNum().substring(0,newText.length());
                        String name = myContactList.get(i).getName();
                        if(num.equals(newText) || (name.length() >= newText.length() &&
                                                  name.substring(0,newText.length()).equals(newText))  ){
                            searchList.add(myContactList.get(i));

                        }
                        setContactList(contact_RecyclerView,adapter,searchList);
                    }

                }else {
                    searchList.clear();
                    setContactList(contact_RecyclerView, adapter, myContactList);
                }
                return true;
            }
        });
    }


    public ArrayList<ContactData> getContactList( Uri uri, String[] projecction){
        tempList = new ArrayList<>();
        try {
            number = 0;
            String name;
            String mobileNum;


            cursor = resolver.query(uri, projecction, null, null, null);

            //直接取contacts中的號碼資料區，再從號碼欄去抓對應的name跟number
            if (cursor != null) {
                while (cursor != null && cursor.moveToNext()) {
                    //抓取id用來判別是否有重覆資料抓取

                    long id = cursor.getLong(cursor.getColumnIndex(Phone.CONTACT_ID));

                        name = cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME));
                        mobileNum = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));


                    if (!TextUtils.isEmpty(mobileNum) && !isCellPhoneNumber(mobileNum)) {
                        continue;
                    } else {
                        number = number+1;
                        addContactToList(number,id,mobileNum,name, get_Avatar(resolver,id), tempList);
                    }
                }
                cursor.close();
                Now_ContactList.addAll(tempList);
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

    //取得聯絡人大頭照資料
    public static Bitmap get_Avatar(ContentResolver resolver, long contact_ID){
        Bitmap bitmap = null;

        Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI,contact_ID);
        Uri phontUri = Uri.withAppendedPath(contactUri, Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = resolver.query(phontUri,new String[]{Photo.PHOTO},null,null,null);

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
    private void addContactToList(int number,long id, String phoneNumber, String name, Bitmap avatar, ArrayList list) {

        if (!tempId.equals(String.valueOf(id))) {

            contactData = new ContactData();

            contactData.setId(id);
            contactData.setName(name);
            contactData.setPhoneNum(commonUtil.getFormatPhone(phoneNumber));

            contactData.setNumber(number);

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
    private void deleteContact(final long id, final String[] projection) {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("注意")
                    .setMessage("此功能會永久刪除此聯絡人，確定要繼續嗎？")
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {

                                //使用id來找原始資料
                                Cursor c = resolver.query(Phone.CONTENT_URI,
                                        projection,
                                        "contact_id =?",
                                        new String[]{String.valueOf(id)},
                                        null);
                                if (c.moveToFirst()) {

                                    resolver.delete(ContactsContract.RawContacts.CONTENT_URI, "contact_id =?", new String[]{String.valueOf(id)});
                                    toast.setText(R.string.deleteOK);
                                    toast.show();
                                    setContactList(contact_RecyclerView,adapter,getContactList(Phone.CONTENT_URI,phoneNumberProjection));

                                }
                            } catch (Exception e) {
                                e.getMessage();
                            }
                            Now_ContactList.clear();

                        }
                    })
                    .setNegativeButton(R.string.no,null)
                    .show();



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

                Cursor c = resolver.query(Data.CONTENT_URI,
                        new String[]{Data.RAW_CONTACT_ID},
                        Contacts.DISPLAY_NAME + " =?",
                        new String[]{ oldName },null);

                c.moveToFirst();
                String raw_contact_id = c.getString(c.getColumnIndex(Data.RAW_CONTACT_ID));
                c.close();

                try{
                    //更新電話
                    ContentValues values = new ContentValues();
                    values.put(Phone.NUMBER,updatePhone);
                    values.put(Phone.TYPE, Phone.TYPE_MOBILE);
                    resolver.update(Data.CONTENT_URI,
                            values,
                            Data.RAW_CONTACT_ID+" =?" +" AND "+ Data.MIMETYPE + " =?" ,
                            new String[]{raw_contact_id, Phone.CONTENT_ITEM_TYPE});
                    //更新名字
                    values = new ContentValues();
                    values.put(Contacts.DISPLAY_NAME,updateName);
                    resolver.update(
                            RawContacts.CONTENT_URI,
                            values, Data.CONTACT_ID+" =?",
                            new String[]{contact_id});
                }catch (Exception e){
                    e.getMessage();
                }
            }
            Now_ContactList.clear();
        }

    }
    //圖片uri路徑轉成byte[]
    public byte[] getBytesFromUri(Uri uri){
        ContentResolver resolver = getContentResolver();
        byte[] data = null;
        try {

                InputStream inputStream = resolver.openInputStream(uri);
                byte[] buffer = new byte[1024];
                int len;
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);

                }
                data = outputStream.toByteArray();
                outputStream.close();
                inputStream.close();
                return data;





            } catch(FileNotFoundException e){
                e.printStackTrace();
            } catch(IOException e){
                e.printStackTrace();
            }
            return data;

    }




}
