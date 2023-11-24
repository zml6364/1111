package com.example.post.Util;

import java.io.Serializable;

/**
 * 数据实体类
 */
public class WeldingDatainfo implements Serializable {

    private String ParamName;
    private String ParamValue;

    public WeldingDatainfo(String pname, String pvalue) {
        this.ParamName = pname;
        this.ParamValue = pvalue;
    }

    public WeldingDatainfo() {
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