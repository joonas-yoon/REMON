package com.remon.MenuSelectedPages;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.remon.R;
import com.remon.StartPages.MenuActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import static com.remon.R.id.blood_spinner;

public class InfoActivity extends AppCompatActivity {
    //사용자 정보
    ImageButton pict_btn;
    Bitmap profilePict;
    String fileName = "Profile.jpg";

    EditText editName,editAge,editDisease,editPhone1,editPhone2,editMedicine, editmessage;
    Spinner mBlood_Spinner, mRelation1_Spinner, mRelation2_Spinner;
    String editNameString,editAgeString,bloodString,notString;
    String editDiseaseString,editMedicineString,editPhone2String,editPhone1String;
    String mBlood;

    int bloodspinner_index, relation1spinner_index, relation2spinner_index;

    final int REQ_CODE_SELECT_IMAGE = 100;
    final int MY_PERMISSIONS_EXTERNAL_STORAGE = 1;

    ArrayAdapter<CharSequence> adspin;
    ArrayAdapter<CharSequence> adspin2;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        //SharedPreferences을 통해 데이터를 저장
        final SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);

        //text & sinner & image & button 연결
        pict_btn = (ImageButton)findViewById(R.id.pictureButton);


        editName = (EditText)findViewById(R.id.editName);
        editAge = (EditText)findViewById(R.id.editAge);
        mBlood_Spinner = (Spinner) findViewById(blood_spinner);

        editDisease = (EditText)findViewById(R.id.editDisease);
        editMedicine = (EditText)findViewById(R.id.editMedicine);
        editPhone1 = (EditText)findViewById(R.id.editPhone1);
        editPhone2 = (EditText)findViewById(R.id.editPhone2);
        editmessage =(EditText)findViewById(R.id.EditMessage);

        mRelation1_Spinner = (Spinner) findViewById(R.id.relation_spinner);
        mRelation2_Spinner = (Spinner) findViewById(R.id.relation_spinner2);

        Button savedInfo_btn = (Button)findViewById(R.id.savedInfo_btn);

        //get information from sharedpreferences
        String nametext = pref.getString("name","");
        final String agetext = pref.getString("age","");
        bloodspinner_index = pref.getInt("blood_index",0); //혈액형 mBlood_Spinner

        String distext = pref.getString("dis","");
        String medtext = pref.getString("med","");

        relation1spinner_index = pref.getInt("Relation1_index",0); //비상연락처1
        String p1text = pref.getString("POC1","");
        relation2spinner_index = pref.getInt("Relation2_index",0); //비상연락처2
        String p2text = pref.getString("POC2","");
        String ntext=pref.getString("Message","");

        //setting information
        editName.setText(nametext);
        editAge.setText(agetext);
        editDisease.setText(distext);
        editPhone1.setText(p1text);
        editMedicine.setText(medtext);
        editPhone2.setText(p2text);
        editmessage.setText(ntext);
        mBlood_Spinner.setSelection(bloodspinner_index);
        mRelation1_Spinner.setSelection(relation1spinner_index);
        mRelation2_Spinner.setSelection(relation2spinner_index);
        try {
            File imageFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera/" + fileName);
            Uri uri = FileProvider.getUriForFile(InfoActivity.this, "com.remon.fileprovider", imageFile);
            if( imageFile == null || ! imageFile.exists() ){
                pict_btn.setImageResource(R.drawable.add4);
            } else {
                pict_btn.setImageURI(uri);
            }
        } catch(Exception e) {
            Log.d("FileStreamError", e.getMessage());
        }


        //blood_spinner setting
        mBlood_Spinner.setPrompt("혈액형을 선택해주세요.");
        adspin = ArrayAdapter.createFromResource(this, R.array.blood_selected, android.R.layout.simple_spinner_item);
        adspin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBlood_Spinner.setAdapter(adspin);
        mBlood_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bloodspinner_index =position;
                mBlood = mBlood_Spinner.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //relation1_spinner setting
        mRelation1_Spinner.setPrompt("관계를 선택해주세요.");
        adspin2 = ArrayAdapter.createFromResource(this, R.array.relation_selected,    android.R.layout.simple_spinner_item);
        adspin2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRelation1_Spinner.setAdapter(adspin2);
        mRelation1_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                relation1spinner_index =position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //relation2_spinner setting
        mRelation2_Spinner.setPrompt("관계를 선택해주세요.");
        mRelation2_Spinner.setAdapter(adspin2);
        mRelation2_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                relation2spinner_index =position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //image_button_listener
        pict_btn.setOnClickListener(new View.OnClickListener() {    // 사진추가 버튼 리스너
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(InfoActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED)
                {//권한 없으면 물어보기
                    ActivityCompat.requestPermissions(InfoActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_EXTERNAL_STORAGE);
                }
                else
                {//권한 있을 때, 사진 고르기
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
                }
            }
        });

        //save_button_listener
        savedInfo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editNameString = editName.getText().toString();
                editAgeString = editAge.getText().toString();
                editDiseaseString = editDisease.getText().toString();
                editMedicineString = editMedicine.getText().toString();
                editPhone1String = editPhone1.getText().toString();
                editPhone2String = editPhone2.getText().toString();
                bloodString= mBlood_Spinner.getSelectedItem().toString();
                notString= editmessage.getText().toString();

                if(editAgeString.length()==0)
                    Toast.makeText(getApplicationContext(),"나이를 입력해주세요",Toast.LENGTH_LONG).show();
                else if(editNameString.length()==0)
                    Toast.makeText(getApplicationContext(),"이름을 입력해주세요",Toast.LENGTH_LONG).show();
                else if(editPhone1String.length()==0)
                    Toast.makeText(getApplicationContext(),"비상연락처를 입력해주세요",Toast.LENGTH_LONG).show();
                else if(bloodString.length()==0)
                    Toast.makeText(getApplicationContext(),"이름을 입력해주세요",Toast.LENGTH_LONG).show();
                else {
                    Intent intent = new Intent(InfoActivity.this, MenuActivity.class);
                    startActivity(intent);
                    saveInfo(); //정보 저장
                    finish();
                }
            }
        });
    }

    //permission 여부 선택했을 때 결과에 따른 반응
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MY_PERMISSIONS_EXTERNAL_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) //permission 허가
        {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
        }
        else //permission 거부 했을 때
            Toast.makeText(InfoActivity.this, "PERMISSION DENIED", Toast.LENGTH_LONG).show(); //Don't access EXTERNAL_STORAGE

    }
    @Override
    public void onBackPressed()
    {
        //뒤로 가기 버튼
        super.onBackPressed();
        Intent intent = new Intent(InfoActivity.this, MenuActivity.class);
        startActivity(intent);
        finish();
    }
    private void saveInfo()
    {//SharedPreferences을 통해 데이터를 저장하고 editor를 통해 키값을 저장함 사용하는곳 - menu,msg
        SharedPreferences pref = getSharedPreferences("pref",Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("name",editName.getText().toString());
        editor.putString("age",editAge.getText().toString());
        editor.putInt("blood_index", mBlood_Spinner.getSelectedItemPosition());
        editor.putString("dis",editDisease.getText().toString());
        editor.putString("med",editMedicine.getText().toString());
        editor.putInt("Relation1_index", mRelation1_Spinner.getSelectedItemPosition());
        editor.putString("POC1",editPhone1.getText().toString());
        editor.putInt("Relation2_index", mRelation2_Spinner.getSelectedItemPosition());
        editor.putString("POC2",editPhone2.getText().toString());
        editor.putString("Message", editmessage.getText().toString());

        editor.putString("blood", mBlood); //나중에 메세지에 포함시키기 위해서

        //프로필 사진 저장
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            profilePict.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);

            FileOutputStream fileOutputStream = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera/" + fileName);
            fileOutputStream.write(byteArrayOutputStream.toByteArray());
            fileOutputStream.close();

            //갤러리 새로고침
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/Camera/" + fileName);
            Uri uri = Uri.fromFile(f);
            mediaScanIntent.setData(uri);
            getApplicationContext().sendBroadcast(mediaScanIntent);
        } catch(Exception e)
        {
            Log.d("FileStreamError", e.getMessage());
        }

        editor.apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {// 갤러리가 닫힐때
        if(requestCode == REQ_CODE_SELECT_IMAGE)
        {
            if(resultCode==Activity.RESULT_OK) {
                try {
                    Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                    pict_btn.setImageBitmap(bitmap);
                    profilePict=bitmap;
                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), "파일을 불러올 수 없습니다.", Toast.LENGTH_LONG).show();
                    Log.d("Image_load_error", e.getMessage());
                }
            }
        }
    }
}
