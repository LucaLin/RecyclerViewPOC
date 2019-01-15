package com.example.r30_a.recylerviewpoc.fragment;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.r30_a.recylerviewpoc.R;

import com.example.r30_a.recylerviewpoc.controller.MapsActivity;
import com.example.r30_a.recylerviewpoc.util.CommonUtil;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
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
    private static final String EMAIL_HOME = "email_home";
    private static final String EMAIL_COM = "email_com";
    private static final String EMAIL_OTHER = "email_other";
    private static final String EMAIL_CUSTOM = "email_custom";

    ImageView img_avatar;
    TextView txvName;
    TextView txvPhoneNumber;
    TextView txvNote;
    TextView txv_detailAddress;
    TextView txv_email_home,txv_email_company,txv_email_other,txv_email_custom;

    LinearLayout emailHomeLayout,emailWorkLayout,emailOtherLayout,emailCustomLayout;

    Context context;
    Toast toast;
    Bitmap img_bitmap;

    SharedPreferences sp;

    private String contact_id;
    private String number;
    private String name;
    private String phoneNumber;
    private String note;
    private String address;
    private String email_home,email_company,email_other,email_custom;
    private byte[] img_avatar_bytes;
    ImageButton ibt_toDial,ibt_toSMS,ibt_toEmail;
    ImageView btn_locate;

    public DetailPageFragment() {}

    public static DetailPageFragment newInstance(String contact_id, String number,
                                                 String name,String phoneNumber,
                                                 byte[] img_avatar_bytes,String note,
                                                 String city,String street,
                                                 String email_home,String email_company,
                                                 String email_other,String email_custom) {
        DetailPageFragment fragment = new DetailPageFragment();
        Bundle args = new Bundle();
        args.putString(CONTACT_ID,contact_id );
        args.putString(NUMBER, number);
        args.putString(NAME,name);
        args.putString(PHONE_NUMBER,phoneNumber);
        args.putByteArray(AVATAR,img_avatar_bytes);
        args.putString(NOTE,note);
        args.putString(ADDRESS,city+street);
        args.putString(EMAIL_HOME,email_home);
        args.putString(EMAIL_COM,email_company);
        args.putString(EMAIL_OTHER,email_other);
        args.putString(EMAIL_CUSTOM,email_custom);
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
           email_home = getArguments().getString(EMAIL_HOME);
           email_company = getArguments().getString(EMAIL_COM);
           email_other = getArguments().getString(EMAIL_OTHER);
           email_custom = getArguments().getString(EMAIL_CUSTOM);

           context = getContext();
           toast = Toast.makeText(context,"",Toast.LENGTH_SHORT);
            sp = context.getSharedPreferences("favorTags",MODE_PRIVATE);
            CommonUtil.favorIdSet = sp.getStringSet("favorTags",new HashSet<String>());

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
        ibt_toEmail = (ImageButton)v.findViewById(R.id.ib_toEmail);
        txvNote = (TextView)v.findViewById(R.id.txv_detailNote);
        txv_detailAddress = (TextView)v.findViewById(R.id.txv_detailAddress);
        txv_email_home = (TextView)v.findViewById(R.id.txv_email_home);
        txv_email_company = (TextView)v.findViewById(R.id.txv_email_company);
        txv_email_other = (TextView)v.findViewById(R.id.txv_email_other);
        txv_email_custom = (TextView)v.findViewById(R.id.txv_email_custom);
        emailHomeLayout = (LinearLayout)v.findViewById(R.id.emailHomeLayout);
        emailWorkLayout = (LinearLayout)v.findViewById(R.id.emailComLayout);
        emailOtherLayout = (LinearLayout)v.findViewById(R.id.emailOtherLayout);
        emailCustomLayout = (LinearLayout) v.findViewById(R.id.emailCustomLayout);

        btn_locate = (ImageView)v.findViewById(R.id.btn_locate);
        if(!TextUtils.isEmpty(note)){
            txvNote.setText(note);
        }
        if(!TextUtils.isEmpty(address)){
            txv_detailAddress.setText(address);
        }else {
            btn_locate.setVisibility(View.INVISIBLE);
        }

        txvName.setText(name);
        txvPhoneNumber.setText(phoneNumber);
        if(img_bitmap != null){
            img_avatar.setImageBitmap(img_bitmap);
        }
        setText(emailHomeLayout,txv_email_home,email_home);
        setText(emailWorkLayout,txv_email_company,email_company);
        setText(emailOtherLayout,txv_email_other,email_other);
        setText(emailCustomLayout,txv_email_custom,email_custom);

        //----------撥號----------//
        ibt_toDial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_dial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                context.startActivity(intent_dial);
            }
        });
        //----------簡訊----------//
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
        //----------信箱----------//
        ibt_toEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<String> emailList = new ArrayList<>();
                addMailToList(emailList,email_home);
                addMailToList(emailList,email_company);
                addMailToList(emailList,email_other);
                addMailToList(emailList,email_custom);
                if(emailList.size()>0) {
                    new AlertDialog.Builder(context).setItems(emailList.toArray(new String[emailList.size()]), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int pos) {
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setData(Uri.parse("mailto:"));
                            intent.setType("text/plant");
                            intent.putExtra(Intent.EXTRA_EMAIL, emailList.get(pos));
                            startActivity(intent);
                        }
                    }).setTitle(R.string.chooseMail)
                            .show();


                }else {
                    toast.setText(R.string.noEmailData);toast.show();
                }
            }
        });
        //----------定位----------//
        btn_locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(PermissionsUtil.hasPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION) &&
                                PermissionsUtil.hasPermission(context,Manifest.permission.ACCESS_FINE_LOCATION)){
                //GPS定位,抓地址的經緯度後傳給googlemap顯示
                try {
                    if(!TextUtils.isEmpty(address)){
                        Intent intent = new Intent(context, MapsActivity.class);
                        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                        List<Address> location =  geocoder.getFromLocationName(address,1);
                        double lat = location.get(0).getLatitude();
                        double lng = location.get(0).getLongitude();
                        intent.putExtra("lat",lat);
                        intent.putExtra("lng",lng);
                        intent.putExtra("address",address);
                        startActivity(intent);
                    }else {
                        toast.setText(R.string.noAddress);toast.show();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


                }else {
                    PermissionsUtil.requestPermission(getActivity(), new PermissionListener() {
                        @Override
                        public void permissionGranted(@NonNull String[] permission) {

                        }

                        @Override
                        public void permissionDenied(@NonNull String[] permission) {

                        }
                    },new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION});
                }
            }
        });

        return v;
    }

    private void addMailToList(List<String> emailList, String email) {
        if(!TextUtils.isEmpty(email) && !email.equals(getResources().getString(R.string.none))){
            emailList.add(email);
        }

    }

    private void setText(LinearLayout layout,TextView txv,String data) {
        if(!TextUtils.isEmpty(data)){
            txv.setText(data);
        }else {
            layout.setVisibility(View.GONE);
        }
    }

}
