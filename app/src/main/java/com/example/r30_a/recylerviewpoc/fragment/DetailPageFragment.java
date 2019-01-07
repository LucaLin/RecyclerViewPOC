package com.example.r30_a.recylerviewpoc.fragment;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.r30_a.recylerviewpoc.R;

import com.example.r30_a.recylerviewpoc.controller.MapsActivity;
import com.example.r30_a.recylerviewpoc.util.CommonUtil;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;


public class DetailPageFragment extends Fragment {

    private static final String CONTACT_ID = "id";
    private static final String NUMBER = "number";
    private static final String NAME = "name";
    private static final String PHONE_NUMBER = "phoneNumber";
    private static final String AVATAR = "avatar_base64";
    private static final String NOTE = "note";
    private static final String ADDRESS = "address";

    ImageView img_avatar;
    TextView txvName,txvPhoneNumber,txvNote,txv_detailAddress;
    SharedPreferences sp;


    Context context;
    Toast toast;
    Bitmap img_bitmap;

    private String contact_id;
    private String number;
    private String name;
    private String phoneNumber;
    private String note;
    private String address;
    private byte[] img_avatar_bytes;
    ImageButton ibt_toDial,ibt_toSMS;
    ImageView btn_locate;

    public DetailPageFragment() {}

    public static DetailPageFragment newInstance(String contact_id, String number,String name,String phoneNumber,byte[] img_avatar_bytes,String note,String address) {
        DetailPageFragment fragment = new DetailPageFragment();
        Bundle args = new Bundle();
        args.putString(CONTACT_ID,contact_id );
        args.putString(NUMBER, number);
        args.putString(NAME,name);
        args.putString(PHONE_NUMBER,phoneNumber);
        args.putByteArray(AVATAR,img_avatar_bytes);
        args.putString(NOTE,note);
        args.putString(ADDRESS,address);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
           contact_id = getArguments().getString(CONTACT_ID);
           number = getArguments().getString(NUMBER);
           name = getArguments().getString(NAME);
           phoneNumber = getArguments().getString(PHONE_NUMBER);
           img_avatar_bytes = getArguments().getByteArray(AVATAR);
           if(img_avatar_bytes != null && img_avatar_bytes.length>0){
           img_bitmap = BitmapFactory.decodeByteArray(img_avatar_bytes,0,img_avatar_bytes.length);
           }
           note = getArguments().getString(NOTE);
           address = getArguments().getString(ADDRESS);

           context = getContext();
           toast = Toast.makeText(context,"",Toast.LENGTH_SHORT);
           sp = context.getSharedPreferences("favorTags",MODE_PRIVATE);
           CommonUtil.favorIdSet = sp.getStringSet("favorTags",null);

        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail_page, container, false);
        txvName = (TextView)v.findViewById(R.id.txv_detailName);
        txvPhoneNumber = (TextView)v.findViewById(R.id.txv_detailPhone);
        img_avatar = (ImageView)v.findViewById(R.id.detail_img_avatar);
        ibt_toDial = (ImageButton)v.findViewById(R.id.ib_toCall);
        ibt_toSMS = (ImageButton)v.findViewById(R.id.ib_toMsg);
        txvNote = (TextView)v.findViewById(R.id.txv_detailNote);
        txv_detailAddress = (TextView)v.findViewById(R.id.txv_detailAddress);
        btn_locate = (ImageView)v.findViewById(R.id.btn_locate);
        if(!TextUtils.isEmpty(note)){
            txvNote.setText(note);
        }else {
            txvNote.setText(R.string.none);
        }

        txvName.setText(name);
        txvPhoneNumber.setText(phoneNumber);
        if(img_bitmap != null){
            img_avatar.setImageBitmap(img_bitmap);
        }

        if(!TextUtils.isEmpty(address)){
            txv_detailAddress.setText(address);
        }else {
            txv_detailAddress.setText(R.string.none);
        }

        ibt_toDial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_dial = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                context.startActivity(intent_dial);
            }
        });

        ibt_toSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_sms = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:"+ phoneNumber));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                context.startActivity(intent_sms);
            }
        });

        btn_locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //GPS定位,抓地址的經緯度後傳給googlemap顯示
                Intent intent = new Intent(context, MapsActivity.class);
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                try {
                    List<Address> location =  geocoder.getFromLocationName(address,1);
                    double lat = location.get(0).getLatitude();
                    double lng = location.get(0).getLongitude();
                    intent.putExtra("lat",lat);
                    intent.putExtra("lng",lng);
                    intent.putExtra("address",address);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                startActivity(intent);
            }
        });

        return v;
    }

}
