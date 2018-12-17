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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
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
    RecyclerView contact_RecyclerView;
    MyAdapter adapter;
    EditText edt_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_page);
        initView();

        myContactList = getContactList(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,phoneNumberProjection);
        setContactList(contact_RecyclerView,adapter,myContactList);

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

    private void setContactList(RecyclerView recyclerView, MyAdapter adapter, ArrayList<ContactData> list) {

        adapter = new MyAdapter(this,list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));//設定排版樣式
        recyclerView.setAdapter(adapter);

    }



    @Override
    protected void onResume() {
        super.onResume();

        if(myContactList != null && myContactList.size()>0){
            setContactList(contact_RecyclerView,adapter,myContactList);
        }
    }

    private void initView() {
        resolver = this.getContentResolver();
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        commonUtil = new CommonUtil();
        myContactList = new ArrayList<>();
        contact_RecyclerView = (RecyclerView)findViewById(R.id.contact_RecyclerView);
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



}
