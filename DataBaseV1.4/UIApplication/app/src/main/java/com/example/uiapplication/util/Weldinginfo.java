package com.example.uiapplication.util;


import java.io.Serializable;
import java.util.List;

/**
 * 数据实体类
 */
public class Weldinginfo implements Serializable {

    public String WireDiameter;
    public List<oneGroupInfo> WeldingList;

    public Weldinginfo(String wdiameter, List<oneGroupInfo> weldingList) {
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
    public List<oneGroupInfo> getWeldingList() {
        return WeldingList;
    }

    public void setWeldingList(List<oneGroupInfo> weldingList) {
        WeldingList = weldingList;
    }
}