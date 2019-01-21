package com.example.r30_a.recyclerviewpoc.controller;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.r30_a.recyclerviewpoc.R;
import com.example.r30_a.recyclerviewpoc.helper.MyContactDBHelper;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AddProfileActivity extends AppCompatActivity implements View.OnClickListener{

    Toast toast;
    EditText edtName, edtPhomeNumber, edtNote,edtEmail_Custom, edtCity, edtStreet;//使用者編輯區
    Button btnAddContact;
    LoginButton btnLoginFB;
    ContentResolver resolver;

    //取得結果用的Request Code
    private final int CAMERA_REQUEST = 1;
    private final int ALBUM_REQUEST = 2;
    private final int CROP_REQUEST = 3;

    Uri album_uri, camera_uri;
    byte[] img_avatar_bytes;
    String img_avatar_base64;
    File temp_file;
    ImageView img_avatar;
    FrameLayout pickUserPhoto;
    Bitmap update_avatar = null;
    ContentValues values;
    MyContactDBHelper myContactDBHelper;
    SharedPreferences sp;
    LoginManager loginManager;
    CallbackManager callbackManager;
    private boolean isLogin = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_profile);
        FacebookSdk.sdkInitialize(this);
        loginManager = LoginManager.getInstance();
        callbackManager = CallbackManager.Factory.create();


        initView();

    }

    @Override
    protected void onStop() {
        super.onStop();
        loginManager.logOut();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.userPhoto:
                showPopupMenu(v);
                break;
            case R.id.btnFBLogin:

                loginKB();
                break;
        }
    }

    private void loginKB() {

        if(!isLogin) {
            loginManager.setLoginBehavior(LoginBehavior.NATIVE_WITH_FALLBACK);//預設fb login的顯示方式
            //可基本取得的權限，不需經過fb的審核
            List<String> permissions = new ArrayList<>();

            permissions.add("public_profile");
            permissions.add("email");

            //permissions.add("user_friends");

            loginManager.logInWithReadPermissions(this, permissions);


            loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    //登入成功時, 透過GraphRequest來取得用戶的FB資訊
                    GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            try {
                                if (response.getConnection().getResponseCode() == 200) {
                                    //long id = object.getLong("id");
                                    edtName.setText(object.getString("name"));
                                    edtEmail_Custom.setText(object.getString("email"));
                                    edtName.setEnabled(false);
                                    edtEmail_Custom.setEnabled(false);

                                    //String birth = object.getString("birthday");


                                    //取得用戶大頭照
                                    Profile profile = Profile.getCurrentProfile();
                                    //設定大頭照大小
                                    Uri userPhoto = profile.getProfilePictureUri(300, 300);
                                    Glide.with(AddProfileActivity.this)
                                            .load(userPhoto.toString())
                                            .fitCenter()
                                            .crossFade()
                                            .into(img_avatar);
                                    isLogin = true;
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,email");
                    graphRequest.setParameters(parameters);
                    graphRequest.executeAsync();

                }

                @Override
                public void onCancel() {
                    //取消登入
                }

                @Override
                public void onError(FacebookException error) {
                    //登入失敗時
                }
            });

        }else {
            loginManager.logOut();
        }

    }


    private void initView() {
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        resolver = getContentResolver();
        temp_file = new File("/sdcard/a.jpg");
        //myContactDBHelper = MyContactDBHelper.getInstance(this);
        sp = getSharedPreferences("favorTags", Context.MODE_PRIVATE);
        edtName = (EditText) findViewById(R.id.edtContactName);
        edtPhomeNumber = (EditText) findViewById(R.id.edtPhoneNumber);
        edtNote = (EditText) findViewById(R.id.edtNote);
        edtCity = (EditText) findViewById(R.id.edtCity);
        edtStreet = (EditText) findViewById(R.id.edtStreet);
        edtEmail_Custom = (EditText)findViewById(R.id.edtEmail_custom);
        btnAddContact = (Button) findViewById(R.id.btnUpdate);
        btnLoginFB = (LoginButton) findViewById(R.id.btnFBLogin);


        btnLoginFB.setOnClickListener(this);
        img_avatar = (ImageView) findViewById(R.id.userPhoto);
        img_avatar.setOnClickListener(this);


    }

    private void showPopupMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.inflate(R.menu.popupmenu_foravatar);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.item_camera:
                        cameraStart();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
        if (resultCode == RESULT_OK) {

            if (requestCode == ALBUM_REQUEST || requestCode == CAMERA_REQUEST) {

                if (data != null && data.getData() != null) {
                    album_uri = data.getData();
                    doCropPhoto(album_uri);
                } else {
                    doCropPhoto(camera_uri);
                }
            } else if (requestCode == CROP_REQUEST) {

                try {
                    //設定縮圖大頭貼
                    setChangedAvatar(temp_file, img_avatar);
                    img_avatar.setImageDrawable(Drawable.createFromPath(temp_file.getAbsolutePath()));

                } catch (Exception e) {
                    e.getMessage();
                }
            }
        }
    }


    private void albumStart() {

        Intent albumIntent = new Intent();
        albumIntent.setType("image/*");//設定只顯示圖片區，不要秀其它的資料夾
        albumIntent.setAction(Intent.ACTION_GET_CONTENT);//取得本機相簿的action
        startActivityForResult(albumIntent, ALBUM_REQUEST);

    }

    private void cameraStart() {

        //API < 23的版本使用原來的方法
        if (Build.VERSION.SDK_INT <= 23) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//使用拍照
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            setResult(RESULT_OK, intent);
            startActivityForResult(intent, CAMERA_REQUEST);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, camera_uri);
            setResult(RESULT_OK, intent);
            startActivityForResult(intent, CAMERA_REQUEST);
        }
    }

    private void doCropPhoto(Uri uri) {
        Intent intent = getCropImageIntent(uri);
        startActivityForResult(intent, CROP_REQUEST);
    }

    //呼叫裁切圖片介面
    private Intent getCropImageIntent(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("aspectX", 1);// 这兩項為裁剪框的比例.
        intent.putExtra("aspectY", 1);// x:y=1:1
        intent.putExtra("outputX", 200);//回傳照片比例X
        intent.putExtra("outputY", 200);//回傳照片比例Y
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(temp_file));
        intent.putExtra("outputFormat", "JPEG");

        return intent;

    }

    private void setChangedAvatar(File file, ImageView img_avatar) {
        try {

            update_avatar = BitmapFactory.decodeFile(file.getAbsolutePath());
            //bitmap to byte[]
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            update_avatar.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            img_avatar_bytes = outputStream.toByteArray();
            outputStream.close();
            img_avatar.setImageBitmap(update_avatar);

        } catch (Exception e) {
            e.getMessage();
        }
    }


}
