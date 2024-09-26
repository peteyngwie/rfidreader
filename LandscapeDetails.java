package com.smartcity.cgs;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartcity.item;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class LandscapeDetails extends AppCompatActivity {

    TextView DateTxt ;                   //  Day/Time

    Timer Clocktimer = new Timer() ;     // 計時器 for flushing time per 0.5 second

    String title, time , address , distance , descriptions ;
    int whichone ;

    private TextView Title , SubTitleAddress , Address,  Time , Distance  , Description ;
    private TextView CurrentAddressTxt ;
    private View viewline;
    private ImageView  PeopleWalking , Location ;

    private ImageView LocationMap ;    // landscape's location map


    GridView LandscapeGV;   // gridview of landscapes

    private File file ;
    ArrayList<Bitmap> bitmapList = new ArrayList<>();  // 這裡宣告一個 bitmaplist array

    private  Context mContext ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landscape_details);

        EnableApplicationPermission();   // enable external storage w/r permission

        mContext = LandscapeDetails.this ;

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

        DateTxt   =  (TextView)findViewById(R.id.datetimetxt) ;  // day and time

        SideSpinner SideBarFun;   // sidebar declaration
        SideBarFun = (SideSpinner)this.findViewById(R.id.sidespinner_funs);   // sidebar functions

        Title = (TextView) findViewById(R.id.titletxt) ;    // landscape's title

        SubTitleAddress = (TextView) findViewById(R.id.subtitleaddresstxt) ;  // subtitle address
        Time = (TextView) findViewById(R.id.timetxt) ;                        // time
        Distance = (TextView) findViewById(R.id.distancetxt);                 // disatnce
        Description = (TextView) findViewById(R.id.landscapedescriptiontxt) ;  // description

        CurrentAddressTxt = (TextView) findViewById(R.id.currentaddresstxt);  // current address text
        viewline = (View) findViewById(R.id.viewline) ;

        viewline.setBackgroundColor(0x88888888);   // grey color line

        // Address = (TextView) findViewById(R.id.addresssmalltxt) ;     // cardview address

        Bundle bundle = getIntent().getExtras();    // 取出 intent 中的 bundle
        ////////////////////////////////////////////////////////////////////////////
        // 拆出 bundle的內容，key為content
        // 取出 景點抬頭 , 時間 , 位址 , 距離

        title    = bundle.getString("title");                 // title
        time     = bundle.getString("time");                  // walking time
        address  = bundle.getString("address") ;              // address
        distance = bundle.getString("distance") ;             // distance
        descriptions = bundle.getString("description");       // descriptions
        whichone = bundle.getInt("position");                 // which one

        assert title != null && time != null && address != null && distance != null ;

        Log.d(TAG,"名稱 " + title.toString() ) ;
        Log.d(TAG,"時間 " + time.toString() )  ;
        Log.d(TAG,"地址 " + address.toString() )  ;
        Log.d(TAG,"距離 " + distance.toString() )  ;
        Log.d(TAG,"描述 " + descriptions.toString());

        /*
        Title.setText(title);                // 設定名稱
        SubTitleAddress.setText(address);    // 地址
        Time.setText(time);                  // 時間
        Distance.setText(distance);          // 距離
        CurrentAddressTxt.setText(address);  // 目前位置
         */

        updateTime();                        // 更新時間  per 0.5 second

        LandscapeGV = findViewById(R.id.idGVcourses);
        ArrayList<LandscapeModel> courseModelArrayList = new ArrayList<LandscapeModel>();

        LocationMap = findViewById(R.id.mainamp) ; // 主圖

        // LandscapeGV.setLayoutParams(new GridView.LayoutParams(200, 400));

        int linecount ;

        boolean FileExistFlag = false ;


        // 這裡 5/29 , justify which one was cliecked and show its details

        //  這裡是用顧固定的資料

        switch (whichone) {

            case 0 :

                // 第一項  北門火車站

                // 對應的圖示: travelImgList1-3.png , 由左至右

                // 首先,要清空 bitmap array list

                bitmapList.clear();   // clear all elements in array list

                // travelImgList1.png
                file = new File(Environment.getExternalStorageDirectory(), "/Android/data/com.smartcity.cgs/files/Pictures/travelImgList1.png");

                if (file.exists()) {
                    // 文件存在
                    Log.d("qaz", "travelImgList1.png 存在");

                    Bitmap bitmap  = ConvertPngToBitmap(mContext,"travelImgList1.png") ;
                    bitmapList.add(bitmap);   // add a bitmap that has been converted - travelImgList1.png

                } else {
                    // 文件不存在
                    Log.d("qaz", "travelImgList1.png 不存在");

                }

                // travelImgList2.png
                file = new File(Environment.getExternalStorageDirectory(), "/Android/data/com.smartcity.cgs/files/Pictures/travelImgList2.png");

                if (file.exists()) {
                    // 文件存在
                    Log.d("qaz", "travelImgList2.png 存在");

                    Bitmap bitmap  = ConvertPngToBitmap(mContext,"travelImgList2.png") ;
                    bitmapList.add(bitmap);   // add a bitmap that has been converted - travelImgList2.png
                } else {
                    // 文件不存在
                    Log.d("qaz", "travelImgList2.png 不存在");

                }

                // travelImgList3.png
                file = new File(Environment.getExternalStorageDirectory(), "/Android/data/com.smartcity.cgs/files/Pictures/travelImgList3.png");

                if (file.exists()) {
                    // 文件存在
                    Log.d("qaz", "travelImgList3.png 存在");

                    Bitmap bitmap  = ConvertPngToBitmap(mContext,"travelImgList3.png") ;
                    bitmapList.add(bitmap);   // Add a bitmap that has been converted - travelImgList3.png


                } else {
                    // 文件不存在
                    Log.d("qaz", "travelImgList3.png 不存在");

                }

                // okay , the above codes is responsible for converting png to bitmap.

                // courseModelArrayList.add(new LandscapeModel(R.drawable.alishanforestrailway04));  // 第一
                // courseModelArrayList.add(new LandscapeModel(R.drawable.alishanforestrailway01));  // 第二
                // courseModelArrayList.add(new LandscapeModel(R.drawable.alishanforestrailway3));   // 第三

                // LocationMap.setImageDrawable(getResources().getDrawable( R.drawable.map1 ));   // 第一景點地圖 - 這裡是靜態的

                // 下面要由 cgsTravlList 中抽出第一張圖 - cgsTravelList_1_1.png  ( 景點主圖 - 北門火車站 )

                file = new File(Environment.getExternalStorageDirectory(), "/Android/data/com.smartcity.cgs/files/Pictures/cgsTravelList_1_1.png");

                if (file.exists()) {
                    // 文件存在
                    Log.d("qaz", "cgsTravelList_1_1.png 存在");

                    Bitmap bitmap  = ConvertPngToBitmap(mContext,"cgsTravelList_1_1.png") ;
                    // bitmapList.add(bitmap);   // add a bitmap that has been converted - travelImgList1.png
                    LocationMap.setImageBitmap(bitmap);  // 地圖主圖


                } else {
                    // 文件不存在
                    Log.d("qaz", "cgsTravelList_1_1.png不存在");

                }

                Title.setText(title);                // 設定名稱
                SubTitleAddress.setText(address);    // 地址
                Time.setText(time);                  // 時間
                Distance.setText(distance);          // 距離
                Description.setText(descriptions);   // 描述
                CurrentAddressTxt.setText(address);  // 目前位置

                break ;

            case 1 :

                // 首先,要清空 bitmap array list

                bitmapList.clear();   // clear all elements in array list

                // 第二項  森林之歌

                // 對應的圖示: travelImgLis24-22.png , 由左至右

                // travelImgList24.png
                file = new File(Environment.getExternalStorageDirectory(), "/Android/data/com.smartcity.cgs/files/Pictures/travelImgList24.png");

                if (file.exists()) {
                    // 文件存在
                    Log.d("qaz", "travelImgList24.png 存在");

                    Bitmap bitmap  = ConvertPngToBitmap(mContext,"travelImgList24.png") ;
                    bitmapList.add(bitmap);   // add a bitmap that has been converted - travelImgList1.png

                } else {
                    // 文件不存在
                    Log.d("qaz", "travelImgList24.png 不存在");

                }

                // travelImgList23.png
                file = new File(Environment.getExternalStorageDirectory(), "/Android/data/com.smartcity.cgs/files/Pictures/travelImgList23.png");

                if (file.exists()) {
                    // 文件存在
                    Log.d("qaz", "travelImgList23.png 存在");

                    Bitmap bitmap  = ConvertPngToBitmap(mContext,"travelImgList23.png") ;
                    bitmapList.add(bitmap);   // add a bitmap that has been converted - travelImgList2.png
                } else {
                    // 文件不存在
                    Log.d("qaz", "travelImgList23.png 不存在");

                }

                // travelImgList22.png
                file = new File(Environment.getExternalStorageDirectory(), "/Android/data/com.smartcity.cgs/files/Pictures/travelImgList22.png");

                if (file.exists()) {
                    // 文件存在
                    Log.d("qaz", "travelImgList22.png 存在");

                    Bitmap bitmap  = ConvertPngToBitmap(mContext,"travelImgList22.png") ;
                    bitmapList.add(bitmap);   // Add a bitmap that has been converted - travelImgList3.png


                } else {
                    // 文件不存在
                    Log.d("qaz", "travelImgList22.png 不存在");

                }


                courseModelArrayList.add(new LandscapeModel(R.drawable.chiayimuseum04));   // 第一
                courseModelArrayList.add(new LandscapeModel(R.drawable.chiayimuseum01));   // 第二
                courseModelArrayList.add(new LandscapeModel(R.drawable.chiayimuseum03));   // 第三

                // LocationMap.setImageDrawable(getResources().getDrawable( R.drawable.map2 ));   // 第二景點地圖

                file = new File(Environment.getExternalStorageDirectory(), "/Android/data/com.smartcity.cgs/files/Pictures/cgsTravelList_1_7.png");

                if (file.exists()) {
                    // 文件存在
                    Log.d("qaz", "cgsTravelList_1_7.png 存在");

                    Bitmap bitmap  = ConvertPngToBitmap(mContext,"cgsTravelList_1_7.png") ;
                    // bitmapList.add(bitmap);   // add a bitmap that has been converted - travelImgList1.png
                    LocationMap.setImageBitmap(bitmap);  // 地圖主圖


                } else {
                    // 文件不存在
                    Log.d("qaz", "cgsTravelList_1_7.png 不存在");

                }

                Title.setText(title);                // 設定名稱
                SubTitleAddress.setText(address);    // 地址
                Time.setText(time);                  // 時間
                Distance.setText(distance);          // 距離
                Description.setText(descriptions);   // 描述
                CurrentAddressTxt.setText(address);  // 目前位置

                break ;

            case 2 :


                courseModelArrayList.add(new LandscapeModel(R.drawable.chiayimaterial05));   // 第二
                courseModelArrayList.add(new LandscapeModel(R.drawable.chiayimaterial03));   // 第三
                courseModelArrayList.add(new LandscapeModel(R.drawable.chiayimaterial01));   // 第三

                LocationMap.setImageDrawable(getResources().getDrawable( R.drawable.map3 ));   // 第三景點地圖

                Title.setText(title);                // 設定名稱
                SubTitleAddress.setText(address);    // 地址
                Time.setText(time);                  // 時間
                Distance.setText(distance);          // 距離
                Description.setText(descriptions);   // 描述

                CurrentAddressTxt.setText(address);  // 目前位置
                break ;
            case 3 :

                courseModelArrayList.add(new LandscapeModel(R.drawable.hinokivillage01));   // 第二
                courseModelArrayList.add(new LandscapeModel(R.drawable.hinokivillage03));   // 第三
                courseModelArrayList.add(new LandscapeModel(R.drawable.hinokivillage04));   // 第三

                LocationMap.setImageDrawable(getResources().getDrawable( R.drawable.map4 ));   // 第四景點地圖

                Title.setText(title);                // 設定名稱
                SubTitleAddress.setText(address);    // 地址
                Time.setText(time);                  // 時間
                Distance.setText(distance);          // 距離
                Description.setText(descriptions);   // 描述
                CurrentAddressTxt.setText(address);  // 目前位置

                break ;
            case 4 :

                courseModelArrayList.add(new LandscapeModel(R.drawable.forestsong04));   // 第二
                courseModelArrayList.add(new LandscapeModel(R.drawable.forestsong01));   // 第三
                courseModelArrayList.add(new LandscapeModel(R.drawable.forestsong02));   // 第三

                LocationMap.setImageDrawable(getResources().getDrawable( R.drawable.map5 ));   // 第五景點地圖

                Title.setText(title);                // 設定名稱
                SubTitleAddress.setText(address);    // 地址
                Time.setText(time);                  // 時間
                Distance.setText(distance);          // 距離
                Description.setText(descriptions);   // 描述
                CurrentAddressTxt.setText(address);  // 目前位置

                break ;
            case 5 :

                courseModelArrayList.add(new LandscapeModel(R.drawable.beimenstation03));   // 第二
                courseModelArrayList.add(new LandscapeModel(R.drawable.beimenstation04));   // 第三
                courseModelArrayList.add(new LandscapeModel(R.drawable.beimestation01));    // 第三
                LocationMap.setImageDrawable(getResources().getDrawable( R.drawable.map6 ));   // 第六景點地圖

                Title.setText(title);                // 設定名稱
                SubTitleAddress.setText(address);    // 地址
                Time.setText(time);                  // 時間
                Distance.setText(distance);          // 距離
                Description.setText(descriptions);   // 描述

                CurrentAddressTxt.setText(address);  // 目前位置
                break ;
            case 6 :

                courseModelArrayList.add(new LandscapeModel(R.drawable.taiwantile04));   // 第一
                courseModelArrayList.add(new LandscapeModel(R.drawable.taiwantile06));   // 第二
                courseModelArrayList.add(new LandscapeModel(R.drawable.taiwantile07));   // 第三

                LocationMap.setImageDrawable(getResources().getDrawable( R.drawable.map7 ));   // 第七景點地圖


                Title.setText(title);                // 設定名稱
                SubTitleAddress.setText(address);    // 地址
                Time.setText(time);                  // 時間
                Distance.setText(distance);          // 距離
                Description.setText(descriptions);   // 描述

                CurrentAddressTxt.setText(address);  // 目前位置
                break ;
            case 7 :

                courseModelArrayList.add(new LandscapeModel(R.drawable.beixianghupark02));   // 第一
                courseModelArrayList.add(new LandscapeModel(R.drawable.beixianghupark01));   // 第二
                courseModelArrayList.add(new LandscapeModel(R.drawable.beixianghupark03));   // 第三

                LocationMap.setImageDrawable(getResources().getDrawable( R.drawable.map8 ));   // 第八景點地圖

                Title.setText(title);                // 設定名稱
                SubTitleAddress.setText(address);    // 地址
                Time.setText(time);                  // 時間
                Distance.setText(distance);          // 距離
                Description.setText(descriptions);   // 描述

                CurrentAddressTxt.setText(address);  // 目前位置
                break ;
            case 8 :

                courseModelArrayList.add(new LandscapeModel(R.drawable.chiayimunicipalart02));   // 第一
                courseModelArrayList.add(new LandscapeModel(R.drawable.chiayimunicipalart03));   // 第二
                courseModelArrayList.add(new LandscapeModel(R.drawable.chiayimunicipalart05));   // 第三

                LocationMap.setImageDrawable(getResources().getDrawable( R.drawable.map9 ));   // 第九景點地圖

                Title.setText(title);                // 設定名稱
                SubTitleAddress.setText(address);    // 地址
                Time.setText(time);                  // 時間
                Distance.setText(distance);          // 距離
                Description.setText(descriptions);   // 描述

                CurrentAddressTxt.setText(address);  // 目前位置
                break;
            case 9 :

                courseModelArrayList.add(new LandscapeModel(R.drawable.chiayiculturalpark03));   // 第一
                courseModelArrayList.add(new LandscapeModel(R.drawable.chiayiculturalpark05));   // 第二
                courseModelArrayList.add(new LandscapeModel(R.drawable.chiayiculturalpark06));   // 第三

                LocationMap.setImageDrawable(getResources().getDrawable( R.drawable.map10));   // 第十景點地圖

                Title.setText(title);                // 設定名稱
                SubTitleAddress.setText(address);    // 地址
                Time.setText(time);                  // 時間
                Distance.setText(distance);          // 距離
                Description.setText(descriptions);   // 描述

                CurrentAddressTxt.setText(address);  // 目前位置
                break;

            default:

                Toast.makeText(this, "錯誤的資料", Toast.LENGTH_SHORT).show();
                break ;
        }

        /*
        courseModelArrayList.add(new LandscapeModel(R.drawable.alishanforestrailway04));  // 第一
        courseModelArrayList.add(new LandscapeModel(R.drawable.alishanforestrailway01));  // 第二
        courseModelArrayList.add(new LandscapeModel(R.drawable.alishanforestrailway3));   // 第三
         */

        // 每個 Grid 項的寬高，單位像素
        int itemWidth = 320;    //  320 pixels : width
        int itemHeight = 205;   //  205 pixels : height
        // ArrayList<Bitmap> bitmapList = new ArrayList<>();


        GridViewAdapter adapter = new GridViewAdapter(this, bitmapList ,itemWidth ,  itemHeight);
        LandscapeGV.setAdapter(adapter);

       // LandscapeGVAdapter adapter = new LandscapeGVAdapter (this, courseModelArrayList);
       //  LandscapeGV.setAdapter(adapter);


   }  // end of onCreate



    private void updateTime( ) {

        Clocktimer.schedule(new TimerTask() {
            @Override
            public void run() {
                DateTxt.setText(CurrentTime());   // Get current to show

            }
        },0,500);

    }    // end of updateTime

    private String CurrentTime() {

        String nowDate = new SimpleDateFormat("YYYY/MM/dd").format(new Date());  // 取得目前的日期
        assert nowDate != null;
        String nowTime = new SimpleDateFormat("HH:mm").format(new Date());  // 取得目前時間
        assert nowTime != null ;

        String nowDateNTime = nowDate + " "+ nowTime;

        return nowDateNTime  ;   // 傳回目前的時間

    }  // end of CurrentTime

    private String Today() {

        String nowDate = new SimpleDateFormat("MM/dd").format(new Date());  // 取得目前的日期
        assert nowDate != null;

        return nowDate  ;   // 傳回目前的時間

    }   // end of

    @Override
    public void finish() {

        super.finish();

        overridePendingTransition(R.anim.activity_bottom_in, R.anim.activity_bottom_out);  // fade in/out  的特效

    }  // end of finish

    private static String[] permissionsList = {
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.RECEIVE_BOOT_COMPLETED,
            android.Manifest.permission.POST_NOTIFICATIONS,
            android.Manifest.permission.USE_FULL_SCREEN_INTENT,
            android.Manifest.permission.FOREGROUND_SERVICE,
            android.Manifest.permission.FOREGROUND_SERVICE_CAMERA,
            android.Manifest.permission.FOREGROUND_SERVICE_MICROPHONE,
            android.Manifest.permission.FOREGROUND_SERVICE_PHONE_CALL,
            android.Manifest.permission.FOREGROUND_SERVICE_DATA_SYNC,
            android.Manifest.permission.READ_MEDIA_IMAGES,
            android.Manifest.permission.READ_MEDIA_AUDIO,
            android.Manifest.permission.READ_MEDIA_VIDEO,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.MANAGE_DEVICE_POLICY_MICROPHONE,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.MODIFY_AUDIO_SETTINGS
    } ;

    private void EnableApplicationPermission() {

        Log.d("qaz","EnableApplicationPermission()");

        ActivityCompat.requestPermissions(LandscapeDetails.this, permissionsList, 1);

    }

    public boolean isPNGFileExists(String filename, Context context) {

        // 確保外部儲存空間是可用的

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // 構建文件路徑

            File externalStorageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

            Log.d("qaz", " LandscapeDetails FilePath : " + externalStorageDir.getAbsolutePath());

            File file = new File(externalStorageDir, filename);

            Log.d("qaz", "LandscapeDetails 檔案全名:" + file.getAbsolutePath().toString());

            // 檢查文件是否存在並且是 PNG 文件

            return file.exists();  // 存在: true
        }

        return false;
    }   // end of fucntion


    Bitmap ConvertPngToBitmap(Context mContext , String filename) {

        Log.d("qaz", "檔案已經在外部儲存記憶體");

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // 構建文件路徑

            File externalStorageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

            Log.d("qaz", " Png 檔's FilePath : " + externalStorageDir.getAbsolutePath());

            File file = new File(externalStorageDir, filename);   //  景點小圖

            Log.d("qaz", "路徑加檔案全名:" + file.getAbsolutePath().toString());

            if (file.exists()) {

                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                if (bitmap != null)
                    Log.d("qaz", filename + "文件存在並轉為bitmap 成功");
                else
                    Log.d("qaz", filename + "轉換bitmap 失敗");

                // 接者要將 bitmap 放入 imageview 中
                // LocationMap.setImageBitmap(bitmap);  // show the bitmap in imageview

                return bitmap;

            } else {
                // 文件不存在的处理

                Log.d("qaz", "不存在");
            }

        }

        return null ;   // if bitmap convert is failed !
    }

}