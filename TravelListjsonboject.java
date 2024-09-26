package com.smartcity;

public class TravelListjsonboject {

    private int travelInfoId ;
    private int category ;
    private String name ;
    private String nameEn ;
    private String location ;
    private String locationEn ;
    private String description ;
    private String descriptionEn ;
    private String evaluate ;
    private String modifyUser ;
    private String modifyDate ;

    public TravelListjsonboject (){}

    public TravelListjsonboject (int travelInfoId , int category , String name , String nameEn , String location ,
                                 String locationEn , String description , String descriptionEn , String evaluate , String modifyUser ,
                                 String modifyDate ){

        this.travelInfoId = travelInfoId ;
        this.category =  category ;
        this.name = name ;
        this.nameEn = nameEn ;
        this.location = location ;
        this.locationEn = locationEn ;
        this.description = description ;
        this.descriptionEn = descriptionEn ;
        this.evaluate= evaluate ;
        this.modifyUser = modifyUser ;
        this.modifyDate= modifyDate ;

    }

    public void setTravelInfoId(int travelInfoId )  { this.travelInfoId = travelInfoId  ; }
    public void setCategory(int category) { this.category = category  ; }
    public void setName(String name) { this.name = name ; }
    public void setNameEn(String nameEn) { this.nameEn = nameEn ; }
    public void setLocation(String location) { this.location  = location ;}
    public void setLocationEn(String locationEn) { this.locationEn = locationEn;}
    public void setDescription(String description) { this.description = description ; }
    public void setDescriptionEn(String descriptionEn) { this.descriptionEn = descriptionEn ; }
    public void setEvaluate(String evaluate) { this.evaluate = evaluate ; }
    public void setModifyUser(String modifyUser) { this.modifyUser = modifyUser ; }
    public void setModifyDate(String modifyDate) { this.modifyDate = modifyDate ; }


    public int   getTravelInfoId( )  { return this.travelInfoId ; }
    public int  getCategory() { return this.category   ; }
    public String getName() { return this.name  ; }
    public String getNameEn() { return this.nameEn ; }
    public String getLocation() { return this.location  ;}
    public String getLocationEn() { return this.locationEn ;}
    public String getDescription() { return this.description ; }
    public String getDescriptionEn() { return this.descriptionEn ; }
    public String getEvaluate() { return this.evaluate ; }
    public String getModifyUser() { return this.modifyUser ; }
    public String getModifyDate() { return this.modifyDate ; }


}
