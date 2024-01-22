package com.example.uiapplication.util;

public class oneGroupInfo {

    private String ParamName;
    private String ParamValue;

    public oneGroupInfo(String pname, String pvalue ){

        this.ParamName = pname;
        this.ParamValue = pvalue;
    }

    public oneGroupInfo(){}

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
