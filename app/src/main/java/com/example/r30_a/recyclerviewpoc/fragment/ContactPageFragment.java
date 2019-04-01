package com.example.r30_a.recyclerviewpoc.fragment;

import android.animation.Animator;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.r30_a.recyclerviewpoc.R;
import com.example.r30_a.recyclerviewpoc.adapter.MyAdapter;
import com.example.r30_a.recyclerviewpoc.adapter.MyDecoration;

import com.example.r30_a.recyclerviewpoc.helper.MyContactDBHelper;

import com.example.r30_a.recyclerviewpoc.model.ContactData;
import com.example.r30_a.recyclerviewpoc.model.EmailData;
import com.example.r30_a.recyclerviewpoc.util.CommonUtil;
import com.example.r30_a.recyclerviewpoc.view.MyCustomSearchView;
import com.example.r30_a.recyclerviewpoc.view.MyFloatButton;
import com.example.r30_a.recyclerviewpoc.view.SideBar;
import com.github.promeg.pinyinhelper.Pinyin;
import com.yanzhenjie.recyclerview.swipe.Closeable;
import com.yanzhenjie.recyclerview.swipe.OnSwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.CommonDataKinds.Note;

import static com.example.r30_a.recyclerviewpoc.util.CommonUtil.isCellPhoneNumber;

import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;


public class ContactPageFragment extends Fragment {
    private Context context;
    Toast toast;
    private ArrayList<ContactData> favorList = new ArrayList<>();//常用清單
    private ArrayList<ContactData> Now_ContactList = new ArrayList<>();
    private ArrayList<ContactData> searchList = new ArrayList<>();
    int number = 0;
    LinearLayoutManager manager;
    private MyFloatButton floatButton;//浮動按鈕

    //--------聯絡人元件-------//
    private Cursor cursor;//搜尋資料的游標
    private ContentResolver resolver;
    private String tempId = "";//聯絡人id的暫存
    //-------聯絡人清單元件-----//
    private SwipeMenuRecyclerView contact_RecyclerView;
    private MyAdapter adapter;
    private SideBar sideBar;//側邊字母快搜欄
    private RecyclerView.ItemDecoration itemDecoration;//字首顯示欄

    //儲存資料用
    MyContactDBHelper myContactDBHelper;
    SharedPreferences sp;
    //動態搜尋欄
    MyCustomSearchView searchView;
    boolean isDecoRemove = false;

    @Override
    public void onResume() {
        super.onResume();
        searchList.clear();

        CommonUtil.favorIdSet = sp.getStringSet("favorTags", new HashSet<String>());
        //資料有更新時，要更新nowlist，無更新時丟回原本的

        try {
            if (sp.getInt("listSize", 0) == 0) {//只有第一次
                setContactList(CommonUtil.ALL_CONTACTS_URI, CommonUtil.phoneNumberProjection);
            }
            Now_ContactList = getList();
            CommonUtil.setContactList(context, contact_RecyclerView, adapter, Now_ContactList, manager);

        } catch (Exception e) {
            e.getMessage();
        }

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
        manager = new LinearLayoutManager(context);
        resolver = context.getContentResolver();
        toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        //資料相關
        myContactDBHelper = MyContactDBHelper.getInstance(context);
        sp = context.getSharedPreferences("Tags", Context.MODE_PRIVATE);

//        CommonUtil.favorIdSet = sp.getStringSet("favorTags",new HashSet<String>());
        //資料有更新時，要更新nowlist，無更新時丟回原本的

        //------------名稱分隔線------------//
        itemDecoration = new MyDecoration(context, new MyDecoration.DecorationCallBack() {
            @Override
            public long getGroupId(int pos) {
                String s = Pinyin.toPinyin(Now_ContactList.get(pos).getName().charAt(0));
                return s.charAt(0);
            }

            @Override
            public String getGroupFirstLine(int pos) {
                return Pinyin.toPinyin(Now_ContactList.get(pos).getName().charAt(0)).substring(0, 1);
            }
        });
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contact_page, container, false);

        contact_RecyclerView = (SwipeMenuRecyclerView) v.findViewById(R.id.contact_RecyclerView);
        //增加群組分類抬頭
        contact_RecyclerView.addItemDecoration(itemDecoration);
        //--------滑動菜單設定-------------//
        contact_RecyclerView.setSwipeMenuCreator(new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
                //建立右菜單更新按鈕
                SwipeMenuItem update_item = CommonUtil.setMenuItem(context, 200, 220, R.string.fun_updateData, 16, Color.parseColor("#69e359"));
                //建立右菜單刪除按鈕
                SwipeMenuItem delete_item = CommonUtil.setMenuItem(context, 200, 220, R.string.fun_deleteData, 16, Color.parseColor("#db2824"));
                //建立左菜單加入最愛按鈕
                SwipeMenuItem favor_item = CommonUtil.setMenuItem(context, 200, 220, R.string.fun_addFavor, 16, Color.parseColor("#69e359"));

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
                if (searchList.size() > 0) {
                    data = searchList.get(pos);
                } else {
                    data = Now_ContactList.get(pos);
                }
                if (direction == -1) {//向右滑動
                    switch (menuPosition) {
                        //更新
                        case 0:
                            //獲取現有資料至下一頁進行更新
                            Fragment fragment = UpdateContactFragment.newInstance(String.valueOf(data.getId()), data.getName(),
                                    data.getPhoneNum(), data.getImg_avatar(),
                                    data.getNote(), data.getCity(),
                                    data.getStreet(), data.getEmail_home(),
                                    data.getEmail_company(), data.getEmail_other(),
                                    data.getEmail_custom());
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out);
                            transaction.replace(R.id.frameLayout, fragment);
                            transaction.commit();

                            break;
                        //刪除
                        case 1:
                            deleteContact(data.getId(), data.getNumber(), CommonUtil.phoneNumberProjection);
                            break;
                    }
                } else {
                    switch (menuPosition) {//向左滑動
                        //加入最愛
                        case 0:
                            if (favorList != null) {
                                String id = String.valueOf(data.getId());
                                if (!CommonUtil.favorIdSet.contains(id)) {
                                    CommonUtil.favorIdSet.add(id);
                                    sp.edit().putStringSet("favorTags", CommonUtil.favorIdSet).commit();
                                    //更新當前清單

                                    ContentValues values = new ContentValues();
                                    values.put(MyContactDBHelper.FAVOR_TAG, 1);

                                    myContactDBHelper.getWritableDatabase().update(MyContactDBHelper.TABLE_NAME, values,
                                            MyContactDBHelper.CONTACT_ID + "=?", new String[]{String.valueOf(data.getId())});

                                    Now_ContactList = getList();
                                    CommonUtil.setContactList(context, contact_RecyclerView, adapter, Now_ContactList, manager);

                                    toast.setText(R.string.favorDone);
                                    toast.show();
                                } else {
                                    toast.setText(R.string.alreadyaddfavor);
                                    toast.show();
                                }

                            }
                            break;
                    }
                }
            }
        });

        //------快速新增朋友按鈕設定--------//
        floatButton = (MyFloatButton) v.findViewById(R.id.fab);
        floatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new AddContactFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out, R.anim.slide_left_in, R.anim.slide_right_out);
                transaction.replace(R.id.frameLayout, fragment);
                transaction.commit();

            }
        });
        //------動態搜尋欄---------//
        searchView = new MyCustomSearchView(context);
        searchView.edtInput = (EditText) v.findViewById(R.id.search_input_text);
        searchView.edtInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String txt = String.valueOf(s);
                if (txt.length() > 0) {

                    contact_RecyclerView.removeItemDecoration(itemDecoration);
                    isDecoRemove = true;
                    searchList.clear();
                    for (int i = 0; i < Now_ContactList.size(); i++) {
                        String num = Now_ContactList.get(i).getPhoneNum().substring(0, txt.length());
                        String name = Now_ContactList.get(i).getName();
                        if (num.equals(txt) || (name.length() >= txt.length() &&
                                name.substring(0, txt.length()).equals(txt))) {
                            searchList.add(Now_ContactList.get(i));
                        }
                        CommonUtil.setContactList(context, contact_RecyclerView, adapter, searchList, manager);
                    }
                } else {
                    if (isDecoRemove){
                        contact_RecyclerView.addItemDecoration(itemDecoration);
                        isDecoRemove = false;
                    }
                    searchList.clear();
                    CommonUtil.setContactList(context, contact_RecyclerView, adapter, Now_ContactList, manager);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //--------側邊字母快搜欄設定--------//
        sideBar = v.findViewById(R.id.sideBar2);
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingChanged(String s) {
                if (s.matches("#")) {//其它未分類就跳至最前
                    manager.scrollToPositionWithOffset(0, 0);
                } else {//根據資料找第一個出現的字母資料，並跳至該頁
                    int pos = adapter.getPosForSection(s.charAt(0));
                    manager.scrollToPositionWithOffset(pos, 0);
                }
            }
        });
        return v;
    }

    //-------聯絡清單建立--------
    public void setContactList(Uri uri, String[] projection) {
        try {
            number = 0;
            Long id;
            String name;
            String mobileNum;
            String note = "";//備註欄

            List<EmailData> emailList = new ArrayList<>();

            cursor = resolver.query(uri, projection, null, null, null);
            //直接取contacts中的號碼資料區，再從號碼欄去抓對應的name跟number

            if (cursor != null) {

                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToNext();

                    id = cursor.getLong(cursor.getColumnIndex(Phone.CONTACT_ID));//id
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
//                    String address="";

                    //--------抓取地址--------//
                    String cityName = "", streetName = "";
                    info_cursor = resolver.query(Data.CONTENT_URI,
                            null, Phone.CONTACT_ID + " =? AND " + Data.MIMETYPE + "=?",
                            new String[]{String.valueOf(id), StructuredPostal.CONTENT_ITEM_TYPE}, null);
                    if (info_cursor != null && info_cursor.moveToFirst()) {
                        cityName = info_cursor.getString(info_cursor.getColumnIndex(StructuredPostal.CITY));
                        streetName = info_cursor.getString(info_cursor.getColumnIndex(StructuredPostal.STREET));
                        if (streetName == null) {
                            streetName = "";
                        }
                        if (cityName == null) {
                            cityName = "";
                        }

                    }
                    info_cursor.close();

                    String email = "";

                    //抓取email資料
                    Cursor email_cursor = resolver.query(Email.CONTENT_URI,
                            null, Phone.CONTACT_ID + " = " + id, null, null);
                    if (email_cursor != null) {
                        emailList.clear();
                        while (email_cursor.moveToNext()) {
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
                        number = number + 1;
                        addContactToList(number,
                                id,
                                mobileNum,
                                name,
                                CommonUtil.get_Avatar(resolver, id),
                                note,
                                cityName, streetName,
                                emailList);
                    }
                }
                cursor.close();
            } else {
                toast.setText(R.string.noData);
                toast.show();
            }
        } catch (Exception e) {
            e.getMessage();
        }

    }

    /*新增聯絡人到手機清單*/
    private void addContactToList(int number, long id, String phoneNumber, String name, Bitmap avatar, String note, String city, String street, List<EmailData> emailList) {

        if (!tempId.equals(String.valueOf(id))) {
            String email = "";
            ContentValues values = new ContentValues();
            values.put(MyContactDBHelper.CONTACT_ID, id);
            values.put(MyContactDBHelper.NAME, name);
            values.put(MyContactDBHelper.PHONE_NUMBER, CommonUtil.getFormatPhone(phoneNumber));
            values.put(MyContactDBHelper.NUMBER, number + 1);

            if (emailList != null && emailList.size() > 0) {
                for (int i = 0; i < emailList.size(); i++) {
                    String type = emailList.get(i).getType();
                    if (!TextUtils.isEmpty(type)) {
                        switch (emailList.get(i).getType()) {
                            //多個同樣tag的資料怎麼辦 = =?
                            case "1"://住家

                                values.put(MyContactDBHelper.EMAIL_DATA_HOME, emailList.get(i).getMail());
                                break;
                            case "2"://公司

                                values.put(MyContactDBHelper.EMAIL_DATA_COM, emailList.get(i).getMail());
                                break;
                            case "3":

                                values.put(MyContactDBHelper.EMAIL_DATA_OTHER, emailList.get(i).getMail());
                                break;
                            case "0":

                                values.put(MyContactDBHelper.EMAIL_DATA_CUSTOM, emailList.get(i).getMail());
                                break;
                        }
                    }
                }
            }


            if (!TextUtils.isEmpty(note)) {
                values.put(MyContactDBHelper.NOTE, note);
            }
            values.put(MyContactDBHelper.CITY, city);
            values.put(MyContactDBHelper.STREET, street);

            String img_avatar_base64 = "";
            if (avatar != null) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                avatar.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                byte[] bytes = outputStream.toByteArray();
                img_avatar_base64 = Base64.encodeToString(bytes, Base64.DEFAULT);
                values.put(MyContactDBHelper.IMG_AVATAR, img_avatar_base64);
            }

            myContactDBHelper.getWritableDatabase().insert(MyContactDBHelper.TABLE_NAME, null, values);
            tempId = String.valueOf(id);
        }
    }


    /*刪除聯絡人*/
    private void deleteContact(final long id, final int number, final String[] projection) {

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
                                myContactDBHelper.getWritableDatabase().delete(MyContactDBHelper.TABLE_NAME, String.valueOf(id), null);
                                Now_ContactList.remove(number - 1);
                                toast.setText(R.string.deleteOK);
                                toast.show();

                                CommonUtil.setContactList(context, contact_RecyclerView, adapter, Now_ContactList, manager);
                            }
                        } catch (Exception e) {
                            e.getMessage();
                        }
                    }
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }

    public ArrayList<ContactData> getList() {
        ArrayList<ContactData> list = new ArrayList<>();

        Cursor c = myContactDBHelper.getReadableDatabase().query(MyContactDBHelper.TABLE_NAME, null, null, null, null, null, null);
        if (c != null) {
            while (c.moveToNext()) {
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

                int favor_tags = c.getInt(c.getColumnIndex(MyContactDBHelper.FAVOR_TAG));
                if (favor_tags == 1) {
                    data.setFavorTag(favor_tags);
                    data.setImg_favor(new ImageView(context));
                } else {
                    data.setImg_normal(new ImageView(context));
                }

                String avatar_base64 = c.getString(c.getColumnIndex(MyContactDBHelper.IMG_AVATAR));
                if (!TextUtils.isEmpty(avatar_base64)) {
                    byte[] bytes = Base64.decode(avatar_base64, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    data.setImg_avatar(bitmap);
                }
                //-------獲取名稱的第一個字母拼音，供sidebar使用------//
                String letter = Pinyin.toPinyin(data.getName().substring(0, 1).charAt(0));
                if (letter.matches("[A-Z]")) {
                    data.setLetter(letter);

                } else {
                    data.setLetter("#");
                }
                list.add(data);

            }
        }
        sp.edit().putInt("listSize", c.getCount()).commit();
        c.close();

        //將所有資料根據羅馬拼音做排序
        Collections.sort(list, new Comparator<ContactData>() {
            @Override
            public int compare(ContactData o1, ContactData o2) {

                String s1 = Pinyin.toPinyin(o1.getName().charAt(0)).toUpperCase();
                String s2 = Pinyin.toPinyin(o2.getName().charAt(0)).toUpperCase();
                if (!s1.matches("[0-9]") || !s2.matches("[0-9]")) {
                    return s1.compareTo(s2);
                } else {
                    return 1;
                }

            }
        });

        adapter = new MyAdapter(context, list);
        return list;
    }

}
