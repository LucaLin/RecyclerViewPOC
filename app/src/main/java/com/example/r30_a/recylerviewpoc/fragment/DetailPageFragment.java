package com.example.r30_a.recylerviewpoc.fragment;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.r30_a.recylerviewpoc.R;
import com.example.r30_a.recylerviewpoc.helper.MyDBHelper;
import com.example.r30_a.recylerviewpoc.util.CommonUtil;

import static android.content.Context.MODE_PRIVATE;


public class DetailPageFragment extends Fragment {

    private static final String CONTACT_ID = "id";
    private static final String NUMBER = "number";
    private static final String NAME = "name";
    private static final String PHONE_NUMBER = "phoneNumber";
    private static final String AVATAR = "avatar_base64";

    ImageView img_avatar;
    TextView txvName,txvPhoneNumber;
    SharedPreferences sp;
    MyDBHelper myDBHelper;

    Context context;
    Toast toast;
    Bitmap img_bitmap;



    private String contact_id;
    private String number;
    private String name;
    private String phoneNumber;
    private byte[] img_avatar_bytes;
    ImageButton ibt_toDial,ibt_toSMS;


    public DetailPageFragment() {

    }

    // TODO: Rename and change types and number of parameters
    public static DetailPageFragment newInstance(String contact_id, String number,String name,String phoneNumber,byte[] img_avatar_bytes) {
        DetailPageFragment fragment = new DetailPageFragment();
        Bundle args = new Bundle();
        args.putString(CONTACT_ID,contact_id );
        args.putString(NUMBER, number);
        args.putString(NAME,name);
        args.putString(PHONE_NUMBER,phoneNumber);
        args.putByteArray(AVATAR,img_avatar_bytes);
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


           context = getContext();
            myDBHelper = MyDBHelper.getInstance(context);
            toast = Toast.makeText(context,"",Toast.LENGTH_SHORT);

            sp = context.getSharedPreferences("favorTags",MODE_PRIVATE);
            CommonUtil.favorIdSet = sp.getStringSet("favorTags",null);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail_page, container, false);
        txvName = (TextView)v.findViewById(R.id.txv_detailName);
        txvPhoneNumber = (TextView)v.findViewById(R.id.txv_detailPhone);
        img_avatar = (ImageView)v.findViewById(R.id.detail_img_avatar);
        ibt_toDial = (ImageButton)v.findViewById(R.id.ib_toCall);
        ibt_toSMS = (ImageButton)v.findViewById(R.id.ib_toMsg);

        txvName.setText(name);
        txvPhoneNumber.setText(phoneNumber);
        if(img_bitmap != null){
        img_avatar.setImageBitmap(img_bitmap);
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

        return v;
    }

}
