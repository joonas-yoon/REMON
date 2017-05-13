package com.remon.StartPages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

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
                finish();

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
                intent.putExtra("page_id", "mEmerg");
                startActivity(intent);
            }
        });

    }
}
