package com.smartcity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import com.smartcity.cgs.GridViewAdapter;
import com.smartcity.cgs.LandscapeDetails;
import com.smartcity.cgs.LandscapeGVAdapter;
import com.smartcity.cgs.LandscapeModel;
import com.smartcity.cgs.R;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartcity.cgs.SideSpinner;
import com.smartcity.item;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class AccommodationDetails extends AppCompatActivity {

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
        setContentView(R.layout.activity_accommodation_details);

        mContext = AccommodationDetails.this ;

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

        SubTitleAddress = (TextView) findViewById(R.id.subtitleaddresstxt) ;          // subtitle address
        Time = (TextView) findViewById(R.id.timetxt) ;                                // time
        Distance    = (TextView) findViewById(R.id.distancetxt);                      // disatnce
        Description = (TextView) findViewById(R.id.accommodationdescriptiontxt) ;     // description

        // animation vertical scrolling

        // Description.setMovementMethod(ScrollingMovementMethod.getInstance());  // 垂直捲動

        CurrentAddressTxt = (TextView) findViewById(R.id.currentaddresstxt);  // current address text
        viewline = (View) findViewById(R.id.viewline) ;

        viewline.setBackgroundColor(0x88888888);   // grey  line

        // Address = (TextView) findViewById(R.id.addresssmalltxt) ;     // cardview address

        Bundle bundle = getIntent().getExtras();    // 取出 intent 中的 bundle

        // 拆出bundle的內容，key為content
        // 取出 景點抬頭 , 時間 , 位址 , 距離

        title    = bundle.getString("title");   // title
        time     = bundle.getString("time");    // time
        address  = bundle.getString("address") ;    // address
        distance = bundle.getString("distance") ;   // disatnce
        descriptions = bundle.getString("description");       // descriptions
        whichone = bundle.getInt("position");       // which one

        assert title != null && time != null && address != null && distance != null ;

        Log.d(TAG,"名稱 " + title.toString() ) ;
        Log.d(TAG,"時間 " + time.toString() )  ;
        Log.d(TAG,"地址 " + address.toString() )  ;
        Log.d(TAG,"距離 " + distance.toString() )  ;
        Log.d(TAG,"描述 " + descriptions.toString());

        updateTime();                        // 更新時間  per 0.5 second

        LandscapeGV = findViewById(R.id.idGVcourses);  // 下面的景點顯示 (3個)
        ArrayList<LandscapeModel> courseModelArrayList = new ArrayList<LandscapeModel>();

        LocationMap = findViewById(R.id.mainamp) ;

        int linecount ;


        switch (whichone) {

            case 0 :

                // 第一項    subitems's image

                // 對應的圖示: travelImgList10-12.png , 由左11至右10

                // 首先,要清空 bitmap array list

                bitmapList.clear();   // clear all elements in array list

                // travelImgList1.png
                file = new File(Environment.getExternalStorageDirectory(), "/Android/data/com.smartcity.cgs/files/Pictures/travelImgList11.png");

                if (file.exists()) {
                    // 文件存在
                    Log.d("iii", "travelImgList11.png 存在");

                    Bitmap bitmap  = ConvertPngToBitmap(mContext,"travelImgList11.png") ;
                    bitmapList.add(bitmap);   // add a bitmap that has been converted - travelImgList1.png - 加第一個景點

                } else {
                    // 文件不存在
                    Log.d("iii", "travelImgList11.png 不存在");

                }

                // travelImgList2.png
                file = new File(Environment.getExternalStorageDirectory(), "/Android/data/com.smartcity.cgs/files/Pictures/travelImgList12.png");

                if (file.exists()) {
                    // 文件存在
                    Log.d("iii", "travelImgList12.png 存在");

                    Bitmap bitmap  = ConvertPngToBitmap(mContext,"travelImgList12.png") ;
                    bitmapList.add(bitmap);   // add a bitmap that has been converted - travelImgList2.png - 加第二個景點
                } else {
                    // 文件不存在
                    Log.d("iii", "travelImgList12.png 不存在");

                }

                // travelImgList3.png
                file = new File(Environment.getExternalStorageDirectory(), "/Android/data/com.smartcity.cgs/files/Pictures/travelImgList10.png");

                if (file.exists()) {
                    // 文件存在
                    Log.d("iii", "travelImgList10.png 存在");

                    Bitmap bitmap  = ConvertPngToBitmap(mContext,"travelImgList10.png") ;
                    bitmapList.add(bitmap);   // Add a bitmap that has been converted - travelImgList3.png - 加第三 個景點

                } else {
                    // 文件不存在
                    Log.d("qaz", "travelImgList10.png 不存在");

                }

                // the above have been converted

                // courseModelArrayList.add(new LandscapeModel(R.drawable.hotel1_1));  // 第一
                // courseModelArrayList.add(new LandscapeModel(R.drawable.hotel1_2));  // 第二
                // courseModelArrayList.add(new LandscapeModel(R.drawable.hotel1_3));   // 第三

                /// 地圖 - 一間茶屋

                file = new File(Environment.getExternalStorageDirectory(), "/Android/data/com.smartcity.cgs/files/Pictures/cgsTravelList_1_3.png");

                if (file.exists()) {
                    // 文件存在
                    Log.d("qaz", "cgsTravelList_1_3.png 存在");

                    Bitmap bitmap  = ConvertPngToBitmap(mContext,"cgsTravelList_1_3.png") ;
                    // bitmapList.add(bitmap);   // add a bitmap that has been converted - travelImgList1.png
                    LocationMap.setImageBitmap(bitmap);  // 地圖主圖

                } else {
                    // 文件不存在
                    Log.d("qaz", "cgsTravelList_1_3.png不存在");

                }

                // LocationMap.setImageDrawable(getResources().getDrawable( R.drawable.hotelmap1 ));   // 第一旅館地圖

                // 下面要由 cgsTravlList 中抽出第一張圖 - cgsTravelList_1_1.png  ( 景點主圖 - 北門火車站 )

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


                // 首先,要清空 bitmap array list

                bitmapList.clear();   // clear all elements in array list

                file = new File(Environment.getExternalStorageDirectory(), "/Android/data/com.smartcity.cgs/files/Pictures/travelImgList13.png");


                if (file.exists()) {
                    // 文件存在
                    Log.d("iii", "travelImgList13.png 存在");

                    Bitmap bitmap  = ConvertPngToBitmap(mContext,"travelImgList13.png") ;
                    bitmapList.add(bitmap);   // add a bitmap that has been converted - travelImgList13.png

                } else {
                    // 文件不存在
                    Log.d("iii", "travelImgList13.png 不存在");

                }

                // travelImgList14.png
                file = new File(Environment.getExternalStorageDirectory(), "/Android/data/com.smartcity.cgs/files/Pictures/travelImgList14.png");

                if (file.exists()) {
                    // 文件存在
                    Log.d("iii", "travelImgList14.png 存在");

                    Bitmap bitmap  = ConvertPngToBitmap(mContext,"travelImgList14.png") ;
                    bitmapList.add(bitmap);   // add a bitmap that has been converted - travelImgList2.png
                } else {
                    // 文件不存在
                    Log.d("iii", "travelImgList14.png 不存在");

                }

                // travelImgList3.png
                file = new File(Environment.getExternalStorageDirectory(), "/Android/data/com.smartcity.cgs/files/Pictures/travelImgList15.png");

                if (file.exists()) {
                    // 文件存在
                    Log.d("iii", "travelImgList15.png 存在");

                    Bitmap bitmap  = ConvertPngToBitmap(mContext,"travelImgList15.png") ;
                    bitmapList.add(bitmap);   // Add a bitmap that has been converted - travelImgList3.png

                } else {
                    // 文件不存在
                    Log.d("iii", "travelImgList15.png 不存在");

                }


                // ///// 這裡不需要 - 每一個 item 都只有 3個 圖示

                // 對應的圖示: travelImgLis24-22.png , 由左至右

                // travelImgList24.png
                file = new File(Environment.getExternalStorageDirectory(), "/Android/data/com.smartcity.cgs/files/Pictures/cgsTravelList_1_4.png");

                if (file.exists()) {
                    // 文件存在
                    Log.d("qaz", "cgsTravelList_1_4.png 存在");

                    Bitmap bitmap  = ConvertPngToBitmap(mContext,"cgsTravelList_1_4.png") ;
                    //bitmapList.add(bitmap);   // add a bitmap that has been converted - travelImgList1.png

                } else {
                    // 文件不存在
                    Log.d("qaz", "cgsTravelList_1_4.png 不存在");

                }

                // courseModelArrayList.add(new LandscapeModel(R.drawable.hotel2_1));   // 第一
                // courseModelArrayList.add(new LandscapeModel(R.drawable.hotel2_2));   // 第二
                // courseModelArrayList.add(new LandscapeModel(R.drawable.hotel2_3));   // 第三

                LocationMap.setImageDrawable(getResources().getDrawable( R.drawable.hotelmap2 ));   // 第二旅館地圖

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

                LocationMap.setImageDrawable(getResources().getDrawable( R.drawable.hotelmap3 ));   // 第三旅館地圖

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

                LocationMap.setImageDrawable(getResources().getDrawable( R.drawable.hotelmap4 ));   // 第四景點地圖

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

                LocationMap.setImageDrawable(getResources().getDrawable( R.drawable.hotelmap5 ));   // 第五景點地圖

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

                LocationMap.setImageDrawable(getResources().getDrawable( R.drawable.hotelmap6 ));   // 第六景點地圖

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

                LocationMap.setImageDrawable(getResources().getDrawable( R.drawable.map7 ));   // 第七景點地圖



                LocationMap.setImageDrawable(getResources().getDrawable( R.drawable.hotelmap7 ));   // 第七景點地圖


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

                LocationMap.setImageDrawable(getResources().getDrawable( R.drawable.map8 ));   // 第八景點地圖


                LocationMap.setImageDrawable(getResources().getDrawable( R.drawable.hotelmap8 ));   // 第八旅館地圖

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

                LocationMap.setImageDrawable(getResources().getDrawable( R.drawable.map9 ));   // 第九景點地圖


                LocationMap.setImageDrawable(getResources().getDrawable( R.drawable.hotelmap9 ));   // 第九旅館地圖

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

                LocationMap.setImageDrawable(getResources().getDrawable( R.drawable.hotelmap10));   // 第九旅館地圖
                courseModelArrayList.add(new LandscapeModel(R.drawable.yuhtong1));   // 第一
                courseModelArrayList.add(new LandscapeModel(R.drawable.yuhtong3));   // 第二
                courseModelArrayList.add(new LandscapeModel(R.drawable.yuhtong4));   // 第三

                // LocationMap.setImageDrawable(getResources().getDrawable( R.drawable.map10));   // 第十景點地圖

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


        // GridViewAdapter adapter = new GridViewAdapter(this, bitmapList);
        // LandscapeGV.setAdapter(adapter);  // 將 adapter 加入 gridview 中

        // setListViewHeightBasedOnChildren(LandscapeGV);
        //  adapter.notifyDataSetChanged();

        // 每個 Grid 項的寬高，單位像素
        int itemWidth  = 320;     //  320 pixels : width
        int itemHeight = 205;     //  205 pixels : height


        // ArrayList<Bitmap> bitmapList = new ArrayList<>();

        // 初始化你的 bitmapList，加入 Bitmap 圖

        Log.d("tyu","bitmap的長度 :"  + bitmapList.size()) ;


        GridViewAdapter adapter = new GridViewAdapter(this, bitmapList, itemWidth, itemHeight);
        LandscapeGV.setAdapter(adapter);


    }   // end of onCreate


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


    public class MoreTextUtil {

        public static boolean hasMesure = false;    //是否已经执行过一次

        public static void setMore(TextView textV, String content) {
            textV.setText(content);
            setMore(textV, "...", "查看更多");
        }

        public static void setMore(TextView textV) {
            setMore(textV, "...", "查看更多");
        }

        @SuppressLint("NewApi")
        public static void setMore(final TextView textV, final String ellipsis, final String strmore) {
            if (textV == null) {
                return;
            }
            if (2147483647 == textV.getMaxLines()) textV.setMaxLines(5);
            textV.setEllipsize(TextUtils.TruncateAt.END);
            textV.setVerticalScrollBarEnabled(true);

            hasMesure = false;

            //添加布局变化监听器,view 布局完成时调用，每次view改变时都会调用
            ViewTreeObserver vto = textV.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (!hasMesure) {
                        int maxLines = textV.getMaxLines();
                        int lines = textV.getLineCount();

                        //如果文字的行数超过最大行数，展示缩略的textview

                        if (lines >= maxLines) {
                            Layout layout = textV.getLayout();
                            String str = layout.getText().toString();
                            int end = layout.getLineEnd(maxLines - 2);
                            str = str.substring(0, end);                    //缩略的文字
                            String strall = textV.getText().toString();     //完整的文字
                            hasMesure = true;

                            SpannableString spanstr;
                            //如果以换行符结尾，则不再换行
                            if (str.endsWith("\n")) {
                                spanstr = new SpannableString(str + ellipsis + strmore);
                            } else {
                                spanstr = new SpannableString(str + "\n" + ellipsis + strmore);
                            }

                            //设置“查看更多”的点击事件
                            spanstr.setSpan(new MyClickableSpan(strall, textV.getResources().getColor(android.R.color.holo_green_dark)), spanstr.length() - strmore.length(),
                                    spanstr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                            textV.setText(spanstr);
                            //移除默认背景色
                            textV.setHighlightColor(textV.getResources().getColor(android.R.color.transparent));
                            textV.setMovementMethod(LinkMovementMethod.getInstance());
                        }
                    }
                }
            });

        }

        static class MyClickableSpan extends ClickableSpan {
            private String str;
            private int color;

            public MyClickableSpan(String str, int color) {
                this.str = str;
                this.color = color;
            }

            @Override
            public void onClick(View view) {
                ((TextView) view).setMovementMethod(new ScrollingMovementMethod());
                ((TextView) view).setText(str);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(color);         //设置“查看更多”字体颜色
                ds.setUnderlineText(false); //设置“查看更多”无下划线，默认有
                ds.clearShadowLayer();
            }
        }
    }   // end of MoreTextUtil class

    public static void setListViewHeightBasedOnChildren(GridView listView) {
        // 获取listview的adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        // 固定列宽，有多少列
        int col = 3;// listView.getNumColumns();
        int totalHeight = 0;

        // i每次加4，相当于listAdapter.getCount()小于等于4时 循环一次，计算一次item的高度，
        // listAdapter.getCount()小于等于8时计算两次高度相加

        for (int i = 0; i < listAdapter.getCount(); i += col) {
            // 获取listview的每一个item
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            // 获取item的高度和
            totalHeight += listItem.getMeasuredHeight();
        }

        // 获取listview的布局参数
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        Log.d("sss", "高度:" + params.height) ; 
        // 设置高度
        params.height = totalHeight;
        // 设置margin
        ((ViewGroup.MarginLayoutParams) params).setMargins(10, 10, 10, 10);
        // 设置参数
        listView.setLayoutParams(params);
    }

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

}  // end of  AccommodationDetails