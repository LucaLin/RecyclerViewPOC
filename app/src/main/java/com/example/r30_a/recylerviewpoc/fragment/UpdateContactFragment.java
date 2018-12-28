package com.example.r30_a.recylerviewpoc.fragment;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.r30_a.recylerviewpoc.R;
import com.example.r30_a.recylerviewpoc.controller.ContactsPageActivity;
import com.example.r30_a.recylerviewpoc.controller.UpdateDataActivity;
import com.example.r30_a.recylerviewpoc.util.CommonUtil;

import java.io.File;

import static android.app.Activity.RESULT_OK;


public class UpdateContactFragment extends Fragment implements View.OnClickListener{

    private static final String USER_OLD_NAME = "name";
    private static final String USER_OLD_PHONE = "phoneNumber";
    private static final String USER_AVATAR = "avatar";
    private static final String CONTACT_ID = "contact_id";

    String oldname,oldphoneNumber,contact_id;
    byte[] img_avatar_bytes;

    TextView txvDataName, txvDataPhone;
    Button btnUpdate;
    EditText edtName, edtPhone,edtNote;
    Toast toast;
    String dataId;
    ImageView img_avatar;
    FrameLayout pickUserPhoto;
    Bitmap old_avatar;
    Context context;

    ContentResolver resolver;
    ContentValues values;
    Bitmap update_avatar;
    File temp_file;

    public UpdateContactFragment() {

    }


    public static UpdateContactFragment newInstance(String contact_id,String name, String phoneNumber, byte[] img_avatar_bytes) {
        UpdateContactFragment fragment = new UpdateContactFragment();
        Bundle args = new Bundle();
        args.putString(CONTACT_ID,contact_id);
        args.putString(USER_OLD_NAME, name);
        args.putString(USER_OLD_PHONE, phoneNumber);
        args.putByteArray(USER_AVATAR,img_avatar_bytes);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            context = getContext();
            resolver = context.getContentResolver();
            contact_id = getArguments().getString(CONTACT_ID);
            oldname = getArguments().getString(USER_OLD_NAME);
            oldphoneNumber = getArguments().getString(USER_OLD_PHONE);
            img_avatar_bytes = getArguments().getByteArray(USER_AVATAR);
            temp_file = new File("/sdcard/a.jpg");
            toast = Toast.makeText(context,"",Toast.LENGTH_SHORT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_update_contact, container, false);


        txvDataName = (TextView)v.findViewById(R.id.txvDataName);
        txvDataPhone = (TextView)v.findViewById(R.id.txvDataPhone);
        btnUpdate = (Button)v.findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(this);
        img_avatar = (ImageView)v.findViewById(R.id.userPhoto);
        img_avatar.setOnClickListener(this);
        pickUserPhoto = (FrameLayout)v.findViewById(R.id.pickUserPhoto);
        pickUserPhoto.setOnClickListener(this);

        edtName = (EditText)v.findViewById(R.id.edtContactName);
        edtPhone = (EditText)v.findViewById(R.id.edtPhoneNumber);
        edtName.setText(oldname);
        edtPhone.setText(oldphoneNumber);

        txvDataName.setText(oldname);
        txvDataPhone.setText(oldphoneNumber);
        if(img_avatar_bytes != null && img_avatar_bytes.length>0){
            old_avatar = BitmapFactory.decodeByteArray(img_avatar_bytes,0,img_avatar_bytes.length);
            img_avatar.setImageBitmap(old_avatar);
        }


        return v;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnUpdate) {

            final String updateName = edtName.getText().toString();
            final String updatePhone = edtPhone.getText().toString();

            if (updateName.equals(txvDataName.getText()) && updatePhone.equals(txvDataPhone.getText())
                    && old_avatar == update_avatar) {
                toast.setText(R.string.noUpdate);
                toast.show();
            } else if (TextUtils.isEmpty(updateName) || TextUtils.isEmpty(updatePhone)) {
                toast.setText(R.string.wrongInput);
                toast.show();
            } else {
                if (CommonUtil.isCellPhoneNumber(updatePhone)) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(R.string.hint)
                            .setMessage(R.string.sureToUpdate)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                        //newinstance or 直接做更新的動作並通知有通新

                                        Cursor c = resolver.query(ContactsContract.Data.CONTENT_URI,
                                                new String[]{ContactsContract.Data.RAW_CONTACT_ID},
                                                ContactsContract.Contacts.DISPLAY_NAME + " =?",
                                                new String[]{ oldname },null);

                                        c.moveToFirst();
                                        String raw_contact_id = c.getString(c.getColumnIndex(ContactsContract.Data.RAW_CONTACT_ID));
                                        c.close();
                                        try{
                                            //更新電話
                                            ContentValues values = new ContentValues();
                                            values.put(ContactsContract.CommonDataKinds.Phone.NUMBER,updatePhone);
                                            values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
                                            resolver.update(ContactsContract.Data.CONTENT_URI,
                                                    values,
                                                    ContactsContract.Data.RAW_CONTACT_ID+" =?" +" AND "+ ContactsContract.Data.MIMETYPE + " =?" ,
                                                    new String[]{raw_contact_id, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE});
                                            //更新名字
                                            values = new ContentValues();
                                            values.put(ContactsContract.Contacts.DISPLAY_NAME,updateName);
                                            resolver.update(
                                                    ContactsContract.RawContacts.CONTENT_URI,
                                                    values, ContactsContract.Data.CONTACT_ID+" =?",
                                                    new String[]{contact_id});
                                        }catch (Exception e){
                                            e.getMessage();
                                        }

                                        CommonUtil.isDataChanged = true;
                                        toast.setText(R.string.updateDataOK);
                                        toast.show();
                                    
                                }
                            }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).create();

                    builder.show();
                } else {
                    toast.setText(R.string.wrongInput);
                    toast.show();
                }
            }
        }else if((v.getId() ==  R.id.userPhoto) || (v.getId() ==R.id.pickUserPhoto)){
            showPopupMenu(v);
        }


    }
    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(context,v);
        popupMenu.inflate(R.menu.popupmenu_foravatar);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.item_camera:
                        //cameraStart();
                        break;
                    case R.id.item_picture:
                       // albumStart();
                        break;
                }
                return true;
            }
        });
        popupMenu.show();

    }

}
