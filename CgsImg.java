package com.smartcity.api;

public class CgsImg {
    private  int cgsImgId;          // cgs 圖檔 id
    private  int cgsId;             // cgs id
    private  String category ;      // 分類 : 1. 景點 2. 住宿 3. 美食
    private  String orgFileName ;   // 圖檔名稱

    public CgsImg(){}


    public CgsImg(int cgsImgId , int cgsId , String category , String orgFileName ) {

        this.cgsImgId = cgsImgId ;
        this.cgsId = cgsId ;
        this.category = category ;
        this.orgFileName = orgFileName ;

    }

   public void setCgsImgId(int cgsImgId) { this.cgsImgId = cgsImgId ; }
   public void setCgsId(int cgsId ) { this.cgsId = cgsId ;  }
   public void setCategory(String category) {  this.category = category ;  }
   public void setOrgFileName(String orgFileName) {  this.orgFileName = orgFileName ;  }

    public int getCgsImgId() { return this.cgsImgId;     }
    public int getCgsId() { return this.cgsId ; }
    public String getCategory() { return this.category ; }
    public String getOrgFileName() { return this.orgFileName ; }
}

