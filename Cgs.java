package com.smartcity.api;

public class Cgs {

    private int cgsId;
    private String cgsName;
    private String city;
    private String cityEn;
    private String area;
    private String areaEn;
    private String addrDesc;
    private String addrDescEn;
    private String interVer;
    private String interIp;
    private String sipNumber;
    private String sipPassword;

    public void setCgsId(int cgsId) {  this.cgsId = cgsId;  }
    public void setCgsName(String cgsName) {  this.cgsName = cgsName;  }
    public void setCity(String city) {  this.city = city;  }
    public void setCityEn(String cityEn) {  this.cityEn = cityEn; }
    public void setArea(String area) { this.area = area; }
    public void setAreaEn(String areaEn) {  this.areaEn = areaEn; }
    public void setAddrDesc(String addrDesc) { this.addrDesc = addrDesc;  }
    public void setAddrDescEn(String addrDescEn) {  this.addrDescEn = addrDescEn; }
    public void setInterVer(String interVer) { this.interVer = interVer;  }
    public void setInterIp(String interIp) {  this.interIp = interIp;  }
    public void setSipNumber(String sipNumber) { this.sipNumber = sipNumber;  }
    public void setSipPassword(String sipPassword) { this.sipPassword = sipPassword; }

    public int getCgsId() {  return cgsId; }
    public String getCgsName() {   return cgsName;  }
    public String getCity() {  return city; }
    public String getCityEn() {  return cityEn;  }
    public String getArea() { return area;  }
    public String getAreaEn() {  return areaEn; }
    public String getAddrDesc() {  return addrDesc;  }
    public String getAddrDescEn() {  return addrDescEn; }
    public String getInterVer() { return interVer; }
    public String getInterIp() { return interIp; }
    public String getSipNumber() {  return sipNumber; }
    public String getSipPassword() { return sipPassword; }

}