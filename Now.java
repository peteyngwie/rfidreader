

package com.smartcity;

import com.google.gson.annotations.SerializedName;

public class Now {

    @SerializedName("Precipitation")

    private String  Precipitation;
    public Now (String  Precipitation) {
        this.Precipitation = Precipitation;
    }

    public String  getPrecipitation() {
        return Precipitation ;
    }
}
