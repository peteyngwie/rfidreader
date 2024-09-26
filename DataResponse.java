package com.smartcity;

import com.google.gson.annotations.SerializedName;

/**
 * 中括號要用List，大括號則用Class
 **/
public class DataResponse {
    // 這裡是資料回傳的格式
    @SerializedName("records")   // 指名 response data 中第一階層的 tag 欄位
    private Records records;

    public DataResponse(Records records) {
        this.records = records;
    }

    public Records getRecords() {
        return records;
    }

    public void setRecords(Records records) {
        this.records = records;
    }
}