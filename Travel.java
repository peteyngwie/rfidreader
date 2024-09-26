package com.smartcity.api;

public class Travel {
    private int travelInfoId;
    private String name;
    private String nameEn ;

    private String category ;

    private String location ;

    private String locationEn;

    private String description ;

    private String descriptionEn ;

    public Travel( ) {


    }

    public void settarvelInfoId(int travelInfoId) { this.travelInfoId = travelInfoId ; }
    public void setname(String name ) { this.name = name ;  }
    public void setnameEn(String nameEn) {  this.nameEn = nameEn ;  }
    public void setCategory(String category) {  this.category = category ;  }
    public void setLocation(String location){ this.location = location ; }
    public void setLocationEn(String locationEn) { this.locationEn = locationEn ; }
    public void setDescription(String description){this.description = description ; }
    public void setDescriptionEn(String descriptionEn){this.descriptionEn = descriptionEn ;}


    public int gettarvelInfoId() { return this.travelInfoId;     }
    public String getname() { return this.name ; }
    public String getNameEn() { return this.nameEn ; }
    public String getCategory() { return this.category ;}
    public String getLocation() { return this.location ; }
    public String getLocationEn() { return this.locationEn ; }
    public String getDescription(){ return this.description ; }
    public String getDescriptionEn(){ return this.descriptionEn ; }

}
