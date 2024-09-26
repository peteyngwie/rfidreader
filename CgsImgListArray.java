package com.smartcity;

import android.provider.BaseColumns;

public final class CgsImgListArray {


        private CgsImgListArray () {}

        public static class CgsImgListEntry implements BaseColumns {
            public static final String TABLE_NAME = "cgsimage";   // database table name

            public static final String COLUMN_NAME_travelImgId = "travelImgId"  ;
            public static final String COLUMN_NAME_travelInfoId = "travelInfoId";
            public static final String COLUMN_NAME_orgFileName = "orgFileName"  ;
            public static final String COLUMN_NAME_sortSeq  = "sortSeq" ;
            public static final String COLUMN_NAME_file = "file";


        }



}
