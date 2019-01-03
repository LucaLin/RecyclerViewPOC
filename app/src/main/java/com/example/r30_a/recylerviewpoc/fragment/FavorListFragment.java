package com.example.r30_a.recylerviewpoc.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.r30_a.recylerviewpoc.R;
import com.example.r30_a.recylerviewpoc.adapter.MyAdapter;
import com.example.r30_a.recylerviewpoc.helper.MyFavorDBHelper;
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

import static android.content.Context.MODE_PRIVATE;

public class FavorListFragment extends Fragment {

    MyFavorDBHelper myFavorDBHelper;
    SQLiteDatabase db;
    ArrayList<ContactData> favorList = new ArrayList<>();
    SwipeMenuRecyclerView contact_RecyclerView;
    MyAdapter adapter;
    Toast toast;
    Context context;
    SharedPreferences sp;
    LinearLayout noDataLayout;

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
            sp = context.getSharedPreferences("favorTags",MODE_PRIVATE);

            myFavorDBHelper = MyFavorDBHelper.getInstance(context);
            CommonUtil.favorIdSet = sp.getStringSet("favorTags",new HashSet<String>());
            toast = Toast.makeText(context,"",Toast.LENGTH_SHORT);
            Cursor cursor = myFavorDBHelper.getReadableDatabase().query(MyFavorDBHelper.TABLE_NAME,
                    null,null,null,null,null, null);

            if(cursor.getCount() != 0){
                while (cursor.moveToNext()) {
                    ContactData data = new ContactData();
                    data.setId(cursor.getLong(cursor.getColumnIndex(MyFavorDBHelper.CONTACT_ID)));
                    data.setNumber(Integer.parseInt(cursor.getString(cursor.getColumnIndex(MyFavorDBHelper.NUMBER))));
                    data.setName(cursor.getString(cursor.getColumnIndex(MyFavorDBHelper.NAME)));
                    data.setPhoneNum(cursor.getString(cursor.getColumnIndex(MyFavorDBHelper.PHONE_NUMBER)));

                    String avatar_base64 = cursor.getString(cursor.getColumnIndex(MyFavorDBHelper.IMG_AVATAR));
                    if (!avatar_base64.equals("")) {
                        byte[] bytes = Base64.decode(avatar_base64, Base64.DEFAULT);
                        Bitmap img_avatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        data.setImg_avatar(img_avatar);
                    }
                    favorList.add(data);
                    }
                }
        }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_favor_list, container, false);
        contact_RecyclerView = (SwipeMenuRecyclerView)v.findViewById(R.id.contact_RecyclerView);
        noDataLayout = (LinearLayout)v.findViewById(R.id.noData);
        noDataLayout.setVisibility(View.INVISIBLE);
        if(favorList.size()>0){
            adapter = new MyAdapter(context, favorList);
            CommonUtil.setContactList(context, contact_RecyclerView, adapter, favorList);
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
                                myFavorDBHelper.getWritableDatabase().delete(MyFavorDBHelper.TABLE_NAME, MyFavorDBHelper.CONTACT_ID + "=? ",new String[]{String.valueOf(favorList.get(adapterPosition).getId())});
                            }catch (Exception e){
                                e.getMessage();
                            }
                            favorList.remove(favorList.get(adapterPosition));
                            adapter = new MyAdapter(context,favorList);
                            CommonUtil.setContactList(context,contact_RecyclerView,adapter,favorList);
                            CommonUtil.isDataChanged = true;
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
