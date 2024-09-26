package com.smartcity;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 本局所屬地面測站每日雨量資料-每日雨量
 * https://opendata.cwb.gov.tw/api/v1/rest/datastore/C-B0025-001?Authorization=CWB-F466F13C-84CD-4A6E-81CB-F3ED6C48A773
 *
 * 月平均-局屬地面測站資料
 * https://opendata.cwb.gov.tw/api/v1/rest/datastore/C-B0027-001?Authorization=CWB-F466F13C-84CD-4A6E-81CB-F3ED6C48A773
 *
 * 氣象測站基本資料-有人氣象測站基本資料
 * https://opendata.cwb.gov.tw/api/v1/rest/datastore/C-B0074-001?Authorization=CWB-F466F13C-84CD-4A6E-81CB-F3ED6C48A773
 *
 * 一般天氣預報 , 今明 36小時天氣預報
 * https://opendata.cwa.gov.tw/api/v1/rest/datastore/F-C0032-001?Authorization=CWA-AA44B18B-3CDE-43BE-9A61-95B5F9FECCE5&locationName=%E9%AB%98%E9%9B%84%E5%B8%82
 *
 * 把重複的URL 設在 baseUrl
 * https://opendata.cwb.gov.tw/api/v1/rest/datastore/
 *
 **/

public class RetrofitManager {
    private static RetrofitManager mInstance = new RetrofitManager();
    private APIService apiService;  // for temperature
    private APIServiceForWeatherIcon apiServiceForWeatherIcon ; // for weather icon update

    private RetrofitManager() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://opendata.cwa.gov.tw/api/v1/rest/datastore/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(APIService.class);                              // 溫度
        apiServiceForWeatherIcon = retrofit.create(APIServiceForWeatherIcon.class);  // 天氣圖示

    }
    //向外提供 RetrofitManager
    public static RetrofitManager getInstance() {
        return mInstance;
    }
    //向外提供 APIService
    public APIService getAPI() {
        return apiService;
    }
    public APIServiceForWeatherIcon getApiServiceForWeatherIcon() { return apiServiceForWeatherIcon ; }

    /**
     *
     * 把重複的URL 設在 baseUrl
     * https://opendata.cwb.gov.tw/api/v1/rest/datastore/
     *
     * 不重複的URL 設在 @GET 或 @POST 後方
     * @GET("C-B0025-001?")
     *
     **/

}

