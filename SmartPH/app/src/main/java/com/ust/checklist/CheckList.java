package com.ust.checklist;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.ust.smartph.R;
/**
 * Created by Jordan on 12/30/2015.
 */
public class CheckList
{
    private static CheckList instance;
    private Context mAppContext;
    private List<Item> mList;
    private Map<String, Drawable> mIcons;

    private CheckList(Context appContext)
    {
        mAppContext = appContext;

        //Get the saved check list from db if it exists, otherwise it is an empty list.
        DatabaseHandler db = new DatabaseHandler(mAppContext);
        mList = db.getCheckList();
        db.close();

        //Populate Icon map.
        mIcons = new HashMap<>();
        mIcons.put(Keys.BLANK_ICON_TAG,null);
        mIcons.put(Keys.FITNESS_ICON_TAG,mAppContext.getResources().getDrawable(R.drawable.ic_fitness_center_white_24dp));
        mIcons.put(Keys.SCHOOL_ICON_TAG,mAppContext.getResources().getDrawable(R.drawable.ic_school_white_24dp));
        mIcons.put(Keys.GROCERIES_ICON_TAG, mAppContext.getResources().getDrawable(R.drawable.ic_local_grocery_store_white_24dp));
        mIcons.put(Keys.BIRTHDAY_ICON_TAG,mAppContext.getResources().getDrawable(R.drawable.ic_cake_white_24dp));
        mIcons.put(Keys.CHILD_ICON_TAG,mAppContext.getResources().getDrawable(R.drawable.ic_child_friendly_white_24dp));
        mIcons.put(Keys.CREDIT_ICON_TAG,mAppContext.getResources().getDrawable(R.drawable.ic_credit_card_white_24dp));
        mIcons.put(Keys.PLANE_ICON_TAG,mAppContext.getResources().getDrawable(R.drawable.ic_flight_white_24dp));
        mIcons.put(Keys.GAS_ICON_TAG,mAppContext.getResources().getDrawable(R.drawable.ic_local_gas_station_white_24dp));
        mIcons.put(Keys.MAIL_ICON_TAG,mAppContext.getResources().getDrawable(R.drawable.ic_mail_white_24dp));
        mIcons.put(Keys.PEOPLE_ICON_TAG,mAppContext.getResources().getDrawable(R.drawable.ic_people_white_24dp));
        mIcons.put(Keys.FOOD_ICON_TAG,mAppContext.getResources().getDrawable(R.drawable.ic_restaurant_menu_white_24dp));
    }

    public static CheckList get(Context c)
    {
        if(instance == null)
        {
            instance = new CheckList(c.getApplicationContext());
        }
        return instance;
    }

    public List<Item> getCheckList()
    {
        return mList;
    }
    public void setCheckList(List<Item> list) {
        mList=list;
    }
    public Item getListItem(int itemId)
    {
        for(Item i : mList)
        {
            if(i.getId() == itemId)
            {
                return i;
            }
        }
        return null;
    }


    public void addItem(Item newItem)
    {
        mList.add(newItem);
    }
    public void deleteItem(Item item)
    {
        mList.remove(item);
    }
    public Item getItem(int position)
    {
        return mList.get(position);
    }
    public Drawable getIconDrawable(String s)
    {
        return mIcons.get(s);
    }
    public int deleteAllChecked()
    {
        //Count the number of items being deleted.
        int count = 0;

        //Need to use iterator to avoid ConcurrentModificationException.
        for(Iterator<Item> iterator = mList.iterator(); iterator.hasNext();)
        {
            Item i = iterator.next();
            if(i.isChecked())
            {
                //Increment count.
                count++;
                //Delete item from db.
                DatabaseHandler db = new DatabaseHandler(mAppContext);
                db.deleteItem(i);
                //Delete item from list.
                iterator.remove();
            }
        }

        return count;
    }
    public int getNumberUnchecked()
    {
        int count = 0;
        for(Item i : mList)
        {
            if(!i.isChecked())
            {
                count++;
            }
        }
        return count;
    }
}
