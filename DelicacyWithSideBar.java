package com.smartcity.cgs;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartcity.CustomerAdapterDelicacy;
import com.smartcity.SpacesItemDecoration;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DelicacyWithSideBar extends AppCompatActivity {

    TextView DateTxt ;                   //  Day/Time

    Timer Clocktimer = new Timer() ;     // 計時器 for flushing time per 0.5 second

    RecyclerView recycler;
    List<Model> modelList;               // 美食資料串列
    RecyclerView.Adapter adapter;



    private int howmanyitems ;

    ImageView BackToPrevious;    // 回前頁
    ImageView BackToHome;       // 回到首頁

    private ImageView LocationMap ;  // location map imageview

    private Bitmap bitmapMainMap;

    Context mContext ;
    private SQLiteDatabase db;   // database
    private MyDatabaseHelper myDatabaseHelper;  // db helper


    // information of Delicacy

    String[] TitleNamesList = {
            "Second Floor 貳樓嘉義秀泰店" ,
            "一間茶屋" ,
            "大老闆手作披薩" ,
            "檜樂食堂" ,
            "花淺蔥 Brunch Osteria" ,
            "國王的菜 King's Flavor" ,
            "樂閣" ,
            "水木町 板前料理" ,
            "就是要義大利麵" ,
            "旬樂 家庭料理" } ;    // 餐廳名稱

    ArrayList<String> TitleNamesListFromtravelList = new ArrayList<>();  // 美食抬頭 ( get them from travelList table )
    String[] TitleNamesStrArrayFromcgstravelList;  // title names

    String[] TimeList = {
            "從這步行 5 分鐘到達"   ,
            "從這步行 6 分鐘到達"   ,
            "從這步行 8 分鐘到達"   ,
            "從這步行 10 分鐘到達"  ,
            "從這步行 10 分鐘到達"  ,
            "從這步行 10 分鐘到達"  ,
            "從這步行 11 分鐘到達"  ,
            "從這步行 11 分鐘到達"  ,
            "從這步行 11 分鐘到達"  ,
            "從這步行 14 分鐘到達"  } ;   // 時間

    String[] WalkingTimeStrArrayFromcgstravelList;  // walking time

    ArrayList<String> TimeListFromcgstravelList = new ArrayList<>();  // 步行時間 ( get them from cgstravelList table )

    ArrayList<Integer> travelInfoIdList = new ArrayList<>() ;        // 用來存放 travelInfoId 之用

    String[] AddressList = {
            "600嘉義市西區文化路299號一樓" ,
            "600嘉義市東區共和路233號" ,
            "600嘉義市西區文化路389號",
            "600嘉義市東區共和路177號",
            "600嘉義市西區忠義街182號",
            "600嘉義市西區林森西路280號",
            "600嘉義市東區成仁街238號",
            "600嘉義市東區興中街172-9號",
            "600嘉義市東區長榮街97號",
            "600嘉義市東區共和路272號"
    };    // 地址


    ArrayList<String> AddressListFromtravelList = new ArrayList<>();       // 地址  ( get them from travelList table )
    String[] AddressesStrArrayFromcgstravelList;  //  addresses
    String[] DistanceList = {
            "距這裡 400 公尺",
            "距這裡 450 公尺",
            "距這裡 600 公尺",
            "距這裡 650 公尺",
            "距這裡 700 公尺",
            "距這裡 700 公尺",
            "距這裡 800 公尺",
            "距這裡 800 公尺",
            "距這裡 700 公尺",
            "距這裡 950 公尺"
    };

    String[] DistanceListFromcgstravelList;  // distance from here

    ArrayList<String> DistanceListFromcgsravelList = new ArrayList<>();       // 地址  ( get them from cgstravelList table )

    //   descriptions

    String[] DescriptionsList = {
            "美式餐廳，交通非常便利。",
            "裝潢繽紛的悠閒餐廳，供應豐盛的異國料理。",
            "薄餅餐廳，交通非常便利。",
            "台灣餐廳。" ,
            "薄餅餐廳，交通非常便利。",
            "印度餐廳",
            "裝潢繽紛的悠閒餐廳，供應豐盛的異國料理。",
            "日本餐廳",
            "意大利餐廳",
            "天婦羅餐廳"
    } ;


    ArrayList<String> DescriptionListFromtravelList = new ArrayList<>();  //  描述  ( get them from travelList table )

    String[] DescriptionsStrArrayFromtravelList;  // description from travellist

    int[] ImageList = {
            R.drawable.food1 ,
            R.drawable.food2,
            R.drawable.food3,
            R.drawable.food4 ,
            R.drawable.food5,
            R.drawable.food6,
            R.drawable.food7,
            R.drawable.food8,
            R.drawable.food9,
            R.drawable.food10 } ;     // 美食圖點

    int [] ImageListFromBitmapList  ;    // store bitmap files to show them

    Bitmap[] BitmapForPngArray ;

    List<Bitmap> bitmapList = new ArrayList<>();   // bitmap array - 用來儲存由外部記憶體中取出轉 png 成功的 bitmap 檔

    ArrayList<String> walkingTimeListFromcgsTravlList = new ArrayList<>();   // 步行時間

    ArrayList<String> DistanceListFromcgsTravlList = new ArrayList<>();     // 距離

    private ModelLayer modelLayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delicacy_with_side_bar);

        mContext = DelicacyWithSideBar.this ;

        myDatabaseHelper = new MyDatabaseHelper(mContext);
        db = myDatabaseHelper.getReadableDatabase();   // open database
        LocationMap = findViewById(R.id.mainamp);    // 主圖

        howmanyitems = 0 ;   // 初始化項目 - how many recyclerview's items

        modelLayer = ModelLayer.getModelLayer();


            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1); // 許可權申請

        // 下面是一個試驗 , 將 mainactivity 中的 存在 SharedPreferences 中的 sip 資料 取出來驗證是否正確

        SharedPreferences sharedPref = getSharedPreferences("siptable", Context.MODE_PRIVATE);
        String domain = sharedPref.getString("domain", "0.0.0.0"); // 若取不到, 就是 0.0.0.0 (default)
        String type1Number  = sharedPref.getString("type1Number", "1999"); // 若取不到, 就是 1999 (default)
        String type2Number  =  sharedPref.getString("type2Number", "1999"); // 若取不到, 就是 1999 (default)

        Log.d("vbn" , "doamin >>>>>" + domain) ;
        Log.d("vbn" , "type1Number >>>>>" + type1Number) ;
        Log.d("vbn" , "type2Number >>>>>" + type2Number) ;

        checkApplicationPermission();   // enable permission

        boolean FileExist = isPNGFileExists("cgsImgList3.png", mContext);   // 景點地理位置 主圖 (這個圖與住宿的相同 , 是錯的)

        if (FileExist == true) {

            Log.d("444", "檔案已經在外部儲存記憶體");

            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                // 構建文件路徑

                File externalStorageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

                Log.d("444", " LandscapeWithSideBar FilePath : " + externalStorageDir.getAbsolutePath());

                File file = new File(externalStorageDir, "cgsImgList3.png");   // 景點主圖

                Log.d("444", "LandscapeWithSideBar 檔案全名:" + file.getAbsolutePath().toString());

                if (file.exists()) {

                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    Log.d("444", "文件存在並轉為bitmap");
                    // 接者要將 bitmap 放入 imageview 中
                    LocationMap.setImageBitmap(bitmap);  // show the bitmap in imageview

                } else {
                    // 文件不存在的处理

                    Log.d("444", "不存在");
                }
            }

        } else {
            Log.d("444", "檔案不在外部儲存記憶體");
        }    // end of if ... else

        // 接者要處理景點資料 - travelList 中取資料  這個動作是要先取出 travelInfoId

        Cursor cursor_travelList = db.rawQuery("SELECT * FROM travelList", null);
        cursor_travelList.moveToFirst();

        int count_travelList = cursor_travelList.getInt(0);
        int index = 0;

        if (count_travelList == 0) {

            // 表格為空
            Log.d("qaz", "travelList 表格內容已空");

        } else {

            // 表格不為空

            Log.d("qaz", "travelList 表格內容已有資料了 ! ");
            Log.d("qaz" , "  >>>>> travelList 長度:" + cursor_travelList.getCount()) ;
            Log.d("qaz", "首先將資料列抽出 - 這裡只要 category 為 3  (美食)");



            // 下面抽出相關欄位
            if (cursor_travelList.moveToFirst()) {

                // 取出來欄位驗證一下

                do {

                    int travelInfoId = cursor_travelList.getInt(cursor_travelList.getColumnIndexOrThrow("travelInfoId"));
                    String name = cursor_travelList.getString(cursor_travelList.getColumnIndexOrThrow("name"));
                    String nameEn = cursor_travelList.getString(cursor_travelList.getColumnIndexOrThrow("nameEN"));

                    String category = cursor_travelList.getString(cursor_travelList.getColumnIndexOrThrow("category"));
                    String location = cursor_travelList.getString(cursor_travelList.getColumnIndexOrThrow("location"));
                    String locationEn = cursor_travelList.getString(cursor_travelList.getColumnIndexOrThrow("locationEn"));
                    String description = cursor_travelList.getString(cursor_travelList.getColumnIndexOrThrow("description"));
                    String descriptionEn = cursor_travelList.getString(cursor_travelList.getColumnIndexOrThrow("descriptionEn"));


                    Log.d("qaz", "___________ 美食相關資料 from travelList  _____________");
                    Log.d("qaz", "travelInfoId : " + travelInfoId);
                    Log.d("qaz", "name : " + name);
                    Log.d("qaz", "nameEn : " + nameEn);
                    Log.d("qaz", "category : " + category);
                    Log.d("qaz", "location: " + location);
                    Log.d("qaz", "description: " + description);
                    Log.d("qaz", "descriptionEn: " + descriptionEn);


                    if (category.equals("3")) {

                        // 目前有 2 個景點 - 北門車站 (1)  , 森林之歌 (7) - travelInfoId

                        TitleNamesListFromtravelList.add(index, name);          // 美食名稱  (中文)
                        AddressListFromtravelList.add(index, location);         // 美食地址  (中文)
                        DescriptionListFromtravelList.add(index, description);  // 美食描述  (中文)

                        travelInfoIdList.add(index,travelInfoId);   // add corresponding travelInfoId to the list

                        Log.d("qaz", " 美食相關資料 (Array list){{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{{ ");
                        Log.d("qaz", " 美食名稱 :" + TitleNamesListFromtravelList.get(index).toString());      // 名稱
                        Log.d("qaz", " 美食地址 :" + AddressListFromtravelList.get(index).toString());          // 景點地址
                        Log.d("qaz", " 美食描述 :" + DescriptionListFromtravelList.get(index).toString());      // 景點描述

                        index++;    // index of list

                    } else ;  // ignore others

                } while (cursor_travelList.moveToNext());
            }
        }  // 資料不為空

        TitleNamesStrArrayFromcgstravelList = new String[index];   // declare a string array for  titles
        AddressesStrArrayFromcgstravelList = new String[index];   // declare a string array for addresses
        DescriptionsStrArrayFromtravelList = new String[index];   // declare a string array for descriptions


        assert TitleNamesStrArrayFromcgstravelList != null &&
                AddressesStrArrayFromcgstravelList != null &&
                DescriptionsStrArrayFromtravelList != null;

        // 抽出美食名稱 , 地址 , 描述
        for (int kk = 0; kk < index; kk++) {

            // 景點名稱 ,

            TitleNamesStrArrayFromcgstravelList[kk] = TitleNamesListFromtravelList.get(kk).toString();        // 景點名稱
            AddressesStrArrayFromcgstravelList[kk] = AddressListFromtravelList.get(kk).toString();         // 景點地址  (中文)
            DescriptionsStrArrayFromtravelList[kk] = DescriptionListFromtravelList.get(kk).toString();     // 描述

            Log.d("qaz", "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
            Log.d("qaz", "美食名稱 :" + TitleNamesStrArrayFromcgstravelList[kk]);
            Log.d("qaz", "美食地址 :" + AddressesStrArrayFromcgstravelList[kk]);
            Log.d("qaz", "描述 :" + DescriptionsStrArrayFromtravelList[kk]);

        }  // end of for

        cursor_travelList.close();   // close cursor_travelList cursor

        Cursor cursor_cgstravelList = db.rawQuery("SELECT * FROM cgstravelList", null);
        cursor_cgstravelList.moveToFirst();

        int count_cgstravelList = cursor_cgstravelList.getInt(0);

        if (count_cgstravelList == 0) {

            // 表格為空
            Log.d("qaz", "cgstravelList 表格內容已空");

        } else {

            // 表格不為空

            Log.d("qaz", "cgstravelList 表格內容已有資料了 ! ");
            Log.d("qaz", "首先將資料列抽出 - 這裡只要 travelInfoId (這個會與 travelList 中的 travelInfoId 對應 ");

            int index_cgstravelList = 0;

            // 下面抽出相關欄位 - 步行時間  , 距離

            if (cursor_cgstravelList.moveToFirst()) {

                // 取出來欄位驗證一下

                do {

                    int cgsId = cursor_cgstravelList.getInt(cursor_cgstravelList.getColumnIndexOrThrow("cgsId"));
                    int travelInfoId = cursor_cgstravelList.getInt(cursor_cgstravelList.getColumnIndexOrThrow("travelInfoId"));
                    String distance = cursor_cgstravelList.getString(cursor_cgstravelList.getColumnIndexOrThrow("distance"));
                    String walkingTime = cursor_cgstravelList.getString(cursor_cgstravelList.getColumnIndexOrThrow("walkingTime"));
                    String orgFileName = cursor_cgstravelList.getString(cursor_cgstravelList.getColumnIndexOrThrow("orgFileName"));

                    Log.d("qaz", "___________ 美食相關資料 from cgstravelList _____________");
                    Log.d("qaz", "cgsId : " + cgsId);
                    Log.d("qaz", "travelInfoId : " + travelInfoId);
                    Log.d("qaz", "disatnce : " + distance);
                    Log.d("qaz", "walkingTime : " + walkingTime);
                    Log.d("qaz", "orgFileName : " + orgFileName);  // 這裡的圖檔名 都是各主項目中點下去的細項的地圖路線主圖

                    // 其對應順序為 :             檔案名稱
                    // 1 - 北門火車站 (景點)      cgstravelList_1_1
                    // 3 - 安娜與國王 (住宿)
                    // 4 - 兆品      (住宿)
                    // 5 - 一間茶屋  (美食)
                    // 6 - 國王的菜  (美食)
                    // 7 - 森林之歌  (景點)      cgstravelList_1_7
                    // 於此, 我們只需要 travelInfo 為 5 , 6 的資料


                    if (travelInfoId == 5) {
                        walkingTimeListFromcgsTravlList.add(index_cgstravelList, walkingTime);   // add a walkingtime record to array list
                        DistanceListFromcgsTravlList.add(index_cgstravelList, distance);         // add distance record to array list

                        index_cgstravelList++;    // index of list
                    } else if (travelInfoId == 6) {
                        walkingTimeListFromcgsTravlList.add(index_cgstravelList, walkingTime);   // add a walkingtime record to array list
                        DistanceListFromcgsTravlList.add(index_cgstravelList, distance);         // add distance record to array list

                        index_cgstravelList++;    // index of list
                    } else ;    // just get 1 , 7 travelInfoId


                } while (cursor_cgstravelList.moveToNext());

            }   // cursor_cursor_cgstravelList end

            /// 距離 , 步行時間
            DistanceListFromcgstravelList = new String[index_cgstravelList];    // 距離字串陣列
            WalkingTimeStrArrayFromcgstravelList = new String[index_cgstravelList];   // 步行時間

            cursor_cgstravelList.close();   // close cursor_cgstravelList
            Log.d("qaz", "長度: " + walkingTimeListFromcgsTravlList.size());

            howmanyitems = index_cgstravelList ;   // get how many items will be show


            for (int ii = 0; ii < index_cgstravelList; ii++) {

                DistanceListFromcgstravelList[ii] = DistanceListFromcgsTravlList.get(ii).toString();
                WalkingTimeStrArrayFromcgstravelList[ii] = walkingTimeListFromcgsTravlList.get(ii).toString();

                // 底部兩個顯示項目 : 步行時間 , 距離
                Log.d("qaz", "================= " + index_cgstravelList);
                Log.d("qaz", "步行時間 :" + WalkingTimeStrArrayFromcgstravelList[ii].toString());
                Log.d("qaz", "距離 :" + DistanceListFromcgstravelList[ii].toString());

            } // end of for


        }   // 表格有資料

        ///////// 轉圖 開始 !

        // 先宣告一個 bitmap array
        BitmapForPngArray = new Bitmap[howmanyitems] ;  // 取出幾個 category = 1 就轉幾個

        List<Bitmap> bitmapList = new ArrayList<>();    // 這裡是個 bitmaplist 用來儲存景點相關的圖檔

        Bitmap bitmap1, bitmap2 ;
        bitmap1 = ConvertPngToBitmap("travelImgList17.png", mContext); // 北門火車站

        if (bitmap1 != null ) {

            bitmapList.add(0,bitmap1);   // 一間茶屋
        }

        Log.d("qaz", "travelImgList17.png 轉出來了");

        bitmap2 = ConvertPngToBitmap("travelImgList16.png", mContext);  // 森林之歌

        if ( bitmap2 != null ) {

            bitmapList.add(1,bitmap2);   // 國王的茶

        }

        Log.d("qaz" , " >>>>>>> bitmapList的長度 :" + bitmapList.size()) ;

        Log.d("qaz", " travelImgList16.png 轉出來了");



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
        Toast.makeText(this, "美食餐廳", Toast.LENGTH_SHORT).show();

        DateTxt   =  (TextView)findViewById(R.id.datetimetxt) ;  // day and time

        SideSpinner SideBarFun;   // sidebar declaration
        SideBarFun = (SideSpinner)this.findViewById(R.id.sidespinner_funs);   // sidebar functions
        updateTime();             // 更新時間  per 0.5 second

        // 下面是開始建立 model list for cardview item

        modelList = new ArrayList<>();
        recycler = findViewById(R.id.recyclerView);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        // Bitmap bitmap ;
        // bitmap = ConvertPngToBitmap("travelImgList1.png", mContext);


        // 設立全部的項目 - 第一層
        if ( bitmapList.size() != 0 ) {

        for (int i = 0 ; i < howmanyitems ; i ++ ) {

            modelList.add(new Model(
                    /*ImageList[i]*/ bitmapList.get(i),  // 圖片
                    R.drawable.iconpersonwalking,
                    R.drawable.iconlocation,
                    /*TitleNamesList[i]*/
                    TitleNamesStrArrayFromcgstravelList[i],
                    /*AddressList[i]*/
                    AddressesStrArrayFromcgstravelList[i],
                    /*TimeList[i]*/
                    "從這裡步行" + WalkingTimeStrArrayFromcgstravelList[i] + "分鐘到達",
                    /*DistanceList[i]*/
                    "距這裡" + DistanceListFromcgstravelList[i] + "公里",
                    /*DescriptionsList[i]*/
                       DescriptionsStrArrayFromtravelList[i]));


            Log.d(TAG, "XXXXXXXXX - 資料設定");
            Log.d(TAG, modelList.get(i).Titlename);
            Log.d(TAG, modelList.get(i).Address);
            Log.d(TAG, modelList.get(i).Time);
            Log.d(TAG, modelList.get(i).Distance);
        }

        }  else {
            Log.d("qaz", "bitmap圖轉出有問題!");
        }

            //init the adapter with model list and context - 若用 CustomAdapter 是跳到 景點 !
            // 用 CustomAdapterDelicacy

            adapter = new CustomerAdapterDelicacy(modelList,getApplicationContext());
            //set the adapter into recyclerView
            recycler.setAdapter(adapter);

        recycler.addItemDecoration(new SpacesItemDecoration(1));   // item 間隔寬度

        BackToPrevious = SideBarFun.findViewById(R.id.sidebar_leftbackview);    // 回前頁

        BackToPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

                Bundle bundle = new Bundle();
                bundle.putInt("loginflag", 1);

                Log.d("abc", "景點導覽回到前一頁");
                Intent intent = new Intent(DelicacyWithSideBar.this, MainActivity.class);
                intent.putExtras(bundle);
                view.getContext().startActivity(intent);

            }
        });   // 回前頁

        BackToHome = SideBarFun.findViewById(R.id.sidebar_homeview);

        BackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();

                Bundle bundle = new Bundle();
                bundle.putInt("loginflag", 1);

                Log.d("abc", "景點導覽回到主畫面");
                Intent intent = new Intent(DelicacyWithSideBar.this, MainActivity.class);
                intent.putExtras(bundle);
                view.getContext().startActivity(intent);


            }
        });   // 回到主畫面

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

            String nowDateNTime = nowDate + " • "+ nowTime;

            return nowDateNTime  ;   // 傳回目前的時間

        }  // end of CurrentTime

        private String Today() {

            String nowDate = new SimpleDateFormat("MM/dd").format(new Date());  // 取得目前的日期
            assert nowDate != null;

            return nowDate  ;   // 傳回目前的時間

        }   // end of

        @Override
        public void finish() {

        // finish current activity

            super.finish();

            overridePendingTransition(R.anim.activity_bottom_in, R.anim.activity_bottom_out);  // fade in/out  的特效

            Intent intent = new Intent();
            intent.setClass(DelicacyWithSideBar.this, MainActivity.class);

            Bundle bundle = new Bundle();

            if (bundle != null ) {
                bundle.putInt("loginflag", 1);
                intent.putExtras(bundle);
                startActivity(intent);      // go to main activity
            }


        }  // end of finish


       @Override
       public void onBackPressed() {

           // must call a method to end Activity behind all statement
           super.onBackPressed();
           finish() ;    // call finish method to finish current activity
       }

    private void checkApplicationPermission() {

        ActivityCompat.requestPermissions(DelicacyWithSideBar.this, permissionsList, 1);

    }

    public boolean isPNGFileExists(String filename, Context context) {

        // 確保外部儲存空間是可用的

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // 構建文件路徑

            File externalStorageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

            Log.d("qaz", " LandscapeWithSideBar FilePath : " + externalStorageDir.getAbsolutePath());

            File file = new File(externalStorageDir, filename);

            Log.d("qaz", "LandscapeWithSideBar 檔案全名:" + file.getAbsolutePath().toString());

            // 檢查文件是否存在並且是 PNG 文件

            return file.exists();  // 存在: true
        }

        return false;
    }


    public Bitmap ConvertPngToBitmap(String filename, Context context) {

        boolean FileExist = isPNGFileExists(filename, context);   // 檢查圖是否在外部記憶體

        if (FileExist == true) {

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
                        Log.d("qaz", "文件存在並轉為bitmap 成功");
                    else
                        Log.d("qaz", "轉換bitmap 失敗");

                    // 接者要將 bitmap 放入 imageview 中
                    // LocationMap.setImageBitmap(bitmap);  // show the bitmap in imageview

                    return bitmap ;

                } else {
                    // 文件不存在的处理

                    Log.d("qaz", "不存在");
                }
            }

        } else {
            Log.d("444", "檔案不在外部儲存記憶體");

        }    // end of if ... else

        return null ;

    }  // end of  ConvertPngToBitmap

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
            android.Manifest.permission.MODIFY_AUDIO_SETTINGS,
    };


}