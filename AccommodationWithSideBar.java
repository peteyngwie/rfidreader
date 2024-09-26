package com.smartcity;

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


// import com.smartcity.cgs.CustomAdapter;
import com.smartcity.cgs.LandscapeWithSideBar;
import com.smartcity.cgs.MainActivity;
import com.smartcity.cgs.Model;
import com.smartcity.cgs.ModelLayer;
import com.smartcity.cgs.MyDatabaseHelper;
import com.smartcity.cgs.R;
import com.smartcity.cgs.SideSpinner;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AccommodationWithSideBar extends AppCompatActivity {

    TextView DateTxt ;                   //  Day/Time

    Timer Clocktimer = new Timer() ;     // 計時器 for flushing time per 0.5 second

    RecyclerView recycler;
    List<Model> modelList;   // 景點資料串列
    RecyclerView.Adapter adapter;

    ImageView PreviousActivity ;  // 回前一頁
    ImageView HomeActivity ;      // 回首頁
    ImageView SocialMediaShot ;   // 打卡照相

    int howmanyitems ;


    private ImageView LocationMap;  // location map imageview (主圖)
    private Bitmap bitmapMainMap;

    private SQLiteDatabase db;   // database
    private MyDatabaseHelper myDatabaseHelper;  // db helper

    private Context mContext;

    ArrayList<String> TitleNamesListFromtravelList = new ArrayList<>();  // 住宿抬頭 ( get them from cgstravelList table )
    String[] TitleNamesStrArrayFromcgstravelList;  // title names
    String[] TitleNamesList = {
            "嘉義樂客商旅",
            "嘉義-兆品酒店",
            "福泰桔子商旅文化店",
            "拉瓦.宅 輕旅店 - LAWA HOUSE",
            "承億文旅 桃城茶樣子",
            "Antik 旅館" ,
            "安娜與國王酒店" ,
            "偶然行旅" ,
            "南院旅墅" ,
            "鈺通大飯店 YUH TONG HOTEL" } ;    // landscapes name

    String[] TimeList = {
            "從這裡步行8分鐘到達"  ,
            "從這裡步行8分鐘到達"  ,
            "從這裡步行11分鐘到達"  ,
            "從這裡步行12分鐘到達"  ,
            "從這裡步行14分鐘到達"  ,
            "從這裡步行15分鐘到達"  ,
            "從這裡步行19分鐘到達" ,
            "從這裡步行19分鐘到達" ,
            "從這裡步行21分鐘到達" ,
            "從這裡步行24分鐘到達" } ;

    String[] WalkingTimeStrArrayFromcgstravelList;  // walking time

    ArrayList<String> TimeListFromcgstravelList = new ArrayList<>();  // 步行時間 ( get them from cgstravelList table )

    ArrayList<Integer> travelInfoIdList = new ArrayList<>() ;        // 用來存放 travelInfoId 之用


    String[] AddressList = {
            "600嘉義市西區林森西路155號" ,
            "600嘉義市西區文化路257號" ,
            "600嘉義市西區文化路169號",
            "600嘉義市東區成仁街253號",
            "600嘉義市東區忠孝路516號",
            "600嘉義市西區中央第一商場39號",
            "600嘉義市東區和平路150號",
            "600嘉義市東區蘭井街167號",
            "60047嘉義市東區公明路65號",
            "600嘉義市東區維新路7號"
    };    // 地址

    ArrayList<String> AddressListFromtravelList = new ArrayList<>();       // 地址  ( get them from travelList table )
    String[] AddressesStrArrayFromcgstravelList;  //  addresses

    String[] DistanceList = {
            "距這裡 550 公尺",
            "距這裡 550 公尺",
            "距這裡 800 公尺",
            "距這裡 850 公尺",
            "距這裡 950 公尺" ,
            "距這裡 1.1 公里" ,
            "距這裡 1.3 公里" ,
            "距這裡 1.3 公里" ,
            "距這裡 1.5 公里",
            "距這裡 1.6 公里"
            };

    String[] DistanceListFromcgstravelList;  // distance from here

    ArrayList<String> DistanceListFromcgsravelList = new ArrayList<>();       // 地址  ( get them from cgstravelList table )



    //  hotel descriptions

    String[] DescriptionsList = {
           "這家低調的飯店位於大街上，所在街道餐館和商店林立，距離嘉義火車站步行只需 12 分鐘，距離繁華的文化路夜市步行只需 8 分鐘。",
           "位於設有露天市集的熱鬧街道上，這家精緻的酒店距離北門車站步行 11 分鐘，距離嘉義公園 2 公里，距離蘭潭水庫 4 公里。" +
           "布置溫馨的客房提供免費 Wi-Fi、平面電視、小冰箱和茶飲咖啡沖泡設備。氣氛悠閒的單臥套房鋪設榻榻米地板，並備有日式床墊與日式矮餐桌。 飯店附設中式餐廳、有裸磚牆面的舒適咖啡館/酒吧、健身房和宴會場地。另供應付費早餐。" ,
            "3 星級飯店。這間旅館的氣氛悠閒，步行 2 分鐘可達嘉義文化路夜市，步行 12 分鐘可達嘉義車站，距離嘉義市史蹟資料館 2 公里。" ,
            "2 星級飯店。觀光、休閒娛樂和交通資訊便利。" ,
            "4 星級飯店。這間以茶為主題的雅致飯店毗鄰台 1 線，附近有多間餐館，步行 11 分鐘可達檜意森活村的文化展間，距離嘉義火車站和廣闊的嘉義公園均為 2 公里。提供免費停車位。其他設施包括餐廳、以茶罐裝飾的寬敞交誼廳、屋頂無邊際泳池和酒吧。提供品茶等多種活動。" ,
            "近文化路觀光夜市、交通非常便利。",
            "近文化路觀光夜市、交通非常便利。寵物非常友善酒店。" ,
            "遊客必訪，近文化路觀光夜市，交通非常便利。",
            "4 星級飯店。這間時尚的高級飯店位於市區的主要街道上，步行 9 分鐘可到嘉義公園觀賞歷史遺址，步行 12 分鐘可到熱鬧的文化路夜市，距離嘉義火車站 3 公里。",
            "4 星級飯店。這間精緻的飯店坐落於市中心，步行 10 分鐘可達嘉義公園，距離嘉義博物館和蘭潭水庫皆為 2 公里。 充滿禪意的質樸客房備有免費 Wi-Fi、平面電視、保險箱、小冰箱及茶飲咖啡沖泡設備。提供客房服務。 房價含早餐。典雅的餐廳供應法式和義式料理。其他設施包括健身房、商務中心和享有城市景觀的頂樓花園。"
    } ;

    ArrayList<String> DescriptionListFromtravelList = new ArrayList<>();  //  描述  ( get them from travelList table )

    String[] DescriptionsStrArrayFromtravelList;  // description from travellist

    // hotel images list

    int[] ImageList = {
            R.drawable.hotel1 ,
            R.drawable.hotel2 ,
            R.drawable.hotel3 ,
            R.drawable.hotel4 ,
            R.drawable.hotel5 ,
            R.drawable.hotel6 ,
            R.drawable.hotel7 ,
            R.drawable.hotel8 ,
            R.drawable.hotel9,
            R.drawable.hotel10 } ;     // 旅館圖


    int [] ImageListFromBitmapList  ;    // store bitmap files to show them

    Bitmap[] BitmapForPngArray ;

    List<Bitmap> bitmapList = new ArrayList<>();   // bitmap array - 用來儲存由外部記憶體中取出轉 png 成功的 bitmap 檔

    ArrayList<String> walkingTimeListFromcgsTravlList = new ArrayList<>();   // 步行時間

    ArrayList<String> DistanceListFromcgsTravlList = new ArrayList<>();     // 距離

    private ModelLayer modelLayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accommodation_with_side_bar);

        mContext = AccommodationWithSideBar.this;
        myDatabaseHelper = new MyDatabaseHelper(mContext);
        db = myDatabaseHelper.getReadableDatabase();   // open database for reading
        LocationMap = findViewById(R.id.mainamp);      // 主圖

        modelLayer = ModelLayer.getModelLayer();

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        }  // 許可權申請

        howmanyitems = 0;   // 初始化項目 - how many recyclerview's items

        checkApplicationPermission();   // enable premission


        // 下面是一個試驗 , 將 mainactivity 中的 存在 SharedPreferences 中的 sip 資料 取出來驗證是否正確

        SharedPreferences sharedPref = getSharedPreferences("siptable", Context.MODE_PRIVATE);
        String domain = sharedPref.getString("domain", "0.0.0.0"); // 若取不到, 就是 0.0.0.0 (default)
        String type1Number = sharedPref.getString("type1Number", "1999"); // 若取不到, 就是 1999 (default)
        String type2Number = sharedPref.getString("type2Number", "1999"); // 若取不到, 就是 1999 (default)

        Log.d("vbn", "doamin >>>>>" + domain);
        Log.d("vbn", "type1Number >>>>>" + type1Number);
        Log.d("vbn", "type2Number >>>>>" + type2Number);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        boolean FileExist = isPNGFileExists("cgsTravel_1_3.png", mContext);   // 景點地理位置 主圖 (安娜與國王酒店)

        if (FileExist == true) {

            Log.d("qaq", "檔案已經在外部儲存記憶體");

            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                // 構建文件路徑

                File externalStorageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

                Log.d("qaq", " AccommodationWithSideBar FilePath : " + externalStorageDir.getAbsolutePath());

                File file = new File(externalStorageDir, "cgsTravel_1_3.png");   // 景點主圖 (安娜與國王酒店)

                Log.d("qaq", "AccommodationWithSideBar 檔案全名:" + file.getAbsolutePath().toString());

                if (file.exists()) {

                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    Log.d("qaq", "文件存在並轉為bitmap");
                    // 接者要將 bitmap 放入 imageview 中
                    LocationMap.setImageBitmap(bitmap);  // show the bitmap in imageview

                } else {
                    // 文件不存在的处理

                    Log.d("qaq", "不存在");
                }
            }

        } else {
            Log.d("qaq", "檔案不在外部儲存記憶體");
        }    // end of if ... else


        Cursor cursor_travelList = db.rawQuery("SELECT * FROM travelList", null);
        cursor_travelList.moveToFirst();

        int count_travelList = cursor_travelList.getInt(0);
        int index = 0;

        if (count_travelList == 0) {

            // 表格為空
            Log.d("qaq", "travelList 表格內容已空");

        } else {

            // 表格不為空

            Log.d("qaq", "travelList 表格內容已有資料了 ! ");
            Log.d("qaq", "  >>>>> travelList 長度:" + cursor_travelList.getCount());
            Log.d("qaq", "首先將資料列抽出 - 這裡只要 category 為 2  (住宿)");

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


                    Log.d("qaq", "___________ 相關資料 from travelList  _____________");
                    Log.d("qaq", "travelInfoId : " + travelInfoId);
                    Log.d("qaq", "name : " + name);
                    Log.d("qaq", "nameEn : " + nameEn);
                    Log.d("qaq", "category : " + category);
                    Log.d("qaq", "location: " + location);
                    Log.d("qaq", "description: " + description);
                    Log.d("qaq", "descriptionEn: " + descriptionEn);


                    if (category.equals("2")) {

                        // 目前有 2 個景點 - 安娜與國王酒店 (1)  , 嘉義兆品酒店 (2)  - travelInfoId

                        TitleNamesListFromtravelList.add(index, name);          // 住宿名稱  (中文)
                        AddressListFromtravelList.add(index, location);         // 住宿地址  (中文)
                        DescriptionListFromtravelList.add(index, description);  // 住宿描述  (中文)

                        travelInfoIdList.add(index, travelInfoId);   // add corresponding travelInfoId to the list

                        Log.d("kkk", "___________ 住宿相關資料 (Array list) ____________");
                        Log.d("kkk", " 住宿名稱 :" + TitleNamesListFromtravelList.get(index).toString());       // 名稱
                        Log.d("kkk", " 住宿地址 :" + AddressListFromtravelList.get(index).toString());          // 地址
                        Log.d("kkk", " 住宿描述 :" + DescriptionListFromtravelList.get(index).toString());      // 描述

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

        for (int kk = 0; kk < index; kk++) {

            // 景點名稱 ,

            TitleNamesStrArrayFromcgstravelList[kk] = TitleNamesListFromtravelList.get(kk).toString();        // 景點名稱
            AddressesStrArrayFromcgstravelList[kk] = AddressListFromtravelList.get(kk).toString();         // 景點地址  (中文)
            DescriptionsStrArrayFromtravelList[kk] = DescriptionListFromtravelList.get(kk).toString();     // 描述

            Log.d("qaz", "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
            Log.d("qaz", "住宿名稱 :" + TitleNamesStrArrayFromcgstravelList[kk]);
            Log.d("qaz", "住宿地址 :" + AddressesStrArrayFromcgstravelList[kk]);
            Log.d("qaz", "住宿描述 :" + DescriptionsStrArrayFromtravelList[kk]);

        }  // end of for

        cursor_travelList.close();   // close cursor_travelList cursor

        // 接著,抽出 步行時間,距離 from cgstravelList

        Cursor cursor_cgstravelList = db.rawQuery("SELECT * FROM cgstravelList", null);
        cursor_cgstravelList.moveToFirst();

        int count_cgstravelList = cursor_cgstravelList.getInt(0);

        int index_cgstravelList = 0;
        if (count_cgstravelList == 0) {

            // 表格為空
            Log.d("qaz", "cgstravelList 表格內容已空");

        } else {

            // 表格不為空

            Log.d("qaz", "cgstravelList 表格內容已有資料了 ! ");
            Log.d("qaz", "首先將資料列抽出 - 這裡只要 travelInfoId (這個會與 travelList 中的 travelInfoId 對應 ");

            index_cgstravelList = 0;

            // 下面抽出相關欄位 - 步行時間  , 距離

            if (cursor_cgstravelList.moveToFirst()) {

                // 取出來欄位驗證一下

                do {

                    int cgsId = cursor_cgstravelList.getInt(cursor_cgstravelList.getColumnIndexOrThrow("cgsId"));
                    int travelInfoId = cursor_cgstravelList.getInt(cursor_cgstravelList.getColumnIndexOrThrow("travelInfoId"));
                    String distance = cursor_cgstravelList.getString(cursor_cgstravelList.getColumnIndexOrThrow("distance"));
                    String walkingTime = cursor_cgstravelList.getString(cursor_cgstravelList.getColumnIndexOrThrow("walkingTime"));
                    String orgFileName = cursor_cgstravelList.getString(cursor_cgstravelList.getColumnIndexOrThrow("orgFileName"));

                    Log.d("iii", "___________ 住宿相關資料 from cgstravelList _____________");
                    Log.d("iii", "cgsId : " + cgsId);
                    Log.d("iii", "travelInfoId : " + travelInfoId);
                    Log.d("iii", "disatnce : " + distance);
                    Log.d("iii", "walkingTime : " + walkingTime);
                    Log.d("iii", "orgFileName : " + orgFileName);  // 這裡的圖檔名 都是各主項目中點下去的細項的地圖路線主圖

                    // 其對應順序為 :             檔案名稱
                    // 1 - 北門火車站 (景點)
                    // 3 - 安娜與國王 (住宿)      cgstravelList_1_3
                    // 4 - 兆品      (住宿)      cgstravelList_1_4
                    // 5 - 一間茶屋  (美食)
                    // 6 - 國王的菜  (美食)
                    // 7 - 森林之歌  (景點)
                    // 於此, 我們只需要 travelInfo 為 3,4 的資料


                    if (travelInfoId == 3) {
                        // 安娜與國王

                        Log.d("abc", "___________ 住宿相關資料 from cgstravelList _____________");
                        Log.d("abc", "cgsId : " + cgsId);
                        Log.d("abc", "travelInfoId : " + travelInfoId);
                        Log.d("abc", "disatnce : " + distance);
                        Log.d("abc", "walkingTime : " + walkingTime);
                        Log.d("abc", "orgFileName : " + orgFileName);  // 這裡的圖檔名 都是各主項目中點下去的細項的地圖路線主圖

                        walkingTimeListFromcgsTravlList.add(index_cgstravelList, walkingTime);   // add a walkingtime record to array list
                        DistanceListFromcgsTravlList.add(index_cgstravelList, distance);         // add distance record to array list

                        index_cgstravelList++;    // index of list
                    } else if (travelInfoId == 4) {

                        // 嘉義兆品酒店

                        Log.d("abc", "___________ 住宿相關資料 from cgstravelList _____________");
                        Log.d("abc", "cgsId : " + cgsId);
                        Log.d("abc", "travelInfoId : " + travelInfoId);
                        Log.d("abc", "disatnce : " + distance);
                        Log.d("abc", "walkingTime : " + walkingTime);
                        Log.d("abc", "orgFileName : " + orgFileName);  // 這裡的圖檔名 都是各主項目中點下去的細項的地圖路線主圖

                        walkingTimeListFromcgsTravlList.add(index_cgstravelList, walkingTime);   // add a walkingtime record to array list
                        DistanceListFromcgsTravlList.add(index_cgstravelList, distance);         // add distance record to array list

                        index_cgstravelList++;    // index of list
                    } else ;    // just get 3 , 4 travelInfoId


                } while (cursor_cgstravelList.moveToNext());

            }   // cursor_cursor_cgstravelList end


            ////////////// 距離 , 步行時間
            DistanceListFromcgstravelList = new String[index_cgstravelList];    // 距離字串陣列
            WalkingTimeStrArrayFromcgstravelList = new String[index_cgstravelList];   // 步行時間

            cursor_cgstravelList.close();   // close cursor_cgstravelList
            Log.d("iii", "長度: " + walkingTimeListFromcgsTravlList.size());

            howmanyitems = index_cgstravelList;   // get how many items will be show

            for (int ii = 0; ii < index_cgstravelList; ii++) {

                DistanceListFromcgstravelList[ii] = DistanceListFromcgsTravlList.get(ii).toString();
                WalkingTimeStrArrayFromcgstravelList[ii] = walkingTimeListFromcgsTravlList.get(ii).toString();

                ////////////////////// 底部兩個顯示項目 : 步行時間 , 距離
                Log.d("iii", "================= " + index_cgstravelList);
                Log.d("iii", "步行時間 :" + WalkingTimeStrArrayFromcgstravelList[ii].toString());
                Log.d("iii", "距離 :" + DistanceListFromcgstravelList[ii].toString());

            } // end of for

        }  // 表格有資料

        ////////////////////// 先宣告一個 bitmap array

        BitmapForPngArray = new Bitmap[howmanyitems] ;  // 取出幾個 category = 2 就轉幾個

        List<Bitmap> bitmapList = new ArrayList<>();    // 這裡是個 bitmaplist 用來儲存景點相關的圖檔

        Bitmap bitmaptemp ;
        int pngindex = 11;  // just is an index
        String filename = "travelImgList" ;
        String [] filenamestring ;
        filenamestring = new String[howmanyitems];


            bitmaptemp = ConvertPngToBitmap( "travelImgList11.png" , mContext);  // convert a png file to bitmap

            if (bitmaptemp != null ) {
                bitmapList.add(bitmaptemp);   // add a bitmap object to bitmap list
                Log.d("iii","檔案名稱 :" + filename) ;
            }
            else {
                Log.d("iii","有錯");
            }

        bitmaptemp = ConvertPngToBitmap( "travelImgList13.png" , mContext);  // convert a png file to bitmap

        if (bitmaptemp != null ) {
            bitmapList.add(bitmaptemp);   // add a bitmap object to bitmap list
            Log.d("iii","檔案名稱 :" + filename) ;
        }
        else {
            Log.d("iii","有錯");
        }


        Log.d("iii" , " >>>>>>> bitmapList的長度 :" + bitmapList.size()) ;


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

        Toast.makeText(this, "landscape !!", Toast.LENGTH_SHORT).show();

        DateTxt = (TextView) findViewById(R.id.datetimetxt);  // day and time
        SideSpinner SideBarFun;   // sidebar declaration
        SideBarFun = (SideSpinner) this.findViewById(R.id.sidespinner_funs);   // sidebar functions
        updateTime();             // 更新時間  per 0.5 second

        // 下面是開始建立 model list for cardview item

        modelList = new ArrayList<>();
        recycler = findViewById(R.id.recyclerView);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        // Bitmap bitmap;
        // bitmap = ConvertPngToBitmap("travelImgList1.png", mContext);

        for (int i = 0; i < howmanyitems; i++) {

            modelList.add(new Model(
                    /*ImageList[i]*/  bitmapList.get(i),
                    R.drawable.iconpersonwalking,
                    R.drawable.iconlocation,
                    /*TitleNamesList[i]*/    TitleNamesStrArrayFromcgstravelList[i],
                    /*AddressList[i]*/  AddressesStrArrayFromcgstravelList[i],
                    /*TimeList[i]*/  "從這裡步行" + WalkingTimeStrArrayFromcgstravelList[i] + "分鐘到達",
                    /*DistanceList[i]*/  "距這裡" + DistanceListFromcgstravelList[i] + "公里",
                    /*DescriptionsList[i]*/
                    DescriptionsStrArrayFromtravelList[i]));

        }   // end of for loop

        // 用 CustomerAdapterHotel
        adapter = new CustomerAdapterHotel(modelList, getApplicationContext());
        //set the adapter into recyclerView
        recycler.setAdapter(adapter);

        PreviousActivity = SideBarFun.findViewById(R.id.sidebar_leftbackview);    // initializes previous button
        HomeActivity = SideBarFun.findViewById(R.id.sidebar_homeview);            // initializes home button
        // SocialMediaShot = SideBarFun.findViewById(R.id.sidebar_cameraview);        // social media shot

        // 回到前一頁
        PreviousActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();  // exit current activity
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                view.getContext().startActivity(intent);

            }
        });

        // 回到首頁
        HomeActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();   // exit current activity and back to home activity
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                view.getContext().startActivity(intent);
            }
        });


        // 拍照打卡
        /*
        SocialMediaShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // do something and wait for api
            }
        });

         */


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

    }   // end of Today

    private void checkApplicationPermission() {

        ActivityCompat.requestPermissions(AccommodationWithSideBar.this, permissionsList, 1);

    }

    @Override
    public void finish() {

        super.finish();

        overridePendingTransition(R.anim.activity_bottom_in, R.anim.activity_bottom_out);  // fade in/out  的特效

    }  // end of finish

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