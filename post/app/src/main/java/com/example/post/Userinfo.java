package com.example.post;

import java.io.Serializable;

/**
 * 数据实体类
 */
public class Userinfo implements Serializable {

    //private String WireDiameter;
    private String ParamName;
    private String ParamValue;

    public Userinfo(String wdiameter, String pname, String pvalue) {
        //this.WireDiameter = wdiameter;
        this.ParamName = pname;
        this.ParamValue = pvalue;
    }

    public Userinfo() {
    }

    /*public String getWireDiameter() {
        return WireDiameter;
    }

    public void setWireDiameter(String wireDiameter) {
        WireDiameter = wireDiameter;
    }*/

    public String getParamName() {
        return ParamName;
    }

    public void setParamName(String paramName) {
        ParamName = paramName;
    }

    public String getParamValue() {
        return ParamValue;
    }

    public void setParamValue(String paramValue) {
        ParamValue = paramValue;
    }
}