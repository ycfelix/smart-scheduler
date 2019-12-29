package com.ust.checklist;

import android.database.Cursor;

/**
 * Created by Jordan on 12/29/2015.
 *
 * This class represents an item to be stored in the CheckList.
 */
public class Item
{
    private String mTitle;
    private String mDetails;
    private String mIconKey;

    private Boolean mIsChecked;

    private int _id;

    public Item(String title, String details, String icon)
    {
        //Initialize the items information.
        mTitle = title;
        mDetails = details;
        mIsChecked = false;
        mIconKey = icon;
    }

    public Item(Cursor cursor)
    {
        //Load the information from the cursor.
        _id = Integer.parseInt(cursor.getString(0));
        mTitle = cursor.getString(1);
        mDetails = cursor.getString(2);
        mIsChecked = false;
        if(Integer.parseInt(cursor.getString(3)) == 1)
        {
            mIsChecked = true;
        }
        mIconKey = cursor.getString(4);
    }


    /*
    * * Getters and Setters *
    * */
    public void setTitle(String title)
    {
        mTitle = title;
    }
    public String getTitle()
    {
        return mTitle;
    }
    public void setDetails(String details)
    {
        mDetails = details;
    }
    public String getDetails()
    {
        return mDetails;
    }
    public void setChecked(boolean isChecked)
    {
        mIsChecked = isChecked;
    }
    public Boolean isChecked()
    {
        return mIsChecked;
    }
    public int getId()
    {
        return _id;
    }
    public void setId(int newid)
    {
        _id = newid;
    }
    public String getIconKey()
    {
        return mIconKey;
    }
    public void setIconKey(String iconKey)
    {
        mIconKey = iconKey;
    }
}
