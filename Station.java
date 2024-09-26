package com.smartcity;

import com.google.gson.annotations.SerializedName;

public class Station {

    // 注意:  @SerializedName("名稱") ,括號中的名稱 必須與  json 中的欄位大小寫一致 ; 否則, 資料取出來是 null 的
    @SerializedName("StationId")
    private String stationID;
    @SerializedName("StationName")
    private String stationName;

    @SerializedName("ObsTime")
    private ObsTime obsTime ;
    @SerializedName("GeoInfo")
    private GeoInfo geoInfo ;

    @SerializedName("WeatherElement")
    private WeatherElement weatherElement ;

    public Station(String stationID, String stationName , ObsTime obsTime , GeoInfo geoInfo , WeatherElement weatherElement) {

        this.stationID = stationID;
        this.stationName = stationName;
        this.obsTime = obsTime ;
        this.geoInfo = geoInfo ;
        this.weatherElement = weatherElement ;

    }

    public String getStationID() {
        return stationID;
    }

    public String getStationName() {
        return stationName;
    }
    public WeatherElement getWeatherElement() { return weatherElement ; }

    public  ObsTime getObsTime() { return obsTime ; }
    public  GeoInfo getGeoInfo() { return geoInfo ; }


}