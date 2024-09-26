package com.smartcity;

import android.provider.BaseColumns;


public final class CgsTravelListArray {


    private CgsTravelListArray () {}

    public static class CgsTravelListEntry implements BaseColumns {
        public static final String TABLE_NAME = "cgstravel";  // database table name

        public static final String COLUMN_NAME_cgsId = "cgsId";
        public static final String COLUMN_NAME_travelInfoId = "travelInfoId";
        public static final String COLUMN_NAME_distance = "distance";
        public static final String COLUMN_NAME_sortSeq = "sortSeq";
        public static final String COLUMN_NAME_walkingTime = "walkingTime";
        public static final String COLUMN_NAME_mapfileName = "mapfileName";
    }


    }
