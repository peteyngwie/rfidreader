package com.smartcity;


import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Parameter;

public class TimeW {

    @SerializedName("startTime")
    private String startTime ;
    @SerializedName("endTime")
    private String endTime ;
    @SerializedName("parameter")
    private ParameterW parameter ;

    public TimeW(String  startTime , String endTime , ParameterW parameter){
        this.startTime = startTime ;
        this.endTime = endTime ;
        this.parameter = parameter ;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    public String getStartTime() {
        return this.startTime ;
    }
    public void setEndTime(String endTime) {
        this.endTime = endTime ;
    }
    public String getEndTime() {
        return this.endTime ;
    }

    public void setParameter(ParameterW parameter) {
        this.parameter = parameter;
    }
    public ParameterW getParameter() {
        return this.parameter ;
    }

}
