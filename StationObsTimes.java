package com.smartcity;

import java.util.List;

public class StationObsTimes {
    private List<StationObsTime> stationObsTime;
    //StationObsTimes中包了StationObsTime
    //這邊使用List的原因是因為被中括號包起來
    public StationObsTimes(List<StationObsTime> stationObsTime) {
        this.stationObsTime = stationObsTime;
    }

    public List<StationObsTime> getStationObsTime() {
        return stationObsTime;
    }
}
