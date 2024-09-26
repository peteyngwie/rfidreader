package com.smartcity.cgs;

import static android.graphics.Color.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.smartcity.SOS119;

import java.util.ArrayList;
import java.util.List;

public class AirDefenseAlert extends AppCompatActivity {

    private ImageView mAirAlertImageView;

    private int nowPicPos = 0;

    private int[] imgRes = {
            // 空襲警報的 icons  - 用來撥放 animations
            R.drawable.flee01,
            R.drawable.flee02,
            R.drawable.flee03
    };

    ImageView AirDefenseAnimation ;

    RecyclerView AirAlertrecycler;
    List<com.smartcity.cgs.ModelAirAlert> AirAlertmodelList;   // 防空警報地點資料串列
    RecyclerView.Adapter adapter;

    TextView ShelterAddresstxt ;    // 避難所的位址
    TextView ShelterAddressTitle ;  // 最新避難所

    private static View mRootView;   // root view

    private CustomAdapterAirAlert.MyViewHolder myViewHolder ;

    Handler handler ;


    String [] ShelterNames = {"嘉義市地方法院宿舍地下1樓", "嘉義市文化中心地下1樓", "北門車站飯店地下1樓"} ;  // 避難所名稱
    String [] ShelterAddresses = {"嘉義市東區北門里文化路308號之2","嘉義市東區北門里忠孝路275號","嘉義市東區林森里忠孝路306號"} ;  // 避難所的地址
    String [] Time = {"從這快步 3 分鐘到達 / 距這裡 0.6 公里","從這快步 ４ 分鐘到達 / 距這裡 0.3 公里","從這快步 4 分鐘到達 / 距這裡 0.7 公里"} ;  // 到達時間
    String [] Amount = {"332 人","2248 人","3209 人"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_defense_alert);


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

        // 防空警報相關資料建立
        AirAlertmodelList = new ArrayList<>();
        AirAlertrecycler = findViewById(R.id.airalertrecyclerView);   // 防空警報地點 recycler view
        AirAlertrecycler.setHasFixedSize(true);
        AirAlertrecycler.setLayoutManager(new LinearLayoutManager(this));

        mRootView = AirAlertrecycler.getRootView();   // getting root view


        // ShelterAddressTitle = findViewById(R.id.sheltertitletxt) ;    // 最新避難所
        // ShelterAddressTitle.setTextSize(40);     // 設定字型大小

        ShelterAddresstxt = findViewById(R.id.shelteraddresstxt);

        ShelterAddresstxt.setText("最新避難所 : "+ ShelterNames[0]);  // 預設為第X個

        // 防空警報項目初始化

        for (int i = 0; i < 3; i++) {
            AirAlertmodelList.add(new com.smartcity.cgs.ModelAirAlert(

                    R.drawable.arrowright,      // 箭頭向右
                    R.drawable.locationgreen,   // 地址 (green)
                    R.drawable.personrungreen,  // 時間與距離
                    R.drawable.amountgreen,     // 人數
                    ShelterNames[i],     // 避難所名稱
                    ShelterAddresses[i], // 避難所地址
                    Time[i],             // 步行時間
                    Amount[i]));         // 容量

        }    // end of for loop


        SharedPreferences pref = getSharedPreferences("logintable", MODE_PRIVATE);

        int loginflag  = pref.getInt("login", 0);  // 取出來 login's value 為何 , 預設值為 : 0

        SharedPreferences sharedPref = getSharedPreferences("logintable", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("login", 1);
        editor.commit();

        if ( loginflag == 0 )
            Log.d("bbb" , "目前login : " + loginflag) ;
        else
            Log.d("bbb" , "目前在 AirDefenseAlert 的 login >>>>>> " + loginflag) ;

        adapter = new CustomAdapterAirAlert(AirAlertmodelList, getApplicationContext());
        //set the adapter into recyclerView
        AirAlertrecycler.setAdapter(adapter);

        // 下面的動作就是要察覺目前在哪一個 item ( 在 scrolling 時) , 其目的是為為即時顯示避難所的地址
        AirAlertrecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (recyclerView != null && recyclerView.getChildCount() > 0) {

                    try {

                        int currentPosition = ((RecyclerView.LayoutParams) recyclerView.getChildAt(0).getLayoutParams()).getViewAdapterPosition();
                        Log.d("aaa", "" + currentPosition);  // dump current item's position


                    // 必須做的是, 只有第一項要被 highlight (黃色) , 其他的仍為 white color (白色) , 必須再做

                        switch ( currentPosition) {

                            case 0 :
                                // ShelterAddresstxt.setText(ShelterNames[0]);
                                // ShelterAddresstxt.setTextSize(26);
                                // myViewHolder.relativeLayout.setBackgroundColor(Color.parseColor("#567845"));

                                break ;
                            case 1 :
                                // ShelterAddresstxt.setText(ShelterNames[1]);
                                // ShelterAddresstxt.setTextSize(26);
                                // myViewHolder.relativeLayout.setBackgroundColor(Color.parseColor("#567845"));

                                break;
                            case 2 :
                                // ShelterAddresstxt.setText(ShelterNames[2]);
                                // ShelterAddresstxt.setTextSize(26);
                                // myViewHolder.relativeLayout.setBackgroundColor(Color.parseColor("#567845"));


                                break ;
                            case 3 :
                                break ;
                            case 4 :
                                break ;
                            case 5 :
                                break ;
                            case 6 :
                                break ;
                            case 7 :
                                break ;
                            case 8 :
                                break ;
                            case 9 :
                                break ;
                            case 10:
                                break ;
                            default:
                                break ;
                        }   // end of switch

                    } catch (Exception e) {
                        Log.d("ccc" , e.getMessage().toString()) ;   // dump the exception error message
                    }
                }

            }
        });

        // 自動關閉 - 若無人使用, 則該功能會於 3 分鐘後自動關閉且回到主畫面
        /*
        handler = new Handler(Looper.getMainLooper());   //  get main thread
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                finish();   // using finish to close current activity ( remove it from the top of stack )
                Intent i = new Intent();
                i.setClass(AirDefenseAlert.this , MainActivity.class);  // activity connection
                Bundle bundle = new Bundle();
                bundle.putInt("loginflag",1 );// 傳遞 loginflag

                // 將Bundle物件傳給intent
                i.putExtras(bundle);
                startActivity(i);

            }
        }, 5000);  // 5 secs later close the  activity

         */

    }

    public static void updateBackgroundColor() {

            mRootView.getBackground().setColorFilter(parseColor("#AA0000"), PorterDuff.Mode.DARKEN);

            if (mRootView.getBackground()!=null)
                mRootView.getBackground().clearColorFilter();

    }

    @Override
    public void finish() {

        super.finish();
        overridePendingTransition(R.anim.activity_bottom_in, R.anim.activity_bottom_out);  // fade in/out  的特效

        // handler.removeCallbacks(null);   // stop the handler

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_bottom_in, R.anim.activity_bottom_out);  // fade in/out  的特效

        Intent intent = new Intent(AirDefenseAlert.this , MainActivity.class) ;
        startActivity(intent);

    }
}