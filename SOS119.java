package com.smartcity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.smartcity.cgs.MainActivity;
import com.smartcity.cgs.MyDatabaseHelper;
import com.smartcity.cgs.MyService;
import com.smartcity.cgs.R;
import com.smartcity.cgs.hotline1;
import com.smartcity.cgs.sip_utils.SipHelper;
import com.smartcity.cgs.sip_utils.SipHelperFactory;
import com.smartcity.cgs.sip_utils.SipState;

import org.linphone.core.TransportType;
import org.linphone.mediastream.video.capture.CaptureTextureView;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SOS119 extends AppCompatActivity {

    TextView DateTxt;
    TextView TempTxt;            // 溫度

    TextView AccessibilityCHTxt, AccessibilityENTxt;

    APIService apiService;                               // 這個是目前天氣溫度的 api
    APIServiceForWeatherIcon apiServiceForWeatherIcon;   // 這個是顯示天氣icon的 api

    APIAuthorize apiAuthorize = new APIAuthorize();  // authorization
    Timer WeatherTemperatureTimer = new Timer();     // 計時器 (用來更新天氣溫度)

    Timer WeatherIconUpdateTimer = new Timer();      // 計時器 (用來更新天氣圖示)
    Timer timer;
    TimerTask timerTask;
    Double time = 0.0;

    boolean timerStarted = false;

    ImageView AccessibilityImg;

    private Context mContext;

    TextView OnThePhonetxt;    // 顯示 119緊急通話中
    TextView CallDuration;     // 通話時間計算

    private AnimationDrawable ani;
    private AnimationDrawable anidot;


    String DayOfWeek = "";

    Timer Clocktimer = new Timer();     // 計時器

    private AlertDialog OnthePhone119;    // alert dialog for on the phone 119

    public static boolean stopflag = false; // timer's flag


    // 下面的是 倒數計時的關閉通話的對話框 (3秒)

    private CountDownTimer mTimer;
    private boolean mIsTimerRunning = false;

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

    private SipHelper sipHelper = null;

    /** 119 連線當中的 Dialog */
    AlertDialog alertConnect119Dialog;
    /** 119 對話框 Dialog */
    AlertDialog alert119TakingDialog;

    /** 這邊儲存 目前可以撥打 119 的電話列表 */
    private String[] number119StringArray = null;
    /** 目前是撥打 119 的電話列當中的第幾隻電話 (index 從 0 開始) */
    private int currentCallNumberArrayIndex = 0;
    /** 目前是否是從 SipStateListener Error 當中 過來的? (請詳看下方的判斷說明) */
    private boolean currentError = false;

    /** 這邊主要監聽 [當 sip 通話結束] 的時候 --> 結束通話 */
    private final SipHelper.SipStateListener sipState119Listener = new SipHelper.SipStateListener() {
        @Override
        public void onStateChange(@NonNull SipState sipState) {
            if (SipState.Call == sipState) {
                currentSipState = SipState.Call;
                Log.d("wer", "通話");
            } else if (SipState.StopProcess == sipState) {
                currentSipState = SipState.StopProcess;
                Log.d("wer", "停話");
            } else if (SipState.Registered == sipState) {
                // 註冊完成
                Log.d("wer", "註冊");
            } else if (SipState.Ringing == sipState) {
                // 響鈴中
                Log.d("wer", "響鈴中");
            } else if (SipState.Talking == sipState) {
                // 通話中
                Log.d("wer", "通話中");
            } else if (SipState.Hanguped == sipState) {
                // 通話結束
                if (currentSipState != SipState.Hanguped) {
                    currentSipState = SipState.Hanguped;
                    Log.d("wer", "通話結束");
                    endCallSipPhone();
                }
            } else if (SipState.Error == sipState) {
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

                    if (null != number119StringArray || number119StringArray.length < currentCallNumberArrayIndex + 1) {

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyService.myServiceLock = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos119);

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

        DateTxt = (TextView) findViewById(R.id.datetimetxt);
        TempTxt = (TextView) findViewById(R.id.temperaturetxt);    //

        AccessibilityImg = (ImageView) findViewById(R.id.accessbilityimg);    // 無障礙

        updateTime();              // 更新時間

        sipState119Listener.onStateChange(SipState.Init);
        showDailing119Dialog();    // call 119

        updateWeather();           // 更新現在溫度

        /*

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // 處理 Service 傳來的訊息。
                Bundle message = intent.getExtras();
                int value = message.getInt("alert");
                String strValue = String.valueOf(value);

            }
        };

        final String Action = "FilterString";
        IntentFilter filter = new IntentFilter(Action);
        // 將 BroadcastReceiver 在 Activity 掛起來。
        registerReceiver(receiver, filter);

         */


        // Again , we must create a timer ( expire for 3 mins ) and close the activity if users don't touch
        // 這裡這個功能無須用

        if (false) {
            Handler handler = new Handler(Looper.getMainLooper());   //  get main thread
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 5000);  // 5 secs later close the  activity
        }

    }  // end of onCreate()

    private void updateTime() {   // 時間更新

        Clocktimer.schedule(new TimerTask() {
            @Override
            public void run() {
                DateTxt.setText(CurrentTime());   // Get current to show

            }
        }, 0, 500);

    }    // end of updateTime

    private String CurrentTime() {

        String nowDate = new SimpleDateFormat("MM/dd").format(new Date());  // 取得目前的日期
        assert nowDate != null;
        String nowTime = new SimpleDateFormat("HH:mm").format(new Date());  // 取得目前時間
        assert nowTime != null;

        String nowDateNTime = nowDate + "\n" + nowTime;

        return nowDateNTime;   // 傳回目前的時間

    }   // end of CurrentTime

    private void showDailing119Dialog() {

        // 119 通話中的對話框

        //before inflating the custom alert dialog layout, we will get the current activity viewgroup

        Button HangUpButton;
        ViewGroup viewGroup;
        viewGroup = findViewById(android.R.id.content);

        ani = (AnimationDrawable) getResources().getDrawable(R.drawable.dialinganimation119);   // 動畫撥放
        anidot = (AnimationDrawable) getResources().getDrawable(R.drawable.dotanimation1999);  // 動畫點

        ImageView dialinglogo;            // 撥號中圖示
        ImageView dotdialinganimation;    // 撥號中 dot animation

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialing119dialog, viewGroup, false);

        HangUpButton = dialogView.findViewById(R.id.dialbtn);    // 取消撥打按鈕

        dialinglogo = dialogView.findViewById(R.id.dialimg);       // 撥號動畫 logo

        dialinglogo.setImageDrawable(ani);   // 設置動畫
        ani.start();                         // 播放動畫

        dotdialinganimation = dialogView.findViewById(R.id.dotimag);     // 點動畫

        dotdialinganimation.setImageDrawable(anidot);        // set animation
        anidot.start();                                      // 動畫撥放

        //Now we need an AlertDialog.Builder object

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // setting the view of the builder to our custom view that we already inflated

        builder.setView(dialogView);

        // finally creating the alert dialog and displaying it

        alertConnect119Dialog = builder.create();
        Window window = alertConnect119Dialog.getWindow();

        alertConnect119Dialog.setCancelable(false);
        alertConnect119Dialog.setCanceledOnTouchOutside(false);

        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 10;
        params.y = 635;  // 由底部開始計算 向上  635 dp

        window.setAttributes(params);
        window.setGravity(Gravity.BOTTOM);  // 對齊底部

        try {

            alertConnect119Dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  // 避免邊框空白 消除
            alertConnect119Dialog.show();  // show the dialog
        } catch (Throwable throwable) {
            // do nothing
        }

        // 下面是模擬的動作 -  in order to check the functionality is available , using a dummy function which
        // simulate dialing is successful

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                //過5秒後要做的事情
                try {
                    if (null != alertConnect119Dialog) alertConnect119Dialog.dismiss();                       // 關閉目前的 dialog 並且換一個對話框
                } catch (Throwable throwable) {
                    // do nothing
                }

                try {
                    if (currentSipState != SipState.Hanguped) ShowOnthePhone119(dialogView);
                } catch (Throwable throwable) {
                    // do nothing
                }
            }
        }, 5000);   // just 0.5 seconds

        // 掛斷電話 (取消通話)
        HangUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(SOS119.this, "你掛斷電話了", Toast.LENGTH_SHORT).show();

                try {
                    if (null != alertConnect119Dialog) alertConnect119Dialog.dismiss();
                    sipState119Listener.onStateChange(SipState.Hanguped);
                } catch (Throwable throwable) {
                    // do nothing
                }
            }
        });    // hang up 1999 phone call

    }    // end of showDailing1999Dialog

    private void ShowOnthePhone119(View dialog) {

        // 119 通話中對話框

        Button HangUpButton;
        ImageView Locationicon;

        OnThePhonetxt = findViewById(R.id.onthephonetxt);               // 通話中
        CallDuration = findViewById(R.id.onthephonedurationtxt);        // 通話中時間

        // OnThePhonetxt.setText("119 緊急通話中");
        // Launch timer to tick

        timer = new Timer();     // 用來計算通話時間的計時器


        startTimer();  // 啟動計時器開始計算時間

        ViewGroup viewGroup;
        viewGroup = findViewById(android.R.id.content);

        //  119 通話中的對話框顯示
        View dialogView = LayoutInflater.from(this).inflate(R.layout.onthephone119dialog, viewGroup, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // setting the view of the builder to our custom view that we already inflated

        builder.setView(dialogView);

        // finally creating the alert dialog and displaying it

        alert119TakingDialog = builder.create();
        Window window = alert119TakingDialog.getWindow();

        alert119TakingDialog.setCancelable(false);
        alert119TakingDialog.setCanceledOnTouchOutside(false);

        try {
            alert119TakingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  // 避免邊框空白 消除
            alert119TakingDialog.show();

            // this.OnthePhone119 =  alertDialog ;    // get on the phone 119 instance

            SaveOnThePhone119Dialog(alert119TakingDialog);    // 儲存的通話中的對話框
            Locationicon = dialogView.findViewById(R.id.locationimg);      // 位置 icon
        } catch (Throwable throwable) {
            // do nothing
        }

        // 119 緊急電話 - 對話框位置調整 -------------
        // 下面的 codes 是為了調整 dialog 出現的位置

        Window Dwindow = alert119TakingDialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 10;
        params.y = 635;  // bottom up 635 dp

        Dwindow.setAttributes(params);

        window.setGravity(Gravity.BOTTOM);

        HangUpButton = dialogView.findViewById(R.id.alert_dialog_119_hangup_button);        // 取消撥打按鈕

        HangUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sipState119Listener.onStateChange(SipState.Hanguped);
            }
        });

        /** 這邊是 119 的相機畫面 */
        CaptureTextureView captureTextureView = dialogView.findViewById(R.id.alert_dialog_119_capture_texture_view);
        /** 這邊是 城市守衛者 的相機畫面 */
        TextureView textureView = dialogView.findViewById(R.id.alert_dialog_119_texture_view);

        /** 初始化 和 set 狀態監聽者 */
        sipHelper = SipHelperFactory.getSipHelper(this);
        sipHelper.addListener(sipState119Listener);

        /**
         * 兩個 view 都要做設定 (設定 setVideoView 和 setLocalVideoView)，才能 [雙方都有畫面]
         * (也就是 119 看的到 城市守衛者、城市守衛者 看的到 119)
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
            SharedPreferences sharedPreferences = getSharedPreferences("siptable", Context.MODE_PRIVATE);
            // String domain = sharedPreferences.getString("domain", "0.0.0.0"); // 若取不到, 就是 0.0.0.0 (default)
            String domain ="192.168.0.135";
            /** type2Number 是 1999 的電話 --> 這邊使用不到 */
            // String type2Number  =  sharedPreferences.getString("type2Number", "1999"); // 若取不到, 就是 1999 (default)

            /**
             * type1Number 是 119 的電話  --> 我們這邊使用 1999 的電話
             *
             * 電話可能有多隻，使用字串連結，比如說長得像這樣 : "11900001,11900002"
             * 所以我們這邊先做字串切割
             */
            String type1Number  = sharedPreferences.getString("type1Number", "11000001"); // 若取不到, 就是 1999 (default)
            number119StringArray = type1Number.split(",");

            /** 目前沒有 login 的 domain --> 結束通話狀態 */
            if ("0.0.0.0".equals(domain)) {
                sipState119Listener.onStateChange(SipState.Hanguped);
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
                        // sipNumber  = cgsDatabaseCursor.getString(cgsDatabaseCursor.getColumnIndexOrThrow("sipNumber"));
                        // sipPassword = cgsDatabaseCursor.getString(cgsDatabaseCursor.getColumnIndexOrThrow("sipPassword"));
                        sipNumber = "100000001" ;
                        sipPassword = "123456" ;
                    } while (cgsDatabaseCursor.moveToNext());
                }
            }

            if (!"".equals(sipNumber) && !"".equals(sipPassword)) {
                /** 登入 */
                //Log.d("qwe", "sipNumber :" + sipNumber) ;
                //Log.d("qwe","sipPassword :" + sipPassword);

                sipHelper.login(sipNumber, sipPassword, domain, TransportType.Tcp);

                Log.d("qwe", "sipNumber :" + sipNumber) ;
                Log.d("qwe","sipPassword :" + sipPassword);

                /** 設置一下目前使用的 1999 電話 array --> 從 0 開始 */
                currentCallNumberArrayIndex = 0;
                /** 撥打電話 */
                callSipPhone();
            } else {
                /** 如果沒有 sip 帳號 和 sip 密碼 --> 結束通話狀態  */
                sipState119Listener.onStateChange(SipState.Hanguped);
            }
        } catch (Throwable throwable) {
            /** 打電話異常狀態 --> 結束通話狀態  */
            sipState119Listener.onStateChange(SipState.Hanguped);
        }
    }   // 119 通話中對話框

    private void callSipPhone() {
        try {
            /** 使用 index 來抓到目前的電話 */
            String callNumber = number119StringArray[currentCallNumberArrayIndex];

            sipState119Listener.onStateChange(SipState.Call);
            sipHelper.call(callNumber);
        } catch (Throwable throwable) {
            /** 打電話異常狀態 --> 結束通話狀態  */
            sipState119Listener.onStateChange(SipState.Hanguped);
        }
    }

    private void endCallSipPhone() {

        Toast.makeText(this, "取消通話按下去了", Toast.LENGTH_SHORT).show();

        // 1. 首先必須停掉通話時間的計數
        if (null != timer) {
            timer.cancel();   // 停止計數
            timer.purge();    // 清除計數器
        }

        // 2. 啟動倒數計時的timer (3秒)
        // 接著 , 必須有一個對話框顯示 通話已結束 (按了取消通話)
        EndingtheCallDialog();   // ending the call dialog
    }

    private void updateWeather() {

        //  每 10秒 更新一次溫度

        WeatherTemperatureTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // UI層更新
                runOnUiThread(new Runnable() {
                    @Override

                    // 下面是取出高雄目前的溫度 - 以每分鐘為一次取出來
                    // 使用 Retrofit 來取出天氣相關資料 (目前溫度)

                    public void run() {

                        GetCurrentTemperature();  // 取得目前溫度

                    }
                });

            }
        }, 0, 10000); //  每 10 秒做一次更新

    }    // end of updateWeather


    private void GetCurrentTemperature() {
        // 取得目前溫度  -
        //
        // 比較目前的位置 - (高雄)再秀出來

        apiService = RetrofitManager.getInstance().getAPI();

        Call<DataResponse> call = apiService.getDailyRainfall(apiAuthorize.getAuthorization());

        // 連線API，獲取資料
        call.enqueue(new Callback<DataResponse>() {

            @Override
            public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {

                // 連線成功
                // 取得回傳資料

                if (response.isSuccessful()) {

                    List<Station> list = (List<Station>) response.body().getRecords().getStation();
                    int item = response.body().getRecords().getStation().size();

                    Log.d("xxx", "list length :" + list.size());

                    for (int ii = 0; ii < list.size(); ii++) {


                        Log.d("xxx", "----- Station ------" + "(" + Integer.toString(ii) + ")");
                        String stationname = list.get(ii).getStationName();
                        String stationid = list.get(ii).getStationID();
                        String Temperature = list.get(ii).getWeatherElement().getairTemperature();

                        if (stationname.equals("高雄")) {

                            // Toast.makeText(MainActivity.this, "高雄", Toast.LENGTH_SHORT).show();
                            Log.e("xxx", "站名 : " + stationname);
                            Log.e("xxx", "站名 id : " + stationid);
                            Log.d("xxx", "目前溫度 : " + list.get(ii).getWeatherElement().getairTemperature());

                            TempTxt.setText(Temperature + "°");   // 更新現在溫度
                            TempTxt.setTypeface(Typeface.DEFAULT_BOLD);  // 粗體

                            break;   // 離開 for loop
                        }  // end of if

                    }   // end of for

                } else {

                    Log.d("ccc", "error");
                }
            }  // end of onResponse


            @Override
            public void onFailure(Call<DataResponse> call, Throwable t) {
                // 連線失敗
                Log.d("test", t.toString());
            }
        });  //  call.enqueue

    }  // end of  GetCurrentTemperature

    private void SaveOnThePhone119Dialog(AlertDialog dialog) {

        assert dialog != null;

        this.OnthePhone119 = dialog;
    }   // 儲存 on the phone 119 dialog instance

    private AlertDialog GetOnThePhone119Dialog() {
        return this.OnthePhone119;
    }   // 取得 on the phone 119 dialog instance

    private void EndingtheCallDialog() {

        try {
            if (null != sipHelper) sipHelper.hangUp();
        } catch (Exception exception) {
            // do nothing
        }

        // ending the call - 通話結束對話框 (3 秒後回到主畫面)

        TextInputLayout account, password;
        Button confirmation;

        TextView CountDownDurtionTxt ;     // 3秒倒數計時

        View dialogView = LayoutInflater.
                from(SOS119.this).
                inflate(R.layout.endingthecalldialog, null);   // inflate ending the call dialog layout

        AlertDialog.Builder alert = new AlertDialog.Builder(SOS119.this);

        //set our custom alert dialog to tha alertdialog builder
        alert.setView(dialogView);   // 設定dialog的 view

        final AlertDialog dialog = alert.create();

        CountDownDurtionTxt  = dialogView.findViewById(R.id.backtxt) ;  // 倒數計時(3秒) 計數器

        try {
            dialog.show();

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  // 避免邊框空白 消除
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        } catch (Throwable throwable) {
            // do nothing
        }

        // 下面是動畫的處理 ( bottom up )

        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER_VERTICAL);   //  這裡可以調整 dialog 顯示的位置 (目前是中間)  由下向上
        window.setWindowAnimations(R.style.mystyle);  //  動畫

        //this line removed app bar from dialog and make it transparent and  see the image is like floating outside dialog box.
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setWindowAnimations(R.style.mystyle);    // 添加動畫


        if (!mIsTimerRunning) {

            // 啟動定时器 (3秒)
            // Toast.makeText(SOS119.this, "啟動start3secsTimer()", Toast.LENGTH_SHORT).show();
            start3secsTimer(CountDownDurtionTxt);  // 啟動倒數計時器 (3秒)

        } else {

              // 清除定時器
              mTimer.cancel();

        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {

                try {
                    // 過三秒後要做的事情
                    if (null != dialog) dialog.dismiss();   // close ending the call dialog
                } catch (Throwable throwable) {
                    // do nothing
                }

                try {
                    if (null != alert119TakingDialog) alert119TakingDialog.dismiss(); // 關閉通話中的對話框
                } catch (Throwable throwable) {
                    // do nothing
                }

                finish();  // 關閉當前的 activity

                // 回到主畫面

                Intent intent = new Intent();
                intent.setClass(SOS119.this, MainActivity.class);

                Bundle bundle = new Bundle();

                if (bundle != null ) {
                    bundle.putInt("loginflag", 1);
                    intent.putExtras(bundle);
                    startActivity(intent);                // go to main activity
                }
            }
        }, 3000);


    }  // end of EndingtheCallDialog

    public void start3secsTimer(TextView countdowntimertxt) {


        mTimer = new CountDownTimer(4000, 1000) {

            // 為了視覺的效果,必須是4秒開始
            @Override
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;

                // 定时器計時，每秒執行一次
                countdowntimertxt.setText(Long.toString(seconds) + " 秒後回首頁");
                Log.d("hhh","剩餘 -> " + seconds + " 秒");
            }

            @Override
            public void onFinish() {
                // 定时器计时结束，触发提示

                Log.d("hhh","倒數計時结束");
            }
        };

        mTimer.start();
        mIsTimerRunning = true;

    }   // end of countdown timer (3secs)

    private void InterruptCallDialog() {
        // ending the call - 中斷通話結束對話框

        TextInputLayout account, password;
        Button confirmation;

        View dialogView = LayoutInflater.
                from(SOS119.this).
                inflate(R.layout.interruptcalldialog, null);   // inflate 中斷 the call dialog layout

        AlertDialog.Builder alert = new AlertDialog.Builder(SOS119.this);

        //set our custom alert dialog to tha alertdialog builder
        alert.setView(dialogView);   // 設定dialog的 view

        final AlertDialog dialog = alert.create();

        try {
            dialog.show();

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  // 避免邊框空白 消除
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        } catch (Throwable throwable) {
            // do nothing
        }

        // 下面是動畫的處理 ( from bottom to center )
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER_VERTICAL);   //  這裡可以調整 dialog 顯示的位置 (目前是中間)  由下向上
        window.setWindowAnimations(R.style.mystyle);  //  動畫

        //this line removed app bar from dialog and make it transperent and you see the image is like floating outside dialog box.
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setWindowAnimations(R.style.mystyle);    // 添加動畫
        dialog.getWindow().setWindowAnimations(R.style.mystyle);    // 添加動畫

        // 依序退出的對話框 , 1. 通話中的對話框 2. 緊急通話的頁面

        //  GetOnThePhone119Dialog().dismiss();   // 關閉通話中的對話框
        // finish();  // 關閉當前的 activity

                /*
                Intent intent = new Intent();
                intent.setClass(SOS119.this, MainActivity.class);

                Bundle bundle = new Bundle();

                if (bundle != null ) {
                    bundle.putInt("loginflag", 1);
                    intent.putExtras(bundle);
                    startActivity(intent);                // go to main activity
                }

                 */

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    //過兩秒後要做的事情
                    if (null != dialog) dialog.dismiss();
                } catch (Throwable throwable) {
                    // do nothing
                }
            }
        }, 2000);


    }  // end of EndingtheCallDialog

    private void startTimer() {
        // 啟動一個  timer 開始通話時間計時

        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (stopflag == false) {
                            OnThePhonetxt.setText("119緊急通話中");
                            OnThePhonetxt.setTextSize(60);   // 設定自型為 60 sp
                            time++;
                            CallDuration.setText("通話時間 " + getTimerText());  // 取得目前時間並更新
                            CallDuration.setTextSize(35);
                            Log.d("ccc", getTimerText());
                        } else {
                            // 停止計時
                            if (null != timer) {
                                timer.cancel();
                                timer.purge();   // 清除計數器
                            }
                        }
                    }
                });
            }

        };

        timer.scheduleAtFixedRate(timerTask, 0, 1000);  // 每秒更新

    }

    private String getTimerText() {
        // 計算計數器的時間
        int rounded = (int) Math.round(time);

        int seconds = ((rounded % 86400) % 3600) % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = ((rounded % 86400) / 3600);

        assert seconds < 60 && minutes < 60;       // assertion secs and minutes

        return formatTime(seconds, minutes, hours);  // return a timeformat object
    }

    private String formatTime(int seconds, int minutes, int hours) {
        return String.format("%02d", hours) + " : " + String.format("%02d", minutes) + " : " + String.format("%02d", seconds);
    }

    @Override
    public void finish() {

        // overriding finish

        super.finish();

        overridePendingTransition(R.anim.activity_bottom_in, R.anim.activity_bottom_out);
        // stop animation

        if (ani.isRunning())  // 動畫是否播放中
            ani.stop();        // 停止撥號動畫
        else
            ;                  // do nothing

        if (anidot.isRunning())
            anidot.stop();     // 停止撥號動畫
        else
            ;                   // do nothing
        // stop timer - 計時的 timer

        if (null != timer) {
            timer.cancel();
            timer.purge();   // clear the timer
        }


        if (Clocktimer != null) {
            // 時間計時
            Clocktimer.cancel();   // stop the timer
            Clocktimer.purge();    // clear the timer
            Clocktimer = null;

        }

        if (WeatherTemperatureTimer != null) {

            // 天氣計時更新
            WeatherTemperatureTimer.cancel();
            WeatherTemperatureTimer.purge();
            WeatherTemperatureTimer = null;

        }




    }   // end of finish

    @Override
    public void onBackPressed() {

        // must call a method to end Activity behind all statement
        // super.onBackPressed();   這個要禁用 因為使用會自動在第一次進入app 退出時 不會有對話框詢問 就關閉 app
        // 首先要先關閉對話框

        finish();  //  close current activity

        // super.onBackPressed();

    }   // press the back to previous key

    private View.OnKeyListener mOnKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            return false;
        }
    };



    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            //输入法按下回车键Enter
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                finish();
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            // 执行自定义操作，比如弹出一个对话框
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("确定要退出应用吗？");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setNegativeButton("取消", null);
            builder.show();

            return true; // 返回true表示事件已经被消费，不再传递给其他监听器
        }

        return super.dispatchKeyEvent(event);
    }
}