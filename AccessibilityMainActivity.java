package com.smartcity.cgs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;

public class AccessibilityMainActivity extends AppCompatActivity {


    private TextView DateTxt ;
    private TextView TempTxt ;

    private TextView DefaultCHTxt , DefaultENTxt;


    private ImageView FrontView;

    private ImageView DefaultImg ;
    private TextView defaultCHTxt , defaultENTxt ;


    String DayOfWeek = "" ;

    private Timer Clocktimer = new Timer() ;     // 計時器

    Bitmap bitmap ;
    private ImageButton MultiLangbutton  ;    // 多語切換按鈕


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accessibility_main);


        // 隱藏標題欄
        // requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 設置 Activity 全螢幕
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        View decorView = getWindow().getDecorView();

        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);  // hide status bar and full screen

        DateTxt       = (TextView)findViewById(R.id.datetimetxt);         // date and time
        FrontView     = (ImageView) findViewById(R.id.frontviewimg) ;   // functionalities
        DefaultImg    = (ImageView) findViewById(R.id.defaultimg) ;    // default image icon
        defaultCHTxt  = (TextView) findViewById(R.id.defaultmodechtxt) ;    // default mode in chinese
        defaultENTxt  = (TextView) findViewById(R.id.defaultmodeentxt) ;   // default mode in english

        Intent intent = new Intent(AccessibilityMainActivity.this , MainActivity.class) ;

        // 一般功能畫面
        DefaultImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
                v.getContext().startActivity(intent);   // back to main activity

            }
        });

        defaultCHTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
                v.getContext().startActivity(intent);   // back to main activity

            }
        });

        defaultENTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
                v.getContext().startActivity(intent);   // back to main activity

            }
        });

    }

    @Override
    public void finish() {

     // finish current activity with animation
        super.finish();

        overridePendingTransition(R.anim.activity_bottom_in, R.anim.activity_bottom_out);  // fade in/out  的特效
    }
}