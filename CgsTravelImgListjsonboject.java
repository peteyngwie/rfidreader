package com.smartcity;

public class CgsTravelImgListjsonboject {

    private int travelImgId ;
    private int travelInfoId_  ;
    private String orgFileName ;
    private int sortSeq ;
    private String file ;

    public CgsTravelImgListjsonboject (){}

    public CgsTravelImgListjsonboject (int travelImgId , int travelInfoId_ , String orgFileName , int sortSeq , String file )  {
        this.travelImgId = travelImgId ;
        this.travelInfoId_ = travelInfoId_ ;
        this.orgFileName = orgFileName ;
        this.sortSeq = sortSeq ;
        this.file = file ;

    }

    public void settravelImgId(int travelImgId) {this.travelImgId = travelImgId ; }
    public void settravelInfoId(int travelInfoId) {this.travelInfoId_ = travelInfoId ; }
    public void setorgFileName(String orgFileName) {this.orgFileName = orgFileName ; }
    public void setsortSeq(int sortSeq) {this.sortSeq = sortSeq ; }
    public void setfile(String  file) {this.file = file ; }

    public int     gettravelImgId()  { return this.travelImgId ; }
    public int     gettravelInfoId() { return this.travelInfoId_  ; }
    public String  getorgFileName()  { return this.orgFileName  ; }
    public int     getsortSeq()      { return this.sortSeq  ; }
    public String  getmapfile()      { return this.file ; }

}
