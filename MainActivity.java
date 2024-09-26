package com.smartcity.cgs;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

import com.smartcity.GestureListener;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import android.Manifest;
import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
// import android.icu.util.TimeZone;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StatFs;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.smartcity.APIAuthorize;
import com.smartcity.APIService;
import com.smartcity.APIServiceForWeatherIcon;
import com.smartcity.AccommodationWithSideBar;
import com.smartcity.CgsImgListArray;
import com.smartcity.CgsTravelImgListjsonboject;
import com.smartcity.CgsTravelListArray;
import com.smartcity.CgsTravelListjsonboject;
import com.smartcity.ConnectionReciver;
import com.smartcity.DataResponse;
import com.smartcity.DataResponseWeatherIcon;
import com.smartcity.DatabaseHelper;
import com.smartcity.Location;
import com.smartcity.ParameterW;
import com.smartcity.RetrofitManager;
import android_serialport_api.SerialPort;
// import com.smartcity.SerialPort;
import com.smartcity.SerialPortFinder;
import com.smartcity.SerialPortPreferences;
import com.smartcity.Station;
import com.smartcity.TimeNElementValue;
import com.smartcity.TimeW;
import com.smartcity.TravelListArray;
import com.smartcity.TravelListjsonboject;
import com.smartcity.WeatherElement;
import com.smartcity.WeatherElementObject;
import com.smartcity.WeatherTimeNStatus;
import com.smartcity.api.ApiResponse;
import com.smartcity.api.Cgs;
import com.smartcity.api.CgsImg;
import com.smartcity.api.InterBoxCgs;
import com.smartcity.api.Sip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serial;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.app.Application ;   // using Application for global devices detection

public class MainActivity extends AppCompatActivity implements  ConnectionReciver.ReceiverListener {

    /////////// RS232 command / uart / serial port communcation

    private  com.smartcity.Application mApplication;
    private SerialPortFinder mSerialPortFinder;

    private ImageView tryimg ;
    private TextView DateTxt;
    private TextView TempTxt;          // 右上角溫度
    private ImageView WeatherImage;    // 即時右上角天氣預報

    private Handler handlerForDetecting = new Handler();

    TextView CountDownMessageTxt;

    static boolean fileconvertFinishFlag = false ;     // convert files's flag

    TextView GreetingTxt;
    TextView GreetingTxt2;
    TextView AccessibilityCHTxt, AccessibilityENTxt;

    private volatile boolean isRunning = true;   // check thread is running

    private List<WeatherTimeNStatus> WeatherStatus = new ArrayList<>();   // 用來存放 36 小時的天氣狀態資料 (起終時間 , 天氣狀態)

    ImageView FrontView;

    ImageView AccessibilityImg;

    boolean delete = false;
    View TaxiDriverDialogView;

    String DayOfWeek = "";

    Timer Clocktimer = new Timer();                // 計時器 (用來更新時間)
    Timer WeatherTemperatureTimer = new Timer();   // 計時器 (用來更新天氣溫度)

    Timer WeatherIconUpdateTimer = new Timer();   // 計時器 (用來更新天氣圖示)

    Timer WeatherDialogIconUpdateTimer = new Timer();  // 計時器 (用來更新天氣預報對話框中的圖示)

    Bitmap bitmap;
    ImageButton MultiLangbutton;    // 多語切換按鈕

    final private static int DIALOG_LOGIN = 1;

    private int cgsId_forWeeklyWeatherForecast ;

    public int weatherstatusSize;

    private String acc, pwd;    // in order to store account and password

    private int status;
    private String Token;    // token

    String ChineseGreeting = "您好, 點擊開始";
    String AccessibilityMode = "進入無障礙模式\nAccessibility Services";
    String AccessibilityModeEN = "Accessibility Services";    // English language accessibility
    static int whichone_last = -1;   // 用來補一個天氣預報續先移的問題

    static boolean flagexitfor = false;

    static boolean DownloadingFinish = false ;  // 下載資料結束旗號

    static int timeTotalSize = 0;

    static boolean loginfirsttime ;  // 登入旗號

    static String[] WeatherWeek = new String[7];   // 7天的資料(星期的順移設定)

    static String StaticWeeklyWetherForecast = new String();     // weekly weather forecast json string
    // static WeatherElementObject StaticweatherElementObject = new WeatherElementObject() ;

    // 下面是用來儲存 login 旗號
    private static final String PREFS_NAME = "MyPrefs";

    private static final String LANGPREFS_NAME = "LangPrefs" ;
    private static final String FLAG_KEY = "flag";

    private static final String LANGFLAG_KEY = "langflag";

    ///////////////////////// 天氣預報的相關欄位 /////////////////////////////////
    private int StatusForWeatherForecsst ;            // status for 天氣預報
    private String AuthorizationForWeatherForecast ;  // authorization key for 天氣預報
    private String locationIdForWeatherForecast ;     // location id  for  天氣預報
    private String urlForWeatherForecast ;            // url for 天氣預報

    static int login ;

    ////////////////////////// 天氣圖示  /////////////////////////////////

    /*

    int[] WeatherIconsList = {

            R.drawable.w9,        // 多雲
            R.drawable.w10,       // 晴時多雲
            R.drawable.w12,       // 晴時短暫陣雨
            R.drawable.w11,       //
            R.drawable.w13,       //
            R.drawable.w14 };     //

     */

    int[] WeatherIconsList = {

            R.drawable.clouds ,        // 多雲, 多雲時陰
            R.drawable.sunclouds ,     // 晴時多雲
            R.drawable.cloudsun ,      // 多雲時晴
            R.drawable.cloudsunrain ,  // 晴時多雲偶陣雨 , 晴午後短暫雷陣雨
            R.drawable.clouddrizzle ,  // 多雲短暫陣雨/多雲午後短暫陣雨
            R.drawable.cloudlightningrain , // 多雲(時陰)短暫陣雨或雷雨 , 陰時多雲短暫陣雨或雷雨

    } ;

    private SpannableStringBuilder spb;
    private SpannableStringBuilder spbAccessibility;   // 這個適用來處理預設的
    private SpannableStringBuilder spbAccessibilityEN; // 英文的

    public WeatherTimeNStatus weatherTimeNStatus;

    public int StatusCode = -1;   // internet status code

    private int Status;
    private String Message, Result;
    private boolean Ok;

    private JSONObject json;

    private Context mContext;

    private String text;
    private XEditText edtdummy;

    private boolean connectionflag;   // connection is successful or not

    private Bundle bundle;

    static int LoginFlag = 0;

    private String PhoneNumber;
    private String FinalPhoneNumber;
    // external directory

    private String filename = "SampleFile.txt";
    private String filepath = "MyFileStorage";
    File myExternalFile;
    String myData = "";
    private boolean fileflag = false;
    private boolean filecontains = false;

    private String JsonResponseString;

    String ConfigFilename = "config.txt";

    private ShapeableImageView weatherimgofweek;        // a week weather forecasting image (一周天氣預報)

    private boolean weatherforecasting = false;        // default : 氣象報告觸發

    static int Loginflag = 0;                         // default is 0

    private AlertDialog TaxiDriverDialog;         // 計程車資料列表對話框
    private AlertDialog PhoneNumberInputDialog;   // 電話輸入的對話框

    public AlertDialog DummyShowLoginDialog;    // 用來關閉登入對話框之用

    private boolean clearflag = false;
    private boolean filedownloadcflag = false ;                 // 用來判斷圖檔下載完成的旗號
    // public RetrofitManager.APIService apiService;            // api service
    APIService apiService;                                      // 這個是目前天氣溫度的 api
    APIServiceForWeatherIcon apiServiceForWeatherIcon;         // 這個是顯示天氣icon的 api

    APIServiceForWeatherIcon apiServiceForWeatherDialogIcon;   // 對話框的天氣 icon 的 api

    APIAuthorize apiAuthorize = new APIAuthorize();             // authorization

    public List<CgsTravelListjsonboject> cgsTravelListjsonbojectArray = new ArrayList<CgsTravelListjsonboject>();   // cgsTravelListjsonbojectArray
    public List<CgsTravelImgListjsonboject> cgsTravelImgListjsonbojectArray = new ArrayList<CgsTravelImgListjsonboject>();

    private List<TravelListjsonboject> travelListjsonbojectListArray = new ArrayList<TravelListjsonboject>();

    public SQLiteDatabase database;  // 資料庫

    public DatabaseHelper dbHelper;   // 宣告一個 db helper

    private static final String DataBaseName = "interboxdb";
    private static final int DataBaseVersion = 1;
    private static String DataBaseTable = "cgs";
    public  SQLiteDatabase db;

    public SqlDataBaseHelper sqlDataBaseHelper;

    public MyDatabaseHelper myDatabaseHelper ;

    public SharedPreferences sharedPref ;   //  this is for sip

    public SharedPreferences MultiLanguagepref ;         // 多語切換之用

    SharedPreferences sharedPreferenceslogin ;   // login's flag

    private int isFirstTimeLogin  ;   // 1st time login flag

    static int  MultiLangSetting  ;   // 多語設定

    private static final String FIRST_RUN_KEY = "firstRun";

    private int id ;   //  for saving login id
    private static final String WEATHER_URL = "https://opendata.cwa.gov.tw/api/v1/rest/datastore/F-D0047-059?Authorization=CWA-9D7CED12-4C4D-495E-8D41-82278D85E0ED";

    ///////////////////////////// 下面是天氣預報中相關資料 ///////////////////////////////////////////////////

    /*
    private  ArrayList<TimeNElementValue> PoP12hArrayList = new ArrayList<TimeNElementValue>();               // 12小時降雨機率 array list
    private ArrayList<TimeNElementValue> AverageTemperatureArrayList = new ArrayList<TimeNElementValue>();    // 平均溫度 array list
    private ArrayList<TimeNElementValue> MinTemperatureArrayList = new ArrayList<TimeNElementValue>() ;       // 最低溫度 array list
    private ArrayList<TimeNElementValue> MaxTemperatureArrayList = new ArrayList<TimeNElementValue>() ;       // 最高溫度 array list
    private ArrayList<TimeNElementValue> WxArrayList = new ArrayList<TimeNElementValue>() ;                   // 天氣描述 array list
    private ArrayList<TimeNElementValue> WeatherDescriptionArrayList = new ArrayList<TimeNElementValue>() ;   // 天氣綜合描述 array list
    private ArrayList<TimeNElementValue> humidityArrayList = new ArrayList<TimeNElementValue>() ;             // 濕度 array list
     */

    static   ArrayList<TimeNElementValue> PoP12hArrayList = new ArrayList<TimeNElementValue>();               // 12小時降雨機率 array list
    static ArrayList<TimeNElementValue> AverageTemperatureArrayList = new ArrayList<TimeNElementValue>();   // 平均溫度 array list
    static ArrayList<TimeNElementValue> MinTemperatureArrayList = new ArrayList<TimeNElementValue>() ;      // 最低溫度 array list
    static ArrayList<TimeNElementValue> MaxTemperatureArrayList = new ArrayList<TimeNElementValue>() ;      // 最高溫度 array list
    static ArrayList<TimeNElementValue> WxArrayList = new ArrayList<TimeNElementValue>() ;                  // 天氣描述 array list
    static ArrayList<TimeNElementValue> WeatherDescriptionArrayList = new ArrayList<TimeNElementValue>() ;  // 天氣綜合描述 array list
    static ArrayList<TimeNElementValue> humidityArrayList = new ArrayList<TimeNElementValue>() ;            // 濕度 array list

    private ModelLayer modelLayer;   // modelLayer

    private SerialPort serialPort ;   // rs232 serial port
    private Button RS232Btn ;         // rs232 button

    private byte[] rs232response ;    // create a byte array

    /////////////// led commands
    private byte[] QueryModuleLED_BARCommand ;   // rs232 led command 詢問模組種類
    private byte[] LEDBRI_Setting ;   // set led's bright (亮度)
    private byte[] LEDMode_Setting ;  // set led mode (模式設定)

    private byte[] LEDColor_Setting ; // set led color (顏色設定)

    private SendingThread mSendingThread;   // rs232 thread

    private OutputStream outputStream ;  // rs232 outputstream
    private InputStream inputStream  ;   // rs232 inputstream

    private ImageView ImageFromURL ;     // this is an image which can show the picture from url

    private GestureDetector checkgestureDetector;    // 手勢控制 - 用來偵測目前在主畫面是否有打勾,以便判斷關閉 app 之用

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ////////// 设置语言 ///////////
        setLocale(getSavedLanguage());

        setContentView(R.layout.activity_main);


        ///////////////// 手勢判斷  /////////////////////////////////////////////////////////////////\
        // 0926 - peter
        // 在手勢判斷 - 打勾 用來關閉app (正常退出)
        // GestureListener 是新增的

        checkgestureDetector = new GestureDetector(this, new GestureListener());  // 初始化一個自訂的手勢物件 0925

        // Get Application and set serial finder

        mApplication = (com.smartcity.Application) getApplication();   // 取得 application
        mSerialPortFinder = mApplication.mSerialPortFinder;            // search avialable serial ports

        /*
        RS232Btn = findViewById(R.id.rs232btn) ;     // rs 232 button
        RS232Btn.setOnClickListener(new View.OnClickListener() {
            @Override
        public void onClick(View view) {

                ////////////   發送命令  ///////////////////////

            try {

                    serialPort = mApplication.getSerialPort();  // get serial port

                if ( serialPort != null  ) {

                    Toast.makeText(MainActivity.this, "rs232命令發送", Toast.LENGTH_SHORT).show();

                    QueryModuleLED_BARCommand = new byte[]{0x4E, 0x41, 0x4D, 0x45, 0x0D, 0x0A};   // led bar command - 詢問模組種類
                                                // M      O      D      E     SP      全亮        CtrL
                    LEDMode_Setting  = new byte[]{ 0x4d , 0x4f , 0x44 , 0x45 , 0x20 , 0x31 , 0x0d , 0x0a } ;   // led bar 顯示模組設定 - 全亮
                                                // C      O      L      O     R     SP     頂部    SP     R                      G     B     CtrL
                    LEDColor_Setting = new byte[]{ 0x43 , 0x4f , 0x4c , 0x4f, 0x52, 0x20 , 0x31 , 0x20 , 0x32,0x35,0x35,0x20 , 0x30,0x20, 0x30 , 0x0d ,0x0a } ;

                    String LED_BARCommand_byteString = new String(QueryModuleLED_BARCommand);
                    Log.d("qaz","rs232 command :"  + LED_BARCommand_byteString ) ;  // verify the command

                    outputStream =  serialPort.getOutputStream() ;     // get outputstream
                    inputStream  =  serialPort.getInputStream()  ;     // get inputstream

                        int size = 0  ;

                        if (outputStream != null ) {

                            Log.d("qaz","輸出串流非空");
                            outputStream.write(LEDMode_Setting);               // led mode 設定 - 全亮
                            outputStream.write(LEDColor_Setting);              // led 顏色 - 紅色

                            /////////////////// 接著, 用一個 thread 來接收回傳的資料 ///////////
                            new Thread (){
                            @Override

                            public void run(){

                                final InputStream inputStream1 = serialPort.getInputStream();   // get inputstream

                                if ( inputStream1!=null ) {

                                    byte[] data = new byte[1024];
                                    int len = 0 ;

                                    try {
                                        
                                        len = inputStream1.read(data);
                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();   // dump the exception's message
                                    }

                                    Log.d("qaz" , "接收到的資料 : "  + new String(data,0,len));
                               }
                            }
                        }.start();   // end of thread

                    }                // outputstream
                    else {
                            Log.d("qaz","outputstream 為空");
                    }

                }
                else {
                        Log.d("qwe","串列埠錯誤") ;
                }

            }catch (Exception e) {
                    Log.d("qaz","Exception 發生: " + e.getMessage()) ;
            }

        }       // onClick
        });     // rs232 button click

         */

        sharedPreferenceslogin = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        isFirstTimeLogin = sharedPreferenceslogin.getInt(FLAG_KEY, 0);  // default's value : 0 登入檢查之用

        MultiLanguagepref  = getSharedPreferences(LANGPREFS_NAME , MODE_PRIVATE) ;   // 多語設定
        MultiLangSetting = MultiLanguagepref.getInt(LANGFLAG_KEY, 0);  // default's value : 0 檢查目前語言設定之用

        // ImageFromURL = findViewById(R.id.imagefromURL) ;    // for showing the picture from url

        // 0 : 中文
        // 1 : 英文

        if (MultiLangSetting == 0 ) {

            Log.d("tgb" , "目前語言設定(預設)：" + MultiLangSetting + " 中文" );
        }
        else {

            Log.d("tgb" , "目前語言設定：" + MultiLangSetting + "英文" );
        }


        Log.d("zxc", "目前 login 旗號:" + isFirstTimeLogin) ;

        if (isFirstTimeLogin == 0) {

            Log.d("qsx", ">>>>> 第一次進入app第一次進入app第一次進入app第一次進入app第一次進入app") ;

            DownloadingPngFilesDialog();    // 第一次進入就要下載資料

            // 接著要設定旗號
            SharedPreferences.Editor editor = sharedPreferenceslogin.edit();
            editor.putInt(FIRST_RUN_KEY, 1);   // 設旗號為 1
            editor.apply();

        }
        else {

            Log.d("qsx", "XXXXXXX 不是第一次進入app 不是第一次進入app 不是第一次進入app 不是第一次進入app 不是第一次進入app") ;

        }

        Log.d("qsx", "這裡 loginfirsttime :" + loginfirsttime) ;


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
        {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        }  // // 許可權申請

        // open database
        // sqlDataBaseHelper = new SqlDataBaseHelper(this, DataBaseName, null, DataBaseVersion, "sip");
        // db = sqlDataBaseHelper.getWritableDatabase(); // 開啟資料庫

        mContext = MainActivity.this;
        // 建立資料庫

        myDatabaseHelper = new MyDatabaseHelper(mContext);
        db = myDatabaseHelper.getWritableDatabase();

        modelLayer = ModelLayer.getModelLayer();   // To get a model layer object

        modelLayer.setImageViewFromURL(ImageFromURL,"http://192.168.0.135/cgc/api/cgsImg/1" );

        if (myDatabaseHelper == null ) {

            Log.d("vbn", "資料庫helper 建立失敗") ;

        }
        else {

            Log.d("bnm", "資料庫helper 建立成功") ;
            // 開始設定登入旗號 :
            Cursor cursor = db.rawQuery("SELECT * FROM logintable", null);
            int howmany = cursor.getCount() ;

            Log.d("xnm", "cursor 數量 :" + howmany) ;  // 取得目前的資料筆數

            if ( howmany == 0 ) {
                //
                ContentValues contentValues = new ContentValues();
                contentValues.put("login",0);  // 第一次

                long count1 = db.insert("logintable", null, contentValues);

                Log.d("qsx", " <><><　這是第一次進入 app :" + count1) ;  // 第一次從 app 進入
                // DownloadingPngFilesDialog();    // 第一次進入就要下載資料

            }
            else  {

                //  DownloadingPngFilesDialog();

                // db.execSQL("DELETE FROM logintable");
                // ContentValues contentValues = new ContentValues();
                // contentValues.put("login",0);  // 第一次

                // long count1 = db.insert("logintable", null, contentValues);
                // Log.d("bvb", "login 表格:" + count1) ;


                Cursor cursorlogin  = db.rawQuery("SELECT * FROM logintable", null);

                int amount = cursorlogin.getCount();
                Log.d("qsx", "筆數 :" + amount) ;

                cursorlogin.moveToFirst() ;

                if (cursorlogin.moveToFirst()) {

                    // 取出來欄位驗證一下

                    do {

                        login = cursorlogin.getInt(cursorlogin.getColumnIndexOrThrow("login"));
                        int id = cursorlogin.getInt(cursorlogin.getColumnIndexOrThrow("id"));
                        Log.d("qsx","(((((((((((((((((((((((((") ;

                        Log.d("qsx","id = " + id) ;

                        Log.d("qsx", "login  : " + login);  // 圖檔的對應 id --> 用來下載對應的圖檔

                        // 定义 WHERE 子句，用于指定要修改的记录

                       if ( login == 0 ) {

                           ContentValues values = new ContentValues();
                           values.put("login", 1);

                           String whereClause = "id = ?";
                           String[] whereArgs = new String[]{String.valueOf(id)};  // 传递 id

                           // 调用 update() 方法更新记录
                           int rowsAffected = db.update("logintable", values, whereClause, whereArgs);

                           // 判断是否更新成功
                           if (rowsAffected > 0) {
                               Log.d("qsx", "记录修改成功");
                           } else {
                               Log.d("qsx", "没有找到要修改的记录");
                           }
                       }
                       else {
                           Log.d("qsx","非第一次進入") ;
                       }

                    } while (cursorlogin.moveToNext());
                }
            }
        }

        mContext = MainActivity.this;
        JsonResponseString = new String();

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);  // 橫

        } else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);   // 豎


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

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);   //竖屏设置

        Display display = this.getWindowManager().getDefaultDisplay();
        int rotation = display.getRotation();

        Log.d("444", ">>>" + rotation);
        // lockScreenOrientation(this);

        // 下面事先預設中文介面 , 使用 spannable string 來調整問候語

        spb = new SpannableStringBuilder(ChineseGreeting);  // spannable string
        RelativeSizeSpan relativeSizeSpan = new RelativeSizeSpan(0.5f);  // resize the size of text
        spb.setSpan(relativeSizeSpan, 2, ChineseGreeting.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        GreetingTxt = findViewById(R.id.greetingtxt);   // greeting word (1st word)
        GreetingTxt.setText(spb);

        TempTxt = findViewById(R.id.temperaturetxt);      // 右上角及時溫度更新
        WeatherImage = findViewById(R.id.weatherimg);     // 右上角及時天氣圖示更新 (主畫面)

        ConnectionTest("http://192.168.0.135/cgc/api/test");  // 連線測試

        WeatherImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ShowWeatherDialog();  //  this is a hide  function , if you click the icon , it shows weather forecast dialog
                Intent i = new Intent(MainActivity.this, AirDefenseAlert.class);
                startActivity(i);

            }
        });

        // 中文 /////////////
        spbAccessibility = new SpannableStringBuilder(AccessibilityMode);  // spannable string for accessibility
        RelativeSizeSpan relativeSizeSpanaccessibility = new RelativeSizeSpan(0.8f);  // resize the size of text
        spbAccessibility.setSpan(relativeSizeSpanaccessibility, 7, AccessibilityMode.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spbAccessibility.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, 30, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //粗体
        // 英文 ////////////
        spbAccessibilityEN = new SpannableStringBuilder(AccessibilityModeEN);
        spbAccessibilityEN.setSpan(new AbsoluteSizeSpan(31), 0, 22, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //RelativeSizeSpan relativeSizeSpanaccessibilityEN = new RelativeSizeSpan(1.0f);  // resize the size of text
        //spbAccessibilityEN.setSpan(relativeSizeSpanaccessibility , 0 , AccessibilityModeEN.length() ,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spbAccessibilityEN.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, 22, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //粗体

        // 下面是為了要處理 當天氣區間有位移時的問題, 故必須先將之前的image 位置存起來, 以便之後有誤時 可以取出來
        SharedPreferences sharedPreferences = getSharedPreferences("imagedb", Context.MODE_PRIVATE);
        /** 取得SharedPreferences.Editor編輯內容 */
        SharedPreferences.Editor editor = sharedPreferences.edit();
        /** 放入字串，並定義索引為"whichoneOfImages" */
        editor.putInt("whichoneOfImages", -1);
        /** 提交；提交結果將會回傳一布林值 */
        /** 若不需要提交結果，則可使用.apply() */
        editor.commit();



        //////////////////  圓形天氣預報圖示 (一周天氣)
        weatherimgofweek = (ShapeableImageView) findViewById(R.id.circlecloudy);

        weatherimgofweek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "CCCCCCCCCC", Toast.LENGTH_SHORT).show();

                // 這裡要先取的天氣預報資料 (一周)

                StartGetWeatherForecastRequestThread(); //  取得天氣資料
                // ShowWeatherDialog();   // 顯示氣象預報的對話框

                // 這裡是 防空警報 測試的地方
                // 這裡的  UI 必須要處理一下 , do it - 7/22
                // Intent i = new Intent(MainActivity.this , AirDefenseAlert.class);
                // startActivity(i);
            }
        });
        /**
         * John Hsu <john.hsu@weitech.com.tw> 2024/08/20
         * 在 application 啟動的時候先要求一下 permission
         * 包含 : SipPhone 電話功能的 [相機]、[錄音] ... 之類的
         */

        checkApplicationPermission();
        // Toast.makeText(MainActivity.this, "SD卡目录下创建文件成功...", Toast.LENGTH_LONG).show();

        /*
        File dir = Environment.getExternalStorageDirectory();
        File file = new File(dir , ConfigFilename);

        Log.d("ccc" , " SD card 路徑 :" + dir.toString());

        try {

            FileOutputStream fos  =  new FileOutputStream(file) ;
            String url = "192.168.100.201/cgc" ;
            fos.write(url.getBytes());
            fos.close();    // close file output

        }
        catch (FileNotFoundException e ) {

            e.printStackTrace();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        StringBuilder text = new StringBuilder();

        if (file.exists()) {

            try {

                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;   //

                //read line by line
                while ((line = br.readLine()) != null) {

                    if ( line.contains("192.168.100.201/cgc") == true) {
                        fileflag = true ;
                        filecontains = true ;
                        break ;
                    }   // end of if

                }   // end of while loop
            }
            catch (IOException e) {

                //You'll need to add proper error handling here
            }
        }
        else  {

            Log.d("ccc" , "檔案不存在") ;
            fileflag = false ;
        }

         */

        //  config.txt 檔案存在  , 且內容包含 192.168.100.201/cgc
        /*

        if ( filecontains == true && fileflag == true) {

            Log.d("ccc" , "檔案存在且有  192.168.100.201/cgc") ;

            ConnectionTest("http://192.168.100.201/cgc/api/test");

            //  ReadFileFailureDialog() ; // file error dialog
            //  NoSuchFileDialog();


        }
        else if ( filecontains == false  && fileflag == true )  {

            // 檔案中沒有資料
            ReadFileFailureDialog() ; // file error dialog
        }
        else if ( fileflag == false ) {

            NoSuchFileDialog();   // 無檔案
        }
        else ;

         */

        // ConnectionTest("http://192.168.100.201/cgc/api/test");

        DateTxt = (TextView) findViewById(R.id.datetimetxt);
        TempTxt = (TextView) findViewById(R.id.temperaturetxt);    //  get front view
        FrontView = (ImageView) findViewById(R.id.frontimage);         //  功能前景 (這個圖已經換成 天氣預報)

        // 無障礙 ///////////////////////////////////////////////////////////////////////////

        AccessibilityImg = (ImageView) findViewById(R.id.accessbilityimg);    // 無障礙
        AccessibilityCHTxt = (TextView) findViewById(R.id.accessibilitychtxt);  // 無障礙中文
        // AccessibilityENTxt  = (TextView) findViewById(R.id.accessbilityentxt) ;   // 無障礙英

        AccessibilityCHTxt.setText(spbAccessibility);   // 中英文 無障礙模式

        Intent intentaccessibility = new Intent(MainActivity.this, AccessibilityMainActivity.class);

        AccessibilityImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "無障礙", Toast.LENGTH_SHORT).show();
                // finish();

                // overridePendingTransition(R.anim.activity_bottom_in, R.anim.activity_bottom_out);  // fade in/out  的特效
                // v.getContext().startActivity(intentaccessibility);

            }
        });   // 無障礙

        AccessibilityCHTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(MainActivity.this, "無障礙", Toast.LENGTH_SHORT).show();
                overridePendingTransition(R.anim.activity_bottom_in, R.anim.activity_bottom_out);  // fade in/out  的特效
                // v.getContext().startActivity(intentaccessibility);

            }
        });  // 無障礙


       /*
        AccessibilityENTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(MainActivity.this, "無障礙", Toast.LENGTH_SHORT).show();

                overridePendingTransition(R.anim.activity_bottom_in, R.anim.activity_bottom_out);  // fade in/out  的特效
                v.getContext().startActivity(intentaccessibility) ;
            }
        });

        */


        // 1. 連線測試
        // ConnectionTest("http://192.168.100.201/cgc/api/test");

        // 2. 取出 json object and parse its value.
        //    Must check fields as below:
        //    status , result and ok , respectively .
        //    successful connection condition is status is  0 , result is "ok" and ok is true (boolean)

        // 登入系統
        // ShowAccPwdLoginDialog();   // account and password dialog  , the dialog for account/password login

        //  功能的畫面
        FrontView.setDrawingCacheEnabled(true);
        FrontView.buildDrawingCache(true);

        MultiLangbutton = (ImageButton) findViewById(R.id.langimgbtn);   //  多語設定

        MultiLangbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (MultiLangbutton.
                        getDrawable().
                        getCurrent().
                        getConstantState().
                        equals(getResources().getDrawable(R.drawable.btnlangtw).getConstantState())) {
                    Toast.makeText(MainActivity.this, "目前是:中文", Toast.LENGTH_SHORT).show();

                    // Toast a message which shows current language setting
                    // 這裡需要開啟一個對話框 , 提供語言選擇
                    // showAlertDialogButtonClicked(v , 1 );   // 顯示一個對話框  - 中文

                    showMultiLangDialog();    // 多國語言對話框

                    Log.d(TAG, "中文");   // 預設是中文

                } else if (MultiLangbutton.
                        getDrawable().
                        getCurrent().
                        getConstantState().
                        equals(getResources().getDrawable(R.drawable.btnlangen).getConstantState())) {
                    Toast.makeText(MainActivity.this, "目前是:英文", Toast.LENGTH_SHORT).show();

                    // Toast a message which shows current language setting
                    // 這裡需要開啟一個對話框 , 提供語言選擇

                    // showAlertDialogButtonClicked(v , 2 );   // 顯示一個對話框  - 英文

                    showMultiLangDialog();    // 多國語言對話框

                    Log.d(TAG, "英文");

                }
            }
        });

        checkConnection();   //  check internet connection is available
        // 前景

        FrontView.setOnTouchListener(new View.OnTouchListener() {

            // 前景按下時 判斷目前的區域 然後再處理

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    // 偵測按下的動作

                    final Bitmap bitmap = FrontView.getDrawingCache();
                    int pixel = bitmap.getPixel((int) event.getX(), (int) event.getY());   // 取出目前點擊下去的畫素 為16 進治
                    String pixelString = Integer.toString(pixel, 10);
                    Log.d(TAG, "pixel:" + pixel);

                    // Toast.makeText(MainActivity.this, "value :" + pixelString , Toast.LENGTH_SHORT).show();
                    Log.d("qqq", "值多少:" + pixelString.toString());
                    Toast.makeText(MainActivity.this, "" + pixelString.toString(), Toast.LENGTH_SHORT).show();

                    if (pixelString.equals("-11748211") ||   ///////// 景點導覽 ///////////////
                            pixelString.equals("-3015190") ||
                            pixelString.equals("-11091820") ||
                            pixelString.equals("-300318") ||
                            pixelString.equals("-10303588") ||
                            pixelString.equals("-11026027") ||
                            pixelString.equals("-3409178") ||
                            pixelString.equals("-3277849") ||
                            pixelString.equals("-8202574") ||
                            pixelString.equals("-6889279") ||
                            pixelString.equals("-6101303") ||
                            pixelString.equals("-6757950") ||
                            pixelString.equals("-5707571") ||
                            pixelString.equals("-10040929") ||
                            pixelString.equals("-8465232") ||
                            pixelString.equals("-3606044") ||
                            pixelString.equals("-10041185")) {

                        Toast.makeText(MainActivity.this, "景點導覽", Toast.LENGTH_SHORT).show();
                        // finish();   // close current activity

                        Intent landscapeWithSideBar = new Intent(MainActivity.this, LandscapeWithSideBar.class);
                        v.getContext().startActivity(landscapeWithSideBar);
                        // 這裡需要一個 overridePendingTransition
                        overridePendingTransition(R.anim.activity_bottom_in, R.anim.activity_bottom_out);  // fade in/out  的特效

                    } else if (pixelString.equals("-11364104") ||   //////// 住宿推薦 ///////////////
                            pixelString.equals("-2166785") ||
                            pixelString.equals("-11035656") ||
                            pixelString.equals("-5779973") ||
                            pixelString.equals("-5451524") ||
                            pixelString.equals("-10641672") ||
                            pixelString.equals("-10969864") ||
                            pixelString.equals("-8605191") ||
                            pixelString.equals("-7948038") ||
                            pixelString.equals("-10444296") ||
                            pixelString.equals("-3480834") ||
                            pixelString.equals("-10247432") ||
                            pixelString.equals("-2955266") ||
                            pixelString.equals("-5385988") ||
                            pixelString.equals("-6371333") ||
                            pixelString.equals("-3875075") ||
                            pixelString.equals("-447637512")) {

                        Toast.makeText(MainActivity.this, "住宿推薦", Toast.LENGTH_SHORT).show();
                        // finish();  // close current activity
                        overridePendingTransition(R.anim.activity_bottom_in, R.anim.activity_bottom_out);  // fade in/out  的特效
                        // Intent accommodation = new Intent(MainActivity.this, accommodation1.class) ;

                        Intent accommodation = new Intent(MainActivity.this, AccommodationWithSideBar.class);
                        v.getContext().startActivity(accommodation);

                    } else if (pixelString.equals("-1218448") ||  //////////////// 美食餐廳  //////////////
                            pixelString.equals("-7196") ||
                            pixelString.equals("-746078") ||
                            pixelString.equals("-74361") ||
                            pixelString.equals("-747362") ||
                            pixelString.equals("-1150087") ||
                            pixelString.equals("-410175") ||
                            pixelString.equals("342327") ||
                            pixelString.equals("-1217677")) {

                        Toast.makeText(MainActivity.this, "美食餐廳", Toast.LENGTH_SHORT).show();
                        // finish();  // close current activity

                        overridePendingTransition(R.anim.activity_bottom_in, R.anim.activity_bottom_out);  // fade in/out  的特效
                        // Intent restaurant = new Intent(MainActivity.this, restaurant1.class) ;
                        Intent intent = new Intent(MainActivity.this, DelicacyWithSideBar.class);
                        v.getContext().startActivity(intent);  // 美食餐廳

                    } else if (pixelString.equals("-1487475") ||
                            pixelString.equals("-72979") ||
                            pixelString.equals("-1150043") ||
                            pixelString.equals("-1150043") ||
                            pixelString.equals("-273950") ||
                            pixelString.equals("-139285") ||
                            pixelString.equals("-611639") ||
                            pixelString.equals("-341026") ||
                            pixelString.equals("-1283937") ||
                            pixelString.equals("-880456") ||
                            pixelString.equals("-408872") ||
                            pixelString.equals("-273694") ||
                            pixelString.equals("-139798") ||
                            pixelString.equals("-139542") ||
                            pixelString.equals("-1218145") ||
                            pixelString.equals("-139029") ||
                            pixelString.equals("-274207")) {

                        Toast.makeText(MainActivity.this, "市民專線", Toast.LENGTH_SHORT).show();
                        overridePendingTransition(R.anim.activity_bottom_in, R.anim.activity_bottom_out);  // fade in/out

                        Intent hotline = new Intent(MainActivity.this, hotline1.class);
                        v.getContext().startActivity(hotline);    // 市民專線

                        // Intent AirDefenseAlertIntent = new Intent(MainActivity.this , com.smartcity.cgs.AirDefenseAlert.class) ;
                        // v.getContext().startActivity(AirDefenseAlertIntent);
                    } else if (pixelString.equals("-6732562") ||
                            pixelString.equals("-1519105") ||
                            pixelString.equals("-1914883") ||
                            pixelString.equals("-1849090") ||
                            pixelString.equals("-6336785") ||
                            pixelString.equals("-1782786") ||
                            pixelString.equals("361905639") ||
                            pixelString.equals("-2046979") ||
                            pixelString.equals("-2179075") ||
                            pixelString.equals("-3102726") ||
                            pixelString.equals("-6336273") ||
                            pixelString.equals("-6402833") ||
                            pixelString.equals("-5544462")) {

                        // 首先,

                        // checkConnection();   //  check internet connection

                        // 天氣速報對話框 已經移到主畫面語言設定的旁邊 (hide it )
                        // ShowWeatherDialog();
                        // 叫車服務對話框 - 目前無這功能

                        // RideHailingServiceDialog();   //  call the ride hailing service dialog
                        // 這裡要檢查一下, 萬一資料還未下載完成 先要有一個預防動作
                        if (true)
                        ShowWeatherDialog();  // 一周天氣預報
                        else {

                        }


                    } else {

                        // do nothing
                    }

                }

                return false;
            }
        });

        // GetCGSInformation("http://192.168.0.135/cgc/api/inter_cgs");   // 取出 cgs information

        updateTime();         // 更新時間
        updateWeather();                       // 取得及時溫度
        updateWeatherIcon(WeatherImage);       // 更新天氣圖示

        // GetWeatherIconUpdate();
        // updateWeatherDialogIcon();  // 氣象對話框(一周)

        // 啟動 service - 119 緊急電話服務
        // 這個  service 主要啟動後會執行下面的事情 :
        // 1. 由  gpio port 1 偵測 (0.1秒) 去檢查外部按鈕是否有按下
        // 2.
        /////////// hide it , first ! To be ....

        Intent intent = new Intent(MainActivity.this, MyService.class);
        startService(intent);  // 啟動該服務

        //
        // 天氣預報資料取得服務
        // 及時天氣溫度更新 - 一般天氣預報 , 今明 36小時天氣預報
        // https://opendata.cwa.gov.tw/dist/opendata-swagger.html#/%E9%A0%90%E5%A0%B1/get_v1_rest_datastore_F_C0032_001
        // 而產出來的 url 為 https://opendata.cwa.gov.tw/api/v1/rest/datastore/F-C0032-001?Authorization=CWA-AA44B18B-3CDE-43BE-9A61-95B5F9FECCE5&locationName=%E9%AB%98%E9%9B%84%E5%B8%82
        // 其中有 MinT , MaxT 各三個時段 , 各有 startTime , endTime 兩個欄位
        // 可以使用 date 中的 before , after 方法比較 目前時間落在哪個區間
        // 後再取那欄位time [0], [1] , [2] 中 MinT ,MaxT 的溫度加總再平均
        //
        /**************************************************
         "elementName": "MinT",
         "time": [
         {
         "startTime": "2024-07-02 18:00:00",
         "endTime": "2024-07-03 06:00:00",
         "parameter": {
         "parameterName": "29",
         "parameterUnit": "C"
         }
         },
         {
         "startTime": "2024-07-03 06:00:00",
         "endTime": "2024-07-03 18:00:00",
         "parameter": {
         "parameterName": "29",
         "parameterUnit": "C"
         }
         },
         {
         "startTime": "2024-07-03 18:00:00",
         "endTime": "2024-07-04 06:00:00",
         "parameter": {
         "parameterName": "29",
         "parameterUnit": "C"
         }
         }
         *******************************************************/

        /*

        apiService = RetrofitManager.getInstance().getAPI();

        Call<DataResponse> call = apiService.getDailyRainfall(apiAuthorize.getAuthorization());


        //連線API，獲取資料
        call.enqueue(new Callback<DataResponse>() {

            @Override
            public void onResponse(Call<DataResponse> call, Response<DataResponse> response) {
                // 連線成功
                // 取得回傳資料

                if (response.isSuccessful()) {

                    List<Station> list =   (List<Station>)response.body().getRecords().getStation();
                    int  item =  response.body().getRecords().getStation().size();
                    Log.e("fff", " station 數目 >>>> " + String.valueOf(item));  // convert it to string

                    //   String stationname = response.body().getRecords().getStation().get(0).getStationName();
                    //  String stationid = response.body().getRecords().getStation().get(0).getStationID() ;
                    //Log.e("fff","站名" + stationname);
                    //Log.e("fff","站名 id " + stationid );

                    Log.d("xxx" , "list length :" + list.size()) ;

                    for (int ii = 0; ii < list.size(); ii++) {


                        Log.d("xxx", "----- Station ------" + "(" + Integer.toString(ii) + ")");
                        String stationname = list.get(ii).getStationName();
                        String stationid = list.get(ii).getStationID() ;

                        Log.e("xxx","站名 : " + stationname);
                        Log.e("xxx","站名 id : " + stationid );
                        Log.d("xxx", "目前溫度 : " + list.get(ii).getWeatherElement().getairTemperature()) ;
                    }

                    }
                else {

                    Log.d("ccc", "error") ;
                }
                }


            @Override
            public void onFailure(Call<DataResponse> call, Throwable t) {
                // 連線失敗
                Log.d("test",t.toString());
            }
        });

         */


        // 建立資料庫 - 先開一個 db helper

        // dbHelper = new DatabaseHelper(this);
        // database = dbHelper.getWritableDatabase();
        /*
        if (dbHelper != null) {
            Log.d("777", "資料庫 helper 建立成功 !");

        } else {
            Log.d("777", "資料庫 helper 建立失敗 !");

        }

         */

        /*
        long count_cgs1 = DatabaseUtils.queryNumEntries(db, "cgs1");   // cgs table
        long count_cgsImgList = DatabaseUtils.queryNumEntries(db, "cgsImgList");         // cgsImgList table
        long count_travelList = DatabaseUtils.queryNumEntries(db, "travelList");         // travelList table
        long count_travelImgList = DatabaseUtils.queryNumEntries(db, "travelImgList");   // travelImgList table
        long count_cgsTravelList =  DatabaseUtils.queryNumEntries(db, "cgsTravelList");  // cgsTravelList table

        // 先清空表格 - cgs
        if (count_cgs1 == 0) {
            Log.d("vbn" , "cgs1 為空表格");
        } else {
            Log.d("vbn" , "cgs1 為非空表格");
            db.execSQL("DELETE FROM cgs1");
            Log.d("vbn","cgs1 表格已清空");
        }

        // 先清空表格 - cgs
        if (count_cgsImgList == 0) {
            Log.d("vbn" , "cgsImgList 為空表格");
        } else {
            Log.d("vbn" , "cgsImgList 為非空表格");
            db.execSQL("DELETE FROM cgsImgList");
            Log.d("vbn","cgsImgList 表格已清空");
        }

         */

    }  // end of onCreate

    private void switchLanguage() {


        String currentLanguage = Locale.getDefault().getLanguage();   // 取出目前的語言設定

        String newLanguage = currentLanguage.equals("en") ? "zh" : "en";

        // 保存新语言设置
        saveLanguage(newLanguage);

        // 設置新语言並重啟 Activity
        setLocale(newLanguage);

        recreate();
    }

    private void setLocale(String language) {

        Locale locale = new Locale(language);

        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);


        DisplayMetrics dm = getResources().getDisplayMetrics();
        getResources().updateConfiguration(config, dm);

        // 通知 Activity2 也重新启动以应用新语言
        // Intent intent = new Intent(Activity1.this, Activity2.class);
        // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        // startActivity(intent);

    }

    private void saveLanguage(String language) {

        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("language", language);
        editor.apply();
    }

    private String getSavedLanguage() {

        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        return prefs.getString("language", "en");    // 預設語言為英文
    }


    public static void lockScreenOrientation(Activity activity) {

        Log.d("444", "lockScreenOrientation()");


        Display display = activity.getWindowManager().getDefaultDisplay();

        switch (display.getRotation()) {
            case Surface.ROTATION_90:
                Log.d("444", "轉90度");
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case Surface.ROTATION_180:
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                Log.d("444", "轉 180度 ");
                break;
            case Surface.ROTATION_270:
                Log.d("444", "轉270度");
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                break;
            default:
                Log.d("444", "XXXXXXX");
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
        }
    }

    private void StartGetWeatherForecastRequestThread() {

        // 天氣預報資料取得

        Log.d("kkk", "StartGetWeatherForecastRequestThread()");


        new Thread(new Runnable() {
            @Override
            public void run() {

                if (Looper.myLooper() == null) {
                    Looper.prepare();              //  Notice ! Here must be added , add it on 11/15
                }

                // Looper.prepare();
                //  Notice ! Here must be added

                doHttpGetWeatherForecastDataRequest();       // doHttpGetWeatherForecastDataRequest   - 氣象資料

            }
        }).start();

    }   // end of StartGetWeatherForecastRequestThread


    //////////////////////////////// 向open data 取出天氣資料
    private void StartGetWeeklyWeatherForecastFromOpenDataRequestThread() {

        // 天氣預報資料取得

        Log.d("zxc", "StartGetWeeklyWeatherForecastFromOpenDataRequestThread()");


        new Thread(new Runnable() {
            @Override
            public void run() {

                if (Looper.myLooper() == null) {
                    Looper.prepare();              //  Notice ! Here must be added , add it on 11/15
                }

                // Looper.prepare();
                //  Notice ! Here must be added

                doHttpGetWeatherForecastDataRequest();       // doHttpGetWeatherForecastDataRequest   - 氣象資料

            }
        }).start();

    }   // end of StartGetWeeklyWeatherForecastFromOpenDataRequestThread


   //////////////////////  一周天氣預報
    private void StartGetWeeklyWeatherForecastRequestThread() {

        // 一周天氣預報資料取得

        Log.d("zxc", "StartGetWeeklyWeatherForecastRequestThread()");

        new Thread(new Runnable() {
            @Override
            public void run() {

                if (Looper.myLooper() == null) {
                    Looper.prepare();              //  Notice ! Here must be added , add it on 11/15
                }

                // Looper.prepare();
                //  Notice ! Here must be added

                doHttpGetWeeklyWeatherForecastDataRequest(GeturlForWeatherForecast(), GetAuthorizationForWeatherForecast());       // doHttpGetWeeklyWeatherForecastDataRequest   - 一周氣象資料

            }
        }).start();

    }   // end of StartGetWeatherForecastRequestThread


    private void GetWeatherForecastRequestThreadWithCgsId() {

        // 天氣預報資料取得 - 以 cgsId 來分辨要取哪一個縣市的氣象資料

        Log.d("kkk", "GetWeatherForecastRequestThreadWithCgsId()");

        new Thread(new Runnable() {
            @Override
            public void run() {

                if (Looper.myLooper() == null) {
                    Looper.prepare();
                }

                // Looper.prepare();
                //  Notice ! Here must be added

                doHttpGetWeatherForecastDataRequest();       //    氣象資料

            }
        }).start();

    }   // end of GetWeatherForecastRequestThreadWithCgsId




    private void StartGetCGSInformationRequestThread() {

        // inter cgs 資料取得 - Using a thread

        Log.d("xyz", "StartGetCGSInformationRequestThread()");


        new Thread(new Runnable() {
            @Override
            public void run() {

                if (Looper.myLooper() == null) {
                    Looper.prepare();              //  Notice ! Here must be added , add it on 11/15
                }

                // Looper.prepare();
                //  Notice ! Here must be added

                doHttpGetCGSInformationRequest();       // doHttpGetCGSInformationRequest   - inter box 資料

            }
        }).start();

    }   // end of StartGetCGSInformationRequestThread


    private void GetWeatherIconUpdate() {

        // 天氣圖騰更新
        // 比較目前的位置 - (高雄)再秀出來


        apiServiceForWeatherIcon = RetrofitManager.getInstance().getApiServiceForWeatherIcon();  //  weather icon api

        Call<DataResponseWeatherIcon> call = apiServiceForWeatherIcon.getWeatherIcon(apiAuthorize.getAuthorization()); // weather icon service


        // 連線 API，獲取資料
        call.enqueue(new Callback<DataResponseWeatherIcon>() {

            @Override
            public void onResponse(Call<DataResponseWeatherIcon> call, Response<DataResponseWeatherIcon> response) {

                // 連線成功
                // 取得回傳資料

                if (response.isSuccessful()) {

                    List<Location> list = (List<Location>) response.body().getRecords().getLocation();
                    int item = response.body().getRecords().getLocation().size();  // 取得資料長度
                    Log.d("ccc", "資料長度: " + item);
                    String description = response.body().getRecords().getDatasetDescription();
                    Log.d("sss", "描述:" + description);

                    for (int j = 0; j < item; j++) {

                        String locationName = list.get(j).getLocationName(); // 地點名稱 (取出全部的地點)
                        Log.d("sss", "地點:" + locationName);      // 首先要判斷地點 : 嘉義

                        if (locationName.equals("嘉義市")) {

                            List<WeatherElement> weatherElement = list.get(j).getWeatherElement();
                            int length = weatherElement.size();

                            String CurrentDayAndTime = CurrentDayAndTime();

                            for (int jj = 0; jj < length; jj++) {
                                String elementName = weatherElement.get(jj).getelementName();  // 取出 element name
                                Log.d("sss", "elementName: " + elementName);         // 我們只需取出 elementName 為Wx之中的天氣狀態字串

                                if (elementName.equals("Wx")) {

                                    List<TimeW> time = weatherElement.get(jj).getTime();
                                    int timelots = time.size();

                                    // 判斷有幾個時區 - 該資料都是以 12 小時為一個時區 , 所以有 3 個時區

                                    Log.d("eee", " 時區長度 : " + timelots);

                                    for (int k = 0; k < timelots; k++) {

                                        Log.d("sss", "時區 :" + Integer.toString(k));
                                        String startTime = time.get(k).getStartTime().toString(); // start time
                                        String endTime = time.get(k).getEndTime().toString();     // end time
                                        ParameterW parameterW = time.get(k).getParameter();
                                        String parameterName = parameterW.getParameterName();    // 天氣狀態
                                        String parameterValue = parameterW.getParameterValue();   //

                                        // 這裡的格式是正確的 - String type
                                        try {

                                            String Current = CurrentDayAndTime();

                                            Log.d("eee", ":::::::::::::::::::");

                                            Log.d("eee", "CurrentDayAndTime :" + Current);
                                            Log.d("eee", "StartTime :" + startTime);
                                            Log.d("eee", "EndTime :" + endTime);
                                            // string 必須轉為 date object


                                        } catch (Exception exception) {
                                            Log.d("sss", "error : " + exception.getMessage().toString());
                                        }

                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
                                        String inputFormat = "yyyy-MM-dd HH:mm:ss";

                                        try {

                                            // 轉換時間以 milliseconds 為單位 再進行比較 - 現在 , 區間起始 , 區間結束
                                            // 每次取出 共有 3個區間 - 每個區間為 12小時
                                            // (0600 - 1800) , (1800 - 0600) , (0600 - 1800) 共三個區間
                                            // 下面轉換有問題 ! 07/12 要重新處理 ! 計算時間比較有誤 !

                                            long now = convertToMilliseconds(CurrentDayAndTime(), inputFormat);
                                            long start = convertToMilliseconds(startTime.toString(), inputFormat);
                                            long end = convertToMilliseconds(endTime.toString(), inputFormat);

                                            Log.d("nnn", "現在: " + now);
                                            Log.d("nnn", "開始: " + start);
                                            Log.d("nnn", "結束: " + end);

                                            SimpleDateFormat df = new SimpleDateFormat("HH:mm");  // 時間/日期 格式
                                            Date sd1 = df.parse(startTime);  // 區間的起始日期時間
                                            Date sd2 = df.parse(endTime);    // 區間的終止日期時間
                                            Date sd3 = df.parse(CurrentDayAndTime); // 目前日期時間

                                            SimpleDateFormat dff = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                            Log.d("nnn", "++++++++++++ " + dff.format(new Date()).toString());

                                            long totalMilliSeconds = System.currentTimeMillis();
                                            Log.d("nnn", "++++++++++" + totalMilliSeconds);

                                            long s = sd1.getTime();
                                            long e = sd2.getTime();
                                            long c = sd3.getTime();

                                            Log.d("nnn", "start time >>>>" + s);
                                            Log.d("nnn", "end time >>>>" + totalMilliSeconds);
                                            Log.d("nnn", "current time >>>>" + c);


                                            Log.d("nnn", "起始時間 (yyyy-MM-dd HH:mm:ss) : " + sd1.toString());
                                            Log.d("nnn", "終止時間 (yyyy-MM-dd HH:mm:ss) : " + sd2.toString());
                                            Log.d("nnn", "目前時間 (yyyy-MM-dd HH:mm:ss) : " + sd3.toString());


                                            Log.d("nnn", ">>>> 大小 :" + sd1.before(sd2));  // sd1 < sd2
                                            Log.d("nnn", ">>>> 大小 :" + sd2.after(sd1)); // sd2 > sd1

                                            if (sd1.after(sd3)) {
                                                if (sd2.before(sd3))
                                                    Log.d("nnn", "時間區間正確");

                                            } else
                                                Log.d("nnn", "時間區間錯誤");

                                            if (sd1.before(sd2) && sd2.after(sd1))
                                                Log.d("nnn", ">>>> " + "sd1 < sd2 " + "且" + "sd2 > sd1");
                                            else
                                                Log.d("nnn", "....");


                                            // 判斷目前時間在哪個時間區間

                                            if (now > (start - 1) && now < (end + 1)) {

                                                // 時間區間確定後 , 再將天氣字串傳入後判斷是哪一張圖

                                                if (k == 0) {  // 0600 至 1800 (有時非 0600 - 1800 , 而有其他的區間)
                                                    Log.d("nnn", "今日 0600 至 1800");
                                                    Log.d("nnn", ">>>> 天氣狀態 :" + parameterName);
                                                    int whichone = WhichOneofWeatherIcons(parameterName); // 傳回對應的圖編號順序
                                                    Log.d("nnn", "第幾個圖:" + whichone);
                                                    whichone_last = whichone;
                                                    Log.d("nnn", "whichone_last  " + Integer.toString(whichone_last));
                                                    // 注意 !!!!! 因為天氣預報會在時間區間尚未到時, 就會先將新的區間公布 所以會有取不到的現象
                                                    // 所以,必須用一個 static 變數記錄目前第幾個圖 以便找不到時 (error) 無圖顯示 !!
                                                    //

                                                    WeatherImage.setImageDrawable(getResources().getDrawable(WeatherIconsList[whichone])); // 載入相對應的圖片


                                                } else if (k == 1) {   // 1800 至 明日 0600 (有時不一定是這樣的區間)
                                                    Log.d("ggg", "今日 1800 至 明日 0600 ");
                                                    Log.d("ggg", ">>>> 天氣狀態 :" + parameterName);
                                                    int whichone = WhichOneofWeatherIcons(parameterName);
                                                    Log.d("ggg", "第幾個圖:" + whichone);
                                                    whichone_last = whichone;  // 紀錄當前的圖像順序數
                                                    // 這裡應該有一個可以儲存這個變數的 shared preference variable

                                                    Log.d("nnn", "whichone_last  " + Integer.toString(whichone_last));
                                                    WeatherImage.setImageDrawable(getResources().getDrawable(WeatherIconsList[whichone])); // 載入相對應的圖片

                                                } else if (k == 2) {   // 明日 0600 到 1800
                                                    Log.d("ggg", "明日 0600 到 1800 ");
                                                    Log.d("ggg", ">>>> 天氣狀態 :" + parameterName);
                                                    int whichone = WhichOneofWeatherIcons(parameterName);
                                                    Log.d("ggg", "第幾個圖:" + whichone);
                                                    whichone_last = whichone;
                                                    Log.d("nnn", "whichone_last  " + Integer.toString(whichone_last));
                                                    WeatherImage.setImageDrawable(getResources().getDrawable(WeatherIconsList[whichone])); // 載入相對應的圖片

                                                }

                                            } else {  // 原因是 到 1700 後 取出資料會有變更為 1800 - 0600 則目前取出來的時間 不會在 0600 - 1800 之間
                                                // 發生永遠不在區間之中的錯誤,就無法對應出正確位置的圖檔
                                                // 所以必須記錄在每次正確時就要將所對應到的圖示的位置，這變數必須在這個activity中一直存活著
                                                //

                                                Log.d("nnn", "有錯");
                                                Log.d("nnn", ">>>> 天氣狀態 :" + "無 ");
                                                Log.d("nnn", "whichone_last  " + Integer.toString(whichone_last));
                                                WeatherImage.setImageDrawable(getResources().getDrawable(WeatherIconsList[whichone_last]));
                                            }


                                        } catch (Exception e) {

                                        }

                                        // 36 小時天氣預報 - 每一區間的起始時間,終止時間,天氣狀態

                                        weatherTimeNStatus = new WeatherTimeNStatus(startTime, endTime, parameterName);
                                        WeatherStatus.add(weatherTimeNStatus);

                                        // WeatherStatus.add(weatherTimeNStatus);   // 加入一個天氣狀態到 list array
                                        // saveWeakStatus(weatherTimeNStatus);

                                        Log.d("sss", "WeatherStatus 長度:" + WeatherStatus.size());

                                        Log.d("sss", "天氣起始時間:" + WeatherStatus.get(k).getStartTime());
                                        Log.d("sss", "天氣終止時間:" + WeatherStatus.get(k).getEndTime());
                                        Log.d("sss", "天氣狀態:" + WeatherStatus.get(k).getWeatherStatus());

                                        // WeatherStatus.get(k).setStartTime(startTime);          // 起始時間
                                        // WeatherStatus.get(k).setEndTime(endTime);              // 終止時間
                                        // WeatherStatus.get(k).setWeatherStatus(parameterName);  // 天氣狀態

                                        Log.d("sss", "起始時間: " + startTime);
                                        Log.d("sss", "終止時間: " + endTime);
                                        Log.d("sss", "parameterName: " + parameterName);
                                        Log.d("sss", "parameterValue: " + parameterValue);
                                        Log.d("sss", "-----------------------");

                                        // 這裡取得目前的時間 , 直接比較在哪個區間就做判斷
                                        //

                                    } // end of inner for
                                }     // 過濾 elementName 為 "Wx" 的資料

                            }  // end of outer for loop

                            Log.d("sss", "WeatherStatus 長度:" + WeatherStatus.size());
                            weatherstatusSize = WeatherStatus.size();    // save the size of 天氣狀態
                            Log.d("ppp", "weatherstatusSize :" + weatherstatusSize);
                            saveWeatherStatusSize(weatherstatusSize);     // save the length of weather status
                            Log.d("sss", "長度>>:" + getWeatherstatusSize());

                            // break;  // 處理完畢 離開 !
                        }      // 處理嘉義市

                    }

                } else {

                    Log.d("ccc", "error");
                }


            }  // end of onResponse

            @Override
            public void onFailure(Call<DataResponseWeatherIcon> call, Throwable t) {

            }

        });  //  call.enqueue

        Log.d("ppp", "weatherstatusSize :::" + getWeatherstatusSize());

    }  // end of  GetWeatherIconUpdate


    private void DialogGetWeatherIconUpdate(ImageView weatherImage) {

        // 天氣圖騰更新 (對話框)
        // 比較目前的位置 - (嘉義)再秀出來

        Log.d("sss", "XXXXXXXXXXXX");

        apiServiceForWeatherDialogIcon = RetrofitManager.getInstance().getApiServiceForWeatherIcon();  //  weather icon api

        Call<DataResponseWeatherIcon> call = apiServiceForWeatherDialogIcon.getWeatherIcon(apiAuthorize.getAuthorization()); // weather icon service

        // 連線 API，獲取資料
        call.enqueue(new Callback<DataResponseWeatherIcon>() {

            @Override
            public void onResponse(Call<DataResponseWeatherIcon> call, Response<DataResponseWeatherIcon> response) {

                // 連線成功
                // 取得回傳資料

                if (response.isSuccessful()) {

                    List<Location> list = (List<Location>) response.body().getRecords().getLocation();
                    int item = response.body().getRecords().getLocation().size();  // 取得資料長度
                    Log.d("www", "資料長度: " + item);
                    String description = response.body().getRecords().getDatasetDescription();
                    Log.d("sss", "描述:" + description);

                    List<MaxMinTemp> maxMinTempList = new ArrayList<>();  // 用來儲存 最高/最低溫度的 list

                    // 這裡要先處理 Max , Min 溫度的儲存

                    for (int c = 0; c < item; c++) {

                        List<WeatherElement> weatherElement = list.get(c).getWeatherElement();  // 取出 weatherElement tag
                        int length = weatherElement.size();                                     // 看看有幾個 weatherelement 的元素

                        Log.d("www", "element 數目 : " + Integer.toString(length));
                        Log.d("www", "__________________");
                        for (int k = 0; k < length; k++) {
                            Log.d("www", "Element Name: " + weatherElement.get(k).getelementName());
                            String elementName = weatherElement.get(k).getelementName();


                            if (elementName.equals("MinT")) {
                                List<TimeW> time = weatherElement.get(k).getTime();
                                int timesize = time.size();
                                timeTotalSize = timesize;  // 記錄總長度

                                Log.d("www", "MinT 時區數目: " + Integer.toString(timesize));

                                for (int p = 0; p < timesize; p++) {

                                    MaxMinTemp maxMinTemp = new MaxMinTemp();

                                    String minTemp = time.get(p).getParameter().getParameterName();
                                    Log.d("fff", "最低溫度 : " + minTemp); // 取出溫度
                                    maxMinTemp.setMinTemp(minTemp);   // 設定最低溫
                                    maxMinTemp.setMaxTemp("");        // 設定最高溫為空
                                    maxMinTempList.add(maxMinTemp);   // 將一個溫度物件加入到 list 中
                                    Log.d("fff", "最低溫度:" + "(" + Integer.toString(p) + ")-> " + maxMinTemp.getMinTemp());
                                }
                            }    // 最低溫度

                            if (elementName.equals("MaxT")) {
                                List<TimeW> time = weatherElement.get(k).getTime();
                                int timesize = time.size();
                                Log.d("fff", "MaxT 時區數目: " + Integer.toString(timesize));

                                for (int n = 0; n < timesize; n++) {

                                    MaxMinTemp maxMinTemp = new MaxMinTemp();

                                    String maxTemp = time.get(n).getParameter().getParameterName();
                                    Log.d("fff", "最高溫度 : " + maxTemp); // 取出溫度
                                    maxMinTemp.setMaxTemp(maxTemp);   // 設定最低溫
                                    maxMinTemp.setMinTemp("");        // 設定最低溫為空
                                    maxMinTempList.add(maxMinTemp);   // 將一個溫度物件加入到 list 中
                                    Log.d("fff", "最高溫度:" + "(" + Integer.toString(n) + ")-> " + maxMinTempList.get(n).getMaxTemp());
                                }
                            }    // 最高溫度

                            // Log.d("www", "/////// 總共list長度 :" + Integer.toString(maxMinTempList.size()));

                            if (flagexitfor == false) {
                                for (int pp = 0; pp < 6; pp++) {
                                    // Log.d("fff", "--------------  最高溫: " + maxMinTempList.get(pp).getMaxTemp()) ;
                                    // Log.d("fff","---------------  最低溫: " + maxMinTempList.get(pp).getMinTemp()) ;
                                }
                                flagexitfor = true;
                            }


                        }

                        // 共有 5 個 weatherelement
                        // 分別是 : wx , pop , minT , CI , maxT
                        // 但我們目前只須針對  MinT , MaxT 兩個 weatherelement

                    }   // end of for c

                    for (int j = 0; j < item; j++) {

                        String locationName = list.get(j).getLocationName(); // 地點名稱 (取出全部的地點)
                        Log.d("sss", "地點:" + locationName);       // 首先要判斷地點 : 嘉義

                        if (locationName.equals("嘉義市")) {

                            List<WeatherElement> weatherElement = list.get(j).getWeatherElement();
                            int length = weatherElement.size();

                            String CurrentDayAndTime = CurrentDayAndTime();

                            for (int jj = 0; jj < length; jj++) {
                                String elementName = weatherElement.get(jj).getelementName();  // 取出 element name
                                Log.d("aaa", "elementName: " + elementName);         // 我們只需取出 elementName 為Wx之中的天氣狀態字串

                                if (elementName.equals("Wx") ||
                                        elementName.equals("MinT") ||
                                        elementName.equals("MaxT")) {

                                    List<TimeW> time = weatherElement.get(jj).getTime();
                                    int timelots = time.size();  // 多少個 json objects

                                    // 判斷有幾個時區 - 該資料都是以 12 小時為一個時區 , 所以有 3 個時區

                                    List<MaxMinTemp> maxMinTemperatureList = new ArrayList<>();  // 宣告一個溫差物件陣列

                                    int len = 0;

                                    for (int x = 0; x < timelots; x++) {
                                        Log.d("yyy", "欄位 :" + Integer.toString(x));  //
                                        MaxMinTemp maxMinTempobj = new MaxMinTemp("", ""); // default value are empty string
                                        assert maxMinTempobj != null;  // 確認非空

                                        if (elementName.equals("MaxT")) {
                                            String MaxTemp = time.get(x).getParameter().getParameterName();  // max temperature
                                            Log.d("ppp", "最高溫:" + MaxTemp);
                                            maxMinTempobj.setMaxTemp(MaxTemp); // set max temperature of object
                                            maxMinTemperatureList.add(maxMinTempobj);
                                            len++;
                                        } else if (elementName.equals("MinT")) {
                                            String MinTemp = time.get(x).getParameter().getParameterName();  // min temperature
                                            Log.d("ppp", "最低溫:" + MinTemp);
                                            maxMinTempobj.setMinTemp(MinTemp); // set min temperature of object
                                            maxMinTemperatureList.add(maxMinTempobj);
                                            len++;
                                        } else {
                                            Log.d("yyy", "資料有錯,不是正確溫差欄位");
                                        }

                                    }  // end of for loop

                                    Log.d("ppp", "溫度差串列長:" + len);

                                    for (int k = 0; k < timelots; k++) {

                                        Log.d("yyy", "時區 :" + Integer.toString(k));
                                        String startTime = time.get(k).getStartTime().toString(); // start time
                                        String endTime = time.get(k).getEndTime().toString();     // end time
                                        ParameterW parameterW = time.get(k).getParameter();
                                        String parameterName = parameterW.getParameterName();    // 天氣狀態
                                        String parameterValue = parameterW.getParameterValue();   //

                                        // 這裡是取出最高低溫, 但必須處理一下
                                        // 必須建立一個高低溫的 list 用來存放每一組高低溫

                                        // List<MaxMinTemp> maxMinTempList = new ArrayList<>();

                                        // MaxMinTemp maxMinTempobj = new MaxMinTemp("","") ; // default value are empty string
                                        // assert maxMinTempobj != null ;  // 確認非空

                                        // add 7/10
                                        if (elementName.equals("MaxT")) {
                                            // String MaxTemp = time.get(k).getParameter().getParameterName();  // max temperature
                                            // Log.d("ppp","最高溫:" + MaxTemp) ;
                                            // maxMinTempobj.setMaxTemp(MaxTemp); // set max temperature of object
                                            // maxMinTempList.add(maxMinTempobj) ;
                                        } else if (elementName.equals("MinT")) {
                                            // String MinTemp = time.get(k).getParameter().getParameterName();  // max temperature
                                            // Log.d("ppp","最低溫:" + MinTemp) ;
                                            // maxMinTempobj.setMinTemp(MinTemp); // set min temperature of object
                                            // maxMinTempList.add(maxMinTempobj) ;

                                        }

                                        try {

                                            String Current = CurrentDayAndTime();

                                            Log.d("eee", ":::::::::::::::::::");

                                            Log.d("eee", "CurrentDayAndTime :" + Current);
                                            Log.d("eee", "StartTime :" + startTime);
                                            Log.d("eee", "EndTime :" + endTime);

                                        } catch (Exception exception) {
                                            Log.d("sss", "error : " + exception.getMessage().toString());
                                        }

                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:MM:SS");
                                        String inputFormat = "yyyy-MM-dd HH:mm:ss";

                                        try {

                                            // 轉換時間以 milliseconds 為單位 再進行比較 - 現在 , 區間起始 , 區間結束
                                            // 每次取出 共有 3個區間 - 每個區間為 12小時
                                            // (0600 - 1800) , (1800 - 0600) , (0600 - 1800) 共三個區間

                                            long now = convertToMilliseconds(CurrentDayAndTime(), inputFormat);
                                            long start = convertToMilliseconds(startTime.toString(), inputFormat);
                                            long end = convertToMilliseconds(endTime.toString(), inputFormat);

                                            Log.d("ggg", "現在: " + now);
                                            Log.d("ggg", "開始: " + start);
                                            Log.d("ggg", "結束: " + end);

                                            // 判斷目前時間在哪個時間區間

                                            if (now > (start - 1) && now < (end + 1)) {

                                                // 時間區間確定後 , 再將天氣字串傳入後判斷是哪一張圖

                                                if (k == 0) {  // 0600 至 1800
                                                    Log.d("ggg", "今日 0600 至 1800");
                                                    Log.d("ggg", ">>>> 天氣狀態 :" + parameterName);
                                                    int whichone = WhichOneofWeatherIcons(parameterName);
                                                    Log.d("ggg", "第幾個圖:" + whichone);
                                                    whichone_last = whichone;
                                                    weatherImage.setImageDrawable(getResources().getDrawable(WeatherIconsList[whichone]));   //
                                                    // WeatherImage.setImageDrawable(getResources().getDrawable(WeatherIconsList[whichone])); // 載入相對應的圖片

                                                    SharedPreferences sharedPreferences = getSharedPreferences("imagedb", Context.MODE_PRIVATE);
                                                    /** 取得SharedPreferences.Editor編輯內容 */
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    /**放入字串，並定義索引為 "whichoneOfImages" */
                                                    editor.putInt("whichoneOfImages", whichone);
                                                    /**提交；提交結果將會回傳一布林值*/
                                                    /**若不需要提交結果，則可使用.apply()*/
                                                    editor.commit();

                                                    if (elementName.equals("MaxT")) {
                                                        String MaxTemp = time.get(k).getParameter().getParameterName();  // max temperature
                                                        Log.d("ggg", "最高溫:" + MaxTemp);
                                                    } else if (elementName.equals("MinT")) {
                                                        String MinTemp = time.get(k).getParameter().getParameterName();  // max temperature
                                                        Log.d("ggg", "最高溫:" + MinTemp);

                                                    }
                                                    break;   // 離開
                                                } else if (k == 1) {   // 1800 至 明日 0600
                                                    Log.d("ggg", "今日 1800 至 明日 0600 ");
                                                    Log.d("ggg", ">>>> 天氣狀態 :" + parameterName);
                                                    int whichone = WhichOneofWeatherIcons(parameterName);
                                                    Log.d("ggg", "第幾個圖:" + whichone);
                                                    whichone_last = whichone;
                                                    weatherImage.setImageDrawable(getResources().getDrawable(WeatherIconsList[whichone]));   //

                                                    SharedPreferences sharedPreferences = getSharedPreferences("imagedb", Context.MODE_PRIVATE);
                                                    /** 取得SharedPreferences.Editor編輯內容 */
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    /**放入字串，並定義索引為 "whichoneOfImages" */
                                                    editor.putInt("whichoneOfImages", whichone);
                                                    /**提交；提交結果將會回傳一布林值*/
                                                    /**若不需要提交結果，則可使用.apply()*/
                                                    editor.commit();

                                                    if (elementName.equals("MaxT")) {
                                                        String MaxTemp = time.get(k).getParameter().getParameterName();  // max temperature
                                                        Log.d("ggg", "最高溫:" + MaxTemp);
                                                    } else if (elementName.equals("MinT")) {
                                                        String MinTemp = time.get(k).getParameter().getParameterName();  // max temperature
                                                        Log.d("ggg", "最高溫:" + MinTemp);

                                                    }
                                                    break;   // 離開
                                                    // WeatherImage.setImageDrawable(getResources().getDrawable(WeatherIconsList[whichone])); // 載入相對應的圖片

                                                } else if (k == 2) {   // 明日 0600 到 1800
                                                    Log.d("ggg", "明日 0600 到 1800 ");
                                                    Log.d("ggg", ">>>> 天氣狀態 :" + parameterName);
                                                    int whichone = WhichOneofWeatherIcons(parameterName);
                                                    Log.d("ggg", "第幾個圖:" + whichone);
                                                    whichone_last = whichone;
                                                    weatherImage.setImageDrawable(getResources().getDrawable(WeatherIconsList[whichone]));   //

                                                    SharedPreferences sharedPreferences = getSharedPreferences("imagedb", Context.MODE_PRIVATE);
                                                    /** 取得SharedPreferences.Editor編輯內容 */
                                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                                    /**放入字串，並定義索引為 "whichoneOfImages" */
                                                    editor.putInt("whichoneOfImages", whichone);
                                                    /**提交；提交結果將會回傳一布林值*/
                                                    /**若不需要提交結果，則可使用.apply()*/
                                                    editor.commit();

                                                    if (elementName.equals("MaxT")) {
                                                        String MaxTemp = time.get(k).getParameter().getParameterName();  // max temperature
                                                        Log.d("ggg", "最高溫:" + MaxTemp);
                                                    } else if (elementName.equals("MinT")) {
                                                        String MinTemp = time.get(k).getParameter().getParameterName();  // max temperature
                                                        Log.d("ggg", "最高溫:" + MinTemp);

                                                    }
                                                    // WeatherImage.setImageDrawable(getResources().getDrawable(WeatherIconsList[whichone])); // 載入相對應的圖片
                                                    break;   // 離開

                                                }
                                            } else {

                                                // 這裡要處理 天氣預報的先報問題 !

                                                Log.d("ggg", ">>>> 不在區間中 ! ");
                                                Log.d("ggg", "HHHHHHHHHHHH " + whichone_last);
                                                SharedPreferences sharedPreferences = getSharedPreferences("imagedb", Context.MODE_PRIVATE);
                                                /**回傳在"whichoneOfImages "索引之下的資料；若無儲存則回傳 -1 */
                                                int whichone = sharedPreferences.getInt("whichoneOfImages", -1);
                                                weatherImage.setImageDrawable(getResources().getDrawable(WeatherIconsList[whichone]));

                                                Log.d("ggg", "image 位置: " + sharedPreferences.getInt("whichoneOfImages", -1));

                                            }

                                        } catch (Exception e) {

                                        }

                                        // 36 小時天氣預報 - 每一區間的起始時間,終止時間,天氣狀態

                                        weatherTimeNStatus = new WeatherTimeNStatus(startTime, endTime, parameterName);
                                        WeatherStatus.add(weatherTimeNStatus);

                                        // WeatherStatus.add(weatherTimeNStatus);   // 加入一個天氣狀態到 list array
                                        // saveWeakStatus(weatherTimeNStatus);

                                        Log.d("sss", "WeatherStatus 長度:" + WeatherStatus.size());

                                        Log.d("sss", "天氣起始時間:" + WeatherStatus.get(k).getStartTime());
                                        Log.d("sss", "天氣終止時間:" + WeatherStatus.get(k).getEndTime());
                                        Log.d("sss", "天氣狀態:" + WeatherStatus.get(k).getWeatherStatus());

                                        // WeatherStatus.get(k).setStartTime(startTime);          // 起始時間
                                        // WeatherStatus.get(k).setEndTime(endTime);              // 終止時間
                                        // WeatherStatus.get(k).setWeatherStatus(parameterName);  // 天氣狀態

                                        Log.d("sss", "起始時間: " + startTime);
                                        Log.d("sss", "終止時間: " + endTime);
                                        Log.d("sss", "parameterName: " + parameterName);
                                        Log.d("sss", "parameterValue: " + parameterValue);
                                        Log.d("sss", "-----------------------");

                                        // 這裡取得目前的時間 , 直接比較在哪個區間就做判斷
                                        //

                                    } // end of inner for
                                }     // 過濾 elementName 為 "Wx" 的資料

                            }  // end of outer for loop

                            Log.d("sss", "WeatherStatus 長度:" + WeatherStatus.size());
                            weatherstatusSize = WeatherStatus.size();    // save the size of 天氣狀態
                            Log.d("ppp", "weatherstatusSize :" + weatherstatusSize);
                            saveWeatherStatusSize(weatherstatusSize);     // save the length of weather status
                            Log.d("sss", "長度>>:" + getWeatherstatusSize());

                            // break;  // 處理完畢 離開 !
                        }      // 處理嘉義市

                    }

                } else {

                    Log.d("ccc", "error");
                }


            }  // end of onResponse

            @Override
            public void onFailure(Call<DataResponseWeatherIcon> call, Throwable t) {

            }

        });  //  call.enqueue

        Log.d("ppp", "weatherstatusSize :::" + getWeatherstatusSize());

    }  // end of  GetWeatherIconUpdate

    public static long convertToMilliseconds(String inputDateString, String inputFormat) {
        long milliseconds = 0;

        SimpleDateFormat inputFormatter = new SimpleDateFormat(inputFormat, Locale.getDefault());

        try {
            Date date = inputFormatter.parse(inputDateString);
            milliseconds = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return milliseconds;
    }  // end of convertToMilliseconds

    public int WhichOneofWeatherIcons(String parameterName) {

        String weatherparamter;
        weatherparamter = parameterName;
        int whichone = -1;

        if (weatherparamter.contains("多雲"))
            whichone = 0;
        else if (weatherparamter.contains("晴時多雲"))
            whichone = 1;
        else if (weatherparamter.contains("晴午後短暫雷陣雨"))
            whichone = 2;
        else if (weatherparamter.contains("多雲午後短暫雷陣雨"))
            whichone = 3;
        else if (weatherparamter.contains("多雲短暫陣雨"))
            whichone = 4;
        else if (weatherparamter.contains("多雲時晴"))
            whichone = 5;
        else
            whichone = -1;


        return whichone;

    }

    private void saveWeakStatus(WeatherTimeNStatus weatherTimeNStatus) {
        this.WeatherStatus.add(weatherTimeNStatus);
    }

    private List<WeatherTimeNStatus> getWeakStatus() {
        return this.WeatherStatus;
    }

    private void saveWeatherStatusSize(int size) {
        this.weatherstatusSize = size;
        Log.d("sss", "儲存:" + this.weatherstatusSize);

    }

    private int getWeatherstatusSize() {
        Log.d("sss", "長度:" + this.weatherstatusSize);
        return this.weatherstatusSize;

    }


    private void GetCurrentTemperature() {

        // 取得目前溫度  - 更新到右上角的數值顯示
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


    ////////////////// 一周天氣 from opendata /////////////////////////////////////////////
    private void doHttpGetOpenDataWeeklyWeatherForecastDataRequest(String URL , String locationId , String Authorization ) {


        try {

            Log.d("kkk", "doHttpGetOpenDataWeeklyWeatherForecastDataRequest()");   // dump weekly weather forecast data

            URL url = new URL(URL );   //  url
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "Basic " + Authorization);  // authorization
            conn.setRequestMethod("GET");   // post the user data
            conn.setRequestProperty("Connection", "keep-Alive");
            // conn.setRequestProperty("Content-Type", "application/json");

            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json ; ; charset=UTF-8");

            conn.setDoOutput(false);
            conn.setDoInput(true);  // Notice ! it must be set : true . input file stream
            conn.setUseCaches(false);
            conn.connect();         // connect it !

            //String json = getJsonContent(); // pass username and password in Json object
            // Toast.makeText(this, "Json " + json.toString(), Toast.LENGTH_SHORT).show();
            // Log.d(ContentValues.TAG, "Json String >> " + json.toString());
            //OutputStream os = conn.getOutputStream();
            // UTF_8 format
            // os.write(json.getBytes(StandardCharsets.UTF_8));  // pass password and new password to server
            // os.flush();
            // os.close();

            int responseCode = conn.getResponseCode();

            Log.d("zxc", "Open Data Response Code >> " + responseCode);

            // Toast.makeText(this, "Response Code:" + Integer.toString(responseCode), Toast.LENGTH_SHORT).show();
            // android.util.Log.e("tag", "responseCode = " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {

                InputStream input = conn.getInputStream();  //
                InputStreamReader reader = new InputStreamReader(input, "UTF-8");
                StringBuilder sb = new StringBuilder();

                int ss;

                while ((ss = reader.read()) != -1) {
                    sb.append((char) ss);
                    Log.d("zxc","------" + sb.toString());
                }    // get reponse - json array


                // JSONObject jsonObj = new JSONObject(sb.toString());  // Convert string to json object type

                Log.d("zxc", "Getting data from Open Data  and http retrun Code @@@@@@@@@ " + responseCode);
                Log.d("zxc", "Open Data 取得同步資料 (Json String *****>> " + sb.toString());  // debug json array

                input.close();        // close input stream
                conn.disconnect();    // disconnect http connection it !

            }
        } catch (Exception e) {

            Log.d("zxc", "Error >> " + e.toString());
            e.printStackTrace();
        }

    }  // end of doHttpGetWeatherForecastDataRequest



    //  這個是直接向 opendata 取資料  (目前不用) real time
    private void doHttpGetWeatherForecastDataRequest() {


        try {

            Log.d("kkk", "doHttpGetWeatherForecastDataRequest()");   // dump weather forecast data log to display

            URL url = new URL("https://opendata.cwa.gov.tw/api/v1/rest/datastore/");// get cable data url
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "Basic " + "CWA-AA44B18B-3CDE-43BE-9A61-95B5F9FECCE5");
            conn.setRequestMethod("GET");   // post the user data
            conn.setRequestProperty("Connection", "keep-Alive");
            // conn.setRequestProperty("Content-Type", "application/json");

            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json ; ; charset=UTF-8");

            conn.setDoOutput(false);
            conn.setDoInput(true);  // Notice ! it must be set : true . input file stream
            conn.setUseCaches(false);
            conn.connect();         // connect it !

            //String json = getJsonContent(); // pass username and password in Json object
            // Toast.makeText(this, "Json " + json.toString(), Toast.LENGTH_SHORT).show();
            // Log.d(ContentValues.TAG, "Json String >> " + json.toString());
            //OutputStream os = conn.getOutputStream();
            // UTF_8 format
            // os.write(json.getBytes(StandardCharsets.UTF_8));  // pass password and new password to server
            // os.flush();
            // os.close();

            int responseCode = conn.getResponseCode();

            Log.d("kkk", "Response Code >> " + responseCode);

            // Toast.makeText(this, "Response Code:" + Integer.toString(responseCode), Toast.LENGTH_SHORT).show();
            // android.util.Log.e("tag", "responseCode = " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {

                InputStream input = conn.getInputStream();  //
                InputStreamReader reader = new InputStreamReader(input, "UTF-8");
                StringBuilder sb = new StringBuilder();

                int ss;

                while ((ss = reader.read()) != -1) {
                    sb.append((char) ss);
                }    // get reponse - json array


                JSONObject jsonObj = new JSONObject(sb.toString());  // Convert string to json object type

                Log.d("kkk", "Getting Cable Data http retrun Code @@@@@@@@@ " + responseCode);
                Log.d("kkk", "取得同步資料 (Json String *****>> " + sb.toString());  // debug json array

                input.close();        // close input stream
                conn.disconnect();    // disconnect http connection it !

            }
        } catch (Exception e) {

            Log.d(TAG, "Error >> " + e.toString());
            e.printStackTrace();
        }

    }  // end of doHttpGetWeatherForecastDataRequest


    private void doHttpGetWeeklyWeatherForecastDataRequest(String URL , String Authorization ) {


        try {

            Log.d("zxc", "doHttpGetWeeklyWeatherForecastDataRequest()");   // Weekly weather forecast data

            URL url = new URL(URL);     // get cable data url
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "Basic " + Authorization); // authorization
            conn.setRequestMethod("GET");   // post the user data
            conn.setRequestProperty("Connection", "keep-Alive");
            // conn.setRequestProperty("Content-Type", "application/json");

            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json ; ; charset=UTF-8");

            conn.setDoOutput(false);
            conn.setDoInput(true);  // Notice ! it must be set : true . input file stream
            conn.setUseCaches(false);
            conn.connect();         // connect it !

            //String json = getJsonContent(); // pass username and password in Json object
            // Toast.makeText(this, "Json " + json.toString(), Toast.LENGTH_SHORT).show();
            // Log.d(ContentValues.TAG, "Json String >> " + json.toString());
            //OutputStream os = conn.getOutputStream();
            // UTF_8 format
            // os.write(json.getBytes(StandardCharsets.UTF_8));  // pass password and new password to server
            // os.flush();
            // os.close();

            int responseCode = conn.getResponseCode();

            Log.d("zxc", " <<<<< open data cwa Response Code >> " + responseCode);

            // Toast.makeText(this, "Response Code:" + Integer.toString(responseCode), Toast.LENGTH_SHORT).show();
            // android.util.Log.e("tag", "responseCode = " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {

                InputStream input = conn.getInputStream();  //
                InputStreamReader reader = new InputStreamReader(input, "UTF-8");
                StringBuilder sb = new StringBuilder();

                int ss;

                while ((ss = reader.read()) != -1) {
                    sb.append((char) ss);
                }    // get reponse - json array

                JSONObject jsonObj = new JSONObject(sb.toString());  // Convert string to json object type

                Log.d("zxc", "Getting Cable Data http retrun Code @@@@@@@@@ " + responseCode);
                Log.d("zxc", "取得open data 的資料 (Json String *****>> " + sb.toString());  // debug json array

                input.close();        // close input stream
                conn.disconnect();    // disconnect http connection it !

            }
        } catch (Exception e) {

            Log.d("zxc", "Error >> " + e.toString());
            e.printStackTrace();
        }

    }  // end of doHttpGetWeeklyWeatherForecastDataRequest

    private void doHttpGetWeatherForecastDataRequestWithCGSId() {


        try {

            Log.d("kkk", "doHttpGetWeatherForecastDataRequestWithCGSId()");

            URL url = new URL("https://192.168.0.135/cgc/api/cwaApi/"); //
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "Basic " + GetToken());
            conn.setRequestMethod("GET");   // post the user data
            conn.setRequestProperty("Connection", "keep-Alive");
            // conn.setRequestProperty("Content-Type", "application/json");

            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json ; ; charset=UTF-8");

            conn.setDoOutput(false);
            conn.setDoInput(true);  // Notice ! it must be set : true . input file stream
            conn.setUseCaches(false);
            conn.connect();         // connect it !

            //String json = getJsonContent(); // pass username and password in Json object
            // Toast.makeText(this, "Json " + json.toString(), Toast.LENGTH_SHORT).show();
            // Log.d(ContentValues.TAG, "Json String >> " + json.toString());
            //OutputStream os = conn.getOutputStream();
            // UTF_8 format
            // os.write(json.getBytes(StandardCharsets.UTF_8));  // pass password and new password to server
            // os.flush();
            // os.close();

            int responseCode = conn.getResponseCode();

            Log.d("kkk", "Response Code >> " + responseCode);

            // Toast.makeText(this, "Response Code:" + Integer.toString(responseCode), Toast.LENGTH_SHORT).show();
            // android.util.Log.e("tag", "responseCode = " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {

                InputStream input = conn.getInputStream();  //
                InputStreamReader reader = new InputStreamReader(input, "UTF-8");
                StringBuilder sb = new StringBuilder();

                int ss;

                while ((ss = reader.read()) != -1) {
                    sb.append((char) ss);
                }    // get reponse - json array


                JSONObject jsonObj = new JSONObject(sb.toString());  // Convert string to json object type

                Log.d("kkk", "Getting Cable Data http retrun Code @@@@@@@@@ " + responseCode);
                Log.d("kkk", "取得同步資料 (Json String *****>> " + sb.toString());  // debug json array

                input.close();        // close input stream
                conn.disconnect();    // disconnect http connection it !

            }
        } catch (Exception e) {

            Log.d(TAG, "Error >> " + e.toString());
            e.printStackTrace();
        }

    }  // end of doHttpGetWeatherForecastDataRequest




    private void RideHailingServiceDialog() {

        // Ride Hailing Service - 叫車服務對話框

        Button confirmation, cancel;

        XEditText edt;   // phone edit text

        EditText edttxt;

        View dialogView = LayoutInflater.
                from(MainActivity.this).
                inflate(R.layout.ridehailingservicedialog, null);   // inflate 叫車服務 dialog layout

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

        //set our custom alert dialog to tha alertdialog builder
        alert.setView(dialogView);   // 設定 dialog的 view

        final AlertDialog dialog = alert.create();

        PhoneNumberInputDialog = dialog;  // 為了傳遞用

        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  // 避免邊框空白 消除
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        edt = dialog.findViewById(R.id.phonenumberedittxt);    // phone edit text input

        confirmation = dialog.findViewById(R.id.confirm);     // 同意叫車
        cancel = dialog.findViewById(R.id.cancel);            // 取消叫車

        PhoneNumber = edt.getNonSeparatorText().toString();   // 取出輸入的電話號碼

        confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(MainActivity.this, "內容: " + PhoneNumber.toString(), Toast.LENGTH_SHORT).show();

                // 這個輸入的edittextbox 會將超過 10 個之後的 number digits 除去, 只留下前 10個


                if (PhoneNumber.length() == 10) {

                    // Toast.makeText(MainActivity.this, "長度", Toast.LENGTH_SHORT).show();

                    Log.d("ttt", "電話號碼: " + PhoneNumber.toString());

                    if (isDigits(PhoneNumber)) {

                        // 首先先關閉鍵盤

                        Log.d("iii", "電話號碼: " + PhoneNumber.toString());

                        view = getWindow().getDecorView();

                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                        if (imm != null) {
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        } else {

                            Log.d("sss", "電話號碼: " + PhoneNumber.toString());
                            PhoneNumberErrorDialog(edt);   // 錯誤的電話

                        }
                        // 格式符合的電話號碼

                        dialog.dismiss();
                        DialingTaxi(PhoneNumber, edtdummy, dialog);   // 撥號
                    } else {

                        PhoneNumberErrorDialog(edt);  // 電話格式錯誤
                    }

                } else {

                    // Toast.makeText(mContext, "輸入格式有誤", Toast.LENGTH_SHORT).show();
                    edt.setText("");
                    PhoneNumberErrorDialog(edt);   // 錯誤的電話號碼
                }
            }

        });   // 同意叫車


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();  // 取消叫車

            }
        });  // 取消叫車


        edtdummy = edt;    // copy a dummy phone number edit

        edt.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);   // 去底線

        // 電話號碼格式 - 輸入時 自行帶出 "-"
        edt.setPattern(new int[]{4, 6}); // xxxx-xxxxxx , phone number's format
        // 设置分隔符
        edt.setSeparator("-");

        // 電話號碼輸入框
        edt.setOnTextChangeListener(new XEditText.OnTextChangeListener() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                Log.d("ccc", ">>> " + s.toString());
                Log.d("ccc", ">>>>" + count);
                SaveFinalPhoneNumber(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

                PhoneNumber = s.toString();

                Log.d("ccc", s.toString());

                if (isNum1(PhoneNumber)) {   // 是數字 (在正常輸入中)
                    Toast.makeText(mContext, "正確", Toast.LENGTH_SHORT).show();

                    if (PhoneNumber.length() == 11) {
                        Toast.makeText(mContext, "輸入完成", Toast.LENGTH_SHORT).show();
                        Log.d("ccc", "輸入完成");

                        // 輸入完成後 , 先將輸入的字串中去除 "-" 字符 , 然後再存起來

                        if (isDigits(PhoneNumber)) {

                            PhoneNumber = PhoneNumber.replaceAll("[^0-9]", "");  // remove all  non digital number
                            Log.d("ccc", "去除後: " + PhoneNumber);

                            // 接著, 要撥號出去 it needs a dialog which shows the progress of caliing ( with animation )

                            // DialingTaxi(PhoneNumber, edtdummy , dialog );   // 撥號
                            // 必須關閉鍵盤 , 首先必須取得當前的 view 然後再關閉鍵盤 (because keyboard is also a view )

                            View view = getWindow().getDecorView();

                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                            if (imm != null) {
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            } else ;


                        } else {

                            PhoneNumberErrorDialog(edt);  // 錯誤的電話號碼

                        }

                    } else {
                        // Toast.makeText(mContext, "輸入XXX", Toast.LENGTH_SHORT).show();
                        Log.d("ccc", PhoneNumber);
                    }
                } else if (clearflag != true) {                      // 不是數字

                    // PhoneNumberErrorDialog(edt);

                }
            }
        });

        // 下面是動畫的處理 ( from bottom to center )

        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER_VERTICAL);   //  這裡可以調整 dialog 顯示的位置 (目前是中間)  由下向上
        window.setWindowAnimations(R.style.mystyle);  // 動畫

        //this line removed app bar from dialog and make it transperent and you see the image is like floating outside dialog box.
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setWindowAnimations(R.style.mystyle);    // 添加動畫
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {

                // 過三秒後要做的事情
                // dialog.dismiss();   // close ending the call dialog
                // onthephoneDialog.dismiss();  // close on the phone dialog

                // 依序退出的對話框 , 1. 通話中的對話框 2. 緊急通話的頁面
                // finish();  // 關閉當前的 activity


            }
        }, 3000);

    }  // end of RideHailingServiceDialog

    // 叫車撥號對話框

    private boolean isDigits(String phonenumber) {

        int len = phonenumber.length();
        boolean flag = true;

        for (int i = 0; i < len; i++) {

            if ((phonenumber.charAt(i) >= '0' && phonenumber.charAt(i) <= '9') || phonenumber.charAt(i) == '-')
                ;
            else {

                flag = false;  // 號碼中有非 0-9 及 - 兩類的號碼

            }
        }

        return flag;
    }

    private void DialingTaxi(String number, EditText edtdummy, AlertDialog inputphonenumberdialog) {

        // 撥號給車隊

        Button Booktaxibtn, CancelTaxibtn;

        ViewGroup viewGroup;
        viewGroup = findViewById(android.R.id.content);

        AnimationDrawable anidot = (AnimationDrawable) getResources().getDrawable(R.drawable.taxidotanimation);  // 動畫點點點


        ImageView dotdialinganimation;  // 撥號中 dot animation

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialingtaxidialog, viewGroup, false);


        // 叫車撥號對話框

        dotdialinganimation = dialogView.findViewById(R.id.dottaximag);     // 點動畫

        dotdialinganimation.setImageDrawable(anidot);        // set animation
        anidot.start();                                      // 動畫撥放

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

        //set our custom alert dialog to tha alertdialog builder
        alert.setView(dialogView);   // 設定dialog的 view

        final AlertDialog dialog = alert.create();

        // 下面是事件監聽

        ConnectionFailureListener(dialogView, dialog);   //  confirmation button listener

        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  // 避免邊框空白 消除

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // 下面是動畫的處理 ( bottom up )
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER_VERTICAL);   //  這裡可以調整 dialog 顯示的位置 (目前是中間)  由下向上
        window.setWindowAnimations(R.style.mystyle);  // 動畫

        //this line removed app bar from dialog and make it transperent and you see the image is like floating outside dialog box.
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {

                //過兩秒後要做的事情
                dialog.dismiss();                // 關閉撥號對話框

                // edt.getText().clear();        // 清空
                // 接者, 去對應是否有空的計程車
                ClearPhoneNumber(edtdummy);
                // show available phone numbers dialog

                ShowAvailableTaxiDriverDialog(inputphonenumberdialog);  // 顯示目前有空的計程車隊

            }
        }, 5000);   // 5 秒是為了模擬用的


    }

    private void ShowAvailableTaxiDriverDialog(AlertDialog inputphonedialog) {

        //  顯示可搭載的 Taxi 司機(車隊)的資訊

        ViewGroup viewGroup;
        viewGroup = findViewById(android.R.id.content);

        Button BackHome;   //

        View dialogView = LayoutInflater.from(this).inflate(R.layout.taxidriverdialog, viewGroup, false);
        dialogView.findViewById(R.id.countdowntxt);  // 倒數計時
        BackHome = dialogView.findViewById(R.id.backhome); // 回首頁

        TaxiDriverDialogView = dialogView; // copy the dialog view for

        // 叫車撥號對話框

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

        //set our custom alert dialog to tha alertdialog builder
        alert.setView(dialogView);   // 設定dialog的 view

        final AlertDialog dialog = alert.create();

        TaxiDriverDialog = dialog;   // 計程車司機的對話框

        // 下面是事件監聽
        ConnectionFailureListener(dialogView, dialog);   //  confirmation button listener

        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  // 避免邊框空白 消除

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // 下面是動畫的處理 ( from bottom to center )
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER_VERTICAL);   //  這裡可以調整 dialog 顯示的位置 (目前是中間)  由下向上
        window.setWindowAnimations(R.style.mystyle);  // 動畫

        CountDownMessageTxt = dialog.findViewById(R.id.countdowntxt);   // 倒數計時的 text
        timer.start();   // 啟動 countdown timer (派車對話框)

        //this line removed app bar from dialog and make it transperent and you see the image is like floating outside dialog box.
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {

                // 過兩秒後要做的事情
                // dialog.dismiss();                // 關閉撥號對話框
                // edt.getText().clear();        // 清空
                // 接者, 去對應是否有空的計程車

            }
        }, 10000);

        BackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                timer.cancel();    // cancel the timer (模擬用)
                dialog.dismiss();  // 關閉目前的對話框
                inputphonedialog.dismiss();  // 關閉輸入電話號碼的對話框

                finish();  // 必須關閉目前的 activity

                Intent intent = new Intent(view.getContext(), MainActivity.class);
                view.getContext().startActivity(intent);

            }
        });

    }  // end of ShowAvailableTaxiDriverDialog

    // 下面是一個 countdown timer 用來倒數計時

    public CountDownTimer timer = new CountDownTimer(91000, 1000) {
        // 設置一個 timer (91 秒開始是因為 剛進入畫面時, 必須留有視覺上的效果 )
        @Override
        public void onTick(long millisUntilFinished) {
            // 倒數中 ...
            long sec = millisUntilFinished / 1000;

            if (sec != 0) {
                // formating !
                if (sec > 9)
                    CountDownMessageTxt.setText((millisUntilFinished / 1000) + " 秒後回首頁");
                else
                    CountDownMessageTxt.setText("0" + (millisUntilFinished / 1000) + " 秒後回首頁");


            } else {

                // 倒數時間到 !
                CountDownMessageTxt.setText("");  // 時間到 ! 且關閉派車對話框
                oncancel(TaxiDriverDialogView);  // cancel the countdown timer
                TaxiDriverDialog.dismiss();       // 關閉目前的對話框
                PhoneNumberInputDialog.dismiss(); // 關閉輸入電話的對話框
                overridePendingTransition(R.anim.activity_bottom_in, R.anim.activity_bottom_out);  // animation
            }

        }

        @Override
        public void onFinish() {
            // 倒數完成
            CountDownMessageTxt.setEnabled(true);
            // countdownTitle.setText("");
        }
    };

    public void oncancel(View v) {
        timer.cancel();
    }

    /**
     * 开始倒计时
     *
     * @param v
     */
    public void restart(View v) {
        timer.start();
    }

    private void ClearPhoneNumber(EditText edtdummy) {
        edtdummy.setText("");
    }


    private void PhoneNumberErrorDialog(XEditText edt) {
        // 處理輸入電話格式錯誤

        TextInputLayout account, password;
        Button confirmation;
        Log.d("ccc", "電話號碼錯誤");


        View dialogView = LayoutInflater.
                from(MainActivity.this).
                inflate(R.layout.phonenumbererrdialog, null);

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

        //set our custom alert dialog to tha alertdialog builder
        alert.setView(dialogView);   // 設定dialog的 view

        final AlertDialog dialog = alert.create();

        // 下面是事件監聽

        ConnectionFailureListener(dialogView, dialog);   //  confirmation button listener

        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  // 避免邊框空白 消除

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // 下面是動畫的處理 ( from bottom to center )
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER_VERTICAL);   //  這裡可以調整 dialog 顯示的位置 (目前是中間)  由下向上
        window.setWindowAnimations(R.style.mystyle);  // 動畫

        //this line removed app bar from dialog and make it transperent and you see the image is like floating outside dialog box.
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {

                //過兩秒後要做的事情
                dialog.dismiss();   // close connection is failed dialog
                // edt.getText().clear();   // 清空

                int index = edt.getSelectionStart();   // 获取 Edittext 目前最後所在位置

                String str = edt.getText().toString();

                if (!str.equals("")) {   //  判斷輸入格式錯誤, 作清空動作

                    edt.getText().clear();  // 清除乾淨

                }


            }
        }, 1000);


    }  // end of ConnectionFailureDialog


    public boolean isNum1(String strNum) {

        boolean flag = false;


        for (int i = 0; i < strNum.length(); i++) {

            if (strNum.charAt(i) == '0' ||
                    strNum.charAt(i) == '1' ||
                    strNum.charAt(i) == '2' ||
                    strNum.charAt(i) == '3' ||
                    strNum.charAt(i) == '4' ||
                    strNum.charAt(i) == '5' ||
                    strNum.charAt(i) == '6' ||
                    strNum.charAt(i) == '7' ||
                    strNum.charAt(i) == '8' ||
                    strNum.charAt(i) == '9' ||
                    strNum.charAt(i) == '-') {

                flag = true;
                Log.d("ccc", Boolean.toString(flag));
                SaveFinalPhoneNumber(strNum);  // save the phone number
                Log.d("ccc", strNum);

            } else {
                SaveFinalPhoneNumber(strNum);  // save the phone number
                flag = false;
                Log.d("ccc", Boolean.toString(flag));
                Log.d("ccc", strNum);
                clearflag = true;
            }
        }  // end of for


        return flag;

    }   // end of isNum1

    private void SaveFinalPhoneNumber(String phonenum) {
        this.FinalPhoneNumber = phonenum;
    }

    private String GetFinalPhoneNumber() {
        return this.FinalPhoneNumber;
    }

    private void initTextView(EditText editText) {


        final int CODE_SIZE = 10;

        text = editText.getText().toString();

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                text = editText.getText().toString();
                if (count > after)
                    delete = true;

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                StringBuilder sb = new StringBuilder(s.toString());
                int replacePosition = editText.getSelectionEnd();

                if (s.length() != CODE_SIZE) {
                    if (!delete) {
                        if (replacePosition < s.length())
                            sb.deleteCharAt(replacePosition);
                    } else {
                        sb.insert(replacePosition, '_');
                    }

                    if (replacePosition < s.length() || delete) {
                        editText.setText(sb.toString());
                        editText.setSelection(replacePosition);
                    } else {
                        editText.setText(text);
                        editText.setSelection(replacePosition - 1);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


    }

    private void SaveLoginflag(int flag) {

        Loginflag = flag;  // save the login flag
    }

    private int GetLoginflag() {

        Log.d("abc", "GetLoginflag()");
        Log.d("abc", "目前登入旗號 : " + Loginflag);

        return Loginflag;  // get the login flag
    }

    private static boolean isExternalStorageReadOnly() {

        String extStorageState = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;


    }  // end of isExternalStorageReadOnly

    private static boolean isExternalStorageAvailable() {

        String extStorageState = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;

    }    // end of isExternalStorageAvailable


    private void CreateFile() {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            Log.d("ccc", "SD card path : " + Environment.getExternalStorageDirectory());

            File txtFile = new File(Environment.getExternalStorageDirectory(), "text.txt");

            if (!txtFile.exists()) {

                Log.d("ccc", "檔案不存在,但要建立 text.txt");

                try {

                    FileOutputStream outputStream = new FileOutputStream(txtFile);
                    outputStream.write("hello".getBytes());
                    outputStream.close();

                    Log.d("ccc", "創建 text.txt 完成");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void FindDirectory(String dir) {
        File file = new File(dir);

        if (!file.exists())
            Log.d("ccc", "檔案不存在");
        else
            Log.d("ccc", "檔案存在");

    }

    //判断文件是否存在
    public boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            return false;
        }

        return true;
    }

    private void updateTime() {
        // 時鐘 - 每 0.5 秒 更新一次時間

        Clocktimer.schedule(new TimerTask() {
            @Override
            public void run() {
                DateTxt.setText(CurrentTime());   // Get current to show

            }
        }, 0, 500);

    }    // end of updateTime 時鐘

    private void updateWeather() {

        //  每10秒 更新一次溫度

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

    private void updateWeatherIcon(ImageView weatherImage) {
        //  主畫面的天氣圖示(右上角)更新
        //  每 10秒 更新一次天氣圖示 (右上角的圖示)

        WeatherIconUpdateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // UI層更新
                runOnUiThread(new Runnable() {
                    @Override

                    // 下面是取出高雄目前的溫度 - 以每分鐘為一次取出來
                    // 使用 Retrofit 來取出天氣相關資料 (目前溫度)

                    public void run() {

                        Log.d("nnn", "GGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGG");
                        // GetWeatherIconUpdate();   // 取得目前天氣圖騰 (右上角)
                        DialogGetWeatherIconUpdate(weatherImage);   // 取得目前天氣圖騰 (對話框)

                    }
                });

            }
        }, 0, 10000); //  每 10 秒做一次更新

    }    // end of updateWeather

    private void updateWeatherDialogIcon(ImageView weatherImage) {

        //  每 10秒 更新天氣圖示 (對話框中的)

        WeatherDialogIconUpdateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // UI層更新
                runOnUiThread(new Runnable() {
                    @Override

                    // 下面是取出高雄目前的溫度 - 以每分鐘為一次取出來
                    // 使用 Retrofit 來取出天氣相關資料 (目前溫度)

                    public void run() {

                        Log.d("sss", "uuuuuuuuuuuuuuuuuuuuuu");
                        DialogGetWeatherIconUpdate(weatherImage);   // 取得目前天氣圖騰 (對話框)


                    }
                });

            }
        }, 0, 3000); //  每 10 秒做一次更新

    }    // end of updateWeather


    private boolean ExistSDCard() {

        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {

            return true;    // sd card 存在

        } else

            return false;   // sd card 不存在

    }   // end of ExistSDCard

    public long getSDFreeSize() {

        // 檢查 SD Card 的剩下容量
        // 取得 SD卡文件路徑

        File path = Environment.getExternalStorageDirectory();
        StatFs sf = new StatFs(path.getPath());
        // 获取单个数据块的大小(Byte)
        long blockSize = sf.getBlockSize();
        // 空闲的数据块的数量
        long freeBlocks = sf.getAvailableBlocks();

        // 返回 SD卡內容大小
        // return freeBlocks * blockSize;          //  unit : Byte
        // return (freeBlocks * blockSize)/1024;   //  unit : KB

        return (freeBlocks * blockSize) / 1024 / 1024; // unit : MB
    }


    private String CurrentTime() {

        String nowDate = new SimpleDateFormat("MM/dd").format(new Date());  // 取得目前的日期
        assert nowDate != null;
        String nowTime = new SimpleDateFormat("HH:mm").format(new Date());  // 取得目前時間
        assert nowTime != null;

        String nowDateNTime = nowDate + "\n" + nowTime;

        return nowDateNTime;   // 傳回目前的時間

    }   // end of CurrentTime

    private String Today() {

        String nowDate = new SimpleDateFormat("MM/dd").format(new Date());  // 取得目前的日期
        assert nowDate != null;

        return nowDate;   // 傳回目前的時間

    } // end of Today

    private String CurrentDayAndTime() {

        String nowDate = new SimpleDateFormat("YYYY-MM-dd").format(new Date());  // 取得目前的日期 YYYY-MM-DD 格式
        String nowTime = new SimpleDateFormat("HH:mm:ss").format(new Date());    // 取得目前時間 HH:MM:SS 格式

        String nowDateAndTime = nowDate + " " + nowTime;

        return nowDateAndTime;

    }


    private String GetTodayofWeek() {

        Calendar mCal = Calendar.getInstance();   // 取的  calendar instance
        int Today = mCal.get(Calendar.DAY_OF_WEEK);

        Toast.makeText(this, "Week" + Today, Toast.LENGTH_SHORT).show();

        switch (Today) {

            case 1:
                DayOfWeek = "日";
                break;
            case 2:
                DayOfWeek = "一";
                break;
            case 3:
                DayOfWeek = "二";
                break;
            case 4:
                DayOfWeek = "三";
                break;
            case 5:
                DayOfWeek = "四";
                break;
            case 6:
                DayOfWeek = "五";
                break;
            case 7:
                DayOfWeek = "六";
                break;
            default:
                Log.d(TAG, "錯誤星期");
                break;
        }

        return DayOfWeek;     // 傳回星期幾
    }

    /////////////////// 多國語言設定對話框  /////////////////////////

    private void showMultiLangDialog() {

        TextView chinese, english;
        Button confirmation;

        View dialogView = LayoutInflater.
                from(MainActivity.this).
                inflate(R.layout.multlangdialog, null);

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

        //set our custom alert dialog to tha alertdialog builder
        alert.setView(dialogView);                  // 設定dialog的 view
        final AlertDialog dialog = alert.create();

        confirmation = (Button) dialogView.findViewById(R.id.buttonOk);     //  確認按鈕
        chinese = (TextView) dialogView.findViewById(R.id.chinesetxt);      //  中文
        english = (TextView) dialogView.findViewById(R.id.englishtxt);      //  英文

        // 預設的顯示 - 中文

        chinese.setTextColor(Color.YELLOW);
        chinese.setTypeface(Typeface.DEFAULT_BOLD);  // bold type

        // then we will inflate the custom alert dialog's  layout that we created
        // setting the view of the builder to our custom view that we already inflated

        dialog.show();

        // 中文
        chinese.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(MainActivity.this, "中文", Toast.LENGTH_SHORT).show();
                english.setTextColor(Color.WHITE);     // 英文白色
                chinese.setTextColor(Color.YELLOW);    // 中文黃色

            }
        });  // 中文


        // 英文
        english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(MainActivity.this, "英文", Toast.LENGTH_SHORT).show();
                english.setTextColor(Color.YELLOW);    // 英文黃色
                chinese.setTextColor(Color.WHITE);     // 中文白色

            }
        });   // 英文


        ///////////// 這裡是最後確認的按鈕
        confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(MainActivity.this, "確定", Toast.LENGTH_SHORT).show();

                // 取出中英文按鈕的目前顏色
                ColorStateList mListch = chinese.getTextColors();  // 中文
                ColorStateList mListen = english.getTextColors();  // 英文

                int chinesecolor = mListch.getDefaultColor();      // 中文顏色
                int englishcolor = mListen.getDefaultColor();      // 英文顏色

                if (chinesecolor == Color.YELLOW && englishcolor == Color.WHITE) {

                    // 中文 :
                    // 必須設定 語言的 sharedpreference
                    // 設定語言設定 preference 為 0 - 中文

                    MultiLanguagepref  = getSharedPreferences(LANGPREFS_NAME , MODE_PRIVATE) ;   // 多語設定
                    MultiLangSetting = MultiLanguagepref.getInt(LANGFLAG_KEY, 0);             //  default's value : 0 檢查目前語言設定之用
                    Log.d("tgb","目前語言設定:" + MultiLangSetting);

                    SharedPreferences.Editor MultiLangeditor = MultiLanguagepref.edit();

                    MultiLangeditor.putInt(LANGFLAG_KEY,0);      // 將語言設定還原到預設 : 中文
                    MultiLangeditor.apply();
                    MultiLangeditor.commit();

                    if (MultiLanguagepref.getInt(LANGFLAG_KEY, 0) == 0 ) {
                        Log.d("tgb","中文設定成功") ;
                        //
                        // 中文設定成功 , 將 locale 設定為預設

                    }
                    else {
                        Log.d("tgb","中文設定失敗") ;
                    }

                    Toast.makeText(MainActivity.this, "選了中文", Toast.LENGTH_SHORT).show();
                    MultiLangbutton.setImageDrawable(getResources().getDrawable(R.drawable.btnlangtw));  // 中文 button
                    FrontView.setImageDrawable(getResources().getDrawable(R.drawable.inter_homepage_menu_zh));

                    //////////// 這裡要換成原來的圖 (天氣預報)

                    GreetingTxt.setText(spb);   // spannable string , 主畫面不需要使用 locale
                    // 同時 , 語言選項按鈕也要切換為中文

                    MultiLangbutton.setImageDrawable(getResources().getDrawable(R.drawable.btnlangtw));  // 中文 button
                    dialog.dismiss();  // 關閉目前的語言選項對話框
                    AccessibilityCHTxt.setText(spbAccessibility);  // Chinese Mode

                } else if (chinesecolor == Color.WHITE && englishcolor == Color.YELLOW) {

                    // 英文 :
                    // 必須設定 語言的 sharedpreference
                    // 設定語言設定 preference 為 1 - 英文

                    MultiLanguagepref  = getSharedPreferences(LANGPREFS_NAME , MODE_PRIVATE) ;   // 多語設定
                    MultiLangSetting = MultiLanguagepref.getInt(LANGFLAG_KEY, 0);             //  default's value : 0 檢查目前語言設定之用
                    Log.d("tgb","目前語言設定:" + MultiLangSetting);

                    SharedPreferences.Editor MultiLangeditor = MultiLanguagepref.edit();

                    MultiLangeditor.putInt(LANGFLAG_KEY,1);      // 將語言設定還原到預設 : 英文
                    MultiLangeditor.apply();
                    MultiLangeditor.commit();

                    if (MultiLanguagepref.getInt(LANGFLAG_KEY, 0) == 1 ) {
                        Log.d("tgb","英文設定成功") ;
                    }
                    else {
                        Log.d("tgb","英文設定失敗") ;
                    }
                    
                    Toast.makeText(MainActivity.this, "選了英文", Toast.LENGTH_SHORT).show();
                    MultiLangbutton.setImageDrawable(getResources().getDrawable(R.drawable.btnlangen));  // 英文 button
                    FrontView.setImageDrawable(getResources().getDrawable(R.drawable.homepagefronten));      // with 天氣預報 (English version )
                    GreetingTxt.setText("     Hello"); // 主畫面不需要使用 locale
                    AccessibilityCHTxt.setText(spbAccessibilityEN);   // English mode

                    dialog.dismiss();                  // 關閉目前的語言選項對話框

                } else {
                    ;  // do nothing
                    Log.d("ccc", "有錯誤");
                }
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  // 避免邊框空白 消除

    }  // end of showMultiLangDialog


    //////////////////////////////// 一周天氣預報對話框  /////////////////////////////////
    private void ShowWeatherDialog() {
        // Weather forecast report (一周天氣預報)

        TextView date, DayOfWeekTxt;  // 日期 星期幾
        String DayofWeek;
        ImageView TodayWeather;
        String MaxTemp, MinTemp;       // 最高溫 , 最低溫

        String starttime , endtime ;   //

        TextView MaxMinTemptxt ;       // 最高低溫度顯示
        TextView Humiditytxt   ;       // 濕度顯示
        TextView ProbabilityOfPrecipitationtxt ;   // 降雨率顯示
        ImageView WeatherDescription ;             // 天氣狀況 (中間部分)


        // 用來顯示 天氣預報對話框的 7天的星期

        TextView dayofweek1,
                dayofweek2,
                dayofweek3,
                dayofweek4,
                dayofweek5,
                dayofweek6,
                dayofweek7;   // 一星期的星期

        ImageView Day1WeatherStatusImg ,
                  Day2WeatherStatusImg ,
                  Day3WeatherStatusImg ,
                  Day4WeatherStatusImg ,
                  Day5WeatherStatusImg ,
                  Day6WeatherStatusImg ,
                  Day7WeatherStatusImg ;    // 一周天氣圖示

        TextView  Max_MinTemperatureDay1 ,
                  Max_MinTemperatureDay2 ,
                  Max_MinTemperatureDay3 ,
                  Max_MinTemperatureDay4 ,
                  Max_MinTemperatureDay5 ,
                  Max_MinTemperatureDay6 ,
                  Max_MinTemperatureDay7 ;   // 高低溫

        TextView  WeatherDescriptionDay1,
                  WeatherDescriptionDay2,
                  WeatherDescriptionDay3,
                  WeatherDescriptionDay4,
                  WeatherDescriptionDay5,
                  WeatherDescriptionDay6,
                  WeatherDescriptionDay7 ;   // 天氣描述

        SimpleDateFormat format1 = new SimpleDateFormat("MM/dd");

        Date d = new Date();
        format1.format(d);            //  month / day
        Log.d("bbb", format1.format(d));
        OneWeekAssign(d);    // 星期幾

        Log.d("bbb", "0:" + WeatherWeek[0].toString());
        Log.d("bbb", "1 :" + WeatherWeek[1].toString());
        Log.d("bbb", "2 :" + WeatherWeek[2].toString());
        Log.d("bbb", "3 :" + WeatherWeek[3].toString());
        Log.d("bbb", "4 :" + WeatherWeek[4].toString());
        Log.d("bbb", "5 :" + WeatherWeek[5].toString());
        Log.d("bbb", "6 :" + WeatherWeek[6].toString());


        // updateWeatherDialogIcon();   // 更新 icon

        View dialogView = LayoutInflater.from(this).inflate(R.layout.weatherforcastdialog, null); // 一周天氣對話框佈局

        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).setTitle("資料載入中...").setView(dialogView).create();
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);             // 此處可以設置 dialog 顯示的位置
        window.setWindowAnimations(R.style.mystyle);   // 添加動畫

        ///////////////////// 下面是主圖設定 //////////////////////////////////

        MaxMinTemptxt = dialogView.findViewById(R.id.temptxt) ;        // 高低溫顯示
        Humiditytxt = dialogView.findViewById(R.id.humiditytxt);       // 濕度
        ProbabilityOfPrecipitationtxt = dialogView.findViewById(R.id.umbrellatxt);     // 降雨率
        WeatherDescription = dialogView.findViewById(R.id.weatherimg) ;                // 中央主要天氣圖示

        date = dialogView.findViewById(R.id.datetxt);                  //  日期
        DayOfWeekTxt = dialogView.findViewById(R.id.weektxt);          // 星期幾(左上角)
        TodayWeather = dialogView.findViewById(R.id.weatherimg);       // 今天氣象圖示

        dayofweek1 = dialogView.findViewById(R.id.l1dayofweek);        // 第一個
        dayofweek2 = dialogView.findViewById(R.id.l2dayofweek);
        dayofweek3 = dialogView.findViewById(R.id.l3dayofweek);
        dayofweek4 = dialogView.findViewById(R.id.l4dayofweek);
        dayofweek5 = dialogView.findViewById(R.id.l5dayofweek);
        dayofweek6 = dialogView.findViewById(R.id.l6dayofweek);
        dayofweek7 = dialogView.findViewById(R.id.l7dayofweek);   // 第七個


        dayofweek1.setText(WeatherWeek[0]);
        dayofweek2.setText(WeatherWeek[1]);
        dayofweek3.setText(WeatherWeek[2]);
        dayofweek4.setText(WeatherWeek[3]);
        dayofweek5.setText(WeatherWeek[4]);
        dayofweek6.setText(WeatherWeek[5]);
        dayofweek7.setText(WeatherWeek[6]);

        // 取出目前的時間,在判斷在哪個區間
        // to do

        MinTemp = MinTemperatureArrayList.get(0).getValue();  // 最小溫度
        MaxTemp = MaxTemperatureArrayList.get(0).getValue() ; // 最高溫

        MaxMinTemptxt.setText(MaxTemp+"/"+MinTemp);  // max/min temperature display

        Humiditytxt.setText(humidityArrayList.get(0).getValue());  // 濕度設定

        TimeNElementValue obj = GetPop12hArrayListElement(0) ;

        Log.d("zxc","uuuuuuuu : " + obj.getStartTime());

        ProbabilityOfPrecipitationtxt.setText(GetPop12hArrayListElement(0).getValue());  // 降雨率

        String weatherstatus = WeatherDescriptionArrayList.get(0).getValue() ;


        // Just for dumpping weekly weather status descriptions

        for (int pp = 0 ; pp <  WeatherDescriptionArrayList.size() ; pp ++ ) {
            Log.d("wer","pp = " + pp + " " +  WeatherDescriptionArrayList.get(pp).getValue()); }

        if (weatherstatus.equals("晴時多雲")) {

            WeatherDescription.setImageDrawable(getResources().getDrawable(WeatherIconsList[1]));
            Log.d("zxc", "1");

        }
        else if (weatherstatus.equals("晴午後短暫雷陣雨")) {
            WeatherDescription.setImageDrawable(getResources().getDrawable(WeatherIconsList[3]));
            Log.d("zxc", "3");

        }
        else if (weatherstatus.equals("多雲")|| weatherstatus.equals("多雲時陰")) {
            WeatherDescription.setImageDrawable(getResources().getDrawable(WeatherIconsList[0]));
            Log.d("wer", "0");

        }
        else if (weatherstatus.equals("多雲時晴")) {
            WeatherDescription.setImageDrawable(getResources().getDrawable(WeatherIconsList[2]));
            Log.d("zxc", "2");

        }
        else if  (weatherstatus.equals("多雲短暫陣雨") || weatherstatus.equals("多雲午後短暫雷陣雨") ) {
            WeatherDescription.setImageDrawable(getResources().getDrawable(WeatherIconsList[4]));
            Log.d("zxc", "4");
        }
        else if (weatherstatus.equals("多雲時陰短暫陣雨或雷雨") ||
                 weatherstatus.equals("多雲短暫短暫陣雨或雷雨") ||
                 weatherstatus.equals("陰短暫陣雨或雷雨")      ||
                 weatherstatus.equals("陰時多雲短暫陣雨或雷雨") ) {
            WeatherDescription.setImageDrawable(getResources().getDrawable(WeatherIconsList[5]));

            Log.d("zxc", "5");

        }
        else {

            Log.d("zxc","天氣型態:" + weatherstatus.toString() ) ;
            Log.d("zxc","錯誤的天氣現象");

        }

        //////////////////  一周天氣 圖示  /////////////////////////

        Day1WeatherStatusImg = dialogView.findViewById(R.id.l1img) ;
        Day2WeatherStatusImg = dialogView.findViewById(R.id.l2img) ;
        Day3WeatherStatusImg = dialogView.findViewById(R.id.l3img) ;
        Day4WeatherStatusImg = dialogView.findViewById(R.id.l4img) ;
        Day5WeatherStatusImg = dialogView.findViewById(R.id.l5img) ;
        Day6WeatherStatusImg = dialogView.findViewById(R.id.l6img) ;
        Day7WeatherStatusImg = dialogView.findViewById(R.id.l7img) ;


        ////////////////  一周高低溫   ////////////////////////////////////

        Max_MinTemperatureDay1 =  dialogView.findViewById(R.id.l1temptxt);
        Max_MinTemperatureDay2 =  dialogView.findViewById(R.id.l2temptxt);
        Max_MinTemperatureDay3 =  dialogView.findViewById(R.id.l3temptxt);
        Max_MinTemperatureDay4 =  dialogView.findViewById(R.id.l4temptxt);
        Max_MinTemperatureDay5 =  dialogView.findViewById(R.id.l5temptxt);
        Max_MinTemperatureDay6 =  dialogView.findViewById(R.id.l6temptxt);
        Max_MinTemperatureDay7 =  dialogView.findViewById(R.id.l7temptxt);

        //////////////////// 一周天氣描述    ////////////////////////////////

        WeatherDescriptionDay1 = dialogView.findViewById(R.id.l1weathertxt);
        WeatherDescriptionDay2 = dialogView.findViewById(R.id.l2weathertxt);
        WeatherDescriptionDay3 = dialogView.findViewById(R.id.l3weathertxt);
        WeatherDescriptionDay4 = dialogView.findViewById(R.id.l4weathertxt);
        WeatherDescriptionDay5 = dialogView.findViewById(R.id.l5weathertxt);
        WeatherDescriptionDay6 = dialogView.findViewById(R.id.l6weathertxt);
        WeatherDescriptionDay7 = dialogView.findViewById(R.id.l7weathertxt);


        String weatherstatustemp ;   /////// 天氣狀態描述

        for (int pp = 0 ; pp < WeatherDescriptionArrayList.size() ; pp ++) {

            weatherstatustemp =    WeatherDescriptionArrayList.get(pp).getValue();  // 取得目前的天氣狀態

            ///////////// 一周天氣設定顯示  ///////////////////////////////////
            // 0, 2,4,6,8,10, 12 共 7 天

            switch (pp) {
                case 0:

                    // day 1

                    Log.d("qaz","weatherstatustemp :" + weatherstatustemp) ;
                    Day1WeatherStatusImg.setImageDrawable(getResources().getDrawable(WeatherIconsList[3]));
                    MaxTemp = MaxTemperatureArrayList.get(0).getValue();     // 最高溫
                    MinTemp = MinTemperatureArrayList.get(0).getValue();     // 最低溫
                    Max_MinTemperatureDay1.setText(MaxTemp + "/" + MinTemp + "º");  // show max/min temperature

                    if (weatherstatustemp.length() < 5)  // 判斷目前天氣描述是否為一行; 若是 ,要調整位置 加一個 換行 用來補齊一行
                    {
                        WeatherDescriptionDay1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                        WeatherDescriptionDay1.setText(weatherstatustemp + "\n");        // 天氣狀態描述
                    }
                    else {

                        if (weatherstatustemp.equals("多雲午後短暫雷陣雨")) {
                            weatherstatustemp= "多雲午後短暫陣雨" ;     // 必須調整字串長度,以合佈局 ; 避免換行
                            WeatherDescriptionDay1.setText(weatherstatustemp);               // 天氣狀態描述
                        }
                    }

                    break;
                case 2 :
                    // day 2

                    Log.d("qaz","weatherstatustemp :" + weatherstatustemp) ;
                    Day2WeatherStatusImg.setImageDrawable(getResources().getDrawable(WeatherIconsList[WhichOneWeather(weatherstatustemp)]));
                    MaxTemp = MaxTemperatureArrayList.get(2).getValue();     // 最高溫
                    MinTemp = MinTemperatureArrayList.get(2).getValue();     // 最低溫
                    Max_MinTemperatureDay2.setText(MaxTemp + "/" + MinTemp + "º");  // show max/min temperature

                    if (weatherstatustemp.length() < 5)  // 判斷目前天氣描述是否為一行; 若是 ,要調整位置 加一個 換行 用來補齊一行
                    {
                        WeatherDescriptionDay2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                        WeatherDescriptionDay2.setText(weatherstatustemp + "\n");        // 天氣狀態描述
                    }
                    else
                    if (weatherstatustemp.equals("多雲午後短暫雷陣雨")) {
                        weatherstatustemp= "多雲午後短暫陣雨" ;                  // 必須調整字串長度,以合佈局 ; 避免換行
                        WeatherDescriptionDay1.setText(weatherstatustemp);               // 天氣狀態描述
                    }
                    break;
                case 4:
                    // day 3
                    Log.d("qaz","weatherstatustemp :" + weatherstatustemp) ;
                    Day3WeatherStatusImg.setImageDrawable(getResources().getDrawable(WeatherIconsList[WhichOneWeather(weatherstatustemp)]));
                    MaxTemp = MaxTemperatureArrayList.get(4).getValue();     // 最高溫
                    MinTemp = MinTemperatureArrayList.get(4).getValue();     // 最低溫
                    Max_MinTemperatureDay3.setText(MaxTemp + "/" + MinTemp + "º");  // show max/min temperature

                    if (weatherstatustemp.length() < 5)  // 判斷目前天氣描述是否為一行; 若是 ,要調整位置 加一個 換行 用來補齊一行
                    {
                        WeatherDescriptionDay3.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                        WeatherDescriptionDay3.setText(weatherstatustemp + "\n");        // 天氣狀態描述
                    }
                    else
                    if (weatherstatustemp.equals("多雲午後短暫雷陣雨")) {
                        weatherstatustemp= "多雲午後短暫陣雨" ;     // 必須調整字串長度,以合佈局 ; 避免換行
                        WeatherDescriptionDay1.setText(weatherstatustemp);               // 天氣狀態描述
                    }

                    break;
                case 6:
                    // day 4

                    Log.d("qaz","天氣狀態天氣狀態天氣狀態天氣狀態天氣狀態天氣狀態天氣狀態 :" + weatherstatustemp.toString());
                    Day4WeatherStatusImg.setImageDrawable(getResources().getDrawable(WeatherIconsList[WhichOneWeather(weatherstatustemp)]));
                    MaxTemp = MaxTemperatureArrayList.get(6).getValue();     // 最高溫
                    MinTemp = MinTemperatureArrayList.get(6).getValue();     // 最低溫
                    Max_MinTemperatureDay4.setText(MaxTemp + "/" + MinTemp + "º");  // show max/min temperature

                    if (weatherstatustemp.length() < 5)  // 判斷目前天氣描述是否為一行; 若是 ,要調整位置 加一個 換行 用來補齊一行
                    {
                        WeatherDescriptionDay4.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                        WeatherDescriptionDay4.setText(weatherstatustemp + "\n");        // 天氣狀態描述
                    }
                    else
                    if (weatherstatustemp.equals("多雲午後短暫雷陣雨")) {
                        weatherstatustemp= "多雲午後短暫陣雨" ;     // 必須調整字串長度,以合佈局 ; 避免換行
                        WeatherDescriptionDay1.setText(weatherstatustemp);               // 天氣狀態描述
                    }
                    break ;
                case 8:
                    // day 5
                    Log.d("qaz","天氣狀態>>>>>>>>>>>>>>>>>>>>>>>>>> " + weatherstatustemp.toString()) ;
                    Day5WeatherStatusImg.setImageDrawable(getResources().getDrawable(WeatherIconsList[/*WhichOneWeather(weatherstatustemp)*/ 3]));
                    MaxTemp = MaxTemperatureArrayList.get(8).getValue();     // 最高溫
                    MinTemp = MinTemperatureArrayList.get(8).getValue();     // 最低溫
                    Max_MinTemperatureDay5.setText(MaxTemp + "/" + MinTemp + "º");  // show max/min temperature

                    if (weatherstatustemp.length() < 5)  // 判斷目前天氣描述是否為一行; 若是 ,要調整位置 加一個 換行 用來補齊一行
                    {
                        WeatherDescriptionDay5.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                        WeatherDescriptionDay5.setText(weatherstatustemp + "\n");        // 天氣狀態描述
                    }
                    else
                    if (weatherstatustemp.equals("多雲午後短暫雷陣雨")) {
                        weatherstatustemp= "多雲午後短暫陣雨" ;     // 必須調整字串長度,以合佈局 ; 避免換行
                        WeatherDescriptionDay1.setText(weatherstatustemp);               // 天氣狀態描述
                    }



                    break ;
                case 10:

                   // day 6
                    Log.d("qaz","天氣狀態>>>>>>>>>>>>>>>>>>>>>>>>>> " + weatherstatustemp.toString());
                    Day6WeatherStatusImg.setImageDrawable(getResources().getDrawable(WeatherIconsList[WhichOneWeather(weatherstatustemp)]));
                    MaxTemp = MaxTemperatureArrayList.get(10).getValue();     // 最高溫
                    MinTemp = MinTemperatureArrayList.get(10).getValue();     // 最低溫
                    Max_MinTemperatureDay6.setText(MaxTemp + "/" + MinTemp + "º");  // show max/min temperature

                    if (weatherstatustemp.length() < 5)  // 判斷目前天氣描述是否為一行; 若是 ,要調整位置 加一個 換行 用來補齊一行
                    {
                        WeatherDescriptionDay6.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                        WeatherDescriptionDay6.setText(weatherstatustemp + "\n");        // 天氣狀態描述
                    }
                    else
                    if (weatherstatustemp.equals("多雲午後短暫雷陣雨")) {
                        weatherstatustemp= "多雲午後短暫陣雨" ;     // 必須調整字串長度,以合佈局 ; 避免換行
                        WeatherDescriptionDay1.setText(weatherstatustemp);               // 天氣狀態描述
                    }

                    break ;
                case 12:
                    // day 7
                    Log.d("qaz","天氣狀態>>>>>>>>>>>>>>>>>>>>>>>>>> " + weatherstatustemp.toString());
                    Day7WeatherStatusImg.setImageDrawable(getResources().getDrawable(WeatherIconsList[WhichOneWeather(weatherstatustemp) == -1  ? 5 : WhichOneWeather(weatherstatustemp)]));
                    MaxTemp = MaxTemperatureArrayList.get(12).getValue();     // 最高溫
                    MinTemp = MinTemperatureArrayList.get(12).getValue();     // 最低溫
                    Max_MinTemperatureDay7.setText(MaxTemp + "/" + MinTemp + "º");  // show max/min temperature

                    if (weatherstatustemp.length() < 5)  // 判斷目前天氣描述是否為一行; 若是 ,要調整位置 加一個 換行 用來補齊一行
                    {
                        WeatherDescriptionDay7.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                        WeatherDescriptionDay7.setText(weatherstatustemp + "\n");        // 天氣狀態描述
                    }
                    else
                    if (weatherstatustemp.equals("多雲午後短暫雷陣雨")) {
                        weatherstatustemp= "多雲午後短暫陣雨" ;     // 必須調整字串長度,以合佈局 ; 避免換行
                        WeatherDescriptionDay1.setText(weatherstatustemp);               // 天氣狀態描述
                    }

                    break ;
                default:

                    break;
            }   // end dof switch

        }   // end of for


        // 對話框顯示時必須出現的事件監聽 !
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                Toast.makeText(MainActivity.this, "天氣預報對話框", Toast.LENGTH_SHORT).show();

                // 設置 Activity 全螢幕
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                        WindowManager.LayoutParams.FLAG_FULLSCREEN);

                View decorView = getWindow().getDecorView();

                decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN);

                controlBottomNavigation(true);
            }
        });


        dialog.show();

        /*
        View dialogView = LayoutInflater.
                from(MainActivity.this).
                inflate(R.layout.weatherforcastdialog, null);   // inflate weather forecast dialog (初始化天氣預報對話框)

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

        alert.setCancelable(false) ;    // 點擊外部不可關閉對話框

        //set our custom alert dialog to tha alertdialog builder
        alert.setView(dialogView);      // 設定dialog的 view

        final AlertDialog dialog = alert.create();

         */


        updateWeatherDialogIcon(TodayWeather);   // 更新 icon (中央)

        // 如下是要取出相關資料,然後再顯示出來

        DayofWeek = getWeekOfDate(d);             // 取得星期幾
        Log.d("bbb", DayofWeek.toString());    // dump the Dayofweek

        date.setText(format1.format(d));              // 取得今天日期
        DayOfWeekTxt.setText(DayofWeek.toString());     // 星期幾

        // 啟動一個 timer 用來取出目前的天氣圖示 ,

        Button homebackbtn;  //  回首頁

        homebackbtn = dialogView.findViewById(R.id.backhomebtn);
        // 按下之後, 對話框會消失 , 也會將 timer 刪除
        homebackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SaveLoginflag(1);  // 用來判斷是否還需要開啟帳密對話框
                dialog.dismiss();   // close the dialog
            }
        });

        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  // 避免邊框空白 消除
        WindowManager.LayoutParams params = window.getAttributes();

        //  params.x = 10;
        params.y = 500;  // 由上至下 距頂 500 dp

        window.setAttributes(params);
        window.setGravity(Gravity.TOP);
        window.setWindowAnimations(R.style.mystyle);  // 動畫


    }  // end of showMultiLangDialog

    private int WhichOneWeather ( String weatherstatus) {

        if (weatherstatus.equals("晴時多雲")) {

            Log.d("zxc", "1");
            return  1 ;

        }
        else if (weatherstatus.equals("多雲午後短暫雷陣雨")) {

            Log.d("zxc", "3");

            return 3 ;

        }
        else if (weatherstatus.equals("晴午後短暫雷陣雨")) {

            Log.d("zxc", "2");

            return 2 ;

        }
        else if (weatherstatus.equals("多雲")||weatherstatus.equals("陰時多雲") ) {

            Log.d("zxc", "0");

            return 0 ;

        }
        else if (weatherstatus.equals("多雲時晴")) {

            Log.d("zxc", "5");

            return 5 ;

        }
        else if (weatherstatus.equals("陰短暫陣雨或雷雨") || weatherstatus.equals("多雲時陰短暫陣雨或雷雨")
        || weatherstatus.equals("陰陣雨或雷雨") ){
            Log.d("zxc", weatherstatus.toString() ) ;

            return 5 ;

        }
        else if ( weatherstatus.equals("多雲短暫陣雨或雷雨")) {
            Log.d("zxc" , weatherstatus.toString()) ;

            return 3 ;
        }
        else if (weatherstatus.equals("多雲時陰短暫陣雨或雷雨") ||
                weatherstatus.equals("陰時多雲短暫陣雨或雷雨")) {
            return 3 ;
        }
        else if (weatherstatus.equals("陰天")) {
            return 0 ;
        }
        else {

            Log.d("zxc","錯誤的天氣現象");

            return -1 ;
        }

    }

    /**
     * 控制底部导航栏，显示/隐藏虚拟按键
     *
     * @param isShow true:显示；false：隐藏
     */
    private void controlBottomNavigation(boolean isShow) {
        //隐藏虚拟按键
        if (isShow) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
    }

    /**
     * 取得今天是星期幾
     *
     * @param date
     * @return
     */
    public static String getWeekOfDate(Date date) {
        // 一個禮拜的星期幾對應

        String[] weekDays = {"日", "一", "二", "三", "四", "五", "六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;

        if (w < 0)
            w = 0;
        return weekDays[w];

    }  // end of getWeekOfDate

    public static void OneWeekAssign(Date date) {

        // 首先 , 必須定義出今天的日期,然後可以對應出今天為星期幾之後就可以推論
        //

        String DayOfTheWeek;

        String[] week = {"日", "一", "二", "三", "四", "五", "六"};

        SimpleDateFormat format1 = new SimpleDateFormat("MM/dd");

        format1.format(date);                  //  month / day
        Log.d("bbb", format1.format(date));

        DayOfTheWeek = getWeekOfDate(date);  // 星期幾

        Log.d("bbb", "星期幾 :" + DayOfTheWeek);

        if (DayOfTheWeek.equals(week[0])) {  // 今天是星期日

            for (int j = 0; j < 7; j++) {

                WeatherWeek[j % 7] = week[j % 7];
            }
        } else if (DayOfTheWeek.equals(week[1])) {  // 今天是星期一

            Log.d("bbb", "1111");
            for (int j = 0; j < 7; j++) {

                WeatherWeek[(j) % 7] = week[(j + 1) % 7];
            }
        } else if (DayOfTheWeek.equals(week[2])) {  // 今天是星期二

            Log.d("bbb", "22222");

            for (int j = 0; j < 7; j++) {
                WeatherWeek[(j) % 7] = week[(j + 2) % 7];
            }
        } else if (DayOfTheWeek.equals(week[3])) {  // 今天是星期三
            for (int j = 0; j < 7; j++) {
                WeatherWeek[(j) % 7] = week[(j + 3) % 7];
            }
        } else if (DayOfTheWeek.equals(week[4])) {   // 今天是星期四
            for (int j = 0; j < 7; j++) {
                WeatherWeek[(j) % 7] = week[(j + 4) % 7];
            }
        } else if (DayOfTheWeek.equals(week[5])) {   // 今天是星期五
            for (int j = 0; j < 7; j++) {
                WeatherWeek[(j) % 7] = week[(j + 5) % 7];
            }
        } else if (DayOfTheWeek.equals(week[6])) {   // 今天是星期六
            for (int j = 0; j < 7; j++) {
                WeatherWeek[(j) % 7] = week[(j + 6) % 7];
            }
        } else {
            Log.d("bbb", "計算出來有誤");
        }

        if (false) {
            // this is for dumping day of week
            Log.d("bbb", "1 :" + WeatherWeek[0].toString());
            Log.d("bbb", "2 :" + WeatherWeek[1].toString());
            Log.d("bbb", "3 :" + WeatherWeek[2].toString());
            Log.d("bbb", "4 :" + WeatherWeek[3].toString());
            Log.d("bbb", "5 :" + WeatherWeek[4].toString());
            Log.d("bbb", "6 :" + WeatherWeek[5].toString());
            Log.d("bbb", "7 :" + WeatherWeek[6].toString());
        }

    }  // end of OneWeekAssign


    private void ConnectionFailureDialog() {
        // connection is failed dialog

        TextInputLayout account, password;
        Button confirmation;
        Log.d("ccc", "失敗");

        View dialogView = LayoutInflater.
                from(MainActivity.this).
                inflate(R.layout.connectionfailuredialog, null);

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

        //set our custom alert dialog to tha alertdialog builder
        alert.setView(dialogView);   // 設定dialog的 view

        final AlertDialog dialog = alert.create();

        // 下面是事件監聽

        ConnectionFailureListener(dialogView, dialog);   //  confirmation button listener

        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  // 避免邊框空白 消除

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // 下面是動畫的處理 ( from bottom to center )
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER_VERTICAL);   //  這裡可以調整 dialog 顯示的位置 (目前是中間)  由下向上
        window.setWindowAnimations(R.style.mystyle);  // 動畫

        //this line removed app bar from dialog and make it transperent and you see the image is like floating outside dialog box.
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {

                //過兩秒後要做的事情
                dialog.dismiss();         // close connection is failed dialog
                finishAndRemoveTask();   // 正常離開 app

            }
        }, 1000);


    }  // end of ConnectionFailureDialog

    private void ShowAccPwdLoginDialog() {
        // acc / pwd login dialog

        TextInputLayout account, password;
        Button confirmation;
        Log.d("abc", "登入");
        /*
        View dialogView = LayoutInflater.
                from(MainActivity.this).
                inflate(R.layout.loginaccpwddialog, null);

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

        //set our custom alert dialog to tha alertdialog builder
        alert.setView(dialogView);   // 設定dialog的 view

        final AlertDialog dialog = alert.create();
        dialog.setCancelable(false);  // 防止外部點擊關閉對話框 , 登入成功後才會消失

        DummyShowLoginDialog = dialog ;   // copy it !

        // 下面是事件監聽
        AccountEditListener(dialogView ,dialog);   // edit text listener

        dialog.show();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  // 避免邊框空白 消除

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // 下面是動畫的處理 ( from bottom to center )
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER_VERTICAL);   //  這裡可以調整 dialog 顯示的位置 (目前是中間)  由下向上
        window.setWindowAnimations(R.style.mystyle);  // 動畫

        //this line removed app bar from dialog and make it transperent and you see the image is like floating outside dialog box.
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

         */


    }  // end of ShowAccPwdLoginDialog

    private void SetWeatherForecastingFlag(boolean flag) {

        this.weatherforecasting = flag;
    }

    private boolean GetWeatherForecastingFlag() {
        return this.weatherforecasting;
    }

    private void ReadFileFailureDialog() {

        // 檔案中內容有誤

        TextInputLayout account, password;
        Button confirmation;

        View dialogView = LayoutInflater.
                from(MainActivity.this).
                inflate(R.layout.fileerrordialog, null);   // file error dialog

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

        //set our custom alert dialog to tha alertdialog builder
        alert.setView(dialogView);   // 設定dialog的 view

        final AlertDialog dialog = alert.create();

        // 下面是事件監聽

        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  // 避免邊框空白 消除

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);
        dialog.getWindow().setWindowAnimations(R.style.mystyle); // 添加動畫

        confirmation = dialogView.findViewById(R.id.confirmbtn);

        confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();         // close the dialog and launch login dialog again
                finishAndRemoveTask();    // exit app

            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {

                // 過兩秒後要做的事情
                dialog.dismiss();  // close the dialog and launch login dialog again

            }
        }, 2000);

    }  // end of ReadFileFailureDialog

    private void NoSuchFileDialog() {

        // 無檔案

        TextInputLayout account, password;
        Button confirmation;

        View dialogView = LayoutInflater.
                from(MainActivity.this).
                inflate(R.layout.nosuchfiledialog, null);   // no such file error dialog

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

        //set our custom alert dialog to tha alertdialog builder
        alert.setView(dialogView);   // 設定dialog的 view

        final AlertDialog dialog = alert.create();

        // 下面是事件監聽

        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  // 避免邊框空白 消除

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);
        dialog.getWindow().setWindowAnimations(R.style.mystyle); // 添加動畫

        confirmation = dialogView.findViewById(R.id.confirmbtn);

        confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();          // close the dialog and launch login dialog again
                finishAndRemoveTask();    // exit app normally
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {

                // 過兩秒後要做的事情
                dialog.dismiss();  // close the dialog and launch login dialog again

            }
        }, 2000);

    }  // end of NoSuchFileDialog

    private void AccPwdLoginSuccessfulDialog() {
        // 帳密登入成功對話框

        Log.d("abc", "登入成功");

        Button confirmation;

        View dialogView = LayoutInflater.
                from(MainActivity.this).
                inflate(R.layout.loginsuccessdialog, null);  //  inflate the view

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

        //set our custom alert dialog to tha alertdialog builder
        alert.setView(dialogView);   // 設定dialog的 view

        final AlertDialog dialog = alert.create();

        // 下面是事件監聽

        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  // 避免邊框空白 消除

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);
        dialog.getWindow().setWindowAnimations(R.style.mystyle); // 添加動畫

        confirmation = dialogView.findViewById(R.id.confirmbtn);

        SetWeatherForecastingFlag(true);   // 設置天氣預報能點擊

        confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();  // close the dialog and launch login dialog again
                DummyShowLoginDialog.dismiss();   // 關閉登入對話框
                // doHttpGetCGSInformationRequest();

            }
        });    // 在時間內,按下確認按鈕後

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {

                //過兩秒後要做的事情
                Log.d("abc", "關閉對話框");
                dialog.dismiss();    // 關閉對話框

            }
        }, 2000);


    }  // end of AccPwdLoginSuccessfulDialog

    private void doHttpGetSIPInformationRequest() {

        // 取出 sip 相關資訊

        try {

            Log.d("vbn", "doHttpGetSIPInformationRequest()");   //

            URL url = new URL("http://192.168.0.135/cgc/api/sip");  // get sip information from url

            Log.d("vbn"," >>>>>  取出之前登入成功的 Token: "  + GetToken()) ;

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization","Bearer " + GetToken());   //
            conn.setRequestMethod("GET");   // get the user data
            conn.setRequestProperty("Connection", "keep-Alive");
            // conn.setRequestProperty("Content-Type", "application/json");

            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json ; ; charset=UTF-8");

            conn.setDoOutput(false);
            conn.setDoInput(true);     // Notice ! it must be set : true . input file stream
            conn.setUseCaches(false);
            conn.connect();            // connect it !

            int responseCode = conn.getResponseCode();

            Log.d("vbn", "Response Code - sip response code  >> " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {

                // http code : 200 , http 請求成功

                Log.d("vbn", "進入- sip 資料連線 ");

                InputStream input = conn.getInputStream();  //
                InputStreamReader reader = new InputStreamReader(input, "UTF-8");
                StringBuilder sb = new StringBuilder();

                int ss;

                // 取出 sip json 字串
                while ((ss = reader.read()) != -1) {

                    sb.append((char) ss);  // 將讀到內容附加到字串中

                }    // get sip response - json array

                Log.d("vbn" , ">>>　sip json字串 : " + sb.toString()) ;

                // sip 是全部的 json string 對應

                Gson gson = new Gson();
                com.smartcity.api.ResponseSip ar = gson.fromJson(sb.toString(), com.smartcity.api.ResponseSip.class);

                // 這個 json string 不一定會按照順序排列 所以看到 log dump 不一定看的出來資料的完整

                Log.d("vbn" , "ar.status :" + ar.getStatus()) ;
                Log.d("vbn",  "ar.message : " + ar.getMessage()) ;
                Log.d("vbn",  "ar.ok : " + ar.isOk()) ;
                Log.d("vbn",  "sip ar 內容 : " + ar.getResult() ) ;

                String domain = ar.getResult().getDomain() ; // 取出 sip object
                String type1Number  = ar.getResult().getType1Number() ;  // 取出  type1Number
                String type2Number = ar.getResult().getType2Number() ;    // 取出 type2Numer

                Log.d("vbn","00000000000000000000000000000000000");
                Log.d("vbn","&&&& domain :" + domain);
                Log.d("vbn","&&&& type1Number :" + type1Number);
                Log.d("vbn","&&&& type2Number :" + type2Number);

                // 將取出來的 sip 資料 放入 contentValues 然後寫入 db (interboxdb) 中的 sip1 表格中
                // 首先, 要先清空之前 sip1 中舊資料

                ContentValues values = new ContentValues();

                values.put( "domain"   ,  domain   );               // domain
                values.put( "type1Number" ,  type1Number );            // type1number
                values.put( "type2Number"    ,  type2Number );             // type2number
                Log.d("vbn","(((((((((((((((((((((((((((((((((((") ;
                Log.d("vbn","domain :" + values.get("domain").toString()) ;
                Log.d("vbn","type1Number :" + values.get("type1Number").toString()) ;
                Log.d("vbn","type2Number :" + values.get("type2Number").toString()) ;

                // 將取出來的 sip 資料 存在 SharedPreferences 中 以供之後市民專線 1999 之用
                sharedPref = getSharedPreferences("siptable", Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("domain", values.get("domain").toString());                     // domain
                editor.putString("type1Number", values.get("type1Number").toString());           //  type1number
                editor.putString("type2Number", values.get("type2Number").toString());           //  type2number
                editor.apply();

                db = myDatabaseHelper.getWritableDatabase(); // for writing dbatabase

                /* 下面的做法有問題 , 勿用 ! */
                if (true) {
                    try {

                        // 1. 先將之前資料表中資料清空
                        db.delete("sip1", null, null);  // 先將之前的資料清空

                        // 2. 判斷是否清空了
                        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sip1", null);

                        if (cursor.moveToFirst()) {

                            int count = cursor.getInt(0);

                            if (count == 0) {
                                //  cgs1 為空
                                Log.d("vbn", "sip1 表格內容中舊資料被清空了!");
                            } else {
                                //  cgs1 不為空
                                Log.d("vbn", "sip1 內容不為空.");
                            }

                            cursor.close();
                        }

                        // 開始插入新資料到 sip1

                        myDatabaseHelper = new MyDatabaseHelper(getApplicationContext());

                        db = myDatabaseHelper.getWritableDatabase() ;

                        try {

                            Log.d("vbn", "sip1 開始將新資料插入!!");
                            // long count1 = db.insert("sip1", null, values);
                            // Log.d("vbn", " NN>>>>>>>>>  sip1 新插入的行數 :" + count1);

                            // Cursor ycursor = db.rawQuery("SELECT * FROM sip1", null);
                            // cursor.moveToFirst();
                            // int count = ycursor.getInt(0);

                            // if (count == 0) {

                            // 表格為空
                            //  Log.d("vbn","表格內容已空") ;

                            // } else {

                            // Log.d("vbn","sip1 有資料插入了") ;
                            // }

                        } catch (Exception e) {
                            Log.d("vbn", e.getMessage());
                        }

                    } catch (Exception e) {
                        Log.d("vbn", "" + e.getMessage());
                    }
                }  // end of false


                input.close();        // close input stream
                conn.disconnect();    // disconnect http connection it !

            }   // status code : 200
            else {

                Log.d("vbn", "@@ cgc 連線有誤, 回傳碼 :"  + responseCode);

            }

        }
        catch(Exception e) {

            Log.d("vbn", "Error >> " + e.toString());
            e.printStackTrace();
        }

    }   // end of function


    private void doHttpGetCGSInformationRequest() {

        // 取得 interbox 的相關資訊 : cgs

        try {

            Log.d("bnm", "doHttpGetCGSInformationRequest()");   // dump cable data log to display

            // URL url = new URL("http://192.168.100.201/cgc/api/inter_cgs");  // get cgs information from url
            URL url = new URL("http://192.168.0.135/cgc/api/inter_cgs");  // get cgs information from url

            Log.d("bnm"," >>>>>  取出之前登入成功的 Token: "  + GetToken()) ;

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // 下面是若網路有出現網絡請求的超時，可以增加連接超時和讀取超時的時間

            conn.setConnectTimeout(10000);  // 設置連接超時時間為10秒
            conn.setReadTimeout(10000);     // 設置讀取超時時間為10秒

            conn.setRequestProperty("Authorization","Bearer " + GetToken());   //
            conn.setRequestMethod("GET");   // get the user data
            conn.setRequestProperty("Connection", "keep-Alive");
            // conn.setRequestProperty("Content-Type", "application/json");

            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json ; ; charset=UTF-8");

            conn.setDoOutput(false);
            conn.setDoInput(true);     // Notice ! it must be set : true . input file stream
            conn.setUseCaches(false);
            conn.connect();            // connect it !

            int responseCode = conn.getResponseCode();

            Log.d("qaz", "Response Code - inter box cgs  >> " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {

                // http code : 200 , http 請求成功

                Log.d("qaz", "進入- cgc 資料連線 ");

                InputStream input = conn.getInputStream();  //
                InputStreamReader reader = new InputStreamReader(input, "UTF-8");
                StringBuilder sb = new StringBuilder();

                int ss;

                // 取出 json 字串
                while ((ss = reader.read()) != -1) {

                    sb.append((char) ss);  // 將讀到內容附加到字串中

                    Log.d("qaz" , sb.toString()) ;

                }    // get response - json array

                // InterBoxCgs 是全部的 json string 對應

                Gson gson = new Gson();
                com.smartcity.api.Response ar = gson.fromJson(sb.toString(), com.smartcity.api.Response.class);

                Log.d("abc" , "ar.status :" + ar.getStatus()) ;
                Log.d("abc",  "ar.message : " + ar.getMessage()) ;
                Log.d("abc",  "ar.ok : " + ar.isOk()) ;
                Log.d("abc",  "cgs ar 內容:" + ar.getResult()) ;

                // 這個 json string 不一定會按照順序排列 所以看到 log dump 不一定看的出來資料的完整
                // response result 取出
                InterBoxCgs m = ar.getResult();

                // result 中各欄位的抽取 ///////////////////////////////////////////////////////////////////////////
                com.smartcity.api.Cgs cgs = m.getCgs();                                     // 取出 cgs object
                List<com.smartcity.api.CgsImg> cgsImglist = m.getCgsImgList() ;             // 取出 cgsImg list
                List<com.smartcity.api.Travel> travelList = m.getTravelList() ;             // 取出 travel list
                List<com.smartcity.api.TravelImg> traveImglList = m.getTravelImgList() ;    // 取出 travelImg list
                List<com.smartcity.api.CgsTravel> cgsTravelList = m.getCgsTravelList() ;    // 取出 cgstravel list

                Log.d("qaz"," $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$  travelList長度 :" + travelList.size()) ;
                for (int oo = 0 ; oo < travelList.size(); oo++) {
                    Log.d("qaz", "travelList 內容 :" + travelList.get(oo).getDescription());
                }

                // 取出 cgs 各欄位 - cgsId , name , city , cityEn , area , areaEn , addrDesc , addrDescEn , interVer, interIp,  sipNumber, sipPassword

                Log.d("qaz","&&　cgs  ---------------------------------");
                Log.d("qaz" , "&&&&　cgs id : " + cgs.getCgsId()) ;
                Log.d("qaz" , "&&&&　cgs name : " + cgs.getCgsName()) ;
                Log.d("qaz" , "&&&&　cgs city  : " + cgs.getCity()) ;
                Log.d("qaz" , "&&&&　cgs cityen : " + cgs.getCityEn()) ;
                Log.d("qaz" , "&&&&　cgs area  : " + cgs.getArea()) ;
                Log.d("qaz" , "&&&&　cgs areaEn : " + cgs.getAreaEn()) ;
                Log.d("qaz" , "&&&&　cgs addrDesc : " + cgs.getAddrDesc()) ;
                Log.d("qaz" , "&&&&　cgs addrDescEn : " + cgs.getAddrDescEn()) ;
                Log.d("qaz" , "&&&&　cgs interVer : " + cgs.getInterVer()) ;
                Log.d("qaz" , "&&&&　cgs interIp : " + cgs.getInterIp()) ;
                Log.d("qaz" , "&&&&　cgs sipNumber : " + cgs.getSipNumber()) ;
                Log.d("qaz" , "&&&&　cgs sipPassword : " + cgs.getSipPassword()) ;


                // 取出 cgsImgList 各欄位 - cgsId , cgsImgId, Category , OrgFileName
                // category - 用來分類 是哪種資料型態 , 目前是 cgsImgList 中的圖片

                for (int ii = 0 ; ii < cgsImglist.size() ; ii++ ) {

                    Log.d("love"," ************　cgsImglist ---------------------------------");
                    Log.d("love", "cgsId:" + cgsImglist.get(ii).getCgsId());
                    Log.d("love", "cgsImgId:" + cgsImglist.get(ii).getCgsImgId());
                    Log.d("love", "Category:" + cgsImglist.get(ii).getCategory());
                    Log.d("love", "OrgFileName:" + cgsImglist.get(ii).getOrgFileName());

                }   // end of for - cgsImglist

                // 取出 travelList 各欄位 - tarvelInfoId , name, nameEn , category , location , locationEn , description , descriptionEn

                for (int jj = 0 ; jj < travelList.size() ; jj ++ ) {

                    Log.d("ccc"," () travelList ---------------------------------");
                    Log.d("ccc", "tarvelInfoId:" + travelList.get(jj).gettarvelInfoId());
                    Log.d("ccc", "name:" + travelList.get(jj).getname());
                    Log.d("ccc", "nameEn:" + travelList.get(jj).getNameEn());
                    Log.d("ccc", "category:" + travelList.get(jj).getCategory());
                    Log.d("ccc","location:" + travelList.get(jj).getLocation()) ;
                    Log.d("ccc","locationEn:" + travelList.get(jj).getLocationEn() ) ;
                    Log.d("ccc","description:" + travelList.get(jj).getDescription()) ;
                    Log.d("ccc","descriptionEn:" + travelList.get(jj).getLocationEn());

                }  // end of for - travelList


                // 取出 traveImglList 各欄位 - ImgId , travelInfoId , OrgFileName , SortSeq
                for (int nn = 0 ;  nn < traveImglList.size() ; nn ++) {

                    Log.d("ttt"," <> TraveImglList ---------------------------------");
                    Log.d("ttt", "tarvelImgId:" + traveImglList.get(nn).getTravelImgId());
                    Log.d("ttt", "name:" + traveImglList.get(nn).getTravelInfoId());
                    Log.d("ttt", "OrgFileName:" + traveImglList.get(nn).getOrgFileName());
                    Log.d("ttt", "SortSeq:" + traveImglList.get(nn).getSortSeq());

                } // end of for - traveImglList

                // 取出 cgsTravelList 各欄位 - cgsId , travelInfoId , distance,  walkingTime , OrgFileName
                // 這裡是主畫面顯示(1st layer)

                for ( int aa = 0 ; aa < cgsTravelList.size() ; aa ++) {
                    Log.d("ttt", "@@ cgsTravelList ---------------------------------");
                    Log.d("ttt", "cgsId:" + cgsTravelList.get(aa).getCgsId());
                    Log.d("ttt", "travelInfoId:" + cgsTravelList.get(aa).getTravelInfoId());
                    Log.d("ttt", "distance:" + cgsTravelList.get(aa).getDistance());
                    Log.d("ttt", "walkingTime:" + cgsTravelList.get(aa).getWalkingTime());
                    Log.d("ttt", "orgFileName:" + cgsTravelList.get(aa).getOrgFileName());
                }

                ///////// cgs 資料表建立 ////////////////////////////////////////
                // put it all and store them into database table - cgs  !
                // 將取出來的 cgs 資料 放入 contentValues 然後寫入 db (cgs.sqlite) 中的 cgs1 表格中

                ContentValues values_cgs = new ContentValues();

                values_cgs.put( "cgsId"   ,  cgs.getCgsId()   );       // cgs id
                values_cgs.put( "cgsName" ,  cgs.getCgsName() );       // cgs name
                values_cgs.put( "city"    ,  cgs.getCity());             // cgs city
                values_cgs.put( "cityEn"  ,  cgs.getCityEn());         // cgs cityEn
                values_cgs.put( "area"    ,  cgs.getArea());             // cgs area
                values_cgs.put( "areaEn"  ,  cgs.getAreaEn());         // cgs areaEn
                values_cgs.put( "addrDesc"  ,  cgs.getAddrDesc());     // cgs desc
                values_cgs.put( "addrDescEn"  ,  cgs.getAddrDescEn());     // cgs descEn
                values_cgs.put( "sipNumber"  ,  cgs.getSipNumber());     // cgs sip number
                values_cgs.put( "sipPassword"  ,  cgs.getSipPassword());     // cgs sip password
                values_cgs.put( "interVer" ,  cgs.getInterVer());            //  interVer
                values_cgs.put( "interIp"  ,  cgs.getInterIp());             // interIp

                try {
                    // 1. 先將之前資料表中資料清空
                    db.delete("cgs1", null, null);  // 先將之前的資料清空

                    // 2. 判斷是否清空了
                    Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM cgs1", null);

                    if (cursor.moveToFirst()) {

                        int count = cursor.getInt(0);

                        if (count == 0) {
                            //  cgs1 為空
                            Log.d("bnm", "cgs1 表格內容中舊資料被清空了!");
                        } else {
                            //  cgs1 不為空
                            Log.d("bnm", "cgs1 內容不為空.");
                        }

                        cursor.close();
                    }

                    // 開始插入新資料到 cgs1

                    Log.d("bnm","cgs1 開始將新資料插入!!");
                    long count1 = db.insert("cgs1", null, values_cgs);
                    Log.d("bnm", "cgs1 新插入的行數 :" + count1);

                } catch (Exception e ) {
                    Log.d("bnm" , ""+ e.getMessage());
                }  // end of try ... catch

                Cursor cursor_cgs = db.rawQuery("SELECT * FROM cgs1", null);
                cursor_cgs.moveToFirst();
                int count_cgs = cursor_cgs.getInt(0);

                if (count_cgs == 0) {

                    // 表格為空
                    Log.d("bnm","cgs 表格內容已空") ;

                } else {

                    // 表格不為空

                    Log.d("bnm", "cgs 表格內容已有資料了 ! ");
                    Log.d("bnm", "首先將新資料列出來驗證一下");

                    if (cursor_cgs.moveToFirst()) {

                        int howmany = 0 ;

                        // 取出來欄位驗證一下 (含圖檔)
                        do {

                            // int cgsImgId  = cursor_cgs.getInt(cursor_cgs.getColumnIndexOrThrow("cgsImgId"));
                            // int  cgsId = cursor_cgs.getInt(cursor_cgs.getColumnIndexOrThrow("cgsId"));
                            // Log.d("vbn", "++++++++++++++++++++++++++++++");
                            // Log.d("vbn", "cgsImgId : " + cgsImgId);  //
                            // Log.d("vbn","cgsId : " + cgsId);

                        } while (cursor_cgs.moveToNext());
                    }
                }

                ///////// cgsImgList 資料表建立 ////////////////////////////////////////
                // put it all and store them into database table - cgsImgList   !
                // 將取出來的 cgsImgList 資料 放入 contentValues 然後寫入 db (cgs.sqlite) 中的 cgsImgList 表格中
                // 先將之前cgsImgList 資料表中資料清空

                try {
                    // 1. 先將之前資料表中資料清空
                    db.delete("cgsImgList", null, null);  // 先將之前的資料清空

                    // 2. 判斷是否清空了
                    Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM cgsImgList", null);

                    if (cursor.moveToFirst()) {

                        int count = cursor.getInt(0);

                        if (count == 0) {
                            //  cgs1 為空
                            Log.d("bnm", "cgsImgList 表格內容中舊資料被清空了!");
                        } else {
                            //  cgs1 不為空
                            Log.d("bnm", "cgsImgList 內容不為空.");
                        }

                        cursor.close();
                    }
                } catch (Exception e) {
                    Log.d("bnm" , "錯誤 :" + e.getMessage().toString()) ;


                }

                Log.d("bnm","cgsImgList  開始將新資料插入!!");
                long _count_cgsImgList  ;

                // 這個 bytearray 是用來存放圖檔資料 - cgsImgList

                byte[] imagebytearray = new byte[1024*100];    // For storing image byte array : 102400 bytes

                ContentValues values_cgsImg = new ContentValues();

                clearExternalStorageDirectory(); // 清空資料

                for ( int pp = 0 ; pp < cgsImglist.size() ; pp++ ) {

                    // put it into contentvalue one by one

                    values_cgsImg.put("cgsImgId", cgsImglist.get(pp).getCgsImgId());        // cgsImgId
                    values_cgsImg.put("cgsId", cgsImglist.get(pp).getCgsId());              // cgsId
                    values_cgsImg.put("category", cgsImglist.get(pp).getCategory());        // category
                    values_cgsImg.put("orgFileName", cgsImglist.get(pp).getOrgFileName());  // orgFileName

                    Log.d("bnm", "....... 開始下載圖檔並且插入資料庫表格 cgsImgList 中");

                    int cgsImgId = cgsImglist.get(pp).getCgsImgId();

                    // 下載圖檔 - cgsImgList
                    imagebytearray = downloadImage("http://192.168.0.135/cgc/api/cgsImg/" + cgsImgId);
                    Log.d("bnm", "  cgsImglist >>>>>>>>>> imagebytearray 大小 : " + imagebytearray.length);
                    values_cgsImg.put("image", imagebytearray);   // 將取出的 image 存入 cgsImg 表格中

                    // 將 byte array 先轉成 bitmap 格式 之後再轉成 png 格式

                    Bitmap bitmap = BitmapFactory.decodeByteArray(imagebytearray, 0, imagebytearray.length);
                    mContext = getApplicationContext();

                    // 建立一個外部儲存路徑 (SDcard card)用來存放 cgsImglist 的圖檔
                    // 圖檔名稱為: cgsImgX.png ; X 是 cgsImgId 為圖片的流水號

                    File playFilesDir = getOrCreateDirectory("cgsimglist"); // 建立 cgsimglist 路徑

                    File FileName = new File(playFilesDir, "cgsimg" + cgsImgId + ".png");

                    File externalStorageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    // 或者使用公共目錄（如果需要其他應用訪問圖片）
                    // File externalStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

                    // 檔案檢查 - 目的 :若外部儲存記憶體中有檔案就先刪除,以免記憶體不夠用

                    if (FileName.isFile()) {  // 若有舊檔案,就先刪除

                        //刪除舊檔案
                        // FileName.deleteOnExit();
                        Log.d("bnm", "~~~~~~~~~~ <<<<<<< 刪除舊檔案 ~~~~~~~~~~");

                    } else {  // 無舊檔 就要建立


                        Log.d("bnm", ">>>>>>>>>>>>>>>>>> //////// 無舊檔案");
                             // 無舊檔案

                        savecgsImgListPngFileToExternalStorage(mContext,  bitmap ,cgsImgId) ;  // save cgsImgList's images to external storage !

                        String pngFileName = "cgsImgList"+cgsImgId+".png" ;

                        if (isPNGFileExists(pngFileName, mContext)) {
                            Log.d("bnm","檔案:" + pngFileName + " 已在外部記憶體中");
                        }
                        else {
                            Log.d("bnm","檔案:" + pngFileName + " 不在外部記憶體中");
                        }

                    }

                    Log.d("bnm" ,"values_cgsImg 中的 image : " + values_cgsImg.get("image")) ;

                    _count_cgsImgList = db.insert("cgsImgList", null, values_cgsImg);

                    if (_count_cgsImgList != -1  ) {
                        Log.d("bnm", "   cgsImglist >>>>>>>>> _count_cgsImgList :" + _count_cgsImgList);
                    }
                    else {
                        Log.d("bnm", "插入失敗") ;
                    }

                }   // cgsImg 表格插入 - end of for

                // 接著, 檢查外部記憶體

                //////////////////////////// 去外部記憶體中檢查檔案是否存在

                Cursor cursor_cgsImgList = db.rawQuery("SELECT * FROM cgsImgList", null);
                cursor_cgsImgList.moveToFirst();
                int count_cgsImgList = cursor_cgsImgList.getInt(0);

                if (count_cgsImgList == 0) {

                    // 表格為空
                    Log.d("bnm","cgsImgList 表格內容已空") ;

                } else {

                    // 表格不為空

                    Log.d("bnm", "cgsImgList 表格內容已有資料了 ! ");
                    Log.d("bnm", "首先將新資料列出來驗證一下");

                    if (cursor_cgsImgList.moveToFirst()) {

                        int howmany = 0 ;

                        // 取出來欄位驗證一下 (含圖檔)
                        do {

                            int cgsImgId = cursor_cgsImgList.getInt(cursor_cgsImgList.getColumnIndexOrThrow("cgsImgId"));
                            int  cgsId = cursor_cgsImgList.getInt(cursor_cgsImgList.getColumnIndexOrThrow("cgsId"));
                            String category = cursor_cgsImgList.getString(cursor_cgsImgList.getColumnIndexOrThrow("category"));
                            String orgFileName = cursor_cgsImgList.getString(cursor_cgsImgList.getColumnIndexOrThrow("orgFileName"));
                            //////////////////// 圖檔取出
                            byte[] imageByteArray = cursor_cgsImgList.getBlob(cursor_cgsImgList.getColumnIndexOrThrow("image"));  // 取圖
                            /// 下面是驗證圖檔是否在資料庫中

                            howmany += 1 ;

                            //if (imageByteArray.length != 0 ) {
                            //    Bitmap bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
                            //    tryimg.setImageBitmap(bitmap);  // 測試秀一下看看
                            // }
                                Log.d("qaz" , "cgsImgList  %%%%%%%%%%%%%%%%　第 " + howmany + " 張圖" );
                            // }
                            //  else {
                            //     Log.d("qaz","　%%%%%%%%%%%% 有錯誤的圖檔") ;
                            //  }

                            Log.d("zzzz", "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                            Log.d("zzzz", "%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                            Log.d("zzzz", "cgsImgId : " + cgsImgId);  // 圖檔的對應 id --> 用來下載對應的圖檔
                            Log.d("zzzz", "cgsId : " + cgsId);
                            Log.d("zzzz", "category : " + category);
                            Log.d("zzzz", "orgFileName : " + orgFileName);
                            Log.d("zzzz","圖檔(bytes) :" + imageByteArray ) ;


                        } while (cursor_cgsImgList.moveToNext());
                    }

                    // 資料驗證一下是否寫入資料庫中 - cgsImgList

                    cursor_cgsImgList = db.query("cgsImgList", null, null, null, null, null, null);
                    int _count = cursor_cgsImgList.getCount();

                    if (_count == 0) {

                        // 表格為空
                        Log.d("bnm", "cgsImgList 表格內容已空");
                    } else {

                        // 表格不為空

                        Log.d("bnm", "cgsImgList 表格已經插入新資料了 ! ");

                        cursor_cgsImgList.close();

                        // db.close();  Notice ! 最後再關 在 onDestory 中關
                        // db.close();

                    }  // 有資料的處理
                }  // 有資料

                // travelList
                // put it all and store them into database table - travelList   !
                // 將取出來的 travelList 資料 放入 contentValues 然後寫入 db (cgs.sqlite) 中的 travelList 表格中
                // 先將之前 travelList 資料表中資料清空

                try {
                    // 1. 先將之前資料表中資料清空
                    db.delete("travelList", null, null);  // 先將之前的資料清空

                    // 2. 判斷是否清空了
                    Cursor cursor_travelList = db.rawQuery("SELECT COUNT(*) FROM travelList", null);
                    // 3.
                    if (cursor_travelList != null ) {
                        Log.d("bnm", "cursor_travelList 有效");

                        if (cursor_travelList.moveToFirst()) {

                            int count_travelList = cursor_travelList.getInt(0);

                            if (count_travelList == 0) {
                                //  cgs1 為空
                                Log.d("vbn", "travelList 表格內容中舊資料被清空了!");
                            } else {
                                //  cgs1 不為空
                                Log.d("vbn", "travelList 內容不為空.");
                            }

                            cursor_travelList.close();

                        } else {
                            Log.d("vbn", "cursor_travelList 失效");

                        }

                    }
                } catch (Exception e) {
                    Log.d("vbn" , "錯誤 :" + e.getMessage().toString()) ;

                }  // end of travelList 清空檢查

                Log.d("bnm","travelList  開始將新資料插入!!");

                long count_ ;

                ContentValues values_travelList = new ContentValues();

                for ( int gg = 0 ; gg < travelList.size() ; gg++ ) {

                    // put it into contentvalue one by one

                    values_travelList.put("travelInfoId", travelList.get(gg).gettarvelInfoId());        // travelInfoId
                    values_travelList.put("name",travelList.get(gg).getname()) ;                        // name
                    values_travelList.put("nameEn",travelList.get(gg).getNameEn()) ;                    // nameEn
                    values_travelList.put("category", travelList.get(gg).getCategory());                // category
                    values_travelList.put("location", travelList.get(gg).getLocation());                // location
                    values_travelList.put("locationEn", travelList.get(gg).getLocationEn());            // locationEn
                    values_travelList.put("description" , travelList.get(gg).getDescription()) ;        // description
                    values_travelList.put("descriptionEn" , travelList.get(gg).getDescriptionEn()) ;    // descriptionEn

                    count_ = db.insert("travelList", null, values_travelList );
                    Log.d("vbn"," %%%%%%%%%%%%%%%% count_travelList :" + count_ ) ;

                }  // end of for - 插入資料

                //   準備 dump 資料驗證一下是否寫入資料庫中 - travelList , 先檢查一下

                Cursor cursor_travelList = db.rawQuery("SELECT * FROM travelList", null);
                cursor_travelList.moveToFirst();
                int count_travelList = cursor_travelList.getInt(0);


                if (count_travelList == 0) {

                    // 表格為空
                    Log.d("bnm","travelList 表格內容已空") ;

                } else {

                    // 表格不為空

                    Log.d("bnm", "travelList 表格內容已有資料了 ! ");
                    Log.d("bnm", "首先將新資料列出來驗證一下");

                    if (cursor_travelList.moveToFirst()) {

                        // 取出來欄位驗證一下

                        do {

                            int travelInfoId = cursor_travelList.getInt(cursor_travelList.getColumnIndexOrThrow("travelInfoId"));
                            String name = cursor_travelList.getString(cursor_travelList.getColumnIndexOrThrow("name"));
                            String nameEn = cursor_travelList.getString(cursor_travelList.getColumnIndexOrThrow("nameEN"));
                            String category = cursor_travelList.getString(cursor_travelList.getColumnIndexOrThrow("category"));
                            String location = cursor_travelList.getString(cursor_travelList.getColumnIndexOrThrow("location"));
                            String locationEn = cursor_travelList.getString(cursor_travelList.getColumnIndexOrThrow("locationEn"));
                            String description  = cursor_travelList.getString(cursor_travelList.getColumnIndexOrThrow("description"));
                            String descriptionEn  = cursor_travelList.getString(cursor_travelList.getColumnIndexOrThrow("descriptionEn"));

                            if ( category.equals("2")) {  // 住宿的
                                Log.d("222", " %%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                                Log.d("222", "travelInfoId : " + travelInfoId);
                                Log.d("222", "name : " + name);
                                Log.d("222", "nameEn : " + nameEn);
                                Log.d("222", "category : " + category);
                                Log.d("222", "location: " + location);
                                Log.d("222", "description: " + description);
                                Log.d("222", "descriptionEn: " + descriptionEn);
                            }

                        } while (cursor_travelList.moveToNext());
                    }
                }  // 資料不為空

                cursor_travelList = db.query("travelList", null, null, null, null, null, null);
                int _count_travelList = cursor_travelList.getCount();

                if (_count_travelList == 0) {

                    // 表格為空
                    Log.d("vbn", "travelList 表格內容已空");
                } else {

                    // 表格不為空

                    Log.d("bnm", "travelList 表格已經插入新資料了 ! ");

                    cursor_travelList.close();


                }  // 有資料

                // travelImgList
                // put it all and store them into database table - travelImgList   !
                // 將取出來的 travelImgList 資料 放入 contentValues 然後寫入 db (cgs.sqlite) 中的 travelImgList 表格中
                // 先將之前 travelImgList 資料表中資料清空

                try {
                    // 1. 先將之前資料表中資料清空
                    db.delete("travelImgList", null, null);  // 先將之前的資料清空

                    // 2. 判斷是否清空了
                    Cursor cursor_travelImgList = db.rawQuery("SELECT COUNT(*) FROM travelImgList", null);
                    // 3.

                    if (cursor_travelImgList != null ) {
                        Log.d("bnm", "cursor_travelImgList 有效");

                        if (cursor_travelImgList.moveToFirst()) {

                            int count_travelImgList = cursor_travelImgList.getInt(0);

                            if (count_travelImgList == 0) {
                                //  cgs1 為空
                                Log.d("bnm", "travelImgList 表格內容中舊資料被清空了!");
                            } else {
                                //  cgs1 不為空
                                Log.d("bnm", "travelImgList 內容不為空.");
                            }

                            cursor_travelImgList.close();

                        } else {
                            Log.d("vbn", "cursor_travelImgList 失效");

                        }

                    }
                } catch (Exception e) {
                    Log.d("vbn" , "錯誤 :" + e.getMessage().toString()) ;

                }  // end of travelImgList 清空檢查


                Log.d("bnm","travelImgList  開始將新資料插入!!");

                ContentValues values_travelImgList = new ContentValues();
                byte[] imagebytearray_travelImgList ;
                int travelImgId ;   // this is for debugging

               //  clearExternalStorageDirectory(); // 清空資料

                for ( int ff = 0 ; ff < traveImglList.size() ; ff ++ ) {

                    // put it into contentvalue one by one

                    values_travelImgList.put("travelImgId", traveImglList.get(ff).getTravelImgId());        // travelImgId
                    values_travelImgList.put("travelInfoId",traveImglList.get(ff).getTravelInfoId()) ;      // travelInfoId
                    values_travelImgList.put("orgFileName",traveImglList.get(ff).getOrgFileName()) ;        // orgFileName
                    values_travelImgList.put("sortSeq", traveImglList.get(ff).getSortSeq());                // sortSeq

                    travelImgId = traveImglList.get(ff).getTravelImgId() ;

                    Log.d("101","travelImgId >>>> " + travelImgId  ) ;

                    // int travelImgId =  traveImglList.get(ff).getTravelImgId() ;  // 取出  travelImgId

                    // 下載圖檔 - travelImgList
                    imagebytearray_travelImgList = downloadImage("http://192.168.0.135/cgc/api/travelImg/" +  travelImgId  );

                    Log.d("bnm" , "    %%%%%%%%%%%%%%%% travelImgList imagebytearray 大小 : " + imagebytearray_travelImgList.length) ;
                    values_travelImgList.put("image" , imagebytearray_travelImgList);   // 將取出的 image 存入 travelImgList 表格中

                    Bitmap bitmap = BitmapFactory.decodeByteArray(imagebytearray_travelImgList, 0, imagebytearray_travelImgList.length);
                    mContext = getApplicationContext();

                    // 建立一個外部儲存路徑 (SDcard card)用來存放 travelImgList 的圖檔
                    // 圖檔名稱為: travelImgListX.png ; X 是 travelImgId 為圖片的流水號

                    File playFilesDir = getOrCreateDirectory("travelImgList"); // 建立 travelImgList 路徑

                    File FileName = new File(playFilesDir, "travelImgList" + travelImgId + ".png");

                    File externalStorageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    // 或者使用公共目錄（如果需要其他應用訪問圖片）
                    // File externalStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

                    // 檔案檢查 - 目的 :若外部儲存記憶體中有檔案就先刪除,以免記憶體不夠用

                    if (FileName.isFile()) {  // 若有舊檔案,就先刪除

                        //刪除舊檔案
                        // FileName.deleteOnExit();
                        Log.d("bbb", "~~~~~~~~~~ <<<<<<< 刪除舊檔案 ~~~~~~~~~~");

                    } else {  // 無舊檔 就要建立


                        Log.d("bnm", ">>>>>>>>>>>>>>>>>> //////// 無舊檔案");
                        // 無舊檔案

                        savetravelImgListPngFileToExternalStorage(mContext,  bitmap ,travelImgId) ;  // save travelImgList's images to external storage !

                        String pngFileName = "travelImgList"+travelImgId+".png" ;

                        if (isPNGFileExists(pngFileName, mContext)) {
                            Log.d("bnm","檔案:" + pngFileName + " 已在外部記憶體中");
                        }
                        else {
                            Log.d("bnm","檔案:" + pngFileName + " 不在外部記憶體中");
                        }

                        Log.d("bnm" ,"travelImgList 中的 image : " + values_cgsImg.get("image")) ;

                    }


                    long _counttravelImgList = db.insert("travelImgList", null, values_travelImgList);

                    // Log.d("bbb" ,"values_travelImgList 中的 image : " + values_cgsImg.get("image")) ;
                    // 插入資料含圖檔

                    if (_counttravelImgList != -1  ) {  // 插入成功
                        Log.d("bnm", "    %%%%%%%%%%%%%%%% _counttravelImgList :" + _counttravelImgList);
                    }
                    else {
                        Log.d("bnm", "插入失敗") ;
                    }


                }  // end of for - travelImgList 插入資料

                //   準備 dump 資料驗證一下是否寫入資料庫中 - travelImgList , 先檢查一下

                // Cursor cursor_travelImgList = db.rawQuery("SELECT image FROM travelImgList"
                //        , null);  //  /* SELECT * FROM travelImgList" */

                /*
                cursor_travelImgList.moveToFirst();
                int count_travelImgList = cursor_travelImgList.getInt(0);

                if (count_travelImgList == 0) {

                    // 表格為空
                    Log.d("bbb","travelImgList 表格內容已空") ;

                } else {


                    // 表格不為空

                    Log.d("bbb", "travelImgList 表格內容已有資料了 ! ");
                    Log.d("bbb", ">>>> travelImgList 首先將新資料列出來驗證一下");

                    Log.d("bbb"," ************************** cursor 大小:" +  cursor_travelImgList.getCount()) ;

                    if (cursor_travelImgList.moveToFirst()) {

                        // 取出來欄位驗證一下
                        int howmany = 0 ;

                        do {

                            try {

                                int travelImgId = cursor_travelImgList.getInt(cursor_travelImgList.getColumnIndexOrThrow("travelImgId"));
                                int traveleInfoId = cursor_travelImgList.getInt(cursor_travelImgList.getColumnIndexOrThrow("travelInfoId"));
                                String orgFileName = cursor_travelImgList.getString(cursor_travelImgList.getColumnIndexOrThrow("orgFileName"));
                                int sortSeq = cursor_travelImgList.getInt(cursor_travelImgList.getColumnIndexOrThrow("sortSeq"));

                                byte[] imageByteArray = cursor_travelImgList.getBlob(cursor_travelImgList.getColumnIndexOrThrow("image"));  // 取圖
                                /// 下面是驗證圖檔是否在資料庫中
                                howmany += 1 ;

                                //if (imageByteArray.length != 0 ) {
                                //    Bitmap bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
                                //    tryimg.setImageBitmap(bitmap);  // 測試秀一下看看
                                // }
                              
                                // }
                                //  else {
                                //     Log.d("qaz","　%%%%%%%%%%%% 有錯誤的圖檔") ;
                                //  }

                                Log.d("bbb", "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< ");
                                Log.d("bbb", "travelImgId : " + travelImgId);
                                Log.d("bbb", "traveleInfoId : " + traveleInfoId);
                                Log.d("bbb", "orgFileName : " + orgFileName);
                                Log.d("bbb", "sortSeq : " + sortSeq);
                                Log.d("bbb","image(圖檔)大小 :" + imageByteArray.length) ;

                            }
                            catch (Exception e) {
                                Log.d("bbb", "錯誤 :" + e.getMessage().toString() ) ;
                            }

                        } while (cursor_travelImgList.moveToNext());
                    }

                }  // 資料不為空

                 */

                // Cursor cursor_travelImgList = db.query("travelImgList", null, null, null, null, null, null);

                Cursor cursor_travelImgList  = db.rawQuery("SELECT image FROM travelImgList ", null);

                int _count_travelImgList = cursor_travelImgList.getCount();

                if (_count_travelImgList == 0) {

                    // 表格為空
                    Log.d("bnm", "travelImgList 表格內容已空");
                } else {

                    // 表格不為空
                    Log.d("bnm" , "資料筆數 :" + _count_travelImgList) ;
                    Log.d("bnm", "travelImgList 表格已經插入新資料了 ! ");


                    // cursor_travelImgList.moveToFirst() ;   // 移動游標到第一個位置
                    /*
                    if (cursor_travelImgList.moveToFirst()) {

                        // travelImgList 的欄位取出驗證一下

                        do {

                            // int travelImgId = cursor_travelImgList.getInt(cursor_travelImgList.getColumnIndexOrThrow("travelImgId"));
                            //int travelInfoId = cursor_travelImgList.getInt(cursor_travelImgList.getColumnIndexOrThrow("travelInfoId"));
                            //String orgFileName  = cursor_travelImgList.getString(cursor_travelImgList.getColumnIndexOrThrow("orgFileName"));
                            //int  sortSeq  = cursor_travelImgList.getInt(cursor_travelImgList.getColumnIndexOrThrow("sortSeq"));
                            // byte[] imagebyteArray =  cursor_travelImgList.getBlob(cursor_travelImgList.getColumnIndexOrThrow("image"));

                            Log.d("bbb", "KKKKKKKKKKKKKKKKKKKKKKKKK ");
                            //Log.d("bbb", "travelImgId : " + travelImgId);
                            //Log.d("bbb", "travelInfoId : " + travelInfoId);
                            //Log.d("bbb", "orgFileName : " + orgFileName);
                            //Log.d("bbb", "sortSeq : " + sortSeq);
                            // Log.d("bbb" , "imagebyteArray :" + imagebyteArray) ;

                        } while (cursor_travelImgList.moveToNext());
                    }

                    cursor_travelImgList.close();

                     */


                }  // 有資料

                // cgstravelList
                // put it all and store them into database table - cgstravelList   !
                // 將取出來的 cgstravelList 資料 放入 contentValues 然後寫入 db (cgs.sqlite) 中的 cgstravelList 表格中
                // 先將之前 cgstravelList 資料表中資料清空

                try {
                    // 1. 先將之前資料表中資料清空
                    db.delete("cgstravelList", null, null);  // 先將之前的資料清空

                    // 2. 判斷是否清空了

                    Cursor cursor_cgstravelList = db.rawQuery("SELECT COUNT(*) FROM cgstravelList", null);
                    // 3.
                    if (cursor_cgstravelList != null ) {

                        Log.d("999", "cursor_cgstravelList 有效");

                        if (cursor_cgstravelList.moveToFirst()) {

                            int count_cgstravelList = cursor_cgstravelList.getInt(0);

                            if (count_cgstravelList == 0) {
                                //  cgs1 為空
                                Log.d("999", "cgstravelList 表格內容中舊資料被清空了!");
                            } else {
                                //  cgs1 不為空
                                Log.d("999", "cgstravelList 內容不為空.");
                            }

                            cursor_cgstravelList.close();

                        } else {
                            Log.d("999", "cursor_cgstravelList 失效");

                        }

                    }
                } catch (Exception e) {
                    Log.d("vbn" , "錯誤 :" + e.getMessage().toString()) ;

                }  // end of cgstravelList 清空檢查


                Log.d("999","cgstravelList  開始將新資料插入!!");

                byte[] imagebytearray_cgstravelList = new byte[1024*100];    // For storing image byte array : 102400 bytes


                ContentValues values_cgstravelList = new ContentValues();

                for ( int kk = 0 ; kk < cgsTravelList.size() ; kk ++ ) {

                    // put it into contentvalue one by one

                    values_cgstravelList.put("cgsId",        cgsTravelList.get(kk).getCgsId());              // cgsId
                    values_cgstravelList.put("travelInfoId", cgsTravelList.get(kk).getTravelInfoId()) ;      // travelInfoId
                    values_cgstravelList.put("distance"    , cgsTravelList.get(kk).getDistance()) ;          // distance
                    values_cgstravelList.put("walkingTime" , cgsTravelList.get(kk).getWalkingTime());        // walking time
                    values_cgstravelList.put("orgFileName" , cgsTravelList.get(kk).getOrgFileName()) ;       // orgFileName

                    int cgsId = cgsTravelList.get(kk).getCgsId() ;                 // 取出  cgsId
                    int travelInfoId =  cgsTravelList.get(kk).getTravelInfoId() ;  // 取出  travelInfoId

                    String url_cgsTravelList = "http://192.168.0.135/cgc/api/cgsTravel"    ;
                    url_cgsTravelList = url_cgsTravelList+ "/" + cgsId + "/" +travelInfoId ;

                    Log.d("999", "cgstravelLists's url -----" + url_cgsTravelList) ;

                    // 下載圖檔 - cgstravelList
                    imagebytearray_cgstravelList = downloadImage(url_cgsTravelList);

                    Log.d("999" , "    %%%%%%%%%%%%%%%% cgstravelList imagebytearray 大小 : " + imagebytearray_cgstravelList.length) ;

                    try {

                        values_cgstravelList.put("image" , imagebytearray_cgstravelList);   // 將取出的 image 存入 cgstravelList 表格中

                        Bitmap bitmap = BitmapFactory.decodeByteArray(imagebytearray_cgstravelList, 0, imagebytearray_cgstravelList.length);
                        mContext = getApplicationContext();

                        // 建立一個外部儲存路徑 (SDcard card)用來存放 cgstravelList 的圖檔
                        // 圖檔名稱為: travelImgListX.png ; X 是 cgstravelId 為圖片的流水號

                        File playFilesDir = getOrCreateDirectory("cgstravelList"); // 建立 travelImgList 路徑

                        File FileName = new File(playFilesDir, "cgstravelList" + "_" + cgsId + "_"+ travelInfoId + ".png");

                        File externalStorageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                        // 或者使用公共目錄（如果需要其他應用訪問圖片）
                        // File externalStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

                        // 檔案檢查 - 目的 :若外部儲存記憶體中有檔案就先刪除,以免記憶體不夠用

                        if (FileName.isFile()) {  // 若有舊檔案,就先刪除

                            //刪除舊檔案 0924
                            // FileName.deleteOnExit();
                            Log.d("999", "~~~~~~~~~~ <<<<<<< 刪除舊檔案 ~~~~~~~~~~");

                        } else {  // 無舊檔 就要建立

                            Log.d("999", ">>>>>>>>>>>>>>>>>> //////// 無舊檔案");

                            // 無舊檔案

                            savecgsTravelListPngFileToExternalStorage(mContext,  bitmap , cgsId , travelInfoId ) ;  // save travelImgList's images to external storage !

                            String pngFileName = "cgstravelList"+ "_"+ cgsId + "_" + travelInfoId+".png" ;

                            if (isPNGFileExists(pngFileName, mContext)) {
                                Log.d("bnm","檔案:" + pngFileName + " 已在外部記憶體中");
                            }
                            else {
                                Log.d("bnm","檔案:" + pngFileName + " 不在外部記憶體中");
                            }

                            Log.d("bnm" ,"cgstravelList 中的 image : " + values_cgstravelList.get("image")) ;

                        }


                        long _countcgstravelList = db.insert("cgstravelList", null, values_cgstravelList);

                        if (_countcgstravelList != -1  ) {  // 插入成功
                            Log.d("bnm", "   %%%%%%%%%%%%%%%% 插入成功 _countcgstravelList :" + _countcgstravelList);
                        }
                        else {
                            Log.d("333", "插入失敗") ;
                        }

                    } catch (Exception e) {

                        Log.d("bnm","%%%%%%%%%%%%%%%%" + e.getMessage().toString());
                    }

                    // Log.d("bbb" ,"values_travelImgList 中的 image : " + values_cgsImg.get("image")) ;
                    // 插入資料含圖檔

                }  // end of for - cgstravelList 插入資料

                //   準備 dump 資料驗證一下是否寫入資料庫中 - cgstravelList , 先檢查一下

                Cursor cursor_cgstravelList = db.rawQuery("SELECT * FROM cgstravelList", null);
                cursor_cgstravelList.moveToFirst();
                int count_cgstravelList = cursor_cgstravelList.getInt(0);

                if (count_cgstravelList == 0) {

                    // 表格為空
                    Log.d("bnm","cgstravelList 表格內容已空") ;

                } else {

                    // 表格不為空

                    Log.d("bnm", "cgstravelList 表格內容已有資料了 ! ");
                    Log.d("bnm", "%%%%%%%%%%%%%%%% cgstravelList 首先將新資料列出來驗證一下");

                    if (cursor_cgstravelList.moveToFirst()) {

                        // 取出來欄位驗證一下

                        do {

                            try {

                                int cgsId = cursor_cgstravelList.getInt(cursor_cgstravelList.getColumnIndexOrThrow("cgsId"));
                                int traveleInfoId = cursor_cgstravelList.getInt(cursor_cgstravelList.getColumnIndexOrThrow("travelInfoId"));
                                String distance = cursor_cgstravelList.getString(cursor_cgstravelList.getColumnIndexOrThrow("distance"));
                                String walkingTime  = cursor_cgstravelList.getString(cursor_cgstravelList.getColumnIndexOrThrow("walkingTime"));
                                String orgFileName = cursor_cgstravelList.getString(cursor_cgstravelList.getColumnIndexOrThrow("orgFileName"));

                                byte[] imageByteArray_cgsTravlList  = cursor_cgstravelList.getBlob(cursor_cgstravelList.getColumnIndexOrThrow("image"));  // 取圖

                                Log.d("bnm", "cgsTravlList }}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}} ");
                                Log.d("bnm", "cgsId : " + cgsId );
                                Log.d("bnm", "traveleInfoId : " + traveleInfoId);
                                Log.d("bnm", "distance : " + distance);
                                Log.d("bnm", "walkingTime : " + walkingTime);
                                Log.d("bnm", "orgFileName : " + orgFileName);
                                Log.d("bnm","image :" + imageByteArray_cgsTravlList);

                            }
                            catch (Exception e) {
                                Log.d("333", "錯誤 :" + e.getMessage().toString() ) ;
                            }

                        } while (cursor_cgstravelList.moveToNext());
                    }
                }  // 資料不為空

                cursor_cgstravelList = db.query("cgsTravelList", null, null, null, null, null, null);
                int _count_cgstravelList = cursor_cgstravelList.getCount();

                if (_count_cgstravelList == 0) {

                    // 表格為空
                    Log.d("bnm", "cgsTravelList 表格內容已空");
                } else {

                    // 表格不為空

                    Log.d("bnm", "cgsTravelList 表格已經插入新資料了 ! ");

                    cursor_cgstravelList.close();

                    Log.d("zxc", "=======================  所有的表格已經插入新資料了 ! ================");


                     filedownloadcflag = true;   // 設定旗號 , 轉檔完成
                     loginfirsttime = true ;
                     Log.d("zxc", " filedownloadcflag :::  " + filedownloadcflag  ) ;
                     Log.d("zxc", "檔案下載完畢 loginfirsttime :  " + loginfirsttime ) ;

                    /////////////// 當下載轉檔完成 ,則必須設定旗號用來之後判斷是否是第一次進入 app  ///////////////////

                    sharedPreferenceslogin = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor logineditor = sharedPreferenceslogin.edit();

                    logineditor.putInt(FLAG_KEY, 1);  // 將登入旗號設置為 1
                    logineditor.apply();
                    logineditor.commit();

                    // 取出登入旗號,以便判斷是否旗號已設置

                    isFirstTimeLogin = sharedPreferenceslogin.getInt(FLAG_KEY, 0);          // default's value : 0

                    Log.d("qsx","重新啟動 app 並下載 cgs資料完成並存圖完成") ;
                    Log.d("qsx","目前 isFirstTimeLogin :" + isFirstTimeLogin ) ;

                }   // 有資料

                // 這裡應該要檢查 extrenal storage 中的圖片是否有效 !//////////////////////////

                findAllPngFiles();    // find all png files !

                // JSONObject jsonObj = new JSONObject(sb.toString());  // Convert string to json object type
                // Log.d("qqq", "登入成功後回傳的 interbox cgsjson 字串 >>>>" + sb.toString()) ;
                // notice ! 這個不能用 android studio 的 console 來看  , 會被截斷資料顯示不完整

            /*
            int status ;
            String message ;

            JSONObject subjsonobject            = jsonObj.getJSONObject("result") ;               //  json object (全部的資料)
            JSONArray  cgsTravelListjsonarray      = subjsonobject.getJSONArray("cgsTravelList") ;   //  json cgsTravelList array
            JSONArray  travelImgListjsonArray   = subjsonobject.getJSONArray("travelImgList") ;   //  json travelImgList array
            JSONArray  travelListjsonarray      = subjsonobject.getJSONArray("travelList") ;      //  json travelList array
            JSONObject cgsobject  = subjsonobject.getJSONObject("cgs");
            JSONArray  cgsImgListjsonarray = subjsonobject.getJSONArray("cgsImgList");

             */

                // Dumping its contains as below
                /*
            Log.d("qqq", "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$")   ;
            status = jsonObj.getInt("status") ;
            Log.d("qqq", "status: "   + status);
            message = jsonObj.getString("message");
            Log.d("qqq", "message:"   + message);
            Log.d("qqq", "result: "   + subjsonobject ) ;  // 主體
            Log.d("kkk","    ");

            Log.d("kkk" , "  ----  cgsTravelList: "  + cgsTravelListjsonarray );          // cgsTravelList
            Log.d("kkk" , "  ----  travelImgList: "  + travelImgListjsonArray ) ;         // travelImgList
            Log.d("kkk" , "  ----  travelList: "     + travelListjsonarray );             // travelList
            Log.d("kkk",  "  ----  cgsImgList: "     + cgsImgListjsonarray) ;
            Log.d("kkk" , "  ----  cgs: "            + cgsobject) ;

            Log.d("kkk", "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$")   ;
            Log.d("kkk" ,  " ----  cgsTravelList 長度: "    + cgsTravelListjsonarray.length()) ;
            Log.d("kkk" ,  " ----  travelImgList 長度: "    + travelImgListjsonArray.length()) ;
            Log.d("kkk" ,  " ----  travelList 長度: "       + travelListjsonarray.length()) ;
            Log.d("kkk" ,  " ----  cgsImgList 長度: "       + cgsImgListjsonarray.length()) ;
            Log.d("kkk" ,  " ----  cgs 長度: "              + cgsobject.length()) ;
            Log.d("kkk",  "_______________________________")   ;

                 */

                // 第一個 json array  - cgsTravelList
                // 解開 cgsTravelList json array 中的各個欄位
                // cgsTravelList - 其中每個 object 的欄位為
                // cgsId , travelInfoId , distance , walkingTime  , orgFileName (共 5 個)
                // 其中最後一個是地圖名稱, 但實體必須藉由 cgsId 帶入後下載並存到資料庫中

                JSONObject   cgsTravelListjsonboject;
                int     cgsId , travelInfoId ;   // cgsId , travelInfoId
                int     sortSeq_ ;
                double  distance ;               // 距離
                int     walkingTime ;            // 步行時間
                String  mapfileName ;            // 地圖檔案名稱
                CgsTravelListjsonboject     cgstravelListjsonboject ;  // CgsTravelList jsonboject

                /*

            Log.d("kkk", "cgsTravelListjsonarray 長度:" +  cgsTravelListjsonarray.length() ) ;

            for (int ii = 0 ; ii < cgsTravelListjsonarray.length() ; ii ++) {

                Log.d("kkk",  " cgsTravelList +++++++++++++++++++++++++++++") ;

                cgsTravelListjsonboject = cgsTravelListjsonarray.getJSONObject(ii) ;        // 取得第 i 個 jsonobject
                Log.d("kkk","cgsTravelListjsonboject >> " + cgsTravelListjsonboject) ;

                cgsId = cgsTravelListjsonboject.getInt("cgsId") ;// cgs id
                Log.d("kkk" , "cgsId : "        + cgsId ) ;
                travelInfoId  = cgsTravelListjsonboject.getInt("travelInfoId");    // travelInfoId
                Log.d("kkk" , "travelInfoId : " + travelInfoId ) ;
                distance  = cgsTravelListjsonboject.getDouble("distance");         // distance
                Log.d("kkk" , "distance(距離) : "     + distance ) ;
                // sortSeq_ = cgsTravelListjsonboject.getInt("sortSeq");
                // Log.d("abc","sortSeq :" + sortSeq_) ;
                walkingTime = cgsTravelListjsonboject.getInt("walkingTime") ;      // walkingTime
                Log.d("kkk" , "walkingTime(步行時間) : "  + walkingTime ) ;
                mapfileName = cgsTravelListjsonboject.getString("orgFileName");    // mapfileName
                Log.d("kkk" , "mapfileName(地圖檔案名稱) : "  + mapfileName ) ;

                // cgstravelListjsonboject = new CgsTravelListjsonboject(cgsId ,travelInfoId , distance ,  walkingTime , mapfileName ) ;
                // 接著,加入這個 object 到 arraylist

                // cgsTravelListjsonbojectArray.add(cgstravelListjsonboject) ;   // add an object to the rear of array list


                // CgsTravelListjsonboject cc = cgsTravelListjsonbojectArray.get(ii);

            }   // end of for loop  - dumping cgsImgListjsonarray's contents

                Log.d("abc", "1111111111111 cgsTravelListjsonboject Array的長度: " + cgsTravelListjsonbojectArray.size()) ;
                /////////////////////////////  revisit data and put it all into 表格   CgsTravelList

                // 建立表格資料前先刪除之前建立的全部資料, 目的是為避免重複加入資料

                int count = database.delete(
                        CgsTravelListArray.CgsTravelListEntry.TABLE_NAME,      // 要刪除資料所在的資料表
                        null,                 // 篩選欄位 (相當於 WHERE 後面的條件)
                        null                   // 篩選欄位的資料 (相當於 WHERE 後面條件的資料)
                );

                Log.d("mmm" , "刪除的資料筆數 :" + count ) ;

                for (int x = 0 ; x < cgsTravelListjsonbojectArray.size() ; x ++ ) {

                    CgsTravelListjsonboject c =  cgsTravelListjsonbojectArray.get(x);

                    Log.d("kkk" , "getcgsId -- " + c.getcgsId());
                    Log.d("kkk" , "gettravelInfoId -- " + c.gettravelInfoId());
                    Log.d("kkk" , "getdistance -- " + c.getdistance());
                    // Log.d("abc" , "getSortSeq -- " + c.getSortSeq());
                    Log.d("kkk" , "getwalkingTime -- " + c.getwalkingTime());
                    Log.d("kkk" , "getmapfileName -- " + c.getmapfileName());

                    ContentValues values = new ContentValues();

                    values.put(CgsTravelListArray.CgsTravelListEntry.COLUMN_NAME_cgsId, c.getcgsId());
                    values.put(CgsTravelListArray.CgsTravelListEntry.COLUMN_NAME_travelInfoId, c.gettravelInfoId());
                    values.put(CgsTravelListArray.CgsTravelListEntry.COLUMN_NAME_distance, c.getdistance());
                    // values.put(CgsTravelListArray.CgsTravelListEntry.COLUMN_NAME_sortSeq, c.getSortSeq());
                    values.put(CgsTravelListArray.CgsTravelListEntry.COLUMN_NAME_walkingTime, c.getwalkingTime());
                    values.put(CgsTravelListArray.CgsTravelListEntry.COLUMN_NAME_mapfileName, c.getmapfileName());

                    long newRowId = database.insert(CgsTravelListArray.CgsTravelListEntry.TABLE_NAME, null, values);

                    Log.d("kkk","oooooooooooooooooooooooo目前RowId :" + newRowId) ; // 顯示加入資料的 row id
                }   // end of dumping data

                Cursor cursorTravelList = database.query(
                        CgsTravelListArray.CgsTravelListEntry.TABLE_NAME,      // 要查詢的資料表
                        null,                      // 要查詢的欄位 (使用 null 表示全部欄位)
                        null,                      // 過濾欄位 (相當於 WHERE 後面的條件)
                        null,                      // 過濾欄位的資料 (相當於 WHERE 後面條件的資料)
                        null,                      // 分組 (相當於 SQL 中 GROUP BY 後面的語法)
                        null,                      // (相當於 SQL 中 HAVING 後面的語法)
                        null                       // 排序 (相當於 ORDER BY 後面的語法)
                );      // 查詢表格資料 (無條件)

                Log.d("kkk","cursorTravelList cursor 長度:" + cursorTravelList.getCount());

                Log.d("kkk","下面是表格 cursorTravelList 中的每筆資料內容");

                while (cursorTravelList.moveToNext()) {

                    String cgsid            = cursorTravelList.getString(cursorTravelList.getColumnIndexOrThrow(CgsTravelListArray.CgsTravelListEntry.COLUMN_NAME_cgsId));
                    String travelInfoId_    = cursorTravelList.getString(cursorTravelList.getColumnIndexOrThrow(CgsTravelListArray.CgsTravelListEntry.COLUMN_NAME_travelInfoId));
                    String distance_        = cursorTravelList.getString(cursorTravelList.getColumnIndexOrThrow(CgsTravelListArray.CgsTravelListEntry.COLUMN_NAME_distance));
                    String walkingTime_     = cursorTravelList.getString(cursorTravelList.getColumnIndexOrThrow(CgsTravelListArray.CgsTravelListEntry.COLUMN_NAME_walkingTime));
                    String mapfileName_     = cursorTravelList.getString(cursorTravelList.getColumnIndexOrThrow(CgsTravelListArray.CgsTravelListEntry.COLUMN_NAME_mapfileName));

                    Log.d("kkk","%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                    Log.d("kkk" , "cgsid >>> " + cgsid ) ;
                    Log.d("kkk" , "travelInfoId_ >>> " + travelInfoId_ ) ;
                    Log.d("kkk" , "distance_ >>> " + distance_ ) ;
                    Log.d("kkk" , "walkingTime_ >>> " + walkingTime_ ) ;
                    Log.d("kkk" , "mapfileName_ >>> " + mapfileName_ ) ;

                }

                // 第二個 json array  - travelImgList
                // 解開 travelImgList json array 中的各個欄位
                // travelImgList - 其中每個 object 的欄位為
                // travelImgId , travelInfoId  , orgFileName , sortSeq  , file  (共 5 個)

                JSONObject   cgsTravelImgListjsonboject ;
                int travelImgId , travelInfoId_ ;
                String orgFileName ;
                int sortSeq ;
                String file ;

                CgsTravelImgListjsonboject     cgsTravelImgListjsonboject1 ;  // CgsTravelImgList jsonboject

                Log.d("abc","travelImgListjsonArray的長度 :" + travelImgListjsonArray.length()) ;

                for ( int jj = 0 ; jj < travelImgListjsonArray.length() ; jj ++ ) {

                    Log.d("abc",  " travelImgList {{{{{{{{{{{{{{{{{{{{{{{{{{{{{ ") ;

                    cgsTravelImgListjsonboject = travelImgListjsonArray.getJSONObject(jj) ;
                    travelImgId = cgsTravelImgListjsonboject.getInt("travelImgId") ;    // travel image id
                    Log.d("kkk" , "travelImaId : " + travelImgId ) ;
                    travelInfoId_ = cgsTravelImgListjsonboject.getInt("travelInfoId");  // travel Information Id
                    Log.d("kkk" , "travelInfoId : " + travelInfoId_ ) ;
                    orgFileName = cgsTravelImgListjsonboject.getString("orgFileName");  // orgFile Name
                    Log.d("kkk" , "orgFileName(來源圖檔名稱) : " + orgFileName ) ;
                    sortSeq = cgsTravelImgListjsonboject.getInt("sortSeq") ;            // sort sequence
                    Log.d("kkk" , "sortSeq(排序順序) : " + sortSeq ) ;
                    file = cgsTravelImgListjsonboject.getString("file");
                    Log.d("kkk" , "file(檔案) : " + file ) ;

                    cgsTravelImgListjsonboject1 = new CgsTravelImgListjsonboject(travelImgId ,travelInfoId_ , orgFileName , sortSeq , file ) ;

                    cgsTravelImgListjsonbojectArray.add(cgsTravelImgListjsonboject1) ;   // add an object

                }   //  // end of for loop  - dumping travelImgListjsonArray's contents

                Log.d("kkk", "2222222222222 cgsTravelImgListjsonboject  Array的長度: " + cgsTravelImgListjsonbojectArray.size()) ;

                int _count = database.delete(
                        CgsImgListArray.CgsImgListEntry.TABLE_NAME,      // 要刪除資料所在的資料表
                        null,                  // 篩選欄位 (相當於 WHERE 後面的條件)
                        null                   // 篩選欄位的資料 (相當於 WHERE 後面條件的資料)
                );

                Log.d("mmm" , "刪除的資料筆數 :" + _count ) ;

                for (int x = 0 ; x < cgsTravelImgListjsonbojectArray.size() ; x ++ ) {

                    CgsTravelImgListjsonboject c =  cgsTravelImgListjsonbojectArray.get(x);

                    Log.d("abc" , "gettravelImgId -- " + c.gettravelImgId());
                    Log.d("abc" , "gettravelInfoId -- " + c.gettravelInfoId());
                    Log.d("abc" , "getorgFileName -- " + c.getorgFileName());
                    Log.d("abc" , "getsortSeq -- " + c.getsortSeq());
                    Log.d("abc" , "getmapfile -- " + c.getmapfile());

                    ContentValues values = new ContentValues();

                    values.put(CgsImgListArray.CgsImgListEntry.COLUMN_NAME_travelImgId, c.gettravelImgId());
                    values.put(CgsImgListArray.CgsImgListEntry.COLUMN_NAME_travelInfoId, c.gettravelInfoId());
                    values.put(CgsImgListArray.CgsImgListEntry.COLUMN_NAME_orgFileName, c.getorgFileName());
                    values.put(CgsImgListArray.CgsImgListEntry.COLUMN_NAME_sortSeq, c.getsortSeq());
                    values.put(CgsImgListArray.CgsImgListEntry.COLUMN_NAME_file, c.getmapfile());

                    long newRowId = database.insert(CgsImgListArray.CgsImgListEntry.TABLE_NAME, null, values);

                    Log.d("555","oooooooooooooooooooooooo 目前 RowId :" + newRowId) ; // 顯示加入資料的 row id

                }

                Cursor cursorTravelImgList = database.query(
                        CgsImgListArray.CgsImgListEntry.TABLE_NAME,      // 要查詢的資料表
                        null,                      // 要查詢的欄位 (使用 null 表示全部欄位)
                        null,                      // 過濾欄位 (相當於 WHERE 後面的條件)
                        null,                      // 過濾欄位的資料 (相當於 WHERE 後面條件的資料)
                        null,                      // 分組 (相當於 SQL 中 GROUP BY 後面的語法)
                        null,                      // (相當於 SQL 中 HAVING 後面的語法)
                        null                       // 排序 (相當於 ORDER BY 後面的語法)
                );      // 查詢表格資料 (無條件)

                Log.d("123","CgsImgListArray cursor 長度: >>>>> " + cursorTravelImgList.getCount());

                Log.d("123","下面是表格 CgsImgListArray 中的每筆資料內容");

                while (cursorTravelImgList.moveToNext()) {

                    String travelImgId_     = cursorTravelImgList.getString(cursorTravelImgList.getColumnIndexOrThrow(CgsImgListArray.CgsImgListEntry.COLUMN_NAME_travelImgId));
                    String _travelInfoId    = cursorTravelImgList.getString(cursorTravelImgList.getColumnIndexOrThrow(CgsImgListArray.CgsImgListEntry.COLUMN_NAME_travelInfoId));
                    String _orgFileName     = cursorTravelImgList.getString(cursorTravelImgList.getColumnIndexOrThrow(CgsImgListArray.CgsImgListEntry.COLUMN_NAME_orgFileName));
                    String _sortSeq         = cursorTravelImgList.getString(cursorTravelImgList.getColumnIndexOrThrow(CgsImgListArray.CgsImgListEntry.COLUMN_NAME_sortSeq));
                    String _file            = cursorTravelImgList.getString(cursorTravelImgList.getColumnIndexOrThrow(CgsImgListArray.CgsImgListEntry.COLUMN_NAME_file));

                    Log.d("555","~~~~~~~~~~~~~~~~~~  TravelImgList 表格 ~~~~~~~~~~~~~~~~~~~~~~~~");
                    Log.d("555" , "travelImgId  >>> " + travelImgId_ ) ;
                    Log.d("555" , "travelInfoId_ >>> " + _travelInfoId ) ;
                    Log.d("555" , "orgFileName >>> " + _orgFileName ) ;
                    Log.d("555" , "sortSeq >>> " + _sortSeq ) ;
                    Log.d("555" , "file >>> " + _file       ) ;

                }

                // 第三個 json array  - travelList
                // 解開 travelList json array 中的各個欄位
                // travelList - 其中每個 object 的欄位為
                // travelInfoId , name  , nameEn , category  , location , locationEn , description , descriptionEn
                // evaluate , modifyUser , modifyDate (共 11 個)

                JSONObject   travelListjsonboject ;
                int           _travelInfoId , category  ;
                String name , nameEn ,  location , locationEn  , description , descriptionEn , evaluate ,  modifyUser , modifyDate ;
                TravelListjsonboject travelListjsonboject1 ;

                Log.d("555","travelListjsonbojectArray 的長度 :" + travelListjsonarray.length()) ;

                for ( int jj = 0 ; jj < travelListjsonarray.length() ; jj ++ ) {

                    Log.d("555",  " travelListjsonArray  ^^^^^^^^^^^^^^^^^^^^^^^^^^^ ") ;

                    travelListjsonboject = travelListjsonarray.getJSONObject(jj) ;
                    _travelInfoId = travelListjsonboject.getInt("travelInfoId") ;    // travel Information Id
                    Log.d("555" , "travelInfoId : " + _travelInfoId ) ;
                    name = travelListjsonboject.getString("name");                  // name
                    Log.d("555" , "name : " + name ) ;
                    nameEn = travelListjsonboject.getString("nameEn");              // nameEn
                    Log.d("555" , "nameEn(英文名稱) : " + nameEn ) ;
                    category = travelListjsonboject.getInt("category") ;            // category
                    Log.d("555" , "category(分類) : " + category ) ;
                    location = travelListjsonboject.getString("location");
                    Log.d("555" , "location(地點) : " + location ) ;
                    locationEn = travelListjsonboject.getString("locationEn");
                    Log.d("555" , "locationEn : " + locationEn ) ;
                    description = travelListjsonboject.getString("description");
                    Log.d("555","description(描述) :" + description);
                    descriptionEn = travelListjsonboject.getString("descriptionEn");
                    Log.d("555","descriptionEn :" + descriptionEn);
                    evaluate = travelListjsonboject.getString("evaluate");
                    Log.d("555","evaluate :" + evaluate);
                    modifyUser = travelListjsonboject.getString("modifyUser");
                    Log.d("555","modifyUser :" + modifyUser);
                    modifyDate = travelListjsonboject.getString("modifyDate");
                    Log.d("555","modifyDate :" + modifyDate);

                    travelListjsonboject1  = new TravelListjsonboject(
                            _travelInfoId ,
                            category ,
                            name ,
                            nameEn ,
                            location ,
                            locationEn ,
                            description ,
                            descriptionEn ,
                            evaluate ,
                            modifyUser ,
                            modifyDate
                            ) ;

                    travelListjsonbojectListArray.add(travelListjsonboject1) ;    // add an object to array list

                }   //  // end of for loop  - dumping travelListjsonArray's contents


                Log.d("555" , "刪了TravelListArray數目:" ) ;

                for (int nn = 0 ; nn < travelListjsonbojectListArray.size() ; nn ++) {

                    TravelListjsonboject c = travelListjsonbojectListArray.get(nn) ;

                    Log.d("555", "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$") ;
                    Log.d("555","getTravelInfoId -- " + c.getTravelInfoId()) ;
                    Log.d("555","getCategory -- " + c.getCategory()) ;
                    Log.d("555","getName -- " + c.getName()) ;
                    Log.d("555" , "getNameEn -- " + c.getNameEn());
                    Log.d("555","getLocation -- " + c.getLocation()) ;
                    Log.d("555" , "getLocationEn -- " + c.getLocationEn()) ;
                    Log.d("555","getDescription -- " + c.getDescription()) ;
                    Log.d("555" , "getDescriptionEn -- " + c.getDescriptionEn()) ;
                    Log.d("555", "getEvaluate --" + c.getEvaluate()) ;
                    Log.d("555","getModifyUser -- " + c.getModifyUser());
                    Log.d("555","getModifyDate -- " + c.getModifyDate()) ;


                    ContentValues values = new ContentValues();

                    values.put(TravelListArray.TravelListEntry.COLUMN_NAME_travelInfoId, c.getTravelInfoId());
                    // values.put(TravelListArray.TravelListEntry.COLUMN_NAME_category, c.getCategory());
                    //values.put(TravelListArray.TravelListEntry.COLUMN_NAME_name, c.getName());
                    //values.put(TravelListArray.TravelListEntry.COLUMN_NAME_nameEn, c.getNameEn());
                    //values.put(TravelListArray.TravelListEntry.COLUMN_NAME_location, c.getLocation());
                    //values.put(TravelListArray.TravelListEntry.COLUMN_NAME_locationEn, c.getLocationEn());
                    //values.put(TravelListArray.TravelListEntry.COLUMN_NAME_description, c.getDescription());
                    //values.put(TravelListArray.TravelListEntry.COLUMN_NAME_descriptionEn, c.getDescriptionEn());
                    //values.put(TravelListArray.TravelListEntry.COLUMN_NAME_evaluate, c.getEvaluate());
                    //values.put(TravelListArray.TravelListEntry.COLUMN_NAME_modifyUser, c.getModifyUser());
                    // values.put(TravelListArray.TravelListEntry.COLUMN_NAME_modifyDate, c.getModifyDate());

                    Log.d("555",">>>>>>>>>>>>>> 修改日期:" + values.get(TravelListArray.TravelListEntry.COLUMN_NAME_modifyDate));
                    long newRowId = database.insert(TravelListArray.TravelListEntry.TABLE_NAME, null, values);

                    Log.d("555","travelList 目前 RowId :" + newRowId) ; // 顯示加入資料的 row id

                }   // end of for loop  - dumping travelListjsonbojectListArray's contents

                Log.d("abc", "333333333333 travelListjsonbojectListArray  Array的長度: " + travelListjsonbojectListArray.size()) ;


                // 接著,要下載圖檔 , 分別為  cgsTravelList , travelImgList , travelList 中的圖檔
                // 1. cgsTravelList 圖檔下載 - 此為地圖下載

            // parsing json array
                /*
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonobject = jsonArray.getJSONObject(i);

                    int  travelImgId  = jsonobject.getInt("travelImgId");   // travel iamge id
                    int travelInfoId = jsonobject.getInt("travelInfoId");   // travel info id
                    String orgFileName = jsonobject.getString("orgFileName") ;   // orginal file name
                    int sortSeq = jsonobject.getInt("sortSeq");                  // sort sequence
                    String file = jsonobject.getString("file") ;

                    //  dump it all to screen

                    Log.d("ccc" , "----------------------------") ;

                    Log.d("ccc" , "travelImgId :"   +  travelImgId ) ;
                    Log.d("ccc" , "travelInfoId :"  + travelInfoId ) ;
                    Log.d("ccc" , "orgFileName :" + orgFileName) ;
                    Log.d("ccc" , "sortSeq :" + sortSeq ) ;
                    Log.d("ccc" , "file :" + file ) ;


                }

                 */


                input.close();        // close input stream
                conn.disconnect();    // disconnect http connection it !

            }   // status code : 200
            else {

                Log.d("abc", "@@ cgc 連線有誤, 回傳碼 :"  + responseCode);

            }

        }
        catch(Exception e) {

            Log.d(TAG, "Error >> " + e.toString());
            e.printStackTrace();
        }

       // 將旗號設定,為了之後的檢查
        SharedPreferences.Editor editor = sharedPreferenceslogin.edit();  // for login's flag
        editor.putInt(FLAG_KEY, 1);  // set the flag's value to 1
        editor.apply();
        editor.commit() ;

    }

    @Override
    public void finish() {

        // 當 activity 結束時 , 必須將 timer 清空

        super.finish();
        overridePendingTransition(R.anim.activity_bottom_in, R.anim.activity_bottom_out);  // fade in/out  的特效

        // 時鐘 timer 必須銷毀

        Clocktimer.cancel();
        Clocktimer.purge() ;

        // 天氣溫度 timer 必須銷毀
        WeatherTemperatureTimer.cancel();
        WeatherTemperatureTimer.purge() ;

    }

    private void AccountEditListener(View dialogview , AlertDialog dialog)
    {

        // TextInputLayout - account and password
        Log.d("abc" , "AccountEditListener") ;   // 登入進入

        final TextInputLayout account  = (TextInputLayout) dialogview.findViewById(R.id.accountedit);    // account
        final TextInputLayout password = (TextInputLayout) dialogview.findViewById(R.id.pwdedit);        // password
        final Button confirmationbtn   = (Button) dialogview.findViewById(R.id.confirmbtn);              // confrimation button

        // account and password edittext

        EditText accountedit = dialogview.findViewById(R.id.accedittxt) ;
        EditText passwordedit = dialogview.findViewById(R.id.pwdedittxt);

        // 帳號的監聽事件

        account.getEditText().addTextChangedListener(new TextWatcher() {

            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {

                if (text.length() < 5 || text.length() > 5 ) {

                    account.setError("帳號有錯,請重新輸入");
                    account.setErrorEnabled(true);  // error !

                } else {

                    account.setErrorEnabled(false);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                Toast.makeText(MainActivity.this, accountedit.getText().toString()
                        , Toast.LENGTH_SHORT).show();

                acc = accountedit.getText().toString() ;

                Log.d("abc","帳號輸入 : " +  acc );  // check account
            }
        });    //  the end of account.getEditText().addTextChangedListener

        // 密碼的監聽事件

        password.getEditText().addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {

                if (text.length() < 5 || text.length() > 5 ) {

                    account.setError("密碼有錯,請重新輸入");
                    account.setErrorEnabled(true);  // error !

                } else {

                    account.setErrorEnabled(false);

                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                Toast.makeText(MainActivity.this, accountedit.getText().toString()
                        , Toast.LENGTH_SHORT).show();

                pwd = passwordedit.getText().toString() ;

                Log.d("abc","密碼輸入 : " +  pwd );  // check password
            }
        });    //  the end of password.getEditText().addTextChangedListener

        // 確定按鈕

        confirmationbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ( accountedit.getText().toString().equals("")  ||
                        passwordedit.getText().toString().equals("")) {

                    // 錯誤訊息 !　帳密為空
                    Toast.makeText(MainActivity.this, "有空", Toast.LENGTH_SHORT).show();

                }
                else {

                    Log.d("abc", "輸入的帳號: " + acc) ;
                    Log.d("abc", "輸入的密碼:" +  pwd) ;

                    SaveAccount(accountedit.getText().toString());        // 儲存帳號
                    SavePassword(passwordedit.getText().toString()) ;     // 儲存密碼

                    // 接著,要開始傳遞 account 及 password 並且取回一個 token

                    // dialog.dismiss();    // 先關閉輸入對話框

                    startHttpRequestThread();   // start a thread to post account and password

                }

            }  // end of onClick
        });

    }   // end of account

    private void AccPwdLoginFailureDialog()
    {

        // 帳密登入錯誤

        Button confirmation ;

        View dialogView = LayoutInflater.
                from(MainActivity.this).
                inflate(R.layout.loginfailuredialog, null);

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

        //set our custom alert dialog to tha alertdialog builder
        alert.setView(dialogView);   // 設定dialog的 view

        final AlertDialog dialog = alert.create();

        // 下面是事件監聽

        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  // 避免邊框空白 消除

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);
        dialog.getWindow().setWindowAnimations(R.style.mystyle); // 添加動畫
        confirmation = dialogView.findViewById(R.id.confirmbtn);

        confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SetWeatherForecastingFlag(false);  // 設置天氣預報無法點擊

                dialog.dismiss();  // close the dialog and launch login dialog again

                ShowAccPwdLoginDialog();  // 重新輸入
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){

            @Override
            public void run() {

                // 過兩秒後要做的事情
                dialog.dismiss();  // close the dialog and launch login dialog again

                ShowAccPwdLoginDialog();   // 重新輸入


            }}, 2000);

    }  // end of AccPwdLoginFailureDialog

    private void  ConnectionFailureListener(View dialogview , AlertDialog dialog)
    {

        // TextInputLayout - account and password

        // final TextInputLayout account  = (TextInputLayout) dialogview.findViewById(R.id.accountedit);    // account
        //  final TextInputLayout password = (TextInputLayout) dialogview.findViewById(R.id.pwdedit);        // password
        final Button confirmationbtn   = (Button) dialogview.findViewById(R.id.confirmbtn);              // confrimation button

        // account and password edittext
        //  EditText accountedit = dialogview.findViewById(R.id.accedittxt) ;
        // EditText passwordedit = dialogview.findViewById(R.id.pwdedittxt);

        // 帳號的監聽事件

        /*

        account.getEditText().addTextChangedListener(new TextWatcher() {

            // ...
            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {

                if (text.length() < 5 || text.length() > 5 ) {

                    account.setError("帳號有錯,請重新輸入");
                    account.setErrorEnabled(true);  // error !

                } else {

                    account.setErrorEnabled(false);

                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                Toast.makeText(MainActivity.this, accountedit.getText().toString()
                        , Toast.LENGTH_SHORT).show();

                acc = accountedit.getText().toString() ;

                Log.d(TAG,"帳號輸入 : " +  acc );  // check account
            }
        });    //  the end of account.getEditText().addTextChangedListener

        // 密碼的監聽事件

        password.getEditText().addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence text, int start, int count, int after) {

                if (text.length() < 5 || text.length() > 5 ) {

                    account.setError("密碼有錯,請重新輸入");
                    account.setErrorEnabled(true);  // error !

                } else {

                    account.setErrorEnabled(false);

                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                Toast.makeText(MainActivity.this, accountedit.getText().toString()
                        , Toast.LENGTH_SHORT).show();

                pwd = passwordedit.getText().toString() ;

                Log.d(TAG,"密碼輸入 : " +  pwd );  // check password
            }
        });    //  the end of password.getEditText().addTextChangedListener

        */

        // 確定按鈕
        /*
        confirmationbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    dialog.dismiss();      // 關閉帳密對話框 並且要開始將帳密傳上去檢查

            }  // end of onClick
        });

         */

    }   // end of account

    private void CameraDialog()
    {

        // Weather Report

        TextView date , DayofWeek ;

        View dialogView = LayoutInflater.
                from(MainActivity.this).
                inflate(R.layout.weatherforcastdialog, null);


        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

        alert.setCancelable(false) ;    // 點擊外部不可關閉對話框

        //set our custom alert dialog to tha alertdialog builder
        alert.setView(dialogView);   // 設定dialog的 view

        final AlertDialog dialog = alert.create();

        Button homebackbtn ;

        homebackbtn = dialogView.findViewById(R.id.backhomebtn) ;

        homebackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(MainActivity.this, "回到主畫面", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(v.getContext() , MainActivity.class) ;

                dialog.dismiss();   // close the dialog
                overridePendingTransition(R.anim.activity_bottom_in, R.anim.activity_bottom_out);  // fade in/out  的特效
                v.getContext().startActivity(intent);

            }
        });

        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  // 避免邊框空白 消除

    }  // end of CameraDialog

    private void checkConnection() {

        // initialize intent filter
        IntentFilter intentFilter = new IntentFilter();

        RelativeLayout relativeLayout ;

        // add action
        intentFilter.addAction("android.new.conn.CONNECTIVITY_CHANGE");

        // register receiver
        registerReceiver(new ConnectionReciver(), intentFilter);

        // Initialize listener
        ConnectionReciver.Listener = this;

        // Initialize connectivity manager
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        // Initialize network info
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        // get connection status
        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();

        // display snack bar
        // Toast.makeText(this, "Connection :" + isConnected, Toast.LENGTH_SHORT).show();

        showSnackBar(isConnected);  // check internet is available

    }

    @Override
    public void onNetworkChange(boolean isConnected) {


        showSnackBar(isConnected);
        // a snackbar to show internet connection status

    }

    @Override
    protected void onResume() {
        super.onResume();

        // call method
        checkConnection();

        Log.d("qaz", "回到主畫面") ;


    }

    @Override
    protected void onPause() {
        super.onPause();

        // call method
        checkConnection();

        SharedPreferences.Editor editor = sharedPreferenceslogin.edit();
        editor.putInt(FIRST_RUN_KEY, 1);   // 設旗號為 1
        editor.apply();
        Log.d("qsx","call onPause") ;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // return gestureDetector.onTouchEvent(event);   // 用來判斷手勢的觸摸動作
        return true ;

    }

    // Save/Get account

    private void SaveAccount(String account ){

        this.acc  = account ;
        Log.d("ContentValues","儲存的帳號: " + this.acc) ;
    }
    private String GetAccount() {

        Log.d("ContentValues","取出的帳號: " + this.acc) ;
        return this.acc ;
    }

    // Save/Get password

    private void SavePassword(String password ){
        this.pwd  = password ;
        Log.d("ContentValues","儲存的密碼: " + this.pwd) ;

    }
    private String GetPassword() {
        Log.d("ContentValues","取出的密碼: " + this.pwd) ;
        return this.pwd ;
    }

    public void showSnackBar (Boolean isconnection) {


        final Snackbar snackBar = Snackbar.make(findViewById(android.R.id.content), "網路連接成功", Snackbar.LENGTH_LONG);
        final Snackbar snackBar1 = Snackbar.make(findViewById(android.R.id.content), "網路連接失敗", Snackbar.LENGTH_LONG);

        View snackBarView = snackBar.getView() ;    // 首先取出 snackbar 的 view
        View snackBarView1 = snackBar1.getView() ;   // snackbar 的 view


        if(snackBarView!=null) {
            snackBarView.setBackgroundColor(Color.BLUE);// 修改view的背景色
        }

        if (snackBarView1 != null) {
            snackBarView1.setBackgroundColor(Color.RED);// 修改 view的背景色

        }

        ViewGroup.LayoutParams layoutParams = snackBarView.getLayoutParams();
        // 重新设置属性参数
        layoutParams.width = 400 ;
        layoutParams.height = 50 ;
        FrameLayout.LayoutParams cl = new FrameLayout.LayoutParams(layoutParams.width,layoutParams.height);
        // 位置在下居中

        cl.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        snackBarView.setLayoutParams(cl);
        snackBarView1.setLayoutParams(cl);

        snackBar.setActionTextColor(Color.WHITE) ;    // 白色字顯示
        snackBar.setDuration(1500) ;                  // 設定顯示時間 , 1.5 秒內未按 "確定" 就會自動關閉 !

        snackBar1.setActionTextColor(Color.WHITE) ;    // 白色字顯示
        snackBar1.setDuration(1500) ;                  // 設定顯示時間 , 1.5 秒內未按 "確定" 就會自動關閉 !


        if (isconnection == true ) {

            snackBar.setAction("確定", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Call your action method here
                    snackBar.dismiss();
                }
            });

            snackBar.setActionTextColor(Color.WHITE) ;    // 白色字顯示

            snackBar.setDuration(1500) ;                  // 設定顯示時間 , 1.5 秒內未按 "確定" 就會自動關閉 !
            snackBar.show();

        }
        else {

            // 網路連接失敗

            snackBar1.setAction("確定", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Call your action method here
                    snackBar1.dismiss();
                }
            });

            snackBar1.setActionTextColor(Color.WHITE) ;    // 白色字顯示
            snackBar1.setDuration(1500) ;                  // 設定顯示時間 , 1.5 秒內未按 "確定" 就會自動關閉 !
            snackBar1.show();
        }
    }   // end of showsnackbar

    private void startHttpRequestThread() {

        // 這個是用來處理 http connection - url account and password (登入處理)

        Log.d("kkk","登入:startHttpRequestThread() ");

        Thread HttpRequestThread ;

        HttpRequestThread = new Thread(new Runnable() {
            @Override
            public void run() {

                doHttpRequest();   // 登入

            }
        });

        HttpRequestThread.start();   // start the thread to get data

    }   // end of startHttpRequestThread  (登入用的)

    private void startImageDownloadHttpRequestThread(String url) {

        // 這個是用來處理 http connection - download (圖檔下載處理)

        Log.d("bnm","登入:startImageDownloadHttpRequestThread() ");

        Thread HttpRequestThread ;

        HttpRequestThread = new Thread(new Runnable() {
            @Override
            public void run() {

               // doHttpRequest();   // 登入
                downloadImage(url) ;   // 下載圖檔

            }
        });

        HttpRequestThread.start();   // start the thread to get data

    }   // end of startImageDownloadHttpRequestThread  (圖檔下載用)

    private void startHttpCGCInformationRequestThread() {

        // 這個是用來取資料

        Log.d("vbn","開始取出 interbox 資料 startHttpCGCInformationRequestThread() ");

        Thread HttpRequestThread ;

        HttpRequestThread = new Thread(new Runnable() {
            @Override
            public void run() {

                doHttpGetCGSInformationRequest();   // 取出 interbox cgs 資料
            }
        });

        HttpRequestThread.start();   // 啟動這個 thread 處理  cgc inforamtion 的資料提取


    }   // end of startHttpCGCInformationRequestThread  (取資料)



    ///////////////////////////// 一周天氣預報 (自己的)  /////////////////////////////////////////////
    private void startHttpCGCWeeklyWeatherForecastRequestThread(int cgsId ) {

        // 這個是用來取資料

        Log.d("zxc","一周天氣資料 startHttpCGCWeeklyWeatherForecastRequestThread() ");

        Thread HttpRequestThread ;

        HttpRequestThread = new Thread(new Runnable() {
            @Override
            public void run() {

                doHttpGetCGSWeeklyWeatherForecastInformationRequest(cgsId);   // 取出 一周天氣資料 via cgsId
            }
        });

        HttpRequestThread.start();   // 啟動這個 thread 處理 天氣資料資料提取


    }   // end of startHttpCGCWeeklyWeatherForecastRequestThread  (取資料)

    // 取出一周資料 (由 Open data )
    private void startWeeklyWeatherForecastRequestThread(int cgsId ) {

        // 這個是用來取資料

        Log.d("zxc","一周天氣資料 startWeeklyWeatherForecastRequestThread() ");

        Thread HttpRequestThread ;

        HttpRequestThread = new Thread(new Runnable() {
            @Override
            public void run() {

                doHttpGetCGSWeeklyWeatherForecastInformationRequest(cgsId);   // 取出 一周天氣資料 via cgsId
            }
        });

        HttpRequestThread.start();   // 啟動這個 thread 處理 天氣資料資料提取




    }   // end of startHttpCGCWeeklyWeatherForecastRequestThread  (取資料)


    ///////////////////////////// 取出一周天氣  (自己的) ////////////////////////////
    private void doHttpGetCGSWeeklyWeatherForecastInformationRequest(int cgsId) {

        // 取得 一周天氣的相關資訊

        try {

            Log.d("zxc", "doHttpGetCGSWeeklyWeatherForecastInformationRequest()");   // dump cable data log to display

            URL url = new URL("http://192.168.0.135/cgc/api/cwaApi/" + cgsId   );  // get weather information via cgsId (1 : 嘉義)

            Log.d("zxc", " >>>>> ((((((***** 天氣預報 *****))))))) 取出之前登入成功的 Token: " + GetToken());

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "Bearer " + GetToken());   //
            conn.setRequestMethod("GET");   // get the user data
            conn.setRequestProperty("Connection", "keep-Alive");
            // conn.setRequestProperty("Content-Type", "application/json");

            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json ; ; charset=UTF-8");

            conn.setDoOutput(false);
            conn.setDoInput(true);     // Notice ! it must be set : true . input file stream
            conn.setUseCaches(false);
            conn.connect();            // connect it !

            int responseCode = conn.getResponseCode();

            Log.d("bnm", "Response Code - inter box 天氣預報  >> " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {

                // http code : 200 , http 請求成功

                Log.d("zxc", "進入- 天氣預報資料連線 ");

                InputStream input = conn.getInputStream();  //
                InputStreamReader reader = new InputStreamReader(input, "UTF-8");
                StringBuilder sb = new StringBuilder();

                int ss;
                JSONObject ResultForWeatherForecast ;  // result of weatherforecast

                ///////////  取出 json 字串 - 基本上有一周天氣預報
                while ((ss = reader.read()) != -1) {

                    sb.append((char) ss);  // 將讀到內容附加到字串中

                    Log.d("zxc", sb.toString());

                }    // get response - json array - end of while

                Log.d("zxc", "+++++++++++  最終的氣象資料 :" + sb.toString()) ;
                // 若天氣資料有效 則進行分析

                if (sb.toString() != null ) {  /////////////////   有效的天氣資料

                    JSONObject jsonObj = new JSONObject(sb.toString());  // convert plain string to json object

                    Log.d("zxc", "天氣預報 Json Object >>>>>>>>>>>>>>>>> " + jsonObj.toString());
                    StatusForWeatherForecsst = jsonObj.getInt("status");         // status code
                    Log.d("zxc","Status        >>>> " + GetStatusForWeatherForecsst());
                    SaveStatusForWeatherForecsst(StatusForWeatherForecsst);
                    ResultForWeatherForecast = jsonObj.getJSONObject("result");  // body of json
                    Log.d("zxc","result :" + ResultForWeatherForecast) ;
                    AuthorizationForWeatherForecast = ResultForWeatherForecast.getString("Authorization") ;   // get authorization
                    SaveAuthorizationForWeatherForecast(AuthorizationForWeatherForecast);
                    Log.d("zxc","Authorization >>>> " + GetAuthorizationForWeatherForecast());

                    locationIdForWeatherForecast = ResultForWeatherForecast.getString("locationId");     // location Id
                    SavelocationIdForWeatherForecast(locationIdForWeatherForecast);
                    Log.d("zxc","location id   >>>> " + GetlocationIdForWeatherForecast());

                    urlForWeatherForecast = ResultForWeatherForecast.getString("url");                  // url address
                    SaveurlForWeatherForecast(urlForWeatherForecast);
                    Log.d("zxc","url           >>>> " + GeturlForWeatherForecast());

                    // The above fields are available.
                    //////////// 設定天氣下載旗號完成   ////////////////////////////

                    DownloadingFinish = true ;
                    Log.d("zxc", "DownloadingFinish 被設定為 : " + DownloadingFinish) ;
                    checkWeatherForecastDownloadFinish();   // 檢查是否資料下載完成

                }
                else {
                    Log.d("zxc", "無效的天氣資料") ;

                }

            }
            else {

                Log.d("abc","錯誤 http code : " + responseCode ) ;
            }
        }
        catch (Exception e) {
            Log.d("bnm" , "天氣資料錯誤 :" + e.getMessage().toString()) ;
        }

        Log.d("zxc", "---------------------------------------------------------------");
        Log.d("zxc", "------" + GetAuthorizationForWeatherForecast());
        Log.d("zxc", "------" + GeturlForWeatherForecast());
        Log.d("zxc", "------" + GetlocationIdForWeatherForecast());

        String input = GeturlForWeatherForecast();
        String[] parts = input.split("/");

         // 取出 / 後的部分 (最後一個元素) - 最後一段
        String result = parts[parts.length - 1];

        Log.d("zxc", "0000000000%%%%%%%%%%% " + result.toString());


    }  // end of doHttpGetCGSWeeklyWeatherForecastInformationRequest


                private void startHttpSIPInformationRequestThread() {

        // 這個是用來取資料

        Log.d("vbn","開始取出 SIP 資料 startHttpSIPInformationRequestThread() ");

        Thread HttpRequestThread ;

        HttpRequestThread = new Thread(new Runnable() {
            @Override
            public void run() {

                doHttpGetSIPInformationRequest();   // 取出 sip 資料
            }
        });

        HttpRequestThread.start();   // 啟動這個 thread 處理  cgc inforamtion 的資料提取


    }   // end of startHttpCGCInformationRequestThread  (取資料)

    // 天氣預報
    private void SaveStatusForWeatherForecsst(int status){
        this.StatusForWeatherForecsst = status ;
    }
    private int GetStatusForWeatherForecsst() {
        return this.StatusForWeatherForecsst ;
    }
    private void SaveAuthorizationForWeatherForecast(String Authorization ) {
        this.AuthorizationForWeatherForecast = Authorization ;
    }
    private String GetAuthorizationForWeatherForecast() {
        return this.AuthorizationForWeatherForecast ;
    }
    private void SavelocationIdForWeatherForecast(String locationId) {
        this.locationIdForWeatherForecast = locationId ;
    }
    private String GetlocationIdForWeatherForecast() {
        return this.locationIdForWeatherForecast ;
    }
    private void SaveurlForWeatherForecast(String url) {
        this.urlForWeatherForecast = url ;
    }
    private String GeturlForWeatherForecast() {
        return this.urlForWeatherForecast;
    }


    private String getJsonContent() throws JSONException {

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("userId",   "admin" /* GetAccount().toString() */);     //  username  : admin
        jsonObject.put("password", "adminP" /*GetPassword().toString()*/ );    //  password  : adminP

        return jsonObject.toString();

    }

    private void doHttpRequest() {
        // 登入檢查
        try {

            // String ResToken ;  // declare a token

            Log.d("kkk","mmmmmmmmmm");
            // URL url = new URL("http://192.168.100.201/cgc/api/login");  // login url for cgc connection check

            URL url = new URL("http://192.168.0.135/cgc/api/login");  // this is a new login url for cgc connection check

            Log.d("kkk","mmmmmmmmm1");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");   // post the user data
            conn.setRequestProperty("Connection","keep-Alive");
            conn.setRequestProperty("Content-Type", "application/json");
            Log.d("kkk","mmmmmmmmm2");

            // conn.setRequestProperty("Content-Type","application/json; charset=UTF-8");
            // conn.setRequestProperty("Accept", "application/json");

            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.connect();

            String json = getJsonContent(); // pass username and password in Json object

            Log.d("kkk","mmmmmmmmmm");

            Log.d("kkk", "Json String <<<<<>>>>>>> " + json.toString());

            OutputStream os = conn.getOutputStream();
            // UTF_8 format
            os.write(json.getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();

            Log.d("abc", "Response Code <<>> " + responseCode);

            // Toast.makeText(this, "Response Code:" + Integer.toString(responseCode), Toast.LENGTH_SHORT).show();
            // android.util.Log.e("tag", "responseCode = " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK)
            {

                InputStream input = conn.getInputStream();
                StringBuilder sb = new StringBuilder();

                int ss;

                while ((ss = input.read()) != -1) {
                    sb.append((char) ss);

                }  // get input stream

                JSONObject jsonObj = new JSONObject(sb.toString());  // Convert string to json object type
                Log.d("kkk", "Json Object >> " + jsonObj.toString());

                String ResToken ;  // declare a token
                ResToken = jsonObj.getString("result");          // Get the token if status code is 0
                Log.d("kkk","ResToken ----->　" + ResToken);

                int Authorizationstatus ;
                Authorizationstatus = jsonObj.getInt("status");    // Get the return status
                SetAuthorization(Authorizationstatus);                   // set authorization is available

                SetToken(ResToken);
                modelLayer.setToken(ResToken);

                Log.d("kkk", "<<<<<<<<<< 登入成功回傳來的資料 >>>>>>>>>>> ");
                Log.d("kkk", "Status (狀態) >> "  + Authorizationstatus);
                Log.d("kkk", "Token  (令牌) >> "   + ResToken);
                Log.d("kkk", "Code   (返回碼)>> "    + responseCode);
                Log.d("kkk", "回傳 Json String >> " + sb.toString());
                Log.d("kkk", "Token 的長度: " + ResToken.length()) ;

                if ( !ResToken.equals("null") && ResToken.length() != 0 ) {
                    //  登入是合法的有效的 !
                    SetToken(ResToken);  // 若 token 是正確非空 , 則存起來
                    modelLayer.setToken(ResToken);

                    String token = GetToken();

                    Log.d("kkk", "取出來的 token >>>>> " + token) ;


                    // 先清除外部記憶體中的檔案
                    clearExternalStorageDirectory();  // clear all files in external storage
                    // 登入成功後, 要取出資料 !

                    // Log.d("abc", "目前的 loginfirsttime :" + loginfirsttime) ;
                    // 用來下載資料

                    sharedPreferenceslogin = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                    isFirstTimeLogin = sharedPreferenceslogin.getInt(FLAG_KEY, 0);  // default's value : 0

                    if (isFirstTimeLogin == 0 ) {

                        Log.d("qsx" , "這裡是app 第一次進入,且開始下載資料") ;
                        Log.d("qsx", "執行 startHttpCGCInformationRequestThread " ) ;

                        startHttpCGCInformationRequestThread(); ///////  開始下載 cgs 資料  並將資料存到 external storage 中

                    }
                    else {
                        Log.d("qsx","這裡是由某個activity 返回") ;

                    }

                    if ( loginfirsttime == false ) {


                        // 首先要取出 cgsId from cgs ( 1: 嘉義)

                        db = myDatabaseHelper.getReadableDatabase();  // open datbase

                        ////////// Again , 要取出  cgs1 資料表中的  cgsId

                        Cursor cursor_weeklyweather = db.rawQuery("SELECT * FROM cgs1", null);  // get cursor from cgs1

                         //
                         if ( cursor_weeklyweather != null ) {

                             cursor_weeklyweather.moveToFirst();   // move cursor to first

                             if (cursor_weeklyweather.moveToFirst()) {

                                 // 取出來欄位驗證一下

                                 do {

                                     try {

                                         cgsId_forWeeklyWeatherForecast = cursor_weeklyweather.getInt(cursor_weeklyweather.getColumnIndexOrThrow("cgsId"));
                                         SaveWeeklyWeatherForecst_cgsId(cgsId_forWeeklyWeatherForecast) ;   // save the cgsId for weekly weather forecast

                                         Log.d("zxc", "cgs 中的 cgsId }}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}} ");
                                         Log.d("zxc", "cgsId : " + cgsId_forWeeklyWeatherForecast );

                                     }
                                     catch (Exception e) {
                                         Log.d("bnm", "錯誤 :" + e.getMessage().toString() ) ;
                                     }

                                 } while (cursor_weeklyweather.moveToNext());

                                 Log.d("zxc", "執行 startHttpCGCWeeklyWeatherForecastRequestThread " ) ;

                                 ////////// 這裡是要先取出相關關天氣資料 (自己的) ///////////////////////////////
                                 startHttpCGCWeeklyWeatherForecastRequestThread(GetWeeklyWeatherForecst_cgsId());  // 取出相關城市的一周天氣預報資料

                                 ////////// 接著,要去 opendata 取出資料  ////////////////////////////////////

                                 Log.d("zxc" , "000000000000000000000000");
                                 // startGetOpenDataWeeklyWeatherForecastRequestThread();


                             }  // cursor end

                         }     // check cursor is not null
                         else {

                             Log.d("zxc", "cgs 表格游標有誤") ;
                         }

                        // loginfirsttime = true ;  // 設定為已經進入過 app
                    }
                    else ;

                     // startHttpCGCInformationRequestThread();   // 取出 cgc information 資料

                    // filedownloadcflag = true ;  // 關閉下載對話框
                    // startHttpSIPInformationRequestThread();   // 取出 sip 資料

                    // GetCGSInformation("https://192.168.0.135/cgc/api/inter_cgs");   // 取出 cgs information

                }
                else {

                    SetToken(null);
                    Looper.prepare();
                    AccPwdLoginFailureDialog();   // 登入失敗
                    Looper.loop();

                }
                input.close();

            }   // connection is successful ! 200


            else {

                Log.d("kkk","Code >> " + responseCode) ;

            }

            conn.disconnect();   // disconnect it !

            // doHttpGetCGSInformationRequest();

        } catch (Exception e) {

            Log.d("kkk", "Error >> " + e.toString());
            e.printStackTrace();
        }

    }   // end of doHttpRequest

    // Save cgsId for weekly weather forecast

    private void  SaveWeeklyWeatherForecst_cgsId(int cgsIdforweelyweatherforecast) {

        this.cgsId_forWeeklyWeatherForecast = cgsIdforweelyweatherforecast ;

    }

    private int GetWeeklyWeatherForecst_cgsId() {

        return this.cgsId_forWeeklyWeatherForecast ;
    }


    // Set status code and token
    private void SetAuthorization (int status ) {
        this.status = status ;

    }
    private void SetToken (String token ) {

        Log.d("abc", "要儲存的 token --> " + token);
        this.Token = token ;
        Log.d("abc", "已儲存的 token --> " + this.Token);

    }
    private String GetToken() {

        Log.d("xyz","GetToken()" );


        Log.d("xyz","可取出的 token --> " + this.Token);

        return this.Token;
    }

    // Get status and token
    private int GetAuthorization ( ) {

        return this.status ;
    }


    private void  ConnectionTest(String str ) {

        this.mContext = getApplicationContext();

        boolean isavailable = isAvailableOfNetworking(mContext);
        Log.d("abc","連線測試");

        if (isavailable != true) {
            Log.d("ccc", "Networking is not available");

        }
        else {

            Log.d("ccc", "--- Networking available");

            // launch a thread and get those data

            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {

                        String strUrl = str;  // url address

                        URL url = new URL(strUrl);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");   //  GET
                        connection.connect();                 //  connect to web page

                        int responseCode = connection.getResponseCode();

                        Log.d("ppp", "--- connection ");
                        SetStatus(responseCode);

                        if (GetStatus() == 200) {  // 連線成功 !

                            Log.d("abc", "Code >>" + " 正確 : " + Integer.toString(responseCode));


                        } else {    // 登入失敗 !

                            Log.d("abc", "Code >>" + " 錯誤 : " + Integer.toString(responseCode));

                        }

                        if (responseCode == HttpURLConnection.HTTP_OK) {

                            //  request successfully !
                            //  登入成功後取出 json 字串 !

                            InputStream inputStream = connection.getInputStream(); //  get stream data
                            JSONObject json = streamToJson(inputStream);           //  JSON String
                            Log.d("abc", "連線測試回傳的json 字串:  " + json.toString());                     //  JSON response string

                            JSONObject jsonObject = new JSONObject(json.toString());
                            int status = jsonObject.getInt("status");
                            String message = jsonObject.getString("message");
                            String result = jsonObject.getString("result");
                            boolean ok = jsonObject.getBoolean("ok");

                            connectionflag = true;

                            Log.d("kkk", "--- status :" + status);
                            Log.d("kkk", "--- message :" + message);
                            Log.d("kkk", "--- result  :" + result);
                            Log.d("kkk", "--- ok :" + ok);

                            // Log.d("ccc", "--- connectionflag :" + connectionflag);

                            Looper.prepare();

                            SaveStatus(status);
                            SaveResult(result);
                            SaveOk(ok);
                            // json array
                            // 正確的 json string - {"status":0,"message":null,"result":"OK","ok":true}

                        }   // end of http_ok

                        connection.disconnect();   // 連線斷掉
                        Log.d("kkk", "--- connectionflag :" + connectionflag);
                        SaveConnectionStatus(connectionflag);

                        // 接著 , 要做登入動作
                        if (GetConnectionStatus() == true) {

                            // 這要判斷是否為第一次登入 ; 若是 , 登入對話框必須出現 ; 否則不能出現
                            // 判斷的依據是用 bundle的內容

                            Bundle bundle = getIntent().getExtras();

                            if (bundle != null) {
                                int Loginflag = bundle.getInt("loginflag");
                                SaveLoginflag(Loginflag);
                            }
                            else {}   //  null bundle

                            startHttpRequestThread(); //

                        }
                        else  {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ConnectionFailureDialog();

                                }
                            });    // ui thread to show error dialog

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }).start();    // start the thread

        }   // end of else


    }   // end of ConnectionTest

    private void  GetCGSInformation(String str ) {

        // 取得 cgs 相關資訊

        this.mContext = getApplicationContext();

        boolean isavailable = isAvailableOfNetworking(mContext);

        if (isavailable != true) {
            Log.d("abc", "網路無效");

        }
        else {

            Log.d("abc", " VVVV　Networking available");

            // launch a thread and get those data

            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {

                        String strUrl = str;  // url address (cgs / cgc inter box information )

                        URL url = new URL(strUrl);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");   //  GET
                        connection.connect();                 //  connect to web page

                        int responseCode = connection.getResponseCode();

                        Log.d("abc", "--- connection ");
                        SetStatus(responseCode);

                        if (GetStatus() == 200) {  // 連線成功 !

                            Log.d("123", "Code >>" + " cgs 連線 正確 : " + Integer.toString(responseCode));


                        } else {    // 登入失敗 !

                            Log.d("123", "Code >>" + "  cgs 連線 錯誤 : " + Integer.toString(responseCode));

                        }

                        if (responseCode == HttpURLConnection.HTTP_OK) {

                            //  request successfully !
                            //  登入成功後取出 json 字串 !

                            InputStream inputStream = connection.getInputStream(); //  get stream data
                            JSONObject json = streamToJson(inputStream);           //  JSON String
                            Log.d("123", "CGC的json 字串" + json.toString());                     //  JSON response string

                            /*
                            JSONObject jsonObject = new JSONObject(json.toString());

                            int status = jsonObject.getInt("status");
                            String message = jsonObject.getString("message");
                            String result = jsonObject.getString("result");
                            boolean ok = jsonObject.getBoolean("ok");

                             */

                            connectionflag = true;
                            /*
                            Log.d("aaa", "--- status :" + status);
                            Log.d("aaa", "--- message :" + message);
                            Log.d("aaa", "--- result  :" + result);
                            Log.d("aaa", "--- ok :" + ok);

                             */

                            // Log.d("ccc", "--- connectionflag :" + connectionflag);

                            Looper.prepare();


                            // json array
                            // 正確的 json string - {"status":0,"message":null,"result":"OK","ok":true}

                            // end of http_ok

                            connection.disconnect();   // 連線斷掉
                            Log.d("123", "--- connectionflag :" + connectionflag);
                        }
                        else  {


                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }).start();    // start the thread

        }   // end of else

    }

    public void SaveConnectionStatus(boolean flag) {

        this.connectionflag = flag ;

        Log.d("abc" , "SaveConnectionStatus (目前儲存的狀態) : " + this.connectionflag) ;

    }
    public boolean GetConnectionStatus() {

        Log.d("abc" , "GetConnectionStatus :" + this.connectionflag);

        return this.connectionflag ;


    }


    public void SaveStatus(int status) {

        this.Status= status ;
    }

    public int FetchStatus() {
        Log.d("ccc" , ">>>> status :" + this.Status) ;
        return this.Status ;
    }
    public void SaveMessage(String  message) {

        this.Message = message ;

    }
    public String FecthMessage() {
        Log.d("ccc" , ">>>> message :" + this.Message) ;
        return this.Message ;
    }
    public String FetchResult() {
        Log.d("ccc" , ">>>> result :" + this.Result) ;
        return this.Result ;
    }

    public boolean FetchOk() {
        Log.d("ccc" , ">>>> ok :" + this.Ok) ;

        return this.Ok ;
    }

    public void  SaveResult(String result) {

        this.Result = result ;

    }
    public String GetResult() {
        return this.Result ;
    }

    public void SaveOk(boolean ok) {

        this.Ok = ok ;


    }

    public void SetStatus(int statusCode) {

        this.StatusCode = statusCode ;
    }
    public int  GetStatus() {

        return this.StatusCode  ;
    }

    private JSONObject streamToJson(InputStream inputStream) throws Exception {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        String temp = "";
        StringBuilder stringBuilder = new StringBuilder();

        while ((temp = bufferedReader.readLine()) != null) {
            stringBuilder.append(temp);
        }
        JSONObject json = new JSONObject(stringBuilder.toString().trim());
        return json;
    }


    private boolean isAvailableOfNetworking(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (null != activeNetwork) {
            // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                return true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to mobile data
                return true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_ETHERNET) {
                // connected to ethernet
                return true;
            }
        } else {
            // not connected to the internet
        }
        return false;
    }

    private void ExitAppDialog()
    {

        // exit the app

        Log.d("qsx", "ExitAppDialog()") ;

        Button yes, no ;

        View dialogView = LayoutInflater.
                from(MainActivity.this).
                inflate(R.layout.exitappdialog, null);   // png files downloading dialog

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

        // set our custom alert dialog to tha alertdialog builder
        alert.setView(dialogView);   // 設定dialog的 view

        final AlertDialog dialog = alert.create();

        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  // 避免邊框空白 消除

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);
        dialog.getWindow().setWindowAnimations(R.style.mystyle);    // 添加動畫

        yes = dialogView.findViewById(R.id.yesbtn) ;   // exit app
        no  = dialogView.findViewById(R.id.nobtn)  ;   // stay here

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 重置 flag 為 0 - 因為正常離開就要 "清零" 以便之後 app 重新登入之用
                sharedPreferenceslogin = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                isFirstTimeLogin = sharedPreferenceslogin.getInt(FLAG_KEY, 0);          // default's value : 0
                SharedPreferences.Editor logineditor = sharedPreferenceslogin.edit();

                logineditor.putInt(FLAG_KEY, 0);  // 將登入旗號還原為 0
                logineditor.apply();
                logineditor.commit();

                isFirstTimeLogin = sharedPreferenceslogin.getInt(FLAG_KEY, 0);          // default's value : 0

                Log.d("qsx" , " 離開app  " + "isFirstTimeLogin : " + isFirstTimeLogin) ;  // dump

                ///////////////////// 語言設定之用  //////////////////////////////////////

                MultiLanguagepref  = getSharedPreferences(LANGPREFS_NAME , MODE_PRIVATE) ;   // 多語設定
                MultiLangSetting = MultiLanguagepref.getInt(LANGFLAG_KEY, 0);             //  default's value : 0 檢查目前語言設定之用
                Log.d("tgb","目前語言設定:" + MultiLangSetting);
                SharedPreferences.Editor MultiLangeditor = MultiLanguagepref.edit();

                MultiLangeditor.putInt(LANGFLAG_KEY,0);      // 將語言設定還原到預設 : 中文
                MultiLangeditor.apply();
                MultiLangeditor.commit();

                MultiLangSetting = MultiLanguagepref.getInt(LANGPREFS_NAME,0) ;     // 取出語言設定值

                Log.d("tgb","語言設定已還原 :"  + MultiLangSetting);

                dialog.dismiss();          // close the dialog

                finishAndRemoveTask() ;    // exit app normally

            }
        });   // exit app button

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();   // just close the dialog ( make it disappear )
            }
        });  // return the main screen

        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){

            @Override
            public void run() {

                //  過兩秒後要做的事情
                //  dialog.dismiss();          // close the dialog and launch login dialog again
                //  finishAndRemoveTask() ;    // exit app normally

            }}, 2000);

    }  // end of ExitAppDialog

    private void DownloadingPngFilesDialog()
    {

        // downloading png files

        Log.d("444", "DownloadingPngFilesDialog()") ;

        Button yes, no ;

        View dialogView = LayoutInflater.
                from(MainActivity.this).
                inflate(R.layout.downloadingpngfilesdialog, null);   // png files downloading dialog

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

        //set our custom alert dialog to tha alertdialog builder
        alert.setView(dialogView);   // 設定dialog的 view

        final AlertDialog dialog = alert.create();

        AlertDialog tempdialog = dialog ;

        dialog.setCanceledOnTouchOutside(false); // 禁止點擊外部關閉 (等時間到才關閉)

        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  // 避免邊框空白 消除

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);
        dialog.getWindow().setWindowAnimations(R.style.mystyle);    // 添加動畫

        // yes = dialogView.findViewById(R.id.yesbtn) ;   // exit app
        // no  = dialogView.findViewById(R.id.nobtn)  ;   // stay here
        /*
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();          // close the dialog and launch login dialog again
                finishAndRemoveTask() ;    // exit app normally


            }
        });   // exit app button

         */
        /*
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();   // just close the dialog ( make it disappear )
            }
        });  // return the main screen

         */
        /*
        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){

            @Override
            public void run() {

                //  過兩秒後要做的事情
                     dialog.dismiss();          // close the dialog and launch login dialog again
                //  finishAndRemoveTask() ;    // exit app normally

            }}, 10000);

         */

        checkFileDownloadFinish(tempdialog);   // 檔案下載完成與否
        //checkWeatherForecastDownloadFinish();  // 天氣預報下載完成檢查

    }  // end of ExitAppDialog



    @Override
    public void onBackPressed() {

        // must call a method to end Activity behind all statement
        // super.onBackPressed();   這個要禁用 因為使用會自動在第一次進入app 退出時 不會有對話框詢問 就關閉 app
        // finish() ;    // call finish method
        // 接著 , 要跳出一個對話框詢問是否要退出 app

        ExitAppDialog();   // exit the app

    }

    private void checkFileDownloadFinish(AlertDialog tempdialog ) {
        handlerForDetecting.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (filedownloadcflag) { // true is closing dialog

                    if (tempdialog != null && tempdialog.isShowing()) {

                        tempdialog.dismiss();   // 關閉下載對話框

                                Intent intent = new Intent(MainActivity.this, SerialPortPreferences.class);
                                startActivity(intent);




                    }
                } else {

                    // 旗號未改變，繼續檢查
                    Log.d("bnm" , "filedownloadcflag :" + filedownloadcflag) ; // dump filedownloadcflag's value
                    checkFileDownloadFinish (tempdialog);
                }
            }
        }, 500); // 每隔 500ms check
    }

    ////////////////////// 檢查天氣預報下載完成與否 /////////////////////////////
    private void checkWeatherForecastDownloadFinish() {
        handlerForDetecting.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (DownloadingFinish) {

                        // 再加個 3 秒 delay 一下

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable(){

                            @Override
                            public void run() {

                                //過 3秒後要做的事情

                                Log.d("cvb", "天氣資料下載完畢! ") ;

                                Log.d("cvb","url            +++++ " + GeturlForWeatherForecast());
                                Log.d("zxc","Authorization  +++++ " + GetAuthorizationForWeatherForecast());
                                Log.d("zxc","LocationId     +++++ " + GetlocationIdForWeatherForecast()) ;

                                //////// 確定資料備妥 才去取資料 - DownloadingFinish 為 true
                                new GetWeatherTask().execute(WEATHER_URL);  // 開始下載資料  via 非同步 task

                            }}, 2000);

                } else {

                    // 旗號未改變，繼續檢查
                    Log.d("zxc" , "DownloadingFinish :" + DownloadingFinish ) ; // dump DownloadingFinish's value
                    checkWeatherForecastDownloadFinish ();
                }
            }
        }, 500); // 每隔 500ms check
    }

    /////////////////////// 使用 AsyncTask 執行網絡操作 - 取出天氣資料
    private class GetWeatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String jsonString = "";

            try {

                // 建立 URL 物件
                URL url = new URL(urls[0]);
                // 打開 HTTP 連接
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(5000);  // 設置連接超時
                urlConnection.setReadTimeout(5000);     // 設置讀取超時
                urlConnection.connect();

                // 取得回應碼
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // 讀取回應
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    reader.close();
                    jsonString = response.toString();  // 取得回應字串

                    StaticWeeklyWetherForecast = jsonString ;     /////////// copy 一周天氣資料
                    Log.d("zxc","一周天氣資料(static) :" + StaticWeeklyWetherForecast) ;

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return jsonString;
        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);

            if (!result.isEmpty()) {

                Log.d("zxc","取出的資料 >>>>　" + result.toString()) ;
                // 解析 天氣資料的 JSON
                parseWeatherData(result);

            }
        }
    }   // end of asynch task


    private void parseWeatherData(String weeklyweatherstring) {

    try {

            JSONObject jsonObject = new JSONObject(weeklyweatherstring);      // convert plain string to json object
            JSONObject records = jsonObject.getJSONObject("records");         // parse records
            JSONArray locations = records.getJSONArray("locations");    // locations

            for (int i = 0; i < locations.length(); i++) {

                JSONObject locationObj = locations.getJSONObject(i);
                String locationsName = locationObj.getString("locationsName");    // get locationname
                Log.d("zxc", "locationname:" + locationsName);
                JSONArray locationArray = locationObj.getJSONArray("location");    // parse location json array

                // location  json array
                for (int j = 0; j < locationArray.length(); j++) {

                    JSONObject locObj = locationArray.getJSONObject(j);    // get locationarray

                    String locationName = locObj.getString("locationName");  // 區

                    String geocode = locObj.getString("geocode");
                    String lat = locObj.getString("lat");
                    String lon = locObj.getString("lon");


                    if (locationName.equals("西區")) {

                        Log.d("zxc" , "西區天氣資料");

                        JSONArray weatherElementArray = locObj.getJSONArray("weatherElement");  //

                        for (int k = 0; k < weatherElementArray.length(); k++) {

                            JSONObject weatherElementObj = weatherElementArray.getJSONObject(k);
                            String elementName = weatherElementObj.getString("elementName");
                            String description = weatherElementObj.getString("description");


                            if (elementName.equals("PoP12h")) {      //  12小時降雨機率

                                Log.d("zxc", "12小時降雨機率");

                                JSONArray timeArray = weatherElementObj.getJSONArray("time");  // start/end time

                                // ArrayList<TimeNElementValue> temp = new ArrayList<>();   // temp array list
                                String startTime , endTime , value ;

                                for (int kk = 0; kk < timeArray.length(); kk++) {

                                    JSONObject timeObj = timeArray.getJSONObject(kk);
                                    startTime = timeObj.getString("startTime");
                                    endTime = timeObj.getString("endTime");

                                    JSONArray elementValueArray = timeObj.getJSONArray("elementValue");
                                    //  get elementvalue

                                    JSONObject elementValueObj = elementValueArray.getJSONObject(0);
                                    value = elementValueObj.getString("value");

                                    TimeNElementValue temp = new TimeNElementValue(startTime,endTime,value) ;

                                    Log.d("zxc" , "資料::::::::::::::: " ) ;
                                    Log.d("zxc" , "startTime :" + startTime) ;
                                    Log.d("zxc" , "endTime :" + endTime) ;
                                    Log.d("zxc" , "value  :" + temp.getValue()) ;

                                    // notice ! 這個降雨率只有前三天, 後面的四天都是空白的 !

                                     SavePop12hArrayList(kk,temp);  // ok

                                     TimeNElementValue obj = GetPop12hArrayListElement(kk) ;
                                     Log.d("zxc" , "取出的資料 starttime: " + obj.getStartTime()) ;
                                     Log.d("zxc" , "取出的資料 endtime: " + obj.getEndTime()) ;
                                     Log.d("zxc","取出的資料 value:" + obj.getValue()) ;

                                }   // end of time for

                                Log.d("zxc","poppoppoppoppoppoppoppoppop :" ) ;

                                Log.d("zxc","pop12harraylist 長度:" + GetPop12hArrayListLength()) ;

                                for (int kk = 0 ; kk < GetPop12hArrayListLength() ; kk ++) {
                                    Log.d("zxc" , "start time >>>>" + GetPop12hArrayListElement(kk).getStartTime()) ;
                                    Log.d("zxc" , "end time   >>>>" + GetPop12hArrayListElement(kk).getEndTime()) ;
                                    Log.d("zxc" , "value      >>>>" + GetPop12hArrayListElement(kk).getValue()) ;
                                }

                                for (int gg = 0 ; gg < PoP12hArrayList.size() ; gg ++) {
                                    // 長度 : 14
                                    Log.d("zxc", gg + " PoP12hArrayList") ;
                                    Log.d("zxc" , "======== start time :"     + PoP12hArrayList.get(gg).getStartTime()) ;
                                    Log.d("zxc" , "======== end time :"       + PoP12hArrayList.get(gg).getEndTime()) ;
                                    Log.d("zxc" , "======== element value :"  + PoP12hArrayList.get(gg).getValue()) ;

                                }

                            } else if (elementName.equals("RH"))   {      // 濕度
                                Log.d("zxc" , " 濕度");

                                JSONArray timeArray = weatherElementObj.getJSONArray("time");  // start/end time

                                String startTime , endTime , value ;

                                for (int kk = 0; kk < timeArray.length(); kk++) {

                                    JSONObject timeObj = timeArray.getJSONObject(kk);
                                    startTime = timeObj.getString("startTime");
                                    endTime = timeObj.getString("endTime");

                                    JSONArray elementValueArray = timeObj.getJSONArray("elementValue");
                                    //  get elementvalue

                                    JSONObject elementValueObj = elementValueArray.getJSONObject(0);
                                    value = elementValueObj.getString("value");

                                    TimeNElementValue temp = new TimeNElementValue(startTime,endTime,value) ;

                                    Log.d("zxc" , "資料::::::::::::::: " ) ;
                                    Log.d("zxc" , "startTime :" + startTime) ;
                                    Log.d("zxc" , "endTime :" + endTime) ;
                                    Log.d("zxc" , "value  :" + temp.getValue()) ;

                                    SaveHumidityArrayList(kk,temp);  // ok

                                    TimeNElementValue obj = GethumidityArrayListElement(kk) ;
                                    Log.d("zxc" , "取出的資料 starttime: " + obj.getStartTime()) ;
                                    Log.d("zxc" , "取出的資料 endtime: " + obj.getEndTime()) ;
                                    Log.d("zxc","取出的資料 value:" + obj.getValue()) ;

                                }   // end of time for

                                Log.d("zxc","poppoppoppoppoppoppoppoppop :" ) ;

                                Log.d("zxc","humidityArrayList 長度:" + GethumidityArrayListLength()) ;

                                for (int kk = 0 ; kk < GethumidityArrayListLength() ; kk ++) {
                                    Log.d("zxc" , "start time >>>>" + GethumidityArrayListElement(kk).getStartTime()) ;
                                    Log.d("zxc" , "end time   >>>>" + GethumidityArrayListElement(kk).getEndTime()) ;
                                    Log.d("zxc" , "value      >>>>" + GethumidityArrayListElement(kk).getValue()) ;
                                }

                                for (int gg = 0 ; gg < humidityArrayList.size() ; gg ++) {
                                    // 長度 : 14
                                    Log.d("zxc", gg + " humidityArrayList") ;
                                    Log.d("zxc" , "======== start time :"     + humidityArrayList.get(gg).getStartTime()) ;
                                    Log.d("zxc" , "======== end time :"       + humidityArrayList.get(gg).getEndTime()) ;
                                    Log.d("zxc" , "======== element value :"  + humidityArrayList.get(gg).getValue()) ;

                                }


                            } else if (elementName.equals("Wx"))   {      // 天氣描述
                                Log.d("zxc" , "天氣描述");

                                JSONArray timeArray = weatherElementObj.getJSONArray("time");  // start/end time

                                String startTime , endTime , value ;

                                for (int kk = 0; kk < timeArray.length(); kk++) {

                                    JSONObject timeObj = timeArray.getJSONObject(kk);
                                    startTime = timeObj.getString("startTime");
                                    endTime = timeObj.getString("endTime");

                                    JSONArray elementValueArray = timeObj.getJSONArray("elementValue");
                                    //  get elementvalue

                                    JSONObject elementValueObj = elementValueArray.getJSONObject(0);
                                    value = elementValueObj.getString("value");

                                    TimeNElementValue temp = new TimeNElementValue(startTime,endTime,value) ;

                                    Log.d("zxc" , "資料::::::::::::::: " ) ;
                                    Log.d("zxc" , "startTime :" + startTime) ;
                                    Log.d("zxc" , "endTime :" + endTime) ;
                                    Log.d("zxc" , "value  :" + temp.getValue()) ;

                                    SaveWeatherDescriptionArrayList(kk,temp);  // ok - 天氣描述

                                    TimeNElementValue obj = GetWeatherDescriptionArrayListElement(kk) ;  // 天氣描述元素
                                    Log.d("zxc" , "取出的資料 starttime: " + obj.getStartTime()) ;
                                    Log.d("zxc" , "取出的資料 endtime: " + obj.getEndTime()) ;
                                    Log.d("zxc","取出的資料 value:" + obj.getValue()) ;

                                }   // end of time for

                                Log.d("zxc","天氣描述 天氣描述天氣描述天氣描述天氣描述天氣描述天氣描述天氣描述 :" ) ;

                                Log.d("zxc","WeatherDescriptionArrayLis 長度:" + GetMinWeatherDescriptionListLength()) ;

                                for (int kk = 0 ; kk < GetMinWeatherDescriptionListLength() ; kk ++) {
                                    Log.d("zxc" , "start time >>>>" + GetWeatherDescriptionArrayListElement(kk).getStartTime()) ;
                                    Log.d("zxc" , "end time   >>>>" + GetWeatherDescriptionArrayListElement(kk).getEndTime()) ;
                                    Log.d("zxc" , "value      >>>>" + GetWeatherDescriptionArrayListElement(kk).getValue()) ;
                                }

                                for (int gg = 0 ; gg < WeatherDescriptionArrayList.size() ; gg ++) {
                                    ////////////////////// 長度 : 14 - 天氣狀態描述

                                    Log.d("zxc", gg + " WeatherDescriptionArrayList") ;
                                    Log.d("zxc" , "======== start time :"     + WeatherDescriptionArrayList.get(gg).getStartTime()) ;
                                    Log.d("zxc" , "======== end time :"       + WeatherDescriptionArrayList.get(gg).getEndTime()) ;
                                    Log.d("zxc" , "======== element value :"  + WeatherDescriptionArrayList.get(gg).getValue()) ;

                                }

                            } else if (elementName.equals("MinT")) {     // 最低溫
                                Log.d("zxc" , "最低溫");

                                JSONArray timeArray = weatherElementObj.getJSONArray("time");  // start/end time

                                String startTime , endTime , value ;

                                for (int kk = 0; kk < timeArray.length(); kk++) {

                                    JSONObject timeObj = timeArray.getJSONObject(kk);
                                    startTime = timeObj.getString("startTime");
                                    endTime = timeObj.getString("endTime");

                                    JSONArray elementValueArray = timeObj.getJSONArray("elementValue");
                                    //  get elementvalue

                                    JSONObject elementValueObj = elementValueArray.getJSONObject(0);
                                    value = elementValueObj.getString("value");

                                    TimeNElementValue temp = new TimeNElementValue(startTime,endTime,value) ;

                                    Log.d("zxc" , "資料::::::::::::::: " ) ;
                                    Log.d("zxc" , "startTime :" + startTime) ;
                                    Log.d("zxc" , "endTime :" + endTime) ;
                                    Log.d("zxc" , "value  :" + temp.getValue()) ;

                                    SaveMinTemperatureArrayList(kk,temp);  // ok - 低溫

                                    TimeNElementValue obj = GetMinTemperatureArrayListElement(kk) ;
                                    Log.d("zxc" , "取出的資料 starttime: " + obj.getStartTime()) ;
                                    Log.d("zxc" , "取出的資料 endtime: " + obj.getEndTime()) ;
                                    Log.d("zxc","取出的資料 value:" + obj.getValue()) ;

                                }   // end of time for

                                Log.d("zxc","最小溫度  :" ) ;

                                Log.d("zxc","MinTemperatureArrayList 長度:" + GetMinTemperatureArrayListLength()) ;

                                for (int kk = 0 ; kk < GetMinTemperatureArrayListLength() ; kk ++) {
                                    Log.d("zxc" , "start time >>>>" + GetMinTemperatureArrayListElement(kk).getStartTime()) ;
                                    Log.d("zxc" , "end time   >>>>" + GetMinTemperatureArrayListElement(kk).getEndTime()) ;
                                    Log.d("zxc" , "value      >>>>" + GetMinTemperatureArrayListElement(kk).getValue()) ;
                                }

                                for (int gg = 0 ; gg < MinTemperatureArrayList.size() ; gg ++) {
                                    // 長度 : 14
                                    Log.d("zxc", gg + " MinTemperatureArrayList") ;
                                    Log.d("zxc" , "======== start time :"     + MinTemperatureArrayList.get(gg).getStartTime()) ;
                                    Log.d("zxc" , "======== end time :"       + MinTemperatureArrayList.get(gg).getEndTime()) ;
                                    Log.d("zxc" , "======== element value :"  + MinTemperatureArrayList.get(gg).getValue()) ;

                                }

                            } else if (elementName.equals("MaxT")) {     // 最高溫
                               Log.d("zxc" ,  "最高溫");

                                JSONArray timeArray = weatherElementObj.getJSONArray("time");  // start/end time

                                String startTime , endTime , value ;

                                for (int kk = 0; kk < timeArray.length(); kk++) {

                                    JSONObject timeObj = timeArray.getJSONObject(kk);
                                    startTime = timeObj.getString("startTime");
                                    endTime = timeObj.getString("endTime");

                                    JSONArray elementValueArray = timeObj.getJSONArray("elementValue");
                                    //  get elementvalue

                                    JSONObject elementValueObj = elementValueArray.getJSONObject(0);
                                    value = elementValueObj.getString("value");

                                    TimeNElementValue temp = new TimeNElementValue(startTime,endTime,value) ;

                                    Log.d("zxc" , "資料::::::::::::::: " ) ;
                                    Log.d("zxc" , "startTime :" + startTime) ;
                                    Log.d("zxc" , "endTime :" + endTime) ;
                                    Log.d("zxc" , "value  :" + temp.getValue()) ;

                                    SaveMaxTemperatureArrayList(kk,temp);  // ok - 高溫

                                    TimeNElementValue obj = GetMaxTemperatureArrayListElement(kk) ;
                                    Log.d("zxc" , "取出的資料 starttime: " + obj.getStartTime()) ;
                                    Log.d("zxc" , "取出的資料 endtime: " + obj.getEndTime()) ;
                                    Log.d("zxc","取出的資料 value:" + obj.getValue()) ;

                                }   // end of time for

                                Log.d("zxc","最高溫度  :" ) ;

                                Log.d("zxc","MaxTemperatureArrayList 長度:" + GetMinTemperatureArrayListLength()) ;

                                for (int kk = 0 ; kk < GetMaxTemperatureArrayListLength() ; kk ++) {
                                    Log.d("zxc" , "start time >>>>" + GetMaxTemperatureArrayListElement(kk).getStartTime()) ;
                                    Log.d("zxc" , "end time   >>>>" + GetMaxTemperatureArrayListElement(kk).getEndTime()) ;
                                    Log.d("zxc" , "value      >>>>" + GetMaxTemperatureArrayListElement(kk).getValue()) ;
                                }

                                for (int gg = 0 ; gg < MaxTemperatureArrayList.size() ; gg ++) {
                                    // 長度 : 14
                                    Log.d("zxc", gg + " MaxTemperatureArrayList") ;
                                    Log.d("zxc" , "======== start time :"     + MaxTemperatureArrayList.get(gg).getStartTime()) ;
                                    Log.d("zxc" , "======== end time :"       + MaxTemperatureArrayList.get(gg).getEndTime()) ;
                                    Log.d("zxc" , "======== element value :"  + MaxTemperatureArrayList.get(gg).getValue()) ;

                                }

                            } else {


                            }    // end of if ... else

                        }   // end of for
                    }      // end of 西區
                }
            }
        }
        catch (Exception e) {

            Log.d("zxc","錯誤:" + e.getMessage().toString()) ;

        }

    }   // endof parse string

    void SavePop12hArrayList(int index , TimeNElementValue obj) {
        this.PoP12hArrayList.add(index,obj);

        Log.d("zxc","SavePop12hArrayList");
        Log.d("zxc","startTime :" + this.PoP12hArrayList.get(index).getStartTime());
        Log.d("zxc","endTime :" + this.PoP12hArrayList.get(index).getEndTime());
        Log.d("zxc","value :" + this.PoP12hArrayList.get(index).getValue());

    }

    void SaveHumidityArrayList(int index , TimeNElementValue obj) {
        this.humidityArrayList.add(index,obj);

        Log.d("zxc","SaveHumidityArrayList");
        Log.d("zxc","startTime :" + this.humidityArrayList.get(index).getStartTime());
        Log.d("zxc","endTime :" + this.humidityArrayList.get(index).getEndTime());
        Log.d("zxc","value :" + this.humidityArrayList.get(index).getValue());

    }

    void SaveMinTemperatureArrayList(int index , TimeNElementValue obj) {
        this.MinTemperatureArrayList.add(index,obj);

        Log.d("zxc","SaveMinTemperatureArrayList");
        Log.d("zxc","startTime :" + this.MinTemperatureArrayList.get(index).getStartTime());
        Log.d("zxc","endTime :" + this.MinTemperatureArrayList.get(index).getEndTime());
        Log.d("zxc","value :" + this.MinTemperatureArrayList.get(index).getValue());

    }

    void SaveMaxTemperatureArrayList(int index , TimeNElementValue obj) {
        this.MaxTemperatureArrayList.add(index,obj);

        Log.d("zxc","SaveMinTemperatureArrayList");
        Log.d("zxc","startTime :" + this.MaxTemperatureArrayList.get(index).getStartTime());
        Log.d("zxc","endTime :" + this.MaxTemperatureArrayList.get(index).getEndTime());
        Log.d("zxc","value :" + this.MaxTemperatureArrayList.get(index).getValue());

    }

    void SaveWeatherDescriptionArrayList(int index , TimeNElementValue obj) {
        this.WeatherDescriptionArrayList.add(index,obj);

        Log.d("zxc","SaveWeatherDescriptionArrayList");
        Log.d("zxc","startTime :" + this.WeatherDescriptionArrayList.get(index).getStartTime());
        Log.d("zxc","endTime :" + this.WeatherDescriptionArrayList.get(index).getEndTime());
        Log.d("zxc","value :" + this.WeatherDescriptionArrayList.get(index).getValue());


    }
    private ArrayList<TimeNElementValue> GetPop12hArrayList() { return this.PoP12hArrayList ; }
    private ArrayList<TimeNElementValue> GethumidityArrayList() { return this.humidityArrayList ; }
    private TimeNElementValue GetPop12hArrayListElement(int index) { return this.PoP12hArrayList.get(index) ; }
    private TimeNElementValue GethumidityArrayListElement(int index) { return this.PoP12hArrayList.get(index) ; }
    private TimeNElementValue GetMinTemperatureArrayListElement(int index) { return this.MinTemperatureArrayList.get(index) ; }

    private TimeNElementValue GetMaxTemperatureArrayListElement(int index) { return this.MaxTemperatureArrayList.get(index) ; }
    private TimeNElementValue GetWeatherDescriptionArrayListElement(int index) { return this.WeatherDescriptionArrayList.get(index) ; }


    private int GetPop12hArrayListLength() { return this.PoP12hArrayList.size() ; }
    private int GethumidityArrayListLength() { return this.humidityArrayList.size() ; }
    private int GetMinTemperatureArrayListLength() { return this.MinTemperatureArrayList.size() ; }
    private int GetMaxTemperatureArrayListLength() { return this.MaxTemperatureArrayList.size() ; }
    private int GetMinWeatherDescriptionListLength() { return this.WeatherDescriptionArrayList.size() ; }

      public class MaxMinTemp {  // 高低溫
        String MaxTemp ;
        String MinTemp ;

        public MaxMinTemp(String MaxTemp , String MinTemp ) {
            this.MaxTemp = MaxTemp ;
            this.MinTemp = MinTemp ;
        }

        public MaxMinTemp(){ }  // constructor without parameters

        public String getMaxTemp() { return this.MaxTemp ; }
        public String getMinTemp() { return this.MinTemp;  }

        public void setMaxTemp(String MaxTemp ) { this.MaxTemp = MaxTemp ;  }
        public void setMinTemp(String MinTemp ) { this.MinTemp = MinTemp ;  }

    }

    // 下載圖檔
    // 傳入圖檔的 url - format :

    public byte[] downloadImage(String urlString) {

        Log.d("bnm","downloadImage(" + urlString + ")") ;

        try {

            URL url = new URL(urlString);

            Log.d("vbn"," >>>>>  取出之前登入成功的 Token: "  + GetToken()) ;

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setConnectTimeout(10000); //
            connection.setReadTimeout(10000);    // 避免網路的雍塞

            connection.setRequestProperty("Authorization","Bearer " + GetToken());   //
            connection.setRequestMethod("GET");   // get the user data
            connection.setRequestProperty("Connection", "keep-Alive");
            // conn.setRequestProperty("Content-Type", "application/json");

            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json ; ; charset=UTF-8");

            connection.setDoOutput(false);
            connection.setDoInput(true);     // Notice ! it must be set : true . input file stream
            connection.setUseCaches(false);
            connection.connect();            // connect it !

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {

                // http code : 200 , http 請求成功

                Log.d("vbn", "進入- 圖檔下載連線 ");

                InputStream input = connection.getInputStream();

                InputStreamReader reader = new InputStreamReader(input, "UTF-8");
                StringBuilder sb = new StringBuilder();

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                long totalsize = 0 ;

                while ((length = input.read(buffer)) != -1) {

                    byteArrayOutputStream.write(buffer, 0, length);
                    // Log.d("bnm","列總位元大小: " +length ) ;
                    totalsize += length ;    // 計算圖形總位元數

                }   // end of while

                input.close();    // 關閉輸入流

                Log.d("bnm" , "總位元數:" + totalsize ) ;
                Log.d("bnm", "byteArrayOutputStream 大小："+ byteArrayOutputStream.size()) ;

                return byteArrayOutputStream.toByteArray();

            }


        } catch (IOException e) {
            e.printStackTrace();
            Log.d("bnm" , "下載錯誤 :" + e.getMessage() );
        }

        return null;
    }

    /** 權限列表 */
    private static String[] permissionsList = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.USE_FULL_SCREEN_INTENT,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.FOREGROUND_SERVICE_CAMERA,
            Manifest.permission.FOREGROUND_SERVICE_MICROPHONE,
            Manifest.permission.FOREGROUND_SERVICE_PHONE_CALL,
            Manifest.permission.FOREGROUND_SERVICE_DATA_SYNC,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.CAMERA,
            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_DEVICE_POLICY_MICROPHONE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
    };

    /** 跟用戶要求權限 */
    private void checkApplicationPermission() {

        ActivityCompat.requestPermissions(MainActivity.this, permissionsList, 1);

    }

    //////////////////////////////////// 建立外部儲存記憶體路徑
    private File getOrCreateDirectory(String dirName) {

        File externalFilesDir = getExternalFilesDir(null);

        if (externalFilesDir == null) {

            // 若 externalFilesDir為 null，表明外部存儲無用

            Log.d("bbb", "External storage is not available.");


            return null;  // 無外部儲存路徑

        }

        File dir = new File(externalFilesDir, dirName);  // 建立一個外部儲存路徑

        if (!dir.exists()) {     // 檢查該路徑 , 若不存在就創建

            if (dir.mkdirs()) {  // 建立那個外部儲存路徑

                Log.d("bbb", "Created directory: " + dir.getAbsolutePath());

            } else {

                Log.d("bbb", "Failed to create directory: " + dir.getAbsolutePath());

                return null;
            }
        } else {

            Log.d("bbb", "Directory already exists: " + dir.getAbsolutePath());
        }

        return dir;
    }

    // 將 cgsImgList 中的 png 儲存到外部記憶體空間
    public void savecgsImgListPngFileToExternalStorage(Context context, Bitmap bitmap , int cgsImgId ) {

        Log.d("456","savecgsImgListPngFileToExternalStorage") ;
        // Log.d("zzz","savePngToExternalStorage") ;

        String fileName ;

        fileName = "cgsImgList"+cgsImgId+".png" ;

        File directory = new File(Environment.getExternalStorageDirectory() , "/Android/data/com.smartcity.cgs/files/Pictures/");

        ////////// 清除外部記憶體中所有的檔案 清除外部記憶體中所有的檔案 清除外部記憶體中所有的檔案
        // clearExternalStorageDirectory();    // 清除外部記憶體中所有的檔案  ----

        // 首先,要先檢查外部儲存記憶體中是否有檔案存在;若有,先清除乾淨
        // 刪除目錄下的所有檔案
        /*
        if (directory.isDirectory()) {

            File[] files = directory.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        file.delete();  // 刪除檔案

                        Log.d("456","刪除檔案");

                    }
                }
                Log.d("456", "檔案已清空");
            } else {
                Log.d("456", "目錄不存在或無法讀取");
            }
        } else {
            Log.d("456", "指定的路徑不是目錄");
        }

         */

        // 首先,要先檢查外部儲存記憶體中是否有檔案存在;若有,先清除乾淨
        if (isPNGFileExists(fileName , mContext) ) {
            Log.d("456", " MMMMMMMMMMMMMMMMMMMMMMMMMMMMM　存在之前的檔案!");
            // clear all files in external storage

        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            Log.d("456","目前無許可權");

            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            Log.d("456","許可權開啟");


            return;  // 開權限
        }
        else {
            Log.d("456" , "已經有許可權了") ;
        }

        //
        File externalStorageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (externalStorageDir != null) {
            //  建立 PNG文件
            File pngFile = new File(externalStorageDir, fileName );  // create a png file

            Log.d("456","檔案名稱: " + fileName) ;

            try (FileOutputStream fos = new FileOutputStream(pngFile)) {

                boolean compressflag = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos); // 壓完後以 PNG 格式寫入路徑中

                if ( compressflag == true ) {

                    Log.d("456", " ^^^^^^^^^^^^^^^ 壓縮且儲存成功");

                    File dv = new File(externalStorageDir,fileName);

                    Log.d("456", " 檔案路徑及檔名 :" + dv.getAbsolutePath());
                    Log.d("456", " 檔案路徑及檔名 :" + dv.getAbsolutePath()+", 是否存在:"+ dv.exists());

                    if (isPNGFileExists(fileName , mContext) ){
                        Log.d("456" , "有檔案!");

                        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.smartcity.cgs/files/Pictures/" + fileName;
                        Log.d("456","%%%%　檔案路徑及名稱 :" + filePath);

                        File imgFile = new File(filePath);

                        if (imgFile.exists()) {

                            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                            // 在這裡你可以將 myBitmap 設置到 ImageView 或進行其他操作
                            // 這裡是一個測試用
                            Log.d("456","檔案存在");

                            if (false) {
                                FrontView = findViewById(R.id.frontimage);
                                FrontView.setImageBitmap(myBitmap);
                            } // this is for test !

                        } else {
                            // 處理檔案不存在的情況
                            Log.e("456", "File does not exist.");
                        }

                    }
                    else {
                        Log.d("456" , "沒檔案!");

                    }

                }
                else
                    Log.d("456"," >>>>> 失敗") ;

            } catch (IOException e) {

                e.printStackTrace();
                Log.d("456","錯誤:" + e.getMessage()) ;

            }
        }
    }


    // 將 travelImgList 中的 png 儲存到外部記憶體空間
    public void savetravelImgListPngFileToExternalStorage(Context context, Bitmap bitmap , int travelImgListId ) {


        Log.d("travelImgListPng","savetravelImgListPngFileToExternalStorage") ;
        // Log.d("zzz","savePngToExternalStorage") ;

        String fileName ;

        fileName = "travelImgList"+travelImgListId+".png" ;

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            Log.d("travelImgListPng","目前無許可權");

            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            Log.d("travelImgListPng","許可權開啟");


            return;  // 開權限
        }
        else {
            Log.d("travelImgListPng" , "已經有許可權了") ;
        }

        // 获取外部存储目录
        File externalStorageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (externalStorageDir != null) {
            //  建立 PNG文件
            File pngFile = new File(externalStorageDir, fileName );  // create a png file

            Log.d("travelImgListPng","檔案名稱: " + fileName) ;

            try (FileOutputStream fos = new FileOutputStream(pngFile)) {

                boolean compressflag = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos); // 壓完後以 PNG 格式寫入路徑中

                if ( compressflag == true ) {

                    Log.d("travelImgListPng", " ^^^^^^^^^^^^^^^ 壓縮且儲存成功");

                    File dv = new File(externalStorageDir,fileName);
                    Log.d("travelImgListPng", " 檔案路徑及檔名 :" + dv.getAbsolutePath());
                    Log.d("travelImgListPng", " 檔案路徑及檔名 :" + dv.getAbsolutePath()+", 是否存在:"+ dv.exists());

                    if (isPNGFileExists(fileName , mContext) ){
                        Log.d("travelImgListPng" , "有檔案!");

                        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.smartcity.cgs/files/Pictures/" + fileName;
                        Log.d("travelImgListPng","%%%%　檔案路徑及名稱 :" + filePath);

                        File imgFile = new File(filePath);

                        if (imgFile.exists()) {

                            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                            // 在這裡你可以將 myBitmap 設置到 ImageView 或進行其他操作
                            // 這裡是一個測試用
                            Log.d("travelImgListPng","檔案存在");

                            if (false) {
                                FrontView = findViewById(R.id.frontimage);
                                FrontView.setImageBitmap(myBitmap);
                            } // this is for test !

                        } else {
                            // 處理檔案不存在的情況
                            Log.e("travelImgListPng", "File does not exist.");
                        }

                    }
                    else {
                        Log.d("travelImgListPng" , "沒檔案!");

                    }

                }
                else
                    Log.d("travelImgListPng"," >>>>> 失敗") ;

            } catch (IOException e) {

                e.printStackTrace();
                Log.d("travelImgListPng","錯誤:" + e.getMessage()) ;

            }
        }

    }  // end of savetravelImgListPngFileToExternalStorage

    //////////////////////////// 將 cgsTravelList 中的 png 儲存到外部記憶體空間
    public void savecgsTravelListPngFileToExternalStorage(Context context, Bitmap bitmap , int cgsId , int travelInfoId ) {


        Log.d("333","savecgsTravelListPngFileToExternalStorage") ;
        // Log.d("zzz","savePngToExternalStorage") ;

        String fileName ;

        fileName = "cgsTravelList"+"_"+ cgsId + "_" + travelInfoId+".png" ;

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            Log.d("333","目前無許可權");

            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            Log.d("333","許可權開啟");


            return;  // 開權限
        }
        else {
            Log.d("333" , "已經有許可權了") ;
        }

        // 获取外部存储目录
        File externalStorageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (externalStorageDir != null) {
            //  建立 PNG文件
            File pngFile = new File(externalStorageDir, fileName );  // create a png file

            Log.d("333","檔案名稱: " + fileName) ;

            try (FileOutputStream fos = new FileOutputStream(pngFile)) {

                boolean compressflag = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos); // 壓完後以 PNG 格式寫入路徑中

                if ( compressflag == true ) {

                    Log.d("333", " ^^^^^^^^^^^^^^^ 壓縮且儲存成功");

                    File dv = new File(externalStorageDir,fileName);
                    Log.d("333", " 檔案路徑及檔名 :" + dv.getAbsolutePath());
                    Log.d("333", " 檔案路徑及檔名 :" + dv.getAbsolutePath()+", 是否存在:"+ dv.exists());

                    if (isPNGFileExists(fileName , mContext) ){
                        Log.d("333" , "有檔案!");

                        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.smartcity.cgs/files/Pictures/" + fileName;
                        Log.d("333","%%%%　檔案路徑及名稱 :" + filePath);

                        File imgFile = new File(filePath);

                        if (imgFile.exists()) {

                            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                            // 在這裡你可以將 myBitmap 設置到 ImageView 或進行其他操作
                            // 這裡是一個測試用
                            Log.d("333","檔案存在");

                            if (false) {
                                FrontView = findViewById(R.id.frontimage);
                                FrontView.setImageBitmap(myBitmap);
                            } // this is for test !

                        } else {
                            // 處理檔案不存在的情況
                            Log.e("333", "File does not exist.");
                        }

                    }
                    else {
                        Log.d("333" , "沒檔案!");

                    }

                }
                else
                    Log.d("333"," >>>>> 失敗") ;

            } catch (IOException e) {

                e.printStackTrace();
                Log.d("333","錯誤:" + e.getMessage()) ;

            }
        }

    }  // end of savecgsTravelListPngFileToExternalStorage

    /////////////////////  檢查 png 檔案是否存在
    public boolean isPNGFileExists(String filename, Context context) {

        // 確保外部儲存空間是可用的

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // 構建文件路徑

            File externalStorageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

            Log.d("876","FilePath : " + externalStorageDir.getAbsolutePath()) ;

            File file = new File(externalStorageDir , filename);

            Log.d("877" , "檔案全名:" + file.getAbsolutePath().toString()) ;

            // 檢查文件是否存在並且是 PNG 文件

            return file.exists() && file.isFile() ;  // 存在: true

        }

        return false;
    }  // end of  isPNGFileExists

    //////////// 檢查 extrenal storage 是否圖片有完整

    void  VisitAllPNGFilesFromExternalStorage() {

        //  permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {

            // if permission has been got， find those PNG files in ES.
            findAllPngFiles();

        }
    }

    ////////////////////// 找外部存储中的所有 PNG 文件 //////////////////////////
    private void findAllPngFiles() {

        // 取的外部記憶體根目錄

        File externalStorageDir = Environment.getExternalStorageDirectory();

        // 儲存 所有找到的 PNG 文件

        ArrayList<File> pngFiles = new ArrayList<>();

        // find them recursively
        findPngFilesRecursively(externalStorageDir, pngFiles);

        // dump the directory of png files

        for (File pngFile : pngFiles) {

            Log.d("qsx","已找到的 png 檔案名稱: " +  pngFile.getAbsolutePath());

        }
    }

    // Vist directory recursively and find png files .
    private void findPngFilesRecursively(File dir, ArrayList<File> pngFiles) {

        if (dir.isDirectory()) {

            File[] files = dir.listFiles();

            if (files != null) {

                for (File file : files) {
                    if (file.isDirectory()) {

                        //  if pngFiles is folder , find them recursively !

                        findPngFilesRecursively(file, pngFiles);

                    } else if (file.isFile() && file.getName().endsWith(".png")) {

                        // if file's extendsion is  .png， add them to list

                        pngFiles.add(file);
                    }
                }
            }
        }

    }   // end of findPngFilesRecursively

    public void clearExternalStorageDirectory() {

        // 取得外部儲存的根目錄

        File externalDir = Environment.getExternalStorageDirectory();

        // 呼叫自定義方法來遞迴刪除所有檔案
        Log.d("666","外部檔案路徑 根目錄 ::::::::: " + externalDir.getAbsolutePath()) ;

        deleteRecursive(externalDir);
    }

    private void deleteRecursive(File fileOrDirectory) {

        Log.d("666","開始遞迴清除外部記憶體中的檔案");

        if (fileOrDirectory.isDirectory()) {

            // 刪除目錄中的所有檔案和子目錄

            for (File child : fileOrDirectory.listFiles()) {
                Log.d("666","清除外部記憶體中的檔案 ..... ");
                deleteRecursive(child);
            }
        }

        // 刪除該檔案或目錄 0924
        // fileOrDirectory.delete();
    }

   // 取出外部記憶體中的 png 檔 並
    public Bitmap loadImageFromExternalStorage(String fileName) {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            File file = new File(Environment.getExternalStorageDirectory(), fileName);
            if (file.exists()) {
                return BitmapFactory.decodeFile(file.getAbsolutePath());
            }
        }
        return null; // 如果檔案不存在或無法讀取
    }  // end of loadImageFromExternalStorage


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // db.execSQL("DELETE FROM logintable");

        // 關 database 前先檢查一下資料是否有清除乾淨

        // 關閉 database
        db.close();

        Log.d("vbn","主 activity 已經關閉 ") ;

        loginfirsttime = false ;

        Log.d("vbn", "loginfirsttime :" + loginfirsttime) ;

    }

    private void DownloadingPngDialog() {

        AlertDialog.Builder alertDialog =
                new AlertDialog.Builder(MainActivity.this);

        alertDialog.setTitle("檔案下載中");
        alertDialog.setMessage("請稍後 ....");

        alertDialog.setCancelable(false);


        AlertDialog dialog = alertDialog.create();

        alertDialog.show();

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = 400; // customed width
        layoutParams.height = 200; //
        dialog.getWindow().setAttributes(layoutParams);

    }

    private void SaveId(int id ) {
        this.id = id ;
    }
    private int GetId() { return this.id ; }

    private class SendingThread extends Thread {

        @Override
        public void run() {

            while (!isInterrupted()) {

                try {

                    if ( outputStream != null) {

                        outputStream.write(LEDColor_Setting);     // rs232 command - led's color setting
                        Log.d("qaz","發送rs232 command -  led 顏色設定 ") ;

                    } else {
                        return;

                    }
                } catch (IOException e) {

                    e.printStackTrace();
                    return;
                }
            }
        }
    }


    public class PngFileChecker {

        public File[] getAllPngFiles() {

            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

                File externalStorageDir = Environment.getExternalStorageDirectory();
                File[] pngFiles = externalStorageDir.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.toLowerCase().endsWith(".png");
                    }
                });
                return pngFiles;
            }
            return null;
        }
    }

}   // end of MainActivity
