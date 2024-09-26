package com.smartcity;

public class CgsTravelListjsonboject {

    private int     cgsId  ;
    private int     travelInfoId ;            // travelInfoId
    private double  distance ;                // 距離
    private int     sortSeq  ;                // 排序順序
    private int     walkingTime ;             // 步行時間
    private String  mapfileName ;             // 地圖檔案名稱

    public CgsTravelListjsonboject(){}

    public CgsTravelListjsonboject(int cgsId , int travelInfoId , double distance , /*int sortseq*/  int walkingTime , String mapfileName    )  {
       this.cgsId = cgsId ;
       this.travelInfoId = travelInfoId ;
       this.distance = distance ;
       // this.sortSeq = sortseq ;
       this.walkingTime = walkingTime ;
       this.mapfileName = mapfileName ;

    }

    public void setcgsId(int cgsId) {this.cgsId = cgsId ; }
    public void settravelInfoId(int travelInfoId) {this.travelInfoId = travelInfoId ; }
    public void setdistance(double  distance) {this.distance = distance ; }
    public void setSortSeq(int sortSeq) { this.sortSeq = sortSeq ; }
    public void setwalkingTime(int walkingTime) {this.walkingTime = walkingTime ; }
    public void setmapfileName(String  mapfileName) {this.mapfileName = mapfileName ; }

    public int     getcgsId() { return this.cgsId ; }
    public int     gettravelInfoId() { return this.travelInfoId  ; }
    public double  getdistance() { return this.distance  ; }
    public int getSortSeq() { return this.sortSeq ; }
    public int     getwalkingTime() { return this.walkingTime  ; }
    public String  getmapfileName() { return this.mapfileName ; }


}
