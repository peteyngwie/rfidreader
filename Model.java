package com.smartcity.cgs;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.graphics.Bitmap;
import android.util.Log;

public class Model {

    Bitmap Majorimage;          // 主圖
    int PeopleWalkingimage ; // 小人
    int Locationimage ;      // 位置

    String  Titlename ,       // 主題
            Address   ,       // 地址
            Time      ,       // 時間
            Distance  ,       // 距離

            Descriptions  ;   // 景點描述



    public Model() {}  //  null constructor

    public Model(  Bitmap  Majorimage         ,  // 左主圖
                   int PeopleWalkingimage ,  // 小人
                   int Locationimage      ,  // 位置
                   String Titlename       ,  // 景點名稱
                   String Address         ,  // 景點地址
                   String Time            ,  // 交通時間
                   String Distance        ,  // 交通距離
                   String Description )      // 景點描述

    {

        this.Majorimage = Majorimage ;
        this.PeopleWalkingimage = PeopleWalkingimage ;
        this.Locationimage = Locationimage ;

        this.Titlename = Titlename;
        this.Address = Address;
        this.Time = Time ;
        this.Distance = Distance ;
        this.Descriptions = Description ;

        Log.d(TAG,"-----------------");

        Log.d(TAG,"名稱 >>" +   this.Titlename ) ;
        Log.d(TAG,"地址 >>" +   this.Address ) ;
        Log.d(TAG,"時間 >>" +   this.Time ) ;
        Log.d(TAG,"距離 >>" +   this.Distance ) ;
        Log.d(TAG,"景點描述 >>" +   this.Descriptions ) ;

    }   // end of constructor

    // 主圖
    /*
     public int  getMajorimage() {
        return this.Majorimage ;
    }

     */
    public Bitmap  getMajorimage() {
        return this.Majorimage ;
    }
    public void setMajorimage(Bitmap  image) {
        this.Majorimage = image;
    }

    // 小人
    public int getPeopleWalkingimage() {
        return this.PeopleWalkingimage ;
    }
    public void setPeopleWalkingimage(int image) {
        this.PeopleWalkingimage = image;
    }
    // 位置
    public int getLocationimage() {
        return this.Locationimage ;
    }
    public void setLocationimage(int image) {
        this.Locationimage = image;
    }

    // 景點名稱  ////////////
    public String getTitlename() {
        return this.Titlename ;
    }
    public void setTitlename(String name) {
        this.Titlename = name;
    }

    // 位址  ///////////////
    public String getAddress() {
        return this.Address;
    }
    public void setAddress(String Address) {
        this.Address = Address ;
    }

    // 時間 ///////////////
    public String getTime() {
        return this.Time ;
    }
    public void setTime(String Time) {
        this.Time = Time ;
    }

    // 距離 ///////////////
    public String getDistance() {
        return this.Distance ;
    }
    public void setDistance(String Distance) {
        this.Distance = Distance ;
    }

    // 景點描述

    public String getDescription() {
        return this.Descriptions ;
    }
    public void setDescription(String Descriptions) {
        this.Descriptions = Descriptions ;
    }


}   // end of Model