package com.example.r30_a.recylerviewpoc.controller;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.r30_a.recylerviewpoc.R;
import com.example.r30_a.recylerviewpoc.adapter.MyAdapter;
import com.example.r30_a.recylerviewpoc.model.ContactData;
import com.example.r30_a.recylerviewpoc.util.CommonUtil;

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
            ContactsContract.Contacts.DISPLAY_NAME};
    String tempId = "";//聯絡人id的暫存
    public static final int REQUEST_CODE = 1;
    private CommonUtil commonUtil;
    RecyclerView contact_RecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_page);
        initView();

        MyAdapter adapter = new MyAdapter(this,getContactList(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,phoneNumberProjection));

        contact_RecyclerView.setLayoutManager(new LinearLayoutManager(this));//設定排版樣式
        contact_RecyclerView.setAdapter(adapter);


    }

    private void initView() {
        resolver = this.getContentResolver();
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        commonUtil = new CommonUtil();
        contact_RecyclerView = (RecyclerView)findViewById(R.id.contact_RecyclerView);
    }


    public ArrayList<ContactData> getContactList(Uri uri, String[] projecction){
        myContactList = new ArrayList<>();
        try {

            String name;
            String mobileNum;
            String format_mobileNum = "";

            cursor = resolver.query(uri, projecction, null, null, null);

            //直接取contacts中的號碼資料區，再從號碼欄去抓對應的name跟number
            if (cursor != null) {
                while (cursor != null && cursor.moveToNext()) {
                    //抓取id用來判別是否有重覆資料抓取

                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
//                    if (true) {
//                        name = cursor.getString(0);
//                        mobileNum = cursor.getString(0);
//                    } else {
                        name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        mobileNum = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                    }
                    if (!TextUtils.isEmpty(mobileNum) && !isCellPhoneNumber(mobileNum)) {
                        continue;
                    } else {
                        addContactToList(id,mobileNum,format_mobileNum,name,myContactList);
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


    /*新增聯絡人到手機清單*/
    private void addContactToList(String id, String phoneNumber, String formatPhoneNum, String name, ArrayList list) {
        formatPhoneNum = commonUtil.getFormatPhone(phoneNumber);

        if (!tempId.equals(id)) {
            contactData = new ContactData();

            contactData.setId(id);
            contactData.setName(name);
            contactData.setPhoneNum(formatPhoneNum);

            tempId = id;
            list.add(contactData);
        }
    }
}
