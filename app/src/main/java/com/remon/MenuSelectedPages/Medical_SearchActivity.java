package com.remon.MenuSelectedPages;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.remon.R;

public class Medical_SearchActivity extends AppCompatActivity {
    //병원, 약국 찾기
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_search);

        Button btn_medical = (Button)findViewById(R.id.btn_medicalSearch);
        Button btn_pharmacy = (Button)findViewById(R.id.btn_pharmacySearch);

        btn_medical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Medical_SearchActivity.this, MapActivity.class);
                intent.putExtra("page_id", "hospital");
                startActivity(intent);
            }
        });

        btn_pharmacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Medical_SearchActivity.this, MapActivity.class);
                intent.putExtra("page_id", "pharmacy");
                startActivity(intent);
            }
        });

    }


}
