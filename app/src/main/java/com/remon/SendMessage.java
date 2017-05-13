package com.remon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

/**
 * Created by lg on 2017-05-13.
 */

public class SendMessage {

    String mName, mAge, mDis, mMed, mP1, mBlood;
    final String fileName = "MyLocation.jpg";
    Context context;
    String message_type;
    String addr;
    public SendMessage(Context cont, String type, String address)
    {
        context = cont;
        message_type = type;
        addr = address;

        //SharedPreferences을 통해 데이터를 사용
        SharedPreferences pref = context.getSharedPreferences("pref",0);
        String nametext= pref.getString("name",null);
        String agetext=pref.getString("age",null);
        String distext=pref.getString("dis",null);
        String medtext=pref.getString("med",null);
        String btext=pref.getString("blood",null);
        String p1text=pref.getString("p1",null);

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
            intent.putExtra("sms_body", "이름 : "+mName+'\n'+"나이 : "+mAge+'\n'+"혈액형 : "+mBlood+'\n'+"의학적 질환 및 알레르기 : "+mDis+'\n'+"복용중인 약 : "+mMed+'\n'+"인 환자가 위급합니다"+addr+" 위치로 긴급출동 바랍니다!");
        }

        else if (message_type.compareTo("mEmerge")==0)
        { //비상연락망

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