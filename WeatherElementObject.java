package com.smartcity;

import java.lang.reflect.Array;
import java.sql.Time;
import java.util.ArrayList;

public class WeatherElementObject {

       // for each element's name and description
        private String  elementname ;   // 名稱
        private String  description ;   // 描述

        // 基本上 ,應該有多個 timearray , 每個長度是 2*7 = 14
        // 每個 element有3個欄位 , 分別是 startTime , endTime 及 elementValue
        ///////
        private ArrayList<TimeNElementValue> PoP12hArrayList = new ArrayList<TimeNElementValue>();               // 12小時降雨機率 array list
        private ArrayList<TimeNElementValue> AverageTemperatureArrayList = new ArrayList<TimeNElementValue>();   // 平均溫度 array list
        private ArrayList<TimeNElementValue> MinTemperatureArrayList = new ArrayList<TimeNElementValue>() ;      // 最低溫度 array list
        private ArrayList<TimeNElementValue> MaxTemperatureArrayList = new ArrayList<TimeNElementValue>() ;      // 最高溫度 array list
        private ArrayList<TimeNElementValue> WxArrayList = new ArrayList<TimeNElementValue>() ;                  // 天氣描述 array list
        private ArrayList<TimeNElementValue> WeatherDescriptionArrayList = new ArrayList<TimeNElementValue>() ;  // 天氣綜合描述 array list
        private ArrayList<TimeNElementValue> humidityArrayList = new ArrayList<TimeNElementValue>() ;            // 濕度 array list

    public WeatherElementObject () {}

    // public String getElementname() { return this.elementname ; }
    // public String getDescription() { return this.description ; }
    public ArrayList<TimeNElementValue> getPoP12hArrayList() { return this.PoP12hArrayList ; }
    public ArrayList<TimeNElementValue> getAverageTemperatureArrayList() { return this.AverageTemperatureArrayList ; }
    public ArrayList<TimeNElementValue> getMinTemperatureArrayList() { return this.MinTemperatureArrayList ; }
    public ArrayList<TimeNElementValue> getWxArrayList() { return this.WxArrayList ; }
    public ArrayList<TimeNElementValue> getMaxTemperatureArrayList() { return this.MaxTemperatureArrayList ; }
    public ArrayList<TimeNElementValue> getWeatherDescriptionArrayList() { return this.WeatherDescriptionArrayList ;}
    public ArrayList<TimeNElementValue> getHumidityArrayList() { return  this.humidityArrayList ; }

    // public void setElementname(String elementname) { this.elementname = elementname ; }
    // public void setDescription(String description) { this.description = description ; }
    public void setPoP12hArrayList(ArrayList<TimeNElementValue> PoP12hArrayList) { this.PoP12hArrayList = PoP12hArrayList ;}
    public void setAverageTemperatureArrayList(ArrayList<TimeNElementValue> AverageTemperatureArrayList) { this.AverageTemperatureArrayList = AverageTemperatureArrayList ;}
    public void setMinTemperatureArrayList(ArrayList<TimeNElementValue> minTemperatureArrayList) { this.MinTemperatureArrayList = minTemperatureArrayList ; }
    public void setMaxTemperatureArrayList(ArrayList<TimeNElementValue> maxTemperatureArrayList) { this.MaxTemperatureArrayList = maxTemperatureArrayList ; }
    public void setWxArrayList(ArrayList<TimeNElementValue> wxArrayList) { this.WxArrayList = wxArrayList ;}
    public void setWeatherDescriptionArrayList(ArrayList<TimeNElementValue> weatherDescriptionArrayList) { this.WeatherDescriptionArrayList = weatherDescriptionArrayList ; }
    public void setHumidityArrayList(ArrayList<TimeNElementValue> humidityArrayList) { this.humidityArrayList = humidityArrayList ; }
    public void setDescription (String  description) { this.description = description ;}
    public void setElementname(String elementname) { this.elementname= elementname ; }
}   // end of class


