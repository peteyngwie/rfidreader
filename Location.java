package com.smartcity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Location {


    @SerializedName("locationName")
    private String locationName ;    // 地點名稱

    @SerializedName("weatherElement")
    private List<WeatherElement> weatherElement = new ArrayList<>();   // 氣象元素

    // Location中包了 locationName 與 weatherElement

    public Location(String locationName , List<WeatherElement> weatherElement ) {
        this.locationName = locationName ;
        this.weatherElement = weatherElement;
    }
    public void setLocationName(String locationName) { this.locationName = locationName ; }
    public String getLocationName() {
        return this.locationName;
    }

    public void setWeatherElement ( List<WeatherElement> weatherElement) {
        this.weatherElement = weatherElement ;
    }
    public List<WeatherElement> getWeatherElement() { return this.weatherElement ; }
}