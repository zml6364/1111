package com.example.post;

import java.io.Serializable;
import java.util.List;

/**
 * 数据实体类
 */
public class Weldinginfo implements Serializable {

    public String WireDiameter;
    public List<Userinfo> WeldingList;

    public Weldinginfo(String wdiameter, List<Userinfo> weldingList) {
        this.WireDiameter = wdiameter;
        this.WeldingList = weldingList;
    }

    public Weldinginfo() {
    }

    public String getWireDiameter() {
        return WireDiameter;
    }

    public void setWireDiameter(String wdiameter) {
        WireDiameter = wdiameter;
    }
    public List<Userinfo> getWeldingList() {
        return WeldingList;
    }

    public void setWeldingList(List<Userinfo> weldingList) {
        WeldingList = weldingList;
    }
}