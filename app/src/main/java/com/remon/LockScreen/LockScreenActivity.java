package com.remon.LockScreen;

import android.content.Intent;
import android.icu.text.DateFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextClock;

import com.remon.MainActivity;
import com.remon.R;

import java.util.Date;
import java.util.Locale;

public class LockScreenActivity extends AppCompatActivity {

    float unlockPercentage = 0.0f;
    class TouchPosition {
        public float x, y;
        TouchPosition(float x, float y){
            this.x = x;
            this.y = y;
        }
    }
    private TouchPosition unlockLast = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);

        makeFullScreen();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        attachLayouts();
        attachEvents();
    }

    @Override
    public void onBackPressed() {
        return;
    }

    /**
     * A simple method that sets the screen to fullscreen.  It removes the Notifications bar,
     *   the Actionbar and the virtual keys (if they are on the phone)
     */
    public void makeFullScreen() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if(Build.VERSION.SDK_INT < 19) { //View.SYSTEM_UI_FLAG_IMMERSIVE is only on API 19+
            this.getWindow().getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        } else {
            this.getWindow().getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
    }

    /**
     * Set up layouts
     */
    public void attachLayouts(){
        TextClock time = (TextClock) findViewById(R.id.tcTime);
        TextClock date = (TextClock) findViewById(R.id.tcDate);

        // TODO: API level 17
        time.setFormat12Hour("a hh:mm");
        time.setFormat24Hour("HH:mm");

        date.setFormat12Hour(null);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            DateFormat formatter = DateFormat.getDateInstance(DateFormat.FULL, Locale.KOREAN);
            date.setFormat24Hour(formatter.format(new Date()));
        } else {
            date.setFormat24Hour("EEE MM월 dd일");
        }
    }

    public void attachEvents(){
        final View unlockImage = findViewById(R.id.touch_to_unlock);
        unlockImage.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int eid = event.getAction();
                switch (eid) {
                    case MotionEvent.ACTION_DOWN:
                        unlockPercentage = 0.0f;
                        unlockLast = new TouchPosition(event.getX(), event.getY());
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if(unlockLast != null && Math.abs(unlockPercentage) < 100.0f){
                            TouchPosition curPos = new TouchPosition(event.getX(), event.getY());
                            unlockPercentage += (curPos.x > unlockLast.x) ? -7.5f : 7.5f;
                            unlockLast = curPos;
                            unlockImage.setAlpha(1.0f - Math.abs(unlockPercentage) / 100.0f);
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        if(Math.abs(unlockPercentage) >= 100.0f){
                            finishAffinity();
                            break;
                        }
                        unlockLast = null;
                        unlockImage.setAlpha(1.0f);
                        break;

                    default:
                        break;
                }
                return true;
            }
        });

        final ImageButton imgBtn = (ImageButton) findViewById(R.id.imageButton);
        imgBtn.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    imgBtn.setAlpha(0.5f);
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    runApplication();
                    return true;
                } else {
                    imgBtn.setAlpha(1.0f);
                }
                return false;
            }
        });
    }

    public void runApplication(){
        // TODO: API level 16
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }


}
