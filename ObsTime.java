package com.smartcity ;

import com.google.gson.annotations.SerializedName;

public class ObsTime {

    @SerializedName("DateTime")

    private String DateTime;
    public ObsTime(String DateTime) {
        this.DateTime = DateTime;
    }

    public String getDateTime() {
        return DateTime;
    }
}
