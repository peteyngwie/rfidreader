package com.smartcity.cgs;

public class LandscapeModel {

    // string course_name for storing course_name
    // and imgid for storing image id.


    private int imgid;

    public LandscapeModel (int imgid) {

        this.imgid = imgid;
    }


    public int getImgid() {
        return imgid;
    }

    public void setImgid(int imgid) {
        this.imgid = imgid;
    }
}

