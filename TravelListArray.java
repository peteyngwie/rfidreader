package com.smartcity;

import android.provider.BaseColumns;


public final class TravelListArray {


    private TravelListArray () {}

    public static class TravelListEntry implements BaseColumns {
        public static final String TABLE_NAME = "travellist";  // database table name

        public static final String COLUMN_NAME_travelInfoId = "travelInfoId";
        public static final String COLUMN_NAME_category = "category";
        public static final String COLUMN_NAME_name = "name";
        public static final String COLUMN_NAME_nameEn = "nameEn";
        public static final String COLUMN_NAME_location = "location";
        public static final String COLUMN_NAME_locationEn = "locationEn";
        public static final String COLUMN_NAME_description = "description";
        public static final String COLUMN_NAME_descriptionEn = "descriptionEn";
        public static final String COLUMN_NAME_evaluate  = "evaluate" ;
        public static final String COLUMN_NAME_modifyUser  = "modifyUser" ;
        public static final String COLUMN_NAME_modifyDate  = "modifyDate" ;

    }


}
