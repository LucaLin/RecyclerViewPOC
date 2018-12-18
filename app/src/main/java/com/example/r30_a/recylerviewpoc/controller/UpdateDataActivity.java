package com.example.r30_a.recylerviewpoc.controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class UpdateDataActivity extends AppCompatActivity implements View.OnClickListener{

    TextView txvDataName, txvDataPhone;
    Button btnUpdate;
    EditText edtName, edtPhone;
    Toast toast;
    String dataId;
    ImageView img_avatar;
    FrameLayout pickUserPhoto;



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
            Bitmap bitmap_avatar = BitmapFactory.decodeByteArray(avatar_byte,0,avatar_byte.length);
            img_avatar.setImageBitmap(bitmap_avatar);
        }
        pickUserPhoto = (FrameLayout)findViewById(R.id.pickUserPhoto);
        pickUserPhoto.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.btnUpdate) {

            final String updateName = edtName.getText().toString();
            final String updatePhone = edtPhone.getText().toString();

            if (updateName.equals(txvDataName.getText()) && updatePhone.equals(txvDataPhone.getText())) {
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

                                    Intent intent = new Intent();
                                    intent.putExtra("id", dataId);
                                    intent.putExtra("Name", updateName);
                                    intent.putExtra("Phone", updatePhone);
                                    intent.putExtra("oldName", txvDataName.getText());
                                    intent.setClass(UpdateDataActivity.this, ContactsPageActivity.class);
                                    setResult(RESULT_OK, intent);
                                    toast.setText(R.string.updateDataOK);
                                    toast.show();
                                    finish();

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
                       // albumStart();
                        break;
                }
                return true;
            }
        });
        popupMenu.show();

    }
}
