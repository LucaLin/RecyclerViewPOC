package com.example.r30_a.recylerviewpoc.fragment;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.r30_a.recylerviewpoc.R;
import com.example.r30_a.recylerviewpoc.adapter.MyAdapter;
import com.example.r30_a.recylerviewpoc.adapter.MyDecoration;

import com.example.r30_a.recylerviewpoc.helper.MyContactDBHelper;

import com.example.r30_a.recylerviewpoc.model.ContactData;
import com.example.r30_a.recylerviewpoc.model.EmailData;
import com.example.r30_a.recylerviewpoc.util.CommonUtil;
import com.yanzhenjie.recyclerview.swipe.Closeable;
import com.yanzhenjie.recyclerview.swipe.OnSwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.CommonDataKinds.Note;
import static com.example.r30_a.recylerviewpoc.util.CommonUtil.isCellPhoneNumber;
import android.provider.ContactsContract.CommonDataKinds.Email;


public class ContactPageFragment extends Fragment {

    Toast toast;
    private ArrayList<ContactData> favorList = new ArrayList<>();//常用清單
    private ArrayList<ContactData> Now_ContactList = new ArrayList<>();
    private ArrayList<ContactData> searchList = new ArrayList<>();
    int number=0;

    //--------聯絡人元件-------//
    private Cursor cursor;//搜尋資料的游標
    private ContactData contactData;//用來儲存資料的物件
    private ContentResolver resolver;

    private String tempId = "";//聯絡人id的暫存
    public static final int REQUEST_CODE = 1;
    //-------聯絡人清單元件-----//
    private SwipeMenuRecyclerView contact_RecyclerView;
    private MyAdapter adapter;
    private SearchView searchView;

    private RecyclerView.ItemDecoration itemDecoration;

    MyContactDBHelper myContactDBHelper;

    SharedPreferences sp;
    private Context context;

    public ContactPageFragment() {}

    @Override
    public void onResume() {
        super.onResume();
        searchList.clear();
        CommonUtil.favorIdSet = sp.getStringSet("favorTags",new HashSet<String>());
        //資料有更新時，要更新nowlist，無更新時丟回原本的

        if(sp.getInt("listSize",0) == 0 ){//只有第一次
            setContactList(CommonUtil.ALL_CONTACTS_URI,CommonUtil.phoneNumberProjection);
        }
            Now_ContactList = getList();
            CommonUtil.setContactList(context,contact_RecyclerView,adapter, Now_ContactList);
    }

    public static ContactPageFragment newInstance() {

        ContactPageFragment fragment = new ContactPageFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        resolver = context.getContentResolver();
        myContactDBHelper = MyContactDBHelper.getInstance(context);
        sp = context.getSharedPreferences("favorTags",Context.MODE_PRIVATE);
//        CommonUtil.favorIdSet = sp.getStringSet("favorTags",new HashSet<String>());

        toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        //名稱分隔線
        itemDecoration = new MyDecoration(context, new MyDecoration.DecorationCallBack() {
            @Override
            public long getGroupId(int pos) {
                return Character.toUpperCase(Now_ContactList.get(pos).getName().charAt(0));
            }
            @Override
            public String getGroupFirstLine(int pos) {
                return Now_ContactList.get(pos).getName().substring(0,1).toUpperCase();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contact_page,container,false);
        contact_RecyclerView = (SwipeMenuRecyclerView)v.findViewById(R.id.contact_RecyclerView);
        //增加群組分類抬頭
        contact_RecyclerView.addItemDecoration(itemDecoration);
        //--------滑動菜單設定-------------//
        contact_RecyclerView.setSwipeMenuCreator(new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
                //建立右菜單更新按鈕
                SwipeMenuItem update_item = CommonUtil.setMenuItem(context, 200,240,R.drawable.icons8_restart_72,16, Color.parseColor("#00dd00"));
                //建立右菜單刪除按鈕
                SwipeMenuItem delete_item = CommonUtil.setMenuItem(context,200,240,R.drawable.icons8_trash_72,16,Color.parseColor("#dd0000"));
                //建立左菜單加入最愛按鈕
                SwipeMenuItem favor_item = CommonUtil.setMenuItem(context, 200, 240,R.drawable.icons8_heart_48,16,Color.parseColor("#00dd00"));

                swipeRightMenu.addMenuItem(update_item);
                swipeRightMenu.addMenuItem(delete_item);
                swipeLeftMenu.addMenuItem(favor_item);

            }
        });
        //------滑動菜單動作------//////
        contact_RecyclerView.setSwipeMenuItemClickListener(new OnSwipeMenuItemClickListener() {
            @Override
            public void onItemClick(Closeable closeable, final int pos, int menuPosition, int direction) {
                ContactData data = new ContactData();
                if(searchList.size()>0){
                    data = searchList.get(pos);
                }else {
                    data = Now_ContactList.get(pos);
                }
                if(direction == -1){//向右滑動
                    switch (menuPosition){
                        //更新
                        case 0:

                                Fragment fragment = UpdateContactFragment.newInstance(String.valueOf(data.getId()),data.getName(),
                                        data.getPhoneNum(),data.getImg_avatar(),
                                        data.getNote(),data.getCity(),
                                        data.getStreet(),data.getEmail_home(),
                                        data.getEmail_company(),data.getEmail_other(),
                                        data.getEmail_custom());
                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                transaction.setCustomAnimations(R.anim.slide_right_in,R.anim.slide_left_out,R.anim.slide_left_in,R.anim.slide_right_out);
                                transaction.replace(R.id.frameLayout,fragment);
                                transaction.commit();

                            break;
                        //刪除
                        case 1:
                            deleteContact(data.getId(),data.getNumber(),CommonUtil.phoneNumberProjection);
                            break;
                    }
                }else {
                    switch (menuPosition){//向左滑動
                        //加入最愛
                        case 0:
                            if(favorList != null ){
                                String id = String.valueOf(data.getId());
                                if(!CommonUtil.favorIdSet.contains(id) ){

                                    CommonUtil.favorIdSet.add(id);
                                    sp.edit().putStringSet("favorTags",CommonUtil.favorIdSet).commit();
                                    //更新當前清單
//
                                    ContentValues values = new ContentValues();
                                    values.put(MyContactDBHelper.FAVOR_TAG,1);

                                    myContactDBHelper.getWritableDatabase().update(MyContactDBHelper.TABLE_NAME,values,
                                            MyContactDBHelper.CONTACT_ID+"=?",new String[]{String.valueOf(data.getId())});
                                    Now_ContactList = getList();
                                    CommonUtil.setContactList(context,contact_RecyclerView, adapter, Now_ContactList);
                                    toast.setText(R.string.favorDone);toast.show();
                                }else {
                                    toast.setText(R.string.alreadyaddfavor);toast.show();
                                }

                            }
                            break;
                    }
                }
            }
        });
        //------快速搜尋功能設定--------//
        searchView = (SearchView)v.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {return false;}

            @Override
            public boolean onQueryTextChange(String searchText) {
                if(searchText.length()>0){
                    contact_RecyclerView.removeItemDecoration(itemDecoration);
                    searchList.clear();
                    for(int i = 0;i< Now_ContactList.size();i++){
                        String num = Now_ContactList.get(i).getPhoneNum().substring(0,searchText.length());
                        String name = Now_ContactList.get(i).getName();
                        if(num.equals(searchText) || (name.length() >= searchText.length() &&
                                name.substring(0,searchText.length()).equals(searchText))  ){
                            searchList.add(Now_ContactList.get(i));
                        }
                        CommonUtil.setContactList(context,contact_RecyclerView,adapter,searchList);
                    }
                }else {
                    contact_RecyclerView.addItemDecoration(itemDecoration);
                    searchList.clear();
                    CommonUtil.setContactList(context,contact_RecyclerView, adapter, Now_ContactList);
                }
                return true;
            }

        });

        return v;
    }


    public void setContactList(Uri uri, String[] projection){
        try {
            number = 0;
            Long id;
            String name;
            String mobileNum;
            String note = "";//備註欄
            String cityName="",streetName="";
            List<EmailData> emailList = new ArrayList<>();



            cursor = resolver.query(uri, projection, null, null, ContactsContract.Contacts.DISPLAY_NAME);
            //直接取contacts中的號碼資料區，再從號碼欄去抓對應的name跟number
            if (cursor != null) {
                while (cursor != null && cursor.moveToNext()) {


                    id =cursor.getLong(cursor.getColumnIndex(Phone.CONTACT_ID));//id
                    name = cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME));//名稱
                    mobileNum = cursor.getString(cursor.getColumnIndex(Phone.NUMBER));//電話

//                  //----------抓取備註欄----------//
                    Cursor info_cursor = resolver.query(Data.CONTENT_URI,
                            new String[]{Data._ID, Note.NOTE},
                            Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "='" + Note.CONTENT_ITEM_TYPE + "'",
                            new String[]{String.valueOf(id)}, null);
                        if (info_cursor != null && info_cursor.moveToFirst()) {
                            note = info_cursor.getString(info_cursor.getColumnIndex(Note.NOTE));
                        }
                    String address="";
                    //--------抓取地址--------//
                    info_cursor = resolver.query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
                            null,Phone.CONTACT_ID+" = " +id,null,null);
                        if(info_cursor != null && info_cursor.moveToFirst()){
                            cityName = info_cursor.getString(info_cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                            streetName = info_cursor.getString(info_cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                            if(streetName == null){
                                streetName = "";
                            }
                            if(cityName == null){
                                cityName = "";
                            }
                           // address = cityName+streetName;
                        }
                    info_cursor.close();

                    String email = "";

                    //抓取email資料
                    Cursor email_cursor = resolver.query(Email.CONTENT_URI,
                            null,Phone.CONTACT_ID+" = "+id,null,null);
                    if(email_cursor != null ){
                        emailList.clear();
                        while (email_cursor.moveToNext()){
                            EmailData emailData = new EmailData();
                            String type = email_cursor.getString(email_cursor.getColumnIndex(Email.TYPE));
                            email = email_cursor.getString(email_cursor.getColumnIndex(Email.DATA));
                            emailData.setType(type);
                            emailData.setMail(email);
                            emailList.add(emailData);

                        }
                    }
                    email_cursor.close();


                    if (!TextUtils.isEmpty(mobileNum) && !isCellPhoneNumber(mobileNum)) {
                        continue;
                    } else {
                        number = number+1;
                        addContactToList(number,
                                id,
                                mobileNum,
                                name,
                                CommonUtil.get_Avatar(resolver,id),
                                note,
                                cityName,streetName,
                                emailList);

                    }
                }

                cursor.close();

            } else {
                toast.setText(R.string.noData);
                toast.show();
            }
        }catch (Exception e){
            e.getMessage();
        }

    }

    /*新增聯絡人到手機清單*/
    private void addContactToList(int number, long id, String phoneNumber, String name, Bitmap avatar,String note,String city,String street,List<EmailData> emailList) {

        if (!tempId.equals(String.valueOf(id))) {
            String email ="";
            ContentValues values = new ContentValues();
            values.put(MyContactDBHelper.CONTACT_ID,id);
            values.put(MyContactDBHelper.NAME,name);
            values.put(MyContactDBHelper.PHONE_NUMBER,CommonUtil.getFormatPhone(phoneNumber));
            values.put(MyContactDBHelper.NUMBER,number);
            if(emailList != null && emailList.size()>0){
                for(int i =0;i<emailList.size();i++){
                    String type = emailList.get(i).getType();
                    if(!TextUtils.isEmpty(type)){
                        switch (emailList.get(i).getType()){
                            case "1"://住家

                                values.put(MyContactDBHelper.EMAIL_DATA_HOME,emailList.get(i).getMail());
                                break;
                            case "2"://公司

                                values.put(MyContactDBHelper.EMAIL_DATA_COM,emailList.get(i).getMail());
                                break;
                            case "3":

                                values.put(MyContactDBHelper.EMAIL_DATA_OTHER,emailList.get(i).getMail());
                                break;
                            case "0":

                                values.put(MyContactDBHelper.EMAIL_DATA_CUSTOM,emailList.get(i).getMail());
                                break;
                        }
                    }
                }
            }


            if(!TextUtils.isEmpty(note)){
                values.put(MyContactDBHelper.NOTE,note);
            }
            values.put(MyContactDBHelper.CITY,city);
            values.put(MyContactDBHelper.STREET,street);

            String img_avatar_base64 = "";
            if(avatar != null){
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                avatar.compress(Bitmap.CompressFormat.PNG,100,outputStream);
                byte[] bytes = outputStream.toByteArray();
                img_avatar_base64 = Base64.encodeToString(bytes,Base64.DEFAULT);
                values.put(MyContactDBHelper.IMG_AVATAR,img_avatar_base64);
            }

            myContactDBHelper.getWritableDatabase().insert(MyContactDBHelper.TABLE_NAME,null,values);
            tempId = String.valueOf(id);
        }
    }



    /*刪除聯絡人*/
    private void deleteContact(final long id,final int number, final String[] projection) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.attention)
                .setMessage(R.string.deleteOrNot)
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
                                myContactDBHelper.getWritableDatabase().delete(MyContactDBHelper.TABLE_NAME,String.valueOf(id),null);
                                Now_ContactList.remove(number-1);
                                toast.setText(R.string.deleteOK);
                                toast.show();

                                CommonUtil.setContactList(context,contact_RecyclerView,adapter,Now_ContactList);
                            }
                        } catch (Exception e) {
                            e.getMessage();
                        }
                    }
                })
                .setNegativeButton(R.string.no,null)
                .show();
    }

    public ArrayList<ContactData> getList() {
        ArrayList<ContactData> list = new ArrayList<>();

        Cursor c = myContactDBHelper.getReadableDatabase().query(MyContactDBHelper.TABLE_NAME,null,null,null,null,null,null);
        if(c != null){
            while (c.moveToNext()){
                ContactData data = new ContactData();
                data.setId(Long.valueOf(c.getString(c.getColumnIndex(MyContactDBHelper.CONTACT_ID))));
                data.setName(c.getString(c.getColumnIndex(MyContactDBHelper.NAME)));
                data.setPhoneNum(c.getString(c.getColumnIndex(MyContactDBHelper.PHONE_NUMBER)));
                data.setNumber(Integer.parseInt(c.getString(c.getColumnIndex(MyContactDBHelper.NUMBER))));
                data.setNote(c.getString(c.getColumnIndex(MyContactDBHelper.NOTE)));
                data.setCity(c.getString(c.getColumnIndex(MyContactDBHelper.CITY)));
                data.setStreet(c.getString(c.getColumnIndex(MyContactDBHelper.STREET)));
                data.setEmail_home(c.getString(c.getColumnIndex(MyContactDBHelper.EMAIL_DATA_HOME)));
                data.setEmail_company(c.getString(c.getColumnIndex(MyContactDBHelper.EMAIL_DATA_COM)));
                data.setEmail_other(c.getString(c.getColumnIndex(MyContactDBHelper.EMAIL_DATA_OTHER)));
                data.setEmail_custom(c.getString(c.getColumnIndex(MyContactDBHelper.EMAIL_DATA_CUSTOM)));

                //data.setEmail(c.getString(c.getColumnIndex(MyContactDBHelper.EMAIL)));
                int favor_tags = c.getInt(c.getColumnIndex(MyContactDBHelper.FAVOR_TAG));
                if(favor_tags == 1){
                    data.setImg_favor(new ImageView(context));
                }

                String avatar_base64 = c.getString(c.getColumnIndex(MyContactDBHelper.IMG_AVATAR));
                if(!TextUtils.isEmpty(avatar_base64)){
                byte[] bytes = Base64.decode(avatar_base64,Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                data.setImg_avatar(bitmap);
                }

                list.add(data);
            }
        }
        sp.edit().putInt("listSize",c.getCount()).commit();
        c.close();
        return list;
    }
}
