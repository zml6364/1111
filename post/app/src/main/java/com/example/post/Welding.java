package com.example.post;

/*
焊接数据实体类
 */
public class Welding {
    private String FeederCreepSpeed;
    private String ArcingCurrent;
    private String ArcingTime;
    private String PulseBaseCurrent;
    private String Currentrise;
    private String Currentrisetau;
    private String PulsingCurrent;
    private String PulseingTime;
    private String Currentdrop;
    private String CurrentdropTau;
    private String Dropletdetachmentcurrent;
    private String Dripletdetachmenttime;
    private String PulseFreqency;
    private String WireFeedSpeed;
    private String Voltagecommandvalue;
    private String FactI_bcontrolpi;
    private String FactI_p1controlpi;
    private String Factfcontrolp;
    private String FactIbcorrection;
    private String FactIp1correction;
    private String Factfcorrection;
    private String Currentriseshortcircuit;
    private String Burnbacktime;
    private String Guidelinevalueformaterial;
    private String Amperageguidelinevalue;
    private String Voltageguidelinevalue;
    private String response;

    public Welding(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Welding() {
    }

    public Welding(String feederCreepSpeed, String arcingCurrent, String arcingTime, String pulseBaseCurrent, String currentrise, String currentrisetau, String pulsingCurrent, String pulseingTime, String currentdrop, String currentdropTau, String dropletdetachmentcurrent, String dripletdetachmenttime, String pulseFreqency, String wireFeedSpeed, String voltagecommandvalue, String factI_bcontrolpi, String factI_p1controlpi, String factfcontrolp, String factIbcorrection, String factIp1correction, String factfcorrection, String currentriseshortcircuit, String burnbacktime, String guidelinevalueformaterial, String amperageguidelinevalue, String voltageguidelinevalue) {
        FeederCreepSpeed = feederCreepSpeed;
        ArcingCurrent = arcingCurrent;
        ArcingTime = arcingTime;
        PulseBaseCurrent = pulseBaseCurrent;
        Currentrise = currentrise;
        Currentrisetau = currentrisetau;
        PulsingCurrent = pulsingCurrent;
        PulseingTime = pulseingTime;
        Currentdrop = currentdrop;
        CurrentdropTau = currentdropTau;
        Dropletdetachmentcurrent = dropletdetachmentcurrent;
        Dripletdetachmenttime = dripletdetachmenttime;
        PulseFreqency = pulseFreqency;
        WireFeedSpeed = wireFeedSpeed;
        Voltagecommandvalue = voltagecommandvalue;
        FactI_bcontrolpi = factI_bcontrolpi;
        FactI_p1controlpi = factI_p1controlpi;
        Factfcontrolp = factfcontrolp;
        FactIbcorrection = factIbcorrection;
        FactIp1correction = factIp1correction;
        Factfcorrection = factfcorrection;
        Currentriseshortcircuit = currentriseshortcircuit;
        Burnbacktime = burnbacktime;
        Guidelinevalueformaterial = guidelinevalueformaterial;
        Amperageguidelinevalue = amperageguidelinevalue;
        Voltageguidelinevalue = voltageguidelinevalue;
    }

    public String getFeederCreepSpeed() {
        return FeederCreepSpeed;
    }

    public void setFeederCreepSpeed(String feederCreepSpeed) {
        FeederCreepSpeed = feederCreepSpeed;
    }

    public String getArcingCurrent() {
        return ArcingCurrent;
    }

    public void setArcingCurrent(String arcingCurrent) {
        ArcingCurrent = arcingCurrent;
    }

    public String getArcingTime() {
        return ArcingTime;
    }

    public void setArcingTime(String arcingTime) {
        ArcingTime = arcingTime;
    }

    public String getPulseBaseCurrent() {
        return PulseBaseCurrent;
    }

    public void setPulseBaseCurrent(String pulseBaseCurrent) {
        PulseBaseCurrent = pulseBaseCurrent;
    }

    public String getCurrentrise() {
        return Currentrise;
    }

    public void setCurrentrise(String currentrise) {
        Currentrise = currentrise;
    }

    public String getCurrentrisetau() {
        return Currentrisetau;
    }

    public void setCurrentrisetau(String currentrisetau) {
        Currentrisetau = currentrisetau;
    }

    public String getPulsingCurrent() {
        return PulsingCurrent;
    }

    public void setPulsingCurrent(String pulsingCurrent) {
        PulsingCurrent = pulsingCurrent;
    }

    public String getPulseingTime() {
        return PulseingTime;
    }

    public void setPulseingTime(String pulseingTime) {
        PulseingTime = pulseingTime;
    }

    public String getCurrentdrop() {
        return Currentdrop;
    }

    public void setCurrentdrop(String currentdrop) {
        Currentdrop = currentdrop;
    }

    public String getCurrentdropTau() {
        return CurrentdropTau;
    }

    public void setCurrentdropTau(String currentdropTau) {
        CurrentdropTau = currentdropTau;
    }

    public String getDropletdetachmentcurrent() {
        return Dropletdetachmentcurrent;
    }

    public void setDropletdetachmentcurrent(String dropletdetachmentcurrent) {
        Dropletdetachmentcurrent = dropletdetachmentcurrent;
    }

    public String getDripletdetachmenttime() {
        return Dripletdetachmenttime;
    }

    public void setDripletdetachmenttime(String dripletdetachmenttime) {
        Dripletdetachmenttime = dripletdetachmenttime;
    }

    public String getPulseFreqency() {
        return PulseFreqency;
    }

    public void setPulseFreqency(String pulseFreqency) {
        PulseFreqency = pulseFreqency;
    }

    public String getWireFeedSpeed() {
        return WireFeedSpeed;
    }

    public void setWireFeedSpeed(String wireFeedSpeed) {
        WireFeedSpeed = wireFeedSpeed;
    }

    public String getVoltagecommandvalue() {
        return Voltagecommandvalue;
    }

    public void setVoltagecommandvalue(String voltagecommandvalue) {
        Voltagecommandvalue = voltagecommandvalue;
    }

    public String getFactI_bcontrolpi() {
        return FactI_bcontrolpi;
    }

    public void setFactI_bcontrolpi(String factI_bcontrolpi) {
        FactI_bcontrolpi = factI_bcontrolpi;
    }

    public String getFactI_p1controlpi() {
        return FactI_p1controlpi;
    }

    public void setFactI_p1controlpi(String factI_p1controlpi) {
        FactI_p1controlpi = factI_p1controlpi;
    }

    public String getFactfcontrolp() {
        return Factfcontrolp;
    }

    public void setFactfcontrolp(String factfcontrolp) {
        Factfcontrolp = factfcontrolp;
    }

    public String getFactIbcorrection() {
        return FactIbcorrection;
    }

    public void setFactIbcorrection(String factIbcorrection) {
        FactIbcorrection = factIbcorrection;
    }

    public String getFactIp1correction() {
        return FactIp1correction;
    }

    public void setFactIp1correction(String factIp1correction) {
        FactIp1correction = factIp1correction;
    }

    public String getFactfcorrection() {
        return Factfcorrection;
    }

    public void setFactfcorrection(String factfcorrection) {
        Factfcorrection = factfcorrection;
    }

    public String getCurrentriseshortcircuit() {
        return Currentriseshortcircuit;
    }

    public void setCurrentriseshortcircuit(String currentriseshortcircuit) {
        Currentriseshortcircuit = currentriseshortcircuit;
    }

    public String getBurnbacktime() {
        return Burnbacktime;
    }

    public void setBurnbacktime(String burnbacktime) {
        Burnbacktime = burnbacktime;
    }

    public String getGuidelinevalueformaterial() {
        return Guidelinevalueformaterial;
    }

    public void setGuidelinevalueformaterial(String guidelinevalueformaterial) {
        Guidelinevalueformaterial = guidelinevalueformaterial;
    }

    public String getAmperageguidelinevalue() {
        return Amperageguidelinevalue;
    }

    public void setAmperageguidelinevalue(String amperageguidelinevalue) {
        Amperageguidelinevalue = amperageguidelinevalue;
    }

    public String getVoltageguidelinevalue() {
        return Voltageguidelinevalue;
    }

    public void setVoltageguidelinevalue(String voltageguidelinevalue) {
        Voltageguidelinevalue = voltageguidelinevalue;
    }
}