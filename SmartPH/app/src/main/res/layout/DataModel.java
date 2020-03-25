package com.felix.checklist;

import android.support.annotation.NonNull;

import java.util.Locale;

public class DataModel {

    private String title;
    private String detail;
    //private String date;
    private int icon;
    private boolean checked;

    public DataModel(String title,String detail,int icon,boolean checked){
        this.title=title;
        this.detail=detail;
        //this.date=date;
        this.icon=icon;
        this.checked=checked;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }



    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.US,"title is %s detail is %s",title,detail);
    }
}
