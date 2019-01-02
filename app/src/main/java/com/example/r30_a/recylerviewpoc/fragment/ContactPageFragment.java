package com.example.r30_a.recylerviewpoc.fragment;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.r30_a.recylerviewpoc.R;
import com.example.r30_a.recylerviewpoc.adapter.MyAdapter;
import com.example.r30_a.recylerviewpoc.adapter.MyDecoration;

import com.example.r30_a.recylerviewpoc.helper.MyDBHelper;
import com.example.r30_a.recylerviewpoc.model.ContactData;
import com.example.r30_a.recylerviewpoc.util.CommonUtil;
import com.yanzhenjie.recyclerview.swipe.Closeable;
import com.yanzhenjie.recyclerview.swipe.OnSwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static com.example.r30_a.recylerviewpoc.util.CommonUtil.isCellPhoneNumber;


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
    MyDBHelper myDBHelper;
    SharedPreferences sp;
    private Context context;

    public ContactPageFragment() {}

    @Override
    public void onResume() {
        super.onResume();
        searchList.clear();
        CommonUtil.favorIdSet = sp.getStringSet("favorTags",new HashSet<String>());
        //資料有更新時，要更新nowlist，無更新時丟回原本的
        if(CommonUtil.isDataChanged || CommonUtil.favorIdSet.size()>0){
            Now_ContactList = getContactList(CommonUtil.ALL_CONTACTS_URI,CommonUtil.phoneNumberProjection);
            CommonUtil.setContactList(context,contact_RecyclerView,adapter,
                    Now_ContactList);
        }else {
            CommonUtil.setContactList(context,contact_RecyclerView,adapter,Now_ContactList);
        }

    }
//
//
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
        myDBHelper = MyDBHelper.getInstance(context);
        sp = context.getSharedPreferences("favorTags",Context.MODE_PRIVATE);
        CommonUtil.favorIdSet = sp.getStringSet("favorTags",new HashSet<String>());
        resolver = context.getContentResolver();
        toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        Now_ContactList = getContactList(CommonUtil.ALL_CONTACTS_URI,CommonUtil.phoneNumberProjection);
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

                                Fragment fragment = UpdateContactFragment.newInstance(String.valueOf(data.getId()),data.getName(),data.getPhoneNum(),data.getImg_avatar());
                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                transaction.setCustomAnimations(R.anim.slide_right_in,R.anim.slide_left_out,R.anim.slide_left_in,R.anim.slide_right_out);
                                transaction.replace(R.id.frameLayout,fragment);
                                transaction.commit();

                            break;
                        //刪除
                        case 1:
                            deleteContact(data.getId(),CommonUtil.phoneNumberProjection);
                            break;
                    }
                }else {
                    switch (menuPosition){//向左滑動
                        //加入最愛
                        case 0:
                            if(favorList != null ){
                                String id = String.valueOf(data.getId());
                                if(!CommonUtil.favorIdSet.contains(id) ){

                                    //寫入資料庫
                                    ContentValues values = new ContentValues(5);
                                    values.put(MyDBHelper.CONTACT_ID,String.valueOf(data.getId()));
                                    values.put(MyDBHelper.NUMBER,String.valueOf(data.getNumber()));
                                    values.put(MyDBHelper.NAME,data.getName());
                                    values.put(MyDBHelper.PHONE_NUMBER,data.getPhoneNum());

                                    if(data.getImg_avatar() != null && data.getImg_avatar().length >0){
                                        String img_base64 = Base64.encodeToString(data.getImg_avatar(),Base64.DEFAULT);
                                        values.put(MyDBHelper.IMG_AVATAR,img_base64);
                                    } else {
                                        values.put(MyDBHelper.IMG_AVATAR,"");
                                    }
                                    myDBHelper.getWritableDatabase().insert(MyDBHelper.TABLE_NAME,null,values);

                                    CommonUtil.favorIdSet.add(id);
                                    sp.edit().putStringSet("favorTags",CommonUtil.favorIdSet).commit();
                                    CommonUtil.isDataChanged =true;

                                    //更新當前清單
                                    data.setImg_favor(new ImageView(context));
                                    data.isFavor(true);
                                    CommonUtil.setContactList(context,contact_RecyclerView, adapter, Now_ContactList);

                                    toast.setText(R.string.favorDone);toast.show();
                                }else {
                                    toast.setText(R.string.alreadyaddfavor);toast.show();
                                }
                                //當今清單的tag要顯示

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


    public ArrayList<ContactData> getContactList(Uri uri, String[] projection){
        ArrayList<ContactData> list = new ArrayList<>();
        try {
            number = 0;
            String name;
            String mobileNum;
            cursor = resolver.query(uri, projection, null, null, ContactsContract.Contacts.DISPLAY_NAME);
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
                        number = number+1;
                        addContactToList(number,id,mobileNum,name, CommonUtil.get_Avatar(resolver,id), CommonUtil.favorIdSet, list);
                    }
                }
                cursor.close();

                return list;
            } else {
                toast.setText(R.string.noData);
                toast.show();
                return list;
            }
        }catch (Exception e){
            e.getMessage();
        }
        return list;
    }

    /*新增聯絡人到手機清單*/
    private void addContactToList(int number, long id, String phoneNumber, String name, Bitmap avatar, Set<String> favorIdSet, ArrayList list) {

        if (!tempId.equals(String.valueOf(id))) {

            contactData = new ContactData();
            contactData.setId(id);//id
            contactData.setName(name);//名字
            contactData.setPhoneNum(CommonUtil.getFormatPhone(phoneNumber));//電話
            contactData.setNumber(number);//編號

            if (avatar != null) {
                contactData.setImg_avatar(avatar);//大頭照
            }
            if (favorIdSet.contains(String.valueOf(id))) {
                contactData.isFavor(true);
                contactData.setImg_favor(new ImageView(context));
            }

            tempId = String.valueOf(id);
            list.add(contactData);

        }

    }



    /*刪除聯絡人*/
    private void deleteContact(final long id, final String[] projection) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.attention)
                .setMessage(R.string.deleteOrNot)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
                                CommonUtil.isDataChanged = true;
                                Now_ContactList = getContactList(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,CommonUtil.phoneNumberProjection);
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
}
