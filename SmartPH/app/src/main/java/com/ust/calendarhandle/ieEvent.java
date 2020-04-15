package com.ust.calendarhandle;

public class ieEvent {
    String EVENT, TIME, DATE, MONTH,YEAR,type,ID,Notify;



    public ieEvent(String EVENT, String TIME, String DATE, String MONTH, String YEAR, String type, String ID, String Notify) {
        this.EVENT = EVENT;
        this.TIME = TIME;
        this.DATE = DATE;
        this.MONTH = MONTH;
        this.YEAR = YEAR;
        this.type = type;
        this.ID = ID;
        this.Notify = Notify;
    }

    public String getNotify() { return Notify; }

    public void setNotify(String notify) { Notify = notify; }

    public void setID(String ID) { this.ID = ID; }

    public String getID() { return ID; }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getEVENT() {
        return EVENT;
    }

    public void setEVENT(String EVENT) { this.EVENT = EVENT; }

    public String getTIME() {
        return TIME;
    }

    public void setTIME(String TIME) {
        this.TIME = TIME;
    }

    public String getDATE() {
        return DATE;
    }

    public void setDATE(String DATE) {
        this.DATE = DATE;
    }

    public String getMONTH() {
        return MONTH;
    }

    public void setMONTH(String MONTH) {
        this.MONTH = MONTH;
    }

    public String getYEAR() {
        return YEAR;
    }

    public void setYEAR(String YEAR) {
        this.YEAR = YEAR;
    }
}
