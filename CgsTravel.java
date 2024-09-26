package com.smartcity.api;

public class CgsTravel {
    private int cgsId;
    private int travelInfoId;
    private String  distance ;
    private String  walkingTime ;
    private String  orgFileName ;

    public void setCgsId(int cgsId) {this.cgsId = cgsId ;}
    public void setTravelInfoId(int travelInfoId) { this.travelInfoId = travelInfoId ; }
    public void setDistance(String distance) { this.distance = distance ; }
    public void setWalkingTime(String walkingTime) { this.walkingTime = walkingTime ; }
    public void setOrgFileName(String orgFileName) { this.orgFileName = orgFileName ; }

    public int getCgsId() { return this.cgsId ; }
    public int getTravelInfoId() { return this.travelInfoId ; }
    public String getDistance() { return this.distance ; }
    public String getWalkingTime() { return this.walkingTime ; }
    public String getOrgFileName() { return this.orgFileName ; }

    public CgsTravel( ) {}

}
