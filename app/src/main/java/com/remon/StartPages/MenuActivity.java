package com.remon.StartPages;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.remon.MenuSelectedPages.InfoActivity;
import com.remon.MenuSelectedPages.MapActivity;
import com.remon.MenuSelectedPages.Medical_SearchActivity;
import com.remon.R;

public class MenuActivity extends AppCompatActivity {

    final int MY_PERMISSIONS_ACCESS_FINE_LOCATION = 1;
    final int MY_PERMISSIONS_EXTERNAL_STORAGE =2;
    final int MY_PERMISSIONS_EXTERNAL_STORAGE2=3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        AskPermission();

        TextView mNotice_text = (TextView)findViewById(R.id.message_textView);
        Button mbtn_Info = (Button)findViewById(R.id.btn_info); //사용자 정보
        Button mbtn_ambul = (Button)findViewById(R.id.btn_ambul); //응급실 정보
        Button mbtn_m119 = (Button)findViewById(R.id.btn_m119); //119에 문자
        Button mbtn_med_search = (Button)findViewById(R.id.btn_med_search);//병원, 약국 검색
        Button mbtn_c119 = (Button)findViewById(R.id.btn_c119); //119에 전화
        Button mbtn_mEmerg = (Button)findViewById(R.id.btn_mEmerg); //지인 긴급 문자

        SharedPreferences pref = getSharedPreferences("pref",0); //name, 0=operating mode
        String message_text = pref.getString("Message","");

        if(!message_text.equals("")) {
            mNotice_text.setText(message_text);
            mNotice_text.setSingleLine(true);
            mNotice_text.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            mNotice_text.setSelected(true);
        }
        else mNotice_text.setText("REMON : for emergency use only");

        mbtn_Info.setOnClickListener(new View.OnClickListener() {//사용자 정보
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, InfoActivity.class);
                startActivity(intent);

            }
        });

        mbtn_ambul.setOnClickListener(new View.OnClickListener() {//응급실 정보
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, MapActivity.class);
                intent.putExtra("page_id", "ambul");
                startActivity(intent);
            }
        });
        mbtn_m119.setOnClickListener(new View.OnClickListener() {//119에 문자
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, MapActivity.class);
                intent.putExtra("page_id", "m119");
                startActivity(intent);
            }
        });
        mbtn_med_search.setOnClickListener(new View.OnClickListener() {//병원, 약국 검색
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, Medical_SearchActivity.class);
                startActivity(intent);
            }
        });
        mbtn_c119.setOnClickListener(new View.OnClickListener() {//119에 전화
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:119"));
                startActivity(intent);
            }
        });
        mbtn_mEmerg.setOnClickListener(new View.OnClickListener() {//지인 긴급 문자
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, MapActivity.class);
                intent.putExtra("page_id", "mEmerge");
                startActivity(intent);
            }
        });

    }

    void AskPermission()
    {
        //GPS permission 물어보기
        if(ContextCompat.checkSelfPermission(MenuActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(MenuActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_ACCESS_FINE_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(ContextCompat.checkSelfPermission(MenuActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
        {
            if (ContextCompat.checkSelfPermission(MenuActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                ActivityCompat.requestPermissions(MenuActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_EXTERNAL_STORAGE);
        }

        if(ContextCompat.checkSelfPermission(MenuActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            if(ContextCompat.checkSelfPermission(MenuActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED)
                ActivityCompat.requestPermissions(MenuActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_EXTERNAL_STORAGE2);
        }
    }
}

