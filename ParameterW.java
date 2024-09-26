package com.smartcity;
import com.google.gson.annotations.SerializedName;

import java.security.SecureRandom;

public class ParameterW {
    @SerializedName("parameterName")
    private String  parameterName ;
    @SerializedName("parameterValue")
    private String parameterValue ;

    public ParameterW(String parameterName , String parameterValue ){
        this.parameterName = parameterName ;
        this.parameterValue = parameterValue ;
    }

    public void setParameterName(String parameterName ) {
        this.parameterName = parameterName ;
    }
    public String getParameterName() { return this.parameterName ; }

    public void setParameterValue(String parameterValue) {
        this.parameterValue = parameterValue ;
    }
    public String getParameterValue() {
        return this.parameterValue ;
    }

}
