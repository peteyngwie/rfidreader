package com.smartcity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class WeatherElement {


    @SerializedName("AirTemperature")
    private String airTemperature ;
    @SerializedName("elementName")
    private String elementName ;    // 元素名稱

    @SerializedName("time")
    private List<TimeW> time = new ArrayList<>() ; // time

    public WeatherElement(String airTemperature , String elementName ,  List<TimeW> time ) {

        this.airTemperature = airTemperature;
        this.elementName = elementName ;
        this.time = time ;

    }
    public List<TimeW> getTime() {
        return this.time ;
    }
    public void setTime(List<TimeW> time) {
        this.time = time ;
    }
    public void setelementName (String elementName) {
        this.elementName = elementName ;
    }
    public String getelementName() {
        return this.elementName ;
    }

    public String getairTemperature() {
        return airTemperature;
    }
}