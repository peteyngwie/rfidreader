package com.smartcity;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIServiceForWeatherIcon {
    @GET("F-C0032-001?")  // 請求的地址中的一部分 (今明36小時天氣預報)
    Call<DataResponseWeatherIcon> getWeatherIcon(@Query("Authorization") String Authorization);
}