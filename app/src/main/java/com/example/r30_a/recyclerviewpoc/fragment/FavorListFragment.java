package com.example.r30_a.recyclerviewpoc.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.r30_a.recyclerviewpoc.R;
import com.example.r30_a.recyclerviewpoc.adapter.MyAdapter;
import com.example.r30_a.recyclerviewpoc.helper.MyDBHelper;

import com.example.r30_a.recyclerviewpoc.model.ContactData;
import com.example.r30_a.recyclerviewpoc.util.Util;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.ArrayList;
import java.util.HashSet;

import static android.content.Context.MODE_PRIVATE;

public class FavorListFragment extends Fragment {

    MyDBHelper myDBHelper;

    ArrayList<ContactData> favorList = new ArrayList<>();
    SwipeMenuRecyclerView contact_RecyclerView;
    MyAdapter adapter;
    Toast toast;
    Context context;
    SharedPreferences sp;
    LinearLayout noDataLayout;
    LinearLayoutManager manager;

    public FavorListFragment() {}

    public static FavorListFragment newInstance() {
        FavorListFragment fragment = new FavorListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            context = getContext();
            manager = new LinearLayoutManager(context);
            sp = context.getSharedPreferences("favorTags",MODE_PRIVATE);
            myDBHelper = MyDBHelper.getInstance(context);
            Util.favorIdSet = sp.getStringSet("favorTags", new HashSet<>());
            toast = Toast.makeText(context,"",Toast.LENGTH_SHORT);

        Cursor c = myDBHelper.getReadableDatabase().query(MyDBHelper.TABLE_NAME,null,null,null,null,null,null);

        if(c != null){
            while (c.moveToNext()){

                int count = c.getInt(c.getColumnIndex(MyDBHelper.FAVOR_TAG));
                if(count == 1 ){

                    ContactData data= new ContactData();
                    data.setId(Long.valueOf(Util.getDBData(c, MyDBHelper.CONTACT_ID)));
                    data.setNumber(Integer.parseInt(Util.getDBData(c, MyDBHelper.NUMBER)));
                    data.setName(Util.getDBData(c, MyDBHelper.NAME));
                    data.setPhoneNum(Util.getDBData(c, MyDBHelper.PHONE_NUMBER));
                    data.setNote(Util.getDBData(c, MyDBHelper.NOTE));
                    data.setFavorTag(Integer.parseInt(Util.getDBData(c, MyDBHelper.FAVOR_TAG)));
                    data.setImg_favor(new ImageView(context));
                    data.setCity(Util.getDBData(c, MyDBHelper.CITY));
                    data.setStreet(Util.getDBData(c, MyDBHelper.STREET));
                    data.setEmail_home(Util.getDBData(c, MyDBHelper.EMAIL_DATA_HOME));
                    data.setEmail_company(Util.getDBData(c, MyDBHelper.EMAIL_DATA_COM));
                    data.setEmail_other(Util.getDBData(c, MyDBHelper.EMAIL_DATA_OTHER));
                    data.setEmail_custom(Util.getDBData(c, MyDBHelper.EMAIL_DATA_CUSTOM));
                    String avatar_base64 = Util.getDBData(c, MyDBHelper.IMG_AVATAR);
                    data.setImg_avatar(Util.getBitmap_avatar(avatar_base64));

                    favorList.add(data);
                    }
                }
            }
        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_favor_list, container, false);

        contact_RecyclerView = v.findViewById(R.id.contact_RecyclerView);
        //無資料的固定顯示頁，預設隱藏
        noDataLayout = v.findViewById(R.id.noData);
        noDataLayout.setVisibility(View.INVISIBLE);

        if(favorList.size()>0){
            adapter = new MyAdapter(context, favorList);
            Util.setContactList(context, contact_RecyclerView, adapter, favorList,manager);
        }else {
            noDataLayout.setVisibility(View.VISIBLE);
        }

        contact_RecyclerView.setSwipeMenuCreator((swipeLeftMenu, swipeRightMenu, viewType) -> {
            //建立右菜單刪除按鈕
            SwipeMenuItem delete_item = Util.setMenuItem(context,200,240,R.drawable.icons8_trash_48,16, Color.parseColor("#dd0000"));
            swipeRightMenu.addMenuItem(delete_item);
        });

        contact_RecyclerView.setSwipeMenuItemClickListener((closeable, adapterPosition, menuPosition, direction) -> {
            if(direction == -1){
                switch (menuPosition){
                    case 0:

                        Util.favorIdSet.remove(String.valueOf(favorList.get(adapterPosition).getId()));

                        try{
                            ContentValues values = new ContentValues();
                            values.put(MyDBHelper.FAVOR_TAG,0);
                            myDBHelper.getWritableDatabase().update(MyDBHelper.TABLE_NAME,
                                    values, MyDBHelper.CONTACT_ID + "=? ",new String[]{String.valueOf(favorList.get(adapterPosition).getId())});
                        }catch (Exception e){
                            e.getMessage();
                        }
                        favorList.remove(favorList.get(adapterPosition));
                        adapter = new MyAdapter(context,favorList);
                        Util.setContactList(context,contact_RecyclerView,adapter,favorList,manager);

                        toast.setText(R.string.deleteOK);toast.show();
                        //刪除最愛清單
                        if(favorList.size() == 0){
                            noDataLayout.setVisibility(View.VISIBLE);
                        }
                        break;
                }
            };
        });
        return v;
    }

}
