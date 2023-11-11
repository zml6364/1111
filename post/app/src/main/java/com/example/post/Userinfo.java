package com.example.post;

import java.io.Serializable;

/**
 * 数据实体类
 */
public class Userinfo implements Serializable {

    private String ParamName;
    private String ParamValue;

    public Userinfo(String pname, String pvalue) {

        this.ParamName = pname;
        this.ParamValue = pvalue;
    }

    public Userinfo() {
    }

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