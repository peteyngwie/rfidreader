
package com.smartcity.cgs;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;

public class ModelAirAlert {

    int Arrowimage;           // 箭頭
    int PeopleWalkingimage ;  // 小人
    int Locationimage ;       // 位置
    int shelterimage ;        // 避難所

    String  Titlename ,         // 主題
            Address   ,         // 地址
            Time      ,         // 時間
            Amount    ;         // 數量

    public ModelAirAlert() {}  //  null constructor

    public ModelAirAlert(
                           int arrow                       ,   //  箭頭
                           int Locationimage               ,   //  位置
                           int peopleWalkingimage          ,   //  小人跑
                           int shelterimage                ,   //  避難所

                   String Titlename       ,  // 避難所名稱
                   String Address         ,  // 地址
                   String Time            ,  // 交通時間
                   String Amount             // 人數
                   )                         // 避難所描述

    {
        // 前面的 icon 設定

        this.Arrowimage = arrow ;
        this.Locationimage = Locationimage ;
        this.PeopleWalkingimage = peopleWalkingimage ;
        this.shelterimage = shelterimage  ;

        this.Titlename = Titlename;
        this.Address = Address;
        this.Time = Time ;
        this.Amount = Amount ;

        Log.d(TAG,"-----------------");

        Log.d(TAG,"避難所名稱 >>" +   this.Titlename ) ;
        Log.d(TAG,"地址 >>" +   this.Address ) ;
        Log.d(TAG,"時間 >>" +   this.Time ) ;
        Log.d(TAG,"人數 >>" +   this.Amount ) ;

    }   // end of constructor

    // 箭頭
    public int getArrowimage() {
        return this.Arrowimage ;
    }
    public void setArrowimage(int image ) {

        this.Locationimage = image ;

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

    // 人數 ///////////////
    public int  getAmountimage() {
        return this.shelterimage ;
    }
    public void setAmountimage (int image ) {
        this.shelterimage = image ;
    }


    // 避難所名稱  ////////////
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
    public String getTimeAndDistance() {
        return this.Time ;
    }
    public void setTimeAndDistance(String Time) {
        this.Time = Time ;
    }


    // 人數  ///////////////
    public String getAmount () {
        return this.Amount ;
    }

    public void setAmount(String Amount) {
        this.Amount = Amount ;
    }

}   // end of ModelAirAlert