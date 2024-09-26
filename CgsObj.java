package com.smartcity.cgs;

import android.provider.BaseColumns;




public final class CgsObj {


    private CgsObj () {}

    public static class CgsObjEntry implements BaseColumns {
        public static final String TABLE_NAME = "cgs";   // database table name

        public static final String COLUMN_NAME_cgsId   = "cgsId"  ;
        public static final String COLUMN_NAME_cgsName = "cgsName";
        public static final String COLUMN_NAME_city    = "city"  ;
        public static final String COLUMN_NAME_cityEn  = "cityEn" ;
        public static final String COLUMN_NAME_area    = "area";
        public static final String COLUMN_NAME_areaEn         = "areaEn";
        public static final String COLUMN_NAME_addrDesc       = "addrDesc";
        public static final String COLUMN_NAME_addrDescEn     = "addrDescEn";
        public static final String COLUMN_NAME_sipNumber      = "sipNumber";
        public static final String COLUMN_NAME_sipPassword    = "sipPassword";
        public static final String  COLUMN_NAME_interVer  = "interVer" ;
        public static final String  COLUMN_NAME_interIp  = "interIp" ;



    }



}