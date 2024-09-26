package com.smartcity.cgs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.smartcity.SOS119;
import com.smartcity.cgs.sip_utils.SipHelper;
import com.smartcity.cgs.sip_utils.SipHelperFactory;
import com.smartcity.cgs.sip_utils.SipState;

import org.linphone.core.TransportType;
import org.linphone.mediastream.video.capture.CaptureTextureView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class hotline1 extends AppCompatActivity {

    TextView DateTxt ;

    Timer timer;
    TimerTask timerTask;
    Double time = 0.0;

    public boolean timerStarted = false;

    TextView OnThePhonetxt ;

    TextView CallDuration ;

    TextView BackMainActivityTimerTxt ;  // 這個是一個 timer 用來計算返回首頁的

    Context mContext ;

    private SipHelper sipHelper = null;

    /**
     * 目前的 sipHelper 的狀態
     * 初始化無狀態 : None
     * 確認打電話 : Init
     * 確認打電話、但是被用戶取消 : StopProcess
     * 確認打電話、正在響鈴 : call
     * 確認打電話、正在響鈴，被取消 : Hanguped
     * 正在通話當中、掛斷 (可能是雙方的其中之一掛斷的) : Hanguped
     */
    private SipState currentSipState = SipState.None;

    private AlertDialog alertDialogOnThePhone;

    private ImageView mImageView;

    private int nowPicPos = 0;
    private int[] imgRes = {
            // 1999 撥號的icons  - 用來撥放 animations
            R.drawable.dial199901,
            R.drawable.dial199902,
            R.drawable.dial199901
    };

    public static boolean stopflag = false ; // timer's flag


    // 3 秒倒數計時 - 取消通話
    private CountDownTimer mTimer;
    private boolean mIsTimerRunning = false;

    /** 這邊儲存 目前可以撥打 1999 的電話列表 */
    private String[] number1999StringArray = null;
    /** 目前是撥打 1999 的電話列當中的第幾隻電話 (index 從 0 開始) */
    private int currentCallNumberArrayIndex = 0;
    /** 目前是否是從 SipStateListener Error 當中 過來的? (請詳看下方的判斷說明) */
    private boolean currentError = false;

    /** 這邊主要監聽 [當 sip 通話結束] 的時候 --> 結束通話 */
    private final SipHelper.SipStateListener sipState1999Listener = new SipHelper.SipStateListener() {
        @Override
        public void onStateChange(@NonNull SipState sipState) {
            if (SipState.Call == sipState) {
                currentSipState = SipState.Call;
                Log.d("qwe", "通話");

            } else if (SipState.StopProcess == sipState) {
                currentSipState = SipState.StopProcess;
                Log.d("qwe", "停話");

            } else if (SipState.Registered == sipState) {
                // 註冊完成
                Log.d("qwe", "註冊");

            } else if (SipState.Ringing == sipState) {
                // 響鈴中
                Log.d("qwe", "響鈴中");

            } else if (SipState.Talking == sipState) {
                // 通話中
                Log.d("qwe", "通話中");


            } else if (SipState.Hanguped == sipState) {
                // 通話結束
                if (currentSipState != SipState.Hanguped) {
                    currentSipState = SipState.Hanguped;
                    Log.d("qwe", "通話結束");
                    endCallSipPhone();
                }
            } else if (SipState.Error == sipState) {
                Log.d("qwe", "錯誤發生");
                /**
                 * John Hsu <john.hsu@weitech.com.tw> 2024/08/28
                 * 這邊需要解釋一下 :
                 *
                 * 1. 當打電話時、進入這個狀態的時機
                 *    可能是 對方關機 or 目前這個號碼沒有註冊 ... 等等的情況
                 *
                 * 2. 我們抓 API 的時候，會給多個號碼，比如 : 號碼 1、號碼 2、號碼 3
                 *    讓用戶能夠一個電話不通、可以打下一個
                 *
                 * 3. 當進入 Error 之後、會接續呼叫 Released
                 *
                 * 所以根據以上、我們的判斷邏輯會這樣寫
                 * 當 error 發生的時候 --> 我們記錄一個標誌位 currentError == true
                 * 然後等待 Released 被呼叫的時候、使用另外一組號碼進行撥打電話
                 */
                currentError = true;
            } else if (SipState.Released == sipState) {
                Log.d("wer", "release ");

                /**
                 * 如果目前打的電話有問題 --> 要看有沒有下一隻電話，如果有的話、就打下一隻電話
                 *
                 * 1. 查詢有沒有下一隻 --> 目前的電話為 number1999StringArray 裡面、 index 為 currentCallNumberArrayIndex 的電話
                 *    所以下一隻為 index 加上 1  (currentCallNumberArrayIndex + 1)
                 *    --> 先看一下 index 加上 1，array 有沒有爆掉 ?
                 *        --> 如果已經爆掉、代表目前沒有下一個號碼可以進行嘗試了 --> 結束
                 *    --> (防呆) 如果現在 array 是空的 --> 結束
                 *
                 * 2. 如果有下一組號碼 --> 目前的 index + 1 (代表目前指向下一組號碼)
                 *    然後呼叫 打電話的 方法 callSipPhone()
                 */
                if (currentError) {

                    if (null != number1999StringArray || number1999StringArray.length < currentCallNumberArrayIndex + 1) {

                        currentCallNumberArrayIndex++;
                        currentError = false;

                        callSipPhone();
                    }
                }
            } else {
                // do nothing
            }
        }
    };

    public CountDownTimer endingcalltimer  =  new CountDownTimer(4000, 1000) {
        // 設置一個 timer ( 4 秒開始是因為 剛進入畫面時, 必須留有視覺上的效果 )
        @Override
        public void onTick(long millisUntilFinished) {
            // 倒數中 ...
            long sec = millisUntilFinished / 1000 ;

            if ( sec != 0 ) {
                // formating !
                if ( sec > 3 ) {

                    BackMainActivityTimerTxt.setText((millisUntilFinished / 1000) + " 秒後回首頁");
                    Log.d("jjj" , "剩" + BackMainActivityTimerTxt.getText().toString()) ;
                }
                else {
                    BackMainActivityTimerTxt.setText("0" + (millisUntilFinished / 1000) + " 秒後回首頁");
                    Log.d("jjj" , "剩" + BackMainActivityTimerTxt.getText().toString()) ;

                }


            }
            else {

                // 倒數時間到 !

                overridePendingTransition(R.anim.activity_bottom_in , R.anim.activity_bottom_out);  // animation
            }

        }

        @Override
        public void onFinish() {
            // 倒數完成
            BackMainActivityTimerTxt.setEnabled(true);
            // countdownTitle.setText("");
        }
    };

    Timer Clocktimer = new Timer() ;      //  計時器 更新時間之用
    Timer AnimationTimer = new Timer() ;  //  計時器 更新動畫之用

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_hotline1);

        DateTxt =  (TextView)findViewById(R.id.datetimetxt) ;

        updateTime();     // 更新時間

        showCustomDialog();   // 顯示一個詢問的對話框 詢問是否要撥號 1999
        mContext =  hotline1.this ;

    }   // end of onCreate

    // 這裡與要一個 dialog 用來詢問

    private void updateTime( ) {

        Clocktimer.schedule(new TimerTask() {
            @Override
            public void run() {
                DateTxt.setText(CurrentTime());   // Get current to show

            }
        },0,500);

    }    // end of updateTime

    // 是否撥打 1999 ? 對話框
    private void showCustomDialog() {

        // before inflating the custom alert dialog layout, we will get the current activity viewgroup
        Button Confirmation , Cancel ;
        ViewGroup viewGroup;
        viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.hotline, viewGroup, false);

        Confirmation = dialogView.findViewById(R.id.dialbtn) ;    // 確定撥打按鈕
        Cancel = dialogView.findViewById(R.id.cancelbtn);         // 取消按鈕

        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it

        AlertDialog alertDialog = builder.create();
        Window window = alertDialog.getWindow();
        window.setGravity(Gravity.CENTER_VERTICAL);   //  這裡可以調整 dialog 顯示的位置 (目前是中間)
        window.setWindowAnimations(R.style.mystyle);  //  動畫

        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  // 避免邊框空白 消除
        alertDialog.show();

        // 確定撥打
        Confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(hotline1.this, "確定撥打", Toast.LENGTH_SHORT).show();
                // if you click the button , the dialing animation play over and over till

                alertDialog.dismiss();     // close current dialog

                currentSipState = SipState.Init;
                showDailing1999Dialog();   // dialing 1999 (確定撥出)
            }
        });

        // 取消  - 這裡取消後 , 返回到主畫面

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                alertDialog.dismiss();  // 關閉目前的對話框

                Intent intent = new Intent();
                intent.setClass(hotline1.this, MainActivity.class);

                Bundle bundle = new Bundle();

                if (bundle != null ) {
                    ((Activity)view.getContext()).overridePendingTransition(R.anim.activity_bottom_in, R.anim.activity_bottom_out);
                    bundle.putInt("loginflag", 1);
                    intent.putExtras(bundle);
                    view.getContext().startActivity(intent);
                }
                else {}   // null bundle

            }
        });

    }   // end of 1999 dialing dialog

    // 1999 撥號中對話框
    private void showDailing1999Dialog() {

        // before inflating the custom alert dialog layout, we will get the current activity viewgroup
        Button HangUpButton  ;
        ViewGroup viewGroup  ;
        viewGroup = findViewById(android.R.id.content);

        AnimationDrawable ani = (AnimationDrawable)getResources().getDrawable(R.drawable.dialinganimation1999);   // 動畫撥放
        AnimationDrawable anidot = (AnimationDrawable) getResources().getDrawable(R.drawable.dotanimation1999) ;  // 動畫點


        ImageView dialinglogo ;            // 撥號中圖示

        ImageView dotdialinganimation   ;  // 撥號中 dot animation

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialing1999dialog, viewGroup, false);
        // 1999 市民專線

        HangUpButton = dialogView.findViewById(R.id.dialbtn) ;    // 取消撥打按鈕

        dialinglogo= dialogView.findViewById(R.id.dialimg);       // 撥號動畫 logo

        dialinglogo.setImageDrawable(ani);   // 設置動畫
        ani.start();                         // 播放動畫

        dotdialinganimation = dialogView.findViewById(R.id.dotimag) ;     // 點動畫

        dotdialinganimation.setImageDrawable(anidot);        // set animation
        anidot.start();                                      // 動畫撥放

        //Now we need an AlertDialog.Builder object

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // setting the view of the builder to our custom view that we already inflated

        builder.setView(dialogView);

        // finally creating the alert dialog and displaying it

        AlertDialog alertDialog = builder.create();
        Window window = alertDialog.getWindow();

        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  // 避免邊框空白 消除
        alertDialog.show();

        // 下面是模擬的動作 -  in order to check the functionality is available , using a dummy function which
        // simulate dialing is successful

        Handler handler = new Handler();

        handler.postDelayed(new Runnable(){
            @Override
            public void run() {
                /**
                 * 有一個情況是用戶在尚未開始撥打之前 (撥打功能寫在 ShowOnthePhone() 裡面)
                 * 就按下了 [掛斷電話] --> 這時候 currentSipState 會是 StopProcess
                 *
                 * 所以如果是 StopProcess、我們就不執行以下的邏輯了
                 */
                if (currentSipState == SipState.Init) {
                    alertDialog.dismiss();   // 關閉目前的 dialog 並且換一個對話框
                    ShowOnthePhone();        // 1999 通話中  ...
                }
            }}, 3000);

        // ImageViewAnimatedChange(dialogView.getContext() ,dialinglogo ,  )
        // 掛斷電話 (取消通話)

        HangUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * 這邊是 : 當用戶在 [尚未撥號] 之前按下 取消通話 的按鈕
                 * SipState 變成 StopProcess
                 */
                sipState1999Listener.onStateChange(SipState.StopProcess);

                stopflag = true ;

                Toast.makeText(hotline1.this, "掛斷電話", Toast.LENGTH_SHORT).show();
                // hang up phone
                alertDialog.dismiss();   // close the 1999 dialing dialog and back to main activity

                Intent intent = new Intent();
                intent.setClass(hotline1.this, MainActivity.class);

                Bundle bundle = new Bundle();

                if (bundle != null ) {

                    ((Activity) v.getContext()).overridePendingTransition(R.anim.activity_bottom_in, R.anim.activity_bottom_out);
                    bundle.putInt("loginflag", 1);
                    intent.putExtras(bundle);

                    v.getContext().startActivity(intent);  // jump to Main Activity
                }

                // 動畫是否播放中
                // 停止撥號動畫
                if (ani.isRunning()) ani.stop();

                // 停止撥號動畫
                if (anidot.isRunning()) anidot.stop();
            }
        });    // hang up 1999 phone call

    }          // end of showDailing1999Dialog


    private void ShowOnthePhone() {

        // 1999 通話中對話框 (有人在說話圖)

        Button HangUpButton  ;
        //ImageView Locationicon ;

        OnThePhonetxt = findViewById(R.id.onthephonetxt);             // 通話中
        CallDuration = findViewById(R.id.onthephonedurationtxt) ;     // 通話中時間

        OnThePhonetxt.setText("1999 通話中");

        // Launch timer to tick
        Log.d("plk" , "ShowOnthePhone");
        timer = new Timer() ;     // initialize a timer

        if (timerStarted == false )


            startTimer();         // 啟動一個 timer

        else  ;


        Log.d("sss" , "startTimer()");

        ViewGroup viewGroup  ;
        viewGroup = findViewById(android.R.id.content);

        //  通話中的對話框顯示
        View dialogView = LayoutInflater.from(this).inflate(R.layout.onthephone1999dialog, viewGroup, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // setting the view of the builder to our custom view that we already inflated

        builder.setView(dialogView);

        // finally creating the alert dialog and displaying it

        alertDialogOnThePhone = builder.create();
        Window window = alertDialogOnThePhone.getWindow();

        alertDialogOnThePhone.setCancelable(false);
        alertDialogOnThePhone.setCanceledOnTouchOutside(false);

        alertDialogOnThePhone.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  // 避免邊框空白 消除
        alertDialogOnThePhone.show();

        // 向底部方向計算距離
        // 下面的 codes 是為了調整 dialog 出現的位置

        Window Dwindow = alertDialogOnThePhone.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 10;
        params.y = 635;  // bottom up 635 dp

        Dwindow.setAttributes(params);

        window.setGravity(Gravity.BOTTOM);

        //Locationicon = dialogView.findViewById(R.id.locationimg) ;      // 位置 icon
        // 1999 市民專線

        HangUpButton = dialogView.findViewById(R.id.hot_line_hangup_button) ;        // 取消撥打按鈕

        HangUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sipState1999Listener.onStateChange(SipState.Hanguped);
            }
        });

        /** 這邊是 1999 的相機畫面 */
        CaptureTextureView captureTextureView = dialogView.findViewById(R.id.hot_line_capture_texture_view);
        /** 這邊是 城市守衛者 的相機畫面 */
        TextureView textureView = dialogView.findViewById(R.id.hot_line_texture_view);

        /** 初始化 和 set 狀態監聽者 */
        sipHelper = SipHelperFactory.getSipHelper(this);
        sipHelper.addListener(sipState1999Listener);

        /**
         * John Hsu <john.hsu@weitech.com.tw> 2024/08/20
         * 兩個 view 都要做設定 (設定 setVideoView 和 setLocalVideoView)，才能 [雙方都有畫面]
         * (也就是 1999 看的到 城市守衛者、城市守衛者 看的到 1999)
         *
         * 但是因為我們目前的需求上，城市守衛者 這邊 [不需要看到 自己 的畫面]
         * 所以我們將 client 端的 這邊 textureView 設定為 長寬 1dp (所以基本上就是看不到)
         */
        sipHelper.setVideoView(captureTextureView);
        sipHelper.setLocalVideoView(textureView);

        /**
         * 以下開始 sip 的登入和 打電話
         *      1. userNmae、password 在 db 裡面
         *      2. domain : 在 SharedPreferences 裡面
         */

        try {

            Log.d("plk","llll");
            SharedPreferences sharedPreferences = getSharedPreferences("siptable", Context.MODE_PRIVATE);
            String domain = sharedPreferences.getString("domain", "192.168.0.135"); // 若取不到, 就是 0.0.0.0 (default)
            Log.d("plk","domain : " + domain);


            /** type1Number 是 119 的電話 --> 這邊使用不到 */
            //String type1Number  = sharedPreferences.getString("type1Number", "1999"); // 若取不到, 就是 1999 (default)

            /**
             * type2Number 是 1999 的電話 --> 我們這邊使用 1999 的電話
             *
             * 電話可能有多隻，使用字串連結，比如說長得像這樣 : "199900001,199900002"
             * 所以我們這邊先做字串切割
             */
            String type2Number  =  sharedPreferences.getString("type2Number", "199900001"); // 若取不到, 就是 1999 (default)
            number1999StringArray = type2Number.split(",");

            Log.d("plk","type2Number : " + type2Number);

            /** 目前沒有 login 的 domain --> 結束通話狀態 */
            if ("0.0.0.0".equals(domain)) {
                endCallSipPhone();
                return;
            }

            String sipNumber = "";
            String sipPassword = "";

            MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(this);
            SQLiteDatabase sqLiteDatabase = myDatabaseHelper.getWritableDatabase();

            Cursor cgsDatabaseCursor = sqLiteDatabase.rawQuery("SELECT * FROM cgs1", null);
            cgsDatabaseCursor.moveToFirst();

            int cgsDatabaseCursorCount = cgsDatabaseCursor.getInt(0);

            if (0 != cgsDatabaseCursorCount) {

                if (cgsDatabaseCursor.moveToFirst()) {

                    int howmany = 0 ;

                    do {
                        sipNumber  = cgsDatabaseCursor.getString(cgsDatabaseCursor.getColumnIndexOrThrow("sipNumber"));
                        sipPassword = cgsDatabaseCursor.getString(cgsDatabaseCursor.getColumnIndexOrThrow("sipPassword"));

                    } while (cgsDatabaseCursor.moveToNext());
                }
            }

//            if (!"".equals(/*sipNumber*/100000001 ) && !"".equals(/*sipPassword*/ 123456)) {
            if(1==1)
            {                /** 登入 */
                Log.d("plk","jjjjjjjj");
                sipHelper.login("100000001"/*sipNumber*/,"123456" /*sipPassword*/, domain, TransportType.Tcp);

                Log.d("plk","sipNumber:  " + sipNumber) ;
                Log.d("plk","sipPassword:  " + sipPassword) ;


                /** 設置一下目前使用的 1999 電話 array --> 從 0 開始 */
                currentCallNumberArrayIndex = 0;
                /** 撥打電話 */
                callSipPhone();
            } else {
                Log.d("plk","lllll");
                /** 如果沒有 sip 帳號 和 sip 密碼 --> 結束通話狀態  */
                endCallSipPhone();
            }
        } catch (Throwable throwable) {
            /** 打電話異常狀態 --> 結束通話狀態  */
            Log.e("plk",throwable.getMessage().toString());
            endCallSipPhone();
        }
    }  // end of ShowOnthePhone

    private void callSipPhone() {
        try {
            /** 使用 index 來抓到目前的電話 */
            String callNumber = number1999StringArray[currentCallNumberArrayIndex];

            sipState1999Listener.onStateChange(SipState.Call);
            sipHelper.call(callNumber);
        } catch (Throwable throwable) {
            /** 打電話異常狀態 --> 結束通話狀態  */
            endCallSipPhone();
        }
    }

    private void endCallSipPhone() {
        // setting timer flag is disable
        stopflag = true;

        Toast.makeText(hotline1.this, "取消通話", Toast.LENGTH_SHORT).show();

        // 出現一個對話框顯示 通話已結束
        EndingtheCallDialog();  // drop the call
    }

    private void EndingtheCallDialog () {

        try {
            if (null != sipHelper) sipHelper.hangUp();
        } catch (Exception exception) {
            // do nothing
        }

        endingcalltimer.start();  // 倒數計時 timer
        // ending the call - 通話結束對話框

        TextInputLayout account , password  ;
        Button confirmation ;

        View dialogView = LayoutInflater.
                from(hotline1.this).
                inflate(R.layout.endingthecalldialog, null);   // inflate ending the call dialog layout

        AlertDialog.Builder alert = new AlertDialog.Builder(hotline1.this);

        //set our custom alert dialog to tha alertdialog builder
        alert.setView(dialogView);   // 設定dialog的 view

        final AlertDialog dialog = alert.create();

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  // 避免邊框空白 消除

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // 下面是動畫的處理 ( from bottom to center )

        BackMainActivityTimerTxt = dialog.findViewById(R.id.backtxt) ;   // 通話 3 秒返回

        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER_VERTICAL);   //  這裡可以調整 dialog 顯示的位置 (目前是中間)  由下向上
        window.setWindowAnimations(R.style.mystyle);  // 動畫

        //this line removed app bar from dialog and make it transperent and you see the image is like floating outside dialog box.
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Toast.makeText(mContext, "倒數計時", Toast.LENGTH_SHORT).show();

        Log.d("xxxx" , "啟動倒數計時") ;

        endingcalltimer.start();  // 倒數計時 timer

        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){

            @Override
            public void run() {

                //過三秒後要做的事情
                dialog.dismiss();   // close ending the call dialog
                alertDialogOnThePhone.dismiss();  // close on the phone dialog

                // 依序退出的對話框 , 1. 通話中的對話框 2. 緊急通話的頁面

                // finish();  // 關閉當前的 activity

                finish();
                endingcalltimer.cancel();   // 停止 timer
            }}, 3000);

    }  // end of EndingtheCallDialog

    private void startTimer()
    {

        timerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {

                        if (stopflag == false ) {
                            Log.d("sss", "stopflag:" + Boolean.toString(stopflag));
                            // OnThePhonetxt.setText("1999 通話中");
                            time++;
                            Log.d("sss" , "時間: " + time ) ;
                            CallDuration.setText("通話時間 " + getTimerText());  // 取得目前時間並更新
                            CallDuration.setTextSize(25);
                        }
                        else {
                            // 停止計時
                            Log.d("sss" , "stopflag:" + Boolean.toString(stopflag)) ;
                            timer.cancel();
                            timer.purge();   // clear the timer
                            stopflag = false ; // restore the default value
                        }

                    }
                });

            }

        };

        timer.scheduleAtFixedRate(timerTask, 0 ,1000);


    }


    // 倒數 3 秒返回主畫面
    /*
    endingcalltimer = new CountDownTimer(4000, 1000) {
        // 設置一個 timer ( 4 秒開始是因為 剛進入畫面時, 必須留有視覺上的效果 )
        @Override
        public void onTick(long millisUntilFinished) {
            // 倒數中 ...
            long sec = millisUntilFinished / 1000 ;

            if ( sec != 0 ) {
                // formating !
                if ( sec > 3 ) {

                    BackMainActivityTimerTxt.setText((millisUntilFinished / 1000) + " 秒後回首頁");
                    Log.d("jjj" , "剩" + BackMainActivityTimerTxt.getText().toString()) ;
                }
                else {
                    BackMainActivityTimerTxt.setText("0" + (millisUntilFinished / 1000) + " 秒後回首頁");
                    Log.d("jjj" , "剩" + BackMainActivityTimerTxt.getText().toString()) ;

                }


            }
            else {

                // 倒數時間到 !

                overridePendingTransition(R.anim.activity_bottom_in , R.anim.activity_bottom_out);  // animation
            }

        }

        @Override
        public void onFinish() {
            // 倒數完成
            BackMainActivityTimerTxt.setEnabled(true);
            // countdownTitle.setText("");
        }
    };

    public void oncancel(View v) {
        endingcalltimer.cancel();

    }

     */

    /**
     * 开始倒计时
     * @param v
     */
    public void restart(View v) {
        endingcalltimer.start();

    }

    public static void ImageViewAnimatedChange(Context c, final ImageView v, final Bitmap new_image) {

        final Animation anim_out = AnimationUtils.loadAnimation(c, android.R.anim.fade_out);
        final Animation anim_in  = AnimationUtils.loadAnimation(c, android.R.anim.fade_in);

        anim_out.setAnimationListener(new Animation.AnimationListener()
        {

            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}

            @Override public void onAnimationEnd(Animation animation)
            {
                v.setImageBitmap(new_image);
                anim_in.setAnimationListener(new Animation.AnimationListener() {

                    @Override public void onAnimationStart(Animation animation)  {}
                    @Override public void onAnimationRepeat(Animation animation) {}
                    @Override public void onAnimationEnd(Animation animation)    {}
                });

                v.startAnimation(anim_in);
            }
        });

        v.startAnimation(anim_out);
    }


    private String getTimerText()
    {
        int rounded = (int) Math.round(time) - 1 ;

        int seconds = ((rounded % 86400) % 3600) % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = ((rounded % 86400) / 3600);

        // Log.d("sss", "時:" + hours ) ;
        // Log.d("sss" ,"分:" + minutes) ;
        // Log.d("sss", "秒:" + seconds) ;


        assert seconds <60 && minutes <60 ;       // assertion secs and minutes



        return formatTime(seconds, minutes, hours);  // return a timeformat object

    }

    private String formatTime(int seconds, int minutes, int hours)
    {

        String Duration ;
        Log.d("sss" , "時:" + String.format("%02d",hours)) ;
        Log.d("sss" , "分:" + String.format("%02d",minutes)) ;
        Log.d("sss" , "秒:" + String.format("%02d",seconds)) ;

        Duration = String.format("%02d",hours) + " : " + String.format("%02d",minutes) + " : " + String.format("%02d",seconds);

        return  Duration ; /* String.format("%02d",hours) + " : " + String.format("%02d",minutes) + " : " + String.format("%02d",seconds); */
    }


    private String CurrentTime() {

        String nowDate = new SimpleDateFormat("MM/dd").format(new Date());  // 取得目前的日期
        assert nowDate != null;
        String nowTime = new SimpleDateFormat("HH:mm").format(new Date());  // 取得目前時間
        assert nowTime != null ;

        String nowDateNTime = nowDate + "\n" + nowTime;

        return nowDateNTime  ;   // 傳回目前的時間

    }

    @Override
    public void finish() {

        super.finish();

        // restore timer initialization value
        timerStarted = false;
        timerTask.cancel();  // cancel the timer (必須將timer取消掉)
        overridePendingTransition(R.anim.activity_bottom_in, R.anim.activity_bottom_out);  // fade in/out  的特效
    }

    @Override
    public void onBackPressed() {

        // must call a method to end Activity behind all statement
        super.onBackPressed();
        finish() ;    // call finish method to finish current activity
    }

}
