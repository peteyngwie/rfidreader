package com.smartcity.api;

import java.util.List;


// 如下的資料類別是 result json 回覆中每個欄位
// 按api文件的欄位依序如下:
// 1. cgs - json object ( cgs 資訊)
// 2. cgsImgList - json object array ( cgs 圖檔)
// 3. travelList - json object array (
public class InterBoxCgs {

    private List<CgsTravel> cgsTravelList;
    private List<TravelImg> travelImgList;
    private List<Travel> travelList;
    private Sip sip ;
    private Cgs cgs;
    private List<CgsImg> cgsImgList;

    public List<CgsTravel> getCgsTravelList() {
        return cgsTravelList;
    }
    public void setCgsTravelList(List<CgsTravel> cgsTravelList) { this.cgsTravelList = cgsTravelList; }
    public List<TravelImg> getTravelImgList() {
        return travelImgList;
    }
    public void setTravelImgList(List<TravelImg> travelImgList) {
        this.travelImgList = travelImgList;
    }
    public List<Travel> getTravelList() {
        return travelList;
    }
    public void setTravelList(List<Travel> travelList) {
        this.travelList = travelList;
    }
    public Cgs getCgs() {
        return cgs;
    }
    public void setCgs(Cgs cgs) {
        this.cgs = cgs;
    }

    public Sip getSip() {
        return sip ;
    }

    public void setSip(Sip sip) {
        this.sip = sip;
    }

    public List<CgsImg> getCgsImgList() {
        return cgsImgList;
    }
    public void setCgsImgList(List<CgsImg> cgsImgList) {
        this.cgsImgList = cgsImgList;
    }

}
