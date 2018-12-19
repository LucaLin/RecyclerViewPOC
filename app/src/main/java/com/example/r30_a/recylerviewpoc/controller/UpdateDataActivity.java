package com.example.r30_a.recylerviewpoc.controller;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.r30_a.recylerviewpoc.R;
import com.example.r30_a.recylerviewpoc.util.CommonUtil;
import com.github.dfqin.grantor.PermissionsUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class UpdateDataActivity extends AppCompatActivity implements View.OnClickListener{

    TextView txvDataName, txvDataPhone;
    Button btnUpdate;
    EditText edtName, edtPhone;
    Toast toast;
    String dataId;
    ImageView img_avatar;
    FrameLayout pickUserPhoto;
    Bitmap old_avatar, update_avatar;
    ByteArrayOutputStream stream;
    byte[] bytes;
    SharedPreferences sharedPreferences;

    //取得結果用的Request Code
    private final int CAMERA_REQUEST = 1;
    private final int ALBUM_REQUEST = 2;
    private final int CROP_REQUEST = 3;

    Uri uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_data);
        initView();


    }

    private void initView() {

        toast = Toast.makeText(this,"",Toast.LENGTH_SHORT);
        txvDataName = (TextView)findViewById(R.id.txvDataName);
        txvDataPhone = (TextView)findViewById(R.id.txvDataPhone);
        btnUpdate = (Button)findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(this);

        Intent intent = getIntent();
        txvDataName.setText(intent.getStringExtra("name"));
        txvDataPhone.setText(intent.getStringExtra("phone"));
        dataId = intent.getStringExtra("id");

        edtName = (EditText)findViewById(R.id.edtContactName);
        edtPhone = (EditText)findViewById(R.id.edtPhoneNumber);
        edtName.setText(intent.getStringExtra("name"));
        edtPhone.setText(intent.getStringExtra("phone"));

        img_avatar = (ImageView)findViewById(R.id.userPhoto);
        img_avatar.setOnClickListener(this);

        byte[] avatar_byte = intent.getByteArrayExtra("avatar" );
        if(avatar_byte != null && avatar_byte.length  != 0 ){
            old_avatar = BitmapFactory.decodeByteArray(avatar_byte,0,avatar_byte.length);
            img_avatar.setImageBitmap(old_avatar);
        }
        pickUserPhoto = (FrameLayout)findViewById(R.id.pickUserPhoto);
        pickUserPhoto.setOnClickListener(this);









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
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.hint)
                            .setMessage(R.string.sureToUpdate)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                try {

                                    Intent intent = new Intent();
                                    intent.putExtra("id", dataId);
                                    intent.putExtra("Name", updateName);
                                    intent.putExtra("Phone", updatePhone);
                                    intent.putExtra("oldName", txvDataName.getText());
//                                    intent.putExtra("avatar",bytes);
                                    intent.setData(uri);
                                    setResult(RESULT_OK, intent);

                                    intent.setClass(UpdateDataActivity.this, ContactsPageActivity.class);

                                    //startActivityForResult(intent,ContactsPageActivity.REQUEST_CODE);
                                    toast.setText(R.string.updateDataOK);
                                    toast.show();
                                    finish();

                                }catch (Exception e){
                                    e.getMessage();
                                }


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
        PopupMenu popupMenu = new PopupMenu(this,v);
        popupMenu.inflate(R.menu.popupmenu_foravatar);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.item_camera:
                        //cameraStart();
                        break;
                    case R.id.item_picture:
                        albumStart();
                        break;
                }
                return true;
            }
        });
        popupMenu.show();

    }

    private void albumStart() {
        String[] permissionAlbum = {//先檢查權限
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,};
        if(!PermissionsUtil.hasPermission(this,permissionAlbum)) {

            Intent albumIntent = new Intent();
            albumIntent.setType("image/*");//設定只顯示圖片區，不要秀其它的資料夾
            albumIntent.setAction(Intent.ACTION_GET_CONTENT);//取得本機相簿的action
            startActivityForResult(albumIntent, ALBUM_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){

            if(requestCode == ALBUM_REQUEST){
                uri = data.getData();

                try{

//                    img_avatar.setImageURI(uri);

                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    update_avatar = BitmapFactory.decodeStream(inputStream);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    update_avatar.compress(Bitmap.CompressFormat.PNG,100,stream);
                    img_avatar.setImageBitmap(update_avatar);
                    bytes = stream.toByteArray();
                }catch (Exception e){
                    e.getMessage();
                }
        }

        }
    }

//    private void doCropPhoto(Uri uri) {
//        Intent intent = getCropImageIntent(uri);
//        startActivityForResult(intent,CROP_REQUEST);
//    }
//
//    private Intent getCropImageIntent(Uri uri) {
//        Intent intent = new Intent("com.android.camera.action.CROP");
//        intent.setDataAndType(uri,"image/*");
//        intent.putExtra("crop","true");
//        intent.putExtra("scale",true);
//        intent.putExtra("aspectX", 1);// 这兩項為裁剪框的比例.
//        intent.putExtra("aspectY", 1);// x:y=1:1
//        intent.putExtra("outputX", 500);//回傳照片比例X
//        intent.putExtra("outputY", 500);//回傳照片比例Y
//        intent.putExtra("return-data", true);
//        return intent;
//
//    }

}
