package com.smartcity;

public class WeatherTimeNStatus {
    private String startTime ;     // 起始時間
    private String endTime ;       // 終止時間
    private String WeatherStatus ; // 天氣狀態

    public WeatherTimeNStatus (String startTime , String endTime , String WeatherStatus) {

        this.startTime = startTime ;
        this.endTime = endTime ;
        this.WeatherStatus = WeatherStatus ;
    }

    public String getStartTime() { return this.startTime ; }
    public String getEndTime() { return this.endTime ; }
    public String getWeatherStatus() { return this.WeatherStatus ; }

    public void setStartTime(String startTime) { this.startTime = startTime ; }
    public void setEndTime(String endTime) {  this.endTime = endTime ; }
    public void setWeatherStatus(String weatherStatus) { this.WeatherStatus = weatherStatus ; }

}
