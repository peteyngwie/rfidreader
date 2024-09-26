package com.smartcity.cgs;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mydatabase.db";
    private static final int DATABASE_VERSION = 16;

    // 表 cgs
    private static final String TABLE_1 = "cgs1";  // cgs
    // 表 cgsImgList
    private static final String TABLE_2 ="cgsImgList" ;   // cgsImgList
    private static final String TABLE_3 = "travelList" ;  // travelList

    private static  final  String TABLE_4 = "travelImgList" ;  // travelImgList
    private static final   String TABLE_5 = "cgsTravelList" ;  // cgsTravelList

    private static final  String TABLE_6 = "logintable" ;     // for login

    private static final String CREATE_TABLE_1 = "CREATE TABLE IF NOT EXISTS " + TABLE_1 + " (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "cgsId INTEGER NOT NULL , " +
            "cgsName TEXT NOT NULL , " +
            "city TEXT NOT NULL , " +
            "cityEn TEXT NOT NULL , " +
            "area TEXT NOT NULL , " +
            "areaEn TEXT NOT NULL , " +
            "addrDesc TEXT NOT NULL , " +
            "addrDescEn TEXT NOT NULL , " +
            "sipNumber TEXT NOT NULL , " +
            "sipPassword TEXT NOT NULL , " +
            "interVer TEXT NOT NULL , " +
            "interIp TEXT NOT NULL " +
            ");" ;   // 建立 cgs 表格

    private static final String CREATE_TABLE_2 = "CREATE TABLE IF NOT EXISTS " + TABLE_2 + " (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "cgsImgId INTEGER NOT NULL , " +
            "cgsId INTEGER NOT NULL , " +
            "category TEXT NOT NULL , " +
            "orgFileName TEXT NOT NULL , " +
            "image BLOB " +  //////////////////////////////// 圖檔
            ");" ;   // 建立 cgsImgList 表格

    private static final String CREATE_TABLE_3 = "CREATE TABLE IF NOT EXISTS " + TABLE_3 + " (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "travelInfoId INTEGER NOT NULL , " +
            "name TEXT NOT NULL, " +
            "nameEN TEXT NOT NULL, " +
            "category TEXT NOT NULL, " +
            "location TEXT NOT NULL, " +
            "locationEn TEXT NOT NULL, " +
            "description TEXT NOT NULL, " +
            "descriptionEn TEXT NOT NULL " +
            ");" ;   // 建立 travelList 表格


    private static final String CREATE_TABLE_4 = "CREATE TABLE IF NOT EXISTS " + TABLE_4 + " (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "travelImgId INTEGER NOT NULL , " +
            "travelInfoId INTEGER NOT NULL, " +
            "orgFileName TEXT NOT NULL, " +
            "sortSeq INTEGER NOT NULL, " +
            "image BLOB " +  //////////////////////////////// 圖檔
            ");" ;   // 建立 travelImgList 表格

    private static final String CREATE_TABLE_5 = "CREATE TABLE IF NOT EXISTS " + TABLE_5 + " (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT , " +
            "cgsId INTEGER NOT NULL , " +
            "travelInfoId INTEGER NOT NULL , " +
            "distance TEXT NOT NULL , " +
            "walkingTime TEXT NOT NULL , " +
            "orgFileName TEXT NOT NULL ," +
            "image BLOB " +  //////////////////////////////   圖檔
            ");" ;   // 建立 cgstravelList 表格


    private static final String CREATE_TABLE_6 = "CREATE TABLE IF NOT EXISTS " + TABLE_6 + " (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "login INTEGER NOT NULL " +

            ");" ;   // 建立 登入旗號表格


    // 表 sip
    private static final String TABLE_sip = "sip1";

    private static final String CREATE_TABLE_sip = "CREATE TABLE IF NOT EXISTS " + TABLE_sip + " (" +
            "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "domain TEXT NOT NULL , " +
            "type1Number TEXT NOT NULL , " +
            "type2Number TEXT NOT NULL " +
            ");" ;


    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_1);  // cgs
        db.execSQL(CREATE_TABLE_2);  // cgsImgList
        db.execSQL(CREATE_TABLE_3);  // travelList
        db.execSQL(CREATE_TABLE_4);  // travelImgList
        db.execSQL(CREATE_TABLE_5);  // travelImgList
        db.execSQL(CREATE_TABLE_6);  // 登入用


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            db.execSQL("DROP TABLE IF EXISTS " + TABLE_1);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_2);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_3);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_4);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_5);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_6);  // for login

        onCreate(db);
    }
}

