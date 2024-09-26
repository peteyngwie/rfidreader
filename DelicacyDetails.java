package com.smartcity;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartcity.cgs.GridViewAdapter;
import com.smartcity.cgs.LandscapeGVAdapter;
import com.smartcity.cgs.LandscapeModel;
import com.smartcity.cgs.R;
import com.smartcity.cgs.SideSpinner;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class DelicacyDetails extends AppCompatActivity {

    TextView DateTxt ;                   //  Day/Time

    Timer Clocktimer = new Timer() ;     // 計時器 for flushing time per 0.5 second

    String title, time , address , distance , descriptions ;
    int whichone ;

    private TextView Title , SubTitleAddress , Address,  Time , Distance  , Description ;
    private TextView CurrentAddressTxt ;
    private View viewline;
    private ImageView PeopleWalking , Location ;

    private ImageView LocationMap ;    // landscape's location map

    GridView LandscapeGV;   // gridview of landscapes

    private File file ;
    ArrayList<Bitmap> bitmapList = new ArrayList<>();  // 這裡宣告一個 bitmaplist array

    private Context mContext ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delicacy_details);

        mContext = DelicacyDetails.this ;


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

        SubTitleAddress = (TextView) findViewById(R.id.subtitleaddresstxt) ;       // subtitle address
        Time = (TextView) findViewById(R.id.timetxt) ;                             // time
        Distance    = (TextView) findViewById(R.id.distancetxt);                      // disatnce
        Description = (TextView) findViewById(R.id.accommodationdescriptiontxt) ;  // description

        // animation vertical scrolling
        // Description.setMovementMethod(ScrollingMovementMethod.getInstance());  // 垂直捲動

        CurrentAddressTxt = (TextView) findViewById(R.id.currentaddresstxt);  // current address text
        viewline = (View) findViewById(R.id.viewline) ;

        viewline.setBackgroundColor(0x88888888);   // grey color line

        // Address = (TextView) findViewById(R.id.addresssmalltxt) ;     // cardview address

        Bundle bundle = getIntent().getExtras();    // 取出 intent 中的 bundle

        // 拆出bundle的內容，key為content
        // 取出 景點抬頭 , 時間 , 位址 , 距離

        title        = bundle.getString("title");   // title
        time         = bundle.getString("time");    // time
        address      = bundle.getString("address") ;    // address
        distance     = bundle.getString("distance") ;   // disatnce
        descriptions = bundle.getString("description");       // descriptions
        whichone = bundle.getInt("position");       // which one

        assert title != null && time != null && address != null && distance != null ;

        Log.d(TAG,"名稱 " + title.toString() ) ;
        Log.d(TAG,"時間 " + time.toString() )  ;
        Log.d(TAG,"地址 " + address.toString() )  ;
        Log.d(TAG,"距離 " + distance.toString() )  ;
        Log.d(TAG,"描述 " + descriptions.toString());

        updateTime();                        // 更新時間  per 0.5 second

        LandscapeGV = findViewById(R.id.idGVcourses);
        ArrayList<LandscapeModel> courseModelArrayList = new ArrayList<LandscapeModel>();

        LocationMap = findViewById(R.id.mainamp) ;

        int linecount ;


        switch (whichone) {

            case 0 :   /* 一間茶屋 */

                // 第一項    subitems's image

                // 對應的圖示: travelImgList17-18.png , 由左17至右18

                // 首先,要清空 bitmap array list

               bitmapList.clear();   // clear all elements in array list

                file = new File(Environment.getExternalStorageDirectory(), "/Android/data/com.smartcity.cgs/files/Pictures/travelImgList17.png");

                if (file.exists()) {
                    // 文件存在
                    Log.d("iii", "travelImgList17.png 存在");

                    Bitmap bitmap  = ConvertPngToBitmap(mContext,"travelImgList17.png") ;
                    bitmapList.add(bitmap);   // add a bitmap that has been converted - travelImgList1.png

                } else {
                    // 文件不存在
                    Log.d("iii", "travelImgList17.png 不存在");

                }

                // travelImgList16.png
                file = new File(Environment.getExternalStorageDirectory(), "/Android/data/com.smartcity.cgs/files/Pictures/travelImgList16.png");

                if (file.exists()) {
                    // 文件存在
                    Log.d("iii", "travelImgList16.png 存在");

                    Bitmap bitmap  = ConvertPngToBitmap(mContext,"travelImgList16.png") ;
                    bitmapList.add(bitmap);   // add a bitmap that has been converted - travelImgList2.png
                } else {
                    // 文件不存在
                    Log.d("iii", "travelImgList16.png 不存在");

                }

                // travelImgList3.png
                file = new File(Environment.getExternalStorageDirectory(), "/Android/data/com.smartcity.cgs/files/Pictures/travelImgList18.png");

                if (file.exists()) {
                    // 文件存在
                    Log.d("iii", "travelImgList18.png 存在");

                    Bitmap bitmap  = ConvertPngToBitmap(mContext,"travelImgList18.png") ;
                    bitmapList.add(bitmap);   // Add a bitmap that has been converted - travelImgList3.png

                } else {
                    // 文件不存在
                    Log.d("qaz", "travelImgList18.png 不存在");

                }

                file = new File(Environment.getExternalStorageDirectory(), "/Android/data/com.smartcity.cgs/files/Pictures/cgsTravelList_1_5.png");

                if (file.exists()) {
                    // 文件存在
                    Log.d("jkl", "cgsTravelList_1_5.png 存在");

                    Bitmap bitmap  = ConvertPngToBitmap(mContext,"cgsTravelList_1_5.png") ;
                    // bitmapList.add(bitmap);   // add a bitmap that has been converted - travelImgList1.png
                    LocationMap.setImageBitmap(bitmap);  // 地圖主圖

                } else {
                    // 文件不存在
                    Log.d("qaz", "cgsTravelList_1_5.png不存在");

                }

               // courseModelArrayList.add(new LandscapeModel(R.drawable.food11));   // 第一
               // courseModelArrayList.add(new LandscapeModel(R.drawable.food12));   // 第二
               // courseModelArrayList.add(new LandscapeModel(R.drawable.food15));   // 第三
                // LocationMap.setImageDrawable(getResources().getDrawable( R.drawable.foodloc1 ));   // 第一旅館地圖

                Title.setText(title);                // 設定名稱
                SubTitleAddress.setText(address);    // 地址
                Time.setText(time);                  // 時間
                Distance.setText(distance);          // 距離
                Description.setText(descriptions+"\r\n");   // 描述
                linecount = Description.getLineCount() ;    // 計算目前
                Description.setMovementMethod(ScrollingMovementMethod.getInstance());   // 設置垂直滾動

                if (linecount > 2 ) {
                    int offset = Description.getLineCount() * Description.getLineHeight()  ;
                    Description.scrollTo(0, offset - Description.getHeight() + Description.getLineHeight());
                }

                CurrentAddressTxt.setText(address);  // 目前位置

                break ;
            case 1 :


                bitmapList.clear();   // clear all elements in array list

                file = new File(Environment.getExternalStorageDirectory(), "/Android/data/com.smartcity.cgs/files/Pictures/travelImgList19.png");

                if (file.exists()) {
                    // 文件存在
                    Log.d("iii", "travelImgList19.png 存在");

                    Bitmap bitmap  = ConvertPngToBitmap(mContext,"travelImgList19.png") ;
                    bitmapList.add(bitmap);   // add a bitmap that has been converted - travelImgList1.png

                } else {
                    // 文件不存在
                    Log.d("iii", "travelImgList19.png 不存在");

                }

                // travelImgList16.png
                file = new File(Environment.getExternalStorageDirectory(), "/Android/data/com.smartcity.cgs/files/Pictures/travelImgList20.png");

                if (file.exists()) {
                    // 文件存在
                    Log.d("iii", "travelImgList20.png 存在");

                    Bitmap bitmap  = ConvertPngToBitmap(mContext,"travelImgList20.png") ;
                    bitmapList.add(bitmap);   // add a bitmap that has been converted - travelImgList2.png
                } else {
                    // 文件不存在
                    Log.d("iii", "travelImgList20.png 不存在");

                }

                // travelImgList3.png
                file = new File(Environment.getExternalStorageDirectory(), "/Android/data/com.smartcity.cgs/files/Pictures/travelImgList21.png");

                if (file.exists()) {
                    // 文件存在
                    Log.d("iii", "travelImgList21.png 存在");

                    Bitmap bitmap  = ConvertPngToBitmap(mContext,"travelImgList21.png") ;
                    bitmapList.add(bitmap);   // Add a bitmap that has been converted - travelImgList3.png

                } else {
                    // 文件不存在
                    Log.d("qaz", "travelImgList21.png 不存在");

                }

                file = new File(Environment.getExternalStorageDirectory(), "/Android/data/com.smartcity.cgs/files/Pictures/cgsTravelList_1_6.png");

                if (file.exists()) {
                    // 文件存在
                    Log.d("ggg", "cgsTravelList_1_6.png 存在");

                    Bitmap bitmap  = ConvertPngToBitmap(mContext,"cgsTravelList_1_6.png") ;
                    // bitmapList.add(bitmap);   // add a bitmap that has been converted - travelImgList1.png
                    LocationMap.setImageBitmap(bitmap);  // 地圖主圖

                } else {
                    // 文件不存在
                    Log.d("qaz", "cgsTravelList_1_6.png不存在");

                }


                //courseModelArrayList.add(new LandscapeModel(R.drawable.hotel2_1));   // 第一
                //courseModelArrayList.add(new LandscapeModel(R.drawable.hotel2_2));   // 第二
                //courseModelArrayList.add(new LandscapeModel(R.drawable.hotel2_3));   // 第三

                LocationMap.setImageDrawable(getResources().getDrawable( R.drawable.foodloc2 ));   // 第二旅館地圖

                Title.setText(title);                // 設定名稱
                SubTitleAddress.setText(address);    // 地址
                Time.setText(time);                  // 時間
                Distance.setText(distance);          // 距離
                Description.setText(descriptions+"\r\n");   // 描述
                linecount = Description.getLineCount() ;    // 計算目前
                Description.setMovementMethod(ScrollingMovementMethod.getInstance());   // 設置垂直滾動

                if (linecount > 2 ) {
                    int offset = Description.getLineCount() * Description.getLineHeight()  ;
                    Description.scrollTo(0, offset - Description.getHeight() + Description.getLineHeight());
                }

                CurrentAddressTxt.setText(address);  // 目前位置
                break ;

            case 2 :

                courseModelArrayList.add(new LandscapeModel(R.drawable.fufei2));   // 第一
                courseModelArrayList.add(new LandscapeModel(R.drawable.fufei3));   // 第三
                courseModelArrayList.add(new LandscapeModel(R.drawable.fufei4));   // 第三

                LocationMap.setImageDrawable(getResources().getDrawable( R.drawable.foodloc3 ));   // 第三旅館地圖

                Title.setText(title);                // 設定名稱
                SubTitleAddress.setText(address);    // 地址
                Time.setText(time);                  // 時間
                Distance.setText(distance);          // 距離
                Description.setText(descriptions);   // 描述
                Description.setMovementMethod(ScrollingMovementMethod.getInstance());   // 設置垂直滾動
                Description.setText(descriptions+"\r\n");   // 描述
                linecount = Description.getLineCount() ;    // 計算目前
                Description.setMovementMethod(ScrollingMovementMethod.getInstance());   // 設置垂直滾動

                if (linecount > 2 ) {
                    int offset = Description.getLineCount() * Description.getLineHeight()  ;
                    Description.scrollTo(0, offset - Description.getHeight() + Description.getLineHeight());
                }

                CurrentAddressTxt.setText(address);  // 目前位置
                break ;
            case 3 :

                courseModelArrayList.add(new LandscapeModel(R.drawable.lawa2));   // 第一
                courseModelArrayList.add(new LandscapeModel(R.drawable.lawa3));   // 第三
                courseModelArrayList.add(new LandscapeModel(R.drawable.lawa4));   // 第三

                LocationMap.setImageDrawable(getResources().getDrawable( R.drawable.foodloc4 ));   // 第四景點地圖

                Title.setText(title);                // 設定名稱
                SubTitleAddress.setText(address);    // 地址
                Time.setText(time);                  // 時間
                Distance.setText(distance);          // 距離
                Description.setText(descriptions);   // 描述
                Description.setMovementMethod(ScrollingMovementMethod.getInstance());   // 設置垂直滾動

                Description.setText(descriptions+"\r\n");   // 描述
                linecount = Description.getLineCount() ;    // 計算目前
                Description.setMovementMethod(ScrollingMovementMethod.getInstance());   // 設置垂直滾動

                if (linecount > 2 ) {
                    int offset = Description.getLineCount() * Description.getLineHeight()  ;
                    Description.scrollTo(0, offset - Description.getHeight() + Description.getLineHeight());
                }
                CurrentAddressTxt.setText(address);  // 目前位置
                break ;
            case 4 :

                courseModelArrayList.add(new LandscapeModel(R.drawable.teahouse1));   // 第一
                courseModelArrayList.add(new LandscapeModel(R.drawable.teahouse3));   // 第二
                courseModelArrayList.add(new LandscapeModel(R.drawable.teahouse5));   // 第三

                LocationMap.setImageDrawable(getResources().getDrawable( R.drawable.foodloc5 ));   // 第五景點地圖

                Title.setText(title);                // 設定名稱
                SubTitleAddress.setText(address);    // 地址
                Time.setText(time);                  // 時間
                Distance.setText(distance);          // 距離
                Description.setText(descriptions);   // 描述

                Description.setMovementMethod(ScrollingMovementMethod.getInstance());   // 設置垂直滾動
                Description.setText(descriptions+"\r\n");   // 描述
                linecount = Description.getLineCount() ;    // 計算目前
                Description.setMovementMethod(ScrollingMovementMethod.getInstance());   // 設置垂直滾動

                if (linecount > 2 ) {
                    int offset = Description.getLineCount() * Description.getLineHeight()  ;
                    Description.scrollTo(0, offset - Description.getHeight() + Description.getLineHeight());
                }

                CurrentAddressTxt.setText(address);  // 目前位置

                break ;
            case 5 :

                courseModelArrayList.add(new LandscapeModel(R.drawable.antik2));    // 第一
                courseModelArrayList.add(new LandscapeModel(R.drawable.antik3));    // 第二
                courseModelArrayList.add(new LandscapeModel(R.drawable.antik4));    // 第三


                LocationMap.setImageDrawable(getResources().getDrawable( R.drawable.foodloc6 ));   // 第六景點地圖

                Title.setText(title);                // 設定名稱
                SubTitleAddress.setText(address);    // 地址
                Time.setText(time);                  // 時間
                Distance.setText(distance);          // 距離
                Description.setText(descriptions);   // 描述
                Description.setMovementMethod(ScrollingMovementMethod.getInstance());   // 設置垂直滾動

                Description.setText(descriptions+"\r\n");   // 描述
                linecount = Description.getLineCount() ;    // 計算目前
                Description.setMovementMethod(ScrollingMovementMethod.getInstance());   // 設置垂直滾動

                if (linecount > 2 ) {
                    int offset = Description.getLineCount() * Description.getLineHeight()  ;
                    Description.scrollTo(0, offset - Description.getHeight() + Description.getLineHeight());
                }

                CurrentAddressTxt.setText(address);  // 目前位置
                break ;
            case 6 :

                courseModelArrayList.add(new LandscapeModel(R.drawable.annaking2));   // 第一
                courseModelArrayList.add(new LandscapeModel(R.drawable.annaking3));   // 第二
                courseModelArrayList.add(new LandscapeModel(R.drawable.annaking4));   // 第三

                // LocationMap.setImageDrawable(getResources().getDrawable( R.drawable.map7 ));   // 第七景點地圖

                LocationMap.setImageDrawable(getResources().getDrawable( R.drawable.foodloc7 ));   // 第七景點地圖


                Title.setText(title);                // 設定名稱
                SubTitleAddress.setText(address);    // 地址
                Time.setText(time);                  // 時間
                Distance.setText(distance);          // 距離
                Description.setText(descriptions);   // 描述
                Description.setMovementMethod(ScrollingMovementMethod.getInstance());   // 設置垂直滾動

                Description.setText(descriptions+"\r\n");   // 描述
                linecount = Description.getLineCount() ;    // 計算目前
                Description.setMovementMethod(ScrollingMovementMethod.getInstance());   // 設置垂直滾動

                if (linecount > 2 ) {
                    int offset = Description.getLineCount() * Description.getLineHeight()  ;
                    Description.scrollTo(0, offset - Description.getHeight() + Description.getLineHeight());
                }

                CurrentAddressTxt.setText(address);  // 目前位置
                break ;
            case 7 :



                courseModelArrayList.add(new LandscapeModel(R.drawable.journey1));   // 第一
                courseModelArrayList.add(new LandscapeModel(R.drawable.journey2));   // 第二
                courseModelArrayList.add(new LandscapeModel(R.drawable.journey3));   // 第三

                // LocationMap.setImageDrawable(getResources().getDrawable( R.drawable.map8 ));   // 第八景點地圖


                LocationMap.setImageDrawable(getResources().getDrawable( R.drawable.foodloc8 ));   // 第八旅館地圖

                Title.setText(title);                // 設定名稱
                SubTitleAddress.setText(address);    // 地址
                Time.setText(time);                  // 時間
                Distance.setText(distance);          // 距離
                Description.setText(descriptions);   // 描述
                Description.setMovementMethod(ScrollingMovementMethod.getInstance());   // 設置垂直滾動
                Description.setText(descriptions+"\r\n");   // 描述
                linecount = Description.getLineCount() ;    // 計算目前
                Description.setMovementMethod(ScrollingMovementMethod.getInstance());   // 設置垂直滾動

                if (linecount > 2 ) {
                    int offset = Description.getLineCount() * Description.getLineHeight()  ;
                    Description.scrollTo(0, offset - Description.getHeight() + Description.getLineHeight());
                }

                CurrentAddressTxt.setText(address);  // 目前位置
                break ;
            case 8 :



                courseModelArrayList.add(new LandscapeModel(R.drawable.southgraden3));   // 第一
                courseModelArrayList.add(new LandscapeModel(R.drawable.southgraden4));   // 第二
                courseModelArrayList.add(new LandscapeModel(R.drawable.southgraden5));   // 第三

                // LocationMap.setImageDrawable(getResources().getDrawable( R.drawable.map9 ));   // 第九景點地圖


                LocationMap.setImageDrawable(getResources().getDrawable( R.drawable.foodloc9 ));   // 第九旅館地圖

                Title.setText(title);                // 設定名稱
                SubTitleAddress.setText(address);    // 地址
                Time.setText(time);                  // 時間
                Distance.setText(distance);          // 距離
                Description.setText(descriptions);   // 描述
                Description.setMovementMethod(ScrollingMovementMethod.getInstance());   // 設置垂直滾動

                Description.setText(descriptions+"\r\n");   // 描述
                linecount = Description.getLineCount() ;    // 計算目前
                Description.setMovementMethod(ScrollingMovementMethod.getInstance());   // 設置垂直滾動

                if (linecount > 2 ) {
                    int offset = Description.getLineCount() * Description.getLineHeight()  ;
                    Description.scrollTo(0, offset - Description.getHeight() + Description.getLineHeight());
                }

                CurrentAddressTxt.setText(address);  // 目前位置
                break;
            case 9 :

                LocationMap.setImageDrawable(getResources().getDrawable( R.drawable.foodloc10));   // 第九旅館地圖
                courseModelArrayList.add(new LandscapeModel(R.drawable.yuhtong1));   // 第一
                courseModelArrayList.add(new LandscapeModel(R.drawable.yuhtong3));   // 第二
                courseModelArrayList.add(new LandscapeModel(R.drawable.yuhtong4));   // 第三

                // LocationMap.setImageDrawable(getResources().getDrawable( R.drawable.map10));   // 第十景點地圖
                LocationMap.setImageDrawable(getResources().getDrawable( R.drawable.foodloc10 ));   // 第十美食地圖

                Title.setText(title);                // 設定名稱
                SubTitleAddress.setText(address);    // 地址
                Time.setText(time);                  // 時間
                Distance.setText(distance);          // 距離
                Description.setText(descriptions);   // 描述
                Description.setMovementMethod(ScrollingMovementMethod.getInstance());   // 設置垂直滾動

                Description.setText(descriptions+"\r\n");   // 描述
                linecount = Description.getLineCount() ;    // 計算目前
                Description.setMovementMethod(ScrollingMovementMethod.getInstance());   // 設置垂直滾動

                if (linecount > 2 ) {
                    int offset = Description.getLineCount() * Description.getLineHeight()  ;
                    Description.scrollTo(0, offset - Description.getHeight() + Description.getLineHeight());
                }

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

        // LandscapeGVAdapter adapter = new LandscapeGVAdapter (this, courseModelArrayList);
        // LandscapeGV.setAdapter(adapter);

        int itemWidth = 320;    //  320 pixels : width
        int itemHeight = 205;   //  205 pixels : height
        GridViewAdapter adapter = new GridViewAdapter(this, bitmapList ,itemWidth ,  itemHeight);
        LandscapeGV.setAdapter(adapter);

    }

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