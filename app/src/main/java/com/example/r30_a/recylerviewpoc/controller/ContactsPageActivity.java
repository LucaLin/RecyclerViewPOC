package com.example.r30_a.recylerviewpoc.controller;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.r30_a.recylerviewpoc.R;
import com.example.r30_a.recylerviewpoc.adapter.MyAdapter;
import com.example.r30_a.recylerviewpoc.model.ContactData;
import com.example.r30_a.recylerviewpoc.util.CommonUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import static com.example.r30_a.recylerviewpoc.util.CommonUtil.isCellPhoneNumber;

public class ContactsPageActivity extends AppCompatActivity {

    Toast toast;
    ArrayList<ContactData> myContactList;//聯絡人清單表
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
    RecyclerView contact_RecyclerView;
    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_page);
        initView();

        adapter = new MyAdapter(this,getContactList(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,phoneNumberProjection));

        contact_RecyclerView.setLayoutManager(new LinearLayoutManager(this));//設定排版樣式
        contact_RecyclerView.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT){
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

                return false;

            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {


            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
                itemTouchHelper.attachToRecyclerView(contact_RecyclerView);



    }

    @Override
    protected void onResume() {
        super.onResume();

        if(myContactList != null && myContactList.size()>0){

        }
    }

    private void initView() {
        resolver = this.getContentResolver();
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        commonUtil = new CommonUtil();
        contact_RecyclerView = (RecyclerView)findViewById(R.id.contact_RecyclerView);


    }


    public ArrayList<ContactData> getContactList( Uri uri, String[] projecction){
        myContactList = new ArrayList<>();
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
                        addContactToList(id,mobileNum,name, get_Avatar(resolver,id), myContactList);
                    }
                }
                cursor.close();
                return myContactList;
            } else {
                toast.setText(R.string.noData);
                toast.show();
                return myContactList;
            }
        }catch (Exception e){
            e.getMessage();
        }
        return myContactList;


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


}
