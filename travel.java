package com.smartcity;

public class travel {

    private  int travelInfoId ;          //  travel information id
    private  String Name;                //  Name
    private  String NameEn ;             //  Name (英文)
    private  String category ;           // 分類 : 1. 景點 2. 住宿 3. 美食
    private  String location ;           // 位置
    private  String locationEn ;         // 位置(英文)
    private  String description ;        // 描述
    private  String descriptionEn;       // 描述(英文)

    public travel(){}


    public travel(int travelInfoId , String Name  , String NameEn , String category , String location ,
                  String  locationEn , String description , String descriptionEn) {

       this.travelInfoId = travelInfoId ;
       this.Name = Name ;


    }

    public void setTravelInfoId (int travelInfoId ) { this.travelInfoId = travelInfoId ; }
    public void setName(String Name ) { this.Name  = Name  ;  }
    public void setNameEn(String NameEn) {  this.NameEn = NameEn ;  }
    public void setCategory(String category) {  this.category = category ;  }
    public void setLocation(String location) { this.location = location ;}
    public void setLocationEn(String locationEn) {this.locationEn = locationEn; }
    public void setDescription(String description) { this.description = description ;}
    public void setDescriptionEn(String descriptionEn) { this.descriptionEn = descriptionEn ;}

}
