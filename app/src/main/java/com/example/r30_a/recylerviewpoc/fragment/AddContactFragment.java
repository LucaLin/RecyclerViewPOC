package com.example.r30_a.recylerviewpoc.fragment;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.r30_a.recylerviewpoc.R;
import com.example.r30_a.recylerviewpoc.model.ContactData;

import static com.example.r30_a.recylerviewpoc.util.CommonUtil.isCellPhoneNumber;


public class AddContactFragment extends Fragment {

    Toast toast;
    EditText edtName, edtPhomeNumber;//使用者編輯區
    Button btnAddContact;
    ContentResolver resolver;
    Context context;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;


    public AddContactFragment() {

    }


    public static AddContactFragment newInstance(String param1, String param2) {
        AddContactFragment fragment = new AddContactFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        toast = Toast.makeText(context, "",Toast.LENGTH_SHORT);
        resolver = context.getContentResolver();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_contact, container, false);

        edtName = (EditText)v.findViewById(R.id.edtContactName);
        edtPhomeNumber = (EditText)v.findViewById(R.id.edtPhoneNumber);
        btnAddContact = (Button)v.findViewById(R.id.btnUpdate);

        btnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = edtName.getText().toString();
                String phoneNum = edtPhomeNumber.getText().toString();
                if(TextUtils.isEmpty(name) || !isCellPhoneNumber(phoneNum)){
                    toast.setText(R.string.wrongInput);
                    toast.show();
                }else {
                    insertContact(name, phoneNum);
                    toast.setText(R.string.addSuccess);
                    toast.show();

                }
            }
        });

        return v;
    }

    public boolean insertContact(String name, String phoneNum) {

        try {
            ContentValues values = new ContentValues();

            //建立一個空白ID供新增資料用
            Uri contactUri = resolver.insert(ContactsContract.RawContacts.CONTENT_URI, values);
            long contactId = ContentUris.parseId(contactUri);

            //新增Name
            insertNameData(ContactsContract.Data.RAW_CONTACT_ID, contactId, ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
                    ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name, ContactsContract.Data.CONTENT_URI, values);


            //新增PhoneNum
            insertData(ContactsContract.Data.RAW_CONTACT_ID, contactId,
                    ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                    ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,
                    ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNum, ContactsContract.Data.CONTENT_URI, values);

        }catch (Exception e){
            e.getMessage();
            return false;
        }
        return true;
    }

    //新增資料到聯絡人表格中
    public void insertData(String rawContactIdColumn, long contactId,
                           String MIMETYPE_column, String Content_Item_Type,
                           String phoneType, int TypeMode,
                           String dataColumn, String data,
                           Uri uri, ContentValues values){

        values.clear();
        values.put(rawContactIdColumn, contactId );
        values.put(MIMETYPE_column,Content_Item_Type);
        values.put(phoneType,TypeMode);
        values.put(dataColumn, data);
        resolver.insert(uri,values);

    }

    public boolean insertNameData(String rawContactIdColumn, long contactId,
                                  String MIMETYPE_column, String Content_Item_Type,
                                  String dataColumn, String data,
                                  Uri uri, ContentValues values){

        values.clear();
        values.put(rawContactIdColumn, contactId );
        values.put(MIMETYPE_column,Content_Item_Type);
        values.put(dataColumn, data);
        resolver.insert(uri,values);
        return true;
    }


}
