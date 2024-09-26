package com.smartcity.api;

public class Sip {

    private String domain ;
    private String type1Number ;
    private String type2Number ;


    public void setDomain(String domain) { this.domain = domain ; }
    public void setType1Number(String type1Number) { this.type1Number = type1Number ; }
    public void setType2Number(String type2Number) {this.type2Number = type2Number ; }

    public String getDomain(){ return this.domain ; }
    public String getType1Number() {return this.type1Number ; }
    public String getType2Number() { return this.type2Number ; }


}
