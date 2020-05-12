package com.ust.friend;

public class Friend {

    private String name;
    private String userID;
    private boolean isOnline;
    private String email;

    public Friend(){}
    public Friend(String name,String userID, boolean isOnline,String email){
        this.name=name;
        this.userID=userID;
        this.isOnline=isOnline;
        this.email=email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }
}
