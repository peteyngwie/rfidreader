package com.smartcity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smartcity.cgs.AirDefenseAlert;
import com.smartcity.cgs.MainActivity;
import com.smartcity.cgs.R;

public class ShelterDetail extends AppCompatActivity {

    private ImageView ShelterMapImageView;

    private int nowPicPos = 0;
    private TextView ShelterTitle ;  // 避難所名稱 (主要)

    private TextView ShelterName ;    // 避難所名稱
    private TextView Shelterlocation ;  // 避難所位址
    private TextView ShelterTimeAndDistance ;  // 避難所時間距離
    private TextView ShelterAmount ;           // 避難所人數

    private TextView RoadGuide1 , RoadGuide2 , RoadGuide3 ;  // 道路指引

    private ImageView RoadGuideimg1 , RoadGuideimg2 , RoadGuideimg3;  // 道路指引圖


    private Button RightUpCornerButton ;   // 右上角的按鈕 , 用來返回測試到主畫面

    private Button BackPreviousButton ;     // 回前一頁

    Context mContext ;

    private int whichone ;

    private int[] imgRes = {
            // 空襲警報的 icons  - 用來撥放 animations
            R.drawable.flee01,
            R.drawable.flee02,
            R.drawable.flee03
    };

    private  int[] ShelterMaps = {
            R.drawable.shelter1 ,
            R.drawable.shelter2 ,
            R.drawable.shelter3
    } ;         // 避難所名稱

    private int[] SignRoadGuidelines = {
            R.drawable.arrowleft ,
            R.drawable.arrow90degleft  ,
            R.drawable.arrow90degright
    } ;


    ImageView AirDefenseAnimation ;    // 防空警報動畫

    String [] ShelterNames = { "嘉義市地方法院宿舍地下一樓" ,
                               "嘉義市文化中心地下一樓"    ,
                               "北門車站飯店地下一樓"               } ;   // 避難所名稱

    String [] ShelterAddresses = { "嘉義市東區北門里文化路308號之2" ,
                                   "嘉義市東區北門里忠孝路275號"    ,
                                   "嘉義市東區林森里忠孝路306號"     } ;  // 避難所的地址

    String [] Time = {  "從這快步 3 分鐘到達 / 距這裡 0.6 公里",
                        "從這快步 4 分鐘到達 / 距這裡 0.3 公里",
                        "從這快步 4 分鐘到達 / 距這裡 0.7 公里"  } ;      // 到達時間

    String [] Amount = { "332 人" ,
                         "2248 人",
                         "3209 人"   };                              // 人數

    String [] RoadGuidelines = {
            "經林森西路"    ,
            "向左轉走忠孝路" ,
            "目的地在左邊"  ,
            "目的地在右邊"   } ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_shelter_detail);

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

        AirDefenseAnimation = findViewById(R.id.airalertimg);  // air defense alert animation
        AnimationDrawable ani = (AnimationDrawable) getResources().getDrawable(R.drawable.airdefensealertanimation);   // 空襲警報動畫撥放


        AirDefenseAnimation.setImageDrawable(ani);   // 設置動畫
        ani.start();                                 // 播放動畫

        ShelterMapImageView = findViewById(R.id.sheltermapimg) ;      // 避難所地圖
        ShelterTitle = findViewById(R.id.sheltertitletxt);            // 避難所的名稱
        RightUpCornerButton = findViewById(R.id.descriptionButton);   // 右上角的按鈕
        ShelterName = findViewById(R.id.sheltername) ;                // 避難所名稱
        Shelterlocation = findViewById(R.id.sheltertxt) ;             // 避難所位置
        ShelterTimeAndDistance = findViewById(R.id.sheltertimetxt) ;  // 避難所時間距離
        ShelterAmount = findViewById(R.id.shelteramounttxt) ;

        RoadGuide1 = findViewById(R.id.g1) ;    // road guideline 1
        RoadGuide2 = findViewById(R.id.g2) ;    // road guideline 2
        RoadGuide3 = findViewById(R.id.g3) ;    // road guideline 3

        RoadGuideimg1  = findViewById(R.id.arrowleft) ;             // 指引第一圖
        RoadGuideimg2  = findViewById(R.id.arrow90degleft) ;        // 指引第二圖
        RoadGuideimg3  = findViewById(R.id.arrowwithleftreturn) ;   // 指引第三圖


        RightUpCornerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(ShelterDetail.this, MainActivity.class);
                view.getContext().startActivity(intent);
            }
        });

        // 回到前一頁

        BackPreviousButton = findViewById(R.id.previouspage) ;    // 前一頁

        BackPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();   // close current activity

                Intent intent = new Intent(view.getContext() , AirDefenseAlert.class) ;
                view.getContext().startActivity(intent);  // back to previous activity

                ((Activity)view.getContext()).overridePendingTransition(R.anim.activity_bottom_in, R.anim.activity_bottom_out);  // animation of activity
            }
        });


        // 接收 bundle 並且判斷是哪一個
        this.whichone = getIntent().getExtras().getInt("whichone") ;
        Log.d("ccc" , "送第" + whichone + "個項目");

        switch (this.whichone) {

            case 0:
                ShelterMapImageView.setImageDrawable(getResources().getDrawable( R.drawable.shelter1 ));  // 第一張地圖
                ShelterTitle.setText(ShelterNames[this.whichone]);
                ShelterName.setText(ShelterNames[this.whichone]);
                Shelterlocation.setText(ShelterAddresses[this.whichone]);
                ShelterTimeAndDistance.setText(Time[this.whichone]);
                ShelterAmount.setText(Amount[this.whichone]);

                // first road guideline

                RoadGuideimg1.setImageDrawable(getResources().getDrawable(SignRoadGuidelines[0])); // arrow to left
                RoadGuide1.setText(RoadGuidelines[0]);   // through guideline 1 - 經林森西路
                RoadGuide1.setTextColor(Color.parseColor("#1A9F3F"));  // green color

                // second road guideline

                RoadGuideimg2.setImageDrawable(getResources().getDrawable(SignRoadGuidelines[2])); // arrow to left ,but it disappears
                RoadGuide2.setText(RoadGuidelines[1]);   // through guideline 2 - 向左轉走忠孝路
                RoadGuide2.setTextColor(Color.parseColor("#1A9F3F"));  // green color

                // third road  guideline

                RoadGuideimg3.setImageDrawable(getResources().getDrawable(SignRoadGuidelines[2])); // arrow to left
                RoadGuideimg3.setAlpha(0.0f);     // fully transparent , it is in order to make it disappeared !
                RoadGuide3.setText("");           // through guideline 3 - 這是無需指引

                RightUpCornerButton.setText("公有建築");

                break;
            case 1:
                ShelterMapImageView.setImageDrawable(getResources().getDrawable( R.drawable.shelter2 ));  // 第二張地圖
                ShelterTitle.setText(ShelterNames[this.whichone]);
                ShelterName.setText(ShelterNames[this.whichone]);
                Shelterlocation.setText(ShelterAddresses[this.whichone]);
                ShelterTimeAndDistance.setText(Time[this.whichone]);
                ShelterAmount.setText(Amount[this.whichone]);

                RoadGuideimg1.setImageDrawable(getResources().getDrawable(SignRoadGuidelines[0])); // arrow to left
                RoadGuide1.setText(RoadGuidelines[1]);   // through guideline 1 - 經林森西路
                RoadGuide1.setTextColor(Color.parseColor("#1A9F3F"));  // green color\

                RoadGuideimg2.setImageDrawable(getResources().getDrawable(SignRoadGuidelines[2])); // arrow to left ,but it disappears
                RoadGuideimg2.setAlpha(0.0f);             // disappeared
                RoadGuide2.setText(RoadGuidelines[1]);   // through guideline 2 - 向左轉走忠孝路
                RoadGuide2.setTextColor(Color.parseColor("#1A9F3F"));  // green color

                RoadGuideimg3.setImageDrawable(getResources().getDrawable(SignRoadGuidelines[1])); // arrow to left  ,but it disappears

                RoadGuide3.setText(RoadGuidelines[2]);   // through guideline 3 - 目的地在左邊
                RoadGuide3.setTextColor(Color.parseColor("#1A9F3F"));  // green color
                RightUpCornerButton.setText("公有建築");

                break;
            case 2:
                ShelterMapImageView.setImageDrawable(getResources().getDrawable( R.drawable.shelter3 ));  // 第三張地圖
                ShelterTitle.setText(ShelterNames[this.whichone]);
                ShelterName.setText(ShelterNames[this.whichone]);
                Shelterlocation.setText(ShelterAddresses[this.whichone]);
                ShelterTimeAndDistance.setText(Time[this.whichone]);
                ShelterAmount.setText(Amount[this.whichone]);


                RoadGuideimg1.setImageDrawable(getResources().getDrawable(SignRoadGuidelines[0])); // arrow to left
                RoadGuide1.setText(RoadGuidelines[1]);   // through guideline 1 - 經林森西路
                RoadGuide1.setTextColor(Color.parseColor("#1A9F3F"));  // green color

                RoadGuideimg2.setImageDrawable(getResources().getDrawable(SignRoadGuidelines[1])); // arrow to left ,but it disappears
                RoadGuide2.setText(RoadGuidelines[1]);   // through guideline 2 - 向左轉走忠孝路
                RoadGuide2.setTextColor(Color.parseColor("#1A9F3F"));  // green color

                RoadGuideimg3.setImageDrawable(getResources().getDrawable(SignRoadGuidelines[2])); // arrow to right  ,but it disappears
                RoadGuide3.setText(RoadGuidelines[3]);   // through guideline 3 - 目的地在
                RoadGuide3.setTextColor(Color.parseColor("#1A9F3F"));  // green color

                // 飯店 - 這裡必須有些調整 (因為只有兩個字)
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(300,80) ;
                params.addRule(RelativeLayout.ALIGN_LEFT);   // 向左對齊
                params.setMargins(1450,30,40,0);  // 設定切齊


                // RightUpCornerButton.setLayoutParams(params);  // 改變寬度: 300 dp 及 切齊的地方
                RightUpCornerButton.setText("飯店");
                break ;


            default:

                Log.d("ccc" , "錯誤發生");
                break;

        }   // end of switch

    }   // end of onCreate


    @Override
    public void onBackPressed() {

        // must call a method to end Activity behind all statement
        super.onBackPressed();   // 這個要禁用 因為使用會自動在第一次進入app 退出時 不會有對話框詢問 就關閉 app
        // finish() ;    // call finish method

        mContext = getApplicationContext() ;
        Intent intent = new Intent(mContext,AirDefenseAlert.class);
        startActivity(intent);   // back to previous activity
        overridePendingTransition(R.anim.activity_bottom_in, R.anim.activity_bottom_out);  // fade in/out  的特效

    }

    @Override
    public void finish() {

        // 當 activity 結束時 , 必須將 timer 清空

        super.finish();
        overridePendingTransition(R.anim.activity_bottom_in, R.anim.activity_bottom_out);  // fade in/out  的特效

    }
}