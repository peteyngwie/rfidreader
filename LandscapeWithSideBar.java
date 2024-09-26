package com.smartcity.cgs;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.smartcity.SpacesItemDecoration;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Timer;
import java.util.TimerTask;

public class LandscapeWithSideBar extends AppCompatActivity {

    TextView DateTxt;                   //  Day/Time

    Timer Clocktimer = new Timer();     // 計時器 for flushing time per 0.5 second

    RecyclerView recycler;
    List<Model> modelList;   // 景點資料串列
    RecyclerView.Adapter adapter;

    private int howmanyitems ;


    ImageView BackToPrevious;    // 回前頁
    ImageView BackToHome;       // 回到首頁
    ImageView Camera;            // 景點照相

    private ImageView LocationMap;  // location map imageview (主圖)
    private Bitmap bitmapMainMap;

    private SQLiteDatabase db;   // database
    private MyDatabaseHelper myDatabaseHelper;  // db helper

    private Context mContext;


    String[] TitleNamesList = {
            "阿里山森林鐵路車庫園區",
            "嘉義市立博物館",
            "嘉義製材所園區",
            "檜意生活村",
            "森林之歌",
            "北門火車站",
            "台灣花磚博物館",
            "北香湖公園",
            "嘉義市美術館",
            "嘉義文化創意產業園區"};    // landscapes name
    ArrayList<String> TitleNamesListFromtravelList = new ArrayList<>();  // 景點抬頭 ( get them from travelList table )
    String[] TitleNamesStrArrayFromcgstravelList;  // title names
    String[] TimeList = {
            "從這裡步行1分鐘到達",
            "從這裡步行1分鐘到達",
            "從這裡步行2分鐘到達",
            "從這裡步行2分鐘到達",
            "從這裡步行4分鐘到達",
            "從這裡步行8分鐘到達",
            "從這裡步行9分鐘到達",
            "從這裡步行10分鐘到達",
            "從這裡步行16分鐘到達",
            "從這裡步行23分鐘到達",
            "從這裡步行23分鐘到達"};

    String[] WalkingTimeStrArrayFromcgstravelList;  // walking time

    ArrayList<String> TimeListFromcgstravelList = new ArrayList<>();  // 步行時間 ( get them from cgstravelList table )

    ArrayList<Integer> travelInfoIdList = new ArrayList<>() ;        // 用來存放 travelInfoId 之用

    String[] AddressList = {
            "600嘉義市東區林森西路2號",
            "600嘉義市東區忠孝路275-1號",
            "600嘉義市東區林森西路4號",
            "600嘉義市東區共和路370號",
            "600嘉義市東區文化路",
            "600嘉義市東區共和路428號",
            "600嘉義市東區光華路108號",
            "600嘉義市西區文化路600號",
            "600嘉義市西區廣寧街101號",
            "600嘉義市西區中路616號"
    };    // 地址

    ArrayList<String> AddressListFromtravelList = new ArrayList<>();       // 地址  ( get them from travelList table )
    String[] AddressesStrArrayFromcgstravelList;  //  addresses

    String[] DistanceList = {

            "距這裡0公里",
            "距這裡140公尺",
            "距這裡140公尺",
            "距這裡400公尺",
            "距這裡500公尺",
            "距這裡600公尺",
            "距這裡700公尺",
            "距這裡1.0公里",
            "距這裡1.7公里",
            "距這裡1.7公里"
    };

    String[] DistanceListFromcgstravelList;  // distance from here

    ArrayList<String> DistanceListFromcgsravelList = new ArrayList<>();       // 地址  ( get them from cgstravelList table )

    //  landscape descriptions

    String[] DescriptionsList = {

            "綠意盎然的空間，展示著具有歷史意義的蒸汽和柴油火車頭，以及運貨和載客車廂。",
            "這座博物館收藏地質檔案與化石，並設有藝廊及多種藝術工作坊。",
            "歷史建築",
            "文化景點，坐擁近 30 座建於日治時期的建築。",
            "坐擁美麗景觀的綠地，特色景物是以木材和鐵軌打造的大型圓頂裝置藝術。",
            "文化景點，日治時期所建造的火車站。",
            "木造博物館，展示著數千塊來自台灣各地的完好瓷磚，另附設商店。",
            "這個綠意盎然的遼闊空間設有平整的步道、溜冰場和池塘，埤子頭植物園即設立於此。",
            "日據時期現代主義建築，提供建築導覽行程並定期舉辦藝術巡迴展。",
            "歷史建築"
    };

    ArrayList<String> DescriptionListFromtravelList = new ArrayList<>();  //  描述  ( get them from travelList table )

    String[] DescriptionsStrArrayFromtravelList;  // description from travellist


    // landscape images list

    int[] ImageList = {R.drawable.alishanforestrailway02,
            R.drawable.chiayimuseum01,
            R.drawable.chiayimaterial03,
            R.drawable.hinokivillage02,
            R.drawable.forestsong02,
            R.drawable.beimenrwstation06,
            R.drawable.taiwantile08,
            R.drawable.beixianghupark02,
            R.drawable.chiayimunicipalart04,
            R.drawable.chiayiculturalpark06};     // 景點圖 - 共 10 個景點 (這是靜態的 png 檔在 R.drawable 中 )

    int [] ImageListFromBitmapList  ;    // store bitmap files to show them

    static Bitmap[] BitmapForPngArray ;  // bitmap 陣列

    List<Bitmap> bitmapList = new ArrayList<>();   // bitmap array - 用來儲存由外部記憶體中取出轉 png 成功的 bitmap 檔

    ArrayList<String> walkingTimeListFromcgsTravlList = new ArrayList<>();   // 步行時間

    ArrayList<String> DistanceListFromcgsTravlList = new ArrayList<>();     // 距離

    private ModelLayer modelLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mContext = LandscapeWithSideBar.this;
        myDatabaseHelper = new MyDatabaseHelper(mContext);
        db = myDatabaseHelper.getReadableDatabase();   // open database for reading

        setContentView(R.layout.activity_landscape_with_side_bar);
        LocationMap = findViewById(R.id.mainamp);    // 主圖

        modelLayer = ModelLayer.getModelLayer();

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        }  // 許可權申請

        howmanyitems = 0 ;   // 初始化項目 - how many recyclerview's items


        // 下面是一個試驗 , 將 mainactivity 中的 存在 SharedPreferences 中的 sip 資料 取出來驗證是否正確

        SharedPreferences sharedPref = getSharedPreferences("siptable", Context.MODE_PRIVATE);
        String domain = sharedPref.getString("domain", "0.0.0.0"); // 若取不到, 就是 0.0.0.0 (default)
        String type1Number = sharedPref.getString("type1Number", "1999"); // 若取不到, 就是 1999 (default)
        String type2Number = sharedPref.getString("type2Number", "1999"); // 若取不到, 就是 1999 (default)

        Log.d("vbn", "doamin >>>>>" + domain);
        Log.d("vbn", "type1Number >>>>>" + type1Number);
        Log.d("vbn", "type2Number >>>>>" + type2Number);

       //  mContext = LandscapeWithSideBar.this;

        // 這個是景點的主圖
        // 去外部記憶體中讀取並指定給 LocationMap

        checkApplicationPermission();   // enable premission
         /*
        if (DistanceListFromcgsTravlList != null)
            Log.d("qaz" , "DistanceListFromcgsTravlList is available");
        else
            Log.d("qaz" , "DistanceListFromcgsTravlList is unavailable");

          */


        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        boolean FileExist = isPNGFileExists("cgsImgList1.png", mContext);   // 景點地理位置 主圖

        if (FileExist == true) {

            Log.d("444", "檔案已經在外部儲存記憶體");

            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                // 構建文件路徑

                File externalStorageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

                Log.d("444", " LandscapeWithSideBar FilePath : " + externalStorageDir.getAbsolutePath());

                File file = new File(externalStorageDir, "cgsImgList1.png");   // 景點主圖

                Log.d("444", "LandscapeWithSideBar 檔案全名:" + file.getAbsolutePath().toString());

                if (file.exists()) {

                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    Log.d("444", "文件存在並轉為bitmap");
                    // 接者要將 bitmap 放入 imageview 中
                    LocationMap.setImageBitmap(bitmap);  // show the bitmap in imageview

                } else {


                    Log.d("444", "不存在");
                }
            }

        } else {
            Log.d("444", "檔案不在外部儲存記憶體");
        }    // end of if ... else

        // 接者要處理景點資料 - travelList 中取資料

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
            Log.d("qaz", "首先將資料列抽出 - 這裡只要 category 為 1  (景點)");

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


                    Log.d("qaz", "___________ 景點相關資料 from travelList  _____________");
                    Log.d("qaz", "travelInfoId : " + travelInfoId);
                    Log.d("qaz", "name : " + name);
                    Log.d("qaz", "nameEn : " + nameEn);
                    Log.d("qaz", "category : " + category);
                    Log.d("qaz", "location: " + location);
                    Log.d("qaz", "description: " + description);
                    Log.d("qaz", "descriptionEn: " + descriptionEn);


                    if (category.equals("1")) {

                        // 目前有 2 個景點 - 北門車站 (1)  , 森林之歌 (7) - travelInfoId

                        TitleNamesListFromtravelList.add(index, name);          // 景點名稱  (中文)
                        AddressListFromtravelList.add(index, location);         // 景點地址  (中文)
                        DescriptionListFromtravelList.add(index, description);  // 景點描述  (中文)

                        travelInfoIdList.add(index,travelInfoId);   // add corresponding travelInfoId to the list

                        Log.d("qaz", "___________ 景點相關資料 (Array list) ____________");
                        Log.d("qaz", " 景點名稱 :" + TitleNamesListFromtravelList.get(index).toString());      // 名稱
                        Log.d("qaz", " 景點地址 :" + AddressListFromtravelList.get(index).toString());          // 景點地址
                        Log.d("qaz", " 景點描述 :" + DescriptionListFromtravelList.get(index).toString());      // 景點描述

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

        // 抽出景點名稱 , 地址 , 描述
        for (int kk = 0; kk < index; kk++) {

            // 景點名稱 ,

            TitleNamesStrArrayFromcgstravelList[kk] = TitleNamesListFromtravelList.get(kk).toString();        // 景點名稱
            AddressesStrArrayFromcgstravelList[kk] = AddressListFromtravelList.get(kk).toString();         // 景點地址  (中文)
            DescriptionsStrArrayFromtravelList[kk] = DescriptionListFromtravelList.get(kk).toString();     // 描述

            Log.d("cvb", "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
            Log.d("cvb", "景點名稱 :" + TitleNamesStrArrayFromcgstravelList[kk]);
            Log.d("cvb", "景點地址 :" + AddressesStrArrayFromcgstravelList[kk]);
            Log.d("cvb", "描述 :" + DescriptionsStrArrayFromtravelList[kk]);

        }  // end of for

        cursor_travelList.close();   // close cursor_travelList cursor

        // 接著,抽出 步行時間,距離 from cgstravelList

        Cursor cursor_cgstravelList = db.rawQuery("SELECT * FROM cgstravelList", null);
        cursor_cgstravelList.moveToFirst();

        int count_cgstravelList = cursor_cgstravelList.getInt(0);

        if (count_cgstravelList == 0) {

            // 表格為空
            Log.d("cvb", "cgstravelList 表格內容已空");

        } else {

            // 表格不為空

            Log.d("cvb", "cgstravelList 表格內容已有資料了 ! ");
            Log.d("cvb", "首先將資料列抽出 - 這裡只要 travelInfoId (這個會與 travelList 中的 travelInfoId 對應 ");

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

                    Log.d("cvb", "___________ 景點相關資料 from cgstravelList _____________");
                    Log.d("cvb", "cgsId : " + cgsId);
                    Log.d("cvb", "travelInfoId : " + travelInfoId);
                    Log.d("cvb", "disatnce : " + distance);
                    Log.d("cvb", "walkingTime : " + walkingTime);
                    Log.d("cvb", "orgFileName : " + orgFileName);  // 這裡的圖檔名 都是各主項目中點下去的細項的地圖路線主圖

                    // 其對應順序為 :             檔案名稱
                    // 1 - 北門火車站  (景點)      cgstravelList_1_1
                    // 3 - 安娜與國王  (住宿)
                    // 4 - 兆品      (住宿)
                    // 5 - 一間茶屋   (美食)
                    // 6 - 國王的菜   (美食)
                    // 7 - 森林之歌   (景點)      cgstravelList_1_7
                    // 8 - 檜意森活村 (景點)      cgstravelList_1_8
                    // 於此, 我們只需要 travelInfo 為 1 , 8, 7 的資料

                    if (travelInfoId == 1) {

                        walkingTimeListFromcgsTravlList.add(index_cgstravelList, walkingTime);   // add a walkingtime record to array list
                        DistanceListFromcgsTravlList.add(index_cgstravelList, distance);         // add distance record to array list

                        index_cgstravelList++;     // 加一個

                    } else if (travelInfoId == 7) {
                        walkingTimeListFromcgsTravlList.add(index_cgstravelList, walkingTime);   // add a walkingtime record to array list
                        DistanceListFromcgsTravlList.add(index_cgstravelList, distance);         // add distance record to array list

                        index_cgstravelList++;    // 加一個

                    } else if (travelInfoId == 8) {
                        walkingTimeListFromcgsTravlList.add(index_cgstravelList, walkingTime);   // add a walkingtime record to array list
                        DistanceListFromcgsTravlList.add(index_cgstravelList, distance);         // add distance record to array list

                        index_cgstravelList++;    // index of list


                    } else ;    // just get 1 , 7 , 8 travelInfoId


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

        Log.d("qsx","取出的圖檔數:" + howmanyitems) ;

        // 先宣告一個 bitmap array
        BitmapForPngArray = new Bitmap[howmanyitems] ;  // 取出幾個 category = 1 就轉幾個

        List<Bitmap> bitmapList = new ArrayList<>();    // 這裡是個 bitmaplist 用來儲存景點相關的圖檔

        Bitmap bitmap1, bitmap2 ,bitmap3 ;

        // 應該這裡 要6


        bitmap1 = ConvertPngToBitmap("travelImgList1.png", mContext); // 北門火車站

        if (bitmap1 != null ) {

            bitmapList.add(0,bitmap1);   // 北門火車站

            bitmap1 = bitmapList.get(0);

            if ( bitmap1 != null ) {

                Log.d("qaz", "bitmap 加入有效");
                Log.d("qaz", "travelImgList1.png 轉出來了");
            }
            else {
                Log.d("qaz","bitmap 加入無效");
            }
        }


        bitmap2 = ConvertPngToBitmap("travelImgList24.png", mContext);  // 森林之歌

        if ( bitmap2 != null ) {

            bitmapList.add(1,bitmap2);   // 森林之歌

            bitmap2 = bitmapList.get(1);

            if ( bitmap2 != null ) {
                Log.d("qaz", "bitmap 加入有效");
                Log.d("qaz", "travelImgList24.png 轉出來了");
            }
            else {
                Log.d("qaz","bitmap 加入無效");
            }

        }

        bitmap3 = ConvertPngToBitmap("travelImgList26.png", mContext);  // 檜意森活村

        if ( bitmap3 != null ) {

            bitmapList.add(2,bitmap3);   // 森林之歌

            bitmap3 = bitmapList.get(2);

            if ( bitmap3 != null ) {
                Log.d("qaz", "bitmap 加入有效");
                Log.d("qaz", "travelImgList26.png 轉出來了");
            }
            else {
                Log.d("qaz","bitmap 加入無效");
            }

        }

        Log.d("qsx" , " >>>>>>> bitmapList的長度 :" + bitmapList.size()) ;

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

        SideSpinner SideBarFun;                                               // 側邊攔元件
        SideBarFun = (SideSpinner) this.findViewById(R.id.sidespinner_funs);   // focus the component
        updateTime();             // 更新時間  per 0.5 second

        // 下面是開始建立 model list for cardview item

        modelList = new ArrayList<>();
        recycler = findViewById(R.id.recyclerView);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        // 設立全部的項目 - 第一層
       if ( bitmapList.size() != 0 ) {
           for (int i = 0; i < howmanyitems; i++) {

               modelList.add(new Model(
                       /*ImageList[i]*/ bitmapList.get(i),                  // 圖片
                       R.drawable.iconpersonwalking,  // 小人走路
                       R.drawable.iconlocation,       // 位置圖示

                       /*TitleNamesList[i]*/
                       TitleNamesStrArrayFromcgstravelList[i],
                       /*AddressList[i]*/
                       AddressesStrArrayFromcgstravelList[i],
                       /*TimeList[i]*/
                       "從這裡步行" + WalkingTimeStrArrayFromcgstravelList[i] + "分鐘到達",
                       /*DistanceList[i]*/
                       "距這裡" + DistanceListFromcgstravelList[i] + "公里",
                       /*DescriptionsList[i]*/
                       DescriptionsStrArrayFromtravelList[i]
               ));

               Log.d(TAG, "XXXXXXXXX - 資料設定");
               Log.d(TAG, modelList.get(i).Titlename);
               Log.d(TAG, modelList.get(i).Address);
               Log.d(TAG, modelList.get(i).Time);
               Log.d(TAG, modelList.get(i).Distance);

           }   // 初始化

       }  else {
           Log.d("qaz", "bitmap圖轉出有問題!");
       }


        //init the adapter with model list and context

        adapter = new CustomAdapter(modelList, getApplicationContext());

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

                Log.d("qaz", "景點導覽回到前一頁");
                Intent intent = new Intent(LandscapeWithSideBar.this, MainActivity.class);
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

                Log.d("qaz", "景點導覽回到主畫面");
                Intent intent = new Intent(LandscapeWithSideBar.this, MainActivity.class);
                intent.putExtras(bundle);
                view.getContext().startActivity(intent);


            }
        });   // 回到主畫面


    }


    private void updateTime() {

        Clocktimer.schedule(new TimerTask() {
            @Override
            public void run() {
                DateTxt.setText(CurrentTime());   // Get current to show

            }
        }, 0, 500);

    }    // end of updateTime

    private String CurrentTime() {

        String nowDate = new SimpleDateFormat("YYYY/MM/dd").format(new Date());  // 取得目前的日期
        assert nowDate != null;
        String nowTime = new SimpleDateFormat("HH:mm").format(new Date());  // 取得目前時間
        assert nowTime != null;

        String nowDateNTime = nowDate + " • " + nowTime;

        return nowDateNTime;   // 傳回目前的時間

    }  // end of CurrentTime

    private String Today() {

        String nowDate = new SimpleDateFormat("MM/dd").format(new Date());  // 取得目前的日期
        assert nowDate != null;

        return nowDate;   // 傳回目前的時間

    }   // end of today

    private void checkApplicationPermission() {

        ActivityCompat.requestPermissions(LandscapeWithSideBar.this, permissionsList, 1);

    }

    private Bitmap GetPngFileFromExternalStorage(Context context, String filename, Bitmap bitmap) {

        File externalStorageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        Bitmap myBitmap = null;
        if (externalStorageDir != null) {
            //  建立 PNG文件
            File pngFile = new File(externalStorageDir, filename);  // create a png file

            Log.d("100", "檔案名稱: " + filename);

            try (FileOutputStream fos = new FileOutputStream(pngFile)) {

                boolean compressflag = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos); // 壓完後以 PNG 格式寫入路徑中

                if (compressflag == true) {

                    Log.d("100", " ^^^^^^^^^^^^^^^ 壓縮且儲存成功");

                    File dv = new File(externalStorageDir, filename);
                    Log.d("100", " 檔案路徑及檔名 :" + dv.getAbsolutePath());
                    Log.d("100", " 檔案路徑及檔名 :" + dv.getAbsolutePath() + ", 是否存在:" + dv.exists());

                    if (isPNGFileExists(filename, mContext)) {
                        Log.d("qaz", "有檔案!");

                        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.smartcity.cgs/files/Pictures/" + filename;
                        Log.d("qaz", "%%%%　檔案路徑及名稱 :" + filePath);

                        File imgFile = new File(filePath);

                        if (imgFile.exists()) {

                            myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                            // 在這裡你可以將 myBitmap 設置到 ImageView 或進行其他操作

                            Log.d("qaz", "檔案存在");

                            if (true) {
                                LocationMap = findViewById(R.id.mainamp);
                                LocationMap.setImageBitmap(myBitmap);
                            } //

                            return myBitmap;  // 回傳 bitmap

                        } else {
                            // 處理檔案不存在的情況
                            Log.e("qaz", "File does not exist.");
                        }

                    } else {
                        Log.d("qaz", "沒檔案!");

                    }

                } else
                    Log.d("qaz", " >>>>> 失敗");

            } catch (IOException e) {

                e.printStackTrace();
                Log.d("qaz", "錯誤:" + e.getMessage());

            }
        }

        return null;   // 有錯誤 !
    }

    public boolean isPNGFileExists(String filename, Context context) {

    // 確保外部儲存空間是可用的 ///////////////////////////

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // 权限未被授予，需要请求权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);

                File externalStorageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

                if (externalStorageDir != null) {

                    File file = new File(externalStorageDir, filename);

                    // 檢查文件是否存在並且是 PNG 文件

                    if ( file.isFile() ) {

                        Log.d("qaz", "權限開啟 ::::::::::::::::::::::::::::::::::::");
                        Log.d("qaz", "檔案真的存在: " + file.getAbsolutePath());
                        Log.d("qaz", " LandscapeWithSideBar FilePath : " + externalStorageDir.getAbsolutePath());
                        Log.d("qaz", " LandscapeWithSideBar 檔案全名:" + file.getAbsolutePath().toString());

                        return true;
                    } else {
                        Log.d("qaz", ":::::::::::::::::::::::::::::::::::::");
                        Log.d("qaz", "檔案不存在或不是有效文件: " + file.getAbsolutePath());
                    }
                } else {
                    Log.d("qaz", "externalStorageDir is null");
                }

            }

        else {  // 已經有權限了

            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                // 構建文件路徑

                File externalStorageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);


                if (externalStorageDir != null) {
                    File file = new File(externalStorageDir, filename);

                    // 檢查文件是否存在並且是 PNG 文件
                    if ( file.isFile() ) {

                        Log.d("qaz", ":::::::::::::::::::::::::::::::::::::");
                        Log.d("qaz", "檔案真的存在: " + file.getAbsolutePath());
                        Log.d("qaz", " LandscapeWithSideBar FilePath : " + externalStorageDir.getAbsolutePath());
                        Log.d("qaz", " LandscapeWithSideBar 檔案全名:" + file.getAbsolutePath().toString());

                        return true;
                    } else {
                        Log.d("qaz", ":::::::::::::::::::::::::::::::::::::");
                        Log.d("qaz", "檔案不存在或不是有效文件: " + file.getAbsolutePath());
                    }
                } else {
                    Log.d("qaz", "externalStorageDir is null");
                }
            } else {
                Log.d("qaz", "External storage is not mounted");
            }

            // Log.d("qaz", " LandscapeWithSideBar FilePath : " + externalStorageDir.getAbsolutePath());

            // File file = new File(externalStorageDir, filename);

            // Log.d("qaz", "LandscapeWithSideBar 檔案全名:" + file.getAbsolutePath().toString());

            // 檢查文件是否存在並且是 PNG 文件

            // Log.d("qaz", "檔案真的存在: " + (file.exists() && file.isFile())) ;

            // return file.exists() && file.isFile() ;  // 存在: true
        }


        return false;
    }

    public Bitmap ConvertPngToBitmap(String filename, Context context) {

        boolean FileExist = isPNGFileExists(filename, context);   // 檢查圖是否在外部記憶體

        if (FileExist == true) {

            Log.d("qsx", "檔案已經在外部儲存記憶體");

            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                // 構建文件路徑

                File externalStorageDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

                Log.d("qsx", " Png 檔's FilePath : " + externalStorageDir.getAbsolutePath());

                File file = new File(externalStorageDir, filename);   //  景點小圖

                Log.d("qsx", "路徑加檔案全名:" + file.getAbsolutePath().toString());

                if (file.exists()) {

                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                    if (bitmap != null)
                        Log.d("qsx", "文件存在並轉為bitmap 成功");
                    else
                        Log.d("qsx", "轉換bitmap 失敗");

                    // 接者要將 bitmap 放入 imageview 中
                    // LocationMap.setImageBitmap(bitmap);  // show the bitmap in imageview

                    return bitmap ;

                } else {
                    // 文件不存在的处理

                    Log.d("qsx", "不存在");
                }
            }

        } else {
            Log.d("qsx", "檔案不在外部儲存記憶體");

        }    // end of if ... else

        return null ;

    }  // end of  ConvertPngToBitmap


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
            android.Manifest.permission.MODIFY_AUDIO_SETTINGS,
    };

}