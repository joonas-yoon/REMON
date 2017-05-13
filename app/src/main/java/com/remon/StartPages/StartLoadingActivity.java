package com.remon.StartPages;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.remon.R;

public class StartLoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_loading);

        Handler x = new Handler();
        x.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(StartLoadingActivity.this, MenuActivity.class);
                startActivity(intent);
                finish(); //안 넣어주면 뒤로 가기 눌렀을 때 화면 보임
            }
        },1000);

    }
}
