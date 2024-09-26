package com.smartcity.api;

public class TravelImg {
    private int travelImgId;
    private int travelInfoId;
    private String orgFileName ;
    private int sortSeq ;

    public TravelImg( ) { }

    public void settravelImgId(int travelImgId) { this.travelImgId = travelImgId ; }
    public void settravelInfoId(int travelInfoId ) { this.travelInfoId = travelInfoId ;  }
    public void setorgFileName(String orgFileName) {  this.orgFileName = orgFileName ;  }
    public void setsortSeq(int sortSeq) {  this.sortSeq = sortSeq ;  }

    public int getTravelImgId() { return this.travelImgId;     }
    public int getTravelInfoId() { return this.travelInfoId ; }
    public String getOrgFileName() { return this.orgFileName ; }
    public int getSortSeq() { return this.sortSeq ; }

}
