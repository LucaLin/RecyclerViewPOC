package com.example.r30_a.recyclerviewpoc.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.r30_a.recyclerviewpoc.R;
import com.example.r30_a.recyclerviewpoc.adapter.MyAdapter;
import com.example.r30_a.recyclerviewpoc.helper.MyContactDBHelper;

import com.example.r30_a.recyclerviewpoc.model.ContactData;
import com.example.r30_a.recyclerviewpoc.util.CommonUtil;
import com.yanzhenjie.recyclerview.swipe.Closeable;
import com.yanzhenjie.recyclerview.swipe.OnSwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import java.util.ArrayList;
import java.util.HashSet;

import static android.content.Context.MODE_PRIVATE;

public class FavorListFragment extends Fragment {

    MyContactDBHelper myContactDBHelper;

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
            myContactDBHelper = MyContactDBHelper.getInstance(context);
            CommonUtil.favorIdSet = sp.getStringSet("favorTags",new HashSet<String>());
            toast = Toast.makeText(context,"",Toast.LENGTH_SHORT);

        Cursor c = myContactDBHelper.getReadableDatabase().query(MyContactDBHelper.TABLE_NAME,null,null,null,null,null,null);

        if(c != null){
            while (c.moveToNext()){

                int count = c.getInt(c.getColumnIndex(MyContactDBHelper.FAVOR_TAG));
                if(count == 1 ){
                    ContactData data= new ContactData();
                    data.setId(Long.valueOf(c.getString(c.getColumnIndex(MyContactDBHelper.CONTACT_ID))));
                    data.setNumber(Integer.parseInt(c.getString(c.getColumnIndex(MyContactDBHelper.NUMBER))));
                    data.setName(c.getString(c.getColumnIndex(MyContactDBHelper.NAME)));
                    data.setPhoneNum(c.getString(c.getColumnIndex(MyContactDBHelper.PHONE_NUMBER)));
                    data.setNote(c.getString(c.getColumnIndex(MyContactDBHelper.NOTE)));
                    data.setFavorTag(Integer.parseInt(c.getString(c.getColumnIndex(MyContactDBHelper.FAVOR_TAG))));
                    data.setImg_favor(new ImageView(context));
                    data.setCity(c.getString(c.getColumnIndex(MyContactDBHelper.CITY)));
                    data.setStreet(c.getString(c.getColumnIndex(MyContactDBHelper.STREET)));
                    data.setEmail_home(c.getString(c.getColumnIndex(MyContactDBHelper.EMAIL_DATA_HOME)));
                    data.setEmail_company(c.getString(c.getColumnIndex(MyContactDBHelper.EMAIL_DATA_COM)));
                    data.setEmail_other(c.getString(c.getColumnIndex(MyContactDBHelper.EMAIL_DATA_OTHER)));
                    data.setEmail_custom(c.getString(c.getColumnIndex(MyContactDBHelper.EMAIL_DATA_CUSTOM)));
                    String avatar_base64 = c.getString(c.getColumnIndex(MyContactDBHelper.IMG_AVATAR));
                    if(!TextUtils.isEmpty(avatar_base64)){
                        byte[] bytes = Base64.decode(avatar_base64, Base64.DEFAULT);
                        Bitmap img_avatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        data.setImg_avatar(img_avatar);
                        }
                    favorList.add(data);
                    }
                }
            }
        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_favor_list, container, false);

        contact_RecyclerView = (SwipeMenuRecyclerView)v.findViewById(R.id.contact_RecyclerView);
        //無資料的固定顯示頁，預設隱藏
        noDataLayout = (LinearLayout)v.findViewById(R.id.noData);
        noDataLayout.setVisibility(View.INVISIBLE);

        if(favorList.size()>0){
            adapter = new MyAdapter(context, favorList);
            CommonUtil.setContactList(context, contact_RecyclerView, adapter, favorList,manager);
        }else {
            noDataLayout.setVisibility(View.VISIBLE);
        }

        contact_RecyclerView.setSwipeMenuCreator(new SwipeMenuCreator() {
            @Override
            public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
                //建立右菜單刪除按鈕
                SwipeMenuItem delete_item = CommonUtil.setMenuItem(context,200,240,R.drawable.icons8_trash_48,16, Color.parseColor("#dd0000"));
                swipeRightMenu.addMenuItem(delete_item);
            }
        });

        contact_RecyclerView.setSwipeMenuItemClickListener(new OnSwipeMenuItemClickListener() {
            @Override
            public void onItemClick(Closeable closeable, final int adapterPosition, int menuPosition, int direction) {
                if(direction == -1){
                    switch (menuPosition){
                        case 0:

                            CommonUtil.favorIdSet.remove(String.valueOf(favorList.get(adapterPosition).getId()));

                            try{
                                ContentValues values = new ContentValues();
                                values.put(MyContactDBHelper.FAVOR_TAG,0);
                                myContactDBHelper.getWritableDatabase().update(MyContactDBHelper.TABLE_NAME,
                                        values,MyContactDBHelper.CONTACT_ID + "=? ",new String[]{String.valueOf(favorList.get(adapterPosition).getId())});
                            }catch (Exception e){
                                e.getMessage();
                            }
                            favorList.remove(favorList.get(adapterPosition));
                            adapter = new MyAdapter(context,favorList);
                            CommonUtil.setContactList(context,contact_RecyclerView,adapter,favorList,manager);

                            toast.setText(R.string.deleteOK);toast.show();
                            //刪除最愛清單
                            if(favorList.size() == 0){
                                noDataLayout.setVisibility(View.VISIBLE);
                            }
                            break;
                    }
                };
            }
        });
        return v;
    }

}
