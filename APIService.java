package com.smartcity;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface APIService {
    @GET("O-A0003-001?")  // 請求的地址中的一部分 (溫度)
    Call<DataResponse> getDailyRainfall(@Query("Authorization") String Authorization);

}