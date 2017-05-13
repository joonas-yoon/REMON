package com.remon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.remon.MedicalClasses.EmergencyroomInfo;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by lg on 2017-05-13.
 */

public class SendMessage {

    final int MAX_NEAREST = 5;
    String mName, mAge, mDis, mMed, mP1, mBlood;
    final String fileName = "MyLocation.jpg";
    Context context;
    String message_type;
    String addr;
    String hospitalName;
    ArrayList<EmergencyroomInfo> result_list;
    public SendMessage(Context cont, String type, String address, String hospital, ArrayList<EmergencyroomInfo> result)
    {
        Log.d("called by MapActivity", "Hello~");
        context = cont;
        message_type = type;
        addr = address;
        hospitalName= hospital;
        result_list = result;
        //SharedPreferences을 통해 데이터를 사용
        SharedPreferences pref = context.getSharedPreferences("pref",0);
        String nametext= pref.getString("name",null);
        String agetext=pref.getString("age",null);
        String distext=pref.getString("dis",null);
        String medtext=pref.getString("med",null);
        String btext=pref.getString("blood",null);
        String p1text=pref.getString("POC1",null);

        //키값이 널인지 아닌지 확인 널이 아닐때 string 변수에 넣어 사용
        if(nametext!=null) mName =nametext+"";
        if(agetext!=null) mAge =agetext+"";
        if(distext!=null) mDis =distext+"";
        if(medtext!=null) mMed =medtext+"";
        if(btext!=null) mBlood =btext+"";
        if(p1text!=null) mP1 =p1text+"";
        SendMMS();
    }

    private void SendMMS()
    {

        //manifests에 정의된 authority가 두 번째 인자. xml폴더에 file_path참조할 것. 참조 사이트:http://kit-lab.hatenablog.jp/entry/2017/01/08/011428#これからは-contentURI
        Uri uri = FileProvider.getUriForFile(context, "com.remon.fileprovider",
                new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/DCIM/Camera/"+fileName));
        Intent intent = new Intent(Intent.ACTION_SEND);

        if(message_type.compareTo("m119")==0)
        { //119신고일 경우
            intent.putExtra("address", "119");
            String message = "[환자의 정보]\n" +
                    "\n이름 : " + mName + "\n" +
                    "\n나이 : " + mAge + "\n" +
                    "\n혈액형 : " + mBlood + "\n" +
                    "\n의학적 질환 및 알레르기 : " + mDis + "\n" +
                    "\n복용중인 약 : " + mMed + "\n\n" +
                    "[응급 환자 위치]\n\n" +
                    "환자의 위치 : " + addr + "\n\n" +
                    "[수용 가능한 응급실]\n\n";

            for(int i=0; i<MAX_NEAREST; i++) {
                double distance = result_list.get(i).getDistance();
                String hospital_name = result_list.get(i).getHospitalName();
                String hospital_addr = result_list.get(i).getAddress();
                String tel = result_list.get(i).getTelNum();
                message += "#" + (i+1) + ". " + hospital_name + "\n";
                message += "병원 주소 : " + hospital_addr + "\n";
                message += "병원 전화번호 : " + tel + "\n";
                message += "환자와 병원과의 거리 : " + String.format("%.2f", distance) + "Km" + "\n";
                message += "https://maps.google.com/?q=" + result_list.get(i).getLatitude() + "," + result_list.get(i).getLongitude() +"\n\n";
            }
            intent.putExtra("sms_body", message);
        }

        else if (message_type.compareTo("mEmerge")==0)
        {
            //비상연락망
            intent.putExtra("address", mP1);
            String message = "비상연락망에 적혀있는 연락처로 연락드립니다.\n";
            message += "현재 "+ mName +"님이 입원하였습니다.\n";
            message += "(병원명) " + hospitalName + "\n";
            message += "(위치) " + addr +"\n";
            message += "(오시는 길) https://maps.google.com/?q=" + result_list.get(0).getLatitude() + "," + result_list.get(0).getLongitude() +"\n";
            intent.putExtra("sms_body", message);
        }

        intent.addCategory("android.intent.category.DEFAULT");
        intent.putExtra("exit_on_sent", true);
        intent.putExtra (Intent.EXTRA_STREAM, uri);
        intent.setType ("image/*");
        try {
            context.startActivity(intent);

        } catch (Exception e) {
            Toast.makeText(context, "전송 실패", Toast.LENGTH_LONG).show();
            Log.d("messege_error", e.getMessage());
        }

    }
}