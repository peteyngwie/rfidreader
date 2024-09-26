package com.smartcity;

public class TimeNElementValue {

    String startTime ;   // starting time
    String endTime ;     // ending time
    String value ;       // element's value

    public TimeNElementValue(String startTime, String endTime , String value) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.value = value;
    }

    public TimeNElementValue() { }  // default constructor

    /*
    public void setElementname(String elementname) { this.elementname = elementname ; }
    public void setDescription(String description) { this.description = description ; }
    public String getElementname() { return this.elementname ; }
    public String getDescription() { return this.description ; }

     */

    public void setStartTime(String startTime) { this.startTime = startTime ; }
    public void setEndTime(String endTime) { this.endTime = endTime ; }
    public String getStartTime() { return this.startTime ; }
    public String getEndTime() { return this.endTime ; }

    public void setValue(String value) { this.value = value;  }
    public String getValue() {  return value; }

}
