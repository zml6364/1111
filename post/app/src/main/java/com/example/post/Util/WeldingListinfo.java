package com.example.post.Util;

import java.io.Serializable;
import java.util.List;

/**
 * 数据实体类
 */
public class WeldingListinfo implements Serializable {

    public String WireDiameter;
    public List<WeldingDatainfo> WeldingList;

    public WeldingListinfo(String wdiameter, List<WeldingDatainfo> weldingList) {
        this.WireDiameter = wdiameter;
        this.WeldingList = weldingList;
    }

    public WeldingListinfo() {
    }

    public String getWireDiameter() {
        return WireDiameter;
    }

    public void setWireDiameter(String wdiameter) {
        WireDiameter = wdiameter;
    }
    public List<WeldingDatainfo> getWeldingList() {
        return WeldingList;
    }

    public void setWeldingList(List<WeldingDatainfo> weldingList) {
        WeldingList = weldingList;
    }
}