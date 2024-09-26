package com.smartcity.cgs;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SqlDataBaseHelper extends SQLiteOpenHelper {

    private static final String DataBaseName = "interboxdb";
    private static final int DataBaseVersion = 1;

    public SqlDataBaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version,String TableName) {
        super(context, DataBaseName, null, DataBaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // 建立 cgs 表格 - DSL

        String cgsSqlTable = "CREATE TABLE IF NOT EXISTS cgs (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "cgsId INT not null," +
                "cgsName TEXT not null," +
                "city TEXT not null," +
                "cityEn TEXT not null," +
                "area TEXT not null," +
                "areaEn TEXT not null," +
                "addrDesc TEXT not null," +
                "addrDescEn TEXT not null," +
                "interVer TEXT not null," +
                "interIp TEXT not null," +
                "sipNumber TEXT not null," +
                "sipPassword TEXT not null" +
                ")";

        // 建立 sip 表格 - DSL
        /*
        String sipSqlTable = "CREATE TABLE IF NOT EXISTS sip (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "domain TEXT not null, " +
                "type1Number TEXT not null, " +
                "type2Number TEXT not null " +
                ")";

         */

        String sipSqlTable = "CREATE TABLE IF NOT EXISTS sip (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "domain TEXT not null, " +
                "type1Number TEXT not null, " +
                "type2Number TEXT not null " +
                ")";


        sqLiteDatabase.execSQL(sipSqlTable);  // 建立資料表 sip
        // sqLiteDatabase.execSQL(cgsSqlTable);  // 建立資料表 cgs
        // sqLiteDatabase.execSQL(sipSqlTable);  // 建立資料表 sip

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        final String dropcgsSQL = "DROP TABLE IF EXISTS cgs";  // 刪除 cgs 表格
        final String dropsipSQL = "DROP TABLE IF EXISTS　sip";  // 刪除 sip 表格

        // sqLiteDatabase.execSQL(dropcgsSQL);
        sqLiteDatabase.execSQL(dropsipSQL);

        onCreate(sqLiteDatabase);


    }


}