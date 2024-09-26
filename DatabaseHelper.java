package com.smartcity;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.smartcity.cgs.CgsObj;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "cgc.sqlite";  // database 名稱


    // The following are creating tables statement - (C)reate
    ////////////////////////////////  Cgs  表格建立

    private static final String SQL_CREATE_CGS_ENTRIES =
            "CREATE TABLE " + CgsObj.CgsObjEntry.TABLE_NAME + " (" +
                    CgsObj.CgsObjEntry._ID + " INTEGER PRIMARY KEY," +
                    CgsObj.CgsObjEntry.COLUMN_NAME_cgsId + " TEXT," +
                    CgsObj.CgsObjEntry.COLUMN_NAME_cgsName + " TEXT," +
                    CgsObj.CgsObjEntry.COLUMN_NAME_city+ " TEXT," +
                    CgsObj.CgsObjEntry.COLUMN_NAME_cityEn + " TEXT," +
                    CgsObj.CgsObjEntry.COLUMN_NAME_area +  " TEXT," +
                    CgsObj.CgsObjEntry.COLUMN_NAME_areaEn +  " TEXT," +
                    CgsObj.CgsObjEntry.COLUMN_NAME_addrDesc +  " TEXT,"  +
                    CgsObj.CgsObjEntry.COLUMN_NAME_addrDescEn +  " TEXT,"  +
                    CgsObj.CgsObjEntry.COLUMN_NAME_sipNumber + " TEXT," +
                    CgsObj.CgsObjEntry.COLUMN_NAME_sipPassword + " TEXT," +
                    CgsObj.CgsObjEntry.COLUMN_NAME_interVer + " TEXT," +
                    CgsObj.CgsObjEntry.COLUMN_NAME_interIp + " TEXT)";  // 建立  cgs dobject 表格

    ////////////////////////////////  Cgs  表格刪除

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + CgsObj.CgsObjEntry.TABLE_NAME;

    ////////////////////////////////  建立 sip
    /*
    private static final String SQL_CREATE_SIP_ENTRIES =
            "CREATE TABLE " + Cgs.CgsEntry.TABLE_NAME + " (" +
                    Sip.SipEntry._ID + " INTEGER PRIMARY KEY," +
                    Sip.SipEntry.COLUMN_NAME_domain + " TEXT," +
                    Sip.SipEntry.COLUMN_NAME_type1Number+ " TEXT," +
                    Sip.SipEntry.COLUMN_NAME_type2Number + " TEXT)";  // create sip table

     */


    private static final String SQL_CREATE_CGSTravel_ENTRIES =
            "CREATE TABLE " + CgsTravelListArray.CgsTravelListEntry.TABLE_NAME + " (" +
                    CgsTravelListArray.CgsTravelListEntry._ID + " INTEGER PRIMARY KEY," +
                    CgsTravelListArray.CgsTravelListEntry.COLUMN_NAME_cgsId + " TEXT," +
                    CgsTravelListArray.CgsTravelListEntry.COLUMN_NAME_travelInfoId + " TEXT," +
                    CgsTravelListArray.CgsTravelListEntry.COLUMN_NAME_distance + " TEXT," +
                    CgsTravelListArray.CgsTravelListEntry.COLUMN_NAME_sortSeq +  " TEXT," +
                    CgsTravelListArray.CgsTravelListEntry.COLUMN_NAME_walkingTime +  " TEXT," +
                    CgsTravelListArray.CgsTravelListEntry.COLUMN_NAME_mapfileName + " TEXT)";  // create cgs travel table statement


    // Cgs Image list 表格建立
    private static final String SQL_CREATE_CGSImg_ENTRIES =
            "CREATE TABLE " + CgsImgListArray.CgsImgListEntry.TABLE_NAME + " (" +
                    CgsImgListArray.CgsImgListEntry._ID + " INTEGER PRIMARY KEY," +
                    CgsImgListArray.CgsImgListEntry.COLUMN_NAME_travelImgId + " TEXT," +
                    CgsImgListArray.CgsImgListEntry.COLUMN_NAME_travelInfoId + " TEXT," +
                    CgsImgListArray.CgsImgListEntry.COLUMN_NAME_orgFileName + " TEXT," +
                    CgsImgListArray.CgsImgListEntry.COLUMN_NAME_sortSeq +  " TEXT," +
                    CgsImgListArray.CgsImgListEntry.COLUMN_NAME_file + " TEXT)";  // create cgs image table statement


    private static final String SQL_CREATE_Travel_ENTRIES =
            "CREATE TABLE " + TravelListArray.TravelListEntry.TABLE_NAME + " (" +
                    TravelListArray.TravelListEntry._ID + " INTEGER PRIMARY KEY," +
                    TravelListArray.TravelListEntry.COLUMN_NAME_travelInfoId + " TEXT)" ;
                    /* +
                    TravelListArray.TravelListEntry.COLUMN_NAME_category + " TEXT," +
                    TravelListArray.TravelListEntry.COLUMN_NAME_name + " TEXT," +
                    TravelListArray.TravelListEntry.COLUMN_NAME_nameEn +  " TEXT," +
                    TravelListArray.TravelListEntry.COLUMN_NAME_location +  " TEXT," +
                    TravelListArray.TravelListEntry.COLUMN_NAME_locationEn +  " TEXT," +
                    TravelListArray.TravelListEntry.COLUMN_NAME_description +  " TEXT," +
                    TravelListArray.TravelListEntry.COLUMN_NAME_descriptionEn +  " TEXT," +
                    TravelListArray.TravelListEntry.COLUMN_NAME_evaluate +  " TEXT," +
                    TravelListArray.TravelListEntry.COLUMN_NAME_modifyUser +  " TEXT," +

                    TravelListArray.TravelListEntry.COLUMN_NAME_modifyDate + " TEXT)";  // create cgs travel table statement

                     */

    // 下面是刪除表格的敘述

    private static final String SQL_DELETE_CGS_TravelENTRIES =
            "DROP TABLE IF EXISTS " + CgsTravelListArray.CgsTravelListEntry.TABLE_NAME; // 刪除 cgs travel list entry table
    private static final String SQL_DELETE_CGS_ImgENTRIES =
            "DROP TABLE IF EXISTS " + CgsImgListArray.CgsImgListEntry.TABLE_NAME;       // 刪除 cgs img list entry table
    private static final String SQL_DELETE_TravelENTRIES =
            "DROP TABLE IF EXISTS " + TravelListArray.TravelListEntry.TABLE_NAME;       // 刪除 travel list entry table


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {


        //////// 建立表格  ////////////////////////////

        Log.d("lll","表格已建立表格已建立表格已建立表格已建立表格已建立表格已建立表格已建立表格已建立表格已建立表格已建立") ;

        database.execSQL(SQL_CREATE_CGS_ENTRIES);         // create cgs object table
        database.execSQL(SQL_CREATE_CGSTravel_ENTRIES);   // create cgs travel table
        database.execSQL(SQL_CREATE_CGSImg_ENTRIES);      // create cgs image table

        // database.execSQL(SQL_CREATE_Travel_ENTRIES);     // create travel table
        Log.d("lll","表格已建立表格已建立表格已建立表格已建立表格已建立表格已建立表格已建立表格已建立表格已建立表格已建立") ;

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

        database.execSQL(SQL_DELETE_CGS_TravelENTRIES);   // 刪除 cgs travel 表格
        database.execSQL(SQL_DELETE_CGS_ImgENTRIES);      // 刪除 cgs image 表格
        database.execSQL(SQL_DELETE_TravelENTRIES);       // 刪除 travel 表格

        onCreate(database);
    }

    @Override
    public void onDowngrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        onUpgrade(database, oldVersion, newVersion);
    }


}