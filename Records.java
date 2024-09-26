package com.smartcity;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Records {

    @SerializedName("Station")
    private List<Station> Station = new ArrayList<>() ;     // station

    @SerializedName("datasetDescription")
    private String datasetDescription ;   // 資料集的描述 (三十六小時天氣預報)

    @SerializedName("location")
    private List<Location> location = new ArrayList<>() ; // 城市名稱 (天氣資料)


    // Records中包了Location
    // 這邊使用List的原因是因為被中括號包起來

    public Records(List<Station> station , String datasetDescription , List<Location> location) {

        this.Station = station;
        this.datasetDescription = datasetDescription ;
        this.location = location ;

    }
    public void setDatasetDescription(String datasetDescription) {
        this.datasetDescription = datasetDescription ;  // 存放 三十六小時天氣預報 字串
    }
    public String getDatasetDescription() {
        return this.datasetDescription ;   // 取出 三十六小時天氣預報 字串
    }

    // methods - get/set

    public List<Location> getLocation() {
        return location;  // 取出 location  json array
    }
    public void setLocation(List<Location> location) {
        this.location = location;
    }

    /*

    public Records(List<Location> location ) {

        this.location = location;

    }

     */
    // methods - get/set
    public List<Station> getStation() {
        return Station;
    }
    public void setStation(List<Station> station) {
        this.Station = station;
    }

}